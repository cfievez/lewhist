package com.fievez.lewhist.domain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


class WhistTest {

	@Test
	void canTalkZero() throws Exception {
		Whist whist = initWhist(6);

		whist.talkContract("playerA", 0);
		assertTrue(true);
	}

	@Test
	void cantTalkMoreThanCards() {
		Whist whist = initWhist(7);

		assertThrows(Exception.class, () -> {
			whist.talkContract("playerA", 8);
		});
	}

	@Test
	void totalTalkCantBeEqualToCards() {
		Whist whist = initWhist(7);

		try {
			whist.talkContract("playerA", 2);
			whist.talkContract("playerB", 2);
			whist.talkContract("playerC", 2);
			assertThrows(Exception.class, () -> {
				whist.talkContract("playerD", 1);
			});
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	void cantTalkLessThanZero() {
		Whist whist = initWhist(5);

		assertThrows(Exception.class, () -> {
			whist.talkContract("playerA", -1);
		});
	}

	@Test
	void cantTalkIfNotYourTurn() {
		Whist whist = initWhist(6);

		assertThrows(Exception.class, () -> {
			whist.talkContract("playerB", 2);
		});
	}

	@Test
	void talkingPhaseWhenNotEveryoneTalked() throws Exception {
		Whist whist = initWhist(6);

		whist.talkContract("playerA", 2);
		whist.talkContract("playerB", 2);
		whist.talkContract("playerC", 2);

		assertEquals(whist.getPhase(), Whist.GamePhase.TALK);
	}

	@Test
	void playingPhaseWhenEveryoneTalked() throws Exception {
		Whist whist = initWhist(7);

		whist.talkContract("playerA", 2);
		whist.talkContract("playerB", 2);
		whist.talkContract("playerC", 2);
		whist.talkContract("playerD", 2);

		assertEquals(whist.getPhase(), Whist.GamePhase.PLAY);
	}

	@Test
	void cantPlayIfNotYourTurn() throws Exception {
		Whist whist = getWhistInPlayingPhase(6);

		assertThrows(Exception.class, () -> {
			whist.playCard("playerB", new Card(Card.Suit.CLUB, 8));
		});

	}

	private Whist initWhist(int handSize) {
		Whist whist = new Whist();
		whist.addPlayer("playerA");
		whist.addPlayer("playerB");
		whist.addPlayer("playerC");
		whist.addPlayer("playerD");
		whist.initGame(handSize);
		return whist;
	}

	private Whist getWhistInPlayingPhase(int handSize) throws Exception {
		Whist whist = initWhist(handSize);

		whist.talkContract("playerA", 2);
		whist.talkContract("playerB", 2);
		whist.talkContract("playerC", 2);
		whist.talkContract("playerD", 2);

		return whist;
	}

}
