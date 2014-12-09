package it.keyorchestra.registrowebapp;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

@SuppressWarnings("deprecation")
public class BrowseSqlTabLayoutActivity extends TabActivity {
	private String sqlSqLite;
	private String dbname;
	private String tableName;
	private ArrayList<String> arrTblNames;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browse_sql_tab_layout);
		TabHost tabHost = getTabHost();

		// Tab for Photos
		TabSpec sqlspec = tabHost.newTabSpec("SQLite");
		// setting Title and Icon for the Tab
		sqlspec.setIndicator("SQLite",
				getResources().getDrawable(R.drawable.logo_sqlite));
		Intent sqlSqLiteIntent = new Intent(this, SqLite.class);

		// SI ASPETTA L'SQL DA ESEGUIRE, IL NOME DEL DATABASE DA APRIRE E LA
		// TABELLA DA SELEZIONARE
		sqlSqLiteIntent.putExtras(getIntent().getExtras());

		Bundle basket = getIntent().getExtras();
		setSqlSqLite(basket.getString("sql"));
		dbname = basket.getString("dbname");
		setTableName(basket.getString("tableName"));
		setArrTblNames(basket.getStringArrayList("arrTblNames"));
		//
		setTitle("Browse Tables Database : " + dbname);

		sqlspec.setContent(sqlSqLiteIntent);

		// Adding all TabSpec to TabHost
		tabHost.addTab(sqlspec); // Adding photos tab
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.android_tab_layout, menu);
		return true;
	}

	public String getSqlSqLite() {
		return sqlSqLite;
	}

	public void setSqlSqLite(String sqlSqLite) {
		this.sqlSqLite = sqlSqLite;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getArrTblNames() {
		return arrTblNames;
	}

	public void setArrTblNames(ArrayList<String> arrTblNames) {
		this.arrTblNames = arrTblNames;
	}

}
