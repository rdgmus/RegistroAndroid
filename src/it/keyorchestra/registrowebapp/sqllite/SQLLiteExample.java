package it.keyorchestra.registrowebapp.sqllite;

import it.keyorchestra.registrowebapp.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SQLLiteExample extends Activity implements OnClickListener {

	Button sqlUpdate, sqlView, bGetInfo, bEdit, bDelete;
	EditText sqlName, sqlHotness, etRowInfo;

	boolean allOk = true;
	int numRows = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sqlliteexample);

		sqlUpdate = (Button) findViewById(R.id.bSQLUpdate);
		sqlView = (Button) findViewById(R.id.bSQLOpenView);
		bGetInfo = (Button) findViewById(R.id.bGetInfo);
		bEdit = (Button) findViewById(R.id.bEdit);
		bDelete = (Button) findViewById(R.id.bDelete);

		sqlName = (EditText) findViewById(R.id.etSQLName);
		sqlHotness = (EditText) findViewById(R.id.etSQLHotness);
		etRowInfo = (EditText) findViewById(R.id.etRowInfo);

		sqlUpdate.setOnClickListener(this);
		sqlView.setOnClickListener(this);
		bGetInfo.setOnClickListener(this);
		bEdit.setOnClickListener(this);
		bDelete.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bSQLUpdate:// CREATE RECORD...
			allOk = true;
			try {
				String name = sqlName.getText().toString();
				String hotness = sqlHotness.getText().toString();

				HotOrNot entry = new HotOrNot(SQLLiteExample.this);
				entry.open();
				entry.createEntry(name, hotness);
				entry.close();
			} catch (Exception e) {
				// TODO: handle exception
				allOk = false;
				// DIALOG
				Dialog d = new Dialog(this);
				d.setTitle("bSQLUpdate:");

				TextView tv = new TextView(this);
				tv.setBackgroundColor(Color.WHITE);
				tv.setText("Error!: " + e.getMessage());
				d.setContentView(tv);
				d.show();
			} finally {
				if (allOk) {
					// DIALOG
					Dialog d = new Dialog(this);
					d.setTitle("bSQLUpdate:");

					TextView tv = new TextView(this);
					tv.setBackgroundColor(Color.WHITE);
					tv.setText("Success!");
					d.setContentView(tv);
					d.show();
				}
			}
			break;
		case R.id.bSQLOpenView:// OPEN TABLE VIEW OF RECORDS
			Intent i = new Intent("android.intent.action.SQLVIEW");
			startActivity(i);
			break;
		case R.id.bGetInfo:
			try {
				String s = etRowInfo.getText().toString();
				long l = Long.parseLong(s);

				HotOrNot hon = new HotOrNot(this);
				hon.open();
				String returnedName = hon.getName(l);
				String returnedHotness = hon.getHotness(l);
				hon.close();

				sqlName.setText(returnedName);
				sqlHotness.setText(returnedHotness);
			} catch (Exception e) {
				// TODO: handle exception
				// DIALOG
				Dialog d = new Dialog(this);
				d.setTitle("bGetInfo:");

				TextView tv = new TextView(this);
				tv.setBackgroundColor(Color.WHITE);
				tv.setText("Error!: " + e.getMessage());
				d.setContentView(tv);
				d.show();
			}
			break;
		case R.id.bEdit:// UPDATE DATABASE
			allOk = true;
			numRows = 0;
			try {
				String sRow = etRowInfo.getText().toString();
				String sName = sqlName.getText().toString();
				String sHotness = sqlHotness.getText().toString();
				long lRow = Long.parseLong(sRow);

				HotOrNot ex = new HotOrNot(this);
				ex.open();
				numRows = ex.updateEntry(lRow, sName, sHotness);
				ex.close();
			} catch (Exception e) {
				// TODO: handle exception
				// DIALOG
				allOk = false;
				Dialog d = new Dialog(this);
				d.setTitle("bEdit:");

				TextView tv = new TextView(this);
				tv.setBackgroundColor(Color.WHITE);
				tv.setText("Error!: " + e.getMessage());
				d.setContentView(tv);
				d.show();
			} finally {
				if (allOk) {
					// DIALOG
					Dialog d = new Dialog(this);
					d.setTitle("bEdit:");

					TextView tv = new TextView(this);
					tv.setBackgroundColor(Color.WHITE);
					tv.setText("Success! num. rows updated=" + numRows);
					d.setContentView(tv);
					d.show();
				}

			}
			break;
		case R.id.bDelete:// DELETE FROM
			allOk = true;
			numRows = 0;

			try {
				String sRow1 = etRowInfo.getText().toString();
				long lRow1 = Long.parseLong(sRow1);

				HotOrNot ex1 = new HotOrNot(this);
				ex1.open();
				numRows = ex1.deleteEntry(lRow1);
				ex1.close();
			} catch (Exception e) {
				// TODO: handle exception
				// DIALOG
				Dialog d = new Dialog(this);
				d.setTitle("bDelete:");

				TextView tv = new TextView(this);
				tv.setBackgroundColor(Color.WHITE);
				tv.setText("Error!: " + e.getMessage());
				d.setContentView(tv);
				d.show();
			} finally {
				if (allOk) {
					// DIALOG
					Dialog d = new Dialog(this);
					d.setTitle("bDelete:");

					TextView tv = new TextView(this);
					tv.setBackgroundColor(Color.WHITE);
					tv.setText("Success! num. rows deleted=" + numRows);
					d.setContentView(tv);
					d.show();
				}

			}
		default:
			break;
		}
	}
}
