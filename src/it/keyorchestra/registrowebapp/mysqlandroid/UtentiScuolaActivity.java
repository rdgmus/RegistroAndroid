package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class UtentiScuolaActivity extends Activity {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_utenti_scuola);

		RetrieveUsersData("utenti_scuola");
	}

	protected void RetrieveUsersData(String table_name) {
		// TODO Auto-generated method stub

		SharedPreferences getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);
		String defaultDatabase = getPrefs.getString("databaseList", "1");
		String ip = null;
		if (defaultDatabase.contentEquals("PostgreSQL")) {
			Toast.makeText(
					getApplicationContext(),
					"PostgreSQL è il database di default! Correggi menù preferenze",
					Toast.LENGTH_LONG).show();
			return;
		} else if (defaultDatabase.contentEquals("MySQL")) {
			ip = getPrefs.getString("ipMySQL", "");
		}

		JSONArray jArray = new MySqlAndroid().retrieveTableData(
				getApplicationContext(), "http://" + ip + "/"
						+ retrieveTableData + "?table_name=" + table_name,
				table_name);
		if (jArray == null) {
			Toast.makeText(
					getApplicationContext(),
					"Nessuna risposta dal server MySQL! Controlla lo stato di Apache & MySQL server!",
					Toast.LENGTH_LONG).show();
		} else {
			try {
				// JSONArray jArray = new JSONArray(result);
				TableLayout tv = (TableLayout) findViewById(R.id.table);
				tv.setBackgroundColor(getResources().getColor(
						R.color.colorListItem));
				tv.removeAllViewsInLayout();
				int flag = 1;
				for (int i = -1; i < jArray.length() - 1; i++) {
					TableRow tr = new TableRow(UtentiScuolaActivity.this);
					tr.setLayoutParams(new LayoutParams(
							LayoutParams.MATCH_PARENT,
							LayoutParams.WRAP_CONTENT));
					if (flag == 1) {
						TextView b6 = new TextView(UtentiScuolaActivity.this);
						b6.setText("Id");
						b6.setTextColor(Color.BLUE);
						b6.setTextSize(15);
						tr.addView(b6);
						TextView b19 = new TextView(UtentiScuolaActivity.this);
						b19.setPadding(10, 0, 0, 0);
						b19.setTextSize(15);
						b19.setText("Cognome & Nome");
						b19.setTextColor(Color.BLUE);
						tr.addView(b19);
						TextView b29 = new TextView(UtentiScuolaActivity.this);
						b29.setPadding(10, 0, 0, 0);
						b29.setText("Stato");
						b29.setTextColor(Color.BLUE);
						b29.setTextSize(15);
						tr.addView(b29);
						tv.addView(tr);

						final View vline = new View(UtentiScuolaActivity.this);

						vline.setLayoutParams(new TableRow.LayoutParams(
								TableRow.LayoutParams.MATCH_PARENT, 2));

						vline.setBackgroundColor(Color.BLUE);

						tv.addView(vline);

						flag = 0;

					} else {

						JSONObject json_data = jArray.getJSONObject(i);

						Log.i("log_tag",
								"id: " + json_data.getInt("id_utente")
										+ ", Username: "
										+ json_data.getString("cognome") + " "
										+ json_data.getString("nome")
										+ ", Stato: "
										+ json_data.getInt("is_locked"));

						TextView b = new TextView(UtentiScuolaActivity.this);

						String stime = String.valueOf(json_data
								.getInt("id_utente"));

						b.setText(stime);

						b.setTextColor(Color.RED);

						b.setTextSize(15);

						tr.addView(b);

						TextView b1 = new TextView(UtentiScuolaActivity.this);

						b1.setPadding(10, 0, 0, 0);

						b1.setTextSize(15);

						String stime1 = json_data.getString("cognome") + " "
								+ json_data.getString("nome");

						b1.setText(stime1);

						b1.setTextColor(Color.BLACK);

						tr.addView(b1);

						TextView b2 = new TextView(UtentiScuolaActivity.this);

						b2.setPadding(10, 0, 0, 0);

						String stime2 = String.valueOf(json_data
								.getInt("is_locked"));

						b2.setText(stime2);

						b2.setTextColor(Color.BLACK);

						b2.setTextSize(15);

						tr.addView(b2);

						tv.addView(tr);

						final View vline1 = new View(UtentiScuolaActivity.this);

						vline1.setLayoutParams(new TableRow.LayoutParams(
								TableRow.LayoutParams.MATCH_PARENT, 1));

						vline1.setBackgroundColor(Color.WHITE);

						tv.addView(vline1);

					}

				}

			} catch (JSONException e) {

				Log.e("log_tag", "Error parsing data" + e.toString());

				Toast.makeText(getApplicationContext(), "JsonArray fail",
						Toast.LENGTH_SHORT).show();

			}

		}
	}
}
