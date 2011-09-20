package cz.kramolis.gdd2011.android;

import android.view.ViewGroup;

/**
 * @author Libor Kramolis
 */
public class SimulateController extends AccessoryController {

	SimulateController(MainActivity hostActivity) {
		super(hostActivity);
	}

	protected void onAccesssoryAttached() {
		setupSliderController(R.id.slider1);
	}

	private void setupSliderController(int viewId) {
		SliderController sliderController = new SliderController(mHostActivity, getResources());
		sliderController.attachToView((ViewGroup) findViewById(viewId));
	}

}
