package com.fievez.lewhist;

import com.fievez.lewhist.domain.Whist;
import org.springframework.stereotype.Component;

@Component
public class GlobalSession {

	public Whist whist = new Whist();

}
