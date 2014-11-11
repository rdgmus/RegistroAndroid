package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.interfaces.AlertMagnatic;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;
import it.keyorchestra.registrowebapp.scuola.util.MyDialogs;
import it.keyorchestra.registrowebapp.scuola.util.MySimpleArrayAdapter;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class AdminDatabases extends Activity implements OnClickListener,
		OnItemClickListener, AlertMagnatic {
	Button dropDatabase, createDatabase;
	Button clearName;
	Button bVuewTables;
	ImageButton ibDialogClose;
	EditText databaseName;
	ListView databasesList;
	private SQLiteDatabase ourDatabase;
	String path = null;
	AlertDialog.Builder builder;
	AlertDialog dialog;

	private boolean confirmDropDatabase = false;
	ArrayList<String> arrayListOfDatabases;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.admin_databases);

		clearName = (Button) findViewById(R.id.bClearDatabaseName);
		clearName.setOnClickListener(this);

		dropDatabase = (Button) findViewById(R.id.bDropDatabase);
		dropDatabase.setOnClickListener(this);

		createDatabase = (Button) findViewById(R.id.bCreateDatabase);
		createDatabase.setOnClickListener(this);

		bVuewTables = (Button) findViewById(R.id.bViewTables);
		bVuewTables.setOnClickListener(this);

		ibDialogClose = (ImageButton) findViewById(R.id.ibDialogClose);
		ibDialogClose.setOnClickListener(this);

		databaseName = (EditText) findViewById(R.id.etDatabaseName);

		databasesList = (ListView) findViewById(R.id.lvDatabasesList);
		// databasesList.setBackgroundColor(Color.YELLOW);
		databasesList.setOnItemClickListener(this);

		arrayListOfDatabases = new ArrayList<String>();
		addDatabasesToList();
		// Create The Adapter with passing ArrayList as 3rd parameter
		// ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, arrayListOfDatabases);

		MySimpleArrayAdapter arrayAdapter = new MySimpleArrayAdapter(this,
				arrayListOfDatabases);
		// Set The Adapter
		databasesList.setAdapter(arrayAdapter);
		databasesList.setOnItemClickListener(this);

	}

	private void addDatabasesToList() {
		// TODO Auto-generated method stub
		arrayListOfDatabases.clear();
		String[] dbList = getBaseContext().databaseList();
		if (dbList.length == 0)
			return;
		for (int i = 0; i < dbList.length; i++) {
			arrayListOfDatabases.add(dbList[i]);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		final String dbName = databaseName.getText().toString();

		String[] dbList = getBaseContext().databaseList();

		switch (v.getId()) {
		case R.id.ibDialogClose:
			finish();
			break;
		case R.id.bViewTables:
			// ArrayList<String> arrTblNames = null;
			// ArrayList<TableColumnsInfo> tColInfoArray = null;
			FieldsValidator.Is_Valid_Database_Name(databaseName);

			if (dbName.length() == 0) {
				Toast.makeText(getApplicationContext(),
						"Error: DB_NAME missing!", Toast.LENGTH_SHORT).show();
				break;
			}
			// GET TABLE NAMES TO CONTROL DATABASE EXISTS

			if (!databaseExistsIntoList(dbName, dbList)) {// SE NON ESISTE
															// CHIEDE SE PUO'
															// CREARLO EX
				// NOVO
				String msg = "Vuoi creare il nuovo database:' " + dbName
						+ "' ?";
				String title = "DATABASE DO NOT EXTSTS!";

				MyDialogs.getConfirmDialog(this, title, msg, "Ok", "No", true,
						R.drawable.database_list_ok, new AlertMagnatic() {

							@Override
							public void PositiveMethod(DialogInterface dialog,
									int id) {
								// TODO Auto-generated method stub
								ourDatabase = openOrCreateDatabase(dbName,
										MODE_WORLD_READABLE, null);
								path = ourDatabase.getPath();
								refreshDatabaseList();
								startActivityAdminTables(dbName);
							}

							@Override
							public void NegativeMethod(DialogInterface dialog,
									int id) {
								// TODO Auto-generated method stub
								Toast.makeText(
										getApplicationContext(),
										"Database: '" + dbName
												+ " do not exists!",
										Toast.LENGTH_SHORT).show();
							}
						});
			} else {// APRE LA LISTA TABELLE DEL DATABASE
				apriListaTabelleDatabase();
			}
			break;
		case R.id.bCreateDatabase:
			FieldsValidator.Is_Valid_Database_Name(databaseName);

			if (dbName.length() > 0) {
				ourDatabase = openOrCreateDatabase(dbName,
						MODE_WORLD_WRITEABLE, null);
				path = ourDatabase.getPath();
				refreshDatabaseList();

				Toast.makeText(
						getApplicationContext(),
						"b Create Database: '" + dbName + "' " + "Path:" + path,
						Toast.LENGTH_SHORT).show();

				startActivityAdminTables(dbName);
			} else
				Toast.makeText(getApplicationContext(),
						"Error: DB_NAME missing!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.bDropDatabase:
			if (databaseExistsIntoList(dbName, dbList)) {
				String msg = "Vuoi cancellare il database:'" + dbName + "' ?";
				String title = "DROP DATABASE";

				MyDialogs.getConfirmDialog(this, title, msg, "Ok", "No", true,
						R.drawable.database_list_ok, new AlertMagnatic() {

							@Override
							public void PositiveMethod(DialogInterface dialog,
									int id) {
								// TODO Auto-generated method stub
								setConfirmDropDatabase(getBaseContext()
										.deleteDatabase(dbName));
								refreshDatabaseList();
								databaseName.setText("");
							}

							@Override
							public void NegativeMethod(DialogInterface dialog,
									int id) {
								// TODO Auto-generated method stub
								setConfirmDropDatabase(false);
							}
						});
			} else {
				Toast.makeText(getApplicationContext(),
						"Error: DATABASE DO NO EXISTS!", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.bClearDatabaseName:
			databaseName.setText("");
			break;
		default:
			break;
		}
	}

	private void apriListaTabelleDatabase() {
		// TODO Auto-generated method stub
		String dbName = databaseName.getText().toString();
		ourDatabase = openOrCreateDatabase(dbName, MODE_WORLD_READABLE, null);
		path = ourDatabase.getPath();
		refreshDatabaseList();
		startActivityAdminTables(dbName);
	}

	private boolean databaseExistsIntoList(String dbName, String[] dbList) {
		// TODO Auto-generated method stub
		boolean databaseExists = false;
		if (dbList.length > 0) {// CONTROLLA SE ESITE IL DATABASE
			for (int i = 0; i < dbList.length; i++) {
				databaseExists = dbList[i].equals(dbName);
				if (databaseExists)
					break;
			}
		}
		return databaseExists;
	}

	private void refreshDatabaseList() {
		// TODO Auto-generated method stub
		arrayListOfDatabases = new ArrayList<String>();
		addDatabasesToList();

		MySimpleArrayAdapter arrayAdapter = new MySimpleArrayAdapter(this,
				arrayListOfDatabases);
		// Set The Adapter
		databasesList.setAdapter(arrayAdapter);
		databasesList.setOnItemClickListener(this);
	}

	/**
	 * Lancia l'attivitˆ per l'amministrazione delle tabelle e relative colonne
	 * 
	 * @param dbName
	 */
	private void startActivityAdminTables(String dbName) {
		// TODO Auto-generated method stub

		// DatabaseTablesInfo info = new DatabaseTablesInfo(dbName, arrTblNames,
		// tColInfoArray);
		Bundle backPack = new Bundle();
		backPack.putString("dbName", dbName);

		Intent adminTable = new Intent(
				"it.keyorchestra.registrowebapp.ADMIN_TABLES");
		try {
			adminTable.putExtras(backPack);
		} catch (Exception ex) {
			Toast.makeText(
					getApplicationContext(),
					"startActivityAdminTables: '" + dbName + "' " + "Error: "
							+ ex.getMessage(), Toast.LENGTH_SHORT).show();
		}
		startActivity(adminTable);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
		// TODO Auto-generated method stub
		LinearLayout ll = (LinearLayout) view;
		TextView textView = (TextView) ll.findViewById(R.id.tvMyText);
		String item = textView.getText().toString();
		databaseName.setText(item);
		// apriListaTabelleDatabase();
		// Toast.makeText(getApplicationContext(), "Database: " + item,
		// Toast.LENGTH_SHORT).show();
	}

	public boolean isConfirmDropDatabase() {
		return confirmDropDatabase;
	}

	public void setConfirmDropDatabase(boolean confirmDropDatabase) {
		this.confirmDropDatabase = confirmDropDatabase;
	}

	@Override
	public void PositiveMethod(DialogInterface dialog, int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void NegativeMethod(DialogInterface dialog, int id) {
		// TODO Auto-generated method stub

	}

}
