package ar.com.blog.melendez.asyncrestfb.actor;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Scheduler;
import akka.actor.UntypedActor;
import akka.util.Duration;
import ar.com.blog.melendez.asyncrestfb.messages.Audit;
import ar.com.blog.melendez.asyncrestfb.messages.Block;
import ar.com.blog.melendez.asyncrestfb.messages.Cordinate;
import ar.com.blog.melendez.asyncrestfb.messages.Token;

public class Cordinator extends UntypedActor {

	private static Logger log = Logger.getLogger(Cordinator.class);

	Queue<ActorRef> queue = new LinkedList<ActorRef>();

	private int maxAPICallsPerMinute = 60; // 600 api calls per 600 seg.

	private int bucket = 0;

	private Cancellable cancellable;

	private final Scheduler scheduler;

	private int maxWaitTime;

	public Cordinator(Scheduler scheduler) {

		long rate = this.calculateRate();
		cancellable = scheduler.schedule(
				Duration.create(maxWaitTime, TimeUnit.MINUTES),
				Duration.create(rate, TimeUnit.MILLISECONDS), self(),
				new Token());

		scheduler.schedule(Duration.Zero(),
				Duration.create(1, TimeUnit.MINUTES), self(), new Audit());

		this.scheduler = scheduler;
	}

	public void onReceive(Object message) throws Exception {

		if (message instanceof Audit) {
			DateTime dateTime = new DateTime();
			String date = dateTime.toString("YYYY-MM-dd-HH");
			log.info("Max api calls per minute: " + maxAPICallsPerMinute);
		}

		if (message instanceof Block) {
			cancellable.cancel();
			bucket = 0;
			maxAPICallsPerMinute = maxAPICallsPerMinute - 2;
			long rate = this.calculateRate();
			maxWaitTime = 6;
			cancellable = scheduler.schedule(
					Duration.create(maxWaitTime, TimeUnit.MINUTES),
					Duration.create(rate, TimeUnit.MILLISECONDS), self(),
					new Token());
			log.info(new Date().toString()
					+ " LIMIT REACHED,ALL BLOCKED,  START IN " + maxWaitTime
					+ " MINUTES WITH " + maxAPICallsPerMinute
					+ " Call per minute and rate: " + rate);
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
			this.bucket++;
			if (this.bucket > maxAPICallsPerMinute) {
				this.bucket = maxAPICallsPerMinute;
			}
			if (!queue.isEmpty()) {
				queue.remove().tell("YouCanFetch");
				this.bucket--;
			}

		}

	}

	private long calculateRate() {
		return (long) ((60 * 1000) / maxAPICallsPerMinute);
	}

}