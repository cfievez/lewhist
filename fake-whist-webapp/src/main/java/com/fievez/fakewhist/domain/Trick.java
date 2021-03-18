package com.fievez.fakewhist.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Trick {

	private final Card trump;
	private final boolean trumpAllowed;
	private final List<Card> cards;
	private final int numberOfPlayers;

	public Trick(Card trump, boolean trumpAllowed, int numberOfPlayers) {
		this.cards = new ArrayList<>();
		this.trump = trump;
		this.trumpAllowed = trumpAllowed;
		this.numberOfPlayers = numberOfPlayers;
	}

	public void play(Card card, List<Card> hand) throws Exception {
		if(cards.size() == numberOfPlayers) {
			throw new Exception("Ce pli est fini, faut pas rejouer là...");
		}
		if(!card.hasBonus() && card.getValue() == 2) {
			Optional<Card.Suit> suitAskedFor = suitAskedFor();
			if(!suitAskedFor.isPresent() || suitAskedFor.get().equals(card.getSuit())) {
				throw new Exception("Obligatoire de parler lorsqu'on utilise un deux");
			}
		}
		if(!card.hasBonus() && card.getSuit().equals(Card.Suit.JOKER)) {
			throw new Exception("Obligatoire de parler lorsqu'on utilise une carte bonus");
		}

		List<Card> cardsAllowedToBePlayed = getCardsAllowedToBePlayed(hand);

		if(!cardsAllowedToBePlayed.contains(card)) {
			throw new Exception("Tu peux pas jouer cette carte bolosse");
		}

		cards.add(card);
	}

	public void play(Card card, List<Card> hand, Card.Bonus bonus) throws Exception {
		card.setBonus(bonus);
		play(card, hand);
	}

	public TrickResult getResult() throws Exception {
		if(!isOver()) {
			throw new Exception("Le pli est pas fini là en fait");
		}

		// Si joker grand => gain au dernier joker grand
		if(cards.stream().anyMatch(Card::isTakingJoker)) {
			for(int i = cards.size()-1; i >= 0; i--) {
				if(cards.get(i).isTakingJoker()) {
					return TrickResult.win(i);
				}
			}
		}

		// Sinon si non-atout demandé et atout joué => gain au plus gros atout
		Optional<Card.Suit> suitAskedFor = suitAskedFor();
		if(suitAskedFor.isPresent() && !suitAskedFor.get().equals(trump.getSuit()) && cards.stream().anyMatch(card -> card.getSuit().equals(trump.getSuit()))) {
			Card bestCard = null;
			int winner = 0;
			for (int i = 0; i < cards.size(); i++) {
				Card currentCard = cards.get(i);
				if(currentCard.getSuit().equals(trump.getSuit())) {
					if(bestCard == null) {
						bestCard = currentCard;
						winner = i;
					} else if (bestCard.getValue() < currentCard.getValue()){
						bestCard = currentCard;
						winner = i;
					}
				}
			}
			return TrickResult.win(winner);
		}

		if(!suitAskedFor.isPresent()) {
			// 2 players => 2 small jokers
			return TrickResult.win(0);
		}

		// Sinon si "2 distribue" de la couleur demandée => distribution
		if(cards.stream().anyMatch(card -> card.isDistributingTwo() && card.getSuit().equals(suitAskedFor.get()))) { //TODO ET POSSIBILITE DE DISTRIBUER A QUELQU'UN !!!
			for (int i = 0; i < cards.size(); i++) {
				Card card = cards.get(i);
				if(card.isDistributingTwo() && card.getSuit().equals(suitAskedFor.get())) {
					return TrickResult.distribution(i);
				}
			}
		}

		// Sinon => gain à la plus grosse carte de la couleur demandée
		Card bestCard = null;
		int winner = 0;
		for (int i = 0; i < cards.size(); i++) {
			Card currentCard = cards.get(i);
			if(currentCard.getSuit().equals(suitAskedFor.get())) {
				if(bestCard == null || currentCard.getValue() > bestCard.getValue()) {
					bestCard = currentCard;
					winner = i;
				}
			}
		}
		return TrickResult.win(winner);
	}

	public boolean isOver() {
		return cards.size() == numberOfPlayers;
	}

	public boolean canPlayTwoWithBonus(Card card) {
		return card.getValue() == 2 && (!suitAskedFor().isPresent() || suitAskedFor().get().equals(card.getSuit()));
	}

	private List<Card> getCardsAllowedToBePlayed(List<Card> hand) {
		Optional<Card.Suit> suitAskedFor = suitAskedFor();

		if(hand.size() == 2 && handHasAJoker(hand)) {
			return hand.stream()
					.filter(card -> card.getSuit().equals(Card.Suit.JOKER))
					.collect(Collectors.toList());
		}

		if(hand.size() == 3 && handHasTwoJokers(hand)) {
			return hand.stream()
					.filter(card -> card.getSuit().equals(Card.Suit.JOKER))
					.collect(Collectors.toList());
		}

		if(!suitAskedFor.isPresent()) {
			// No suit asked for
			if(trumpAllowed || handIsFullOfTrump(hand)) {
				return hand;
			}

			return hand.stream()
					.filter(card -> !card.getSuit().equals(trump.getSuit()))
					.collect(Collectors.toList());
		}

		if(handIsAbleToGiveSuit(hand, suitAskedFor.get())) {
			// Can give suit
			return hand.stream()
					.filter(card -> card.getSuit().equals(suitAskedFor.get()) || card.getSuit().equals(Card.Suit.JOKER))
					.collect(Collectors.toList());
		}

		if (handIsAbleToGiveTrump(hand)) {
			// Can't give suit but has trump
			return hand.stream()
					.filter(card -> card.getSuit().equals(trump.getSuit()) || card.getSuit().equals(Card.Suit.JOKER))
					.collect(Collectors.toList());
		}
		return hand;
	}

	private boolean handHasAJoker(List<Card> hand) {
		return hand.stream().anyMatch(card -> card.getSuit().equals(Card.Suit.JOKER));
	}

	private boolean handHasTwoJokers(List<Card> hand) {
		return hand.stream().filter(card -> card.getSuit().equals(Card.Suit.JOKER)).count() == 2;
	}

	private boolean handIsFullOfTrump(List<Card> hand) {
		return handIsFullOfGivenSuit(hand, trump.getSuit());
	}

	private boolean handIsFullOfGivenSuit(List<Card> hand, Card.Suit suit) {
		return hand.stream().allMatch(card -> card.getSuit().equals(suit) || card.getSuit().equals(Card.Suit.JOKER));
	}

	private boolean handIsAbleToGiveTrump(List<Card> hand) {
		return handIsAbleToGiveSuit(hand, trump.getSuit());
	}

	private boolean handIsAbleToGiveSuit(List<Card> hand, Card.Suit suit) {
		return hand.stream().anyMatch(card -> card.getSuit().equals(suit));
	}

	private Optional<Card.Suit> suitAskedFor() {
		return cards.stream().filter(card -> !card.getSuit().equals(Card.Suit.JOKER)).findFirst().map(Card::getSuit);
	}

}
