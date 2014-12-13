package com.github.mcfeod.robots4droid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class SettingsActivity extends PreferenceActivity{
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;
	private boolean mErrorHeight = false, mErrorWidth = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		mEdit = mPref.edit();
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
		
		EditTextPreference width = (EditTextPreference)findPreference("mx_width");
		EditTextPreference height = (EditTextPreference)findPreference("mx_height");
		width.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String s = newValue.toString();
				if (isValid(checkInput(s))){
					preference.setSummary(s);
					mErrorWidth = false;
					mEdit.putInt("width", Integer.decode(s));
					//ВАЖНО! интовая настройка называется width, строковая - mx_width
				}else{
					mErrorWidth = true;
					preference.setSummary(R.string.error);
				}
				return true;
			}
		});
		width.setSummary(width.getText());
		height.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				String s = newValue.toString();
				if (isValid(checkInput(s))){
					preference.setSummary(s);
					mErrorHeight = false;
					mEdit.putInt("height", Integer.decode(s));
					//ВАЖНО! интовая настройка называется height, строковая - mx_height
				}else{
					mErrorHeight = true;
					preference.setSummary(R.string.error);
				}
				return true;
			}
		});
		height.setSummary(height.getText());
	}
	
	private boolean isValid(int x){
		switch (x){
		case -1:
			Toast.makeText(SettingsActivity.this, getString(R.string.cast_error_msg), Toast.LENGTH_SHORT).show();
			return false;
		case -2:
			Toast.makeText(SettingsActivity.this, getString(R.string.val_error_msg), Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
	{
		super.onPreferenceTreeClick(preferenceScreen, preference);
		if (preference!=null)
			if (preference instanceof PreferenceScreen)
				if (((PreferenceScreen)preference).getDialog()!=null)
					((PreferenceScreen)preference).getDialog().getWindow()
						.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
								WindowManager.LayoutParams.FLAG_FULLSCREEN);
		return false;
	}
	
	private int checkInput(String inp){
		int x;
		try{
			x = Integer.decode(inp);
		}catch (NumberFormatException e){
			return -1; //код ошибки для isValid
		}
		if ((x<15)||(x>80)){
			return -2;
		}
		return x;
	}
	
	@Override
	public void onBackPressed(){
		if (mErrorWidth || mErrorHeight)
			Toast.makeText(SettingsActivity.this, getString(R.string.fix_error_msg) , Toast.LENGTH_SHORT).show();
		else{
			mEdit.commit();
			super.onBackPressed();
		}
	}
}
