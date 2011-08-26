package cz.kramolis.gdd2011.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
//import winterwell.jtwitter.Twitter;

public class StatusActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "StatusActivity";
	private EditText editText;
	private Button updateButton;
//	private Twitter twitter; //??? Sehnat knihovnu

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.status);

		// Find views
		editText = (EditText) findViewById(R.id.editText);
		updateButton = (Button) findViewById(R.id.buttonUpdate);

		updateButton.setOnClickListener(this);

//		twitter = new Twitter("student", "password");
//		twitter.setAPIRootUrl("http://gdd2011.kramolis.cz/api");
	}

	@Override
	public void onClick(View view) {
//		twitter.setStatus(editText.getText().toString());
		Log.d(TAG, "onCLicked");
	}

}
