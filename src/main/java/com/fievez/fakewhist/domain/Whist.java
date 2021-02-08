package com.fievez.fakewhist.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Whist {

	private List<Player> playersWaiting = new ArrayList<>();
	private List<Player> playersPlaying = new ArrayList<>();

	private List<Card> deck;
	private Card trump;

	private GamePhase phase = GamePhase.WAITING;
	private int turn;
	private int talkerIndex;
	private int handSize;
	private boolean trumpPlayed;
	private List<Trick> tricks;

	private int gameCount = 0;

	public Whist() {
	}

	public void initGame(int handSize) {
		playersPlaying.addAll(playersWaiting);
		playersWaiting.clear();
		tricks = new ArrayList<>();
		phase = GamePhase.TALK;
		turn = 0;
		talkerIndex = 0;
		trumpPlayed = false;
		this.handSize = handSize;
		generateDeck();
		resetHands();
		distributeCards();
	}

	private void distributeCards() {
		for(int i = 1; i <= handSize; i++) {
			for(Player player : playersPlaying) {
				Card card = pickACardFromDeck();
				player.addCard(card);
			}
		}
		do {
			trump = pickACardFromDeck();
		} while (trump.getSuit().equals(Card.Suit.JOKER));
	}

	private void resetHands() {
		for(Player player : playersPlaying) {
			player.emptyHand();
			player.cancelContract();
		}
	}

	private Card pickACardFromDeck() {
		Random r = new Random();
		int low = 0;
		int max = deck.size();
		int pickCardIndex = r.nextInt(max - low) + low;
		return deck.remove(pickCardIndex);
	}

	private void generateDeck() {
		deck = new ArrayList<>();
		for(int suit = 0; suit < 4; suit++) {
			for (int value = 2; value <= 14; value++) {
				deck.add(new Card(Card.Suit.fromCode(suit), value));
			}
		}
		deck.add(new Card(Card.Suit.JOKER, 0));
		deck.add(new Card(Card.Suit.JOKER, 1));
	}

	public void addPlayer(String playerName) {
		if(playerAlreadyExist(playerName)) {
			playersWaiting.add(new Player(playerName));
		} else {
			throw new RuntimeException("player name already taken");
		}
	}

	private boolean playerAlreadyExist(String playerName) {
		return playersPlaying.stream().noneMatch(player -> player.getName().equals(playerName))
				&& playersWaiting.stream().noneMatch(player -> player.getName().equals(playerName));
	}

	public void removePlayer(String playerName) {
		playersWaiting = playersWaiting.stream()
				.filter(player -> !player.getName().equals(playerName))
				.collect(Collectors.toList());
	}

	public List<Player> getPlayersPlaying() {
		return playersPlaying;
	}

	private Player getPlayerPlayingByName(String username) {
		return playersPlaying.stream().filter(player -> player.getName().equals(username)).findFirst().get();
	}

	public Card getTrump() {
		return trump;
	}

	public GamePhase getPhase() {
		return phase;
	}

	public Player getNextToTalkOrPlay() {
		if(phase == GamePhase.WAITING) {
			return null;
		}
		return playersPlaying.get(talkerIndex);
	}

	public void talkContract(String username, int number) throws Exception {
		if(number < 0) {
			throw new Exception("On peut pas dire moins que 0, tocard");
		} else if (number > handSize) {
			throw new Exception("On peut pas dire plus que ce qu'il n'y de cartes, imbécile");
		} else if(!getNextToTalkOrPlay().getName().equals(username)) {
			throw new Exception("Pas à ton tour de parler, bouffon");
		} else if(!allowedToTalkThatNumber(number)) {
			throw new Exception("T'as pas le droit de dire " + number + ", zebi");
		}
		Player player = getPlayerPlayingByName(username);
		player.setContract(number);

		if(getNumberOfPlayersLeftToTalk() == 0) {
			phase = GamePhase.PLAY;
		}

		talkerIndex = (talkerIndex + 1) % playersPlaying.size();
	}

	public void playCard(String username, Card card) throws Exception {
		if(!getNextToTalkOrPlay().getName().equals(username)) {
			throw new Exception("Pas à ton tour de parler, Einstein");
		}
	}

	private boolean allowedToTalkThatNumber(int number) {
		long playersLeftToTalkCount = getNumberOfPlayersLeftToTalk();
		if(playersLeftToTalkCount == 1) {
			return number + getCurrentContractTotal() != handSize;
		}
		return true;
	}

	private int getCurrentContractTotal() {
		return playersPlaying.stream()
				.filter(player -> player.getContract().isPresent())
				.map(player -> player.getContract().get())
				.reduce(0, Integer::sum);
	}

	private long getNumberOfPlayersLeftToTalk() {
		return playersPlaying.stream()
				.filter(player -> !player.getContract().isPresent())
				.count();
	}

	public enum GamePhase {

		WAITING,
		TALK,
		PLAY

	}
}
