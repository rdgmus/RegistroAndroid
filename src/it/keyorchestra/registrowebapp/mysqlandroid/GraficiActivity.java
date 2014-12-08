package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.R;
import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions;
import it.keyorchestra.registrowebapp.scuola.util.ExpandableListAdapter;
import it.keyorchestra.registrowebapp.scuola.util.GraficiArrayAdapter;
import it.keyorchestra.registrowebapp.scuola.util.PeriodiLogAdapter;
import it.keyorchestra.registrowebapp.scuola.util.RuoliArrayAdapter;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
	private ArrayList<String> periodiArray;
	private JSONArray jArrayPeriodi;

	LinearLayout llConnessioni;
	TextView tvNoGraph;

	private JSONArray jArray;
	private int anno = 2014, mese = 11;
	Spinner spinnerSceltaGrafico, spinnerTipoGrafico, spinnerViewPeriod;

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

		// FILTRO PERIODO LOG
		spinnerViewPeriod = (Spinner) findViewById(R.id.spinnerViewPeriod);
		CaricaPeriodiLog();
		PeriodiLogAdapter periodiLogAdapter = new PeriodiLogAdapter(
				getApplicationContext(), jArrayPeriodi, periodiArray);
		periodiLogAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerViewPeriod.setAdapter(periodiLogAdapter);
		spinnerViewPeriod
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						Toast.makeText(getBaseContext(),
								"" + parent.getItemAtPosition(position),
								Toast.LENGTH_SHORT).show();
						TextView myTextView = (TextView) view
								.findViewById(R.id.tvMyText);
						String[] tag = (String[]) myTextView.getTag();
						setAnno(Integer.valueOf(tag[0]));
						setMese(Integer.valueOf(tag[1]));

						drawGraph(
								spinnerTipoGrafico.getSelectedItemPosition() == 0 ? GraphTypes.LINE
										: GraphTypes.BAR, spinnerSceltaGrafico
										.getSelectedItemPosition());
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		// TIPO GRAFICO
		spinnerTipoGrafico = (Spinner) findViewById(R.id.spinnerTipoGrafico);
		String[] tipoGraficiList;
		tipoGraficiList = getResources()
				.getStringArray(R.array.tipoGraficiList);
		GraficiArrayAdapter tipoGraficiListAdapter = new GraficiArrayAdapter(
				getBaseContext(), tipoGraficiList);
		tipoGraficiListAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinnerTipoGrafico.setAdapter(tipoGraficiListAdapter);
		spinnerTipoGrafico
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						Toast.makeText(getBaseContext(),
								"" + parent.getItemAtPosition(position),
								Toast.LENGTH_SHORT).show();
						switch (position) {
						case 0:
							drawGraph(GraphTypes.LINE, spinnerSceltaGrafico
									.getSelectedItemPosition());
							break;
						case 1:
							drawGraph(GraphTypes.BAR, spinnerSceltaGrafico
									.getSelectedItemPosition());
							break;
						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		// SCELTA DATI GRAFICO
		spinnerSceltaGrafico = (Spinner) findViewById(R.id.spinnerSceltaGrafico);
		// CaricaArrayRuoli();
		String[] graficiArrayList;
		graficiArrayList = getResources().getStringArray(R.array.graficiList);
		GraficiArrayAdapter graficiArrayAdapter = new GraficiArrayAdapter(
				getBaseContext(), graficiArrayList);
		//
		graficiArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		// Apply the adapter to the spinner
		spinnerSceltaGrafico.setAdapter(graficiArrayAdapter);

		spinnerSceltaGrafico
				.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						// TODO Auto-generated method stub
						Toast.makeText(getBaseContext(),
								"" + parent.getItemAtPosition(position),
								Toast.LENGTH_SHORT).show();
						switch (position) {
						case 0:
							drawGraph(
									spinnerTipoGrafico
											.getSelectedItemPosition() == 0 ? GraphTypes.LINE
											: GraphTypes.BAR, position);
							break;
						case 1:
							drawGraph(
									spinnerTipoGrafico
											.getSelectedItemPosition() == 0 ? GraphTypes.LINE
											: GraphTypes.BAR, position);
							break;
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
						// TODO Auto-generated method stub

					}
				});

		drawGraph(
				spinnerTipoGrafico.getSelectedItemPosition() == 0 ? GraphTypes.LINE
						: GraphTypes.BAR,
				spinnerSceltaGrafico.getSelectedItemPosition());

	}

	private void CaricaPeriodiLog() {
		// TODO Auto-generated method stub
		periodiArray = new ArrayList<String>();
		String retrieveTableData = getPrefs
				.getString("retrieveTableData", null);

		String ip = getDatabaseIpFromPreferences();

		// jsonRuoliAmmessi(retrieveTableData, ip);

		String query = "SELECT DISTINCT year(when_registered) as anno, month(when_registered) as mese "
				+ " FROM scuola.utenti_logger WHERE msg_type = 'LOGIN_SUCCESS'"
				+ " ORDER BY when_registered DESC";

		try {
			jArrayPeriodi = new MySqlAndroid().retrieveTableData(
					getApplicationContext(),
					"http://" + ip + "/" + retrieveTableData + "?sql="
							+ URLEncoder.encode(query, "UTF-8"), null);

			for (int i = 0; i < jArrayPeriodi.length(); i++) {
				JSONObject json_data;
				try {
					json_data = jArrayPeriodi.getJSONObject(i);
					String anno = json_data.getString("anno");
					String mese = json_data.getString("mese");

					periodiArray.add("" + anno + "/" + mese);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	protected void drawGraph(GraphTypes graphType, int position) {
		// TODO Auto-generated method stub

		loadData(position);

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
				switch (position) {
				case 0:
					giorno = json_data.getString("giorno");
					sTimestamp = giorno + "/" + mese;
					break;
				case 1:
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

		switch (spinnerSceltaGrafico.getSelectedItemPosition()) {
		case 0:
			graphTitle = "Connessioni Giornaliere:" + getMese() + "/"
					+ getAnno();
			break;
		case 1:
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

	private void showNoGraphMessage() {
		// TODO Auto-generated method stub
		llConnessioni.removeAllViews();
		llConnessioni.addView(tvNoGraph);
	}

	/**
	 * Carica i dati per iul grafico selezionato nella posizione all'interno
	 * dello spinner 'spinnerSceltaGrafico'
	 * 
	 * @param position
	 */
	private void loadData(int position) {
		// TODO Auto-generated method stub

		switch (position) {
		case 0:// GRAFICO LOGIN_SUCCESS NEL MESE DELL'ANNO GIORNO PER GIORNO
			jArray = getDailyConnectionAsJSON(mese, anno);
			break;
		case 1:// GRAFICO LOGIN_SUCCESS NELL'ANNO MESE PER MESE
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

}
