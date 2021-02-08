package com.fievez.fakewhist.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Player {

	private final String name;

	private List<Card> cards;

	private Integer contract;

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

}
