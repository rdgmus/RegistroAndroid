package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.SqLite;
import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.scuola.util.RuoliArrayAdapter;
import it.keyorchestra.registrowebapp.scuola.util.UtentiArrayAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AssegnamentoRuoliActivity extends Activity implements
		ActivitiesCommonFunctions {
	private SharedPreferences getPrefs;
	private ArrayList<String> ruoliArray, utentiArray;
	private JSONArray jArrayRuoli, jArrayUtenti;

	Spinner spinnerRuoli, spinnerUtenti;
	ImageButton bAddRole, bRemoveRole;
	ToggleButton tbMembers;
	private long selectedRole = -1;
	private long selectedUser = -1;

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

	/**
	 * @return the selectedRole
	 */
	public long getSelectedRole() {
		return selectedRole;
	}

	/**
	 * @param selectedRole
	 *            the selectedRole to set
	 */
	public void setSelectedRole(long selectedRole) {
		this.selectedRole = selectedRole;
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
		setContentView(R.layout.activity_assegnamento_ruoli);

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		// tbMembers
		tbMembers = (ToggleButton) findViewById(R.id.tbMembers);
		tbMembers.setChecked(true);
		tbMembers.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				toggleAddRemoveButtons(isChecked);
				reloadUtentiAdapter(getSelectedRole());
				CaricaUtentiRuoloSelezionato(getSelectedRole(), null);

				int position = getUserIdPositionIntoSpinner(spinnerUtenti,
						getSelectedUser());
				spinnerUtenti.setSelection(position, true);

			}
		});
		// ADD ROLE
		bAddRole = (ImageButton) findViewById(R.id.bAddRole);
		registerToolTipFor(bAddRole);
		bAddRole.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				addUserToRole(getSelectedRole(), getSelectedUser());
			}

		});

		// REMOVE ROLE
		bRemoveRole = (ImageButton) findViewById(R.id.bRemoveRole);
		registerToolTipFor(bRemoveRole);
		bRemoveRole.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				removeUserFromRole(getSelectedRole(), getSelectedUser());

			}
		});
		// SPINNER RUOLI
		spinnerRuoli = (Spinner) findViewById(R.id.spinnerRuoli);
		
		CaricaArrayRuoli();
		RuoliArrayAdapter ruoliAdapter = new RuoliArrayAdapter(
				getApplicationContext(), jArrayRuoli,
				ruoliArray);

		ruoliAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinnerRuoli.setAdapter(ruoliAdapter);

		spinnerRuoli.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				TextView myTextView = (TextView) view
						.findViewById(R.id.tvMyText);
				CaricaUtentiRuoloSelezionato((Long) myTextView.getTag(),
						(String) myTextView.getText());

				reloadUtentiAdapter((Long) myTextView.getTag());

				setSelectedRole((Long) myTextView.getTag());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});

		// SPINNER UTENTI
		spinnerUtenti = (Spinner) findViewById(R.id.spinnerUtenti);
		reloadUtentiAdapter(-1l);

		spinnerUtenti.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (view == null)
					return;
				TextView myTextView = (TextView) view
						.findViewById(R.id.tvMyText);
				setSelectedUser((Long) myTextView.getTag());
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		toggleAddRemoveButtons(true);
	}

	private void CaricaArrayRuoli() {
		// TODO Auto-generated method stub
		ruoliArray = new ArrayList<String>();
		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);

		String ip = getDatabaseIpFromPreferences();

		// jsonRuoliAmmessi(retrieveTableData, ip);

		String query = "SELECT * FROM ruoli_utenti WHERE 1 ORDER BY ruolo";

		

		try {
			jArrayRuoli = new MySqlAndroid().retrieveTableData(
					getApplicationContext(),
					"http://" + ip + "/" + retrieveTableData + "?sql="
							+ URLEncoder.encode(query, "UTF-8"), null);
			
			for (int i = 0; i < jArrayRuoli.length(); i++) {
				JSONObject json_data;
				try {
					json_data = jArrayRuoli.getJSONObject(i);
					long id_ruolo = json_data.getLong("id_ruolo");
					String ruolo = json_data.getString("ruolo");

					ruoliArray.add("[" + id_ruolo + "] " + ruolo);
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

	/**
	 * Imposta i bottoni addUser e removeUser visibility
	 * 
	 * @param isChecked
	 */
	protected void toggleAddRemoveButtons(boolean isChecked) {
		// TODO Auto-generated method stub
		bAddRole.setVisibility(isChecked ? ToggleButton.GONE
				: ToggleButton.VISIBLE);
		bRemoveRole.setVisibility(!isChecked ? ToggleButton.GONE
				: ToggleButton.VISIBLE);
	}

	/**
	 * Remuove l'utente dal ruolo indicato.
	 * 
	 * @param selectedRole
	 * @param selectedUser
	 */
	protected void removeUserFromRole(long selectedRole, long selectedUser) {
		// TODO Auto-generated method stub

		DatabaseOps dataBaseOps = new DatabaseOps(getApplicationContext());
		String ruoloScelto = getPrefs.getString("ruoloScelto", "");
		long id_utente = getPrefs.getLong("id_utente", -1);
		if (id_utente == selectedUser) {
			if (ruoloScelto.equals(dataBaseOps.getRuoloName(
					getApplicationContext(), selectedRole))) {
				Toast.makeText(
						getApplicationContext(),
						"NON PUOI RIMUOVERE TE STESSO DAL RUOLO: ["
								+ ruoloScelto + "] !", Toast.LENGTH_LONG)
						.show();
				return;
			}
		}

		// RIMOZIONE UTENTE DA RUOLO
		dataBaseOps.removeUserFromRole(AssegnamentoRuoliActivity.this,
				selectedRole, selectedUser);
		reloadUtentiAdapter(getSelectedRole());
		CaricaUtentiRuoloSelezionato(selectedRole, null);
		Toast.makeText(
				getApplicationContext(),
				"Rimosso Utente: ["
						+ dataBaseOps.getUserSurname(getApplicationContext(),
								selectedUser)
						+ " "
						+ dataBaseOps.getUserName(getApplicationContext(),
								selectedUser)
						+ "] da Ruolo: ["
						+ dataBaseOps.getRuoloName(getApplicationContext(),
								selectedRole) + "]", Toast.LENGTH_LONG).show();
	}

	/**
	 * Aggiunge un utente al gruppo avente il ruolo indicato
	 * 
	 * @param selectedRole
	 * @param selectedUser
	 */
	protected void addUserToRole(long selectedRole, long selectedUser) {
		// TODO Auto-generated method stub

		DatabaseOps dataBaseOps = new DatabaseOps(getApplicationContext());
		dataBaseOps.addUserToRole(AssegnamentoRuoliActivity.this, selectedRole,
				selectedUser);
		reloadUtentiAdapter(getSelectedRole());
		CaricaUtentiRuoloSelezionato(selectedRole, null);
		Toast.makeText(
				getApplicationContext(),
				"Aggiunto Ruolo: ["
						+ dataBaseOps.getRuoloName(getApplicationContext(),
								selectedRole)
						+ "] "
						+ "a Utente: ["
						+ dataBaseOps.getUserSurname(getApplicationContext(),
								selectedUser)
						+ " "
						+ dataBaseOps.getUserName(getApplicationContext(),
								selectedUser) + "]", Toast.LENGTH_LONG).show();
	}

	/**
	 * Rifresca l'adapter degli utenti per lo spinnerUtenti in modo che rifletta
	 * la situazione attuale dei ruoli-utenti
	 * 
	 * @param tag
	 */
	private void reloadUtentiAdapter(Long tag) {
		// TODO Auto-generated method stub
		CaricaArrayUtenti(tag);
		
		UtentiArrayAdapter utentiAdapter = new UtentiArrayAdapter(
				getApplicationContext(), jArrayUtenti, utentiArray);
		utentiAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinnerUtenti.setAdapter(utentiAdapter);
		if (jArrayUtenti.length() > 0) {
			spinnerUtenti.setSelection(0);
		}
	}

	private void CaricaArrayUtenti(long id_ruolo) {
		// TODO Auto-generated method stub
		 utentiArray = new ArrayList<String>();
		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);

		String ip = getDatabaseIpFromPreferences();

		String query;

		if (id_ruolo == -1) {
			query = "SELECT * FROM utenti_scuola WHERE 1 ORDER BY cognome, nome";
		} else {
			if (tbMembers.isChecked()) {
				query = "SELECT * FROM utenti_scuola as a WHERE  EXISTS ("
						+ "SELECT * FROM ruoli_granted_to_utenti as b "
						+ "WHERE a.id_utente = b.id_utente "
						+ "AND b.id_ruolo = " + id_ruolo
						+ ") ORDER BY a.cognome, a.nome";
			} else {
				query = "SELECT * FROM utenti_scuola as a WHERE NOT EXISTS ("
						+ "SELECT * FROM ruoli_granted_to_utenti as b "
						+ "WHERE a.id_utente = b.id_utente "
						+ "AND b.id_ruolo = " + id_ruolo
						+ ") ORDER BY a.cognome, a.nome";
			}
		}

		

		try {
			jArrayUtenti = new MySqlAndroid().retrieveTableData(
					getApplicationContext(),
					"http://" + ip + "/" + retrieveTableData + "?sql="
							+ URLEncoder.encode(query, "UTF-8"), null);
			for (int i = 0; i < jArrayUtenti.length(); i++) {
				JSONObject json_data;
				try {
					json_data = jArrayUtenti.getJSONObject(i);
					long id_utente = json_data.getLong("id_utente");
					String cognome = json_data.getString("cognome");
					String nome = json_data.getString("nome");
					String email = json_data.getString("email");

					utentiArray.add("[" + id_utente + "] " + cognome + " "
							+ nome + " <" + email + ">");
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

	/**
	 * Carica la lista degli utenti appatenenti o non appartenenti al ruolo di
	 * cui si indica l'id e la stringa del ruolo ricoperto per i confronti
	 * necessari nel corpo della funzione.
	 * 
	 * @param id_ruolo
	 * @param ruolo
	 */
	protected void CaricaUtentiRuoloSelezionato(long id_ruolo, String ruolo) {
		// TODO Auto-generated method stub

		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);

		String ip = getDatabaseIpFromPreferences();

		// jsonRuoliAmmessi(retrieveTableData, ip);

		String query;
		if (tbMembers.isChecked()) {
			query = "SELECT * FROM utenti_scuola as a WHERE  EXISTS ("
					+ "SELECT * FROM ruoli_granted_to_utenti as b "
					+ "WHERE a.id_utente = b.id_utente " + "AND b.id_ruolo = "
					+ id_ruolo + ") ORDER BY a.cognome, a.nome";
		} else {
			query = "SELECT * FROM utenti_scuola as a WHERE NOT EXISTS ("
					+ "SELECT * FROM ruoli_granted_to_utenti as b "
					+ "WHERE a.id_utente = b.id_utente " + "AND b.id_ruolo = "
					+ id_ruolo + ") ORDER BY a.cognome, a.nome";
		}
		JSONArray jArray;
		try {
			jArray = new MySqlAndroid().retrieveTableData(
					getApplicationContext(),
					"http://" + ip + "/" + retrieveTableData + "?sql="
							+ URLEncoder.encode(query, "UTF-8"), null);

			if (jArray == null) {
				Toast.makeText(
						getApplicationContext(),
						"Nessuna risposta dal server MySQL! Controlla lo stato di Apache & MySQL server!",
						Toast.LENGTH_LONG).show();
			} else {
				buildUsersAndRolesTable(jArray);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (ruolo != null)
				Toast.makeText(getApplicationContext(),
						"Ruolo: " + ruolo + " Id:" + id_ruolo,
						Toast.LENGTH_SHORT).show();
		}

	}

	/**
	 * Lista dei ruoli previsti nella tabella ruoli_utenti del database.
	 * 
	 * @param phpInterface
	 * @param ip
	 * @return
	 */
	@SuppressWarnings("unused")
	private JSONArray jsonRuoliAmmessi(String phpInterface, String ip) {
		// TODO Auto-generated method stub
		String query = "SELECT * FROM ruoli_utenti WHERE 1 ORDER BY ruolo";

		JSONArray jArray = null;
		try {
			jArray = new MySqlAndroid().retrieveTableData(
					getApplicationContext(),
					"http://" + ip + "/" + phpInterface + "?sql="
							+ URLEncoder.encode(query, "UTF-8"), null);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jArray;
	}

	/**
	 * Costruzione della tabella utenti-ruoli con i dati ricevuti
	 * dall'interfaccia server-scripting-interface in PHP
	 * 
	 * @param jArray
	 */
	private void buildUsersAndRolesTable(JSONArray jArray) {
		// TODO Auto-generated method stub
		try {
			// HEADER TABLE
			TableLayout headerTable = (TableLayout) findViewById(R.id.header_table);
			headerTable.removeAllViews();
			headerTable = addRowToTable(getApplicationContext(), headerTable,
					LayoutParams.WRAP_CONTENT);
			headerTable.setBackgroundColor(getResources().getColor(
					R.color.colorBlack));

			// CONTENT TABLE
			TableLayout contentTable = (TableLayout) findViewById(R.id.content_table);
			contentTable.removeAllViews();
			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);

				contentTable = addRowToTableAsJson(getApplicationContext(),
						contentTable, json_data, LayoutParams.WRAP_CONTENT, i);

			}

		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data" + e.toString());

			Toast.makeText(getApplicationContext(), "JsonArray fail",
					Toast.LENGTH_SHORT).show();

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

	/**
	 * Aggiunge una riga nella tabella dei contenuti, riguardante gli utenti
	 * appartenenti o non appartenenti al ruolo con i ruoli ricoperti.
	 * 
	 * @param applicationContext
	 * @param headerTable
	 * @param json_data
	 * @param height
	 * @param index
	 * @return
	 * @throws JSONException
	 */
	protected TableLayout addRowToTableAsJson(Context applicationContext,
			TableLayout headerTable, JSONObject json_data, int height, int index)
			throws JSONException {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) applicationContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View rowView = inflater.inflate(R.layout.ruoli_row_layout,
				headerTable, false);
		buildRuoliRowLayout(applicationContext, json_data, rowView);

		if ((index % 2) == 0) {
			rowView.setBackgroundColor(getResources().getColor(
					R.color.colorListItem));
		} else {
			rowView.setBackgroundColor(getResources().getColor(
					R.color.colorOrange));
		}

		rowView.setTag(json_data.getLong("id_utente"));
		rowView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation(rowView, 2000);
				int position = getUserIdPositionIntoSpinner(spinnerUtenti,
						(Long) v.getTag());
				spinnerUtenti.setSelection(position, true);
				setSelectedUser((Long) v.getTag());
			}
		});

		headerTable.addView(rowView);
		return headerTable;
	}

	/**
	 * Costruisce la riga di utente e ruoli ricoperti con il layout contenuto
	 * nella rowView, dal quale richiama i TextView e gli ImageButton e li
	 * valorizza i primi, e rende visibili o invisibili i secondi in base ai
	 * ruoli ricoperti dall'utente stesso.
	 * 
	 * @param applicationContext
	 * @param json_data
	 * @param rowView
	 */
	private void buildRuoliRowLayout(Context applicationContext,
			JSONObject json_data, View rowView) {
		// TODO Auto-generated method stub
		TextView tvIdUtente = (TextView) rowView.findViewById(R.id.tvIdUtente);
		TextView tvCognomeNome = (TextView) rowView
				.findViewById(R.id.tvCognomeNome);
		TextView tvEmail = (TextView) rowView.findViewById(R.id.tvEmail);

		// IMMAGINI RUOLI
		HorizontalScrollView hsvRoles = (HorizontalScrollView) rowView
				.findViewById(R.id.hsvRoles);
		LinearLayout layoutRoles = (LinearLayout) hsvRoles
				.findViewById(R.id.layoutRoles);

		ImageButton ibAdmin = (ImageButton) layoutRoles
				.findViewById(R.id.ibAdmin);
		ibAdmin.setVisibility(ImageButton.GONE);
		ibAdmin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long id_ruolo = getIdRuolo("AMMINISTRATORE");
				int position = getRoleIdPositionIntoSpinner(spinnerRuoli,
						id_ruolo);
				spinnerRuoli.setSelection(position, true);

				position = getUserIdPositionIntoSpinner(spinnerUtenti,
						(Long) v.getTag());
				spinnerUtenti.setSelection(position, true);

				setSelectedUser((Long) v.getTag());
				tbMembers.setChecked(true);
			}
		});

		ImageButton ibAta = (ImageButton) layoutRoles.findViewById(R.id.ibAta);
		ibAta.setVisibility(ImageButton.GONE);
		ibAta.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long id_ruolo = getIdRuolo("ATA");
				int position = getRoleIdPositionIntoSpinner(spinnerRuoli,
						id_ruolo);
				spinnerRuoli.setSelection(position, true);

				position = getUserIdPositionIntoSpinner(spinnerUtenti,
						(Long) v.getTag());
				spinnerUtenti.setSelection(position, true);
				setSelectedUser((Long) v.getTag());
				tbMembers.setChecked(true);
			}
		});

		ImageButton ibProf = (ImageButton) layoutRoles
				.findViewById(R.id.ibProf);
		ibProf.setVisibility(ImageButton.GONE);
		ibProf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long id_ruolo = getIdRuolo("INSEGNANTE");
				int position = getRoleIdPositionIntoSpinner(spinnerRuoli,
						id_ruolo);
				spinnerRuoli.setSelection(position, true);

				position = getUserIdPositionIntoSpinner(spinnerUtenti,
						(Long) v.getTag());
				spinnerUtenti.setSelection(position, true);
				setSelectedUser((Long) v.getTag());
				tbMembers.setChecked(true);
			}
		});

		ImageButton ibSegr = (ImageButton) layoutRoles
				.findViewById(R.id.ibSegr);
		ibSegr.setVisibility(ImageButton.GONE);
		ibSegr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				long id_ruolo = getIdRuolo("SEGRETERIA");
				int position = getRoleIdPositionIntoSpinner(spinnerRuoli,
						id_ruolo);
				spinnerRuoli.setSelection(position, true);

				position = getUserIdPositionIntoSpinner(spinnerUtenti,
						(Long) v.getTag());
				spinnerUtenti.setSelection(position, true);
				setSelectedUser((Long) v.getTag());

				tbMembers.setChecked(true);
			}
		});

		try {
			tvIdUtente.setText(String.valueOf(json_data.getLong("id_utente")));
			tvIdUtente.setTextColor(applicationContext.getResources().getColor(
					R.color.colorRed));
			tvIdUtente.setTextSize(15);

			tvCognomeNome.setText(json_data.getString("cognome") + " "
					+ json_data.getString("nome"));
			tvCognomeNome.setTextColor(applicationContext.getResources()
					.getColor(R.color.colorBlack));
			tvCognomeNome.setTextSize(15);

			tvEmail.setText(json_data.getString("email"));
			tvEmail.setTextColor(applicationContext.getResources().getColor(
					R.color.colorBlack));
			tvEmail.setTextSize(15);

			ArrayList<String> ruoliUtente = listUserRoles(json_data
					.getLong("id_utente"));

			TextView tvNoRoles = (TextView) layoutRoles
					.findViewById(R.id.tvNoRoles);
			if (ruoliUtente.size() == 0) {
				tvNoRoles.setVisibility(TextView.VISIBLE);
			} else {
				tvNoRoles.setVisibility(TextView.GONE);
				for (String ruolo : ruoliUtente) {
					if (ruolo.equals("AMMINISTRATORE")) {
						ibAdmin.setVisibility(ImageButton.VISIBLE);
						ibAdmin.setTag(json_data.getLong("id_utente"));
					}
					if (ruolo.equals("ATA")) {
						ibAta.setVisibility(ImageButton.VISIBLE);
						ibAta.setTag(json_data.getLong("id_utente"));
					}
					if (ruolo.equals("INSEGNANTE")) {
						ibProf.setVisibility(ImageButton.VISIBLE);
						ibProf.setTag(json_data.getLong("id_utente"));
					}
					if (ruolo.equals("SEGRETERIA")) {
						ibSegr.setVisibility(ImageButton.VISIBLE);
						ibSegr.setTag(json_data.getLong("id_utente"));
					}
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Ricava la posizione del ruolo indicato nello spinner
	 * 
	 * @param spinner
	 * @param id_ruolo
	 * @return
	 */
	protected int getRoleIdPositionIntoSpinner(Spinner spinner, long id_ruolo) {
		// TODO Auto-generated method stub
		int count = spinner.getCount();
		for (int i = 0; i < count; i++) {
			View rowView = spinner.getAdapter().getView(i, null, spinner);
			TextView tvMyText = (TextView) rowView.findViewById(R.id.tvMyText);
			if ((Long) tvMyText.getTag() == id_ruolo) {
				return i;
			}

		}
		return 0;
	}

	/**
	 * Dal ruolo ricava l'id interrogando il database
	 * 
	 * @param ruolo
	 * @return
	 */
	protected long getIdRuolo(String ruolo) {
		// TODO Auto-generated method stub
		DatabaseOps dataBaseOps = new DatabaseOps(getApplicationContext());
		return dataBaseOps.getIdRuolo(getApplicationContext(), ruolo);
	}

	/**
	 * Ricava la lista dei ruoli ricoperti dall'utente
	 * 
	 * @param id_utente
	 * @return
	 */
	private ArrayList<String> listUserRoles(long id_utente) {
		// TODO Auto-generated method stub
		DatabaseOps dataBaseOps = new DatabaseOps(getApplicationContext());
		return dataBaseOps.listUserRoles(getApplicationContext(), id_utente);
	}

	/**
	 * Ricava la posizione dell'utente indicato nel relativo spinner.
	 * 
	 * @param spinner
	 * @param id
	 * @return
	 */
	protected int getUserIdPositionIntoSpinner(Spinner spinner, long id) {
		// TODO Auto-generated method stub
		int count = spinner.getCount();
		for (int i = 0; i < count; i++) {
			View rowView = spinner.getAdapter().getView(i, null, spinner);
			TextView tvMyText = (TextView) rowView.findViewById(R.id.tvMyText);
			if ((Long) tvMyText.getTag() == id) {
				return i;
			}

		}
		return 0;
	}

	/**
	 * Aggiunge la riga con i nomi dei campi nella tabella di intestazione.
	 * 
	 * @param context
	 * @param table
	 * @param height
	 * @return
	 */
	@SuppressLint("NewApi")
	protected TableLayout addRowToTable(Context context, TableLayout table,
			int height) {
		TableRow tr = new TableRow(AssegnamentoRuoliActivity.this);

		tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));

		if (height != LayoutParams.WRAP_CONTENT)
			tr.setVisibility(View.INVISIBLE);

		TextView b6 = new TextView(AssegnamentoRuoliActivity.this);
		b6.setText("Id");
		b6.setTextColor(Color.WHITE);
		b6.setTextSize(15);
		// b6.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tr.addView(b6);

		TextView b19 = new TextView(AssegnamentoRuoliActivity.this);
		b19.setPadding(10, 0, 0, 0);
		b19.setTextSize(15);
		b19.setText("Cognome & Nome");
		b19.setTextColor(Color.WHITE);
		// b19.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tr.addView(b19);

		TextView b20 = new TextView(AssegnamentoRuoliActivity.this);
		b20.setPadding(10, 0, 0, 0);
		b20.setTextSize(15);
		b20.setText("Email");
		b20.setTextColor(Color.WHITE);
		// b20.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tr.addView(b20);

		TextView b29 = new TextView(AssegnamentoRuoliActivity.this);
		b29.setPadding(10, 0, 0, 0);
		b29.setText("Ruoli Utente");
		b29.setTextColor(Color.WHITE);
		b29.setTextSize(15);
		// b29.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

		tr.addView(b29);
		table.addView(tr);

		return table;

	}

}
