package cz.kramolis.gdd2011.android;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @author Libor Kramolis
 */
public class VerticalSlider extends Slider {

	public VerticalSlider(Context context) {
		super(context);
		initSliderView(context, true);
	}

	public VerticalSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		initSliderView(context, true);
	}

}
