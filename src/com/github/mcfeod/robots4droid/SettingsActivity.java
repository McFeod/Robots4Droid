package com.github.mcfeod.robots4droid;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.Window;
import android.view.WindowManager;

public class SettingsActivity extends PreferenceActivity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		final ListPreference mComplexityList = (ListPreference) findPreference("complexity");
		
		/* вместо summary - выбранное значение */
		mComplexityList.setSummary(mComplexityList.getEntry());
		mComplexityList.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object value) {
				int index = mComplexityList.findIndexOfValue(value.toString());
				preference.setSummary(index >= 0 ? mComplexityList.getEntries()[index]: null);
				return true;
			}
		});
		
	}
}
