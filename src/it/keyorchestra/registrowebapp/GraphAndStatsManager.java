package it.keyorchestra.registrowebapp;

import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.mysqlandroid.AssegnamentoRuoliActivity;
import it.keyorchestra.registrowebapp.mysqlandroid.GraficiActivity;
import it.keyorchestra.registrowebapp.mysqlandroid.RilascioNuovePassword;
import it.keyorchestra.registrowebapp.mysqlandroid.Tab1Activity;
import it.keyorchestra.registrowebapp.mysqlandroid.Tab2Activity;
import it.keyorchestra.registrowebapp.mysqlandroid.UtentiBloccatiActivity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class GraphAndStatsManager extends TabActivity implements
		ActivitiesCommonFunctions {

	ImageButton ibBack;
	TextView tvRilascioTitle;
	private SharedPreferences getPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users_data_manager);

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		ibBack = (ImageButton) findViewById(R.id.ibBack);
		registerToolTipFor(ibBack);
		ibBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startAnimation((ImageButton) v, 2000);
				Toast.makeText(getApplicationContext(),
						"Grafici e Statistiche terminata!", Toast.LENGTH_LONG).show();
				Intent ourStartingPoint = new Intent(GraphAndStatsManager.this,
						UserMenu.class);
				startActivity(ourStartingPoint);

				// FINISH
				finish();
			}
		});

		tvRilascioTitle = (TextView) findViewById(R.id.tvRilascioTitle);
		tvRilascioTitle.setText("Grafici e Statistiche");

		// TAB
		// create the TabHost that will contain the Tabs
		TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
		TabSpec tab1 = tabHost.newTabSpec("First Tab");
		TabSpec tab2 = tabHost.newTabSpec("Second Tab");
		// Set the Tab name and Activity
		// that will be opened when particular Tab will be selected
		tab1.setIndicator("Grafici",
				getResources().getDrawable(R.drawable.graph_tab));
		tab1.setContent(new Intent(this, GraficiActivity.class));

		tab2.setIndicator("Statistiche",
				getResources().getDrawable(R.drawable.stats_tab));
		tab2.setContent(new Intent(this, Tab2Activity.class));

		/** Add the tabs to the TabHost to display. */
		tabHost.addTab(tab1);
		tabHost.addTab(tab2);
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
	public void startAnimation(final View ib, final long durationInMilliseconds) {
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
}
