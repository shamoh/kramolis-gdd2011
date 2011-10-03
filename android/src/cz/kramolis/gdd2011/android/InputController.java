package cz.kramolis.gdd2011.android;

/**
 * @author Libor Kramolis
 * @todo
 */
public class InputController extends AccessoryController {

//	private TextView mTemperature;
//	private TextView mLightView;
//	private TextView mLightRawView;
//	private JoystickView mJoystickView;
//	ArrayList<SwitchDisplayer> mSwitchDisplayers;
//	private final DecimalFormat mLightValueFormatter = new DecimalFormat("##.#");
//	private final DecimalFormat mTemperatureFormatter = new DecimalFormat("###" + (char)0x00B0);

	InputController(LaPardonActivity hostActivity) {
		super(hostActivity);
//		mTemperature = (TextView) findViewById(R.id.tempValue);
//		mLightView = (TextView) findViewById(R.id.lightPercentValue);
//		mLightRawView = (TextView) findViewById(R.id.lightRawValue);
//		mJoystickView = (JoystickView) findViewById(R.id.joystickView);
	}

	protected void onAccesssoryAttached() {
//		mSwitchDisplayers = new ArrayList<SwitchDisplayer>();
//		for (int i = 0; i < 4; ++i) {
//			SwitchDisplayer sd = new SwitchDisplayer(i);
//			mSwitchDisplayers.add(sd);
//		}
	}

}
