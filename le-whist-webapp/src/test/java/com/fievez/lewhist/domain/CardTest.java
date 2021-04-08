package com.fievez.lewhist.domain;

import org.junit.jupiter.api.Test;

import static com.fievez.lewhist.domain.Card.Suit.CLUB;
import static com.fievez.lewhist.domain.Card.Suit.DIAMOND;
import static org.junit.jupiter.api.Assertions.*;

class CardTest {

	@Test
	void cardsWithSameValueAndSuitShouldBeEquals() {
		Card cardA = new Card(CLUB, 4);
		Card cardB = new Card(CLUB, 4);
		assertEquals(cardB, cardA);
	}

	@Test
	void beloteOrder() throws Exception {
		StringBuilder normal = new StringBuilder();
		for (int i=2; i < 15; i++){
			Card card = new Card(CLUB, i, false);
			normal.append(card.toString());
		}
		StringBuilder trump = new StringBuilder();
		for (int i=2; i < 15; i++){
			Card card = new Card(CLUB, i, true);
			trump.append(card.toString());
		}
		assertNotEquals(trump.toString(), normal.toString());
		assertEquals("2C3C4C5C6C7C8CQCKC10CAC9CJC", trump.toString());
		assertEquals("2C3C4C5C6C7C8C9CJCQCKC10CAC", normal.toString());

		//order trump
		assertEquals(new Card(CLUB, 2, true), Card.fromString(CLUB,  "2C"));
		assertEquals(new Card(CLUB, 3, true), Card.fromString(CLUB,  "3C"));
		assertEquals(new Card(CLUB, 4, true), Card.fromString(CLUB,  "4C"));
		assertEquals(new Card(CLUB, 5, true), Card.fromString(CLUB,  "5C"));
		assertEquals(new Card(CLUB, 6, true), Card.fromString(CLUB,  "6C"));
		assertEquals(new Card(CLUB, 7, true), Card.fromString(CLUB,  "7C"));
		assertEquals(new Card(CLUB, 8, true), Card.fromString(CLUB,  "8C"));
		assertEquals(new Card(CLUB, 9, true), Card.fromString(CLUB,  "QC"));
		assertEquals(new Card(CLUB, 10, true), Card.fromString(CLUB,  "KC"));
		assertEquals(new Card(CLUB, 11, true), Card.fromString(CLUB,  "10C"));
		assertEquals(new Card(CLUB, 12, true), Card.fromString(CLUB,  "AC"));
		assertEquals(new Card(CLUB, 13, true), Card.fromString(CLUB,  "9C"));
		assertEquals(new Card(CLUB, 14, true), Card.fromString(CLUB,  "JC"));

		//order not trump
		assertEquals(new Card(CLUB, 2, false), Card.fromString(DIAMOND,  "2C"));
		assertEquals(new Card(CLUB, 3, false), Card.fromString(DIAMOND,  "3C"));
		assertEquals(new Card(CLUB, 4, false), Card.fromString(DIAMOND,  "4C"));
		assertEquals(new Card(CLUB, 5, false), Card.fromString(DIAMOND,  "5C"));
		assertEquals(new Card(CLUB, 6, false), Card.fromString(DIAMOND,  "6C"));
		assertEquals(new Card(CLUB, 7, false), Card.fromString(DIAMOND,  "7C"));
		assertEquals(new Card(CLUB, 8, false), Card.fromString(DIAMOND,  "8C"));
		assertEquals(new Card(CLUB, 9, false), Card.fromString(DIAMOND,  "9C"));
		assertEquals(new Card(CLUB, 10, false), Card.fromString(DIAMOND,  "JC"));
		assertEquals(new Card(CLUB, 11, false), Card.fromString(DIAMOND,  "QC"));
		assertEquals(new Card(CLUB, 12, false), Card.fromString(DIAMOND,  "KC"));
		assertEquals(new Card(CLUB, 13, false), Card.fromString(DIAMOND,  "10C"));
		assertEquals(new Card(CLUB, 14, false), Card.fromString(DIAMOND,  "AC"));
	}

}
