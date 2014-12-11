package com.github.mcfeod.robots4droid;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class SettingsActivity extends PreferenceActivity{
	private SharedPreferences mPref;
	private SharedPreferences.Editor mEdit;
	private boolean mErrorHeight = false, mErrorWidth = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
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
		
		EditListener editLstnr = new EditListener();
		EditTextPreference width = (EditTextPreference)findPreference("mx_width");
		EditTextPreference height = (EditTextPreference)findPreference("mx_height");
		width.setOnPreferenceChangeListener(editLstnr);
		width.setSummary(width.getText());
		height.setOnPreferenceChangeListener(editLstnr);
		height.setSummary(height.getText());
	}
	
	/*обработка изменений в текстовых полях*/
	private class EditListener implements Preference.OnPreferenceChangeListener{
		@Override
		public boolean onPreferenceChange(Preference preference, Object newValue){
			String s = newValue.toString();
			if (isValid(checkInput(s))){
				preference.setSummary(s);
				switch(preference.getKey()){
				case "mx_width":
					mErrorWidth = false;
					mEdit.putInt("width", Integer.decode(s));
					break;
				case "mx_height":
					mErrorHeight = false;
					mEdit.putInt("height", Integer.decode(s));
					break;
				}
				//ВАЖНО! интовая настройка называется width(height), строковая - mx_width(mx_height)
			}else{
				switch(preference.getKey()){
					case "mx_width":
						mErrorWidth = true;
						break;
					case "mx_height":
						mErrorHeight = true;
						break;
				}
				preference.setSummary("ERROR");
			}
			return true;
		}
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
	
	private int checkInput(String inp){
		int x;
		try{
			x = Integer.decode(inp);
		}catch (NumberFormatException e){
			return -1; //код ошибки для isValid
		}
		if (x<10){
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
