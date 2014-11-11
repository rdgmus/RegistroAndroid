package it.keyorchestra.registrowebapp.scuola.tables;

import it.keyorchestra.registrowebapp.scuola.util.EncoderBase64;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class UtentiScuolaTable extends EncoderBase64 {
	public static final String KEY_ID_UTENTE = "id_utente";
	public static final String KEY_COGNOME = "cognome";
	public static final String KEY_NOME = "nome";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	public static final String KEY_ID_RUOLO = "id_ruolo";

	public static final String TABLE_UTENTI_SCUOLA = "utenti_scuola";

	final static String[] columns = new String[] { KEY_ID_UTENTE, KEY_COGNOME,
			KEY_NOME, KEY_EMAIL, KEY_PASSWORD, KEY_ID_RUOLO };

	public static final ArrayList<String> listFieldNames() {
		ArrayList<String> listFieldNames = new ArrayList<String>();
		for (int i = 0; i < columns.length; i++) {
			listFieldNames.add(columns[i]);
		}
		return listFieldNames;
	}
	
	public static long deleteEntry(SQLiteDatabase ourDatabase,String tableName, long idLong) throws SQLException {
		// TODO Auto-generated method stub
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			return ourDatabase.delete(TABLE_UTENTI_SCUOLA, KEY_ID_UTENTE + "="
					+ idLong, null);
		}
		return -1;
	}

	public static long updateEntry(SQLiteDatabase ourDatabase, String tableName,
			ArrayList<String> listFieldNames, ArrayList<String> listFieldValues)
			throws SQLException {
		// TODO Auto-generated method stub

		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			ContentValues cv = new ContentValues();
			for (int i = 0; i < listFieldNames.size(); i++) {
				if (listFieldNames.get(i).equals(KEY_PASSWORD))
					cv.put(listFieldNames.get(i),
							codificaBase64(listFieldValues.get(i)));
				else if (listFieldNames.get(i).equals(KEY_ID_UTENTE))
					cv.put(listFieldNames.get(i), Long.parseLong(listFieldValues.get(i)));
				else
					cv.put(listFieldNames.get(i), listFieldValues.get(i));
			}
			return ourDatabase.update(TABLE_UTENTI_SCUOLA, cv, KEY_ID_UTENTE
					+ "=" + Long.parseLong(listFieldValues.get(0)), null);
		}
		return -1;
	}

	public static long createEntry(SQLiteDatabase ourDatabase,
			String tableName, ArrayList<String> listFieldNames,
			ArrayList<String> listFieldValues) throws SQLException {
		// TODO Auto-generated method stub
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			ContentValues cv = new ContentValues();
			for (int i = 0; i < listFieldNames.size(); i++) {
				if (listFieldNames.get(i).equals(KEY_PASSWORD))
					cv.put(listFieldNames.get(i),
							codificaBase64(listFieldValues.get(i)));
				else if (listFieldNames.get(i).equals(KEY_ID_UTENTE))
					;
				else
					cv.put(listFieldNames.get(i), listFieldValues.get(i));
			}
			return ourDatabase.insert(TABLE_UTENTI_SCUOLA, null, cv);
		}
		return -1;
	}

	public static long createEntry(SQLiteDatabase ourDatabase,
			String tableName, String surname, String name, String email,
			String passwd) throws SQLException {
		// TODO Auto-generated method stub
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			ContentValues cv = new ContentValues();
			cv.put(KEY_COGNOME, surname);
			cv.put(KEY_NOME, name);
			cv.put(KEY_EMAIL, email);
			cv.put(KEY_PASSWORD, codificaBase64(passwd));

			// return the ROW_ID of new inserted row or -1 if error occurred
			return ourDatabase.insert(TABLE_UTENTI_SCUOLA, null, cv);
		}
		return -1;
	}

	public static ArrayList<String> getDataUtenteScuola(
			SQLiteDatabase ourDatabase, String tableName, long id_utente)
			throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String> datiUtente = new ArrayList<String>();

		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			Cursor c = ourDatabase.query(TABLE_UTENTI_SCUOLA, columns,
					KEY_ID_UTENTE + "=" + id_utente, null, null, null, null);
			c.moveToFirst();
			int iRow = c.getColumnIndex(KEY_ID_UTENTE);
			int iName = c.getColumnIndex(KEY_NOME);
			int iSurname = c.getColumnIndex(KEY_COGNOME);
			int iEmail = c.getColumnIndex(KEY_EMAIL);
			int iPassword = c.getColumnIndex(KEY_PASSWORD);
			int iIdRuolo = c.getColumnIndex(KEY_ID_RUOLO);

			datiUtente.add(c.getString(iRow));
			datiUtente.add(c.getString(iSurname));
			datiUtente.add(c.getString(iName));
			datiUtente.add(c.getString(iEmail));
			datiUtente.add(decodificaBase64(c.getString(iPassword)));
			datiUtente.add(c.getString(iIdRuolo));
		}
		return datiUtente;

	}

	public static ArrayList<String[]> getDataAsArray(
			SQLiteDatabase ourDatabase, String tableName) throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<String[]> mySb = new ArrayList<String[]>();
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {

			Cursor c = ourDatabase.query(TABLE_UTENTI_SCUOLA, columns, null,
					null, null, null, null);
			int iRow = c.getColumnIndex(KEY_ID_UTENTE);
			int iName = c.getColumnIndex(KEY_NOME);
			int iSurname = c.getColumnIndex(KEY_COGNOME);
			int iEmail = c.getColumnIndex(KEY_EMAIL);
			int iPassword = c.getColumnIndex(KEY_PASSWORD);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				// result += c.getString(iRow) + " " + c.getString(iSurname) +
				// " "
				// + c.getString(iName) + " " + c.getString(iEmail) + " "
				// + c.getString(iPassword) + "\n";
				String[] row = new String[5];
				row[0] = c.getString(iRow);
				row[1] = c.getString(iSurname);
				row[2] = c.getString(iName);
				row[3] = c.getString(iEmail);
				row[4] = c.getString(iPassword);
				mySb.add(row);
			}
		}
		return mySb;
	}

	public static String getNumRighe(SQLiteDatabase ourDatabase,
			String tableName) throws SQLException {
		// TODO Auto-generated method stub
		String result = null;
		if (tableName.equals(TABLE_UTENTI_SCUOLA)) {
			Cursor c = ourDatabase.query(TABLE_UTENTI_SCUOLA, columns, null,
					null, null, null, null);
			result = "Righe:" + c.getCount() + "\n";
		}
		return result;
	}

}
