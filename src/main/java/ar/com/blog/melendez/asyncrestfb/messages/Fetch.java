package ar.com.blog.melendez.asyncrestfb.messages;

import java.io.Serializable;
import java.util.Properties;

import akka.actor.ActorRef;

public class Fetch implements Serializable {
	
	private static final long serialVersionUID = -1899371737000326329L;
	private Properties prop = null;

	public Fetch() {
	}
	
	public Fetch(final Properties prop) {
		this.prop = prop;
	}

	public Properties getProp() {
		return prop;
	}

}
