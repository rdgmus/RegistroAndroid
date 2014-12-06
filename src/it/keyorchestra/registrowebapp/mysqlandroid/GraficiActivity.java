package it.keyorchestra.registrowebapp.mysqlandroid;

import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraficiActivity extends Activity implements
		ActivitiesCommonFunctions {
	private SharedPreferences getPrefs;
	TextView tvNoGraph;
	ToggleButton tbGraphType;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grafici);

		getPrefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		tbGraphType = (ToggleButton) findViewById(R.id.tbGraphType);
		tbGraphType.setChecked(false);
		drawLineGraphView();
		tbGraphType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					drawBarGraphView();
				} else {
					drawLineGraphView();
				}
			}
		});

	}

	protected void drawBarGraphView() {
		// TODO Auto-generated method stub
		JSONArray jArray = getDailyConnectionAsJSON();

		GraphViewData[] data = new GraphViewData[jArray.length()];
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject json_data;
			try {
				json_data = jArray.getJSONObject(i);
				int connessioni = json_data.getInt("connessioni");
				String giorno = json_data.getString("giorno");
				String mese = json_data.getString("mese");
				String anno = json_data.getString("anno");

				data[i] = new GraphViewData(i, connessioni);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		GraphView graphView = new BarGraphView(this, "Connessioni Giornaliere");
		// add data
		graphView.addSeries(new GraphViewSeries(data));

		// set view port, start=2, size=40
//		graphView.setViewPort(2, 40);
//		graphView.setScrollable(true);
		// optional - activate scaling / zooming
//		graphView.setScalable(true);

//		graphView.setHorizontalLabels(new String[] {"2 days ago", "yesterday", "today", "tomorrow"});
//		graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.llConnessioni);
		layout.removeAllViews();
		layout.addView(graphView);

	}

	protected void drawLineGraphView() {
		// TODO Auto-generated method stub
		// init example series data
		JSONArray jArray = getDailyConnectionAsJSON();

		GraphViewData[] data = new GraphViewData[jArray.length()];
		for (int i = 0; i < jArray.length(); i++) {
			JSONObject json_data;
			try {
				json_data = jArray.getJSONObject(i);
				int connessioni = json_data.getInt("connessioni");
				String giorno = json_data.getString("giorno");
				String mese = json_data.getString("mese");
				String anno = json_data.getString("anno");

				data[i] = new GraphViewData(i, connessioni);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		GraphView graphView = new LineGraphView(this, "Connessioni Giornaliere");
		// add data
		graphView.addSeries(new GraphViewSeries(data));

		// set view port, start=2, size=40
//		graphView.setViewPort(2, 40);
//		graphView.setScrollable(true);
		// optional - activate scaling / zooming
//		graphView.setScalable(true);

//		graphView.setHorizontalLabels(new String[] {"2 days ago", "yesterday", "today", "tomorrow"});
//		graphView.setVerticalLabels(new String[] {"high", "middle", "low"});
		
		LinearLayout layout = (LinearLayout) findViewById(R.id.llConnessioni);
		layout.removeAllViews();
		layout.addView(graphView);
	}

	private JSONArray getDailyConnectionAsJSON() {
		// TODO Auto-generated method stub
		String graphViewInterface = getPrefs.getString("GraphViewInterface",
				null);
		JSONArray jArray = new MySqlAndroid().getDailyConnectionAsJSON(
				getApplicationContext(), "http://"
						+ getDatabaseIpFromPreferences() + "/"
						+ graphViewInterface + "?graph_type=DAILY");

		return jArray;
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
	public void startAnimation(View ib, long durationInMilliseconds) {
		// TODO Auto-generated method stub

	}

}
