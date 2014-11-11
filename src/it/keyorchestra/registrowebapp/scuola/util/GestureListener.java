package it.keyorchestra.registrowebapp.scuola.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

public class GestureListener extends GestureDetector.SimpleOnGestureListener {

	private Context _context;
	float x ;
	float y ;

	public GestureListener(Context context) {
		// TODO Auto-generated constructor stub
		this._context = context;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		 x = e.getX();
		 y = e.getY();
		Toast.makeText(_context, "Tapped onDown at: (" + x + "," + y + ")",
				Toast.LENGTH_SHORT).show();
		return true;
	}

	// event when double tap occurs
	@Override
	public boolean onDoubleTap(MotionEvent e) {
		 x = e.getX();
		 y = e.getY();

		Toast.makeText(_context, "Double Tap Tapped at: (" + x + "," + y + ")",
				Toast.LENGTH_SHORT).show();

		return true;
	}

}
