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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Menu extends ListActivity {

	String classes[] = { "Login", "Iscrizione", "Email", "Camera", "Data",
			"GFX", "GFXSurface", "SoundStaff", "SQLLiteExample",
			"ScuolaActivity", "ScuolaTablesMenu" ,"TableListExpActivity"};
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

		// setContentView(R.layout.activity_menu);
		// setListAdapter(new ArrayAdapter<String>(Menu.this,
		// android.R.layout.simple_list_item_1, classes));
		pics = new ArrayList<Bitmap>();
		InputStream is = getResources().openRawResource(R.drawable.activity);
		bmp = BitmapFactory.decodeStream(is);
		for (int i = 0; i < classes.length; i++) {
			pics.add(bmp);
		}
		mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		setListAdapter(new ArrayAdapter<Bitmap>(Menu.this, R.layout.row, pics) {
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
				tv.setText(classes[position]);

				return row;
			}
		});

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		String cheese = classes[position];
		// // A DIALOG BOX TO TEST POSITION
		// Dialog d = new Dialog(this);
		// d.setTitle("onListItemClick:"+cheese);
		// d.show();
		try {
			@SuppressWarnings("rawtypes")
			Class ourClass;
			String path = "it.keyorchestra.registrowebapp.";
			if (cheese.equals("SQLLiteExample")
					|| cheese.equals("ScuolaActivity")) {
				path += "sqllite.";
			}
			ourClass = Class.forName(path + cheese);
			Intent ourIntent = new Intent(Menu.this, ourClass);
			startActivity(ourIntent);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		// TODO Auto-generated method stub
		// super.onCreateOptionsMenu(menu);

		MenuInflater blowUp = getMenuInflater();

		blowUp.inflate(R.menu.cool_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		// return super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.aboutUs:
			Intent i = new Intent("it.keyorchestra.registrowebapp.ABOUT");
			startActivity(i);
			break;
		case R.id.preferences:
			Intent p = new Intent("it.keyorchestra.registrowebapp.PREFS");
			startActivity(p);
			break;
		case R.id.databases:
			Intent d = new Intent("it.keyorchestra.registrowebapp.DATABASE");
			startActivity(d);
			break;
		case R.id.exit:
			finish();
			break;

		default:
			break;
		}

		return false;
	}

}
