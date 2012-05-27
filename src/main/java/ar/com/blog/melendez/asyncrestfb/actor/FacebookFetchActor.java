package ar.com.blog.melendez.asyncrestfb.actor;

import java.lang.reflect.Proxy;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.WebRequestor;
import com.restfb.types.Page;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import ar.com.blog.melendez.asyncrestfb.FacebookApiLimitedClient;
import ar.com.blog.melendez.asyncrestfb.messages.Block;
import ar.com.blog.melendez.asyncrestfb.messages.Fetch;

public class FacebookFetchActor extends UntypedActor {

	public void onReceive(Object message) throws Exception {

		if (message instanceof Fetch) {
			ActorRef cordinator = this.getContext().actorFor(
					"akka://MySystem/user/Cordinator");

			WebRequestor defaultWebRequestor = new DefaultWebRequestor();
			ClassLoader classLoader = FacebookFetchActor.class.getClassLoader();
			FacebookApiLimitedClient facebookApiLimitedClientImpl = new FacebookApiLimitedClient(
					defaultWebRequestor, cordinator);

			Class[] classesArray = new Class[] { WebRequestor.class };

			WebRequestor WebRequestorProxy = (WebRequestor) Proxy
					.newProxyInstance(classLoader, classesArray,
							facebookApiLimitedClientImpl);

			DefaultJsonMapper jsonMapper = new DefaultJsonMapper();
			FacebookClient c = new DefaultFacebookClient(null,
					WebRequestorProxy, jsonMapper);

			try {
				Page page = c.fetchObject("cocacola", Page.class);
				System.out.println(page.toString());
			} catch (com.restfb.exception.FacebookOAuthException fbException) {
				fbException.printStackTrace();
				cordinator.tell(new Block());
			}

		}
	}
}