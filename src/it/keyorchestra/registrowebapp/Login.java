package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends Activity {

	Button loginButton, bCambiaRuolo;
	TextView loginMessage;
	EditText etRuoloScelto;
	Thread myThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		loginMessage = (TextView) findViewById(R.id.messageView);
		loginMessage.setText("waiting for connection...");

		loginButton = (Button) findViewById(R.id.login_button);
		loginButton.setEnabled(false);
		new FetchSQL().execute();

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

		etRuoloScelto = (EditText) findViewById(R.id.etRuoloScelto);
		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		String ruoloScelto = getPrefs.getString("ruoloList", "1");
		etRuoloScelto.setText(ruoloScelto);

		bCambiaRuolo = (Button) findViewById(R.id.bCambiaRuolo);

		bCambiaRuolo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(
						"it.keyorchestra.registrowebapp.RUOLO_UTENTE");
				startActivity(i);

			}
		});
	}

	private class FetchSQL extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {

			String retval = DatabaseOps.FetchConnection(getBaseContext());
			// loginMessage.setText(retval);
			return retval;
		}

		@Override
		protected void onPostExecute(String value) {
			SharedPreferences getPrefs = PreferenceManager
					.getDefaultSharedPreferences(getBaseContext());
			String defaultDatabase = getPrefs.getString("databaseList", "1");
			// String database = "";
			//
			// if (defaultDatabase.contentEquals("1")) {
			// database = "PostgreSQL";
			// } else if (defaultDatabase.contentEquals("2")) {
			// database = "MySQL";
			// }

			if (value.equals("1")) {
				loginMessage.setText("Connessione stabilita! con "
						+ defaultDatabase);
				loginButton.setEnabled(true);
			} else {
				loginMessage
						.setText("Connessione fallita! con " + defaultDatabase
								+ ".\n " + "Vai a scelta Database...");
				loginButton.setEnabled(false);
				// SE LA CONNESSIONE FALLISCE RIMANDA ALLE PREFERENZE PER
				// LA SCELTA DI UN'ALTRO DATABASE
				Thread timer = new Thread() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						try {
							sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {

							Intent d = new Intent(
									"it.keyorchestra.registrowebapp.DATABASE");
							startActivity(d);
						}
					}

				};
				timer.start();

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
		new FetchSQL().execute();

	}

}
