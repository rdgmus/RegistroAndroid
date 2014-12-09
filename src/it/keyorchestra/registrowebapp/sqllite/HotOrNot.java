package it.keyorchestra.registrowebapp.sqllite;

import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HotOrNot implements DatabasesInterface {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "person_name";
	public static final String KEY_HOTNESS = "person_hotness";

	private static final String DATABASE_NAME = "HotOrNotDb";
	private static final String DATABASE_TABLE = "peopleTable";
	private final static int DATABASE_VERSION = 1;

	private DbHelper ourHelper;
	private final Context ourContext;
	private SQLiteDatabase ourDatabase;

	public HotOrNot(Context c) {
		ourContext = c;
	}

	public HotOrNot open() throws SQLException {
		ourHelper = new DbHelper(ourContext);
		ourDatabase = ourHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		// TODO Auto-generated method stub
		ourHelper.close();
	}

	private static class DbHelper extends SQLiteOpenHelper {

		public DbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub

		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" + KEY_ROWID
					+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_NAME
					+ " TEXT NOT NULL, " + KEY_HOTNESS + " TEXT NOT NULL);");
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE + ";");
			onCreate(db);
		}

	}

	public long createEntry(String name, String hotness) throws SQLException {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, name);
		cv.put(KEY_HOTNESS, hotness);

		// return the ROW_ID of new inserted row or -1 if error occurred
		return ourDatabase.insert(DATABASE_TABLE, null, cv);

	}

	public String getData() throws SQLException {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_HOTNESS };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null,
				null, null);
		String result = "";
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iName = c.getColumnIndex(KEY_NAME);
		int iHotness = c.getColumnIndex(KEY_HOTNESS);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result += c.getString(iRow) + " " + c.getString(iName) + " "
					+ c.getString(iHotness) + "\n";
		}

		return result;
	}

	public String getName(long l) throws SQLException {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_HOTNESS };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "="
				+ l, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
			String name = c.getString(1);
			return name;
		}
		return null;
	}

	public String getHotness(long l) throws SQLException {
		// TODO Auto-generated method stub
		String[] columns = new String[] { KEY_ROWID, KEY_NAME, KEY_HOTNESS };
		Cursor c = ourDatabase.query(DATABASE_TABLE, columns, KEY_ROWID + "="
				+ l, null, null, null, null);
		if (c != null) {
			c.moveToFirst();
			String hotness = c.getString(2);
			return hotness;
		}
		return null;
	}

	/**
	 * RETURN il numero di rows updatate
	 * 
	 * @param lRow
	 * @param sName
	 * @param sHotness
	 * @return
	 * @throws SQLException
	 */
	public int updateEntry(long lRow, String sName, String sHotness)
			throws SQLException {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME, sName);
		cv.put(KEY_HOTNESS, sHotness);
		return ourDatabase.update(DATABASE_TABLE, cv, KEY_ROWID + "=" + lRow,
				null);
	}

	public int deleteEntry(long lRow1) throws SQLException {
		// TODO Auto-generated method stub
		return ourDatabase
				.delete(DATABASE_TABLE, KEY_ROWID + "=" + lRow1, null);
	}

	@Override
	public ArrayList<String> getTablesNames() {
		// TODO Auto-generated method stub
		ArrayList<String> arrTblNames = new ArrayList<String>();
		Cursor c = ourDatabase.rawQuery(
				"SELECT name FROM sqlite_master WHERE type='table'", null);

		if (c.moveToFirst()) {
			while (!c.isAfterLast()) {
				arrTblNames.add(c.getString(c.getColumnIndex("name")));
				c.moveToNext();
			}
		}
		return arrTblNames;
	}

	@SuppressWarnings("unused")
	@Override
	public ArrayList<TableColumnsInfo> getColumnsInfo(
			ArrayList<String> arrTblNames) {
		// TODO Auto-generated method stub
		ArrayList<TableColumnsInfo> tableColumnsInfoArray = new ArrayList<TableColumnsInfo>();
		if (arrTblNames.size() == 0)
			return tableColumnsInfoArray;

		for (int j = 0; j < arrTblNames.size(); j++) {
			String tblName = arrTblNames.get(j);
			String[] columnNames;

			Cursor c = ourDatabase.rawQuery("SELECT * FROM " + tblName
					+ " WHERE 0", null);
			try {
				columnNames = c.getColumnNames();
			} finally {
				c.close();
			}

			// SQLiteDatabase db = ourHelper.getWritableDatabase();
			Cursor cursor = ourDatabase.rawQuery("PRAGMA table_info(" + tblName
					+ ")", null);
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
			TableColumnsInfo tblInfo = new TableColumnsInfo(tblName,
					colInfoArray);
			tableColumnsInfoArray.add(tblInfo);
		}
		return tableColumnsInfoArray;

	}

}
