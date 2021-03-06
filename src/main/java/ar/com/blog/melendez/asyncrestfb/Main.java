package ar.com.blog.melendez.asyncrestfb;

import org.apache.log4j.Logger;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import ar.com.blog.melendez.asyncrestfb.actor.Cordinator;

/**
 * Hello world!
 * 
 */
public class Main {
	public static final String SYSTEM_NAME = "AsyncRestFB";
	
	private static final Logger log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
	}

	public static ActorSystem init() {
		ActorSystem system = ActorSystem.create(SYSTEM_NAME);		
		final Scheduler scheduler = system.scheduler();
		final ActorRef cordinator = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new Cordinator(scheduler);
					}
				}).withRouter(new RoundRobinRouter(1)), Cordinator.class.getSimpleName());
		return system;
	}
}
