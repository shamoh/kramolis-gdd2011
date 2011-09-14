package cz.kramolis.gdd2011.android;

import android.util.Log;
import twitter4j.*;

import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Ondrej Kosatka
 */
public class TwitterAdapter {

	private static final String TAG = "TwitterAdapter";

//	private static final String PROTECTED_RESOURCE_URL = "http://api.twitter.com/1/account/verify_credentials.xml";
//	private static final String API_KEY = "Y4t32ApId1x1eLJHLMkng";
//	private static final String API_SECRET = "IttsA332uUZaSsnGrpf7QyAXSHd35FdfxyLuvydbM8";

	long lastId = -1;


	public List<Tweet> search(String param) {
		Twitter twitter = new TwitterFactory().getInstance();
		try {
			Query query = new Query(param);
			if (lastId != -1) {
				query.setSinceId(lastId);
			}
			QueryResult result = twitter.search(query);
			lastId = result.getMaxId();
			List<Tweet> tweets = result.getTweets();
			return tweets;
		} catch (TwitterException te) {
			Log.d(TAG, "ERROR", te);
		}
		return null;
	}

}
