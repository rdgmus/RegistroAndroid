package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UserMenu extends Activity implements ActivitiesCommonFunctions {

	private SharedPreferences getPrefs;
	private DatabaseOps databaseOps;
	TextView tvUserData, tvNow;
	ImageButton ibLogout, ibRuoloUtente, ibIsAdmin, ibEmail, ibChangePassword,
			ibDatiUtenti, imShowMenu;

	private String ruoloScelto;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_menu);

		databaseOps = new DatabaseOps(getApplicationContext());

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		final long id_utente = databaseOps.getId_utente();
		String cognome = databaseOps.getCognome();
		String nome = databaseOps.getNome();

		if (id_utente == -1) {
			UserMenu.this.finish();
		}

		Toast.makeText(
				getApplicationContext(),
				"Menù dell'Utente: [" + id_utente + "] " + cognome + " " + nome,
				Toast.LENGTH_SHORT).show();
		// RUOLO SCELTO
		ibRuoloUtente = (ImageButton) findViewById(R.id.ibRuoloUtente);
		ruoloScelto = getPrefs.getString("ruoloList", "1");
		setIconRuoloScelto(ibRuoloUtente, ruoloScelto);
		registerToolTipFor(ibRuoloUtente);
		ibRuoloUtente.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(
						getApplicationContext(),
						"Non vi sono attività implementate per il ruolo di: "
								+ ruoloScelto, Toast.LENGTH_SHORT).show();
			}
		});

		// IS ADMIN
		ibIsAdmin = (ImageButton) findViewById(R.id.ibIsAdmin);
		Long user_is_admin = getPrefs.getLong("user_is_admin", -1);
		setIconIsAdmin(ibIsAdmin, user_is_admin);
		registerToolTipFor(ibIsAdmin);

		// DATI UTENTE SULLO SCHERMO
		tvUserData = (TextView) findViewById(R.id.tvUserData);
		String userName = (user_is_admin == 1) ? "Admin: [" : " User: [";
		userName += id_utente + "] " + cognome + " " + nome;
		tvUserData.setText(userName);

		ibLogout = (ImageButton) findViewById(R.id.ibLogout);
		registerToolTipFor(ibLogout);
		ibLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);

				Thread thread = new Thread() {
					@Override
					public void run() {
						try {
							Thread.sleep(3500); // As I am using LENGTH_LONG in
												// Toast
							// SBLOCCA UTENTE
							databaseOps.UnlockUser(getApplicationContext(),
									null, id_utente);
							// CANCELLA UTENTE DALLE PREFERENZE
							databaseOps.DeleteUserFromPreferences(id_utente);
							// REGISTRA LOGOUT EVENT

							// LANCIA TableListExpActivity
							Intent ourStartingPoint = new Intent(UserMenu.this,
									TableListExpActivity.class);
							startActivity(ourStartingPoint);

							// FINISH
							UserMenu.this.finish();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};

				Toast.makeText(getApplicationContext(),
						"Logout dal Registro Scolastico", Toast.LENGTH_SHORT)
						.show();
				thread.start();

			}

		});

		ibEmail = (ImageButton) findViewById(R.id.ibEmail);
		registerToolTipFor(ibEmail);
		ibEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Non vi sono attività implementate", Toast.LENGTH_SHORT)
						.show();
			}
		});

		ibChangePassword = (ImageButton) findViewById(R.id.ibChangePassword);
		registerToolTipFor(ibChangePassword);
		ibChangePassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Non vi sono attività implementate", Toast.LENGTH_SHORT)
						.show();
			}
		});

		ibDatiUtenti = (ImageButton) findViewById(R.id.ibDatiUtenti);
		registerToolTipFor(ibDatiUtenti);
		ibDatiUtenti.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(), "Manager Utenti",
						Toast.LENGTH_SHORT).show();
				Intent ourStartingPoint = new Intent(UserMenu.this,
						UsersDataManager.class);
				startActivity(ourStartingPoint);

			}
		});

		tvNow = (TextView) findViewById(R.id.tvNow);
		setCurrentDate();

		imShowMenu = (ImageButton) findViewById(R.id.imShowMenu);
		imShowMenu.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(),
				// "imShowMenu.OnClickListener()", Toast.LENGTH_SHORT)
				// .show();
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Richiesta menù in corso...", Toast.LENGTH_SHORT)
						.show();

				openOptionsMenu();
			}
		});
	}

	@Override
	public void startAnimation(final ImageButton ib,
			final long durationInMilliseconds) {
		// TODO Auto-generated method stub
		// BUTTONS ANIMATION
		final String TAG = "ImageButton Animation";
		Animation animation = new AlphaAnimation(1.0f, 0.25f); // Change alpha
																// from
		// fully visible to
		// invisible
		animation.setDuration(500); // duration - half a second
		animation.setInterpolator(new LinearInterpolator()); // do not alter
																// animation
																// rate
		animation.setRepeatCount(Animation.INFINITE); // Repeat animation
														// infinitely
		animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the
													// end so the button will
													// fade back in

		ib.startAnimation(animation);

		Thread t = new Thread() {
			long timeElapsed = 0l;

			public void run() {
				try {
					while (timeElapsed <= durationInMilliseconds) {
						long start = System.currentTimeMillis();
						sleep(1000);
						timeElapsed += System.currentTimeMillis() - start;
					}
				} catch (InterruptedException e) {
					Log.e(TAG, e.toString());
				} finally {
					ib.clearAnimation();
				}
			}
		};
		t.start();
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

	// display current date both on the text view and the Date Picker when the
	// application starts.

	private void setCurrentDate() {
		final Calendar calendar = Calendar.getInstance();

		int year = calendar.get(Calendar.YEAR);

		int month = calendar.get(Calendar.MONTH);

		int day = calendar.get(Calendar.DAY_OF_MONTH);

		// set current date into textview

		tvNow.setText(new StringBuilder()

		// Month is 0 based, so you have to add 1

				.append(day).append("-")

				.append(month + 1).append("-")

				.append(year).append(" "));

		// set current date into Date Picker

		// date_picker.init(year, month, day, null);

	}

}
