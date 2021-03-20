package com.fievez.lewhist.controller;

import com.fievez.lewhist.GlobalSession;
import com.fievez.lewhist.controller.dto.GameView;
import com.fievez.lewhist.controller.dto.WhistView;
import com.fievez.lewhist.domain.Card;
import com.fievez.lewhist.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("/rest")
public class MainController {

	private final static Logger logger = LoggerFactory.getLogger(MainController.class);
	public static final int MAX_HAND_SIZE = 9;
	public static final int MIN_HAND_SIZE = 4;

	private final GlobalSession globalSession;

	private final MessageSendingOperations<String> websocketTemplate;

	public MainController(GlobalSession globalSession, MessageSendingOperations<String> websocketTemplate) {
		this.globalSession = globalSession;
		this.websocketTemplate = websocketTemplate;
	}

	private void sendWebsocketMessageAndRefreshView(String message) {
		String socketPath = "/topic/whist";
		websocketTemplate.convertAndSend(socketPath, message);
		refreshViewByWebSocket();
	}

	private void refreshViewByWebSocket() {
		String socketPath = "/topic/whist";
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
		globalSession.whist.initGame(MIN_HAND_SIZE);
		refreshViewByWebSocket();
	}

	@PostMapping("/join")
	public void joinGame(HttpServletRequest request) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		globalSession.whist.addPlayer(username);
		sendWebsocketMessageAndRefreshView(username + " est arrivé");
	}

	@PostMapping("/leave")
	public void leaveGame(HttpServletRequest request) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		globalSession.whist.playerWantsToLeave(new Player(username));
	}

	@PostMapping("/contract")
	public void setContract(HttpServletRequest request, @RequestParam int value) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		globalSession.whist.talkContract(username, value);
		refreshViewByWebSocket();
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
		refreshViewByWebSocket();

		if(globalSession.whist.isWaitingForNextTrick()) {
			refreshViewByWebSocket();
			triggerNextTrickAfterADelay();
		}
	}

	@PostMapping("/distribute")
	public void distribute(HttpServletRequest request, @RequestParam String targetedPlayer) throws Exception {
		String username = getConnectedUsernameOrThrowException(request);
		globalSession.whist.pickPlayerToDistributeTo(username, targetedPlayer);
		sendWebsocketMessageAndRefreshView(username + " distribue à " + targetedPlayer);

		if(globalSession.whist.isWaitingForNextTrick()) {
			refreshViewByWebSocket();
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
					if(globalSession.whist.gameIsOver()) {
						triggerNextGameAfterADelay();
					}
					refreshViewByWebSocket();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		timer.schedule(task, 3000);
	}

	private void triggerNextGameAfterADelay() {
		final Timer timer = new Timer();

		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				try {
					globalSession.whist.initGame(generateRandomHandSize());
					refreshViewByWebSocket();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		timer.schedule(task, 8000);
	}

	private int generateRandomHandSize() {
		Random r = new Random();
		return r.nextInt(MAX_HAND_SIZE - MIN_HAND_SIZE) + MIN_HAND_SIZE;
	}

}
