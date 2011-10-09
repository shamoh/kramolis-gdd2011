package cz.kramolis.gdd2011.android;

import android.util.Log;
import twitter4j.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ondrej Kosatka
 */
public class TwitterAdapter {

	private static final String TAG = "TwitterAdapter";

	long lastId = 123136631556411392L;//-1;

	private final Twitter twitter;

	//
	// init
	//

	public TwitterAdapter() {
		twitter = new TwitterFactory().getInstance();
	}

	//
	// business
	//

	public List<PlayRequest> search(LaPardonApplication application, String param) {
		try {
			Query query = new Query(param);
			if (lastId != -1) {
				query.setSinceId(lastId);
			}
			QueryResult result = twitter.search(query);
			lastId = result.getMaxId();
			List<Tweet> tweets = result.getTweets();
			int readCount = tweets.size();

			Log.d(TAG, "Query result size: " + readCount + "; maxId: " + lastId);

			List<PlayRequest> requests = new ArrayList<PlayRequest>(readCount);
			for (Tweet tweet : tweets) {
				//TODO ignore older tweets!!! Asi by se dalo persistentne ukladat prave lastId.

				String tweetMsg = "@" + tweet.getFromUser() + " - " + tweet.getText();
				try {
					MusicNotation musicNotation = MusicNotation.lookup(tweet.getText());
					if (musicNotation == null) {
						updateStatusUser(application, tweet.getFromUser(),
								String.format("Your request from %tT does not contain any music notation.", tweet.getCreatedAt()),
								application.getPrefInfoHashtag());
					} else if (musicNotation.getNotation().length() == 0) {
						updateStatusUser(application, tweet.getFromUser(),
								String.format("Your request from %tT contains EMPTY music notation.", tweet.getCreatedAt()),
								application.getPrefWarnHashtag());
					} else {
						updateStatusUser(application, tweet.getFromUser(),
								String.format("Your request from %tT is correct and was added to playing queue.", tweet.getCreatedAt()),
								application.getPrefInfoHashtag());
						PlayRequest playRequest = new PlayRequest(tweet.getText(), tweet.getFromUser(), tweet.getCreatedAt(), musicNotation);
						requests.add(playRequest);
					}
				} catch (IllegalArgumentException ex) {
					Log.w(TAG, "Wrong music notation " + tweetMsg, ex);
					updateStatusUser(application, tweet.getFromUser(),
							String.format("Your request from %tT contains errors - %s", tweet.getCreatedAt(), ex.getMessage()),
							application.getPrefErrorHashtag());
				}
			}

			if (readCount > 0) {
				updateStatusSearch(application, readCount, requests.size(), lastId);
			}

			return requests;
		} catch (TwitterException te) {
			Log.e(TAG, "ERROR", te);
		}
		return null;
	}

	private void updateStatusUser(LaPardonApplication application, String author, String message, String... tags) {
		String searchMessage = String.format("@%s %s", author, message);
		Status status = updateStatus(searchMessage, tags);
		Log.d(TAG, "Reply status [" + (status != null ? status.getText() : "<null>") + "].");
	}

	private void updateStatusSearch(LaPardonApplication application, int readCount, int okRequests, long maxId) {
		String searchMessage = String.format("Just read %s new tweets, %s correct at %tT, maxId %s.",
				readCount, okRequests, new Date(), maxId);
		Status status = updateStatus(searchMessage, application.getPrefInfoHashtag());
		Log.d(TAG, "Search status [" + (status != null ? status.getText() : "<null>") + "].");
	}

	private Status updateStatus(String text, String... tags) {
		Status status = null;

		Twitter twitter = new TwitterFactory().getInstance();
		try {
			StringBuilder sb = new StringBuilder();
			for (String tag : tags) {
				sb.append(' ').append(tag);
			}
			int maxLenth = 140 - sb.length();
			if (maxLenth < text.length()) {
				Log.w(TAG, "Text is too long: " + text + "; tags: " + sb);
				text = text.substring(0, maxLenth - 3) + "...";
			}
			sb.insert(0, text);

			status = twitter.updateStatus(sb.toString());
		} catch (TwitterException te) {
			Log.e(TAG, "ERROR", te);
		}
		return status;
	}

}
