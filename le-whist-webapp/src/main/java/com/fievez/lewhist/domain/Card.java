package com.fievez.lewhist.domain;

public class Card {

	private final Suit suit;
	private final int value;

	private Bonus bonus;
	private boolean trumpSuit;

	public Card(Suit suit, int value) {
		this.suit = suit;
		this.value = value;
		this.trumpSuit = false;
	}

	public Card(Suit suit, int value, boolean trumpSuit) {
		this.suit = suit;
		this.value = value;
		this.trumpSuit = trumpSuit;
	}

	public void setBonus(Bonus bonus) throws Exception {
		if(value == 2 && bonus.equals(Bonus.I_TAKE)) {
			throw new Exception("Impossible de dire je prends avec un 2");
		}
		if(suit.equals(Suit.JOKER) && bonus.equals(Bonus.I_DISTRIBUTE)) {
			throw new Exception("Impossible de distribuer avec un Joker");
		}
		this.bonus = bonus;
	}

	public Bonus getBonus() {
		return bonus;
	}

	public boolean hasBonus() {
		return bonus != null;
	}

	public Suit getSuit() {
		return suit;
	}

	public int getValue() {
		return value;
	}

	public void setHasTrumpSuit(){
		this.trumpSuit = true;
	}

	public boolean isTakingJoker() {
		return suit.equals(Suit.JOKER) && Bonus.I_TAKE.equals(bonus);
	}

	public boolean isDistributingTwo() {
		return value == 2 && Bonus.I_DISTRIBUTE.equals(bonus);
	}

	public static Card fromString(Suit trumpSuit, String code) throws Exception {
		if(code.equals("joker")) {
			return new Card(Suit.JOKER, 0);
		} else {
			Suit suit = parseSuit(code);
			if (suit.equals(trumpSuit)) {
				return new Card(suit, parseTrumpValue(code), true);
			} else {
				return new Card(suit, parseValue(code));
			}
		}

	}

	private static Suit parseSuit(String code) throws Exception {
		char suitCode = code.charAt(code.length()-1);
		if(suitCode == 'D') {
			return Suit.DIAMOND;
		} else if(suitCode == 'C') {
			return Suit.CLUB;
		} else if(suitCode == 'S') {
			return Suit.SPADE;
		} else if(suitCode == 'H') {
			return Suit.HEART;
		}
		throw new Exception("Impossible to parse " + code);
	}

	private static int parseValue(String code) {
		if(code.length() == 3) {
			int parseInt = Integer.parseInt(code.substring(0, 2));
			if (parseInt == 10){
				return 13;
			}
			return parseInt;
		} else {
			if(code.charAt(0) == 'J') {
				return 10;
			} else if(code.charAt(0) == 'Q') {
				return 11;
			} else if(code.charAt(0) == 'K') {
				return 12;
			} else if(code.charAt(0) == 'A') {
				return 14;
			} else {
				return Integer.parseInt(code.substring(0, 1));
			}
		}
	}

	private static int parseTrumpValue(String code) {
		if(code.length() == 3) {
			int parseInt = Integer.parseInt(code.substring(0, 2));
			 if (parseInt == 10){
				return 11;
			}
			return parseInt;
		} else {
			if(code.charAt(0) == 'Q') {
				return 9;
			} else if(code.charAt(0) == 'K') {
				return 10;
			} else if(code.charAt(0) == 'A') {
				return 12;
			} else if(code.charAt(0) == 'J') {
				return 14;
			} else {
				int parseInt = Integer.parseInt(code.substring(0, 1));
				if (parseInt == 9){
					return 13;
				}
				return parseInt;
			}
		}
	}

	@Override
	public String toString() {
		String assetName = "";

		if(this.trumpSuit) {
			if(value <= 8 ) {
				assetName += value ;
			} else if (value == 9) {
				assetName += "Q";
			} else if (value == 10) {
				assetName += "K";
			} else if (value == 11) {
				assetName += "10";
			} else if (value == 12) {
				assetName += "A";
			} else if (value == 13) {
				assetName += "9";
			} else if (value == 14) {
				assetName += "J";
			}
		} else {
			if(value <= 9 ) {
				assetName += value ;
			} else if (value == 10) {
				assetName += "J";
			} else if (value == 11) {
				assetName += "Q";
			} else if (value == 12) {
				assetName += "K";
			} else if (value == 13) {
				assetName += "10";
			} else if (value == 14) {
				assetName += "A";
			}
		}

		switch (suit) {
			case JOKER:
				if(value == 0) {
					assetName = "joker";
					break;
				} else {
					assetName = "joker";
					break;
				}
			case DIAMOND:
				assetName += "D";
				break;
			case CLUB:
				assetName += "C";
				break;
			case HEART:
				assetName += "H";
				break;
			case SPADE:
				assetName += "S";
				break;
		}

		return assetName;
	}

	@Override
	public boolean equals(Object o) {
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		Card card = (Card) o;

		// value comparison only if not JOKER
		if(!suit.equals(Suit.JOKER) && value != card.value) {
			return false;
		}
		return suit == card.suit;
	}

	@Override
	public int hashCode() {
		int result = suit != null ? suit.hashCode() : 0;
		result = 31 * result + value;
		return result;
	}

	public enum Bonus {

		I_TAKE,
		I_LEAVE,
		I_DISTRIBUTE;

	}

	public enum Suit {

		HEART,
		CLUB,
		DIAMOND,
		SPADE,
		JOKER;

		public static Suit fromCode(int code) {
			switch (code) {
				case 0:
					return HEART;
				case 1:
					return CLUB;
				case 2:
					return DIAMOND;
				case 3:
					return SPADE;
				case 4:
					return JOKER;
			}
			throw new RuntimeException("BLABLIBLABLOU MAUVAIS CODE COULEUR MAN");
		}

	}
}
