package it.keyorchestra.registrowebapp.dbMatthed;

import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface;
import it.keyorchestra.registrowebapp.mysqlandroid.MySqlAndroid;
import it.keyorchestra.registrowebapp.scuola.util.GMailSender;
import it.keyorchestra.registrowebapp.scuola.util.SimpleMail2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * In questa classe si effettuano query al database MySQL direttamente
 * ricevendone i risultati.
 * 
 * @author rdgmus
 * 
 */
public class DatabaseOps implements DatabasesInterface {
	SharedPreferences getPrefs;
	Long id_utente;
	String cognome;
	String nome;
	String email;
	Long user_is_admin;
	Long has_to_change_password;
	Long is_locked;

	public DatabaseOps(Context context) {
		super();
		getPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	private String getUrl(Context context) {
		String ip = null;
		String userName = null;
		String userPasswd = null;
		String port = null;
		String schema = null;
		String url = "";

		// SharedPreferences getPrefs = PreferenceManager
		// .getDefaultSharedPreferences(context);

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

	public boolean AuthenticateUser(Context applicationContext, String sEmail,
			String sPasswd, String ip, String phpencoder) {

		boolean isAuthenticated = false;

		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			String encoded = encodePassword(applicationContext, ip, phpencoder,
					sPasswd);

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
						applicationContext);

				Toast.makeText(
						applicationContext,
						"Utente riconosciuto: [" + id_utente + "] " + cognome
								+ " " + nome, Toast.LENGTH_LONG).show();
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
	private void salvaUtenteNellePreferenze(long id_utente, String cognome,
			String nome, String email, long user_is_admin,
			long has_to_change_password, long is_locked, Context context) {
		// TODO Auto-generated method stub
		SharedPreferences.Editor editor = getPrefs.edit();
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
			String databaseIp) {
		// TODO Auto-generated method stub
		String url = getUrl(context);

		Long id_utente = getPrefs.getLong("id_utente", -1);
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

	public void LockUserLogging(Context applicationContext, String databaseIp) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);

		Long id_utente = getPrefs.getLong("id_utente", -1);
		if (id_utente >= 0) {
			Connection conn;
			try {
				DriverManager.setLoginTimeout(15);
				conn = DriverManager.getConnection(url);
				Statement st = conn.createStatement();
				String sql = null;

				sql = "UPDATE utenti_scuola SET is_locked=1 WHERE id_utente = "
						+ id_utente;
				int result = st.executeUpdate(sql);
				if (result == 1) {
					Toast.makeText(
							applicationContext,
							"Utente protetto da altri tentativi di accesso\n"
									+ "con le stesse credenziali!",
							Toast.LENGTH_LONG).show();
				}
				st.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void UnlockUser(Context applicationContext, String databaseIp,
			Long id_utente) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);

		if (id_utente >= 0) {
			Connection conn;
			try {
				DriverManager.setLoginTimeout(15);
				conn = DriverManager.getConnection(url);
				Statement st = conn.createStatement();
				String sql = null;

				sql = "UPDATE utenti_scuola SET is_locked=0 WHERE id_utente = "
						+ id_utente;
				int result = st.executeUpdate(sql);
				if (result == 1) {
					Toast.makeText(applicationContext, "Utente sbloccato!",
							Toast.LENGTH_LONG).show();
				}
				st.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressLint("DefaultLocale")
	public boolean RegisterNewUser(Context applicationContext, String nome,
			String cognome, String email, String passwd, String ip,
			String phpencoder) {
		// TODO Auto-generated method stub
		boolean hasBeenRegistered = false;

		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			// Encode password
			String encoded = encodePassword(applicationContext, ip, phpencoder,
					passwd);

			Toast.makeText(applicationContext, "Password encoded:" + encoded,
					Toast.LENGTH_LONG).show();

			// Generate HASH per conferma email
			String hash = generateHash(applicationContext, ip, phpencoder);
			Toast.makeText(applicationContext, "Hash generata:" + hash,
					Toast.LENGTH_LONG).show();
			// SQL
			sql = String
					.format("INSERT INTO utenti_scuola(cognome, nome, email, password, "
							+ "hash, register_date, user_is_admin, has_to_change_password, is_locked,"
							+ "email_confirmed) "
							+ " VALUES ('%s','%s','%s','%s','%s',NOW(),0,0,1,0)",
							cognome.toUpperCase(), nome.toUpperCase(), email,
							encoded, hash);
			int rs = st.executeUpdate(sql);
			if (rs == 1) {

				sendRequestConfirmEmail(applicationContext, ip, phpencoder,
						cognome.toUpperCase(Locale.getDefault()),
						nome.toUpperCase(Locale.getDefault()), email, hash);

				Toast.makeText(
						applicationContext,
						"E' stato registrato un nuovo account per:"
								+ nome.toUpperCase(Locale.getDefault()) + " "
								+ cognome.toUpperCase(Locale.getDefault())
								+ "!\n"
								+ "Non ha ancora alcun ruolo accreditato !\n"
								+ "Riceverà un email all'indirizzo Email: "
								+ email + "\n"
								+ "per la conferma dell'iscrizione,\n"
								+ "e quando un ADMIN effettuerà lo sblocco\n"
								+ "del suo account fornendole un Ruolo.\n"
								+ "Grazie della sua iscrizione!",
						Toast.LENGTH_LONG).show();
				hasBeenRegistered = true;
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hasBeenRegistered;
	}

	private void sendRequestConfirmEmail(Context applicationContext, String ip,
			String phpencoder, String cognome, String nome, String email,
			String hash) {
		// TODO Auto-generated method stub
		/**
		 * Prepare Email
		 */
		Toast.makeText(applicationContext,
				"Invio email per richiesta conferma all'utente!",
				Toast.LENGTH_LONG).show();

		String emailaddress[] = { email };
		String body = requestConfirmEmailBody(applicationContext, ip,
				phpencoder, cognome, nome, email, hash);
		String subject = "Conferma email per iscrizione al Registro Scolastico";

		/**
		 * Invia email TODO: rivedere modalità di invio
		 */
		GMailSenderHtmlEmail(applicationContext, emailaddress, subject, body);

		// SimpleMail2Email(applicationContext, email,
		// subject, body, "file:///android_res/drawable/cbasso1.png",
		// "<cbasso1>" );
	}

	private String generateHash(Context applicationContext, String ip,
			String phpencoder) {
		// TODO Auto-generated method stub
		// Genera hash tramite php
		String hash = new MySqlAndroid().getEncodedStringFromUri(
				applicationContext, "http://" + ip + "/" + phpencoder
						+ "?actionEncode=generateHash");
		return hash;
	}

	private String encodePassword(Context applicationContext, String ip,
			String phpencoder, String passwd) {
		String encoded = new MySqlAndroid().getEncodedStringFromUri(
				applicationContext, "http://" + ip + "/" + phpencoder
						+ "?actionEncode=encodePassword&password=" + passwd);

		return encoded;

	}

	private String requestConfirmEmailBody(Context applicationContext,
			String ip, String phpencoder, String cognome, String nome,
			String email, String hash) {
		String body = new MySqlAndroid().getEncodedStringFromUri(
				applicationContext, "http://" + ip + "/" + phpencoder
						+ "?actionEncode=requestConfirmEmail" + "&cognome="
						+ cognome + "&nome=" + nome + "&email=" + email
						+ "&hash=" + hash + "&ip=" + ip);
		return body;
	}

	/**
	 * Invia email all'utente aprendo l'attività android.email in modo che
	 * l'utente possa cambiare i parametri (subject, message, etc..) della email
	 * e poi inviarla cliccando aul bottone Invia Email in alto a destra.
	 * 
	 * @param callingActivity
	 * @param emailaddress
	 * @param message
	 * @param subject
	 * @return
	 */
	public void AndroidEmail(Activity callingActivity, String emailaddress[],
			String message, String subject) {
		// TODO Auto-generated method stub
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject);
		emailIntent.setType("html/text");
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
		// callingActivity.startActivity(emailIntent);
		PackageManager pm = callingActivity.getBaseContext()
				.getPackageManager();
		List<ResolveInfo> activityList = pm.queryIntentActivities(emailIntent,
				0);
		for (final ResolveInfo app : activityList) {
			if ((app.activityInfo.name).contains("android.email")) {
				final ActivityInfo activity = app.activityInfo;
				final ComponentName name = new ComponentName(
						activity.applicationInfo.packageName, activity.name);
				emailIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
				emailIntent.setComponent(name);
				callingActivity.getBaseContext().startActivity(emailIntent);
				break;
			}
		}
	}

	public void SimpleMail2Email(Context context, String emailTo,
			String subject, String body, String imgPath, String contentId) {
		SimpleMail2 simpleMail2 = new SimpleMail2();
		simpleMail2.send(emailTo, subject, body, imgPath, contentId);
	}

	public void GMailSenderHtmlEmail(Context context, String emailaddress[],
			String subject, String body) {
		GMailSender mailsender = new GMailSender("keyorchestra2014@gmail.com",
				"diavgek@");
		// Aggiunge il LOGO

		try {

			mailsender.sendMail(subject, body, "keyorchestra2014@gmail.com",
					emailaddress);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GMailSenderEmail(Context context, String emailaddress[],
			String subject, String body) {
		// TODO Auto-generated method stub
		GMailSender mailsender = new GMailSender("keyorchestra2014@gmail.com",
				"diavgek@");

		mailsender.set_to(emailaddress);
		mailsender.set_from("keyorchestra2014@gmail.com");
		mailsender.set_subject(subject);
		mailsender.setBody(body);

		try {
			// mailsender.addAttachment("/sdcard/filelocation");

			if (mailsender.send()) {
				Toast.makeText(context, "Email was sent successfully.",
						Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, "Email was not sent.",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(context, "Could not send email:" + e.getMessage(),
					Toast.LENGTH_LONG).show();
		}
	}

	public SharedPreferences getGetPrefs() {
		return getPrefs;
	}

	public Long getId_utente() {
		return getPrefs.getLong("id_utente", -1);
	}

	public String getCognome() {
		return getPrefs.getString("cognome", null);
	}

	public String getNome() {
		return getPrefs.getString("nome", null);
	}

	public String getEmail() {
		return getPrefs.getString("email", null);
	}

	public Long getUser_is_admin() {
		return getPrefs.getLong("user_is_admin", -1);
	}

	public Long getHas_to_change_password() {
		return getPrefs.getLong("has_to_change_password", -1);
	}

	public Long getIs_locked() {
		return getPrefs.getLong("is_locked", -1);
	}

	@SuppressLint("NewApi")
	public void DeleteUserFromPreferences(long id_utente) {
		// TODO Auto-generated method stub
		if (getId_utente() == id_utente) {
			SharedPreferences.Editor editor = getPrefs.edit();
			editor.putLong("id_utente", -1);
			editor.putString("cognome", null);
			editor.putString("nome", null);
			editor.putString("email", null);
			editor.putLong("user_is_admin", -1);
			editor.putLong("has_to_change_password", -1);
			editor.putLong("is_locked", -1);
			editor.apply();
		}

	}

	/**
	 * Update del campo 'is_locked' della tabella 'utenti_scuola' Sblocco o
	 * viceversa, blocco dell'account di un utente. Può essere effettuato solo
	 * da un ADMIN
	 * 
	 * @param applicationContext
	 * @param id_utente
	 * @param is_locked
	 */
	public void setUserLocked(Context applicationContext, int id_utente,
			boolean is_locked) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			int lock = is_locked ? 1 : 0;
			sql = "UPDATE `utenti_scuola` SET `is_locked`=" + lock
					+ " WHERE `id_utente` = " + id_utente;
			int result = st.executeUpdate(sql);
			if (result == 1) {
				if (is_locked)
					Toast.makeText(
							applicationContext,
							"Utente: "
									+ getUserName(applicationContext, id_utente)
									+ " BLOCCATO!", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(
							applicationContext,
							"Utente: "
									+ getUserName(applicationContext, id_utente)
									+ " SBLOCCATO!", Toast.LENGTH_SHORT).show();
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Aggiunge ruolo a utente
	 * 
	 * @param applicationContext
	 * @param selectedRole
	 * @param selectedUser
	 */
	public void addUserToRole(Context applicationContext, long selectedRole,
			long selectedUser) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "INSERT INTO `ruoli_granted_to_utenti`(`id_utente`, `id_ruolo`) "
					+ "VALUES (" + selectedUser + "," + selectedRole + ")";

			int result = st.executeUpdate(sql);

			if (result == 1) {
				Toast.makeText(
						applicationContext,
						"Aggiunto Ruolo: ["
								+ getRuoloName(applicationContext, selectedRole)
								+ "] " + "a Utente: ["
								+ getUserName(applicationContext, selectedUser)
								+ "]", Toast.LENGTH_SHORT).show();
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUserName(Context applicationContext, long selectedUser) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);
		String retval = null;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT cognome, nome FROM utenti_scuola "
					+ "WHERE  id_utente=" + selectedUser;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getString("cognome") + " "
						+ result.getString("nome");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	public String getRuoloName(Context applicationContext, long selectedRole) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);
		String retval = null;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT ruolo FROM ruoli_utenti " + "WHERE  id_ruolo="
					+ selectedRole;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getString("ruolo");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * Rimuove utente da ruolo
	 * 
	 * @param applicationContext
	 * @param selectedRole
	 * @param selectedUser
	 */
	public void removeUserFromRole(Context applicationContext,
			long selectedRole, long selectedUser) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "DELETE FROM ruoli_granted_to_utenti " + "WHERE  id_utente="
					+ selectedUser + " AND id_ruolo=" + selectedRole;
			int result = st.executeUpdate(sql);
			if (result == 1) {
				Toast.makeText(
						applicationContext,
						"Rimosso Utente: ["
								+ getUserName(applicationContext, selectedUser)
								+ "] da Ruolo: ["
								+ getRuoloName(applicationContext, selectedRole)
								+ "]", Toast.LENGTH_SHORT).show();
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<String> listUserRoles(Context applicationContext,
			long selectedUser) {
		// TODO Auto-generated method stub
		ArrayList<String> myRoles = new ArrayList<String>();
		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT * FROM ruoli_granted_to_utenti "
					+ "WHERE  id_utente=" + selectedUser;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				myRoles.add(result.getString("ruolo"));
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myRoles;
	}

	public long getIdRuolo(Context applicationContext, String ruolo) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);
		long retval = -1;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT id_ruolo FROM ruoli_utenti " + "WHERE  ruolo='"
					+ ruolo + "'";
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getLong("id_ruolo");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}
}
