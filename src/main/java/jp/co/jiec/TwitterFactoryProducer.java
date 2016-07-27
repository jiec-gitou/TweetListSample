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
			  .setOAuthConsumerKey("O6qaM2V26FSqe8kwFt57s4U5J")
			  .setOAuthConsumerSecret("MOV9K9nHRX0xJXieJSRV6XaU5qVFZjWOWui2GblSm4UsK3CKB6")
			  .setOAuthAccessToken("137517621-8NyP7ovNPCF3eNPNDmwq5yo9tkB4HBDQNcxWkNz6")
			  .setOAuthAccessTokenSecret("83Zj6zpRn3ZtLXu1RjTxleYGKMYgfmv09u5z4HWYQHgSA");
			tf = new TwitterFactory(cb.build());
		}
		return tf;
	}
}