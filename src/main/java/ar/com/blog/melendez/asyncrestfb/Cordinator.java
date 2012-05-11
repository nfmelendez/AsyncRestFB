package ar.com.blog.melendez.asyncrestfb;

import java.util.LinkedList;
import java.util.Queue;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

public class Cordinator extends UntypedActor {

	Queue<ActorRef> queue = new LinkedList<ActorRef>();

	private static int MAX_BUCKET = 600;

	private int bucket = 100;

	public void onReceive(Object message) throws Exception {

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
			System.out.println(queue.size());
			if (this.bucket > MAX_BUCKET) {
				this.bucket = MAX_BUCKET;
			} else {
				this.bucket--;
				if (!queue.isEmpty()) {
					queue.remove().tell("YouCanFetch");
//					System.out.println("thread coordinated");
				}
			}
		}

	}

}