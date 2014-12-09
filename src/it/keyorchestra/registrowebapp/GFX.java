package it.keyorchestra.registrowebapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import it.keyorchestra.registrowebapp.KeyOrchestraView;

public class GFX extends Activity{

	KeyOrchestraView ourView;
	WakeLock wL;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		//wake-lock
		PowerManager pM = (PowerManager)getSystemService(POWER_SERVICE);
		wL = pM.newWakeLock(PowerManager.FULL_WAKE_LOCK, "whatever");
		
		super.onCreate(savedInstanceState);
		
		wL.acquire();
		
		ourView = new KeyOrchestraView(this);
		setContentView(ourView);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		wL.release();
	}

}
