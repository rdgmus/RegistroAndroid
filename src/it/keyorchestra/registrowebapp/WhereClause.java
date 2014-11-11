package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.scuola.util.DatabaseFieldInfo;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

public class WhereClause extends Activity implements OnItemSelectedListener,
		OnClickListener {
	private Spinner spinColumnNames, spinComparisons, spinUniqueValues,
			spinTables, spinOtherTable, spinOtherColumn, spinOperators;
	private ArrayList<String> columnNames;

	private ArrayList<DatabaseFieldInfo> arrTblSelection;
	private String tableName, dbname, fieldName, comparison, uniqueValue,
			otherTableName, otherFieldName;

	private final String[] comparisonExp = { "=", "==", "<", "<=", ">", ">=",
			"!=", "<>", "IN", "NOT IN", "BETWEEN", "IS", "IS NOT" };
	private final String[] operators = { "+", "-", "*", "/", "%", "<<", ">>",
			"&", "|" };
	private List<String> uniqueValues = null;

	private SQLiteDatabase ourDatabase;
	private ScuolaDb db;
	private Button bAcceptConstant, bAcceptComparison, bAcceptOtherColumn,
			bAcceptOperator, bAND, bOR;
	private ImageButton ibCancel, ibAcceptWhereClause;
	private EditText etRValue, etWhereClause;
	private ArrayList<String> arrTblNames;
	private String whereClause, choosenOperator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_where_clause);

		Bundle basket = getIntent().getExtras();
		setColumnNames(basket.getStringArrayList("columnNames"));
		dbname = basket.getString("dbname");
		tableName = basket.getString("tableName");
		whereClause = basket.getString("whereClause");
		arrTblNames = basket.getStringArrayList("arrTblNames");

		ourDatabase = openOrCreateDatabase(dbname, MODE_WORLD_WRITEABLE, null);

		spinTables = (Spinner) findViewById(R.id.spinTables);
		spinTables.setOnItemSelectedListener(this);
		loadSpinTables();

		spinColumnNames = (Spinner) findViewById(R.id.spinColumnNames);
		spinColumnNames.setOnItemSelectedListener(this);

		spinComparisons = (Spinner) findViewById(R.id.spinComparisons);
		spinComparisons.setOnItemSelectedListener(this);
		loadSpinComparisonsData();

		spinOperators = (Spinner) findViewById(R.id.spinOperators);
		spinOperators.setOnItemSelectedListener(this);
		loadSpinOperatorsData();

		spinUniqueValues = (Spinner) findViewById(R.id.spinUniqueValues);
		spinUniqueValues.setOnItemSelectedListener(this);

		spinOtherTable = (Spinner) findViewById(R.id.spinOtherTable);
		spinOtherTable.setOnItemSelectedListener(this);
		loadSpinOtherTables();

		spinOtherColumn = (Spinner) findViewById(R.id.spinOtherColumn);
		spinOtherColumn.setOnItemSelectedListener(this);

		bAcceptConstant = (Button) findViewById(R.id.bAcceptConstant);
		bAcceptConstant.setOnClickListener(this);
		bAcceptComparison = (Button) findViewById(R.id.bAcceptComparison);
		bAcceptComparison.setOnClickListener(this);
		bAcceptOtherColumn = (Button) findViewById(R.id.bAcceptOtherColumn);
		bAcceptOtherColumn.setOnClickListener(this);
		bAcceptOperator = (Button) findViewById(R.id.bAcceptOperator);
		bAcceptOperator.setOnClickListener(this);

		bAND = (Button) findViewById(R.id.bAND);
		bAND.setOnClickListener(this);
		bOR = (Button) findViewById(R.id.bOR);
		bOR.setOnClickListener(this);
		ibAcceptWhereClause = (ImageButton) findViewById(R.id.ibAcceptWhereClause);
		ibAcceptWhereClause.setOnClickListener(this);
		ibCancel = (ImageButton) findViewById(R.id.ibCancel);
		ibCancel.setOnClickListener(this);

		etRValue = (EditText) findViewById(R.id.etRValue);
		etRValue.setHorizontallyScrolling(true);
		etWhereClause = (EditText) findViewById(R.id.etWhereClause);
		etWhereClause.setText(whereClause);

		setTitle("WHERE clause : Database : " + dbname);
	}

	private void loadSpinOperatorsData() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_row, operators);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinOperators.setAdapter(dataAdapter);
	}

	private void loadSpinOtherTables() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_row, arrTblNames);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinOtherTable.setAdapter(dataAdapter);
		if (otherTableName != null)
			spinOtherTable.setSelection(arrTblNames.indexOf(otherTableName));
	}

	private void loadSpinTables() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_row, arrTblNames);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinTables.setAdapter(dataAdapter);
		if (tableName != null)
			spinTables.setSelection(arrTblNames.indexOf(tableName));
	}

	private void loadSpinUniqueValuesData() {
		// TODO Auto-generated method stub
		uniqueValues = getUniqueValuesFor(dbname, tableName, fieldName);

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_row, uniqueValues);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinUniqueValues.setAdapter(dataAdapter);
	}

	private void loadSpinComparisonsData() {
		// TODO Auto-generated method stub
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				R.layout.spinner_row, comparisonExp);

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinComparisons.setAdapter(dataAdapter);

	}

	/**
	 * RITORNA LA SELECT UNIQUE fieldname INTO tableName OF DATABASE dbname
	 * 
	 * @param dbname2
	 * @param tableName2
	 * @param fieldName2
	 * @return
	 */
	private List<String> getUniqueValuesFor(String dbname, String tableName,
			String fieldName) {
		// TODO Auto-generated method stub
		List<String> uniqueValues = null;
		try {
			db = new ScuolaDb(this);
			db.open(dbname);
			uniqueValues = db.getUniqueValuesFor(ourDatabase, dbname,
					tableName, fieldName);

		} catch (SQLException e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(),
					"SQLException: " + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		} finally {
			db.close();
		}
		return uniqueValues;
	}

	/**
	 * Functions to load the spinner data from SQLite database
	 * */
	private void loadOtherSpinColumnNamesData() {
		// TODO Auto-generated method stub
		ArrayList<String> otherColumnNames = null;
		try {
			db = new ScuolaDb(this);
			db.open(dbname);
			otherColumnNames = db.getfieldNames(ourDatabase, otherTableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.close();
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					R.layout.spinner_row, otherColumnNames);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// attaching data adapter to spinner
			spinOtherColumn.setAdapter(dataAdapter);
		}
	}

	private void loadSpinColumnNamesData() {
		// Creating adapter for spinner
		ArrayList<String> columnNames = null;
		try {
			db = new ScuolaDb(this);
			db.open(dbname);
			columnNames = db.getfieldNames(ourDatabase, tableName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.close();
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					R.layout.spinner_row, columnNames);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// attaching data adapter to spinner
			spinColumnNames.setAdapter(dataAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.where_clause, menu);
		return true;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		switch (parent.getId()) {
		case R.id.spinOperators:
			choosenOperator = spinOperators.getSelectedItem().toString();
			break;
		case R.id.spinOtherTable:
			Toast.makeText(getBaseContext(),
					"spinOtherTable: " + getFieldName(), Toast.LENGTH_SHORT)
					.show();
			otherTableName = spinOtherTable.getSelectedItem().toString();

			loadOtherSpinColumnNamesData();
			break;
		case R.id.spinOtherColumn:
			setOtherFieldName(spinOtherColumn.getSelectedItem().toString());
			Toast.makeText(getBaseContext(),
					"spinColumnNames: " + getOtherFieldName(),
					Toast.LENGTH_SHORT).show();

			loadSpinUniqueValuesData();
			break;
		case R.id.spinTables:
			if (arrTblSelection == null)
				arrTblSelection = new ArrayList<DatabaseFieldInfo>();

			tableName = spinTables.getSelectedItem().toString();
			Toast.makeText(getBaseContext(), "spinTables: " + tableName,
					Toast.LENGTH_SHORT).show();

			loadSpinColumnNamesData();
			break;
		case R.id.spinColumnNames:
			setFieldName(spinColumnNames.getSelectedItem().toString());
			Toast.makeText(getBaseContext(),
					"spinColumnNames: " + getFieldName(), Toast.LENGTH_SHORT)
					.show();

			loadSpinUniqueValuesData();
			break;
		case R.id.spinComparisons:
			setComparison(spinComparisons.getSelectedItem().toString());
			Toast.makeText(getBaseContext(),
					"spinComparisons: " + getComparison(), Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.spinUniqueValues:
			setUniqueValue(spinUniqueValues.getSelectedItem().toString());
			etRValue.setText(getUniqueValue());
			Toast.makeText(getBaseContext(),
					"spinUniqueValues: " + getUniqueValue(), Toast.LENGTH_SHORT)
					.show();
			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getComparison() {
		return comparison;
	}

	public void setComparison(String comparison) {
		this.comparison = comparison;
	}

	public String getUniqueValue() {
		return uniqueValue;
	}

	public void setUniqueValue(String uniqueValue) {
		this.uniqueValue = uniqueValue;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		whereClause = etWhereClause.getText().toString();
		switch (v.getId()) {
		case R.id.bAcceptOperator:
			etWhereClause.setText(addAcceptedOperatorToWhereClause());
			break;
		case R.id.bAcceptComparison:
			etWhereClause.setText(buildComparisonWithChoosenField());
			DatabaseFieldInfo info = new DatabaseFieldInfo(dbname, tableName,
					fieldName);
			if (!arrTblSelection.contains(info))
				arrTblSelection.add(info);
			break;
		case R.id.bAcceptConstant:
			etWhereClause.setText(addAcceptedConstantToWhereClause());
			break;
		case R.id.bAcceptOtherColumn:
			etWhereClause.setText(addAcceptedFieldToWhereClause());
			DatabaseFieldInfo infoOther = new DatabaseFieldInfo(dbname,
					otherTableName, otherFieldName);
			if (!arrTblSelection.contains(infoOther))
				arrTblSelection.add(infoOther);
			break;
		case R.id.bAND:
			etWhereClause.setText(whereClause + " AND ");
			break;
		case R.id.bOR:
			etWhereClause.setText(whereClause + " OR ");
			break;
		case R.id.ibAcceptWhereClause:
			Intent person = new Intent();
			Bundle backPack = new Bundle();
			backPack.putString("whereClause", whereClause);
			backPack.putParcelableArrayList("arrTblSelection", arrTblSelection);

			person.putExtras(backPack);
			setResult(RESULT_OK, person);// PASSA I RISULTATI NELL'INTENT person
			finish();
			break;
		case R.id.ibCancel:
			finish();
			break;
		default:
			break;
		}
	}

	private String addAcceptedOperatorToWhereClause() {
		// TODO Auto-generated method stub
		whereClause += " " + choosenOperator + " ";
		return whereClause;
	}

	private String addAcceptedFieldToWhereClause() {
		// TODO Auto-generated method stub
		whereClause += " " + otherTableName + "." + getOtherFieldName();
		return whereClause;
	}

	private String addAcceptedConstantToWhereClause() {
		// TODO Auto-generated method stub
		whereClause += " \"" + etRValue.getText().toString() + "\"";
		return whereClause;
	}

	private String buildComparisonWithChoosenField() {
		// TODO Auto-generated method stub
		if (whereClause.indexOf("WHERE") < 0)
			whereClause = " WHERE ";

		whereClause += tableName + "." + getFieldName() + " " + getComparison()
				+ " ";
		return whereClause;
	}

	public String getOtherFieldName() {
		return otherFieldName;
	}

	public void setOtherFieldName(String otherFieldName) {
		this.otherFieldName = otherFieldName;
	}

	public ArrayList<String> getColumnNames() {
		return columnNames;
	}

	public void setColumnNames(ArrayList<String> columnNames) {
		this.columnNames = columnNames;
	}

}
