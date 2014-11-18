package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.mysqlandroid.MySqlAndroid;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Login extends Activity {

	ImageButton loginButton, bCambiaRuolo;
	TextView loginMessage;
	TextView etRuoloScelto;
	Thread myThread = null;
	ImageView imShowMenu;
	EditText etLoginEmail, etLoginPasswd;
	private SharedPreferences getPrefs;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);
		etLoginPasswd = (EditText) findViewById(R.id.etLoginPasswd);

		imShowMenu = (ImageView) findViewById(R.id.imShowMenu);
		imShowMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// "imShowMenu.OnClickListener()", Toast.LENGTH_SHORT)
				// .show();
				openOptionsMenu();
			}
		});

		loginMessage = (TextView) findViewById(R.id.messageView);
		loginMessage.setText("Waiting for connection...");

		loginButton = (ImageButton) findViewById(R.id.login_button);
		loginButton.setEnabled(false);

		loginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Check formato
				// Toast.makeText(getApplicationContext(),
				// "Email:"+etLoginEmail.getText()+" Passwd:"+etLoginPasswd.getText(),
				// Toast.LENGTH_LONG).show();

				DatabaseOps databaseOps = new DatabaseOps();
				// Controlla se le credenziali esistono
				String phpencoder = getPrefs.getString("phpencoder", null);
				if (phpencoder == null) {
					Toast.makeText(
							getApplicationContext(),
							"Login fallito! File dell'encoder php non valorizzato in menù preferenze?",
							Toast.LENGTH_LONG).show();
					return;
				}

				// Controlla le credenziali dell'utente
				boolean isAuthenticated = databaseOps.AuthenticateUser(
						getApplicationContext(), etLoginEmail.getText()
								.toString(),
						etLoginPasswd.getText().toString(),
						getDatabaseIpFromPreferences(), phpencoder);

				if (isAuthenticated) {// Credenziali esistenti
					// Controlla se l'utente è locked
					SharedPreferences sharedpreferences = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					Long is_locked = sharedpreferences.getLong("is_locked", -1);
					if (is_locked == 1) {
						Toast.makeText(
								getApplicationContext(),
								"L'utente ha già effettuato il login da un altro IP!\n"
										+ "Permesso di accesso NEGATO!",
								Toast.LENGTH_LONG).show();
						
					} else {

						// Controlla i permessi del ruolo di accesso prescelto
						// per l'utente salvato nelle preferenze
						String ruoloScelto = etRuoloScelto.getText().toString();
						Toast.makeText(
								getApplicationContext(),
								"Ora controllo se hai i permessi\n"
										+ "per il ruolo di " + ruoloScelto,
								Toast.LENGTH_LONG).show();

						if (databaseOps.LoggingUserHasRole(ruoloScelto,
								getApplicationContext(),
								getDatabaseIpFromPreferences())) {
							Toast.makeText(
									getApplicationContext(),
									"Autenticazione riuscita!\n"+
									"Permessi accordati per il ruolo di "
											+ ruoloScelto, Toast.LENGTH_LONG)
									.show();
							// Lancia un nuovo Intent per andare allo userMenu

						} else {
							Toast.makeText(
									getApplicationContext(),
									"Non hai i permessi di accesso per il ruolo di "
											+ ruoloScelto, Toast.LENGTH_LONG)
									.show();
						}
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"Credenziali invalide!", Toast.LENGTH_LONG).show();
				}
			}
		});

		etRuoloScelto = (TextView) findViewById(R.id.etRuoloScelto);

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
		setIconRuoloScelto(bCambiaRuolo, ruoloScelto);

	}

	private void setIconRuoloScelto(ImageButton bCambiaRuolo, String ruoloScelto) {
		// TODO Auto-generated method stub
		// SET ICON admin, professor, secretary, ata
		Resources res = getResources();
		if (ruoloScelto.equals("Amministratore")) {
			bCambiaRuolo.setImageDrawable(res.getDrawable(R.drawable.admin));
		} else if (ruoloScelto.equals("Professore")) {
			bCambiaRuolo
					.setImageDrawable(res.getDrawable(R.drawable.professor));
		} else if (ruoloScelto.equals("Segreteria")) {
			bCambiaRuolo
					.setImageDrawable(res.getDrawable(R.drawable.secretary));
		} else if (ruoloScelto.equals("Ata")) {
			bCambiaRuolo.setImageDrawable(res.getDrawable(R.drawable.ata));
		}
	}

	private class FetchSQL extends AsyncTask<Void, Void, String> {
		@Override
		protected String doInBackground(Void... params) {

			String retval = new DatabaseOps().FetchConnection(getBaseContext());
			return retval;
		}

		@Override
		protected void onPostExecute(String value) {

			String ip = getDatabaseIpFromPreferences();

			String defaultDatabase = getDefaultDatabaseFromPreferences();
			if (value.equals("1")) {
				loginMessage.setText("Connessione con " + defaultDatabase
						+ " stabilita! ip:" + ip);
				loginButton.setEnabled(true);
			} else {
				loginMessage.setText("Fallita connessione al Database "
						+ defaultDatabase + "!\n "
						+ "Controlla i parametri...e i server MySQL & Apache");
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

	private String getDefaultDatabaseFromPreferences() {
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		return defaultDatabase;
	}

	private String getDatabaseIpFromPreferences() {
		String defaultDatabase = getDefaultDatabaseFromPreferences();

		String ip = null;

		if (defaultDatabase.contentEquals("MySQL")) {
			ip = getPrefs.getString("ipMySQL", "");
		} else if (defaultDatabase.contentEquals("PostgreSQL")) {
			ip = getPrefs.getString("ipPostgreSQL", "");
		}
		return ip;
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
		case R.id.reconnectToMySQL:
			testConnectionToDatabase(getApplicationContext());
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

		String ruoloScelto = getPrefs.getString("ruoloList", "1");
		etRuoloScelto.setText(ruoloScelto);

		setIconRuoloScelto(bCambiaRuolo, ruoloScelto);

		testConnectionToDatabase(getApplicationContext());
	}

	private void testConnectionToDatabase(Context context) {
		loginMessage.setText("waiting for connection...");
		String defaultDatabase = getPrefs.getString("databaseList", "1");

		if (defaultDatabase.contentEquals("MySQL")) {
			new FetchSQL().execute();
		}
		if (defaultDatabase.contentEquals("PostgreSQL")) {
			Toast.makeText(context, "NOT YET IMPLEMENTED!", Toast.LENGTH_LONG)
					.show();
		}

	}
}
