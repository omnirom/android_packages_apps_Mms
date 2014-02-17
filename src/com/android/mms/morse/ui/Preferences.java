package com.android.mms.morse.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

import com.android.mms.R;
import com.android.mms.morse.transaction.EventReceiver;

@SuppressWarnings("deprecation")
public class Preferences extends PreferenceActivity {
	
	private CheckBoxPreference      mCheckBoxEnabled;
	private ListPreference          mVibratePart;
	private CheckBoxPreference      mVibrateCounts;
	private SBP mDotLength;
	private EditTextPreference      mTestText;
	private CheckBoxPreference      mScreenOffOnly;
	private CheckBoxPreference      mActiveNormal;
	private CheckBoxPreference      mActiveVibrate;
	private CheckBoxPreference      mActiveSilent;
	
	private OnPreferenceChangeListener mVibratePartListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) { 
			preference.setSummary(getString(R.string.preference_vibrate_parts_summary) + " " + getResources().getStringArray(R.array.preference_vibrate_parts_entries)[Integer.parseInt((String)newValue)]);
			return true;
		}
	};
	private OnPreferenceChangeListener mTestTextListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			Intent intent = new Intent(Preferences.this, EventReceiver.class);
			intent.setAction(EventReceiver.VIBRATE_IN_MORSE);
			intent.putExtra(EventReceiver.VIBRATE_IN_MORSE_KEY, (String)newValue);
			sendBroadcast(intent);
			return true;
		}
	};
	private OnPreferenceChangeListener mEnabledListener = new OnPreferenceChangeListener() {
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			boolean state = (Boolean)arg1;
			
			mVibratePart.setEnabled(state);
			mVibrateCounts.setEnabled(state);
			mDotLength.setEnabled(state);
			mTestText.setEnabled(state);
			mScreenOffOnly.setEnabled(state);
			mActiveNormal.setEnabled(state);
			mActiveVibrate.setEnabled(state);
			mActiveSilent.setEnabled(state);
			
			return true;
		}
	};

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		loadPreferences();
	}
	
	private void loadPreferences() {
		addPreferencesFromResource(R.xml.preferences_morse);
		final PreferenceScreen screen = getPreferenceScreen();
		
		mCheckBoxEnabled = (CheckBoxPreference)screen.findPreference(getString(R.string.preference_enabled));
		mVibratePart     = (ListPreference)screen.findPreference(getString(R.string.preference_vibrate_parts));
		mTestText        = (EditTextPreference)screen.findPreference(getString(R.string.preference_test));
		mVibrateCounts   = (CheckBoxPreference)screen.findPreference(getString(R.string.preference_vibrate_counts));
		mDotLength       = (SBP)screen.findPreference(getString(R.string.preference_dot_length));
		
		mCheckBoxEnabled.setOnPreferenceChangeListener(mEnabledListener);
		mVibratePart.setOnPreferenceChangeListener(mVibratePartListener);
		//Trigger summary update
		mVibratePartListener.onPreferenceChange(mVibratePart, mVibratePart.getValue());
		mTestText.setOnPreferenceChangeListener(mTestTextListener);
		
		mScreenOffOnly = (CheckBoxPreference)screen.findPreference(getString(R.string.preference_screen_off_only));
		mActiveNormal  = (CheckBoxPreference)screen.findPreference(getString(R.string.preference_vibrate_normal));
		mActiveVibrate = (CheckBoxPreference)screen.findPreference(getString(R.string.preference_vibrate_vibrate));
		mActiveSilent  = (CheckBoxPreference)screen.findPreference(getString(R.string.preference_vibrate_silent));
		
	}
}