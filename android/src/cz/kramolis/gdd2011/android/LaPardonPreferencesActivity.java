package cz.kramolis.gdd2011.android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Ondrej Kosatka
 */
public class LaPardonPreferencesActivity extends PreferenceActivity {

	TwitterService twitterService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(R.string.preferences);

		addPreferencesFromResource(R.xml.lapardonpreferences);

//		SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
	}

}
