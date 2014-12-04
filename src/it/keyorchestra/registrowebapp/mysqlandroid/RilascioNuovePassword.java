package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.UserMenu;
import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.interfaces.GeneratePasswordInterface;
import it.keyorchestra.registrowebapp.scuola.util.FieldsValidator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class RilascioNuovePassword extends Activity implements
		ActivitiesCommonFunctions, GeneratePasswordInterface {
	private SharedPreferences getPrefs;
	private boolean backButtonVisible;

	LinearLayout llGeneratorePassword, llRichieste, llCommands, headerLayout;

	SeekBar sbPasswdLen;
	TextView tvPasswdLen, etEmail, etPasswd;
	EditText etRepeatPasswd, etNewPasswd;
	CheckBox cbVediPassword;
	ImageButton ibGeneratePasswd, ibSavePasswd, ibBack;

	private long selectedUser;

	/**
	 * @return the backButtonVisible
	 */
	public boolean isBackButtonVisible() {
		return backButtonVisible;
	}

	/**
	 * @param backButtonVisible
	 *            the backButtonVisible to set
	 */
	public void setBackButtonVisible(boolean backButtonVisible) {
		this.backButtonVisible = backButtonVisible;
		if (backButtonVisible)
			ibBack.setVisibility(ImageButton.VISIBLE);
		else
			ibBack.setVisibility(ImageButton.GONE);
	}

	/**
	 * @return the selectedUser
	 */
	public long getSelectedUser() {
		return selectedUser;
	}

	/**
	 * @param selectedUser
	 *            the selectedUser to set
	 */
	public void setSelectedUser(long selectedUser) {
		this.selectedUser = selectedUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rilascio_nuove_password);
		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		headerLayout = (LinearLayout) findViewById(R.id.headerLayout);
		ibBack = (ImageButton) headerLayout.findViewById(R.id.ibBack);

		setBackButtonVisible(getPrefs.getBoolean("backButtonForPasswordChange",
				false));
		ibBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Se il bottone è visibile una volta premuto riporta
				//alla UserMenu.class
				startAnimation((ImageButton) v, 2000);
				Intent ourStartingPoint = new Intent(RilascioNuovePassword.this,
						UserMenu.class);
				startActivity(ourStartingPoint);

				// FINISH
				RilascioNuovePassword.this.finish();
			}
		});


		llRichieste = (LinearLayout)findViewById(R.id.llRichieste);
		// EMAIL E PASSWORD UTENTE CHE HA EFETTUATO RICHIESTA CAMBIO PASSWORD
		etEmail = (TextView) llRichieste.findViewById(R.id.etEmail);
		etPasswd = (TextView) llRichieste.findViewById(R.id.etPasswd);

		llGeneratorePassword = (LinearLayout) findViewById(R.id.llGeneratorePassword);
		etNewPasswd = (EditText) llGeneratorePassword
				.findViewById(R.id.etNewPasswd);
		etRepeatPasswd = (EditText) llGeneratorePassword
				.findViewById(R.id.etRepeatPasswd);
		llCommands = (LinearLayout) llGeneratorePassword
				.findViewById(R.id.llCommands);

		tvPasswdLen = (TextView) llCommands.findViewById(R.id.tvPasswdLen);

		cbVediPassword = (CheckBox) llCommands
				.findViewById(R.id.cbVediPassword);
		cbVediPassword
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// TODO Auto-generated method stub
						SetPasswordVisibility(isChecked);
					}
				});

		ibGeneratePasswd = (ImageButton) llCommands
				.findViewById(R.id.ibGeneratePasswd);
		registerToolTipFor(ibGeneratePasswd);
		ibGeneratePasswd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				String generatedPassword = GeneratePasswordFromPhp(
						getBaseContext(), getDatabaseIpFromPreferences(),
						getPrefs.getString("phpencoder", null), tvPasswdLen
								.getText().toString());

				etNewPasswd.setText(generatedPassword);
				etRepeatPasswd.setText(generatedPassword);
			}
		});
		ibSavePasswd = (ImageButton) llCommands.findViewById(R.id.ibSavePasswd);
		registerToolTipFor(ibSavePasswd);
		ibSavePasswd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				if (!FieldsValidator.Is_Valid_Password(etNewPasswd)) {
					etNewPasswd.requestFocus();
					return;
				}

				if (!FieldsValidator.Is_Valid_RetypedPassword(etRepeatPasswd,
						etNewPasswd.getText())) {
					etRepeatPasswd.requestFocus();
					return;
				}
				// SaveNewPassword(id_utente, passwordToEncode);
				// EmailToUser(id_utente, newPassword, msg);
				Toast.makeText(
						getApplicationContext(),
						"Password impostata per l'utente!\n" + "Inviata Email!",
						Toast.LENGTH_SHORT).show();
			}
		});

		sbPasswdLen = (SeekBar) llGeneratorePassword
				.findViewById(R.id.sbPasswdLen);

		sbPasswdLen.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			int progressChanged = 0;

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
				tvPasswdLen.setText("" + (progressChanged + 4) + "");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				progressChanged = progress;
			}
		});
		tvPasswdLen.setText("" + (sbPasswdLen.getProgress() + 4) + "");

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
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		return defaultDatabase;
	}

	@Override
	public String getDatabaseIpFromPreferences() {
		// TODO Auto-generated method stub
		String defaultDatabase = getDefaultDatabaseFromPreferences();

		String ip = null;

		if (defaultDatabase.contentEquals("MySQL")) {
			ip = getPrefs.getString("ipMySQL", "");
		} else if (defaultDatabase.contentEquals("PostgreSQL")) {
			ip = getPrefs.getString("ipPostgreSQL", "");
		}
		return ip;
	}

	@Override
	public void startAnimation(final View ib, final long durationInMilliseconds) {
		// TODO Auto-generated method stub
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

	@Override
	public String GeneratePasswordFromPhp(Context context, String ip,
			String servSideScript, String length) {
		// TODO Auto-generated method stub
		DatabaseOps databaseOps = new DatabaseOps(getApplicationContext());
		// Controlla se le credenziali esistono

		return databaseOps
				.generatePassword(context, ip, servSideScript, length);
	}

	@Override
	public boolean SaveNewPassword(long id_utente, String passwordToEncode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void EmailToUser(long id_utente, String newPassword, String msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SetPasswordVisibility(boolean isVisible) {
		// TODO Auto-generated method stub
		if (isVisible) {
			etNewPasswd
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
			etRepeatPasswd
					.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		} else {
			etNewPasswd.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
			etRepeatPasswd.setInputType(InputType.TYPE_CLASS_TEXT
					| InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
	}

}
