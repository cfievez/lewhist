package com.fievez.lewhist.controller.dto;

public class GameView {

	public PlayerStatus status;

	public WhistView whist;

	public GameView(PlayerStatus status, WhistView whist) {
		this.status = status;
		this.whist = whist;
	}

	public enum PlayerStatus {

		UNAUTHENTICATED, SPECTATING, WAITING_TO_PLAY, PLAYING

	}

}

