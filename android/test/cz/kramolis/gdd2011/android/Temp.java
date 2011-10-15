package cz.kramolis.gdd2011.android;

/**
 * @author Libor Kramolis
 */
public class Temp {

	public static void main(String[] args) {
		int xxx = 10;
		int diff = 10;
		String notation = "cCdDefFgGabh";
		for (char ch : notation.toCharArray()) {
//    pumpMap1['c'] = x;
//    pumpMap2['c'] = x;

			System.out.format("    pumpMap1['%s'] = %s;\n", ch, xxx);
			System.out.format("    pumpMap2['%s'] = %s;\n", ch, 120 + xxx);
			xxx += diff;
		}
	}

}
