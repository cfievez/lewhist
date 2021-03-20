package com.fievez.lewhist.controller.dto;

import com.fievez.lewhist.domain.Card;
import com.fievez.lewhist.domain.Whist;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerView {

	public String username;

	public List<CardView> cards;

	public String cardPlayed;

	public BonusUsed bonusUsed;

	public Integer contract;

	public int score;

	public boolean isNextToTalk;

	public boolean isNextToPlay;

	public boolean hasToPickAWinner;

	public boolean isConnectedPlayer;

	public boolean isFirstToPlayThisTrick;

	public static PlayerView from(com.fievez.lewhist.domain.Player player, Whist whist, String connectedUserName) {
		PlayerView playerView = new PlayerView();
		playerView.username = player.getName();
		playerView.cardPlayed = player.getCardPlayed().map(Card::toString).orElse("");
		playerView.bonusUsed = player.getCardPlayed().map(Card::getBonus).map(bonus -> {
			if(bonus.equals(Card.Bonus.I_DISTRIBUTE)) {
				return BonusUsed.DISTRIBUTION;
			} else if(bonus.equals(Card.Bonus.I_TAKE)) {
				return BonusUsed.TAKE;
			} else if(bonus.equals(Card.Bonus.I_LEAVE)) {
				return BonusUsed.LEAVE;
			}
			return null;
		}).orElse(null);
		playerView.contract = player.getContract().orElse(null);
		playerView.score = player.getScore();
		playerView.cards = player.getCards().stream()
				.sorted((a, b) -> {
					if(a.getSuit().compareTo(b.getSuit()) < 0) {
						return 1;
					} else if(a.getSuit().compareTo(b.getSuit()) > 0) {
						return -1;
					} else {
						return Integer.compare(b.getValue(), a.getValue());
					}
				})
				.map(card -> {
					if(card.getSuit().equals(Card.Suit.JOKER)) {
						return CardView.withJokerBonus(card.toString());
					} else if(whist.wouldBeTwoWithBonus(card)) {
						return CardView.withTwoBonus(card.toString());
					}
					return CardView.simple(card.toString());
				})
				.map(card -> connectedUserName.equals(player.getName()) ? card : CardView.simple("back"))
				.collect(Collectors.toList());
		playerView.isNextToTalk = whist.getPhase().equals(Whist.GamePhase.TALK)
				&& whist.getNextToTalkOrPlay().getName().equals(player.getName());
		playerView.isNextToPlay = whist.getPhase().equals(Whist.GamePhase.PLAY)
				&& whist.getNextToTalkOrPlay().getName().equals(player.getName());
		playerView.hasToPickAWinner = player.hasToPickAWinner();
		playerView.isConnectedPlayer = connectedUserName.equals(player.getName());
		playerView.isFirstToPlayThisTrick = whist.playerIsFirstToPlayCurrentTrick(player.getName());
		return playerView;
	}

	public enum BonusUsed {

		DISTRIBUTION, LEAVE, TAKE

	}

}
