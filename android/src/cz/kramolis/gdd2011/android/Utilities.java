package cz.kramolis.gdd2011.android;

import android.graphics.drawable.Drawable;

/**
 * @author Libor Kramolis
 */
public class Utilities {

	static final byte[] HEXES = "0123456789ABCDEF".getBytes();

	public static void main(String[] args) {
		System.out.println(getHex(true, (byte) 0));
		System.out.println(getHex(true, (byte) 1));
		System.out.println(getHex(true, (byte) 15));
		System.out.println(getHex(true, (byte) 16));
		System.out.println(getHex(true, (byte) 17));
		System.out.println(getHex(true, (byte) 127));
		System.out.println(getHex(true, (byte) 128));
		System.out.println(getHex(true, (byte) 129));
		System.out.println(getHex(true, (byte) 254));
		System.out.println(getHex(true, (byte) 255));
	}

	public static String getHex(final boolean appendSpace, final byte... raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(3 * raw.length);
		for (final byte b : raw) {
//			hex.append(b).append(':').append(b >> 4).append('/').append((b >> 4) & 0xF).append('=').append((char)HEXES[(b >> 4) & 0xF]).
//					append('|').append((char)HEXES[(b) & 0xF]);
			hex.append((char) HEXES[(b >> 4) & 0xF]).append((char) HEXES[(b) & 0xF]);
			if (appendSpace) {
				hex.append(' ');
			}
		}
		return hex.toString();
	}

	static void centerAround(int x, int y, Drawable d) {
		int w = d.getIntrinsicWidth();
		int h = d.getIntrinsicHeight();
		int left = x - w / 2;
		int top = y - h / 2;
		int right = left + w;
		int bottom = top + h;
		d.setBounds(left, top, right, bottom);
	}

}
