package jp.co.jiec;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Named;

import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

/**
 * ツイートのリスト
 * @author K.Taira
 */
@Named
@RequestScoped
public class TweetList implements Serializable {
	private static final long serialVersionUID = -5902839809871261693L;
	/** 検索テキスト */
	private Optional<String> text = Optional.empty();
	
	/** ツイートのリストの実体 */
	@Inject
	@New(ArrayList.class)
	private List<Status> list;
	
	@Inject
	private TwitterFactory tf;
	
	@PostConstruct
	public void init(){
	}
	
	public String getText() {
		return text.orElse("*");
	}
	
	public void setText(String text) {
		this.text = Optional.ofNullable(text);
		if(this.text.orElse("").length() == 0){
			this.text = Optional.of("*");
		}
	}
	
	public List<Status> getList() {
		return list;
	}
	
	public void setList(List<Status> list) {
		this.list.clear();
		this.list.addAll(list);
	}
	
	public String getHash(){
		return this.text.orElse("*");
	}
	
	/**
	 * 検索用ハッシュの設定
	 * @param hash
	 * @throws TwitterException
	 */
	public void setHash(String hash) throws TwitterException {
		this.text = Optional.of("#" + hash);
		this.action();
	}
	
	public String getUserID(){
		return this.text.orElse("*");
	}
	
	/**
	 * 検索用ユーザIDの設定
	 * @param id
	 * @throws TwitterException
	 */
	public void setUserID(String id) throws TwitterException {
		this.text = Optional.of("@" + id);
		action("@" + id);
	}
	
	/**
	 * 送信ボタンのハンドラ
	 * @throws TwitterException
	 */
	public void action() throws TwitterException{
		if(this.text.orElse("*").startsWith("@")){
			action(this.text.get());
			return;
		}
		Twitter twitter = tf.getInstance();
		Query query = new Query();
		
		query.setQuery(this.text.orElse("*"));
		query.setLang("ja");
		
		this.setList(twitter.search(query).getTweets());
	}

	/**
	 * ユーザIDの検索
	 * @param id
	 * @throws TwitterException
	 */
	public void action(String id) throws TwitterException{
		if(!id.startsWith("@")){
			action();
			return;
		}
		Twitter twitter = tf.getInstance();
		this.setList(twitter.getUserTimeline(id));
	}
}