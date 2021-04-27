package com.fievez.lewhist.controller.dto;

import com.fievez.lewhist.domain.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WhistView {

	public String trumpCard;

	public List<PlayerView> players;

	public List<String> waitingPlayers;

	public boolean hasToPickAWinner;

	public boolean gameIsOver;

	public int handSize;

	public int totalSaid;

	public static WhistView from(com.fievez.lewhist.domain.Whist whist) {
		return from(whist, "");
	}

	public static WhistView from(com.fievez.lewhist.domain.Whist whist, String connectedUserName) {
		WhistView whistView = new WhistView();
		whistView.trumpCard = whist.getTrump() != null ? whist.getTrump().toString() : "";
		whistView.waitingPlayers = whist.getPlayersWaiting().stream()
				.map(com.fievez.lewhist.domain.Player::getName)
				.collect(Collectors.toList());
		whistView.players = whist.getPlayersPlaying().stream()
				.map(player -> PlayerView.from(player, whist, connectedUserName))
				.collect(Collectors.toList());
		whistView.hasToPickAWinner = whist.getPlayersPlaying().stream()
				.filter(player -> player.getName().equals(connectedUserName))
				.findFirst()
				.map(Player::hasToPickAWinner)
				.orElse(false);
		whistView.gameIsOver = whist.gameIsOver();
		whistView.handSize = whist.getHandSize();
		whistView.totalSaid = whist.totalSaid();
		return whistView;
	}

}
