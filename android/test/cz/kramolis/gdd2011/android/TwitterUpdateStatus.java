package cz.kramolis.gdd2011.android;

import twitter4j.*;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TwitterUpdateStatus {

	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	public static void main(String args[]) throws Exception {
		String param = "#lapardon";
		long lastId = 123132182104514560L;//-1;

		Twitter twitter = new TwitterFactory().getInstance();
		Query query = new Query(param);
		query.setSinceId(lastId);
		query.setSince(DATE_FORMAT.format(new Date()));
		QueryResult result = twitter.search(query);
		for (Tweet tweet : result.getTweets()) {
			System.out.format("%s @%13s %s\n", tweet.getCreatedAt(), tweet.getFromUser(), tweet.getId());
		}
	}

	public static void main_updateStatus2(String args[]) throws Exception {
		Twitter twitter = new TwitterFactory().getInstance();
		Status status = twitter.updateStatus(args[0] + " at " + new Date());
		System.out.println("Successfully updated the status to [" + status.getText() + "].");
	}

	/**
	 * Usage: java twitter4j.examples.tweets.UpdateStatus [text]
	 *
	 * @param args message
	 */
	public static void main_updateStatus1(String[] args) {
		if (args.length < 1) {
			System.out.println("Usage: java cz.kramolis.gdd2011.android.TwitterUpdateStatus [text]");
			System.exit(-1);
		}
		try {
			Twitter twitter = new TwitterFactory().getInstance();
			try {
				// get request token.
				// this will throw IllegalStateException if access token is already available
				RequestToken requestToken = twitter.getOAuthRequestToken();
				System.out.println("Got request token.");
				System.out.println("Request token: " + requestToken.getToken());
				System.out.println("Request token secret: " + requestToken.getTokenSecret());
				AccessToken accessToken = null;

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				while (null == accessToken) {
					System.out.println("Open the following URL and grant access to your account:");
					System.out.println(requestToken.getAuthorizationURL());
					System.out.print("Enter the PIN(if available) and hit enter after you granted access.[PIN]:");
					String pin = br.readLine();
					try {
						if (pin.length() > 0) {
							accessToken = twitter.getOAuthAccessToken(requestToken, pin);
						} else {
							accessToken = twitter.getOAuthAccessToken(requestToken);
						}
					} catch (TwitterException te) {
						if (401 == te.getStatusCode()) {
							System.out.println("Unable to get the access token.");
						} else {
							te.printStackTrace();
						}
					}
				}
				System.out.println("Got access token.");
				System.out.println("Access token: " + accessToken.getToken());
				System.out.println("Access token secret: " + accessToken.getTokenSecret());
			} catch (IllegalStateException ie) {
				// access token is already available, or consumer key/secret is not set.
				if (!twitter.getAuthorization().isEnabled()) {
					System.out.println("OAuth consumer key/secret is not set.");
					System.exit(-1);
				}
			}
			Status status = twitter.updateStatus(args[0]);
			System.out.println("Successfully updated the status to [" + status.getText() + "].");
			System.exit(0);
		} catch (TwitterException te) {
			te.printStackTrace();
			System.out.println("Failed to get timeline: " + te.getMessage());
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.out.println("Failed to read the system input.");
			System.exit(-1);
		}
	}

}
