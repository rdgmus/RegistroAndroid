package it.keyorchestra.registrowebapp.scuola;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.ScuolaDb;
import it.keyorchestra.registrowebapp.scuola.tables.UtentiScuolaTable;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class UtentiScuola extends Activity implements OnClickListener {

	EditText etID, etNAME, etSURNAME, etEMAIL, etPASSWORD, etID_RUOLO;
	// CRUD FRAGMENT
	Button bCrudClear, bCrudView, bCrudUpdate, bCrudDelete, bCrudCreate;
	CheckBox cbViewPassword;

	// TITLE FRAGMENT
	TextView tvTableTitle, tvTableMessage;
	Button bTableAdmin, bTableEnd;

	// FIELDS
	FrameLayout flId, flCognome, flNome, flEmail, flPassword, flIdRuolo;
	TextView tvID, tvNAME, tvSURNAME, tvEMAIL, tvPASSWORD, tvID_RUOLO;

	private ArrayList<String> datiUtente;

	private long id_utente;
	private String name;
	private String surname;
	private String email;
	private String password;
	private long id_ruolo;

	boolean allOk = true;
	private long numRows;
	private SQLiteDatabase ourDatabase;
	private ScuolaDb db;
	private String dbName = "registro";

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bTableEnd:
			Intent retTablesMenu = new Intent(
					"it.keyorchestra.registrowebapp.LIST_ACTIVITY");
			startActivity(retTablesMenu);
			finish();
			break;
		case R.id.cbViewPassword:
			if (cbViewPassword.isChecked())
				etPASSWORD
						.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			else
				etPASSWORD.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			break;
		case R.id.bTableAdmin:
			Intent adminDatabases = new Intent(
					"android.intent.action.ADMIN_DATABASES");
			startActivity(adminDatabases);
			break;
		case R.id.bCrudView:
			// Intent i = new
			// Intent("android.intent.action.UTENTI_SCUOLA_VIEW");
			// startActivityForResult(i, 0);
			Bundle backPack = new Bundle();
			backPack.putString("sql", "SELECT * FROM "
					+ tvTableTitle.getText().toString());
			backPack.putString("dbname", dbName);
			backPack.putString("tableName", ScuolaDb.TABLE_UTENTI_SCUOLA);
			Intent browseSQl = new Intent(
					"it.keyorchestra.registrowebapp.BROWSE_SQL");
			browseSQl.putExtras(backPack);

			startActivity(browseSQl);
			break;
		case R.id.bCrudCreate:
			allOk = true;
			numRows = 0;
			try {
				ArrayList<String> listFieldValues = new ArrayList<String>();
				listFieldValues.add("0");
				listFieldValues.add(etSURNAME.getText().toString());
				listFieldValues.add(etNAME.getText().toString());
				listFieldValues.add(etEMAIL.getText().toString());
				listFieldValues.add(etPASSWORD.getText().toString());
				listFieldValues.add(etID_RUOLO.getText().toString());

				if (!checkFieldsFormat()) {
					allOk = false;
					return;
				}
				db = new ScuolaDb(this);
				db.open(dbName);

				numRows = db.createEntry(ScuolaDb.TABLE_UTENTI_SCUOLA,
						UtentiScuolaTable.listFieldNames(), listFieldValues);
				ourDatabase.close();
			} catch (SQLException e) {
				// TODO: handle exception
				allOk = false;
				// DIALOG
				showMessage("Crea in utenti_scuola:\n",
						"Error!: " + e.getMessage());
			} finally {
				if (allOk) {
					// DIALOG
					showMessage("Crea in utenti_scuola:\n",
							"Success! id_utente = " + numRows);
					backPack = new Bundle();
					backPack.putString("sql", "SELECT * FROM "
							+ tvTableTitle.getText().toString());
					backPack.putString("dbname", dbName);
					backPack.putString("tableName",
							ScuolaDb.TABLE_UTENTI_SCUOLA);
					browseSQl = new Intent(
							"it.keyorchestra.registrowebapp.BROWSE_SQL");
					browseSQl.putExtras(backPack);

					startActivity(browseSQl);
				}
			}
			break;
		case R.id.bCrudUpdate:
			try {
				id_utente = Long.parseLong(etID.getText().toString());
			} catch (Exception numex) {
				// DIALOG
				showMessage("Utente Update:\n",
						"Failure!:" + numex.getMessage());
				break;
			}

			setName(etNAME.getText().toString());
			setSurname(etSURNAME.getText().toString());
			setEmail(etEMAIL.getText().toString());
			setPassword(etPASSWORD.getText().toString());
			setId_ruolo(Long.parseLong(etID_RUOLO.getText().toString()));

			ArrayList<String> listFieldValues = new ArrayList<String>();
			listFieldValues.add(etID.getText().toString());
			listFieldValues.add(etSURNAME.getText().toString());
			listFieldValues.add(etNAME.getText().toString());
			listFieldValues.add(etEMAIL.getText().toString());
			listFieldValues.add(etPASSWORD.getText().toString());
			listFieldValues.add(etID_RUOLO.getText().toString());

			try {
				db = new ScuolaDb(this);
				db.open(dbName);

				numRows = db.updateEntry(ScuolaDb.TABLE_UTENTI_SCUOLA,
						UtentiScuolaTable.listFieldNames(), listFieldValues);

				// numRows =
				// ourDatabase.updateEntry(ScuolaDb.TABLE_UTENTI_SCUOLA,
				// id_utente, surname, name, email, password);
				ourDatabase.close();
			} catch (SQLException ex) {
				// DIALOG
				showMessage("Utente Update:\n", "Failure!:" + ex.getMessage());
				break;
			} finally {
				// DIALOG
				showMessage("Utente Update:\n", "Success! righe:" + numRows);
			}
			break;
		case R.id.bCrudDelete:
			try {
				id_utente = Long.parseLong(etID.getText().toString());
			} catch (Exception numex) {
				// DIALOG
				showMessage("Utente Delete:\n",
						"Failure!:" + numex.getMessage());
				break;
			}
			try {
				db = new ScuolaDb(this);
				db.open(dbName);
				numRows = db.deleteEntry(ScuolaDb.TABLE_UTENTI_SCUOLA,
						id_utente);
				ourDatabase.close();
			} catch (SQLException ex) {
				// DIALOG
				showMessage("Utente Delete:\n", "Failure!:" + ex.getMessage());
				break;
			} finally {
				// DIALOG
				showMessage("Utente Delete:\n", "Success! righe:" + numRows);
				clearFields();
			}
			break;
		case R.id.bCrudClear:
			clearFields();
			break;
		default:
			break;
		}
	}

	private void showMessage(String title, String msg) {
		// TODO Auto-generated method stub
		tvTableMessage.setText(title + msg);
	}

	private boolean checkFieldsFormat() {
		// TODO Auto-generated method stub
		return (FieldsValidator.Is_Valid_Person_Name(etSURNAME)
				&& FieldsValidator.Is_Valid_Person_Name(etNAME)
				&& FieldsValidator.Is_Valid_Email(etEMAIL) && FieldsValidator
					.Is_Valid_Password(etPASSWORD));

	}

	private void clearFields() {
		// TODO Auto-generated method stub
		etID.setText("");
		etSURNAME.setText("");
		etNAME.setText("");
		etEMAIL.setText("");
		etPASSWORD.setText("");
		etID_RUOLO.setText("");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.utentiscuola);

		ourDatabase = openOrCreateDatabase(dbName, MODE_WORLD_WRITEABLE, null);

		// CRUD PANEL
		bCrudClear = (Button) findViewById(R.id.bCrudClear);
		bCrudView = (Button) findViewById(R.id.bCrudView);
		bCrudCreate = (Button) findViewById(R.id.bCrudCreate);
		bCrudDelete = (Button) findViewById(R.id.bCrudDelete);
		bCrudUpdate = (Button) findViewById(R.id.bCrudUpdate);

		bCrudClear.setOnClickListener(this);
		bCrudView.setOnClickListener(this);
		bCrudCreate.setOnClickListener(this);
		bCrudDelete.setOnClickListener(this);
		bCrudUpdate.setOnClickListener(this);

		// TITLE PANEL
		tvTableTitle = (TextView) findViewById(R.id.tvTableTitle);
		tvTableMessage = (TextView) findViewById(R.id.tvTableMessage);
		bTableAdmin = (Button) findViewById(R.id.bTableAdmin);
		bTableEnd = (Button) findViewById(R.id.bTableEnd);

		bTableAdmin.setOnClickListener(this);
		bTableEnd.setOnClickListener(this);

		// FIELDS O FRAME LAYOUT DEI CAMPI INCLUSI NELL'ACTIVITY
		flId = (FrameLayout) findViewById(R.id.fieldId);
		flCognome = (FrameLayout) findViewById(R.id.fieldCognome);
		flNome = (FrameLayout) findViewById(R.id.fieldNome);
		flEmail = (FrameLayout) findViewById(R.id.fieldEmail);
		flPassword = (FrameLayout) findViewById(R.id.fieldPassword);
		flIdRuolo = (FrameLayout) findViewById(R.id.fieldIdRuolo);

		// EDITTEXT DEI CAMPI RIFERITI ALLE INFORMAZIONI DEI CAMPI
		etID = (EditText) flId.findViewById(R.id.etFieldValue);
		etID.setFocusableInTouchMode(false);

		etSURNAME = (EditText) flCognome.findViewById(R.id.etFieldValue);

		etNAME = (EditText) flNome.findViewById(R.id.etFieldValue);

		etEMAIL = (EditText) flEmail.findViewById(R.id.etFieldValue);
		etEMAIL.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

		etPASSWORD = (EditText) flPassword.findViewById(R.id.etFieldValue);
		etPASSWORD.setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_PASSWORD);

		etID_RUOLO = (EditText) flIdRuolo.findViewById(R.id.etFieldValue);

		// TEXTVIEW CONTENENTI I NOMI STESSI DEI CAMPI DELLA TABELLA
		tvID = (TextView) flId.findViewById(R.id.tvFieldName);
		tvID.setText(UtentiScuolaTable.KEY_ID_UTENTE);

		tvSURNAME = (TextView) flCognome.findViewById(R.id.tvFieldName);
		tvSURNAME.setText(UtentiScuolaTable.KEY_COGNOME);

		tvNAME = (TextView) flNome.findViewById(R.id.tvFieldName);
		tvNAME.setText(UtentiScuolaTable.KEY_NOME);

		tvEMAIL = (TextView) flEmail.findViewById(R.id.tvFieldName);
		tvEMAIL.setText(UtentiScuolaTable.KEY_EMAIL);

		tvPASSWORD = (TextView) flPassword.findViewById(R.id.tvFieldName);
		tvPASSWORD.setText(UtentiScuolaTable.KEY_PASSWORD);

		tvID_RUOLO = (TextView) flIdRuolo.findViewById(R.id.tvFieldName);
		tvID_RUOLO.setText(UtentiScuolaTable.KEY_ID_RUOLO);

		cbViewPassword = (CheckBox) flPassword
				.findViewById(R.id.cbViewPassword);

		cbViewPassword.setOnClickListener(this);
		cbViewPassword.setVisibility(1);

		setTableTitle(ScuolaDb.TABLE_UTENTI_SCUOLA);

		setDataIfExistsRow();
	}

	/**
	 * SE GLI EXTRAS CONTENGONO UN ID_ROW LA RIGA VIENE CARICATA DAL DATABASE
	 * NEGLI APPOSITI CAMPI
	 */
	private void setDataIfExistsRow() {
		// TODO Auto-generated method stub
		datiUtente = null;
		Bundle basket = getIntent().getExtras();

		long id_utente;
		try {
			id_utente = basket.getLong("id_row");
		} catch (NullPointerException nex) {
			return;
		}
		try {

			db = new ScuolaDb(this);
			db.open(dbName);
			datiUtente = db.getRow(ScuolaDb.TABLE_UTENTI_SCUOLA, id_utente);
			ourDatabase.close();
		} catch (SQLException ex) {
			// DIALOG
			Dialog d = new Dialog(this);
			d.setTitle("Utente Selected");

			TextView tv = new TextView(this);
			tv.setBackgroundColor(Color.WHITE);
			tv.setText("Failure!:" + ex.getMessage());
			d.setContentView(tv);
			d.show();
			return;
		}
		if (datiUtente != null) {
			etID.setText(datiUtente.get(0));
			etSURNAME.setText(datiUtente.get(1));
			etNAME.setText(datiUtente.get(2));
			etEMAIL.setText(datiUtente.get(3));
			etPASSWORD.setText(datiUtente.get(4));
			etID_RUOLO.setText(datiUtente.get(5));
		}

	}

	private void setTableTitle(String tableTitle) {
		// TODO Auto-generated method stub
		setTitle(tableTitle);
		tvTableTitle.setText(tableTitle);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId_ruolo() {
		return id_ruolo;
	}

	public void setId_ruolo(long id_ruolo) {
		this.id_ruolo = id_ruolo;
	}

}
