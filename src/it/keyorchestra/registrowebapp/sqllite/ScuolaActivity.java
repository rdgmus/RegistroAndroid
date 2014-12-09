package it.keyorchestra.registrowebapp.sqllite;

import it.keyorchestra.registrowebapp.scuola.util.DataBaseHelper;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TwoLineListItem;

@SuppressWarnings("deprecation")
public class ScuolaActivity extends ListActivity implements OnTouchListener,
		OnItemClickListener {
	private Cursor cUtentiScuola;
	private SQLiteDatabase db;
	@SuppressLint("SdCardPath")
	private final String DATABASE_PATH = "/data/data/it.keyorchestra.registrowebapp/databases/";
	private final String DATABASE_NAME = "registro";
	boolean allOk = true;

	// TextView table_name;
	boolean checkDB(String databaseName) throws Exception {

		try {
			DataBaseHelper myDbHelper = new DataBaseHelper(getBaseContext(), DATABASE_NAME);
			myDbHelper = new DataBaseHelper(this);

			try {

				myDbHelper.createDataBase();

			} catch (IOException ioe) {

				throw new Error("Unable to create database");
			}

			try {

				myDbHelper.openDataBase();

			} catch (SQLException sqle) {

				throw sqle;

			}
		} catch (Error e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allOk = true;
		setTitle("DATABASE SqLite: " + DATABASE_NAME);
		try {
			if (checkDB(DATABASE_NAME)) {
				db = SQLiteDatabase.openDatabase(DATABASE_PATH
						+ DATABASE_NAME, null,
						SQLiteDatabase.OPEN_READWRITE);
				cUtentiScuola = db
						.rawQuery(
								"SELECT rowid as _id, name,type FROM sqlite_master WHERE type='table'",
								null);
				cUtentiScuola.moveToFirst();
				ListAdapter adapter = new SimpleCursorAdapter(this,
						android.R.layout.simple_list_item_2, cUtentiScuola,
						new String[] { "name", "type" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				getListView().setBackgroundColor(Color.BLUE);
				getListView().setOnTouchListener(this);
				getListView().setOnItemClickListener(this);

				getListView().setAdapter(adapter);
				// cUtentiScuola.close();
				db.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			allOk = false;
			// DIALOG
			Dialog d = new Dialog(this);
			d.setTitle("ScuolaDatabase:");

			TextView tv = new TextView(this);
			tv.setBackgroundColor(Color.WHITE);
			tv.setText("SQLException:" + e.getMessage());
			d.setContentView(tv);
			d.show();
		} finally {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			// cUtentiScuola.close();
			// db.close();
		} catch (SQLException e) {

		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub

		// Toast.makeText(getBaseContext(), "onTouch ! View:"+v.toString()+
		// " MotionEvent:"+event.getAction(), Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg1 instanceof TwoLineListItem) {
			TwoLineListItem tlli = (TwoLineListItem) arg1;
			TextView tv = (TextView) tlli.getChildAt(0);
			// tv.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
			Toast.makeText(
					getBaseContext(),
					"onItemClick ! AdapterView:" + " View: ["
							+ tv.getText().toString() + "] ",
					Toast.LENGTH_SHORT).show();
		}
	}
}
