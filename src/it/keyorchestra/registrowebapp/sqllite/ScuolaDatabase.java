package it.keyorchestra.registrowebapp.sqllite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class ScuolaDatabase extends SQLiteAssetHelper {
	public static final String KEY_ROWID = "_id";
	public static final String KEY_SURNAME = "cognome";
	public static final String KEY_NAME = "nome";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";

	private static final String DATABASE_NAME = "scuola";
	private static final String DATABASE_TABLE = "utenti_scuola";
	private static final int DATABASE_VERSION = 1;
	private SQLiteDatabase ourDatabase;

	public ScuolaDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public ScuolaDatabase open() throws SQLException {
		ourDatabase = getWritableDatabase();
		return this;
	}

	public void close() {
		// TODO Auto-generated method stub
		ourDatabase.close();
	}

	public Cursor getUtentiScuola() throws SQLException {

		ourDatabase = getReadableDatabase();
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		String[] sqlSelect = { "0 _id", "cognome", "nome", "email", "password" };

		qb.setTables(DATABASE_TABLE);
		Cursor c = qb.query(ourDatabase, sqlSelect, null, null, null, null,
				null);

		c.moveToFirst();
		return c;

	}

	public long createEntry(String name, String hotness) throws SQLException {
		// TODO Auto-generated method stub
		ContentValues cv = new ContentValues();
		cv.put(KEY_NAME,"Armando");
		cv.put(KEY_SURNAME,"Armando");
		cv.put(KEY_EMAIL,"Armando");
		cv.put(KEY_PASSWORD,"Armando");

		// return the ROW_ID of new inserted row or -1 if error occurred
		return ourDatabase.insert(DATABASE_TABLE, null, cv);

	}
	public String getData() throws SQLException {
		// TODO Auto-generated method stub
		String[] columns = new String[] { "_id", "cognome", "nome", "email",
				"password" };
		Cursor c = ourDatabase.query("utenti_scuola", columns, null, null,
				null, null, null);
		String result = "";
		int iRow = c.getColumnIndex(KEY_ROWID);
		int iName = c.getColumnIndex(KEY_NAME);
		int iSurname = c.getColumnIndex(KEY_SURNAME);
		int iEmail = c.getColumnIndex(KEY_EMAIL);
		int iPassword = c.getColumnIndex(KEY_PASSWORD);

		for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
			result += c.getString(iRow) + " " + c.getString(iName) + " "
					+ c.getString(iSurname) + c.getString(iEmail)
					+ c.getString(iPassword) + "\n";
		}

		return result;
	}
}
