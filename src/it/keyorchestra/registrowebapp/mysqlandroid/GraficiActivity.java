package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.scuola.util.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class GraficiActivity extends Activity implements
		ActivitiesCommonFunctions {
	private SharedPreferences getPrefs;

	LinearLayout llConnessioni;
	TextView tvNoGraph;
	ToggleButton tbGraphType;
	RadioGroup rgGraphChoose;
	private JSONArray jArray;
	ExpandableListView expListViewPeriods;
	ExpandableListAdapter listAdapter;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	private int anno, mese;

	enum GraphTypes {
		LINE, BAR
	};

	/**
	 * @return the anno
	 */
	public int getAnno() {
		return anno;
	}

	/**
	 * @param anno
	 *            the anno to set
	 */
	public void setAnno(int anno) {
		this.anno = anno;
	}

	/**
	 * @return the mese
	 */
	public int getMese() {
		return mese;
	}

	/**
	 * @param mese
	 *            the mese to set
	 */
	public void setMese(int mese) {
		this.mese = mese;
	}

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

		llConnessioni = (LinearLayout) findViewById(R.id.llConnessioni);
		tvNoGraph = (TextView) llConnessioni.findViewById(R.id.tvNoGraph);

		expListViewPeriods = (ExpandableListView) findViewById(R.id.expListViewPeriods);
		prepareListData();
		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild, "TableListExpActivity");

		// setting list adapter
		expListViewPeriods.setAdapter(listAdapter);
		expListViewPeriods.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				switch (groupPosition) {
				case 0:
					setAnno(2014);
					Toast.makeText(getBaseContext(), "Anno: " + getAnno(),
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					setMese(childPosition + 1);
					Toast.makeText(
							getBaseContext(),
							"Mese: "
									+ getMeseAsString(), Toast.LENGTH_SHORT)
							.show();
					break;

				default:
					break;
				}
				parent.collapseGroup(groupPosition);
				drawGraph(tbGraphType.isChecked() ? GraphTypes.BAR
						: GraphTypes.LINE);
				return true;
			}
		});

		rgGraphChoose = (RadioGroup) findViewById(R.id.rgGraphChoose);
		rgGraphChoose.check(R.id.radioDailyConnections);
		rgGraphChoose
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						// TODO Auto-generated method stub
						drawGraph(tbGraphType.isChecked() ? GraphTypes.BAR
								: GraphTypes.LINE);
					}
				});
		tbGraphType = (ToggleButton) findViewById(R.id.tbGraphType);
		tbGraphType.setChecked(false);
		drawGraph(GraphTypes.LINE);
		tbGraphType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					drawGraph(GraphTypes.BAR);
				} else {
					drawGraph(GraphTypes.LINE);
				}
			}
		});

	}

	protected JSONArray getMontlyConnectionAsJSON(int anno) {
		// TODO Auto-generated method stub
		String graphViewInterface = getPrefs.getString("GraphViewInterface",
				null);
		JSONArray jArray = new MySqlAndroid().getMontlyConnectionAsJSON(
				getApplicationContext(), "http://"
						+ getDatabaseIpFromPreferences() + "/"
						+ graphViewInterface + "?graph_type=MONTLY" + "&anno="
						+ anno);

		return jArray;
	}

	protected void drawGraph(GraphTypes graphType) {
		// TODO Auto-generated method stub

		loadData();

		GraphViewData[] data = new GraphViewData[jArray.length()];
		String[] dataArray = new String[jArray.length()];
		String graphTitle = null;

		for (int i = 0; i < jArray.length(); i++) {
			JSONObject json_data;
			try {
				json_data = jArray.getJSONObject(i);
				int connessioni = json_data.getInt("connessioni");
				String giorno = null;
				String mese = json_data.getString("mese");
				String anno = json_data.getString("anno");

				String sTimestamp = null;
				switch (rgGraphChoose.getCheckedRadioButtonId()) {
				case R.id.radioDailyConnections:
					giorno = json_data.getString("giorno");
					sTimestamp = giorno + "/" + mese;
					break;
				case R.id.radioMontlyConnections:
					sTimestamp = mese + "/" + anno;
					break;
				}

				data[i] = new GraphViewData(i, connessioni);
				dataArray[i] = sTimestamp;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				showNoGraphMessage();
				return;
			}

		}

		switch (rgGraphChoose.getCheckedRadioButtonId()) {
		case R.id.radioDailyConnections:
			graphTitle = "Connessioni Giornaliere:" + getMeseAsString() + "/"
					+ getAnno();
			break;
		case R.id.radioMontlyConnections:
			graphTitle = "Connessioni Mensili:" + getAnno();
			break;
		}

		GraphView graphView;
		switch (graphType) {
		case LINE:
			graphView = new LineGraphView(this, graphTitle);
			break;
		case BAR:
			graphView = new BarGraphView(this, graphTitle);
			break;
		default:
			return;
		}
		// add data
		graphView.addSeries(new GraphViewSeries(data));
		graphView.setHorizontalLabels(dataArray);
		// graphView.setVerticalLabels(new String[] {"high", "middle", "low"});

		// set view port, start=2, size=40
		// graphView.setViewPort(1, 4);
		// graphView.setScrollable(true);

		// set styles
		graphView.getGraphViewStyle().setGridColor(Color.GREEN);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(
				getApplicationContext().getResources().getColor(
						R.color.colorOrange));
		graphView.getGraphViewStyle().setVerticalLabelsColor(
				getApplicationContext().getResources().getColor(
						R.color.colorOrange));
		graphView.getGraphViewStyle().setTextSize(
				getResources().getDimension(R.dimen.graphDimTiny));
		// graphView.getGraphViewStyle().setNumHorizontalLabels(4);
		// graphView.getGraphViewStyle().setNumVerticalLabels(4);
		// graphView.getGraphViewStyle().setVerticalLabelsWidth(300);
		// optional - activate scaling / zooming
		// graphView.setScalable(true);

		llConnessioni.removeAllViews();
		llConnessioni.addView(graphView);
	}


	private String getMeseAsString() {
		// TODO Auto-generated method stub
		List<String> mesi = listDataChild.get("Mesi");
		return mesi.get(getMese()-1);
	}

	private void showNoGraphMessage() {
		// TODO Auto-generated method stub
		llConnessioni.removeAllViews();
		llConnessioni.addView(tvNoGraph);
	}

	private void loadData() {
		// TODO Auto-generated method stub
		switch (rgGraphChoose.getCheckedRadioButtonId()) {
		case R.id.radioDailyConnections:
			jArray = getDailyConnectionAsJSON(mese, anno);
			break;
		case R.id.radioMontlyConnections:
			jArray = getMontlyConnectionAsJSON(anno);
			break;
		default:
			break;
		}
	}

	protected JSONArray getDailyConnectionAsJSON(int mese, int anno) {
		// TODO Auto-generated method stub
		String graphViewInterface = getPrefs.getString("GraphViewInterface",
				null);
		JSONArray jArray = new MySqlAndroid().getDailyConnectionAsJSON(
				getApplicationContext(), "http://"
						+ getDatabaseIpFromPreferences() + "/"
						+ graphViewInterface + "?graph_type=DAILY" + "&anno="
						+ anno + "&mese=" + mese);

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

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding header data

		listDataHeader.add("Anni");
		listDataHeader.add("Mesi");

		// Adding child data
		ArrayList<String> anniList = new ArrayList<String>();
		ArrayList<String> mesiList = new ArrayList<String>();
		anniList.add("2014");
		mesiList.add("Gennaio");
		mesiList.add("Febbraio");
		mesiList.add("Marzo");
		mesiList.add("Aprile");
		mesiList.add("Maggio");
		mesiList.add("Giugno");
		mesiList.add("Luglio");
		mesiList.add("Agosto");
		mesiList.add("Settembre");
		mesiList.add("Ottobre");
		mesiList.add("Novembre");
		mesiList.add("Dicembre");

		listDataChild.put(listDataHeader.get(0), anniList); // Header, Child
															// data
		listDataChild.put(listDataHeader.get(1), mesiList);
	}

}
