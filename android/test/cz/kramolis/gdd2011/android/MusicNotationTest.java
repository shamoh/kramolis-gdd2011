package cz.kramolis.gdd2011.android;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author Libor Kramolis
 */
public class MusicNotationTest {

	@Test
	public void testLookup() throws Exception {
		{
			testLookupImpl(null, "");
			testLookupImpl(null, " aaaaaaaa ");
			testLookupImpl(null, " cdefgab|cdefgab ");

			testLookupImpl("", "[]");
			testLookupImpl("", "[	]");
			testLookupImpl("", "[ ]");
			testLookupImpl("", "  []	");
			testLookupImpl("", "  #abc  []");
			testLookupImpl("", " [] #abc  ");
			testLookupImpl("", "  #abc [] ");
			testLookupImpl("c1", "[c]");
			testLookupImpl("c1", "[c1]");
//			testLookupImpl("c2", "[c2]");
			testLookupImpl("c1c1", "[cc]");
//			testLookupImpl("c1c1c1c2c1c1c2", "[cc1cc2ccc2]");
			testLookupImpl("d1d1d1", "  #abc	[d1dd]	#opq  ");
			testLookupImpl("e1e1e1", "  [ee1e] #abc	#opq  #xyz	");
			testLookupImpl("f1f1f1", " [fff1] [ggg] #lapardon ");
			testLookupImpl("a1a1a1|1b1b1b1", "[a a a |	b	b	b ]");
			testLookupImpl("c1C1d1D1e1f1F1g1G1a1b1h1", "[cCdDefFgGabh]");
			testLookupImpl("c1C1d1D1e1f1F1g1G1a1b1h1", "[c1C1d1D1e1f1F1g1G1a1b1h1]");
//			testLookupImpl("c2C2d2D2e2f2F2g2G2a2b2h2", "[c2C2d2D2e2f2F2g2G2a2b2h2]");
//			testLookupImpl("|1c1|1C1|1d1|1D1|1e1|1f1|1F1|1g1|1G1|1a1|1b1|1h1|1|1c2|1C2|1d2|1D2|1e2|1f2|1F2|1g2|1G2|1a2|1b2|1h2|1|1",
//					"[|c|C|d|D|e|f|F|g|G|a|b|h||c2|C2|d2|D2|e2|f2|F2|g2|G2|a2|b2|h2||]");
		}
		{
			testLookupThrowsException(null);
			testLookupThrowsException("[");
			testLookupThrowsException("]");
			testLookupThrowsException("][");
			testLookupThrowsException(" aaa ] bbb [ ccc ");

			testLookupThrowsException("[1]");
			testLookupThrowsException("[2]");
			testLookupThrowsException("[z]");
			testLookupThrowsException("[c3]");
		}
	}

	private void testLookupImpl(String expected, String text) {
		MusicNotation musicNotation = MusicNotation.lookup(text);
		if (expected == null) {
			Assert.assertNull(musicNotation);
		} else {
			Assert.assertEquals(expected, musicNotation.getNotation());
		}
	}

	private void testLookupThrowsException(String text) {
		try {
			MusicNotation.lookup(text);
			Assert.fail("Must fail: " + text);
		} catch (IllegalArgumentException ex) {
			//OK
		}
	}

}
