package it.keyorchestra.registrowebapp.dbMatthed;

import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DatabaseOps implements DatabasesInterface {

	private  String getUrl(Context context) {
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

	public   String FetchConnection(Context context) {
		String retval = "";

		String url = getUrl(context);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(0);
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

	public  boolean AuthenticateUser(Context context, String sEmail, String sPasswd) {
		String retval = "";

		String url = getUrl(context);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(5);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			byte[] encodingPasswd = sPasswd.getBytes();
			
			sql = "SELECT * FROM utenti_scuola WHERE email= '"+sEmail+
					"' AND password = '"+ android.util.Base64.encode(
							android.util.Base64.encode(encodingPasswd, 0),0)+"'";
			ResultSet rs = st.executeQuery(sql);
			while (rs.next()) {
				retval = rs.getString(1);
				Toast.makeText(context,
						"rs.getString(1): " + retval,
						Toast.LENGTH_LONG).show();

			}
			rs.close();
			st.close();
			conn.close();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
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
}
