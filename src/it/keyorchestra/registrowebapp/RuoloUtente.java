package it.keyorchestra.registrowebapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class RuoloUtente extends PreferenceActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ruolo_utente);
	}

}
