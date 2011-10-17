package cz.kramolis.gdd2011.android;

/**
 * @author Libor Kramolis
 */
public class Temp {

	public static void main(String[] args) {
		int top = 157 - 11;
		int value = 10;
		int diff = 9;
		String notation = "cCdDefFgGabh";
		for (char ch : notation.toCharArray()) {

			System.out.format("    pumpMap1['%s'] = %s;\n", ch, top + value);
			System.out.format("    pumpMap2['%s'] = %s;\n", ch, top + value);
			value += diff;
		}
	}

}
