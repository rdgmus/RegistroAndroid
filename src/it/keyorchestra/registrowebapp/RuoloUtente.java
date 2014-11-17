package it.keyorchestra.registrowebapp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class RuoloUtente extends PreferenceActivity {

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.ruolo_utente);

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		String ruoloScelto = getPrefs.getString("ruoloList", "Professore");

		Toast.makeText(getApplicationContext(), "ruoloScelto = " + ruoloScelto,
				Toast.LENGTH_LONG).show();

		// Aggiorna il ruolo selezionato
		Preference pref = findPreference("ruoloScelto");
		pref.setSummary(ruoloScelto);
		pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"onPreferenceClick = ruoloScelto", Toast.LENGTH_LONG)
						.show();

				
				startActionMode(mActionModeCallback);

				return true;
			}

		});

		setIconRuoloScelto(pref, ruoloScelto);

		// Aggiunge un listener per la selezione dei ruoli
		pref = findPreference("ruoloList");
		pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(Preference preference,
					Object newValue) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"onPreferenceChange = " + newValue, Toast.LENGTH_LONG)
						.show();
				Preference pref = findPreference("ruoloScelto");
				pref.setSummary((CharSequence) newValue);
				setIconRuoloScelto(pref, newValue.toString());
				
				return true;
			}

		});
	}

	@SuppressLint("NewApi")
	private void setIconRuoloScelto(Preference pref, String ruoloScelto) {
		// TODO Auto-generated method stub
		// SET ICON admin, professor, secretary, ata
		Resources res = getResources();
		if (ruoloScelto.equals("Amministratore")) {
			pref.setIcon(res.getDrawable(R.drawable.admin));
		} else if (ruoloScelto.equals("Professore")) {
			pref.setIcon(res.getDrawable(R.drawable.professor));
		} else if (ruoloScelto.equals("Segreteria")) {
			pref.setIcon(res.getDrawable(R.drawable.secretary));
		} else if (ruoloScelto.equals("Ata")) {
			pref.setIcon(res.getDrawable(R.drawable.ata));
		}
	}

	/******************************************************/
	/** Implementazione di ActionMode callback Interface **/
	/******************************************************/

	@SuppressLint("NewApi")
	private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "onCreateActionMode",
					Toast.LENGTH_LONG).show();
			// Inflate a menu resource providing context menu items
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.main, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "onPrepareActionMode",
					Toast.LENGTH_LONG).show();
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "onActionItemClicked",
					Toast.LENGTH_LONG).show();
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), "onDestroyActionMode",
					Toast.LENGTH_LONG).show();
//			mActionModeCallback = null;
		}

	};
}
