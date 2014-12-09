package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.interfaces.AlertMagnatic;
import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface.ColumnInfo;
import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface.DatabaseTablesInfo;
import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface.TableColumnsInfo;
import it.keyorchestra.registrowebapp.scuola.util.ExpandableListAdapter;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;
import it.keyorchestra.registrowebapp.scuola.util.MyDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

public class AdminTables extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnItemSelectedListener {
	private ArrayList<String> arrTblNames = null;
	private ArrayList<TableColumnsInfo> tColInfoArray = null;

	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	// HashMap<String, TableLayout> listTableChild;

	FrameLayout flNewTable;
	Button bCreateTable, bDropTable, bAddField, bRemoveField, bCancel, bBrowse,
			bCreateIndex, bCreateConstraint;
	ImageButton ibUndo;
	CheckBox cbNotNull, cbPK;
	EditText etName, etType, etDefault, etTableName;
	SharedPreferences getPrefs;
	private Spinner spinnerType;
	TextView tvNumTables, tvNumTypes;

	private SQLiteDatabase ourDatabase;
	private String dbName;
	private ArrayList<String> dataTypesList = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin_tables);

		Bundle basket = getIntent().getExtras();
		dbName = basket.getString("dbName");

		tvNumTables = (TextView) findViewById(R.id.tvNumTables);
		tvNumTypes = (TextView) findViewById(R.id.tvNumTypes);

		getPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		flNewTable = (FrameLayout) findViewById(R.id.flCreateTable);

		bCreateTable = (Button) flNewTable.findViewById(R.id.bCreateTable);
		bCreateTable.setOnClickListener(this);

		bDropTable = (Button) flNewTable.findViewById(R.id.bDropTable);
		bDropTable.setOnClickListener(this);

		bAddField = (Button) flNewTable.findViewById(R.id.bAddField);
		bAddField.setOnClickListener(this);

		bRemoveField = (Button) flNewTable.findViewById(R.id.bRemoveField);
		bRemoveField.setOnClickListener(this);

		bCancel = (Button) flNewTable.findViewById(R.id.bCancelOperation);
		bCancel.setOnClickListener(this);

		bBrowse = (Button) flNewTable.findViewById(R.id.bBrowseTable);
		bBrowse.setOnClickListener(this);

		bCreateIndex = (Button) flNewTable.findViewById(R.id.bCreateIndexes);
		bCreateIndex.setOnClickListener(this);

		bCreateConstraint = (Button) flNewTable
				.findViewById(R.id.bCreateConstraints);
		bCreateConstraint.setOnClickListener(this);

		ibUndo = (ImageButton) flNewTable.findViewById(R.id.ibUndo);
		ibUndo.setOnClickListener(this);

		cbNotNull = (CheckBox) flNewTable.findViewById(R.id.cbFieldNotnull);
		cbNotNull.setOnCheckedChangeListener(this);

		cbPK = (CheckBox) flNewTable.findViewById(R.id.cbFieldPK);
		cbPK.setOnCheckedChangeListener(this);

		etName = (EditText) flNewTable.findViewById(R.id.etFieldName);
		etName.setHorizontallyScrolling(true);

		spinnerType = (Spinner) findViewById(R.id.spinnerType);
		spinnerType.setOnItemSelectedListener(this);

		etType = (EditText) flNewTable.findViewById(R.id.etFieldType);
		etType.setHorizontallyScrolling(true);

		etTableName = (EditText) flNewTable.findViewById(R.id.etTableName);
		etTableName.setHorizontallyScrolling(true);

		etDefault = (EditText) flNewTable.findViewById(R.id.etFieldsDefault);
		etDefault.setHorizontallyScrolling(true);

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.expandableListViewTables);

		// preparing list data
		restoreExpandibleListOfTables();
		loadSpinnerData();

		expListView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// Toast.makeText(
				// getApplicationContext(),
				// "onScrollStateChanged  scrollState:" + scrollState
				// + " View:" + view, Toast.LENGTH_SHORT).show();
				// if (scrollState == 1)
				// expListView.setBackgroundResource(R.drawable.table);
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				// Toast.makeText(
				// getApplicationContext(),
				// "onScroll  totalItemCount:" + totalItemCount + " View:"
				// + view, Toast.LENGTH_SHORT).show();
			}
		});
		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				etTableName.setText(listDataHeader.get(groupPosition));
				return false;
			}

		});
		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// Toast.makeText(getApplicationContext(),
				// listDataHeader.get(groupPosition) + " Expanded",
				// Toast.LENGTH_SHORT).show();
			}
		});
		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// Toast.makeText(getApplicationContext(),
				// listDataHeader.get(groupPosition) + " Collapsed",
				// Toast.LENGTH_SHORT).show();

			}
		});

		/**
		 * CALLED WHEN A FIELD TYPE IS CLICKED INSIDE THE TABLES EXPANDIBLE LIST
		 */
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();

				String fieldInfo = listDataChild.get(
						listDataHeader.get(groupPosition)).get(childPosition);

				etName.setText(getInfoField(fieldInfo, 0));

				etType.setText(getInfoField(fieldInfo, 1));

				boolean notNullBool = (getInfoField(fieldInfo, 2).equals("1")) ? true
						: false;
				cbNotNull.setChecked(notNullBool);

				etDefault.setText(getInfoField(fieldInfo, 3));

				boolean pk = (getInfoField(fieldInfo, 4).equals("1")) ? true
						: false;
				cbPK.setChecked(pk);

				String cheese = "";
				try {
					@SuppressWarnings("rawtypes")
					Class ourClass;
					String path = "it.keyorchestra.registrowebapp.";
					if (cheese.equals("SQLLiteExample")
							|| cheese.equals("ScuolaActivity")) {
						path += "sqllite.";
					} else if (cheese.equals("UtentiScuola")) {
						path += "scuola.";
					}
					ourClass = Class.forName(path + cheese);
					Intent ourIntent = new Intent(AdminTables.this, ourClass);
					startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

		});

	}

	/**
	 * Function to load the spinner data from SQLite database
	 * */
	private void loadSpinnerData() {
		// Creating adapter for spinner
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_row, getDataTypesList());

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinnerType.setAdapter(dataAdapter);
	}

	/**
	 * CARICA I TIPI DI DATI USATI NEL DATABASE UNIQUE VALUES { "INTEGER",
	 * "BOOL", "REAL", "DOUBLE", "FLOAT", "CHAR", "TEXT", "VARCHAR", "BLOB",
	 * "NUMERIC", "DATETIME", ... };
	 * 
	 * EXECUTE ONLY FIRST TIME FUNCTION IS CALLED
	 * 
	 * @return
	 */
	private ArrayList<String> getDataTypesList() {
		// TODO Auto-generated method stub
		if (dataTypesList == null) {
			dataTypesList = new ArrayList<String>();
			loadStandardTypes();
			if (!(tColInfoArray == null)) {
				int size = tColInfoArray.size();// INFORMAZIONI SULLE COLONNE
												// DELLE
				// TABELLE
				for (int j = 0; j < size; j++) {
					TableColumnsInfo info = tColInfoArray.get(j);
					ArrayList<ColumnInfo> infoColumns = info.getColInfoArray();
					for (int i = 0; i < infoColumns.size(); i++) {
						ColumnInfo column = infoColumns.get(i);
						if (!dataTypesList.contains(column.getType()))
							dataTypesList.add(column.getType().trim());
					}
				}
			}
		}
		tvNumTypes.setText(String.valueOf(dataTypesList.size()));
		return dataTypesList;
	}

	private void loadStandardTypes() {
		// TODO Auto-generated method stub
		String[] standards = { "INTEGER", "BOOL", "REAL", "DOUBLE", "FLOAT",
				"CHAR", "TEXT", "VARCHAR", "BLOB", "NUMERIC", "DATETIME" };
		for (int s = 0; s < standards.length; s++) {
			dataTypesList.add(standards[s]);
		}
	}

	private CharSequence getInfoField(String childText, int position) {
		// TODO Auto-generated method stub
		String[] splitted = childText.split(";");
		return splitted[position];
	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {

		// ArrayList<String> arrTblNames = null;
		// ArrayList<TableColumnsInfo> tColInfoArray = null;

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		// listTableChild = new HashMap<String, TableLayout>();

		DatabaseTablesInfo info = getDatabaseTablesInfo();
		if (info != null) {
			dbName = info.getDbName();
			// SET TITLE
			setTitle("Database: " + dbName);

			arrTblNames = info.getArrTblNames();

			tvNumTables.setText(String.valueOf(arrTblNames.size()));
			tColInfoArray = info.gettColInfoArray();

			// Adding child data
			if (arrTblNames.size() > 0) {
				for (int j = 0; j < arrTblNames.size(); j++) {
					TableColumnsInfo colInfo = tColInfoArray.get(j);

					listDataHeader.add(arrTblNames.get(j));// Nome della tabella
															// in posizione j

					List<String> columns = new ArrayList<String>();

					ArrayList<ColumnInfo> infoRow = colInfo.getColInfoArray();
					String s = "NAME;TYPE;NOT NULL;DEFAULT;PK";
					// columns.add(s);
					for (int k = 0; k < infoRow.size(); k++) {
						s = infoRow.get(k).getName() + ";"
								+ infoRow.get(k).getType() + ";"
								+ infoRow.get(k).getNotNull() + ";"
								+ infoRow.get(k).getDfltValue() + ";"
								+ infoRow.get(k).getPrimaryKey();
						columns.add(s);
						// tl.addView(anOthereConvertView(s));
					}
					listDataChild.put(arrTblNames.get(j), columns); // Header,
																	// Child
																	// listTableChild.put(arrTblNames.get(j),
																	// tl);

				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	private DatabaseTablesInfo getDatabaseTablesInfo() {
		// TODO Auto-generated method stub
		ScuolaDb db1 = null;

		if (dbName.length() == 0) {
			Toast.makeText(getApplicationContext(), "Error: DB_NAME missing!",
					Toast.LENGTH_SHORT).show();
			return null;
		}

		try {
			ourDatabase = openOrCreateDatabase(dbName, MODE_WORLD_READABLE,
					null);
			String path = ourDatabase.getPath();
			Toast.makeText(getApplicationContext(),
					"Database: '" + dbName + "' " + "Path:" + path,
					Toast.LENGTH_SHORT).show();

			db1 = new ScuolaDb(this);
			db1.open(dbName);
			arrTblNames = db1.getTablesNames(ourDatabase);
			tColInfoArray = db1.getColumnsInfo(arrTblNames);
			db1.close();
			// }
		} catch (SQLException ex) {
			Toast.makeText(
					getApplicationContext(),
					"Database: '" + dbName + "' " + "Error: " + ex.getMessage(),
					Toast.LENGTH_SHORT).show();
			return null;
		} finally {
		}
		return new DatabaseTablesInfo(dbName, arrTblNames, tColInfoArray);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		// TODO Auto-generated method stub
		String field = null;
		switch (buttonView.getId()) {
		case R.id.cbFieldNotnull:
			field = "cbFieldNotnull";
			break;
		case R.id.cbFieldPK:
			field = "cbFieldPK";
			break;
		default:
			break;
		}
		Toast.makeText(
				getApplicationContext(),
				"onCheckedChanged: id:'" + buttonView.getId() + "' Field:"
						+ field + " isChecked:" + String.valueOf(isChecked),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String field = null;
		switch (v.getId()) {
		case R.id.bCreateTable:
			field = "bCreateTable";
			if (FieldsValidator.Is_Valid_Database_Table_Name(etTableName)
					|| FieldsValidator.Is_Valid_Database_Field_Name(etName)) {

				try {
					ScuolaDb db1 = new ScuolaDb(this);
					db1.open(dbName);
					db1.createTableWithPrimaryKey(dbName, etTableName, etName,
							etType);
					db1.close();
				} catch (SQLException ex) {
					Toast.makeText(getApplicationContext(),
							"Create Table ERROR! : " + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				} finally {
					restoreExpandibleListOfTables();
				}
			}
			break;
		case R.id.bDropTable:
			field = "bDropTable";

			if (FieldsValidator.Is_Valid_Database_Table_Name(etTableName)) {

				final ScuolaDb db1 = new ScuolaDb(this);

				MyDialogs.getConfirmDialog(this, "DROP TABLE",
						"Vuoi cancellare la tabella: '"
								+ etTableName.getText().toString() + "' ?",
						"Ok", "No", true, R.drawable.tables_list_ok,
						new AlertMagnatic() {

							@Override
							public void PositiveMethod(DialogInterface dialog,
									int id) {
								// TODO Auto-generated method stub
								try {
									db1.open(dbName);
									db1.dropTable(dbName, etTableName);
									db1.close();
								} catch (SQLException ex) {
									Toast.makeText(
											getApplicationContext(),
											"Drop Table ERROR! : "
													+ ex.getMessage(),
											Toast.LENGTH_SHORT).show();
								} finally {
									db1.close();
									restoreExpandibleListOfTables();
								}
							}

							@Override
							public void NegativeMethod(DialogInterface dialog,
									int id) {
								// TODO Auto-generated method stub

							}
						});

			}
			break;
		case R.id.bAddField:
			// ALTER TABLE test1 ADD COLUMN bar TEXT;
			if (FieldsValidator.Is_Valid_Database_Table_Name(etTableName)
					|| FieldsValidator.Is_Valid_Database_Field_Name(etName)) {
				if (FieldsValidator.Is_Valid_Database_Table_Name(etTableName)) {

					try {
						ScuolaDb db1 = new ScuolaDb(this);
						db1.open(dbName);
						db1.addColumnToTable(dbName, etTableName, etName,
								etType, cbNotNull, etDefault, cbPK);
						db1.close();
					} catch (SQLException ex) {
						Toast.makeText(getApplicationContext(),
								"ADD COLUMN ERROR! : " + ex.getMessage(),
								Toast.LENGTH_SHORT).show();
					} finally {
						restoreExpandibleListOfTables();
					}
				}
			}
			field = "bAddField";
			break;
		case R.id.bRemoveField:
			// ALTER TABLE TABLENAME DROP COLUMN DIST_TYPE
			/**
			 * BEGIN TRANSACTION; CREATE TEMPORARY TABLE t1_backup(a,b); INSERT
			 * INTO t1_backup SELECT a,b FROM t1; DROP TABLE t1; CREATE TABLE
			 * t1(a,b); INSERT INTO t1 SELECT a,b FROM t1_backup; DROP TABLE
			 * t1_backup; COMMIT;
			 */
			if (FieldsValidator.Is_Valid_Database_Table_Name(etTableName)
					|| FieldsValidator.Is_Valid_Database_Field_Name(etName)) {

				try {
					ScuolaDb db1 = new ScuolaDb(this);
					db1.open(dbName);
					db1.removeColumnFromTable(dbName, etTableName, etName);
					db1.close();
				} catch (SQLException ex) {
					Toast.makeText(getApplicationContext(),
							"DROP COLUMN ERROR! : " + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				} finally {
					restoreExpandibleListOfTables();
				}

			}
			field = "bRemoveField";
			break;
		case R.id.bBrowseTable:
			field = "bBrowseTable";
			Bundle backPack = new Bundle();
			backPack.putString("sql", "SELECT * FROM "
					+ etTableName.getText().toString());
			backPack.putString("dbname", dbName);
			backPack.putString("tableName", etTableName.getText().toString());
			backPack.putStringArrayList("arrTblNames", arrTblNames);

			FieldsValidator.Is_Valid_Database_Table_Name(etTableName);
			Intent browseSQl = new Intent(
					"it.keyorchestra.registrowebapp.BROWSE_SQL");
			browseSQl.putExtras(backPack);

			startActivity(browseSQl);

			break;
		case R.id.bCreateIndexes:
			FieldsValidator.Is_Valid_Database_Table_Name(etTableName);
			field = "bCreateIndexes";
			break;
		case R.id.bCreateConstraints:
			FieldsValidator.Is_Valid_Database_Table_Name(etTableName);
			field = "bCreateConstraints";
			break;
		case R.id.bCancelOperation:
			field = "bCancelOperation";
			etTableName.setText("");
			etName.setText("");
			etType.setText("INTEGER");
			etDefault.setText("");
			cbNotNull.setChecked(true);
			cbPK.setChecked(false);
			break;
		case R.id.ibUndo:
			finish();
		default:
			break;
		}
		Toast.makeText(getApplicationContext(),
				"onClick: id:'" + v.getId() + " Field:" + field,
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * PREPARA LA LISTA DEI CAMPI DI OGNI TABELLA E RELATIVI PARAMETRI
	 */
	private void restoreExpandibleListOfTables() {
		// TODO Auto-generated method stub
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild, "AdminTables");

		// setting list adapter
		expListView.setAdapter(listAdapter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		String typeOfDataField = getPrefs.getString("typeOfField", "INTEGER");
		etType.setText("" + typeOfDataField);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.itemTypeOfField:
			Intent i = new Intent(
					"it.keyorchestra.registrowebapp.FIELD_TYPE_PREFS");

			startActivity(i);

			break;
		default:
			break;
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.create_table, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		String type;
		// TODO Auto-generated method stub
		switch (parent.getId()) {
		case R.id.spinnerType:
			type = spinnerType.getSelectedItem().toString();
			etType.setText(type);
			Toast.makeText(
					getBaseContext(),
					"onItemSelected: "
							+ spinnerType.getSelectedItem().toString(),
					Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
