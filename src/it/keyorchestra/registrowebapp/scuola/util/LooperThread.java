package it.keyorchestra.registrowebapp.scuola.util;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

public class LooperThread extends Thread {
	public Handler mHandler;

	@SuppressLint("HandlerLeak")
	public void run() {
		Looper.prepare();

		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				// process incoming messages here
			}
		};

		Looper.loop();
	}
}
