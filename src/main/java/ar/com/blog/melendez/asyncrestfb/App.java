package ar.com.blog.melendez.asyncrestfb;

import java.util.concurrent.TimeUnit;

import com.sun.tools.internal.xjc.generator.bean.DualObjectFactoryGenerator;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Cancellable;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.actor.UntypedActorFactory;
import akka.routing.RoundRobinRouter;
import akka.util.Duration;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		ActorSystem system = ActorSystem.create("MySystem");

		// Metadata persistence actor
		final ActorRef resourceFetcher = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new FacebookFetchActor();
					}
				}).withRouter(new RoundRobinRouter(10)), FacebookFetchActor.class.getName());
		
		final ActorRef cordinator = system.actorOf(new Props(
				new UntypedActorFactory() {
					public UntypedActor create() {
						return new Cordinator();
					}
				}).withRouter(new RoundRobinRouter(1)), Cordinator.class.getSimpleName());
		
	    Cancellable cancellable = system.scheduler().schedule(Duration.Zero(), Duration.create(200, TimeUnit.MILLISECONDS),
	            cordinator, new Token());

		for (int i = 0; i < 1000000; i++) {
			resourceFetcher.tell(new Fetch(cordinator));
		}
		System.out.println("FINAL");
	}
}
