package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserMenu extends Activity implements ActivitiesCommonFunctions {

	private SharedPreferences getPrefs;
	private DatabaseOps databaseOps;
	TextView tvUserData;
	ImageButton ibLogout, ivRuoloUtente, ibIsAdmin, ibEmail, ibChangePassword,
			ibUsersPassword, ibDatiUtenti;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_menu);

		databaseOps = new DatabaseOps(getApplicationContext());

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		final long id_utente = databaseOps.getId_utente();
		String cognome = databaseOps.getCognome();
		String nome = databaseOps.getNome();

		Toast.makeText(
				getApplicationContext(),
				"Menù dell'Utente: [" + id_utente + "] " + cognome + " " + nome,
				Toast.LENGTH_SHORT).show();
		// RUOLO SCELTO
		ivRuoloUtente = (ImageButton) findViewById(R.id.ivRuoloUtente);
		String ruoloScelto = getPrefs.getString("ruoloList", "1");
		setIconRuoloScelto(ivRuoloUtente, ruoloScelto);
		registerToolTipFor(ivRuoloUtente);

		// IS ADMIN
		ibIsAdmin = (ImageButton) findViewById(R.id.ibIsAdmin);
		Long user_is_admin = getPrefs.getLong("user_is_admin", -1);
		setIconIsAdmin(ibIsAdmin, user_is_admin);
		registerToolTipFor(ibIsAdmin);

		// DATI UTENTE SULLO SCHERMO
		tvUserData = (TextView) findViewById(R.id.tvUserData);
		tvUserData.setText("User: [" + id_utente + "] " + cognome + " " + nome);

		ibLogout = (ImageButton) findViewById(R.id.ibLogout);
		registerToolTipFor(ibLogout);
		ibLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),
						"Logout dal Registro Scolastico in corso...",
						Toast.LENGTH_SHORT).show();

				// SBLOCCA UTENTE
				databaseOps
						.UnlockUser(getApplicationContext(), null, id_utente);
				// CANCELLA UTENTE DALLE PREFERENZE
				databaseOps.DeleteUserFromPreferences(id_utente);
				// REGISTRA LOGOUT EVENT

				// LANCIA TableListExpActivity
				Intent ourStartingPoint = new Intent(UserMenu.this,
						TableListExpActivity.class);
				startActivity(ourStartingPoint);

				// FINISH
				finish();
			}

		});
		
		ibEmail = (ImageButton) findViewById(R.id.ibEmail);
		registerToolTipFor(ibEmail); 
		
		ibChangePassword = (ImageButton) findViewById(R.id.ibChangePassword);
		registerToolTipFor(ibChangePassword);
		
		ibUsersPassword = (ImageButton) findViewById(R.id.ibUsersPassword);
		registerToolTipFor(ibUsersPassword);
		
		ibDatiUtenti = (ImageButton) findViewById(R.id.ibDatiUtenti);
		registerToolTipFor(ibDatiUtenti);
	}

	private void setIconIsAdmin(ImageButton ibIsAdmin, Long user_is_admin) {
		// TODO Auto-generated method stub
		Resources res = getResources();
		if (user_is_admin == 1) {
			ibIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.administrator64));
		} else
			ibIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.ruolo_utente64));

	}

	private void setIconRuoloScelto(ImageButton bCambiaRuolo, String ruoloScelto) {
		// TODO Auto-generated method stub
		// SET ICON admin, professor, secretary, ata
		Resources res = getResources();
		if (ruoloScelto.equals("Amministratore")) {
			bCambiaRuolo.setImageDrawable(res.getDrawable(R.drawable.admin));
		} else if (ruoloScelto.equals("Professore")) {
			bCambiaRuolo
					.setImageDrawable(res.getDrawable(R.drawable.professor));
		} else if (ruoloScelto.equals("Segreteria")) {
			bCambiaRuolo
					.setImageDrawable(res.getDrawable(R.drawable.secretary));
		} else if (ruoloScelto.equals("Ata")) {
			bCambiaRuolo.setImageDrawable(res.getDrawable(R.drawable.ata));
		}
	}

	@Override
	public void registerToolTipFor(ImageButton ib) {
		// TODO Auto-generated method stub
		ib.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				customToast(view.getContentDescription(), R.drawable.help32,
						R.layout.info_layout);

				return true;
			}
		});
	}

	@Override
	public boolean customToast(CharSequence charSequence, int iconId,
			int layoutId) {
		// TODO Auto-generated method stub
		Resources res = getResources();
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(layoutId,
				(ViewGroup) findViewById(R.id.toast_layout_root));
		TextView tvToastConnect = (TextView) layout
				.findViewById(R.id.tvToastConnect);
		tvToastConnect.setText(charSequence);

		ImageView ivToastConnect = (ImageView) layout
				.findViewById(R.id.ivToastConnect);
		ivToastConnect.setImageDrawable(res.getDrawable(iconId));

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.BOTTOM, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
		return true;
	}

	@Override
	public String getDefaultDatabaseFromPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseIpFromPreferences() {
		// TODO Auto-generated method stub
		return null;
	}

}
