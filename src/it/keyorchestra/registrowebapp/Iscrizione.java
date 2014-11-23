package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;
import it.keyorchestra.registrowebapp.scuola.util.GMailSender;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Iscrizione extends Activity implements ActivitiesCommonFunctions {

	ImageButton registraButton, pulisciButton, ibGotoLogin, ibHome;
	EditText nome, cognome, email, passwd, repeatPasswd;
	private SharedPreferences getPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_register_user);

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		ibHome = (ImageButton) findViewById(R.id.ibHome);
		ibHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Va alla pagina di login
				Toast.makeText(
						getApplicationContext(),
						"Re-indirizzamento a pagina iniziale in corso...",
						Toast.LENGTH_SHORT).show();

				Intent ourStartingPoint = new Intent(
						"it.keyorchestra.registrowebapp.LIST_ACTIVITY");
				startActivity(ourStartingPoint);
				finish();
			}

		});
		registerToolTipFor(ibHome);

		ibGotoLogin = (ImageButton) findViewById(R.id.ibGotoLogin);
		registerToolTipFor(ibGotoLogin);
		ibGotoLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Va alla pagina di login
				Toast.makeText(
						getApplicationContext(),
						"Re-indirizzamento a pagina di Login in corso...",
						Toast.LENGTH_SHORT).show();

				Intent loginUserActivity = new Intent(
						"android.intent.action.LOGIN");
				startActivity(loginUserActivity);
				finish();
			}

		});

		registraButton = (ImageButton) findViewById(R.id.registra_button);
		pulisciButton = (ImageButton) findViewById(R.id.pulisci_button);
		nome = (EditText) findViewById(R.id.editTextNome);
		cognome = (EditText) findViewById(R.id.editTextCognome);
		email = (EditText) findViewById(R.id.editTextEmail);
		passwd = (EditText) findViewById(R.id.editTextPassword);
		repeatPasswd = (EditText) findViewById(R.id.editTextRepeatPassword);

		registerToolTipFor(registraButton);
		registraButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						"Registrazione nuovo utente in corso...",
						Toast.LENGTH_SHORT).show();

				if (!FieldsValidator.Is_Valid_Person_Name(nome)) {
					nome.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_Person_Name(cognome)) {
					cognome.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_Email(email)) {
					email.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_Password(passwd)) {
					passwd.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_RetypedPassword(repeatPasswd,
						passwd.getText())) {
					repeatPasswd.requestFocus();
					return;
				}

				// Salva dati nel database con password criptata

				DatabaseOps databaseOps = new DatabaseOps();
				// Controlla se le credenziali esistono
				String phpencoder = getPrefs.getString("phpencoder", null);
				if (phpencoder == null) {
					Toast.makeText(
							getApplicationContext(),
							"Login fallito! File dell'encoder php non valorizzato in menù preferenze?",
							Toast.LENGTH_SHORT).show();
					return;
				}

				// Controlla le credenziali dell'utente
				Toast.makeText(
						getApplicationContext(),
						"Registrazione Nuovo Utente del Registro Scolastico in corso...",
						Toast.LENGTH_SHORT).show();

				boolean hasBeenRegistered = databaseOps.RegisterNewUser(
						getApplicationContext(), nome.getText().toString(),
						cognome.getText().toString(), email.getText()
								.toString(), passwd.getText().toString(),
						getDatabaseIpFromPreferences(), phpencoder);

				if (hasBeenRegistered) {

					/**
					 * Va alla pagina di login
					 */
					Intent loginUserActivity = new Intent(
							"android.intent.action.LOGIN");
					startActivity(loginUserActivity);
				} else {
					Toast.makeText(getApplicationContext(),
							"Iscrizione fallita! Correggi i dati inseriti!",
							Toast.LENGTH_SHORT).show();
				}
			}

		});

		registerToolTipFor(pulisciButton);
		pulisciButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						"Pulizia campi in corso...",
						Toast.LENGTH_SHORT).show();

				nome.setText("");
				cognome.setText("");
				email.setText("");
				passwd.setText("");
				repeatPasswd.setText("");
				nome.setError(null);
				cognome.setError(null);
				email.setError(null);
				passwd.setError(null);
				repeatPasswd.setError(null);
			}

		});
	}

	@Override
	public String getDefaultDatabaseFromPreferences() {
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		return defaultDatabase;
	}

	@Override
	public String getDatabaseIpFromPreferences() {
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
		getMenuInflater().inflate(R.menu.register_user, menu);
		return true;
	}

	@Override
	public void registerToolTipFor(ImageButton ib) {
		// TODO Auto-generated method stub
		ib.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				customToast(view.getContentDescription(), R.drawable.help32,
						R.layout.info_layout);
				return true;
			}
		});
	}

	@Override
	public boolean customToast(CharSequence charSequence, int iconId,
			int layoutId) {
		// TODO Auto-generated method stub
		Resources res = getResources();
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(layoutId,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		TextView tvToastConnect = (TextView) layout
				.findViewById(R.id.tvToastConnect);
		tvToastConnect.setText(charSequence);

		ImageView ivToastConnect = (ImageView) layout
				.findViewById(R.id.ivToastConnect);
		ivToastConnect.setImageDrawable(res.getDrawable(iconId));

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
		return true;
	}

}
