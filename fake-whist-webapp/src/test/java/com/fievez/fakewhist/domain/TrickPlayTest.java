package com.fievez.fakewhist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrickPlayTest {

	@Test
	void cantPlayACardIfNotInHand() {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), true, 4);

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.SPADE, 7), generateHandWithoutJoker()));
	}

	@Test
	void cantPlayTrumpIfNotAllowed() {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.CLUB, 12), generateHandWithoutJoker()));

	}

	@Test
	void canPlayTrumpIfAllowed() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), true, 4);

		trick.play(new Card(Card.Suit.CLUB, 12), generateHandWithoutJoker());

		assertTrue(true);
	}

	@Test
	void canPlayTrumpIfOnlyTrumpInHand() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		trick.play(new Card(Card.Suit.CLUB, 8), generateHandFullOfClub());
	}

	@Test
	void canPlayTrumpIfOnlyTrumpAndJokerInHand() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		trick.play(new Card(Card.Suit.CLUB, 5), generateHandFullOfClubPlusAJoker());
	}

	@Test
	void cantPlayAnotherSuitThanAskedIfAnythingMatchingInHand() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), true, 4);
		trick.play(new Card(Card.Suit.HEART, 5), generateHandWithoutJoker());

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.CLUB, 12), generateHandWithoutJoker()));
	}

	@Test
	void cantPlayAnotherSuitThanTrumpIfTrumpInHand() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);
		trick.play(new Card(Card.Suit.HEART, 5), generateHandWithoutJoker());

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.CLUB, 12), generateHandWithoutHeart()));
	}

	@Test
	void canPlayAnotherSuitThanAskedIfNothingMatchingInHand() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);
		trick.play(new Card(Card.Suit.HEART, 5), generateHandWithoutJoker());

		trick.play(new Card(Card.Suit.CLUB, 8), generateHandFullOfClub());

		assertTrue(true);
	}

	@Test
	void canStartWithJoker() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);

		trick.play(new Card(Card.Suit.JOKER, 0), generateHandWithJoker(), Card.Bonus.I_TAKE);
	}

	@Test
	void canPlayJokerInsteadOfSuit() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);
		trick.play(new Card(Card.Suit.HEART, 5), generateHandWithoutJoker());

		trick.play(new Card(Card.Suit.JOKER, 0), generateHandWithJoker(), Card.Bonus.I_LEAVE);
	}

	@Test
	void cantPlayAnythingElseThanJokerIfBeforeLastTurn() {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.DIAMOND, 8), generateTwoCardsHandWithJoker()));
	}

	@Test
	void cantPlayAnythingElseThanJokerIfTwoJokersThreeRoundsBeforeTheLast() {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.DIAMOND, 8), generateThreeCardsHandWithTwoJokers()));
	}

	@Test
	void cantPlayMoreThanPlayers() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);

		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHandWithJoker());
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHandWithJoker());
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHandWithJoker());
		trick.play(new Card(Card.Suit.JOKER, 0), generateHandWithJoker(), Card.Bonus.I_TAKE);

		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.DIAMOND, 8), generateHandWithJoker()));
	}

	@Test
	void mandatoryToTalkWhenPlayingBonusCard() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.SPADE, 9), true, 4);

		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHandWithJoker());
		assertThrows(Exception.class, () -> trick.play(new Card(Card.Suit.JOKER, 0), generateHandWithJoker()));
	}

	private List<Card> generateHandWithoutJoker() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.DIAMOND, 8));
		hand.add(new Card(Card.Suit.CLUB, 12));
		hand.add(new Card(Card.Suit.HEART, 5));
		return hand;
	}

	private List<Card> generateHandWithoutHeart() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.DIAMOND, 8));
		hand.add(new Card(Card.Suit.CLUB, 12));
		hand.add(new Card(Card.Suit.SPADE, 5));
		return hand;
	}

	private List<Card> generateHandWithJoker() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.DIAMOND, 8));
		hand.add(new Card(Card.Suit.CLUB, 12));
		hand.add(new Card(Card.Suit.HEART, 5));
		hand.add(new Card(Card.Suit.HEART, 14));
		hand.add(new Card(Card.Suit.JOKER, 0));
		return hand;
	}


	private List<Card> generateTwoCardsHandWithJoker() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.DIAMOND, 8));
		hand.add(new Card(Card.Suit.JOKER, 0));
		return hand;
	}

	private List<Card> generateThreeCardsHandWithTwoJokers() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.DIAMOND, 8));
		hand.add(new Card(Card.Suit.JOKER, 1));
		hand.add(new Card(Card.Suit.JOKER, 0));
		return hand;
	}

	private List<Card> generateHandFullOfClub() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.CLUB, 8));
		hand.add(new Card(Card.Suit.CLUB, 2));
		hand.add(new Card(Card.Suit.CLUB, 5));
		return hand;
	}

	private List<Card> generateHandFullOfClubPlusAJoker() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.CLUB, 8));
		hand.add(new Card(Card.Suit.CLUB, 2));
		hand.add(new Card(Card.Suit.CLUB, 5));
		hand.add(new Card(Card.Suit.JOKER, 0));
		return hand;
	}

}