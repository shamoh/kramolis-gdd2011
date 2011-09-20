package cz.kramolis.gdd2011.android;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Libor Kramolis
 */
public class SliderController {

	private Drawable sliderDrawable;
	private MainActivity mActivity;

	public SliderController(MainActivity activity, Resources res) {
		mActivity = activity;

		sliderDrawable = res.getDrawable(R.drawable.scrubber_horizontal_red_holo_dark);
	}

	public void attachToView(ViewGroup targetView) {
			ViewGroup g = (ViewGroup) targetView.getChildAt(0);
			TextView label = (TextView) g.getChildAt(0);
			Slider slider = (Slider) g.getChildAt(1);
			TextView valueText = (TextView) g.getChildAt(2);
			Slider.SliderPositionListener positionListener = new SliderValueUpdater(valueText);
			slider.setPositionListener(positionListener);
			LabelClickListener leftLabelListener = new LabelClickListener(
					slider, 0);
			label.setOnClickListener(leftLabelListener);
			LabelClickListener rightLabelListener = new LabelClickListener(
					slider, 1);
			valueText.setOnClickListener(rightLabelListener);
			valueText.setText("0");

			label.setText("Water");
			slider.setSliderBackground(sliderDrawable);
	}

	//
	// class SliderValueUpdater
	//

	class SliderValueUpdater implements Slider.SliderPositionListener {
		private TextView mTarget;

		SliderValueUpdater(TextView target) {
			mTarget = target;
		}

		public void onPositionChange(double value) {
			int v = (int) (255 * value);
			mTarget.setText(String.valueOf(v));
			if (mActivity != null) {
				mActivity.sendCommand(MainActivity.SIMULATE_COMMAND, (byte) v);
			}
		}

	} // class SliderValueUpdater

	//
	// class LabelClickListener
	//

	class LabelClickListener implements OnClickListener {
		final private double mValue;
		private final Slider mSlider;

		public LabelClickListener(Slider slider, double value) {
			mSlider = slider;
			mValue = value;
		}

		public void onClick(View v) {
			mSlider.setPosition(mValue);
		}

	} // class LabelClickListener

}
