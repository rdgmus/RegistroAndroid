package it.keyorchestra.registrowebapp;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	
	Button registeredUser,newUser;
	//MediaPlayer splash_song;
	//MediaController soundController;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//splash_song= MediaPlayer.create(MainActivity.this, R.raw.splash_sound_3);
		//splash_song.start();
		registeredUser=(Button) findViewById(R.id.registeredUser);
		registeredUser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//splash_song.release();
				Intent loginActivity = new Intent("android.intent.action.LOGIN");
				startActivity(loginActivity);
			}
		});
		newUser=(Button) findViewById(R.id.newUser);
		newUser.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//splash_song.release();
				Intent registerUserActivity = new Intent("android.intent.action.REGISTER_USER");
				startActivity(registerUserActivity);
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//splash_song= MediaPlayer.create(MainActivity.this, R.raw.splash_sound_3);
		//splash_song.start();

	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
