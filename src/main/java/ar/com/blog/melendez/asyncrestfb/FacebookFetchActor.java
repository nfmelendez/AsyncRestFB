package ar.com.blog.melendez.asyncrestfb;

import java.lang.reflect.Proxy;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.WebRequestor;
import com.restfb.types.Page;

import akka.actor.UntypedActor;
import ar.com.blog.melendez.asyncrestfb.messages.Block;
import ar.com.blog.melendez.asyncrestfb.messages.Fetch;

public class FacebookFetchActor extends UntypedActor {

	public void onReceive(Object message) throws Exception {

		if (message instanceof Fetch) {
			Fetch f = (Fetch) message;
			WebRequestor defaultWebRequestor = new DefaultWebRequestor();
			WebRequestor proxy = (WebRequestor) Proxy.newProxyInstance(
					FacebookFetchActor.class.getClassLoader(),
					new Class[] { WebRequestor.class },
					new FacebookApiLimitedClient(defaultWebRequestor, f
							.getCordinator()));

			FacebookClient c = new DefaultFacebookClient(null, proxy,
					new DefaultJsonMapper());

			try {
				Page page = c.fetchObject("cocacola", Page.class);
				System.out.println(page.toString());
			} catch (com.restfb.exception.FacebookOAuthException fbException) {
				this.getContext().actorFor("akka://MySystem/user/Cordinator")
						.tell(new Block());
			}

		}
	}
}