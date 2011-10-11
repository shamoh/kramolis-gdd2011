package cz.kramolis.gdd2011.android;

import java.util.Date;

/**
 * @author Libor Kramolis
 */
public class PlayRequest {

	private static long ID_SEQUENCE = 0;

	private long id;

	private final Long tweetId;
	private final String text;
	private final String author;
	private final Date createdAt;
	private final MusicNotation musicNotation;

	//
	// init
	//

	public PlayRequest(Long tweetId, String text, String author, Date createdAt, MusicNotation musicNotation) {
		this.id = nextSequenceId();

		this.tweetId = tweetId;
		this.text = text;
		this.author = author;
		this.createdAt = createdAt;
		this.musicNotation = musicNotation;
	}

	//
	// get/set
	//

	public long getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public String getAuthor() {
		return author;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public MusicNotation getMusicNotation() {
		return musicNotation;
	}

	//
	// static
	//

	private static long nextSequenceId() {
		ID_SEQUENCE++;
		return ID_SEQUENCE;
	}

}
