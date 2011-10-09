package cz.kramolis.gdd2011.android;

import android.util.Log;
import twitter4j.*;

import java.util.Date;
import java.util.List;

/**
 * @author Ondrej Kosatka
 */
public class TwitterAdapter {

	private static final String TAG = "TwitterAdapter";

	long lastId = -1;

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

	public List<Tweet> search(LaPardonApplication application, String param) {
		try {
			Query query = new Query(param);
			if (lastId != -1) {
				query.setSinceId(lastId);
			}
			QueryResult result = twitter.search(query);
			lastId = result.getMaxId();
			List<Tweet> tweets = result.getTweets();
			int readCount = tweets.size();

			if (readCount > 0) {
				updateStatusSearch(application, readCount);
			}

			return tweets;
		} catch (TwitterException te) {
			Log.e(TAG, "ERROR", te);
		}
		return null;
	}

	private void updateStatusSearch(LaPardonApplication application, int readCount) {//, int okRequests} {
		String searchMessage = String.format("Just read %s new tweets at %tT.", readCount, new Date());
		Status status = updateStatus(searchMessage, application.getPrefInfoHashtag());
		Log.d(TAG, "Successfully updated the status to [" + status.getText() + "].");
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
