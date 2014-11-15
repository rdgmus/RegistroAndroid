package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.mysqlandroid.MySqlAndroid;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Login extends Activity {

	ImageButton loginButton, bCambiaRuolo;
	TextView loginMessage;
	TextView etRuoloScelto;
	Thread myThread = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginMessage = (TextView) findViewById(R.id.messageView);
		loginMessage.setText("Waiting for connection...");

		loginButton = (ImageButton) findViewById(R.id.login_button);
		loginButton.setEnabled(false);

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Boolean check = false;
				// Check formati

				// Controlla se le credenziali esistono

				if (check) {
					// Va alla pagina principale

				} else {
					loginMessage.setText("Credenziali invalide!");
				}
			}
		});

		etRuoloScelto = (TextView) findViewById(R.id.etRuoloScelto);
		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String ruoloScelto = getPrefs.getString("ruoloList", "Professore");
		etRuoloScelto.setText(ruoloScelto);

		bCambiaRuolo = (ImageButton) findViewById(R.id.bCambiaRuolo);

		bCambiaRuolo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						"it.keyorchestra.registrowebapp.RUOLO_UTENTE");
				startActivity(i);

			}
		});

		ArrayList<String> results = new MySqlAndroid()
				.mysqlAndroidTest(getApplicationContext());
		if (results == null || results.size() == 0) {
			Toast.makeText(getApplicationContext(),
					"Nessuna risposta dal server MySQL! ", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "Mysql<=>Android Test ok!",
					Toast.LENGTH_LONG).show();
		}
		new FetchSQL().execute();

	}

	private class FetchSQL extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {

			String retval = DatabaseOps.FetchConnection(getBaseContext());
			return retval;
		}

		@Override
		protected void onPostExecute(String value) {
			SharedPreferences getPrefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			String defaultDatabase = getPrefs.getString("databaseList", "1");

			if (value.equals("1")) {
				loginMessage.setText("Connessione con " + defaultDatabase
						+ " stabilita! ");
				loginButton.setEnabled(true);
			} else {
				loginMessage
						.setText("Fallita connessione al Database "
								+ defaultDatabase + "!\n "
								+ "Controlla i parametri...");
				openOptionsMenu();
				loginButton.setEnabled(false);
				// SE LA CONNESSIONE FALLISCE RIMANDA ALLE PREFERENZE PER
				// LA SCELTA DI UN'ALTRO DATABASE
				// Thread timer = new Thread() {
				//
				// @Override
				// public void run() {
				// // TODO Auto-generated method stub
				//
				// try {
				// sleep(2000);
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// } finally {
				//
				// Intent d = new Intent(
				// "it.keyorchestra.registrowebapp.DATABASE");
				// startActivity(d);
				// }
				// }
				//
				// };
				// timer.start();

			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.ruoloUtente:
			Intent i = new Intent("it.keyorchestra.registrowebapp.RUOLO_UTENTE");
			startActivity(i);
			break;
		case R.id.databases:
			Intent d = new Intent("it.keyorchestra.registrowebapp.DATABASE");
			startActivity(d);
			break;
		default:
			break;
		}

		return false;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String ruoloScelto = getPrefs.getString("ruoloList", "1");
		etRuoloScelto.setText(ruoloScelto);

		loginMessage.setText("waiting for connection...");

		ArrayList<String> results = new MySqlAndroid()
				.mysqlAndroidTest(getApplicationContext());
		if (results == null || results.size() == 0) {
			Toast.makeText(getApplicationContext(),
					"Nessuna risposta dal server! ", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "Mysql<=>Android Test ok!",
					Toast.LENGTH_LONG).show();
		}
		new FetchSQL().execute();

	}

}
