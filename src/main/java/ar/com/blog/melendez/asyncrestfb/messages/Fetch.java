package ar.com.blog.melendez.asyncrestfb.messages;

import java.io.Serializable;

import akka.actor.ActorRef;

public class Fetch implements Serializable {

	private final ActorRef cordinator;

	public Fetch(ActorRef cordinator) {
		this.cordinator = cordinator;
	}

	public ActorRef getCordinator() {
		return cordinator;
	}
}
