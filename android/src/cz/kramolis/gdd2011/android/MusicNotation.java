package cz.kramolis.gdd2011.android;

/**
 * Allowed note characters are {@code cCdDefFgGabh}, Use number 2 after character to distinguish the second octave.
 * Use character {@code |} (pipe) as rest (pause).
 *
 * @author Libor Kramolis
 */
public class MusicNotation {

	private static final String TAG = "LaPardon.MusicNotation";

	private static final String SUPPORTED_CHARS = "cCdDefFgGabh";
	private static final String SUPPORTED_NUMBERS = "12";
	private static final char REST_CHAR = '|';

	private final String notation;

	private MusicNotation(String notation) {
		this.notation = notation;
	}

	//
	// get
	//

	public String getNotation() {
		return notation;
	}

	//
	// utils
	//

	/**
	 * Lookups for music notation in {@param text}. Notation is bordered by brackets ({@code [} and {@code ]}).
	 * If there are no brackets it returns {@code null}. Otherwise it returns text between brackets with following modifications:
	 * <ul>
	 * <li>all white spaces are removed</li>
	 * </ul>
	 * If there is no text between brackets it returns empty string.
	 *
	 * @param text
	 * @return mentioned music notation in {@param text}
	 * @throws IllegalArgumentException if there are brackets with wrong order or if notation beaks syntax.
	 */
	public static MusicNotation lookup(String text) throws IllegalArgumentException {
		if (text == null) {
			throw new IllegalArgumentException("wrong format: text is mandatory");
		}

		int startIndex = text.indexOf('[');
		int endIndex = text.indexOf(']');
		{
			if ((startIndex == -1) && (endIndex == -1)) {
				return null;
			}
			if (startIndex > endIndex) {
				throw new IllegalArgumentException("wrong format: order of brackets.");
			}
			if (startIndex == -1) {
				throw new IllegalArgumentException("wrong format: start bracket is missing.");
			}
			if (endIndex == -1) {
				throw new IllegalArgumentException("wrong format: end bracket is missing.");
			}
		}

		text = text.substring(startIndex + 1, endIndex).trim();
		{
			text = text.replaceAll("\\s", "");
//			Log.d(TAG, "Clean text: " + text);
		}
		StringBuilder sb = new StringBuilder();
		{
			boolean charLast = false;
			for (int i = 0; i < text.length(); i++) {
				char ch = text.charAt(i);

				if ((REST_CHAR == ch) || (SUPPORTED_CHARS.indexOf(ch) != -1)) {
					if (charLast) {
						sb.append('1');
					}
					sb.append(ch);
					if (i + 1 == text.length()) {
						sb.append('1');
					}
					charLast = true;
				} else if ((SUPPORTED_NUMBERS.indexOf(ch) != -1) && charLast) {
					sb.append(ch);
					charLast = false;
				} else {
					throw new IllegalArgumentException("illegal character: '" + ch + "'.");
				}
			}
		}
		MusicNotation musicNotation = new MusicNotation(sb.toString());

		return musicNotation;
	}

}
