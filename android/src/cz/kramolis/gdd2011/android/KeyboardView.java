package cz.kramolis.gdd2011.android;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Libor Kramolis
 */
public class KeyboardView extends View {

	private static final String TAG = "KeyboardView";

	public KeyboardView(Context context) {
		super(context);

		init();
	}

	public KeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		init();
	}


	private void init() {
		setFocusable(true);
		setFocusableInTouchMode(true);

		setOnTouchListener(new MyListener());
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint background = new Paint();
		background.setARGB(128, 200, 100, 255);
		canvas.drawRect(0, 0, getWidth(), getHeight(), background);
	}

	//
	// class MyListener
	//

	private class MyListener implements OnTouchListener {

		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			Log.d(TAG, "On TOUCH: [" + view + "] : " + motionEvent);

			return onTouchEvent(motionEvent);
		}
	} // class MyListener

}
