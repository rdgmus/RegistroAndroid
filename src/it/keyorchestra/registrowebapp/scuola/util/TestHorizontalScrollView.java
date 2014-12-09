package it.keyorchestra.registrowebapp.scuola.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.HorizontalScrollView;

public class TestHorizontalScrollView extends HorizontalScrollView {

	@SuppressWarnings("unused")
	private Context _context;

	public TestHorizontalScrollView(Context context) {
		super(context);
		this._context = context;
	}
	

	public TestHorizontalScrollView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this._context = context;
	}


	public TestHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this._context = context;
	}


	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		Log.i("Scrolling", "X from ["+oldl+"] to ["+l+"]");
//		Toast.makeText(_context, "onScrollChanged", Toast.LENGTH_SHORT).show();
	}

}
