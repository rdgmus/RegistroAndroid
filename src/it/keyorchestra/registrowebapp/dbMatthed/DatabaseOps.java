package it.keyorchestra.registrowebapp.dbMatthed;

import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface;
import it.keyorchestra.registrowebapp.mysqlandroid.MySqlAndroid;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DatabaseOps implements DatabasesInterface {

	private String getUrl(Context context) {
		String ip = null;
		String userName = null;
		String userPasswd = null;
		String port = null;
		String schema = null;
		String url = "";

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		String defaultDatabase = getPrefs.getString("databaseList", "1");

		try {
			if (defaultDatabase.contentEquals("PostgreSQL")) {
				ip = getPrefs.getString("ipPostgreSQL", "");
				userName = getPrefs.getString("userNamePostgreSQL", "");
				userPasswd = getPrefs.getString("userPasswdPostgreSQL", "");
				port = getPrefs.getString("portPostgreSQL", "");
				schema = getPrefs.getString("schemaPostgreSQL", "");

				Class.forName("org.postgresql.Driver");

				// "jdbc:postgresql://192.168.0.150:5432/postgres?user=postgres&password=iw3072yl";
				url = "jdbc:postgresql://" + ip + ":" + port + "/" + schema
						+ "?user=" + userName + "&password=" + userPasswd;

			} else if (defaultDatabase.contentEquals("MySQL")) {
				ip = getPrefs.getString("ipMySQL", "");
				userName = getPrefs.getString("userNameMySQL", "");
				userPasswd = getPrefs.getString("userPasswdMySQL", "");
				port = getPrefs.getString("portMySQL", "");
				schema = getPrefs.getString("schemaMySQL", "");

				Class.forName("com.mysql.jdbc.Driver");

				// "jdbc:mysql://192.168.0.110:3306/scuola?user=root&password=iw3072ylA";
				url = "jdbc:mysql://" + ip + ":" + port + "/" + schema
						+ "?user=" + userName + "&password=" + userPasswd;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return url;
	}

	public String FetchConnection(Context context) {
		String retval = "";

		String url = getUrl(context);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql;
			sql = "SELECT 1";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				retval = rs.getString(1);
			}
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			retval = e.toString();
		}
		return retval;
	}

	public boolean AuthenticateUser(Context context, String sEmail,
			String sPasswd, String ip, String phpencoder) {

		boolean isAuthenticated = false;

		String url = getUrl(context);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			String encoded = new MySqlAndroid().getEncodedStringFromUri(
					context, "http://" + ip + "/" + phpencoder
							+ "?actionEncode=encodePassword&password="
							+ sPasswd);

			// Toast.makeText(context, "Password encoded:" + encoded,
			// Toast.LENGTH_LONG).show();

			sql = "SELECT id_utente, cognome, nome, email, password, user_is_admin, has_to_change_password, is_locked "
					+ " FROM utenti_scuola WHERE email= '"
					+ sEmail
					+ "' AND password = '" + encoded + "'";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				int id_utente = rs.getInt("id_utente");
				String cognome = rs.getString("cognome");
				String nome = rs.getString("nome");
				String email = rs.getString("email");
				int user_is_admin = rs.getInt("user_is_admin");
				int has_to_change_password = rs
						.getInt("has_to_change_password");
				int is_locked = rs.getInt("is_locked");

				// Valorizzare le preferenze con le informazioni estratte
				salvaUtenteNellePreferenze(id_utente, cognome, nome, email,
						user_is_admin, has_to_change_password, is_locked,
						context);

				Toast.makeText(
						context,
						"Utente riconosciuto: [" + id_utente + "] " + cognome + " "
								+ nome, Toast.LENGTH_LONG).show();
				isAuthenticated = true;
			}
			rs.close();
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isAuthenticated;
	}

	@SuppressLint("NewApi")
	private void salvaUtenteNellePreferenze(int id_utente, String cognome,
			String nome, String email, int user_is_admin,
			int has_to_change_password, int is_locked, Context context) {
		// TODO Auto-generated method stub
		SharedPreferences sharedpreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedpreferences.edit();
		editor.putLong("id_utente", id_utente);
		editor.putString("cognome", cognome);
		editor.putString("nome", nome);
		editor.putString("email", email);
		editor.putLong("user_is_admin", user_is_admin);
		editor.putLong("has_to_change_password", has_to_change_password);
		editor.putLong("is_locked", is_locked);
		editor.apply();

	}

	@Override
	public ArrayList<String> getTablesNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<TableColumnsInfo> getColumnsInfo(
			ArrayList<String> arrTblNames) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Controlla che l'utente che sta tenando il log abbia il ruolo con il quale
	 * vuole entrare nel registro scolastico. Le informazioni sull'utente sono
	 * state salvate nelle preferenze in AuthenticateUser(){}
	 * 
	 * @param ruoloScelto
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public boolean LoggingUserHasRole(String ruoloScelto, Context context,
			String ip) {
		// TODO Auto-generated method stub
		String url = getUrl(context);

		SharedPreferences sharedpreferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		Long id_utente = sharedpreferences.getLong("id_utente", -1);
		if (id_utente >= 0) {
			Connection conn;
			try {
				DriverManager.setLoginTimeout(15);
				conn = DriverManager.getConnection(url);
				Statement st = conn.createStatement();
				String sql = null;

				sql = "SELECT id_ruoli_granted, id_utente, id_ruolo, ruolo FROM ruoli_granted_to_utenti"
						+ " WHERE id_utente = " + id_utente;
				ResultSet rs = st.executeQuery(sql);
				while (rs.next()) {
					// Valorizzare le preferenze con le informazioni estratte
					String ruolo = rs.getString("ruolo");
					if (ruolo.equals(ruoloScelto.toUpperCase())) {
						rs.close();
						st.close();
						conn.close();
						return true;
					}
				}
				rs.close();
				st.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
