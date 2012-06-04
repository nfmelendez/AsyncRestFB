package ar.com.blog.melendez.asyncrestfb.actor;

import java.lang.reflect.Proxy;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.restfb.DefaultFacebookClient;
import com.restfb.DefaultJsonMapper;
import com.restfb.DefaultWebRequestor;
import com.restfb.FacebookClient;
import com.restfb.WebRequestor;
import com.restfb.types.Page;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import ar.com.blog.melendez.asyncrestfb.FacebookApiLimitedClient;
import ar.com.blog.melendez.asyncrestfb.Main;
import ar.com.blog.melendez.asyncrestfb.messages.Block;
import ar.com.blog.melendez.asyncrestfb.messages.Fetch;

public class FacebookFetchActor extends UntypedActor {
	 private static Logger log = Logger.getLogger(FacebookFetchActor.class);


	public void onReceive(Object message) throws Exception {

		if (message instanceof Fetch) {
			ActorRef cordinator = this.getContext().actorFor(
					"akka://" + Main.SYSTEM_NAME + "/user/Cordinator");

			WebRequestor defaultWebRequestor = new DefaultWebRequestor();
			ClassLoader classLoader = FacebookFetchActor.class.getClassLoader();
			FacebookApiLimitedClient facebookApiLimitedClientImpl = new FacebookApiLimitedClient(
					defaultWebRequestor, cordinator);

			Class[] classesArray = new Class[] { WebRequestor.class };

			WebRequestor WebRequestorProxy = (WebRequestor) Proxy
					.newProxyInstance(classLoader, classesArray,
							facebookApiLimitedClientImpl);

			DefaultJsonMapper jsonMapper = new DefaultJsonMapper();
			
			
			
			Fetch fetchMessage = (Fetch) message;
			FacebookClient c = new DefaultFacebookClient(fetchMessage.getProp().getProperty("token"),
					WebRequestorProxy, jsonMapper);
			try {
				fetch(c,fetchMessage.getProp());
			} catch (com.restfb.exception.FacebookOAuthException fbException) {
				fbException.printStackTrace();
				cordinator.tell(new Block());
			}

		}
	}


	public void fetch(FacebookClient facebookClient, Properties properties) {
		Page page = facebookClient.fetchObject("cocacola", Page.class);
		log.info(page.toString());
	}
}