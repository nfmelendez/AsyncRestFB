package ar.com.blog.melendez.asyncrestfb;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import ar.com.blog.melendez.asyncrestfb.messages.Fetch;

/**
 * Hello world!
 * 
 */
public class Main {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("MySystem");

		// Metadata persistence actor
		final ActorRef resourceFetcher = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new FacebookFetchActor();
					}
				}).withRouter(new RoundRobinRouter(10)), FacebookFetchActor.class.getName());
		
		final Scheduler scheduler = system.scheduler();
		final ActorRef cordinator = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new Cordinator(scheduler);
					}
				}).withRouter(new RoundRobinRouter(1)), Cordinator.class.getSimpleName());
		
		for (int i = 0; i < 1000000; i++) {
			resourceFetcher.tell(new Fetch());
		}
		System.out.println("FINAL");
	}
}
