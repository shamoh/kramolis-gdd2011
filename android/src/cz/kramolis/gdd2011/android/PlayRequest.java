package cz.kramolis.gdd2011.android;

import java.util.Date;

/**
 * @author Libor Kramolis
 */
public class PlayRequest {

	private static long ID_SEQUENCE = 0;

	private long id;
	private String text;
	private String author;
	private Date createdAt;
	private MusicNotation musicNotation;

	//
	// init
	//

	public PlayRequest(String text, String author, Date createdAt, MusicNotation musicNotation) {
		this.id = nextSequenceId();

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
