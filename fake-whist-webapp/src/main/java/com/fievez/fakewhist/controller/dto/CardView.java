package com.fievez.fakewhist.controller.dto;

public class CardView {

	public String value;

	public boolean bonusTwo;

	public boolean bonusJoker;

	private CardView(String value, boolean bonusTwo, boolean bonusJoker) {
		this.value = value;
		this.bonusTwo = bonusTwo;
		this.bonusJoker = bonusJoker;
	}

	public static CardView simple(String value) {
		return new CardView(value, false, false);
	}

	public static CardView withTwoBonus(String value) {
		return new CardView(value, true, false);
	}

	public static CardView withJokerBonus(String value) {
		return new CardView(value, false, true);
	}
}
