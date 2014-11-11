package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface;
import it.keyorchestra.registrowebapp.scuola.tables.UtentiScuolaTable;
import it.keyorchestra.registrowebapp.scuola.util.GestureListener;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ScuolaDb extends UtentiScuolaTable implements DatabasesInterface {

	private static final String DATABASE_NAME = "scuola";
	private static final int DATABASE_VERSION = 1;
	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;
	private String dbName = DATABASE_NAME;

	final static String[] columns = new String[] { KEY_ID_UTENTE, KEY_COGNOME,
			KEY_NOME, KEY_EMAIL, KEY_PASSWORD };

	public ScuolaDb(Context c) {
		ourContext = c;

	}

	/**
	 * OPEN DATABASE = dbName
	 * 
	 * @param dbName
	 * @return
	 */
	public ScuolaDb open(String dbName) {
		// TODO Auto-generated method stub
		setDbName(dbName);
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;

	}

	public ScuolaDb open() throws SQLException {
		ourHelper = new DbHelper(ourContext, DATABASE_NAME, DATABASE_VERSION);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		// TODO Auto-generated method stub
		ourHelper.close();
	}

	/**
	 * CREATE TABLE AND PRIMARY KEY AUTOINCREMENT IF TYPE=INTEGER
	 * 
	 * @param dbName
	 * @param etTableName
	 * @param etName
	 * @param etType
	 * @throws SQLException
	 */
	public void createTableWithPrimaryKey(String dbName, EditText etTableName,
			EditText etName, EditText etType) throws SQLException {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		ourHelper.createTableWithPrimaryKey(ourDatabase, etTableName, etName,
				etType);

	}

	/**
	 * DROP TABLE
	 * 
	 * @param dbName
	 * @param etTableName
	 * @throws SQLException
	 */
	public void dropTable(String dbName, EditText etTableName)
			throws SQLException {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		ourHelper.dropTable(ourDatabase, etTableName);
	}

	/**
	 * ADD COLUMN
	 * 
	 * @param dbName
	 * @param etTableName
	 * @param etName
	 * @param etType
	 * @param cbNotNull
	 * @param etDefault
	 * @param cbPK
	 * @throws SQLException
	 */
	public void addColumnToTable(String dbName, EditText etTableName,
			EditText etName, EditText etType, CheckBox cbNotNull,
			EditText etDefault, CheckBox cbPK) throws SQLException {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		ourHelper.addColumnToTable(ourDatabase, dbName, etTableName, etName,
				etType, cbNotNull, etDefault, cbPK);
	}

	/**
	 * DROP COLUMN
	 * 
	 * @param dbName
	 * @param etTableName
	 * @param etName
	 * @throws SQLException
	 */
	public void removeColumnFromTable(String dbName, EditText etTableName,
			EditText etName) throws SQLException {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		ourHelper.removeColumnFromTable(ourDatabase, dbName, etTableName,
				etName);
	}

	public int execSelect(SQLiteDatabase ourDatabase, String sql,
			TableLayout tlTableHeader, TableLayout tlSqliteSql,
			Activity sqLite, String tablename, int startRec, int endRec) {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		return ourHelper.execSelect(ourDatabase, sql, tlTableHeader,
				tlSqliteSql, sqLite, tablename, startRec, endRec);
	}

	/**********************************************************************/
	/***************************** DbHelper *******************************/
	/**********************************************************************/
	public static class DbHelper extends SQLiteOpenHelper implements
			OnClickListener, OnFocusChangeListener, OnLongClickListener {
		private Context context;
		private String databaseName;
		GestureDetector gestureDetector;

		/**
		 * COSTRUTTORE di un database
		 * 
		 * @param ourContext
		 * @param databaseName
		 * @param databaseVersion
		 */
		public DbHelper(Context ourContext, String databaseName,
				int databaseVersion) {
			// TODO Auto-generated constructor stub
			super(ourContext, databaseName, null, databaseVersion);
			this.context = ourContext;
			this.databaseName = databaseName;
			gestureDetector = new GestureDetector(context, new GestureListener(
					context));
		}

		/**
		 * ESEGUE DROP COLUMN
		 * 
		 * @param db
		 * @param dbName
		 * @param etTableName
		 * @param etName
		 * @throws SQLException
		 */
		public void removeColumnFromTable(SQLiteDatabase db, String dbName,
				EditText etTableName, EditText etName) throws SQLException {
			// TODO Auto-generated method stub
			/**
			 * (1)BEGIN TRANSACTION; (2)CREATE TEMPORARY TABLE t1_backup(a,b);
			 * (3)INSERT INTO t1_backup SELECT a,b FROM t1; (4)DROP TABLE t1;
			 * (5)CREATE TABLE t1(a,b); (6)INSERT INTO t1 SELECT a,b FROM
			 * t1_backup; (7)DROP TABLE t1_backup; (8)COMMIT;
			 */
			// select sql from sqlite_master where type='table'
			// (1)
			db.beginTransaction();// BEGIN TRANSACTION;
			try {
				// SELEZIONA I CAMPI DELLA TABELLA
				String tableName = etTableName.getText().toString();

				CreateTemporaryTable(db, dbName, etTableName, etName);

				PopulateBackUpTable(db, dbName, etTableName, etName);

				// (4)
				// DROP TABLE t1;
				ArrayList<ColumnInfo> columnsInfo = getColumnInfoFromTableWithoutFieldToDrop(
						db, tableName, etName.getText().toString());

				dropTable(db, tableName);

				reCreateTableWithoutDroppedColumn(db, etTableName, columnsInfo);

				rePopulateTableWithBackupTable(db, dbName, etTableName, etName);

				// (7)
				// DROP TABLE * t1_backup;
				dropTable(db, tableName + "_backup");

				db.setTransactionSuccessful();
			} catch (SQLException ex) {
				throw new SQLException(ex.getMessage());
			} finally {
				db.endTransaction();
			}
		}

		/**
		 * RIPOPOLA LA NUOVA TABELLA CON I DATI DELLA TABELLA DI BACKUP
		 * 
		 * @param db
		 * @param dbName
		 * @param etTableName
		 * @param etName
		 */
		private String rePopulateTableWithBackupTable(SQLiteDatabase db,
				String dbName, EditText etTableName, EditText etName) {
			// TODO Auto-generated method stub
			// INSERT * INTO t1 SELECT a,b FROM t1_backup;
			String tableName = etTableName.getText().toString();
			ArrayList<String> fieldNames = getFieldNames(db, tableName);

			String sqlForInsertNew = "INSERT INTO " + tableName + " SELECT ";
			// AGGIUNGE I CAMPI
			sqlForInsertNew += addFieldsListToSql(sqlForInsertNew, fieldNames,
					etName.getText().toString());

			sqlForInsertNew += " FROM " + tableName + "_backup" + ";";
			db.execSQL(sqlForInsertNew);
			return sqlForInsertNew;
		}

		/**
		 * RICREA LA NUOVA TABELLA
		 * 
		 * @param db
		 * @param etTableName
		 * @param columnsInfo
		 */
		private String reCreateTableWithoutDroppedColumn(SQLiteDatabase db,
				EditText etTableName, ArrayList<ColumnInfo> columnsInfo) {
			// TODO Auto-generated method stub
			// CREATE TABLE * t1(a,b); crea la nuova table
			String tableName = etTableName.getText().toString();

			String sqlForRecreate = "CREATE TABLE " + tableName;
			sqlForRecreate += "(" + buildParametersWith(columnsInfo) + ")";

			db.execSQL(sqlForRecreate);
			return sqlForRecreate;
		}

		/**
		 * COPIA I DATI DELLA TABELLA etTableName NELLA TABELLA DI BACKUP
		 * etTableName_backup
		 * 
		 * @param db
		 * @param dbName
		 * @param etTableName
		 * @param etName
		 */
		private String PopulateBackUpTable(SQLiteDatabase db, String dbName,
				EditText etTableName, EditText etName) {
			// TODO Auto-generated method stub
			String tableName = etTableName.getText().toString();
			ArrayList<String> fieldNames = getFieldNames(db, tableName);

			String sqlForInsert = "INSERT INTO " + tableName + "_backup"
					+ " SELECT ";
			// AGGIUNGE I CAMPI
			sqlForInsert += addFieldsListToSql(sqlForInsert, fieldNames, etName
					.getText().toString());

			sqlForInsert += " FROM " + tableName + ";";
			db.execSQL(sqlForInsert);
			return sqlForInsert;

		}

		/**
		 * CREA UNA TABELLA TEMPORANEA PER IL BACKUP DEI DATI DELLA etTableName
		 * DALLA QUALE SIA ESCLUSO IL CAMPO etName
		 * 
		 * @param db
		 * @param dbName
		 * @param etTableName
		 * @param etName
		 */
		private String CreateTemporaryTable(SQLiteDatabase db, String dbName,
				EditText etTableName, EditText etName) {
			// TODO Auto-generated method stub
			String tableName = etTableName.getText().toString();
			ArrayList<String> fieldNames = getFieldNames(db, tableName);

			Cursor c = db.rawQuery(
					"SELECT sql FROM sqlite_master WHERE type='table' AND name='"
							+ etTableName.getText().toString() + "'", null);

			c.moveToFirst();
			String sql = c.getString(c.getColumnIndex("sql"));
			c.close();

			String sqlForCreateTable = sql;
			// CAMBIA IL NOME DELLA TABELLA IN *_backup
			int indexOfTableName = sqlForCreateTable.lastIndexOf(tableName);
			String prefix = sqlForCreateTable.substring(0, indexOfTableName);
			sqlForCreateTable = prefix + tableName + "_backup(";
			sqlForCreateTable += addFieldsListToSql(sqlForCreateTable,
					fieldNames, etName.getText().toString());
			sqlForCreateTable += ");";

			// (2)
			// CREATE TEMPORARY TABLE t1_backup(a,b);
			db.execSQL(sqlForCreateTable);
			return sqlForCreateTable;
		}

		/**
		 * COSTRUISCE UNA LISTA DI NOMI DI CAMPI SEPARATI DA VIRGOLA
		 * 
		 * @param columnsInfo
		 * @return
		 */
		private String buildParametersWith(ArrayList<ColumnInfo> columnsInfo) {
			// TODO Auto-generated method stub
			String params = "";
			ArrayList<String> primaryKeys = new ArrayList<String>();

			for (int j = 0; j < columnsInfo.size(); j++) {
				ColumnInfo info = columnsInfo.get(j);

				String name = info.getName();
				String type = info.getType();
				int notNull = info.getNotNull();
				String dfltValue = info.getDfltValue();
				int pk = info.getPrimaryKey();

				params += name + " " + type + " ";
				if (notNull == 1)
					params += " NOT NULL ";
				else
					params += " NULL ";

				if (dfltValue != null && dfltValue.length() > 0)
					params += " DEFAULT " + dfltValue;

				if (pk == 1)
					primaryKeys.add(name);
				if (j < columnsInfo.size() - 1)
					params += ",";
			}
			if (primaryKeys.size() > 0) {// AGGIUNGE CONSTRAINT PRIMARY KEY
				String listOfPrimary = listOfPrimary(primaryKeys);
				params += ", CONSTRAINT pkey PRIMARY KEY (" + listOfPrimary
						+ ")";
			}
			return params;
		}

		/**
		 * OTTIENE UNA LISTA DI PRIMARY KEY SEPARATE DA VIRGOLA
		 * 
		 * @param primaryKeys
		 * @return
		 */
		private String listOfPrimary(ArrayList<String> primaryKeys) {
			// TODO Auto-generated method stub
			String primaries = "";
			for (int j = 0; j < primaryKeys.size(); j++) {
				String pkey = primaryKeys.get(j);
				primaries += pkey;
				if (j < primaryKeys.size() - 1)
					primaries += ",";
			}
			return primaries;
		}

		/**
		 * OTIIENE LE INFORMAZIONI SU TUTTE LE COLONNE DELLA TABELLA AD
		 * ESCLUSIONE DI fieldToDrop SE fieldToDrop!=NULL
		 * 
		 * @param db
		 * @param tblName
		 * @param fieldToDrop
		 * @return
		 */
		private ArrayList<ColumnInfo> getColumnInfoFromTableWithoutFieldToDrop(
				SQLiteDatabase db, String tblName, String fieldToDrop) {
			// TODO Auto-generated method stub
			Cursor cursor = db.rawQuery("PRAGMA table_info(" + tblName + ")",
					null);
			cursor.moveToFirst();
			ArrayList<ColumnInfo> colInfoArray = new ArrayList<DatabasesInterface.ColumnInfo>();
			while (!cursor.isAfterLast()) {
				try {
					int nameIdx = cursor
							.getColumnIndexOrThrow(sqliteColumnsInfoNames[0]);
					int typeIdx = cursor
							.getColumnIndexOrThrow(sqliteColumnsInfoNames[1]);
					int notNullIdx = cursor
							.getColumnIndexOrThrow(sqliteColumnsInfoNames[2]);
					int dfltValueIdx = cursor
							.getColumnIndexOrThrow(sqliteColumnsInfoNames[3]);
					int primaryKeyIdx = cursor
							.getColumnIndexOrThrow(sqliteColumnsInfoNames[4]);

					String name = cursor.getString(nameIdx);
					if (fieldToDrop != null && name.equals(fieldToDrop))
						continue;
					String type = cursor.getString(typeIdx);
					int notNull = cursor.getInt(notNullIdx);
					String dfltValue = cursor.getString(dfltValueIdx);
					int primaryKey = cursor.getInt(primaryKeyIdx);

					ColumnInfo colInfo = new ColumnInfo(name, type, notNull,
							dfltValue, primaryKey);
					colInfoArray.add(colInfo);
				} finally {
					cursor.moveToNext();
				}
			}
			cursor.close();
			return colInfoArray;
		}

		/**
		 * TORNA LA LISTA DEI CAMPI IN FORMATO A,B,C
		 * 
		 * @param sqlForInsert
		 * @param fieldNames
		 * @return
		 */
		private String addFieldsListToSql(String sqlForInsert,
				ArrayList<String> fieldNames, String toDropColumn) {
			// TODO Auto-generated method stub
			String fields = "";
			for (int i = 0; i < fieldNames.size(); i++) {
				if (!fieldNames.get(i).equals(toDropColumn)) {

					fields += fieldNames.get(i);
					if (i < fieldNames.size() - 1) {
						if (!fieldNames.get(i + 1).equals(toDropColumn))
							fields += ",";
					}
				}
			}
			return fields;
		}

		/**
		 * Ottiene i nomi dei campi della tabella
		 * 
		 * @param db
		 * @param tableName
		 * @return
		 */
		private ArrayList<String> getFieldNames(SQLiteDatabase db,
				String tableName) {
			// TODO Auto-generated method stub
			Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")",
					null);
			cursor.moveToFirst();
			ArrayList<String> fieldNames = new ArrayList<String>();
			while (!cursor.isAfterLast()) {
				try {
					String name = cursor.getString(cursor
							.getColumnIndex("name"));

					fieldNames.add(name);

				} finally {
					cursor.moveToNext();
				}
			}
			cursor.close();
			return fieldNames;
		}

		/**
		 * DROP TABLE
		 * 
		 * @param db
		 * @param tableName
		 */
		private void dropTable(SQLiteDatabase db, String tableName) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE " + tableName + ";");
		}

		/**
		 * ALTER TABLE ADD COLUMN
		 * 
		 * @param db
		 * @param dbName
		 * @param etTableName
		 * @param etName
		 * @param etType
		 * @param cbNotNull
		 * @param etDefault
		 * @param cbPK
		 * @throws SQLException
		 */
		public void addColumnToTable(SQLiteDatabase db, String dbName,
				EditText etTableName, EditText etName, EditText etType,
				CheckBox cbNotNull, EditText etDefault, CheckBox cbPK)
				throws SQLException {
			// TODO Auto-generated method stub
			boolean isNotNull = cbNotNull.isChecked();
			boolean isPK = cbPK.isChecked();
			if (isPK) {
				addPrimaryKeyColumn(db, dbName, etTableName, etName, etType,
						cbNotNull, etDefault, cbPK);
			} else {

				String sql = "ALTER TABLE " + etTableName.getText().toString()
						+ " ADD COLUMN " + etName.getText().toString() + " "
						+ etType.getText().toString();
				if (isNotNull)
					sql += " NOT NULL ";
				else
					sql += " NULL ";

				sql += " DEFAULT " + etDefault.getText().toString();

				db.execSQL(sql + ";");
			}
		}

		/**
		 * LA ALTER TABLE ADD COLUMN IN CASO DI PRIMARY KEY RICHIEDE UNA
		 * RICOSTRUZIONE DELLA TABELLA COME PER LA DROP COLUMN
		 * 
		 * @param db
		 * @param dbName
		 * @param etTableName
		 * @param etName
		 * @param etType
		 * @param cbNotNull
		 * @param etDefault
		 * @param cbPK
		 * @throws SQLException
		 */
		private void addPrimaryKeyColumn(SQLiteDatabase db, String dbName,
				EditText etTableName, EditText etName, EditText etType,
				CheckBox cbNotNull, EditText etDefault, CheckBox cbPK)
				throws SQLException {
			// TODO Auto-generated method stub
			Toast.makeText(this.context,
					"addPrimaryKeyColumn: '" + dbName + "' " + " TODO !",
					Toast.LENGTH_SHORT).show();

		}

		/**
		 * DROP TABLE
		 * 
		 * @param db
		 * @param etTableName
		 * @throws SQLException
		 */
		public void dropTable(SQLiteDatabase db, EditText etTableName)
				throws SQLException {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE " + etTableName.getText().toString() + ";");
		}

		/**
		 * CREATE TABLE WITH A PRIMARY KEY AUTOINCREMENT
		 * 
		 * @param db
		 * @param etTableName
		 * @param etName
		 * @param etType
		 * @throws SQLException
		 */
		public void createTableWithPrimaryKey(SQLiteDatabase db,
				EditText etTableName, EditText etName, EditText etType)
				throws SQLException {
			// TODO Auto-generated method stub
			String sql = "CREATE TABLE " + etTableName.getText().toString()
					+ " (" + etName.getText().toString() + " "
					+ etType.getText().toString() + " PRIMARY KEY ";
			if (etType.getText().toString().equals("INTEGER"))
				sql += " AUTOINCREMENT ";

			sql += " );";
			db.execSQL(sql);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			if (databaseName.equals(DATABASE_NAME)) {
				db.execSQL("CREATE TABLE " + TABLE_UTENTI_SCUOLA + " ("
						+ KEY_ID_UTENTE
						+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_COGNOME
						+ " TEXT NOT NULL, " + KEY_NOME + " TEXT NOT NULL, "
						+ KEY_EMAIL + " TEXT NOT NULL, " + KEY_PASSWORD
						+ " TEXT NOT NULL);");
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTENTI_SCUOLA + ";");
			onCreate(db);
		}

		private Activity sqLite;
		private String tablename;

		/**
		 * ESEGUE UNA QUERY=sql NELLA TABLE=tablename NEL DATABASE=ourDatabase
		 * PASSA COME PARAMETRI LE TABLELAYOUT CHE CONTENGONO L'INTESTAZIONE E I
		 * DATI IN TableLayout tlTableHeader, TableLayout tlSqliteSql RCUPERA I
		 * RECORDS CHE VANNO DA startRec A endRec
		 * 
		 * @param ourDatabase
		 * @param sql
		 * @param tlTableHeader
		 * @param tlSqliteSql
		 * @param sqLite
		 * @param tablename
		 * @param startRec
		 * @param endRec
		 * @return
		 */
		public int execSelect(SQLiteDatabase ourDatabase, String sql,
				TableLayout tlTableHeader, TableLayout tlSqliteSql,
				Activity sqLite, String tablename, int startRec, int endRec) {
			// TODO Auto-generated method stub

			int numRecs = 0;

			this.sqLite = sqLite;
			this.tablename = tablename;

			tlSqliteSql.removeAllViews();
			tlTableHeader.removeAllViews();

			Cursor c = ourDatabase.rawQuery(sql, null);
			numRecs = c.getCount();
			c.moveToFirst();

			// int countRows = c.getCount();
			int columnsCount = c.getColumnCount();
			String[] columnNames = c.getColumnNames();

			// COSTRUISCE L'INTESTAZIONE
			tlTableHeader.addView(
					buildRowIntestazione(columnsCount, columnNames),
					new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));

			tlSqliteSql.addView(
					buildRowIntestazione(columnsCount, columnNames),
					new TableLayout.LayoutParams(LayoutParams.FILL_PARENT,
							LayoutParams.WRAP_CONTENT));

			int count = startRec - 1;
			c.moveToPosition(count);
			while (!c.isAfterLast()) {
				count++;
				if (count >= startRec && count <= endRec)
					tlSqliteSql.addView(
							buildRowData(columnsCount, columnNames, c),
							new TableLayout.LayoutParams(
									LayoutParams.FILL_PARENT,
									LayoutParams.WRAP_CONTENT));
				if (count > endRec)
					break;
				c.moveToNext();
			}

			// DEVE RENDERE DI UGUALE LARGHEZZA LE COLONNE HEADER E LE COLONNE
			// DATI
			c.close();
			return numRecs;
		}

		/**
		 * COSTRUIDCE UNA RIGA DI VALORI RELATIVI ALLA RIGA DI INTESTAZIONE
		 * PRECEDENTEMENTE COSTRUITA
		 * 
		 * @param tlSqliteSql
		 * 
		 * @param columnsCount
		 * @param columnNames
		 * @param c
		 * @return
		 */

		private TableRow buildRowData(int columnsCount, String[] columnNames,
				Cursor c) {
			// TODO Auto-generated method stub
			TableRow tableRow = new TableRow(context);

			tableRow.setLayoutParams(new TableRow.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, 1));
			tableRow.setPadding(0, 0, 0, 10);

			setBackroundColor(tableRow, c.getPosition());

			for (int i = 0; i < columnsCount; i++) {
				int idx = c.getColumnIndexOrThrow(columnNames[i]);
				EditText columnValue = getEditTextForColumnValue(c
						.getString(idx));

				columnValue.setOnClickListener(this);
				columnValue.setOnFocusChangeListener(this);

				columnValue.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
				// GET THE WIDTH
				tableRow.addView(columnValue);
			}
			// tableRow.setOnTouchListener(this);
			tableRow.setOnLongClickListener(this);
			return tableRow;
		}

		/**
		 * IMPOSTA IL BACKGROUND COLOR DI OGNI RIGA ALERNANDO DUE COLORI IN
		 * RAGIONE DEL NUMERO DI RIGA SE PARI O DISPARI
		 * 
		 * @param tableRow
		 * @param position
		 */
		private void setBackroundColor(TableRow tableRow, int position) {
			// TODO Auto-generated method stub
			if (position % 2 == 0)
				tableRow.setBackgroundColor(context.getResources().getColor(
						R.color.colorOrange));
			else
				tableRow.setBackgroundColor(context.getResources().getColor(
						R.color.colorCyan));
		}

		/**
		 * UNA TEXTVIEW PER UN CAMPO DI VALORI
		 * 
		 * @param value
		 * @return
		 */
		private EditText getEditTextForColumnValue(String value) {
			// TODO Auto-generated method stub
			Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"font/Roboto-Bold.ttf");
			EditText eText = new EditText(context);
			eText.setText(value);
			eText.setMaxLines(1);
			eText.setHorizontallyScrolling(true);
			eText.setTextAppearance(context,
					android.R.style.TextAppearance_Small);
			eText.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.cell_shape));
			eText.setTextSize(12);
			eText.setTypeface(tf);

			return eText;
		}

		/**
		 * COSTRUISCE LA RIGA DI INTESTAZIONI DELLA TABELLA RISULTATO DELL'SQL
		 * 
		 * @param tlSqliteSql
		 * 
		 * @param columnsCount
		 * @param columnNames
		 * @return
		 */
		private TableRow buildRowIntestazione(int columnsCount,
				String[] columnNames) {
			// TODO Auto-generated method stub
			TableRow tableRow = new TableRow(context);

			tableRow.setLayoutParams(new TableRow.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT, 1));
			tableRow.setPadding(0, 0, 0, 10);

			for (int i = 0; i < columnsCount; i++) {
				TextView columnName = getTextViewForColumnName(columnNames[i]);

				columnName.setLayoutParams(new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT, 0.8f));

				tableRow.addView(columnName);
			}
			return tableRow;
		}

		/**
		 * UNA TEXTVIEW PER UN CAMPO DI INTESTAZIONE TABELLA
		 * 
		 * @param columnName
		 * @return
		 */
		private TextView getTextViewForColumnName(String columnName) {
			// TODO Auto-generated method stub
			Typeface tf = Typeface.createFromAsset(context.getAssets(),
					"font/Roboto-Bold.ttf");
			TextView textView = new TextView(context);
			textView.setText(columnName);
			textView.setMaxLines(1);
			textView.setHorizontallyScrolling(true);
			textView.setTextSize(12f);
			textView.setTextAppearance(context,
					android.R.style.TextAppearance_Medium);
			textView.setBackgroundDrawable(context.getResources().getDrawable(
					R.drawable.cell_shape));
			textView.setGravity(Gravity.CENTER);
			textView.setTypeface(tf);
			textView.setPadding(5, 0, 5, 0);
			return textView;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(context, "onClick", Toast.LENGTH_SHORT).show();
		}

		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// // TODO Auto-generated method stub
		// if (v instanceof TableRow) {
		// TableRow trow = (TableRow) v;
		// trow.getChildCount();
		//
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// Object field = trow.getChildAt(0);
		// if (field instanceof EditText) {
		// EditText tvID = (EditText) trow.getChildAt(0);
		// // IMPACCHETTA L'ID DELL'UTENTE DA CANCELLARE o UPDATARE
		// if (tablename != null) {
		// if (tablename.equals(TABLE_UTENTI_SCUOLA)) {
		// long id_utente = 0;
		// Bundle backPack = null;
		// try {
		// id_utente = Long.valueOf(tvID.getText()
		// .toString());
		// } catch (NumberFormatException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// Toast.makeText(
		// context,
		// "ID MISSING! : "
		// + TABLE_UTENTI_SCUOLA
		// + " ERROR:"
		// + e.getMessage(),
		// Toast.LENGTH_SHORT).show();
		// break;
		// }
		//
		// // TORNA A UTENTI_SCUOLA MA LO DEVE FARE PER
		// // QUALSIASI TABELLA
		// backPack = new Bundle();
		// backPack.putLong("id_row", id_utente);
		//
		// Intent utente = new Intent(
		// "android.intent.action.UTENTI_SCUOLA");
		// utente.putExtras(backPack);
		// Toast.makeText(
		// context,
		// "onTouch:" + v.toString() + " UTENTE:"
		// + id_utente, Toast.LENGTH_SHORT)
		// .show();
		// sqLite.startActivity(utente);
		// sqLite.finish();
		// }
		//
		// }
		// }
		// break;
		// case MotionEvent.ACTION_UP:
		// // setTextViewDefaultBackgroundColor(trow, childCount, 0);
		// break;
		// case MotionEvent.ACTION_MOVE:
		// break;
		// default:
		// break;
		// }
		//
		// }
		// return true;
		// // return gestureDetector.onTouchEvent(event);
		// }
		//
		@Override
		public void onFocusChange(View view, boolean focus) {
			// TODO Auto-generated method stub
			if (view instanceof EditText) {
			}
		}

		/**
		 * SELECT DISTINCT column_name,column_name FROM table_name;
		 * 
		 * @param db
		 * @param dbname
		 * @param tableName
		 * @param fieldName
		 * @return
		 */
		public List<String> getUniqueValuesFor(SQLiteDatabase db,
				String dbname, String tableName, String fieldName) {
			// TODO Auto-generated method stub
			ArrayList<String> arrTblNames = new ArrayList<String>();
			Cursor c = db.rawQuery("SELECT DISTINCT " + fieldName + " FROM "
					+ tableName + " WHERE length(trim(" + fieldName
					+ ")) > 0 ORDER BY " + fieldName + " ASC;", null);

			if (c.moveToFirst()) {
				while (!c.isAfterLast()) {
					String value = c.getString(c.getColumnIndex(fieldName));
					if (value.length() > 0)
						arrTblNames.add(value);
					c.moveToNext();
				}
			}
			return arrTblNames;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub

			if (v instanceof TableRow) {
				TableRow trow = (TableRow) v;
				trow.getChildCount();

				Object field = trow.getChildAt(0);
				if (field instanceof EditText) {
					// IMPACCHETTA L'ID DELL'UTENTE DA CANCELLARE o UPDATARE
					if (tablename != null) {
						EditText tvID = (EditText) trow.getChildAt(0);
						if (tablename.equals(TABLE_UTENTI_SCUOLA)) {
							long id_utente = 0;
							Bundle backPack = null;
							try {
								id_utente = Long.valueOf(tvID.getText()
										.toString());
							} catch (NumberFormatException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								Toast.makeText(
										context,
										"ID MISSING! : " + TABLE_UTENTI_SCUOLA
												+ " ERROR:" + e.getMessage(),
										Toast.LENGTH_SHORT).show();
								return false;
							}

							// TORNA A UTENTI_SCUOLA MA LO DEVE FARE PER
							// QUALSIASI TABELLA
							backPack = new Bundle();
							backPack.putLong("id_row", id_utente);

							Intent utente = new Intent(
									"android.intent.action.UTENTI_SCUOLA");
							utente.putExtras(backPack);
							Toast.makeText(
									context,
									"onLongClick : " + v.toString()
											+ " UTENTE:" + id_utente,
									Toast.LENGTH_SHORT).show();

							sqLite.startActivity(utente);
							sqLite.finish();
						} else {
							Toast.makeText(
									context,
									"ScuolaDb.java : onLongClick on "
											+ tablename
											+ " NOT YET IMPLEMENTED!",
									Toast.LENGTH_SHORT).show();
						}

					}
				}

			}
			return true;
		}

	}

	/**********************************************************************/
	/***************************** FINE DbHelper **************************/
	/**********************************************************************/
	public long createEntry(String tableName, ArrayList<String> listFieldNames,
			ArrayList<String> listFieldVAlues) throws SQLException {
		// TODO Auto-generated method stub
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			return UtentiScuolaTable.createEntry(ourDatabase, tableName,
					listFieldNames, listFieldVAlues);
		}
		return -1;
	}

	public String getNumRighe(String tableName) throws SQLException {
		// TODO Auto-generated method stub
		String result = null;
		if (tableName.equals(TABLE_UTENTI_SCUOLA))
			return UtentiScuolaTable.getNumRighe(ourDatabase, tableName);
		return result;
	}

	public ArrayList<String[]> getDataAsArray(String tableName)
			throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String[]> mySb = new ArrayList<String[]>();
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			return UtentiScuolaTable.getDataAsArray(ourDatabase, tableName);
		}
		return mySb;
	}

	public ArrayList<String> getRow(String tableName, long idLong)
			throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String> rowData = null;
		if (tableName.equals(TABLE_UTENTI_SCUOLA))
			rowData = UtentiScuolaTable.getDataUtenteScuola(ourDatabase,
					tableName, idLong);

		return rowData;
	}

	public long updateEntry(String tableName, ArrayList<String> listFieldNames,
			ArrayList<String> listFieldValues) throws SQLException {
		// TODO Auto-generated method stub
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			return UtentiScuolaTable.updateEntry(ourDatabase, tableName,
					listFieldNames, listFieldValues);
		}
		return -1;
	}

	public long deleteEntry(String tableName, long idLong) throws SQLException {
		// TODO Auto-generated method stub
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			return UtentiScuolaTable.deleteEntry(ourDatabase,
					TABLE_UTENTI_SCUOLA, idLong);
		}
		return -1;
	}

	public ArrayList<String> getTablesNames(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		ArrayList<String> arrTblNames = new ArrayList<String>();
		Cursor c = db
				.rawQuery(
						"SELECT name FROM sqlite_master WHERE type='table' ORDER BY sqlite_master.name ASC",
						null);

		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				arrTblNames.add(c.getString(c.getColumnIndex("name")));
				c.moveToNext();
			}
		}

		return arrTblNames;

	}

	@Override
	public ArrayList<String> getTablesNames() {
		ArrayList<String> arrTblNames = new ArrayList<String>();
		Cursor c = ourDatabase
				.rawQuery(
						"SELECT name FROM sqlite_master WHERE type='table' ORDER BY sqlite_master.name ASC",
						null);

		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				arrTblNames.add(c.getString(c.getColumnIndex("name")));
				c.moveToNext();
			}
		}

		return arrTblNames;
	}

	@Override
	public ArrayList<TableColumnsInfo> getColumnsInfo(
			ArrayList<String> arrTblNames) {

		ArrayList<TableColumnsInfo> tableColumnsInfoArray = new ArrayList<TableColumnsInfo>();
		if (arrTblNames.size() == 0)
			return tableColumnsInfoArray;

		for (int j = 0; j < arrTblNames.size(); j++) {
			String tblName = arrTblNames.get(j);

			// GETS COLUMNINFOs
			ArrayList<ColumnInfo> colInfoArray = ourHelper
					.getColumnInfoFromTableWithoutFieldToDrop(ourDatabase,
							tblName, null);

			TableColumnsInfo tblInfo = new TableColumnsInfo(tblName,
					colInfoArray);
			tableColumnsInfoArray.add(tblInfo);
		}
		return tableColumnsInfoArray;

	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public ArrayList<String> getfieldNames(SQLiteDatabase ourDatabase,
			String tablename) {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		return ourHelper.getFieldNames(ourDatabase, tablename);

	}

	public List<String> getUniqueValuesFor(SQLiteDatabase ourDatabase,
			String dbname, String tableName, String fieldName) {
		// TODO Auto-generated method stub
		ourHelper = new DbHelper(ourContext, dbName, DATABASE_VERSION);
		return ourHelper.getUniqueValuesFor(ourDatabase, dbname, tableName,
				fieldName);
	}

}
