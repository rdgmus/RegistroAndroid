package it.keyorchestra.registrowebapp;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.RadioGroup;

public class Database extends PreferenceActivity {

	String setData = "myDatabase";
	RadioGroup databaseList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.database);
	}

}
