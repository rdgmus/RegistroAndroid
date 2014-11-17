package it.keyorchestra.registrowebapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class LoggingUserPrefs extends PreferenceActivity {
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.logging_user_prefs);
		
		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());		
	
		Preference pref = findPreference("id_utente");
		pref.setDefaultValue("46");
		
		getPrefs.getString("id_utente",null);
	}
}
