package cz.kramolis.gdd2011.android;

import android.util.Log;
import twitter4j.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Ondrej Kosatka
 * @author Libor Kramolis
 */
public class TwitterAdapter {

	private static final String TAG = "LaPardon.TwitterAdapter";

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
		QueryResult result = null;
		long lastId = -1;

		TwitterDbAdapter dbAdapter = new TwitterDbAdapter(application);
		try {
			dbAdapter.open();
			lastId = dbAdapter.getLastTweetId();
			Log.d(TAG, "LastTweetId from DB: " + lastId);

			Query query = new Query(param);
			if (lastId != -1) {
				query.setSinceId(lastId);
			} else {
				dbAdapter.init(0, new Date());
			}
			result = twitter.search(query);

			lastId = result.getMaxId();
			dbAdapter.update(lastId, new Date());
		} catch (TwitterException te) {
			Log.e(TAG, "ERROR", te);
		} finally {
			dbAdapter.close();
		}

		List<PlayRequest> requests = null;
		if (result != null) {
			List<Tweet> tweets = result.getTweets();
			int readCount = tweets.size();

			Log.d(TAG, "Query result size: " + readCount + "; maxId: " + lastId);

			requests = new ArrayList<PlayRequest>(readCount);
			for (Tweet tweet : tweets) {
				String tweetMsg = "@" + tweet.getFromUser() + " - " + tweet.getText();
				try {
					MusicNotation musicNotation = MusicNotation.lookup(tweet.getText());
					if (musicNotation == null) {
						updateStatusUser(application, tweet.getFromUser(), tweet.getId(),
								String.format("Your request from %tT does not contain any music notation.", tweet.getCreatedAt()),
								application.getPrefInfoHashtag());
					} else if (musicNotation.getNotation().length() == 0) {
						updateStatusUser(application, tweet.getFromUser(), tweet.getId(),
								String.format("Your request from %tT contains empty music notation.", tweet.getCreatedAt()),
								application.getPrefWarnHashtag());
					} else {
						updateStatusUser(application, tweet.getFromUser(), tweet.getId(),
								String.format("Your request from %tT is correct and was added to playing queue.", tweet.getCreatedAt()),
								application.getPrefInfoHashtag());
						PlayRequest playRequest = new PlayRequest(tweet.getId(), tweet.getText(), tweet.getFromUser(), tweet.getCreatedAt(), musicNotation);
						requests.add(playRequest);
					}
				} catch (IllegalArgumentException ex) {
					Log.w(TAG, "Wrong music notation " + tweetMsg, ex);
					updateStatusUser(application, tweet.getFromUser(), tweet.getId(),
							String.format("Your request from %tT is incorrect - %s", tweet.getCreatedAt(), ex.getMessage()),
							application.getPrefErrorHashtag());
				}
			}

//			if (readCount > 0) {
			updateStatusSearch(application, readCount, requests.size(), lastId);
//			}
		}
		return requests;
	}

	private void updateStatusUser(LaPardonApplication application, String author, long replyToId, String message, String... tags) {
		String searchMessage = String.format("@%s %s", author, message);
		Status status = updateStatus(replyToId, searchMessage, tags);
		Log.d(TAG, "Reply status [" + (status != null ? status.getText() : "<null>") + "].");
	}

	private void updateStatusSearch(LaPardonApplication application, int readCount, int okRequests, long maxId) {
		String searchMessage = String.format("Just read %s new tweets (including %s incorrect) at %tT, last tweet Id: %s.",
				readCount, (readCount - okRequests), new Date(), maxId);

		application.addJournalTwitterSearch(searchMessage);

		if (readCount > 1) {
			//??? Mozna by se hodilo delat nejake pauzy, at nedojde Twitter limit
			Status status = updateStatus(null, searchMessage, application.getPrefInfoHashtag());
			Log.d(TAG, "Search status [" + (status != null ? status.getText() : "<null>") + "].");
		}
	}

	private Status updateStatus(Long replyToId, String text, String... tags) {
		Status status = null;
		StatusUpdate statusUpdate = null;
		{
			StringBuilder sb = new StringBuilder();
			for (String tag : tags) {
				sb.append(' ').append(tag);
			}
			int maxLength = 140 - sb.length();
			if (maxLength < text.length()) {
				Log.w(TAG, "Text is too long: " + text + "; tags: " + sb);
				text = text.substring(0, maxLength - 3) + "...";
			}
			sb.insert(0, text);

			statusUpdate = new StatusUpdate(sb.toString());
			if (replyToId != null) {
				statusUpdate.setInReplyToStatusId(replyToId);
			}
		}
		try {
			status = twitter.updateStatus(statusUpdate);
		} catch (TwitterException te) {
			Log.e(TAG, "Problem to update Twitter status.", te);
		}
		return status;
	}

}
