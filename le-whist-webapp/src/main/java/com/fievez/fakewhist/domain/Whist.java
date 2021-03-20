package com.fievez.lewhist.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class Whist {

	private List<Player> playersPlaying = new ArrayList<>();
	Logger logger = LoggerFactory.getLogger(Whist.class);
	private List<Player> playersWaiting = new ArrayList<>();
	private List<Player> playersLeaving = new ArrayList<>();
	private List<Card> deck;
	private Card trump;

	private GamePhase phase = GamePhase.WAITING;
	private Player firstToPlayCurrentTrick;
	private int dealerIndex = -1;
	private int talkerIndex;
	private int handSize;
	private boolean trumpPlayed;
	private List<Trick> tricks;
	private Player pickedPlayer;

	public Whist() {
	}

	public void initGame(int handSize) {
		logger.info("Start new game with hand size of {}", handSize);
		removeLeavingPlayers();
		playersPlaying.addAll(playersWaiting);
		playersWaiting.clear();
		tricks = new ArrayList<>();
		phase = GamePhase.TALK;
		dealerIndex = (dealerIndex + 1) % playersPlaying.size();
		talkerIndex = dealerIndex;
		firstToPlayCurrentTrick = playersPlaying.get(talkerIndex);
		trumpPlayed = false;
		this.handSize = handSize;
		generateDeck();
		resetHands();
		distributeCards();
		tricks.add(new Trick(trump, trumpPlayed, playersPlaying.size()));
	}

	private void distributeCards() {
		for (int i = 1; i <= handSize; i++) {
			for (Player player : playersPlaying) {
				Card card = pickACardFromDeck();
				player.addCard(card);
			}
		}
		do {
			trump = pickACardFromDeck();
		} while (trump.getSuit().equals(Card.Suit.JOKER));
	}

	private void resetHands() {
		for (Player player : playersPlaying) {
			player.emptyHand();
			player.cancelContract();
			player.resetScore();
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
		for (int suit = 0; suit < 4; suit++) {
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

	public void playerWantsToLeave(Player leavingPlayer) {
		playersLeaving.add(leavingPlayer);
		playersWaiting = playersWaiting.stream()
				.filter(player -> !player.equals(leavingPlayer))
				.collect(Collectors.toList());
	}

	public void removeLeavingPlayers() {
		playersPlaying = playersPlaying.stream()
				.filter(player -> !playersLeaving.contains(player))
				.collect(Collectors.toList());
		playersLeaving.clear();
	}

	public List<Player> getPlayersWaiting() {
		return playersWaiting;
	}

	public List<Player> getPlayersPlaying() {
		return playersPlaying;
	}

	private Player getPlayerPlayingByName(String username) throws Exception {
		Optional<Player> player = playersPlaying.stream().filter(p -> p.getName().equals(username)).findFirst();
		if(!player.isPresent()) {
			throw new Exception("Player not found");
		}
		return player.get();
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
		if(!phase.equals(GamePhase.TALK)) {
			throw new Exception("C'est pas le moment de parler");
		} else if(number < 0) {
			throw new Exception("On peut pas dire moins que 0, tocard");
		} else if(number > handSize) {
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

		increaseTalkerIndex();
	}

	public void playCard(String username, Card card) throws Exception {
		playCard(username, card, null);
	}

	public void playCard(String username, Card card, Card.Bonus bonus) throws Exception {
		logger.info("{} is trying to play {}", username, card);

		if(!phase.equals(GamePhase.PLAY)) {
			throw new Exception("C'est pas le moment de jouer");
		}

		Player player = getNextToTalkOrPlay();
		if(!getNextToTalkOrPlay().getName().equals(username)) {
			throw new Exception("Pas à ton tour de parler, Einstein");
		}

		Trick currentTrick = getCurrentTrick();
		if(bonus != null) {
			currentTrick.play(card, player.getCards(), bonus);
		} else {
			currentTrick.play(card, player.getCards());
		}
		player.getCards().remove(card);
		player.setCardPlayed(card);

		if(card.getSuit().equals(trump.getSuit())) {
			trumpPlayed = true;
		}

		increaseTalkerIndex();

		if(trickIsOver()) {
			TrickResult currentTrickResult = getCurrentTrickResult();
			if(currentTrickResult.isDistributingType()) {
				logger.info("talkerIndex:{}, winnerFromTrickResult:{}", talkerIndex, currentTrickResult.getWinner());
				Player leader = getWinner(currentTrickResult);
				logger.info("{} has to pick a winner for this trick", leader);
				leader.setHasToPickAWinner();
				phase = GamePhase.WAITING_FOR_WINNER_TO_BE_PICKED;
			} else {
				triggerWaitingForNextTrickPhase();
			}
		}
	}

	public boolean wouldBeTwoWithBonus(Card card) {
		return getCurrentTrick().canPlayTwoWithBonus(card);
	}

	public void pickPlayerToDistributeTo(String username, String targetedPlayerUsername) throws Exception {
		TrickResult currentTrickResult = getCurrentTrickResult();

		if(!currentTrickResult.isDistributingType()) {
			throw new Exception("Not distributing time");
		}
		if(!getWinner(currentTrickResult).getName().equals(username)) {
			throw new Exception("Not to " + username + " to distribute");
		}
		if(targetedPlayerUsername.equals(username)) {
			throw new Exception("Player can't distribute to himself");
		}

		Player targetedPlayer = getPlayerPlayingByName(targetedPlayerUsername);

		if(targetedPlayer.canBePicked()) {
			getPlayerPlayingByName(username).pickAWinner();
			pickedPlayer = targetedPlayer;
			triggerWaitingForNextTrickPhase();
		}
	}

	private void triggerWaitingForNextTrickPhase() {
		phase = GamePhase.WAITING_FOR_NEXT_TRICK;
	}

	private boolean atLeastOnePlayerHasCardInHand() {
		return playersPlaying.stream().anyMatch(player -> player.getCards().size() > 0);
	}

	/**
	 * @return Current trick result
	 * @throws Exception if trick is not over
	 */
	public TrickResult getCurrentTrickResult() throws Exception {
		Trick currentTrick = getCurrentTrick();
		return currentTrick.getResult();
	}

	public void triggerNextTrick() throws Exception {
		Trick currentTrick = getCurrentTrick();
		if(trickIsOver()) {
			TrickResult trickResult = currentTrick.getResult();
			if(trickResult.isTakingType()) {
				int winner = getWinnerIndex(trickResult);
				triggerNextTrickWithWinner(winner);
			} else if(trickResult.isDistributingType() && pickedPlayer != null) {
				int winner = getPlayerIndex(pickedPlayer);
				triggerNextTrickWithWinner(winner);
			} else {
				throw new Exception("Shouldn't happen");
			}
		} else {
			throw new Exception("Impossible de lancer un nouveau pli alors que l'actuel n'est pas fini");
		}
	}

	private void triggerNextTrickWithWinner(int winnerIndex) {
		Player winner = playersPlaying.get(winnerIndex);

		winner.increaseScore();
		talkerIndex = winnerIndex;
		tricks.add(new Trick(trump, trumpPlayed, playersPlaying.size()));
		playersPlaying.forEach(Player::resetCardPlayed);
		firstToPlayCurrentTrick = winner;
		phase = GamePhase.PLAY;

		logger.info("{} wins this trick", winner.getName());

		if(!atLeastOnePlayerHasCardInHand()) {
			logger.info("Game is over, waiting for next game");
			phase = GamePhase.WAITING_FOR_NEXT_GAME;
		}
	}

	private int getWinnerIndex(TrickResult trickResult) {
		return (trickResult.getWinner() + talkerIndex) % playersPlaying.size();
	}

	private Player getWinner(TrickResult trickResult) {
		return playersPlaying.get(getWinnerIndex(trickResult));
	}

	private int getPlayerIndex(Player player) throws Exception {
		int playerIndex = playersPlaying.indexOf(player);
		if(playerIndex == -1) {
			throw new Exception("Player index not found");
		}
		return playerIndex;
	}

	private void increaseTalkerIndex() {
		talkerIndex = (talkerIndex + 1) % playersPlaying.size();
	}

	private Trick getCurrentTrick() {
		return tricks.get(tricks.size() - 1);
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

	private boolean trickIsOver() {
		return getCurrentTrick().isOver();
	}

	public boolean isWaitingForNextTrick() {
		return phase.equals(GamePhase.WAITING_FOR_NEXT_TRICK);
	}

	public boolean gameIsOver() {
		return phase.equals(GamePhase.WAITING_FOR_NEXT_GAME);
	}

	public boolean playerIsFirstToPlayCurrentTrick(String playerName) {
		return firstToPlayCurrentTrick != null && firstToPlayCurrentTrick.getName().equals(playerName);
	}

	public enum GamePhase {

		WAITING,
		TALK,
		PLAY,
		WAITING_FOR_WINNER_TO_BE_PICKED,
		WAITING_FOR_NEXT_GAME,
		WAITING_FOR_NEXT_TRICK

	}
}
