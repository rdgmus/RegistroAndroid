package it.keyorchestra.registrowebapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class Splash extends Activity {

	MediaPlayer splash_song;
	ImageButton startButton;
	Boolean started = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		splash_song = MediaPlayer.create(Splash.this, R.raw.splash_sound_3);

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
		Boolean music = getPrefs.getBoolean("music", true);
		if (music)
			splash_song.start();
		
		startButton = (ImageButton) findViewById(R.id.imageButtonStart);
		startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				splash_song.release();
				started = true;
				Intent ourStartingPoint = new Intent(
						"it.keyorchestra.registrowebapp.LIST_ACTIVITY");
				startActivity(ourStartingPoint);
			}
		});

		Thread timer = new Thread() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					sleep(12000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					if (!started) {
						splash_song.release();
						Intent ourStartingPoint = new Intent(
								"it.keyorchestra.registrowebapp.LIST_ACTIVITY");
						startActivity(ourStartingPoint);
					}
				}
			}

		};
		timer.start();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
