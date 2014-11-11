package it.keyorchestra.registrowebapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class FieldTypePrefs extends PreferenceActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.field_type_pref);
	}
}
