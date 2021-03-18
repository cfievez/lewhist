package com.fievez.fakewhist.controller.dto;

import com.fievez.fakewhist.domain.Player;

import java.util.List;
import java.util.stream.Collectors;

public class WhistView {

	public String trumpCard;

	public List<PlayerView> players;

	public List<String> waitingPlayers;

	public boolean hasToPickAWinner;

	public boolean gameIsOver;

	public static WhistView from(com.fievez.fakewhist.domain.Whist whist) {
		return from(whist, "");
	}

	public static WhistView from(com.fievez.fakewhist.domain.Whist whist, String connectedUserName) {
		WhistView whistView = new WhistView();
		whistView.trumpCard = whist.getTrump() != null ? whist.getTrump().toString() : "";
		whistView.waitingPlayers = whist.getPlayersWaiting().stream()
				.map(com.fievez.fakewhist.domain.Player::getName)
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
		return whistView;
	}

}