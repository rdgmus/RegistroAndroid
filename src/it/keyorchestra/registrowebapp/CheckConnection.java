package it.keyorchestra.registrowebapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class CheckConnection extends PreferenceActivity {
//implements
//		OnSharedPreferenceChangeListener {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.check_connection);

	}

//	@Override
//	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
//			String key) {
//		// TODO Auto-generated method stub
//		if (key.equals("checkConnection")) {
//			Boolean checkConnection = sharedPreferences.getBoolean(
//					"checkConnection", false);
//			if (checkConnection) {
//				Dialog d = new Dialog(getBaseContext(), DEFAULT_KEYS_DIALER);
//				d.show();
//			}
//		}
//	}

}
