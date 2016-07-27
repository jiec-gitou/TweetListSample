package jp.co.jiec;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * TwitterFactoryのプロデューサー
 * TODO:演習では各自で作成してもらうつもり
 * @author K.Taira
 */
@ApplicationScoped
public class TwitterFactoryProducer {
	private TwitterFactory tf;
	@Produces
	public TwitterFactory produce(InjectionPoint ip){
		if(tf == null){
			System.setProperty("twitter4j.http.useSSL","false"); 
			ConfigurationBuilder cb = new ConfigurationBuilder();
			cb.setDebugEnabled(true)
			  .setOAuthConsumerKey("")
			  .setOAuthConsumerSecret("")
			  .setOAuthAccessToken("")
			  .setOAuthAccessTokenSecret("");
			tf = new TwitterFactory(cb.build());
		}
		return tf;
	}
}
