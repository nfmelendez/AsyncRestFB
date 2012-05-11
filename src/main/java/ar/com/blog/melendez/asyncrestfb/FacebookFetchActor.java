package ar.com.blog.melendez.asyncrestfb;

import java.lang.reflect.Proxy;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.types.Page;

import akka.actor.UntypedActor;

public class FacebookFetchActor extends UntypedActor {

	public void onReceive(Object message) throws Exception {

		if (message instanceof Fetch) {
			Fetch f  = (Fetch) message;
			FacebookClient publicOnlyFacebookClient = new DefaultFacebookClient();
			FacebookClient proxy = (FacebookClient) Proxy.newProxyInstance(
					FacebookFetchActor.class.getClassLoader(),
					new Class[] { FacebookClient.class },
					new FacebookApiLimitedClient(publicOnlyFacebookClient,f.getCordinator()));

			Page page = proxy.fetchObject("cocacola", Page.class);
//			 System.out.println(page.toString());
		}
	}
}