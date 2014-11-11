package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.scuola.util.DatabaseFieldInfo;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SqLite extends Activity implements OnClickListener,
		OnLongClickListener, OnItemSelectedListener {
	private final static int WHERE_CLAUSE = 0;
	private final static int CHOOSE_FIELDS_FOR_SELECT = 1;

	private int recsPerPage = 15, startRec = 1, endRec = 15;

	EditText etsqlSqlite;
	TextView tvNumRecords, tvRecsOnPage, tvStartRec, tvEndRec;
	Button execSql;
	private String dbname, tableName;
	private String sql;
	private SQLiteDatabase ourDatabase;
	TableLayout tlSqliteSql, tlTableHeader;
	private ScuolaDb db;
	private HorizontalScrollView horizontalScrollViewHeader,
			horizontalScrollViewBody;
	private ScrollView scrollViewVerticalBody;
	private ImageButton ibPrevious, ibNext, ibFilterRecords, ibUndoWhere,
			ibTables, ibDialogClose;
	private Chronometer chronSelection;
	private ArrayList<String> arrTblNames;
	private ArrayList<DatabaseFieldInfo> arrTblSelection;
	private int numRecs = 0;
	private ArrayList<TableFieldsForSelect> tffsArray;
	private String whereClause;
	private String previousSelect;
	private int index;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sqlite_layout);

		arrTblSelection = new ArrayList<DatabaseFieldInfo>();

		Bundle basket = getIntent().getExtras();
		sql = basket.getString("sql");
		dbname = basket.getString("dbname");
		tableName = basket.getString("tableName");
		arrTblNames = basket.getStringArrayList("arrTblNames");

		arrTblSelection.add(new DatabaseFieldInfo(dbname, tableName, "*"));

		ourDatabase = openOrCreateDatabase(dbname, MODE_WORLD_WRITEABLE, null);

		tvRecsOnPage = (TextView) findViewById(R.id.tvRecsOnPage);
		tvStartRec = (TextView) findViewById(R.id.tvStartRec);
		tvEndRec = (TextView) findViewById(R.id.tvEndRec);

		// PAGINATION
		ibPrevious = (ImageButton) findViewById(R.id.ibPrevious);
		ibNext = (ImageButton) findViewById(R.id.ibNext);
		ibPrevious.setOnClickListener(this);
		ibPrevious.setOnLongClickListener(this);
		ibNext.setOnClickListener(this);
		ibNext.setOnLongClickListener(this);
		ibFilterRecords = (ImageButton) findViewById(R.id.ibFilterRecords);
		ibFilterRecords.setOnClickListener(this);
		ibUndoWhere = (ImageButton) findViewById(R.id.ibUndoWhere);
		ibUndoWhere.setOnClickListener(this);
		ibDialogClose = (ImageButton) findViewById(R.id.ibDialogClose);
		ibDialogClose.setOnClickListener(this);
		ibTables = (ImageButton) findViewById(R.id.ibTables);
		ibTables.setOnClickListener(this);

		chronSelection = (Chronometer) findViewById(R.id.chronometerSelection);

		resetRowDelimiters();

		etsqlSqlite = (EditText) findViewById(R.id.etSqliteSql);

		if (sql != null) {
			etsqlSqlite.setText(sql);
		}
		execSql = (Button) findViewById(R.id.bExecuteSqLite);
		execSql.setOnClickListener(this);

		tlTableHeader = (TableLayout) findViewById(R.id.tlTableHeader);
		tlTableHeader.removeAllViews();
		tlTableHeader.setVisibility(View.INVISIBLE);

		tlSqliteSql = (TableLayout) findViewById(R.id.tlSqliteSql);
		tlSqliteSql.removeAllViews();

		tvNumRecords = (TextView) findViewById(R.id.tvNumRecords);

		horizontalScrollViewHeader = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewHeader);
		horizontalScrollViewBody = (HorizontalScrollView) findViewById(R.id.horizontalScrollViewBody);

		horizontalScrollViewHeader.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.equals(MotionEvent.ACTION_UP))
					return true;

				int scrollX = v.getScrollX();
				int scrollY = v.getScrollY();

				horizontalScrollViewBody.scrollTo(scrollX, scrollY);
				return false;
			}
		});

		horizontalScrollViewBody.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if (event.equals(MotionEvent.ACTION_UP))
					return true;

				int scrollX = v.getScrollX();
				int scrollY = v.getScrollY();

				horizontalScrollViewHeader.scrollTo(scrollX, scrollY);
				return false;
			}
		});

		scrollViewVerticalBody = (ScrollView) findViewById(R.id.scrollViewVerticalBody);
		scrollViewVerticalBody.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				int scrollY = v.getScrollY();
				if (scrollY > 0) {
					tlTableHeader.setVisibility(View.VISIBLE);
					if (tlSqliteSql.getChildCount() > 0) {
						TableRow row = (TableRow) tlSqliteSql.getChildAt(0);
						row.setVisibility(TableRow.INVISIBLE);
					}
				} else {
					tlTableHeader.setVisibility(View.INVISIBLE);
					if (tlSqliteSql.getChildCount() > 0) {
						TableRow row = (TableRow) tlSqliteSql.getChildAt(0);
						row.setVisibility(TableRow.VISIBLE);
					}
				}
				return false;
			}
		});
		retrieveSelectionData();
	}


	/**
	 * RETRIEVE DATA FROM startRec TO endRec
	 */
	private void retrieveSelectionData() {
		// TODO Auto-generated method stub
		chronSelection.start();// CRONOMETRA TEMPI DELLE SELECT
		try {
			db = new ScuolaDb(this);
			db.open(dbname);
			numRecs = db.execSelect(ourDatabase, etsqlSqlite.getText()
					.toString(), tlTableHeader, tlSqliteSql, this, tableName,
					startRec, endRec);
		} catch (SQLException e) {
			// TODO: handle exception
			Toast.makeText(getApplicationContext(),
					"SQLException: " + e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		} finally {
			db.close();
			tvNumRecords.setText(String.valueOf(numRecs));
			if (numRecs < getEndRec())
				setEndRec(numRecs);

			chronSelection.stop();
		}
	}

	public void returnResult() {
		getParent().setResult(RESULT_OK);
		finish();
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.ibUndoWhere:
			previousSelect = etsqlSqlite.getText().toString();
			// ELIMINA LA CLAUSOLA WHERE SE GIA' ESISTENTE
			index = previousSelect.indexOf("WHERE");
			if (index > 0) {
				previousSelect = previousSelect.substring(0, index);
			}

			etsqlSqlite.setText(previousSelect);
			resetRowDelimiters();
			retrieveSelectionData();
			break;
		case R.id.bExecuteSqLite:
			resetRowDelimiters();
			retrieveSelectionData();
			break;
		case R.id.ibPrevious:
			if (startRec == 1)
				return;
			if (startRec - recsPerPage < 1)
				setStartRec(1);
			else
				setStartRec(startRec - recsPerPage);
			setEndRec(getStartRec() + recsPerPage - 1);
			retrieveSelectionData();
			break;
		case R.id.ibNext:
			if (startRec > numRecs - recsPerPage)
				return;
			setStartRec(startRec + recsPerPage);
			if (getStartRec() + recsPerPage > numRecs)
				setEndRec(numRecs);
			else
				setEndRec(getStartRec() + recsPerPage - 1);

			retrieveSelectionData();
			break;
		case R.id.ibFilterRecords:

			ArrayList<String> columnNames = null;
			try {
				db = new ScuolaDb(this);
				db.open(dbname);
				columnNames = db.getfieldNames(ourDatabase, tableName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Toast.makeText(getApplicationContext(),
						"ibFilterRecords:" + e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				db.close();
				// WHERE CLAUSE EXISISTING
				previousSelect = etsqlSqlite.getText().toString();
				// ELIMINA LA CLAUSOLA WHERE SE GIA' ESISTENTE
				index = previousSelect.indexOf("WHERE");
				if (index > 0) {
					whereClause = previousSelect.substring(index);
				} else
					whereClause = " WHERE ";

				// START ACTIVITY WhereClause...
				Bundle pack = new Bundle();
				pack.putStringArrayList("columnNames", columnNames);
				pack.putString("dbname", dbname);
				pack.putString("tableName", tableName);
				pack.putString("whereClause", whereClause);
				if(arrTblNames==null){
					getArrayTableNames();
				}
				pack.putStringArrayList("arrTblNames", arrTblNames);

				Intent whereClauseIntent = new Intent(
						"it.keyorchestra.registrowebapp.WHERE_CLAUSE");
				whereClauseIntent.putExtras(pack);
				startActivityForResult(whereClauseIntent, WHERE_CLAUSE);// FOR
																		// RESULT
			}
			break;
		case R.id.ibTables:
			Bundle packTables = new Bundle();
			packTables.putString("dbname", dbname);

			packTables.putParcelableArrayList("arrTblSelection",
					arrTblSelection);

			Intent chooseTableIntent = new Intent(
					"it.keyorchestra.registrowebapp.CHOOSE_FIELDS_FOR_SELECT");
			chooseTableIntent.putExtras(packTables);
			startActivityForResult(chooseTableIntent, CHOOSE_FIELDS_FOR_SELECT);// FOR
																				// RESULT
			break;
		case R.id.ibDialogClose:
			finish();
			break;
		}

	}

	private void getArrayTableNames() {
		// TODO Auto-generated method stub
		db = new ScuolaDb(this);
		db.open(dbname);
		arrTblNames = db.getTablesNames(ourDatabase);
		db.close();
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bundle basket;
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			int index;
			String previousSelect;
			switch (requestCode) {
			case WHERE_CLAUSE:
				basket = data.getExtras();
				String whereClause = basket.getString("whereClause");
				if (whereClause.trim().equals("WHERE"))
					whereClause = "";
				arrTblSelection = basket
						.getParcelableArrayList("arrTblSelection");

				if (whereClause != null)
					Toast.makeText(getBaseContext(),
							"onActivityResult: " + whereClause,
							Toast.LENGTH_SHORT).show();
				previousSelect = etsqlSqlite.getText().toString();
				// ELIMINA LA CLAUSOLA WHERE SE GIA' ESISTENTE
				index = previousSelect.indexOf("FROM");
				if (index > 0) {
					previousSelect = previousSelect.substring(0, index);
				}
				previousSelect += " " + listTablesForSelection() + " "
						+ whereClause;
				etsqlSqlite.setText(previousSelect);
				resetRowDelimiters();
				retrieveSelectionData();
				break;
			case CHOOSE_FIELDS_FOR_SELECT:
				basket = data.getExtras();
				tffsArray = basket.getParcelableArrayList("tffsArray");
				previousSelect = etsqlSqlite.getText().toString();

				index = previousSelect.indexOf("FROM");
				if (index > 0) {
					previousSelect = previousSelect.substring(index);
				}
				previousSelect = getListOfFieldsToSelect() + " "
						+ previousSelect;
				etsqlSqlite.setText(previousSelect);
				resetRowDelimiters();
				retrieveSelectionData();
				break;

			default:
				break;
			}

		}
	}

	private String getListOfFieldsToSelect() {
		// TODO Auto-generated method stub
		String fields = "SELECT ";
		for (int i = 0; i < tffsArray.size(); i++) {
			fields += tffsArray.get(i).getTable() + "."
					+ tffsArray.get(i).getField();
			if (i < tffsArray.size() - 1)
				fields += ", ";
		}
		return fields;
	}

	private String listTablesForSelection() {
		// TODO Auto-generated method stub
		String listTables = "FROM ";
		ArrayList<String> tableNames = new ArrayList<String>();

		for (int i = 0; i < arrTblSelection.size(); i++) {
			if (!tableNames.contains(arrTblSelection.get(i).getTableName()))
				tableNames.add(arrTblSelection.get(i).getTableName());
		}
		for (int j = 0; j < tableNames.size(); j++) {

			listTables += tableNames.get(j);
			if (j < tableNames.size() - 1)
				listTables += ", ";
		}

		return listTables;
	}

	private void resetRowDelimiters() {
		// TODO Auto-generated method stub
		setStartRec(1);
		setEndRec(getStartRec() + recsPerPage - 1);
		setRecsPerPage(recsPerPage);
	}

	public int getRecsPerPage() {
		return recsPerPage;
	}

	public void setRecsPerPage(int recsPerPage) {
		this.recsPerPage = recsPerPage;
		tvRecsOnPage.setText(String.valueOf(recsPerPage));
	}

	public int getStartRec() {
		return startRec;
	}

	public void setStartRec(int startRec) {
		this.startRec = startRec;
		tvStartRec.setText(String.valueOf(startRec));
	}

	public int getEndRec() {
		return endRec;
	}

	public void setEndRec(int endRec) {
		this.endRec = endRec;
		tvEndRec.setText(String.valueOf(endRec));
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ibPrevious:
		case R.id.ibNext:
			// Toast.makeText(this, "onLongClick:ibPrevious " + v.toString(),
			// Toast.LENGTH_SHORT).show();
			Bundle pack = new Bundle();
			pack.putInt("startRec", startRec);
			pack.putInt("numRecs", numRecs);

			showDialog(R.drawable.table, pack);
			break;
		}
		return false;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
		// TODO Auto-generated method stub
		super.onPrepareDialog(id, dialog, args);
		int position = args.getInt("startRec");

		SeekBar sb = (SeekBar) dialog.findViewById(R.id.sbSelection);

		sb.setProgress(position - 1);
	}

	@Override
	public Dialog onCreateDialog(int id, Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// Get the layout inflater
		LayoutInflater inflater = this.getLayoutInflater();
		Drawable image = this.getResources().getDrawable(id);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		int position = savedInstanceState.getInt("startRec");
		int totRecords = savedInstanceState.getInt("numRecs");

		View inf = inflater.inflate(R.layout.choose_range_selection, null);

		TextView tvMinInRange = (TextView) inf.findViewById(R.id.tvMinInRange);
		tvMinInRange.setText(String.valueOf(1));

		TextView tvMaxInRange = (TextView) inf.findViewById(R.id.tvMaxInRange);
		tvMaxInRange.setText(String.valueOf(totRecords));

		final TextView tvRangeSelection = (TextView) inf
				.findViewById(R.id.tvRangeSelection);
		tvRangeSelection.setText(String.valueOf(position));

		SeekBar sb = (SeekBar) inf.findViewById(R.id.sbSelection);

		sb.setMax(totRecords - 1);// ZERO BASED RANGE OF SEEKBAR
		sb.setProgress(position - 1);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int discrete;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				Toast.makeText(getBaseContext(),
						"discrete = " + String.valueOf(discrete),
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				discrete = progress + 1;// ZERO BASED RANGE OF SEEKBAR
				tvRangeSelection.setText(String.valueOf(discrete));
			}
		});

		builder.setView(inf)
				// Add action buttons
				.setIcon(image)
				.setPositiveButton(R.string.rangeSelectionOk,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// sign in the user ...
								int pos = Integer.valueOf(
										String.valueOf(tvRangeSelection
												.getText())).intValue();
								setStartRec(pos);
								setEndRec(getStartRec() + recsPerPage - 1);
								retrieveSelectionData();
								// Toast.makeText(
								// getBaseContext(),
								// "setPositiveButton = "
								// + String.valueOf(pos),
								// Toast.LENGTH_SHORT).show();
							}
						})
				.setNegativeButton(R.string.rangeSelecionCancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// LoginDialogFragment.this.getDialog().cancel();
							}
						});
		return builder.create();
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}