package com.fievez.lewhist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrickResultTest {


	@Test
	void withoutSpecialCardThenHighestSuitedWins() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0
		trick.play(new Card(Card.Suit.DIAMOND, 4), generateHand());
		// player 1
		trick.play(new Card(Card.Suit.DIAMOND, 3), generateHand());
		// player 2 (WINNER)
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3
		trick.play(new Card(Card.Suit.DIAMOND, 7), generateHand());

		assertEquals(TrickResult.win(2), trick.getResult());
	}

	@Test
	void withOneTrumpThenTrumpWins() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0
		trick.play(new Card(Card.Suit.DIAMOND, 4), generateHand());
		// player 1 (WINNER)
		trick.play(new Card(Card.Suit.CLUB, 3), generateHandWithoutDiamond());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3
		trick.play(new Card(Card.Suit.DIAMOND, 7), generateHand());

		assertEquals(TrickResult.win(1), trick.getResult());
	}

	@Test
	void withMoreThanOneTrumpThenHighestTrumpWins() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0
		trick.play(new Card(Card.Suit.DIAMOND, 4), generateHand());
		// player 1
		trick.play(new Card(Card.Suit.CLUB, 3), generateHandWithoutDiamond());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3 (WINNER)
		trick.play(new Card(Card.Suit.CLUB, 13), generateHandWithoutDiamond());

		assertEquals(TrickResult.win(3), trick.getResult());
	}

	@Test
	void withTwoDistributingAndTrumpThenTrumpWins() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0
		trick.play(new Card(Card.Suit.DIAMOND, 2), generateHand(), Card.Bonus.I_DISTRIBUTE);
		// player 1
		trick.play(new Card(Card.Suit.CLUB, 3), generateHandWithoutDiamond());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3 (WINNER)
		trick.play(new Card(Card.Suit.CLUB, 13), generateHandWithoutDiamond());

		assertEquals(TrickResult.win(3), trick.getResult());
	}

	@Test
	void jokerWins() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0 (WINNER)
		trick.play(new Card(Card.Suit.JOKER, 0), generateHand(), Card.Bonus.I_TAKE);
		// player 1
		trick.play(new Card(Card.Suit.DIAMOND, 3), generateHand());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3
		trick.play(new Card(Card.Suit.CLUB, 13), generateHandWithoutDiamond());

		assertEquals(TrickResult.win(0), trick.getResult());
	}

	@Test
	void secondJokerWins() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0 (WINNER)
		trick.play(new Card(Card.Suit.JOKER, 0), generateHand(), Card.Bonus.I_TAKE);
		// player 1
		trick.play(new Card(Card.Suit.DIAMOND, 3), generateHand());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3
		trick.play(new Card(Card.Suit.JOKER, 1), generateHand(), Card.Bonus.I_TAKE);

		assertEquals(TrickResult.win(3), trick.getResult());
	}

	@Test
	void lowJokerDoesntTake() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0 (WINNER)
		trick.play(new Card(Card.Suit.JOKER, 0), generateHand(), Card.Bonus.I_TAKE);
		// player 1
		trick.play(new Card(Card.Suit.DIAMOND, 3), generateHand());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());
		// player 3
		trick.play(new Card(Card.Suit.JOKER, 1), generateHand(), Card.Bonus.I_LEAVE);

		assertEquals(TrickResult.win(0), trick.getResult());
	}

	@Test
	void dontGiveResultsIfTurnNotOver() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0 (WINNER)
		trick.play(new Card(Card.Suit.JOKER, 0), generateHand(), Card.Bonus.I_TAKE);
		// player 1
		trick.play(new Card(Card.Suit.DIAMOND, 3), generateHand());
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 8), generateHand());

		assertThrows(Exception.class, trick::getResult);
	}

	@Test
	void twoDistributing() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 4);

		// player 0
		trick.play(new Card(Card.Suit.DIAMOND, 3), generateHand());
		// player 1 (DISTRIBUTING)
		trick.play(new Card(Card.Suit.DIAMOND, 2), generateHand(), Card.Bonus.I_DISTRIBUTE);
		// player 2
		trick.play(new Card(Card.Suit.DIAMOND, 7), generateHand());
		// player 3
		trick.play(new Card(Card.Suit.DIAMOND, 4), generateHand());

		assertEquals(TrickResult.distribution(1), trick.getResult());
	}

	@Test
	void twoSmallJoker() throws Exception {
		Trick trick = new Trick(new Card(Card.Suit.CLUB, 9), false, 2);

		// player 0 (WINNER)
		trick.play(new Card(Card.Suit.JOKER, 1), generateHand(), Card.Bonus.I_LEAVE);
		// player 1
		trick.play(new Card(Card.Suit.JOKER, 0), generateHand(), Card.Bonus.I_LEAVE);

		assertEquals(TrickResult.win(0), trick.getResult());
	}

	private List<Card> generateHand() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.DIAMOND, 8));
		hand.add(new Card(Card.Suit.DIAMOND, 4));
		hand.add(new Card(Card.Suit.DIAMOND, 3));
		hand.add(new Card(Card.Suit.DIAMOND, 7));
		hand.add(new Card(Card.Suit.DIAMOND, 2));
		hand.add(new Card(Card.Suit.CLUB, 12));
		hand.add(new Card(Card.Suit.CLUB, 14));
		hand.add(new Card(Card.Suit.SPADE, 5));
		hand.add(new Card(Card.Suit.SPADE, 9));
		hand.add(new Card(Card.Suit.HEART, 2));
		hand.add(new Card(Card.Suit.HEART, 11));
		hand.add(new Card(Card.Suit.JOKER, 0));
		hand.add(new Card(Card.Suit.JOKER, 1));
		return hand;
	}

	private List<Card> generateHandWithoutDiamond() {
		List<Card> hand = new ArrayList<>();
		hand.add(new Card(Card.Suit.CLUB, 13));
		hand.add(new Card(Card.Suit.CLUB, 3));
		hand.add(new Card(Card.Suit.CLUB, 2));
		hand.add(new Card(Card.Suit.SPADE, 5));
		hand.add(new Card(Card.Suit.SPADE, 9));
		hand.add(new Card(Card.Suit.HEART, 2));
		hand.add(new Card(Card.Suit.HEART, 11));
		hand.add(new Card(Card.Suit.JOKER, 0));
		return hand;
	}

}
