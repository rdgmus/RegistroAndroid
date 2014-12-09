package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.UserMenu;
import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.interfaces.GeneratePasswordInterface;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;
import it.keyorchestra.registrowebapp.scuola.util.UtentiArrayAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class RilascioNuovePassword extends Activity implements
		ActivitiesCommonFunctions, GeneratePasswordInterface {
	private SharedPreferences getPrefs;
	private boolean backButtonVisible;
	private ArrayList<String> arrayData;
	private JSONArray jsonData;

	LinearLayout llGeneratorePassword, llRichieste, llCommands, headerLayout,
			spinnerLayout;

	Spinner spinnerRichiestePassword;
	SeekBar sbPasswdLen;
	TextView tvPasswdLen, tvRilascioTitle;
	EditText etRepeatPasswd, etNewPasswd, etEmail, etPasswd;;
	CheckBox cbVediPassword;
	ImageButton ibGeneratePasswd, ibSavePasswd, ibBack;
	// RadioGroup rgPendingConfirmed;
	ToggleButton tbPendingConfirmed;

	private long selectedUser;

	/**
	 * @return the backButtonVisible
	 */
	public boolean isBackButtonVisible() {
		return backButtonVisible;
	}

	/**
	 * @param backButtonVisible
	 *            the backButtonVisible to set
	 */
	public void setBackButtonVisible(boolean backButtonVisible) {
		this.backButtonVisible = backButtonVisible;
		if (backButtonVisible) {
			ibBack.setVisibility(ImageButton.VISIBLE);
			tvRilascioTitle.setVisibility(TextView.VISIBLE);
		} else {
			ibBack.setVisibility(ImageButton.GONE);
			tvRilascioTitle.setVisibility(TextView.GONE);
		}
	}

	/**
	 * @return the selectedUser
	 */
	public long getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser
	 *            the selectedUser to set
	 */
	public void setSelectedUser(long selectedUser) {
		this.selectedUser = selectedUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rilascio_nuove_password);
		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		headerLayout = (LinearLayout) findViewById(R.id.headerLayout);

		tvRilascioTitle = (TextView) headerLayout
				.findViewById(R.id.tvRilascioTitle);
		ibBack = (ImageButton) headerLayout.findViewById(R.id.ibBack);

		setBackButtonVisible(getPrefs.getBoolean("backButtonForPasswordChange",
				false));
		ibBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Se il bottone è visibile una volta premuto riporta
				// alla UserMenu.class
				startAnimation((ImageButton) v, 2000);
				Intent ourStartingPoint = new Intent(
						RilascioNuovePassword.this, UserMenu.class);
				startActivity(ourStartingPoint);

				// FINISH
				RilascioNuovePassword.this.finish();
			}
		});

		llRichieste = (LinearLayout) findViewById(R.id.llRichieste);

		spinnerLayout = (LinearLayout) findViewById(R.id.spinnerLayout);
		// TOGGLE BUTTON
		tbPendingConfirmed = (ToggleButton) spinnerLayout
				.findViewById(R.id.tbPendingConfirmed);
		tbPendingConfirmed.setChecked(true);
		setRadioGroupRequestStateVisible(getPrefs.getBoolean(
				"backButtonForPasswordChange", false));

		tbPendingConfirmed
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						ibSavePasswd
								.setVisibility(isChecked ? ImageButton.VISIBLE
										: ImageButton.INVISIBLE);
						etEmail.setText("");
						etPasswd.setText("");
						loadUserRequestPendingConfirmed(isChecked);
					}
				});
		// SPINNER
		spinnerRichiestePassword = (Spinner) spinnerLayout
				.findViewById(R.id.spinnerRichiestePassword);

		reloadRequestPasswordAdapter();

		spinnerRichiestePassword
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						if (jsonData.length() == 0) {
							etEmail.setText("");
							etPasswd.setText("");
							return;
						}
						// VALORIZZA CAMPI CON jsonData
						try {
							JSONObject json_data = jsonData
									.getJSONObject(position);
							etEmail.setText(json_data.getString("email"));
							etPasswd.setText(json_data.getString("password"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),
								"onNothingSelected:" + parent,
								Toast.LENGTH_SHORT).show();
					}
				});

		// EMAIL E PASSWORD UTENTE CHE HA EFETTUATO RICHIESTA CAMBIO PASSWORD
		etEmail = (EditText) llRichieste.findViewById(R.id.etEmail);
		etPasswd = (EditText) llRichieste.findViewById(R.id.etPasswd);

		llGeneratorePassword = (LinearLayout) findViewById(R.id.llGeneratorePassword);
		etNewPasswd = (EditText) llGeneratorePassword
				.findViewById(R.id.etNewPasswd);
		etRepeatPasswd = (EditText) llGeneratorePassword
				.findViewById(R.id.etRepeatPasswd);
		llCommands = (LinearLayout) llGeneratorePassword
				.findViewById(R.id.llCommands);

		tvPasswdLen = (TextView) llCommands.findViewById(R.id.tvPasswdLen);

		cbVediPassword = (CheckBox) llCommands
				.findViewById(R.id.cbVediPassword);
		cbVediPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						SetPasswordVisibility(isChecked);
					}
				});

		ibGeneratePasswd = (ImageButton) llCommands
				.findViewById(R.id.ibGeneratePasswd);
		registerToolTipFor(ibGeneratePasswd);
		ibGeneratePasswd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				String generatedPassword = GeneratePasswordFromPhp(
						getBaseContext(), getDatabaseIpFromPreferences(),
						getPrefs.getString("phpencoder", null), tvPasswdLen
								.getText().toString());

				etNewPasswd.setText(generatedPassword);
				etRepeatPasswd.setText(generatedPassword);
			}
		});
		ibSavePasswd = (ImageButton) llCommands.findViewById(R.id.ibSavePasswd);
		registerToolTipFor(ibSavePasswd);
		ibSavePasswd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				if (!FieldsValidator.Is_Valid_Password(etNewPasswd)) {
					etNewPasswd.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_RetypedPassword(etRepeatPasswd,
						etNewPasswd.getText())) {
					etRepeatPasswd.requestFocus();
					return;
				}
				// jsonData contiene la lista degli utenti confermati
				// La lista non sarà mai vuota perchè la funzione in php
				// richiamata, ritorna in tal caso una riga [{}]
				if (jsonData.length() == 0) {
					Toast.makeText(getApplicationContext(),
							"Non vi sono utenti confermati!",
							Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					long id_utente = jsonData.getJSONObject(
							spinnerRichiestePassword.getSelectedItemPosition())
							.getLong("id_utente");

					if (SaveNewPassword(id_utente,
							etEmail.getText().toString(), etPasswd.getText()
									.toString(), etNewPasswd.getText()
									.toString())) {
						Toast.makeText(
								getApplicationContext(),
								"Password impostata per l'utente: " + id_utente,
								Toast.LENGTH_LONG).show();
					} else {
						Toast.makeText(
								getApplicationContext(),
								"Non è stato possibile impostare la nuova passord per l'utente: "
										+ id_utente, Toast.LENGTH_LONG).show();
						return;
					}

					if (EmailPasswordToUser(id_utente, etEmail.getText()
							.toString(), etNewPasswd.getText().toString())) {
						Toast.makeText(getApplicationContext(),
								"Inviata Email!  all'utente: " + id_utente,
								Toast.LENGTH_LONG).show();

						// SETTARE IL CAMPO email_sent della tabella
						// change_password_request
						JSONObject jsonRequest = jsonData
								.getJSONObject(spinnerRichiestePassword
										.getSelectedItemPosition());
						String hash = jsonRequest.getString("hash");
						setEmailSentWithSuccessAt(id_utente, hash);

						// Ricarica i dati utente con la nuova password
						loadUserRequestPendingConfirmed(tbPendingConfirmed
								.isChecked());
					} else {
						Toast.makeText(
								getApplicationContext(),
								"Non è stato possibile inviare l'Email!  all'utente: "
										+ id_utente, Toast.LENGTH_LONG).show();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(getApplicationContext(),
							"JSONException: " + e.getMessage(),
							Toast.LENGTH_LONG).show();
				}
			}
		});

		sbPasswdLen = (SeekBar) llGeneratorePassword
				.findViewById(R.id.sbPasswdLen);

		sbPasswdLen.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				tvPasswdLen.setText("" + (progressChanged + 4) + "");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				progressChanged = progress;
			}
		});
		tvPasswdLen.setText("" + (sbPasswdLen.getProgress() + 4) + "");

	}

	protected boolean setEmailSentWithSuccessAt(long id_utente, String hash) {
		// TODO Auto-generated method stub

		DatabaseOps databaseOps = new DatabaseOps(getApplicationContext());
		return databaseOps.setEmailSentWithSuccessAt(getApplicationContext(),
				id_utente, hash);
	}

	/**
	 * Invio email per comunicazione nuova password impostat nel sistema
	 * 
	 * @param id_utente
	 * @param email
	 * @param password
	 * @param msg
	 * @return
	 */
	@Override
	public boolean EmailPasswordToUser(long id_utente, String email,
			String passwordToEncode) {
		// TODO Auto-generated method stub
		String sendNewPasswordToUser = getPrefs.getString(
				"sendNewPasswordToUser", null);
		String ip = getDatabaseIpFromPreferences();

		DatabaseOps databaseOps = new DatabaseOps(getApplicationContext());
		return databaseOps.EmailPasswordToUser(getApplicationContext(),
				id_utente, email, passwordToEncode, sendNewPasswordToUser, ip);
	}

	protected void loadUserRequestPendingConfirmed(boolean isChecked) {
		// TODO Auto-generated method stub
		if (!isChecked) {
			Toast.makeText(
					getApplicationContext(),
					"Richieste in attesa di \n"
							+ "conferma da parte dell'utente!",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getApplicationContext(),
					"Richieste confermate dall'utente!", Toast.LENGTH_SHORT)
					.show();
		}
		reloadRequestPasswordAdapter();
	}

	private void reloadRequestPasswordAdapter() {
		// TODO Auto-generated method stub

		CaricaRichiesteCambioPassword();

		UtentiArrayAdapter utentiAdapter = new UtentiArrayAdapter(
				getApplicationContext(), jsonData, arrayData);
		utentiAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinnerRichiestePassword.setAdapter(utentiAdapter);
		if (jsonData.length() > 0) {
			spinnerRichiestePassword.setSelection(0);
		}
	}

	private void CaricaRichiesteCambioPassword() {
		// TODO Auto-generated method stub
		arrayData = new ArrayList<String>();
		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);

		String ip = getDatabaseIpFromPreferences();

		String query = null;
		if (isBackButtonVisible()) {// cambio password per l'utente stesso
			query = "SELECT * FROM utenti_scuola  WHERE `id_utente`= "
					+ getPrefs.getLong("id_utente", -1l);
		} else {// CAMBIO PASSWORD PER GESTIONE UTENTI DA PARTE DI UN ADMIN
			if (tbPendingConfirmed.isChecked()) {// RICHIESTE PENDING AND
													// CONFIRMED NECESSARIA
													// QUESTA QUERY PER
													// ESCLUDERE IL CAMPO hash
													// della tabella utenti_scuola
				query = "SELECT a.`id_request`, a.`from_user`, a.`user_email`, a.`request_date`, "
						+ "a.`pending`, a.`hash`, a.`confirmed`, a.`request_done_date`, a.`request_done`, "
						+ "a.`email_sent`, a.`id_admin`, a.`elapsed_days`,"
						+ "b.`id_utente`, b.`cognome`, b.`nome`, b.`email`, b.`password`, "
						+ "b.`register_date`, b.`user_is_admin`, b.`has_to_change_password`,"
						+ " b.`is_locked`, b.`email_confirmed` "
						+ " FROM `change_password_request` AS a, utenti_scuola AS b "
						+ " WHERE a.`pending`=1 AND a.`confirmed`=1"
						+ " AND a.`from_user` = b.id_utente";
			} else {// RICHIESTE PENDING MA NON CONFIRMED
				query = "SELECT a.`id_request`, a.`from_user`, a.`user_email`, a.`request_date`, "
						+ "a.`pending`, a.`hash`, a.`confirmed`, a.`request_done_date`, a.`request_done`, "
						+ "a.`email_sent`, a.`id_admin`, a.`elapsed_days`,"
						+ "b.`id_utente`, b.`cognome`, b.`nome`, b.`email`, b.`password`, "
						+ "b.`register_date`, b.`user_is_admin`, b.`has_to_change_password`,"
						+ " b.`is_locked`, b.`email_confirmed` "
						+ " FROM `change_password_request` AS a, utenti_scuola AS b "
						+ " WHERE a.`pending`=1 AND a.`confirmed`=0"
						+ " AND a.`from_user` = b.id_utente";
			}
		}

		try {
			jsonData = new MySqlAndroid().retrieveTableData(
					getApplicationContext(),
					"http://" + ip + "/" + retrieveTableData + "?sql="
							+ URLEncoder.encode(query, "UTF-8"), null);

			for (int i = 0; i < jsonData.length(); i++) {
				JSONObject json_data;
				try {
					json_data = jsonData.getJSONObject(i);
					long id_utente = json_data.getLong("id_utente");
					String cognome = json_data.getString("cognome");
					String nome = json_data.getString("nome");
					String email = json_data.getString("email");

					arrayData.add("[" + id_utente + "] " + cognome + " " + nome
							+ " <" + email + ">");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//
	// protected void loadUserRequestPendingConfirmed(int checkedId) {
	// // TODO Auto-generated method stub
	// switch (checkedId) {
	// case R.id.radioPending:
	// Toast.makeText(getApplicationContext(), "Richieste in attesa!",
	// Toast.LENGTH_SHORT).show();
	// reloadRequestPasswordAdapter();
	// break;
	// case R.id.radioConfirmed:
	// Toast.makeText(getApplicationContext(), "Richieste confermate!",
	// Toast.LENGTH_SHORT).show();
	// reloadRequestPasswordAdapter();
	// break;
	// }
	// }
	//
	public void setRadioGroupRequestStateVisible(boolean backButtonVisible) {
		// TODO Auto-generated method stub
		if (backButtonVisible) {
			// rgPendingConfirmed.setVisibility(RadioGroup.GONE);
			tbPendingConfirmed.setVisibility(ToggleButton.GONE);
		} else {
			// rgPendingConfirmed.setVisibility(RadioGroup.VISIBLE);
			tbPendingConfirmed.setVisibility(ToggleButton.VISIBLE);
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
	public String getDefaultDatabaseFromPreferences() {
		// TODO Auto-generated method stub
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		return defaultDatabase;
	}

	@Override
	public String getDatabaseIpFromPreferences() {
		// TODO Auto-generated method stub
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

	@Override
	public String GeneratePasswordFromPhp(Context context, String ip,
			String servSideScript, String length) {
		// TODO Auto-generated method stub
		DatabaseOps databaseOps = new DatabaseOps(getApplicationContext());
		// Controlla se le credenziali esistono

		return databaseOps
				.generatePassword(context, ip, servSideScript, length);
	}

	@Override
	public boolean SaveNewPassword(long id_utente, String email,
			String oldPassword, String passwordToEncode) {
		// TODO Auto-generated method stub
		String phpencoder = getPrefs.getString("phpencoder", null);
		String ip = getDatabaseIpFromPreferences();

		DatabaseOps databaseOps = new DatabaseOps(getApplicationContext());
		return databaseOps.SaveNewPassword(getApplicationContext(), id_utente,
				email, oldPassword, passwordToEncode, ip, phpencoder);
	}

	@Override
	public void SetPasswordVisibility(boolean isVisible) {
		// TODO Auto-generated method stub
		if (isVisible) {
			etNewPasswd
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			etRepeatPasswd
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		} else {
			etNewPasswd.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			etRepeatPasswd.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	}

}
