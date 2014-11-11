package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.scuola.util.DatabaseFieldInfo;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;

public class ChooseFieldsForSelect extends Activity implements
		OnItemSelectedListener, OnClickListener {
	private SQLiteDatabase ourDatabase;
	private ScuolaDb db;

	private String choosenTable, choosenField;
	private String dbname;
	private ArrayList<String> arrTblNames;

	private Spinner spinTables, spinFields;
	private ImageButton ibDialogClose, ibAccept, ibAdd;
	private ArrayList<DatabaseFieldInfo> arrTblSelection;
	private TableLayout tlFieldsChoosen;
	private ArrayList<TableFieldsForSelect> tffsArray;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_fields_for_select);

		tffsArray = new ArrayList<TableFieldsForSelect>();

		Bundle basket = getIntent().getExtras();
		dbname = basket.getString("dbname");
		arrTblSelection = basket.getParcelableArrayList("arrTblSelection");

		ourDatabase = openOrCreateDatabase(dbname, MODE_WORLD_WRITEABLE, null);

		spinTables = (Spinner) findViewById(R.id.spinTables);
		spinTables.setOnItemSelectedListener(this);
		loadSpinTables();

		spinFields = (Spinner) findViewById(R.id.spinFields);
		spinFields.setOnItemSelectedListener(this);

		ibDialogClose = (ImageButton) findViewById(R.id.ibDialogClose);
		ibDialogClose.setOnClickListener(this);
		ibAccept = (ImageButton) findViewById(R.id.ibAccept);
		ibAccept.setOnClickListener(this);
		ibAdd = (ImageButton) findViewById(R.id.ibAdd);
		ibAdd.setOnClickListener(this);

		setTitle("Choose fields for SELECT : Database : " + dbname);

		tlFieldsChoosen = (TableLayout) findViewById(R.id.tlFieldsChoosen);

	}

	private void loadSpinTables() {
		// TODO Auto-generated method stub
		if (arrTblNames == null)
			arrTblNames = new ArrayList<String>();

		ArrayAdapter<String> dataAdapter;
		if (arrTblSelection.size() == 0) {
			try {
				db = new ScuolaDb(this);
				db.open(dbname);
				arrTblNames = db.getTablesNames(ourDatabase);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				db.close();
				return;
			} finally {
				db.close();
				dataAdapter = new ArrayAdapter<String>(this,
						R.layout.spinner_row, arrTblNames);
			}
		} else {

			for (int i = 0; i < arrTblSelection.size(); i++) {
				if (!arrTblNames
						.contains(arrTblSelection.get(i).getTableName()))
					arrTblNames.add(arrTblSelection.get(i).getTableName());
			}
			dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row,
					arrTblNames);
		}

		// Drop down layout style - list view with radio button
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// attaching data adapter to spinner
		spinTables.setAdapter(dataAdapter);
	}

	private void loadSpinColumnNames() {
		// Creating adapter for spinner
		ArrayList<String> columnNames = new ArrayList<String>();
		try {
			db = new ScuolaDb(this);
			db.open(dbname);
			columnNames = db.getfieldNames(ourDatabase, getChoosenTable());
			columnNames.add(0, "*");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.close();
			return;
		} finally {
			db.close();
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
					R.layout.spinner_row, columnNames);

			// Drop down layout style - list view with radio button
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			// attaching data adapter to spinner
			spinFields.setAdapter(dataAdapter);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_fields_for_select, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ibDialogClose:
			finish();
			break;
		case R.id.ibAccept:
			Intent person = new Intent();
			Bundle backPack = new Bundle();
			backPack.putParcelableArrayList("tffsArray", tffsArray);

			person.putExtras(backPack);
			setResult(RESULT_OK, person);// PASSA I RISULTATI NELL'INTENT person
			finish();

			break;
		case R.id.ibAdd:
			addFieldToSelect();
			break;
		default:
			break;
		}
	}

	private void addFieldToSelect() {
		// TODO Auto-generated method stub
		TableFieldsForSelect tffs = new TableFieldsForSelect(choosenTable,
				choosenField);
		if (tffsArray.contains(tffs))
			return;
		tffsArray.add(tffs);

		TableRow tableRow = new TableRow(getBaseContext());

		tableRow.setLayoutParams(new TableRow.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		TextView columnTable = getTextViewForColumnName(getChoosenTable());

		columnTable.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT, 0.8f));

		TextView columnField = getTextViewForColumnName(getChoosenField());

		columnField.setLayoutParams(new TableRow.LayoutParams(
				TableRow.LayoutParams.WRAP_CONTENT,
				TableRow.LayoutParams.WRAP_CONTENT, 0.8f));

		tableRow.addView(columnTable);
		tableRow.addView(columnField);

		tlFieldsChoosen.addView(tableRow, new TableLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		tableRow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(),
						"setOnClickListener: " + v.toString(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private TextView getTextViewForColumnName(String columnName) {
		// TODO Auto-generated method stub
		Typeface tf = Typeface.createFromAsset(this.getAssets(),
				"font/Roboto-Bold.ttf");
		TextView textView = new TextView(this);
		textView.setText(columnName);
		textView.setMaxLines(1);
		textView.setTextSize(12f);
		textView.setTextAppearance(this, android.R.style.TextAppearance_Medium);
		textView.setBackgroundDrawable(getBaseContext().getResources()
				.getDrawable(R.drawable.cell_shape));
		textView.setGravity(Gravity.CENTER);
		textView.setTypeface(tf);
		textView.setPadding(5, 0, 5, 0);
		return textView;
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		switch (parent.getId()) {
		case R.id.spinTables:
			setChoosenTable(spinTables.getSelectedItem().toString());
			Toast.makeText(getBaseContext(), "Table: " + getChoosenTable(),
					Toast.LENGTH_SHORT).show();
			if (getChoosenTable() != null)
				loadSpinColumnNames();
			break;
		case R.id.spinFields:
			setChoosenField(spinFields.getSelectedItem().toString());

			break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	public String getChoosenTable() {
		return choosenTable;
	}

	public void setChoosenTable(String choosenTable) {
		this.choosenTable = choosenTable;
	}

	public String getChoosenField() {
		return choosenField;
	}

	public void setChoosenField(String choosenField) {
		this.choosenField = choosenField;
	}

}
