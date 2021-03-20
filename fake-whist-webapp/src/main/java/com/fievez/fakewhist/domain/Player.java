package com.fievez.fakewhist.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player {

	private final String name;

	private List<Card> cards;

	private Integer contract;

	private int score = 0;

	private Card cardPlayed;

	private boolean hasToPickAWinner = false;

	public Player(String name) {
		this.name = name;
	}

	public void emptyHand() {
		cards = new ArrayList<>();
	}

	public void addCard(Card card) {
		cards.add(card);
	}

	public String getName() {
		return name;
	}

	public List<Card> getCards() {
		return cards;
	}

	public void cancelContract() {
		contract = null;
	}

	public void setContract(int value) {
		contract = value;
	}

	public Optional<Integer> getContract() {
		return Optional.ofNullable(contract);
	}

	public void resetCardPlayed() {
		this.cardPlayed = null;
	}

	public void setCardPlayed(Card cardPlayed) {
		this.cardPlayed = cardPlayed;
	}

	public Optional<Card> getCardPlayed() {
		return Optional.ofNullable(cardPlayed);
	}

	public void resetScore() {
		score = 0;
	}

	public void increaseScore() {
		score++;
	}

	public int getScore() {
		return score;
	}

	public boolean canBePicked() {
		return contract != null && score != contract;
	}

	public boolean hasToPickAWinner() {
		return hasToPickAWinner;
	}

	public void setHasToPickAWinner() {
		this.hasToPickAWinner = true;
	}

	public void pickAWinner() {
		this.hasToPickAWinner = false;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Player player = (Player) o;

		return name != null ? name.equals(player.name) : player.name == null;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}
}
