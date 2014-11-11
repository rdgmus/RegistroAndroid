package it.keyorchestra.registrowebapp.sqllite;

import it.keyorchestra.registrowebapp.R;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

public class SQLView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sqlview);

		try {
			TextView tv = (TextView) findViewById(R.id.tvSQLInfo);
			HotOrNot info = new HotOrNot(this);

			info.open();
			String data = info.getData();
			info.close();
			tv.setText(data);
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
	}

}
