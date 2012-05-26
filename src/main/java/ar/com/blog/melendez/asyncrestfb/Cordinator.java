package ar.com.blog.melendez.asyncrestfb;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.util.Duration;
import ar.com.blog.melendez.asyncrestfb.messages.Block;
import ar.com.blog.melendez.asyncrestfb.messages.Cordinate;
import ar.com.blog.melendez.asyncrestfb.messages.Token;

public class Cordinator extends UntypedActor {

	Queue<ActorRef> queue = new LinkedList<ActorRef>();

	private int maxAPICallsPerMinute = 1000;

	private int bucket = 0;

	private Cancellable cancellable;

	private final Scheduler scheduler;

	public Cordinator(Scheduler scheduler) {
		long rate = this.calculateRate();
		cancellable = scheduler.schedule(Duration.Zero(),
				Duration.create(rate, TimeUnit.MILLISECONDS), self(),
				new Token());
		this.scheduler = scheduler;
	}

	public void onReceive(Object message) throws Exception {

		if (message instanceof Block) {
			cancellable.cancel();
			bucket = 0;
			maxAPICallsPerMinute--;
			long rate = this.calculateRate();
			cancellable = scheduler.schedule(
					Duration.create(2, TimeUnit.MINUTES),
					Duration.create(rate, TimeUnit.MILLISECONDS), self(),
					new Token());
			System.out
					.println("LIMIT REACHED,ALL BLOCKED,  START IN 2 MINUTES WITH "
							+ maxAPICallsPerMinute + " Call per minute");
		}

		if (message instanceof Cordinate) {
			ActorRef sender = this.getSender();
			if (this.bucket > 0) {
				this.bucket--;
				sender.tell("YouCanFetch");
			} else {
				queue.add(sender);
			}
		}

		if (message instanceof Token) {
			if (this.bucket > maxAPICallsPerMinute) {
				this.bucket = maxAPICallsPerMinute;
			} else {
				if (!queue.isEmpty()) {
					queue.remove().tell("YouCanFetch");
					this.bucket--;
				}
			}
		}

	}

	private long calculateRate() {
		return (long) ( (60 * 1000) / maxAPICallsPerMinute);
	}

}