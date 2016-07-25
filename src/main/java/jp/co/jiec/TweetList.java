package jp.co.jiec;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.New;
import javax.inject.Inject;
import javax.inject.Named;

import twitter4j.HashtagEntity;
import twitter4j.Query;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;

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
	public void init() {
		try {
			action();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	}
	
	public String getText() {
		return text.orElse("*");
	}
	
	public void setText(String text) throws TwitterException {
		this.text = (text == null || "".equals(text)) ? Optional.of("*") : Optional.of(text);
		action();
	}
	
	public List<Status> getList() {
		return list;
	}
	
	public void setList(List<Status> list) {
		this.list.clear();
		this.list.addAll(list);
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
	
	public String send(){
		return "?text=" + this.getText();
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
	
	private String toURLString(String origin, String url){
		return toURLString(origin, url, url);
	}
	private String toURLString(String origin, String target, String url){
		return origin.replaceAll(target, "&nbsp;<a href=\"" + url + "\">"+ target + "</a>");
	}
	public String formatFor(Status status) throws UnsupportedEncodingException{
		String result = status.getText().replaceFirst(" +[A-z0-9\\:\\/\\#\\.]*… ?$", "");
		for(UserMentionEntity user : status.getUserMentionEntities()){
			result = toURLString(result, "@" + user.getScreenName(), "?userID=" + user.getScreenName());
		}
		for(HashtagEntity tag : status.getHashtagEntities()){
			if(!result.contains("#" + tag.getText())){
				result += "&nbsp;<a href=\"?text=#" + tag.getText() + "\">" + URLEncoder.encode("#" + tag.getText(), "UTF-8") + "</a>";
			}else{
				result = toURLString(result, "#" + tag.getText(), "?text=" + URLEncoder.encode("#" + tag.getText(), "UTF-8"));
			}
		}
		
		List<URLEntity> urls = new ArrayList<>();
		Collections.addAll(urls, status.getURLEntities());
		Collections.addAll(urls, status.getMediaEntities());
		for(URLEntity entity : urls){
			if(!result.contains(entity.getURL())){
				result += "&nbsp;<a href=\"" + entity.getURL() + "\">" + entity.getURL() + "</a>";
			}else{
				result = toURLString(result, entity.getURL());
			}
		}
		return result;
	}
}