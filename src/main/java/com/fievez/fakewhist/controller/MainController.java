package com.fievez.fakewhist.controller;

import com.fievez.fakewhist.GlobalSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

@Controller
public class MainController {

	private final GlobalSession globalSession;

	public MainController(GlobalSession globalSession) {
		this.globalSession = globalSession;
	}

	@GetMapping("/")
	public String main(@RequestParam(required = false) String username,
					   @RequestParam(required = false, defaultValue = "false") boolean distribution,
					   HttpServletRequest request,
					   HttpServletResponse response,
					   Model model) {
		Cookie[] cookies = request.getCookies();
		Optional<String> connectedUsername = getUsername(cookies);

		if(distribution) {
			globalSession.whist.initGame(7);
		}

		if(connectedUsername.isPresent()) {
			// connecté
			return showMainPage(connectedUsername.get(), model);
		} else if (username != null && username.length() > 3) {
			// connexion
			Cookie cookie = new Cookie("username", username);
			response.addCookie(cookie);
			return showMainPage(username, model);
		} else {
			// non connecté
			return "connexion";
		}
	}

	@GetMapping("/join")
	public String joinGame(HttpServletRequest request) throws Exception {
		globalSession.whist.addPlayer(getConnectedUsername(request));
		return "redirect";
	}

	@PostMapping("/contract")
	public String setContract(HttpServletRequest request) throws Exception {
		String username = getConnectedUsername(request);
		return "redirect";
	}

	private String showMainPage(String username, Model model) {
		model.addAttribute("username", username);
		model.addAttribute("whist", globalSession.whist);
		return "game";
	}

	private String getConnectedUsername(HttpServletRequest request) throws Exception {
		Cookie[] cookies = request.getCookies();
		Optional<String> connectedUsername = getUsername(cookies);
		if(connectedUsername.isPresent()) {
			return connectedUsername.get();
		}
		throw new Exception("Pas connecté ma couillasse");
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

}