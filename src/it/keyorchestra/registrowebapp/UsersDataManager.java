package it.keyorchestra.registrowebapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.mysqlandroid.MySqlAndroid;
import it.keyorchestra.registrowebapp.mysqlandroid.Tab1Activity;
import it.keyorchestra.registrowebapp.mysqlandroid.Tab2Activity;
import it.keyorchestra.registrowebapp.mysqlandroid.Tab3Activity;
import it.keyorchestra.registrowebapp.mysqlandroid.UtentiScuolaActivity;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class UsersDataManager extends TabActivity implements
		ActivitiesCommonFunctions {

	ImageButton ibBack;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_data_manager);

		ibBack = (ImageButton) findViewById(R.id.ibBack);
		registerToolTipFor(ibBack);
		ibBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(), "Running Back...",
						Toast.LENGTH_LONG).show();
				Intent ourStartingPoint = new Intent(UsersDataManager.this,
						UserMenu.class);
				startActivity(ourStartingPoint);

				// FINISH
				finish();
			}
		});
//		// TAB
//		// create the TabHost that will contain the Tabs
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec tab1 = tabHost.newTabSpec("First Tab");
		TabSpec tab2 = tabHost.newTabSpec("Second Tab");
		TabSpec tab3 = tabHost.newTabSpec("Third tab");
		// Set the Tab name and Activity
		// that will be opened when particular Tab will be selected
		tab1.setIndicator("Tab1");
		tab1.setContent(new Intent(this,
				Tab1Activity.class));

		tab2.setIndicator("Tab2");
		tab2.setContent(new Intent(this, Tab2Activity.class));

		tab3.setIndicator("Tab3");
		tab3.setContent(new Intent(this, Tab3Activity.class));

		/** Add the tabs to the TabHost to display. */
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
		tabHost.addTab(tab3);
		
//		RetrieveUsersData("utenti_scuola");
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
	protected void RetrieveUsersData(String table_name) {
		// TODO Auto-generated method stub

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		String ip = null;
		if (defaultDatabase.contentEquals("PostgreSQL")) {
			Toast.makeText(
					getApplicationContext(),
					"PostgreSQL è il database di default! Correggi menù preferenze",
					Toast.LENGTH_LONG).show();
			return;
		} else if (defaultDatabase.contentEquals("MySQL")) {
			ip = getPrefs.getString("ipMySQL", "");
		}

		JSONArray jArray = new MySqlAndroid().retrieveTableData(
				getApplicationContext(), "http://" + ip + "/"
						+ retrieveTableData + "?table_name=" + table_name,
				table_name);
		if (jArray == null) {
			Toast.makeText(
					getApplicationContext(),
					"Nessuna risposta dal server MySQL! Controlla lo stato di Apache & MySQL server!",
					Toast.LENGTH_LONG).show();
		} else {
			try {
				// JSONArray jArray = new JSONArray(result);
				TableLayout tv = (TableLayout) findViewById(R.id.table);
				tv.setBackgroundColor(getResources().getColor(
						R.color.colorListItem));
				tv.removeAllViewsInLayout();
				int flag = 1;
				for (int i = -1; i < jArray.length() - 1; i++) {
					TableRow tr = new TableRow(UsersDataManager.this);
					tr.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					if (flag == 1) {
						TextView b6 = new TextView(UsersDataManager.this);
						b6.setText("Id");
						b6.setTextColor(Color.BLUE);
						b6.setTextSize(15);
						tr.addView(b6);
						TextView b19 = new TextView(UsersDataManager.this);
						b19.setPadding(10, 0, 0, 0);
						b19.setTextSize(15);
						b19.setText("Cognome & Nome");
						b19.setTextColor(Color.BLUE);
						tr.addView(b19);
						TextView b29 = new TextView(UsersDataManager.this);
						b29.setPadding(10, 0, 0, 0);
						b29.setText("Stato");
						b29.setTextColor(Color.BLUE);
						b29.setTextSize(15);
						tr.addView(b29);
						tv.addView(tr);

						final View vline = new View(UsersDataManager.this);

						vline.setLayoutParams(new TableRow.LayoutParams(
								TableRow.LayoutParams.MATCH_PARENT, 2));

						vline.setBackgroundColor(Color.BLUE);

						tv.addView(vline);

						flag = 0;

					} else {

						JSONObject json_data = jArray.getJSONObject(i);

						Log.i("log_tag",
								"id: " + json_data.getInt("id_utente")
										+ ", Username: "
										+ json_data.getString("cognome") + " "
										+ json_data.getString("nome")
										+ ", Stato: "
										+ json_data.getInt("is_locked"));

						TextView b = new TextView(UsersDataManager.this);

						String stime = String.valueOf(json_data
								.getInt("id_utente"));

						b.setText(stime);

						b.setTextColor(Color.RED);

						b.setTextSize(15);

						tr.addView(b);

						TextView b1 = new TextView(UsersDataManager.this);

						b1.setPadding(10, 0, 0, 0);

						b1.setTextSize(15);

						String stime1 = json_data.getString("cognome") + " "
								+ json_data.getString("nome");

						b1.setText(stime1);

						b1.setTextColor(Color.BLACK);

						tr.addView(b1);

						TextView b2 = new TextView(UsersDataManager.this);

						b2.setPadding(10, 0, 0, 0);

						String stime2 = String.valueOf(json_data
								.getInt("is_locked"));

						b2.setText(stime2);

						b2.setTextColor(Color.BLACK);

						b2.setTextSize(15);

						tr.addView(b2);

						tv.addView(tr);

						final View vline1 = new View(UsersDataManager.this);

						vline1.setLayoutParams(new TableRow.LayoutParams(
								TableRow.LayoutParams.MATCH_PARENT, 1));

						vline1.setBackgroundColor(Color.WHITE);

						tv.addView(vline1);

					}

				}

			} catch (JSONException e) {

				Log.e("log_tag", "Error parsing data" + e.toString());

				Toast.makeText(getApplicationContext(), "JsonArray fail",
						Toast.LENGTH_SHORT).show();

			}

		}
	}

}
