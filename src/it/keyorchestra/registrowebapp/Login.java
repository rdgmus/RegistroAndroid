package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;
import it.keyorchestra.registrowebapp.scuola.util.ToastExpander;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class Login extends Activity implements ActivitiesCommonFunctions {

	ImageButton loginButton, bCambiaRuolo, ibFillFields, ibGotoRegister,
			ibHome, pulisciButton, imShowMenu, ibForgotPassword;
	TextView etRuoloScelto;
	Thread myThread = null;
	EditText etLoginEmail, etLoginPasswd;
	private SharedPreferences getPrefs;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		ibForgotPassword = (ImageButton) findViewById(R.id.ibForgotPassword);
		registerToolTipFor(ibForgotPassword);
		ibForgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Richiesta Nuova Password inviata all'ADMIN!",
						Toast.LENGTH_LONG).show();
			}
		});

		pulisciButton = (ImageButton) findViewById(R.id.pulisci_campi);
		registerToolTipFor(pulisciButton);
		pulisciButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(), "Pulizia campi",
						Toast.LENGTH_SHORT).show();

				etLoginEmail.setText("");
				etLoginPasswd.setText("");
				etLoginEmail.setError(null);
				etLoginPasswd.setError(null);
			}

		});

		ibHome = (ImageButton) findViewById(R.id.ibHome);
		ibHome.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Va alla pagina di login
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Re-indirizzamento a pagina iniziale",
						Toast.LENGTH_SHORT).show();

				Intent ourStartingPoint = new Intent(
						"it.keyorchestra.registrowebapp.LIST_ACTIVITY");
				startActivity(ourStartingPoint);
				finish();
			}

		});
		registerToolTipFor(ibHome);

		ibGotoRegister = (ImageButton) findViewById(R.id.ibGotoRegister);
		ibGotoRegister.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Va alla pagina di login
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Re-indirizzamento a Iscrizione", Toast.LENGTH_SHORT)
						.show();

				Intent loginUserActivity = new Intent(
						"android.intent.action.REGISTER_USER");
				startActivity(loginUserActivity);
				finish();
			}

		});
		registerToolTipFor(ibGotoRegister);

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		etLoginEmail = (EditText) findViewById(R.id.etLoginEmail);

		etLoginPasswd = (EditText) findViewById(R.id.etLoginPasswd);

		imShowMenu = (ImageButton) findViewById(R.id.imShowMenu);
		registerToolTipFor(imShowMenu);
		imShowMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// "imShowMenu.OnClickListener()", Toast.LENGTH_SHORT)
				// .show();
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(), "Richiesta menù",
						Toast.LENGTH_SHORT).show();

				openOptionsMenu();
			}
		});

		loginButton = (ImageButton) findViewById(R.id.login_button);
		setLoginState(false);
		registerToolTipFor(loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {

			@SuppressLint("ShowToast")
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Autenticazione credenziali", Toast.LENGTH_SHORT)
						.show();
				if (!FieldsValidator.Is_Valid_Email(etLoginEmail)) {
					etLoginEmail.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_Password(etLoginPasswd)) {
					etLoginPasswd.requestFocus();
					return;
				}

				DatabaseOps databaseOps = new DatabaseOps(
						getApplicationContext());
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
				boolean isAuthenticated = databaseOps.AuthenticateUser(
						getApplicationContext(), etLoginEmail.getText()
								.toString(),
						etLoginPasswd.getText().toString(),
						getDatabaseIpFromPreferences(), phpencoder);

				if (isAuthenticated) {// Credenziali esistenti
					// Controlla se l'utente è locked
					ibForgotPassword.setVisibility(ImageButton.INVISIBLE);

					SharedPreferences sharedpreferences = PreferenceManager
							.getDefaultSharedPreferences(getApplicationContext());
					Long is_locked = sharedpreferences.getLong("is_locked", -1);
					if (is_locked == 1) {
						Toast myToast = Toast
								.makeText(
										getApplicationContext(),
										"(1) L'utente ha già effettuato il login da un altro IP?\n"
												+ "(2) Non ha alcun ruolo accreditato?\n"
												+ "(3) Non ha effettuato il logout?\n"
												+ "Contattare l'Amministratore!\n"
												+ "Permesso di accesso NEGATO!",
										Toast.LENGTH_SHORT);

						ToastExpander.showFor(myToast, 10000);

					} else {

						// Controlla i permessi del ruolo di accesso prescelto
						// per l'utente salvato nelle preferenze
						String ruoloScelto = etRuoloScelto.getText().toString();
						Toast.makeText(
								getApplicationContext(),
								"Ora controllo se hai i permessi\n"
										+ "per il ruolo di " + ruoloScelto,
								Toast.LENGTH_SHORT).show();

						if (databaseOps.LoggingUserHasRole(ruoloScelto,
								getApplicationContext(),
								getDatabaseIpFromPreferences())) {

							Toast.makeText(
									getApplicationContext(),
									"Permessi accordati!\n"
											+ "Autenticazione riuscita per il ruolo di "
											+ ruoloScelto, Toast.LENGTH_SHORT)
									.show();
							saveRuoloSceltoIntoPreferences(ruoloScelto);
							// Loccare l'id dell'utente: is_locked = 1
							databaseOps.LockUserLogging(
									getApplicationContext(),
									getDatabaseIpFromPreferences());

							// REGISTRA LOGIN EVENT

							// Lancia un nuovo Intent per andare allo userMenu
							// del ruolo prescelto. All'interno dello user menù
							// verranno mostrate opzioni in base al ruolo e se
							// l'utente è un admin avrà anche la possibilità di
							// accedere
							// al database con tutti i permessi

							Thread thread = new Thread() {
								@Override
								public void run() {
									try {
										Thread.sleep(3500); // As I am using
															// LENGTH_LONG in
															// Toast
										Intent ourStartingPoint = new Intent(
												Login.this, UserMenu.class);
										startActivity(ourStartingPoint);
										finish();
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							};

							Toast.makeText(getApplicationContext(),
									"Ora accediamo al suo menù!",
									Toast.LENGTH_SHORT).show();
							thread.start();

						} else {
							Toast.makeText(
									getApplicationContext(),
									"Non hai i permessi di accesso per il ruolo di "
											+ ruoloScelto
											+ "\nPermesso di accesso NEGATO!",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast myToast = Toast
							.makeText(
									getApplicationContext(),
									"(1) Credenziali invalide!?\n"
											+ "(2) Vuoi richiedere una Nuova Password?\n"
											+ "(3) Contattare l'Amministratore\n"
											+ "    premendo sulle chiavi!\n"
											+ "Permesso di accesso NEGATO!",
									Toast.LENGTH_SHORT);

					ToastExpander.showFor(myToast, 10000);

					ibForgotPassword.setVisibility(ImageButton.VISIBLE);
					startAnimation(ibForgotPassword, 5000);
				}
			}
		});

		etRuoloScelto = (TextView) findViewById(R.id.etRuoloScelto);

		String ruoloScelto = getPrefs.getString("ruoloList", "Professore");

		etRuoloScelto.setText(ruoloScelto);

		bCambiaRuolo = (ImageButton) findViewById(R.id.bCambiaRuolo);

		registerToolTipFor(bCambiaRuolo);

		bCambiaRuolo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(), "Cambio Ruolo",
						Toast.LENGTH_SHORT).show();

				Intent i = new Intent(
						"it.keyorchestra.registrowebapp.RUOLO_UTENTE");
				startActivity(i);

			}
		});
		setIconRuoloScelto(bCambiaRuolo, ruoloScelto);

		// FILL FIELDS
		ibFillFields = (ImageButton) findViewById(R.id.ibFillFields);
		registerToolTipFor(ibFillFields);
		ibFillFields.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Inserimento credenziali amministratore",
						Toast.LENGTH_SHORT).show();

				String adminEmail = getPrefs.getString("adminEmail", "");
				String adminPasswd = getPrefs.getString("adminPasswd", "");
				// String adminNome = getPrefs.getString("adminNome", "");
				// String adminCognome = getPrefs.getString("adminCognome", "");
				etLoginEmail.setText(adminEmail);
				etLoginPasswd.setText(adminPasswd);
			}
		});

	}

	protected void saveRuoloSceltoIntoPreferences(String ruoloScelto) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = getPrefs.edit();
		editor.putString("ruoloScelto", ruoloScelto.toUpperCase());
		editor.apply();
	}

	private void setLoginState(boolean b) {
		// TODO Auto-generated method stub
		loginButton.setEnabled(b);
		Resources res = getResources();
		if (b) {
			loginButton.setImageDrawable(res.getDrawable(R.drawable.exec));
		} else
			loginButton.setImageDrawable(res.getDrawable(R.drawable.pause));
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
			String retval = new DatabaseOps(getApplicationContext())
					.FetchConnection(getBaseContext());
			return retval;
		}

		@Override
		protected void onPostExecute(String value) {

			String ip = getDatabaseIpFromPreferences();

			String defaultDatabase = getDefaultDatabaseFromPreferences();
			if (value.equals("1")) {
				loginMessage("Connessione con " + defaultDatabase
						+ " stabilita! ip:" + ip);

				setLoginState(true);
			} else {
				loginMessage("Fallita connessione al Database "
						+ defaultDatabase + "!\n "
						+ "Controlla i parametri...e i server MySQL & Apache");
				openOptionsMenu();
				setLoginState(false);
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
		loginMessage("Verifica della connessione al database!");
		setLoginState(false);

		String defaultDatabase = getPrefs.getString("databaseList", "1");

		if (defaultDatabase.contentEquals("MySQL")) {

			new FetchSQL().execute();
		}
		if (defaultDatabase.contentEquals("PostgreSQL")) {
			Toast.makeText(context, "NOT YET IMPLEMENTED!", Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void loginMessage(String msg) {
		// TODO Auto-generated method stub
		String defaultDatabase = getDefaultDatabaseFromPreferences();
		if (defaultDatabase.contentEquals("MySQL")) {
			customToast(msg, R.drawable.logo_mysql,
					R.layout.toast_connect_layout);
		}
		if (defaultDatabase.contentEquals("PostgreSQL")) {
			customToast(msg, R.drawable.logo_postgresql,
					R.layout.toast_connect_layout);
		}
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

	@Override
	public void startAnimation(final View ib, final long durationInMilliseconds) {
		// TODO Auto-generated method stub
		final String TAG = "ImageButton Animation";
		Animation animation = new AlphaAnimation(1.0f, 0.25f); // Change alpha
																// from
		// fully visible to
		// invisible
		animation.setDuration(500); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
													// end so the button will
													// fade back in

		ib.startAnimation(animation);

		Thread t = new Thread() {
			long timeElapsed = 0l;

			public void run() {
				try {
					while (timeElapsed <= durationInMilliseconds) {
						long start = System.currentTimeMillis();
						sleep(1000);
						timeElapsed += System.currentTimeMillis() - start;
					}
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
				} finally {
					ib.clearAnimation();
				}
			}
		};
		t.start();
	}

}
