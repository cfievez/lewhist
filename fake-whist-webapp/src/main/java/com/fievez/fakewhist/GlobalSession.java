package com.fievez.fakewhist;

import com.fievez.fakewhist.domain.Whist;
import org.springframework.stereotype.Component;

@Component
public class GlobalSession {

	public Whist whist = new Whist();

}
