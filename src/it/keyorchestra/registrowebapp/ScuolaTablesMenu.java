package it.keyorchestra.registrowebapp;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ScuolaTablesMenu extends ListActivity {
	private String tables[] = { "UtentiScuola","Menu" };
	private Bitmap bmp;
	private ArrayList<Bitmap> pics;
	private LayoutInflater mInflater;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
//		// fullscreen
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setBackgroundDrawableResource(
				R.drawable.application_background);

		pics = new ArrayList<Bitmap>();
		InputStream is = getResources().openRawResource(R.drawable.table);
		bmp = BitmapFactory.decodeStream(is);
		pics.add(bmp);
		is = getResources().openRawResource(R.drawable.activity);
		bmp = BitmapFactory.decodeStream(is);
		pics.add(bmp);

		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// setContentView(R.layout.activity_menu);
		// setListAdapter(new ArrayAdapter<String>(ScuolaTablesMenu.this,
		// android.R.layout.simple_list_item_1, tables));
		setListAdapter(new ArrayAdapter<Bitmap>(ScuolaTablesMenu.this,
				R.layout.row, pics) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View row;

				if (null == convertView) {
					row = mInflater.inflate(R.layout.row, null);
				} else {
					row = convertView;
				}

				ImageView iv = (ImageView) row.findViewById(R.id.ivTableBitmap);
				iv.setImageBitmap(getItem(position));

				TextView tv = (TextView) row.findViewById(R.id.tvTableName);
				tv.setText(tables[position]);

				return row;
			}
		});
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		String cheese = tables[position];
		try {
			@SuppressWarnings("rawtypes")
			Class ourClass;
			String path = "it.keyorchestra.registrowebapp.scuola.";
			if (cheese.equals("Menu")) {
				path = "it.keyorchestra.registrowebapp.";
			}
			ourClass = Class.forName(path + cheese);
			Intent ourIntent = new Intent(ScuolaTablesMenu.this, ourClass);
			startActivity(ourIntent);
			if(cheese.equals("Menu"))
				finish();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
