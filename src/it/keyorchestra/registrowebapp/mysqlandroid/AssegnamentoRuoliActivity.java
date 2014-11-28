package it.keyorchestra.registrowebapp.mysqlandroid;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.keyorchestra.registrowebapp.dbMatthed.DatabaseOps;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AssegnamentoRuoliActivity extends Activity implements
		ActivitiesCommonFunctions {
	private SharedPreferences getPrefs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_assegnamento_ruoli);

		CaricaUtentiRuoli("utenti_scuola");
	}

	private void CaricaUtentiRuoli(String table_name) {
		// TODO Auto-generated method stub
		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);

		String ip = getDatabaseIpFromPreferences();

		String query = "SELECT * FROM " + table_name + " WHERE is_locked = 1";

		JSONArray jArray;
		try {
			jArray = new MySqlAndroid().retrieveTableData(
					getApplicationContext(), "http://" + ip + "/"
							+ retrieveTableData + "?table_name=" + table_name
							+ "&sql=" + URLEncoder.encode(query, "UTF-8"),
					table_name);

			if (jArray == null) {
				Toast.makeText(
						getApplicationContext(),
						"Nessuna risposta dal server MySQL! Controlla lo stato di Apache & MySQL server!",
						Toast.LENGTH_LONG).show();
			} else {
				buildUsersAndRolesTable(jArray);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void buildUsersAndRolesTable(JSONArray jArray) {
		// TODO Auto-generated method stub
		try {
			// HEADER TABLE
			TableLayout headerTable = (TableLayout) findViewById(R.id.header_table);

			headerTable = addRowToTable(getApplicationContext(), headerTable,
					LayoutParams.WRAP_CONTENT);
			headerTable.setBackgroundColor(getResources().getColor(
					R.color.colorBlack));

			// CONTENT TABLE
			TableLayout contentTable = (TableLayout) findViewById(R.id.content_table);

			int maxLengthRow = 0;
			@SuppressWarnings("unused")
			JSONObject longestRow = null;

			for (int i = 0; i < jArray.length(); i++) {
				JSONObject json_data = jArray.getJSONObject(i);

				int lengthRow = (String.valueOf(json_data.getInt("id_utente"))
						+ json_data.getString("cognome") + " "
						+ json_data.getString("nome")
						+ String.valueOf(json_data.getInt("is_locked")) + json_data
						.getString("email")).length();

				if (lengthRow > maxLengthRow || i == 0) {
					maxLengthRow = lengthRow;
					longestRow = json_data;
				}
				contentTable = addRowToTableAsJson(getApplicationContext(),
						contentTable, json_data, LayoutParams.WRAP_CONTENT, i);

			}
			// headerTable = addRowToTableAsJson(getApplicationContext(),
			// headerTable, longestRow, convertPixelsToDp(1), -1);
		} catch (JSONException e) {

			Log.e("log_tag", "Error parsing data" + e.toString());

			Toast.makeText(getApplicationContext(), "JsonArray fail",
					Toast.LENGTH_SHORT).show();

		}
	}

	@Override
	public void registerToolTipFor(ImageButton ib) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean customToast(CharSequence charSequence, int iconId,
			int layoutId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDefaultDatabaseFromPreferences() {
		// TODO Auto-generated method stub
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		return defaultDatabase;
	}

	@Override
	public String getDatabaseIpFromPreferences() {
		// TODO Auto-generated method stub
		String defaultDatabase = getDefaultDatabaseFromPreferences();

		String ip = null;

		if (defaultDatabase.contentEquals("MySQL")) {
			ip = getPrefs.getString("ipMySQL", "");
		} else if (defaultDatabase.contentEquals("PostgreSQL")) {
			ip = getPrefs.getString("ipPostgreSQL", "");
		}
		return ip;
	}

	@Override
	public void startAnimation(ImageButton ib, long durationInMilliseconds) {
		// TODO Auto-generated method stub

	}

	protected TableLayout addRowToTableAsJson(Context applicationContext,
			TableLayout headerTable, JSONObject json_data, int height, int index)
			throws JSONException {
		// TODO Auto-generated method stub
		TableRow tr = new TableRow(AssegnamentoRuoliActivity.this);

		if (index % 2 == 0)
			tr.setBackgroundColor(getResources()
					.getColor(R.color.colorListItem));
		else
			tr.setBackgroundColor(getResources().getColor(R.color.colorOrange));

		// android:layout_width="0dp" // this t.v will consume all width
		// android:layout_weight="1"
		// android:layout_height="match_parent"
		TextView b = new TextView(AssegnamentoRuoliActivity.this);

		String stime = String.valueOf(json_data.getInt("id_utente"));

		b.setText(stime);

		b.setTextColor(Color.RED);

		b.setTextSize(15);

		tr.addView(b);

		TextView b1 = new TextView(AssegnamentoRuoliActivity.this);

		b1.setPadding(10, 0, 0, 0);

		b1.setTextSize(15);

		String stime1 = json_data.getString("cognome") + " "
				+ json_data.getString("nome");

		b1.setText(stime1);

		b1.setTextColor(Color.BLACK);

		tr.addView(b1);

		TextView b3 = new TextView(AssegnamentoRuoliActivity.this);

		b3.setPadding(10, 0, 0, 0);

		b3.setTextSize(15);

		String stime3 = json_data.getString("email");

		b3.setText(stime3);

		b3.setTextColor(Color.BLACK);

		tr.addView(b3);

		// QUI VOGLIO METTERE UNA CHECKBOX
		final CheckBox b2 = new CheckBox(AssegnamentoRuoliActivity.this);

		b2.setChecked((json_data.getInt("is_locked") == 1) ? true : false);
		b2.setText(json_data.getInt("is_locked") == 1 ? "Sì" : "No");

		b2.setTag(json_data.getInt("id_utente"));

		b2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				DatabaseOps dataBaseOps = new DatabaseOps(
						getApplicationContext());
				dataBaseOps.setUserLocked(getApplicationContext(),
						(Integer) b2.getTag(), isChecked);
			}
		});

		tr.addView(b2);
		tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0));
		tr.setWeightSum(1);

		headerTable.addView(tr);
		return headerTable;
	}

	@SuppressLint("NewApi")
	protected TableLayout addRowToTable(Context context, TableLayout table,
			int height) {
		TableRow tr = new TableRow(AssegnamentoRuoliActivity.this);

		tr.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));

		if (height != LayoutParams.WRAP_CONTENT)
			tr.setVisibility(View.INVISIBLE);

		TextView b6 = new TextView(AssegnamentoRuoliActivity.this);
		b6.setText("Id");
		b6.setTextColor(Color.WHITE);
		b6.setTextSize(15);
		// b6.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tr.addView(b6);

		TextView b19 = new TextView(AssegnamentoRuoliActivity.this);
		b19.setPadding(10, 0, 0, 0);
		b19.setTextSize(15);
		b19.setText("Cognome & Nome");
		b19.setTextColor(Color.WHITE);
		// b19.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tr.addView(b19);

		TextView b20 = new TextView(AssegnamentoRuoliActivity.this);
		b20.setPadding(10, 0, 0, 0);
		b20.setTextSize(15);
		b20.setText("Email");
		b20.setTextColor(Color.WHITE);
		// b20.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
		tr.addView(b20);

		TextView b29 = new TextView(AssegnamentoRuoliActivity.this);
		b29.setPadding(10, 0, 0, 0);
		b29.setText("Blocco");
		b29.setTextColor(Color.WHITE);
		b29.setTextSize(15);
		// b29.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

		tr.addView(b29);
		table.addView(tr);

		return table;

	}

}
