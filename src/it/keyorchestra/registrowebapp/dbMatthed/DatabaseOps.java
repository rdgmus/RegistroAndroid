package it.keyorchestra.registrowebapp.dbMatthed;

import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions.NewPasswordRequestState;
import it.keyorchestra.registrowebapp.interfaces.DatabasesInterface;
import it.keyorchestra.registrowebapp.mysqlandroid.MySqlAndroid;
import it.keyorchestra.registrowebapp.scuola.util.GMailSender;
import it.keyorchestra.registrowebapp.scuola.util.SimpleMail2;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
	// CREDENZIALI UTENTE DELL'APPLICAZIONE
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

	/**
	 * Cotruisce la URL per la conessione al database fornando IP, credenziali
	 * dell'amministratore e altro, prelevando i valori dalle preferenze. Questi
	 * valori devono essere forniti dall'ammnistratore dell'applicazione e
	 * dipendono dalla rete in cui è situato il server di database e dalle
	 * credenziali fornite al momento dell'installazione di XAMPP o di
	 * PostgreSQL o altro, in futuro.
	 * 
	 * @param context
	 * @return
	 */
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

	/**
	 * Controlla se la comunicazione con il servere MySQL sia attiva oppure no.
	 * Se è attiva ritorna 1.
	 * 
	 * @param context
	 * @return
	 */
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

	/**
	 * Procedura di autenticazione dell'utente che ha richiesto il LOGIN.
	 * 
	 * @param applicationContext
	 * @param sEmail
	 * @param sPasswd
	 * @param ip
	 * @param phpencoder
	 * @return
	 */
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

	/**
	 * Salva i dati dell'utente che ha effettuato il LOGIN nelle preferenze.
	 * 
	 * @param id_utente
	 * @param cognome
	 * @param nome
	 * @param email
	 * @param user_is_admin
	 * @param has_to_change_password
	 * @param is_locked
	 * @param context
	 */
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

	/**
	 * Effettua il blocco dell'utente che sta effettuando il LOGIN
	 * 
	 * @param applicationContext
	 * @param databaseIp
	 */
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

	/**
	 * Sblocca un utente nel senso che pone a 0 il campo is_locked Ora chiunque
	 * abbia le credenziali potrà accedere.
	 * 
	 * @param applicationContext
	 * @param databaseIp
	 * @param id_utente
	 */
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

	/**
	 * Registrazione nuovo utente iscritto al REGISTRO SCOLASTICO ovvero
	 * all'applicazione Android.
	 * 
	 * @param applicationContext
	 * @param nome
	 * @param cognome
	 * @param email
	 * @param passwd
	 * @param ip
	 * @param phpencoder
	 * @return
	 */
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

	/**
	 * Invio email per richiesta conferma all'utente!
	 * 
	 * @param applicationContext
	 * @param ip
	 * @param phpencoder
	 * @param cognome
	 * @param nome
	 * @param email
	 * @param hash
	 */
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

	/**
	 * Richiede all'interfaccia PHP di generare una HASH
	 * 
	 * @param applicationContext
	 * @param ip
	 * @param phpencoder
	 * @return
	 */
	public String generateHash(Context applicationContext, String ip,
			String phpencoder) {
		// TODO Auto-generated method stub
		// Genera hash tramite php
		String hash = new MySqlAndroid().getEncodedStringFromUri(
				applicationContext, "http://" + ip + "/" + phpencoder
						+ "?actionEncode=generateHash");
		return hash;
	}

	/**
	 * Richiede all'interfccia PHP di generare una password di formato
	 * pressocchè appropriato per cambiamento password da parte dell'utente o
	 * per intervento dell'ADMIN. Quindi una utility per l'utente
	 * dell'applicazione Android.
	 * 
	 * @param applicationContext
	 * @param ip
	 * @param phpencoder
	 * @param length
	 * @return
	 */
	public String generatePassword(Context applicationContext, String ip,
			String phpencoder, String length) {
		// TODO Auto-generated method stub
		// Genera hash tramite php
		String password = new MySqlAndroid().getEncodedStringFromUri(
				applicationContext, "http://" + ip + "/" + phpencoder
						+ "?actionEncode=generatePassword&length=" + length);
		return password;
	}

	/**
	 * Richiama l'interfaccia PHP per la codifica delle informazioni, come la
	 * password da registrare nel sistema in caso di iscrizione o per
	 * l'autenticazione dell'utente al LOGIN, etc. etc.
	 * 
	 * @param applicationContext
	 * @param ip
	 * @param phpencoder
	 * @param passwd
	 * @return
	 */
	private String encodePassword(Context applicationContext, String ip,
			String phpencoder, String passwd) {
		@SuppressWarnings("deprecation")
		String encoded = new MySqlAndroid().getEncodedStringFromUri(
				applicationContext,
				"http://" + ip + "/" + phpencoder
						+ "?actionEncode=encodePassword&password="
						+ URLEncoder.encode(passwd));

		return encoded;

	}

	/**
	 * Costruisce il <body/> della email html da inviare all'utente che ha
	 * chiesto il cambiamento di password fornandogli un link raggiungendo il
	 * quale potrà confermare di essere stato lui ad effettuare la richiesta al
	 * sistema.
	 * 
	 * @param applicationContext
	 * @param ip
	 * @param phpencoder
	 * @param cognome
	 * @param nome
	 * @param email
	 * @param hash
	 * @return
	 */
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

	/**
	 * Un altro tentativo di implementazione invio email
	 * 
	 * @param context
	 * @param emailTo
	 * @param subject
	 * @param body
	 * @param imgPath
	 * @param contentId
	 */
	@Deprecated
	public void SimpleMail2Email(Context context, String emailTo,
			String subject, String body, String imgPath, String contentId) {
		SimpleMail2 simpleMail2 = new SimpleMail2();
		simpleMail2.send(emailTo, subject, body, imgPath, contentId);
	}

	/**
	 * Invia email in formato html con GMAIL server
	 * 
	 * @param context
	 * @param emailaddress
	 * @param subject
	 * @param body
	 */
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

	/**
	 * Invia email con GMAIL server
	 * 
	 * @param context
	 * @param emailaddress
	 * @param subject
	 * @param body
	 */
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

	/**
	 * Ritorna l'id_utente dalle preferenze
	 * 
	 * @return
	 */
	public Long getId_utente() {
		return getPrefs.getLong("id_utente", -1);
	}

	/**
	 * Ritorna il cognome dalle preferenze
	 * 
	 * @return
	 */
	public String getCognome() {
		return getPrefs.getString("cognome", null);
	}

	/**
	 * Ritorna il nome dalle preferenze
	 * 
	 * @return
	 */
	public String getNome() {
		return getPrefs.getString("nome", null);
	}

	/**
	 * Ritorna l'email dalle preferenze
	 * 
	 * @return
	 */
	public String getEmail() {
		return getPrefs.getString("email", null);
	}

	/**
	 * Ritorna l'informazione che mi dice se l'utente che ha effettuato il login
	 * appartiene al gruppo degli ADMIN dalle preferenze
	 * 
	 * @return
	 */
	public Long getUser_is_admin() {
		return getPrefs.getLong("user_is_admin", -1);
	}

	/**
	 * Mi dice se l'utente deve cambiare password. Serve per notificare
	 * all'utente che deve cambiare password. Questo avviene quando l'utente ha
	 * già richiesto un cambiamento di password. Ha poi confermato la richiesta
	 * rispondendo all'email inviatagli dal sistema. L'ADMIN ha effettuato il
	 * cambiamento di password e lo ha comunicato all'utente sempre tramite
	 * email. L'utente ha effettuato il LOGIN con la nuova password fornitagli
	 * dall'ADMIN, ma ora viene richiesto il suo intervento affinchè cambi
	 * ancora la password, onde evitare che la password nuova sia conosciuta
	 * quanto meno dall'ADMIN che la elaborata. In sostanza è una sicurezza in
	 * più per l'utente che così vede salvaguardata la propria privacy e anche
	 * una politica di sicurezza che il sistema vuole mantenere solida. Il
	 * valore è sempre preso dalle preferenze.
	 * 
	 * @return
	 */
	public Long getHas_to_change_password() {
		return getPrefs.getLong("has_to_change_password", -1);
	}

	/**
	 * L'informazione relativa al fatto che l'utente sia o no bloccato in caso
	 * di successivi tentativi di LOGIN con le stesse credenziali. Questa
	 * informazione dovrebbe essere naturalmente = 1, avendo l'utente effettuato
	 * il login con successo e quindi il sistema ha provveduto a registrare
	 * l'accesso ponendo a 1 questo bit.
	 * 
	 * @return
	 */
	public Long getIs_locked() {
		return getPrefs.getLong("is_locked", -1);
	}

	/**
	 * In caso di LOGOUT vengono cancellati i dati relativi all'utente dalle
	 * preferenze
	 * 
	 * @param id_utente
	 */
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
									+ getUserSurname(applicationContext,
											id_utente)
									+ " "
									+ getUserName(applicationContext, id_utente)
									+ "\n Inserito blocco su Login!",
							Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(
							applicationContext,
							"Utente: "
									+ getUserSurname(applicationContext,
											id_utente)
									+ " "
									+ getUserName(applicationContext, id_utente)
									+ "\nRimosso blocco su Login!",
							Toast.LENGTH_SHORT).show();
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

			@SuppressWarnings("unused")
			int result = st.executeUpdate(sql);

			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ritorna l'email dell'utente indicato
	 * 
	 * @param applicationContext
	 * @param selectedUser
	 * @return
	 */
	public String getUserEmail(Context applicationContext, long selectedUser) {
		String url = getUrl(applicationContext);
		String retval = null;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT email FROM utenti_scuola " + "WHERE  id_utente="
					+ selectedUser;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getString("email");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * Ritorna il solo cognome dell'utente
	 * 
	 * @param applicationContext
	 * @param selectedUser
	 * @return
	 */
	@Deprecated
	public String getUserSurname(Context applicationContext, long selectedUser) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);
		String retval = null;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT cognome FROM utenti_scuola " + "WHERE  id_utente="
					+ selectedUser;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getString("cognome");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * Ritorna il solo nome dell'utente
	 * 
	 * @param applicationContext
	 * @param selectedUser
	 * @return
	 */
	@Deprecated
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

			sql = "SELECT nome FROM utenti_scuola " + "WHERE  id_utente="
					+ selectedUser;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getString("nome");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * Ritorna il ruolo corrispondente all'ID fornito
	 * 
	 * @param applicationContext
	 * @param selectedRole
	 * @return
	 */
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
			@SuppressWarnings("unused")
			int result = st.executeUpdate(sql);
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Lista dei ruoli ricoperti dall'utente
	 * 
	 * @param applicationContext
	 * @param selectedUser
	 * @return
	 */
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

	/**
	 * ID del ruolo indicato nella tabella ruoli_utenti
	 * 
	 * @param applicationContext
	 * @param ruolo
	 * @return
	 */
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

	/**
	 * Si occupa di inoltrare una richiesta di cambiamento password
	 * all'interfaccia PHP.
	 * 
	 * @param applicationContext
	 * @param email
	 * @param sendRequestChangePassword
	 * @param ip
	 * @return un valore enum del tipo NewPasswordRequestState
	 */
	public NewPasswordRequestState SendRequestChangePassword(
			Context applicationContext, String email,
			String sendRequestChangePassword, String ip) {
		// TODO Auto-generated method stub
		long id_utente = getIdOfUserWithEmail(applicationContext, email);
		String cognome = getUserSurname(applicationContext, id_utente);
		String nome = getUserName(applicationContext, id_utente);

		NewPasswordRequestState result = NewPasswordRequestState.NONE;
		try {
			result = new MySqlAndroid().SendRequestChangePassword(
					applicationContext, "http://" + ip + "/"
							+ sendRequestChangePassword + "?cognome="
							+ URLEncoder.encode(cognome, "UTF-8") + "&nome="
							+ URLEncoder.encode(nome, "UTF-8") + "&email="
							+ URLEncoder.encode(email, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Carica l'id_utente corrispondente alla email fornita nel tentativo di
	 * LOGIN andato a male perchè non si conosce la password oppure è stata
	 * dimenticata.
	 * 
	 * @param applicationContext
	 * @param email
	 * @return
	 */
	public long getIdOfUserWithEmail(Context applicationContext, String email) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);
		long retval = -1;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT id_utente FROM utenti_scuola " + "WHERE  email='"
					+ email + "'";
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getLong("id_utente");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * Ritorna la password dell'utente come codificata e quindi a meno di
	 * conoscere l'algoritmo di decodifica non la può leggere neanche l'admin
	 * che svolge l'operazione di cambiamento password per gli utenti
	 * richiedenti.
	 * 
	 * @param applicationContext
	 * @param selectedUser
	 * @return
	 */
	public String getUserPasswd(Context applicationContext, long selectedUser) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);
		String retval = null;

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "SELECT password FROM utenti_scuola " + "WHERE  id_utente="
					+ selectedUser;
			ResultSet result = st.executeQuery(sql);
			while (result.next()) {
				retval = result.getString("password");
			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retval;
	}

	/**
	 * Procedura cambiamento password a sistema
	 * 
	 * @param id_utente
	 * @param email
	 * @param oldPassword
	 * @param passwordToEncode
	 * @return
	 */
	public boolean SaveNewPassword(Context applicationContext, long id_utente,
			String email, String oldPassword, String passwordToEncode,
			String ip, String phpencoder) {
		// TODO Auto-generated method stub

		String url = getUrl(applicationContext);

		String encodedPassword = encodePassword(applicationContext, ip,
				phpencoder, passwordToEncode);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "UPDATE utenti_scuola SET password = '" + encodedPassword
					+ "', has_to_change_password = 1" + " WHERE id_utente = "
					+ id_utente + " AND email = '" + email
					+ "' AND password = '" + oldPassword + "'";

			@SuppressWarnings("unused")
			int result = st.executeUpdate(sql);
			if (result == 1) {
				// SETTA CAMPI DELLA change_password_request
				long id_utenteAdmin = getPrefs.getLong("id_utente", -1);
				sql = "UPDATE `change_password_request` SET `request_done_date`=NOW(),`request_done`= 1,`email_sent`= 0,`id_admin`= "
						+ id_utenteAdmin
						+ ", pending = 0"
						+ " WHERE from_user = "
						+ id_utente
						+ " AND pending = 1 AND confirmed = 1";
				result = st.executeUpdate(sql);

			}
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Procedura cambiamento password: invio della email con la passord
	 * 
	 * @param id_utente2
	 * @param email2
	 * @param passwordToEncode
	 * @param msg
	 * @return
	 */
	public boolean EmailPasswordToUser(Context applicationContext,
			long id_utente, String email, String passwordToEncode,
			String sendNewPasswordToUser, String ip) {
		// TODO Auto-generated method stub

		try {
			return new MySqlAndroid().EmailPasswordToUser(applicationContext,
					"http://" + ip + "/" + sendNewPasswordToUser + "?email="
							+ URLEncoder.encode(email, "UTF-8") + "&password="
							+ URLEncoder.encode(passwordToEncode, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public boolean setEmailSentWithSuccessAt(Context applicationContext,
			long id_utente, String hash) {
		// TODO Auto-generated method stub
		String url = getUrl(applicationContext);

		Connection conn;
		try {
			DriverManager.setLoginTimeout(15);
			conn = DriverManager.getConnection(url);
			Statement st = conn.createStatement();
			String sql = null;

			sql = "UPDATE `change_password_request` SET `email_sent`= 1 "
					+ " WHERE from_user = " + id_utente + " AND hash = '"
					+ hash + "'";

			@SuppressWarnings("unused")
			int result = st.executeUpdate(sql);
			st.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
