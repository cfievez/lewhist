package com.fievez.fakewhist.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

	@Test
	void cardsWithSameValueAndSuitShouldBeEquals() {
		Card cardA = new Card(Card.Suit.CLUB, 4);
		Card cardB = new Card(Card.Suit.CLUB, 4);
		assertEquals(cardB, cardA);
	}

}