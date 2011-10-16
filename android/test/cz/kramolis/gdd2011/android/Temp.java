package cz.kramolis.gdd2011.android;

/**
 * @author Libor Kramolis
 */
public class Temp {

	public static void main(String[] args) {
		int value = 10;
		int diff = 8;
		String notation = "cCdDefFgGabh";
		for (char ch : notation.toCharArray()) {

			System.out.format("    pumpMap1['%s'] = %s;\n", ch, 157 + value);
			System.out.format("    pumpMap2['%s'] = %s;\n", ch, 157 + value);
			value += diff;
		}
	}

}
