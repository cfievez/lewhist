package com.fievez.fakewhist.controller;

import com.fievez.fakewhist.GlobalSession;
import com.fievez.fakewhist.controller.dto.GameView;
import com.fievez.fakewhist.controller.dto.WhistView;
import com.fievez.fakewhist.domain.Card;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

@RestController
@RequestMapping("/rest")
public class MainController {

	private final static Logger logger = LoggerFactory.getLogger(MainController.class);

	private final GlobalSession globalSession;

	private final MessageSendingOperations<String> websocketTemplate;

	public MainController(GlobalSession globalSession, MessageSendingOperations<String> websocketTemplate) {
		this.globalSession = globalSession;
		this.websocketTemplate = websocketTemplate;
	}

	private void sendMessageToWebsocket(String message) {
		String socketPath = "/topic/whist";
		websocketTemplate.convertAndSend(socketPath, message);
		websocketTemplate.convertAndSend(socketPath, "refresh");
	}

	@GetMapping("/whist")
	public GameView whist(HttpServletRequest request) {
		Optional<String> connectedUsername = getConnectedUsername(request);
		WhistView whistView = connectedUsername
				.map(u -> WhistView.from(globalSession.whist, u))
				.orElseGet(() -> WhistView.from(globalSession.whist));

		GameView.PlayerStatus playerStatus = connectedUsername
				.map(username -> {
					if(globalSession.whist.getPlayersWaiting().stream().anyMatch(p -> p.getName().equals(connectedUsername.get()))) {
						return GameView.PlayerStatus.WAITING_TO_PLAY;
					} else if(globalSession.whist.getPlayersPlaying().stream().anyMatch(p -> p.getName().equals(connectedUsername.get()))) {
						return GameView.PlayerStatus.PLAYING;
					} else {
						return GameView.PlayerStatus.SPECTATING;
					}
				})
				.orElse(GameView.PlayerStatus.UNAUTHENTICATED);

		return new GameView(playerStatus, whistView);
	}

	@PostMapping("/login")
	public void login(@RequestParam String username,
					  HttpServletRequest request,
					  HttpServletResponse response) throws Exception {
		Optional<String> connectedUsername = getConnectedUsername(request);

		if(connectedUsername.isPresent()) {
			throw new Exception("Déjà connecté");
		}
		if (username == null || username.length() < 3) {
			throw new Exception("Usename doit excéder 3 lettres");
		}

		// connexion
		Cookie cookie = new Cookie("username", username);
		response.addCookie(cookie);
	}

	@PostMapping("/start")
	public void main(HttpServletRequest request) throws Exception {
		getConnectedUsernameOrThrowException(request);
		globalSession.whist.initGame(13);
	}

	@PostMapping("/join")
	public void joinGame(HttpServletRequest request) throws Exception {
		globalSession.whist.addPlayer(getConnectedUsernameOrThrowException(request));
	}

	@PostMapping("/contract")
	public void setContract(HttpServletRequest request, @RequestParam int value) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		globalSession.whist.talkContract(username, value);
		sendMessageToWebsocket(username + " said " + value);
	}

	@PostMapping("/play")
	public void play(HttpServletRequest request, @RequestParam String card,
					 @RequestParam(required = false) Card.Bonus bonus) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		if(bonus != null) {
			globalSession.whist.playCard(username, Card.fromString(card), bonus);
		} else {
			globalSession.whist.playCard(username, Card.fromString(card));
		}
		sendMessageToWebsocket(username + " played " + card + (bonus != null ? " + " + bonus.toString() : ""));

		if(globalSession.whist.isWaitingForNextTrick()) {
			sendMessageToWebsocket("Fin du pli");
			triggerNextTrickAfterADelay();
		}
	}

	@PostMapping("/distribute")
	public void distribute(HttpServletRequest request, @RequestParam String targetedPlayer) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		globalSession.whist.pickPlayerToDistributeTo(username, targetedPlayer);

		if(globalSession.whist.isWaitingForNextTrick()) {
			sendMessageToWebsocket("Fin du pli");
			triggerNextTrickAfterADelay();
		}
	}

	private String getConnectedUsernameOrThrowException(HttpServletRequest request) throws Exception {
		Optional<String> connectedUsername = getConnectedUsername(request);
		if(connectedUsername.isPresent()) {
			return connectedUsername.get();
		}
		throw new Exception("Pas connecté ma couillasse");
	}

	private Optional<String> getConnectedUsername(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		return getUsername(cookies);
	}

	private Optional<String> getUsername(Cookie[] cookies) {
		if(cookies != null) {
			return Arrays.stream(cookies)
					.filter(cookie -> cookie.getName().equals("username"))
					.map(Cookie::getValue)
					.findFirst();
		}
		return Optional.empty();
	}

	private void triggerNextTrickAfterADelay() {
		final Timer timer = new Timer();

		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					globalSession.whist.triggerNextTrick();
					sendMessageToWebsocket("Début du pli");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		timer.schedule(task, 3000);
	}

}