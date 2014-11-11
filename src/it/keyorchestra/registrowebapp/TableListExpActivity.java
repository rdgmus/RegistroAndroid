package it.keyorchestra.registrowebapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.keyorchestra.registrowebapp.scuola.util.ExpandableListAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

public class TableListExpActivity extends Activity {
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listDataHeader;
	HashMap<String, List<String>> listDataChild;
	String activities[] = { "Login", "Iscrizione", "Email", "Camera", "Data",
			"GFX", "GFXSurface", "SoundStaff", "SQLLiteExample",
			"ScuolaActivity", "ScuolaTablesMenu", "AdminDatabases" };
	String tables[] = { "UtentiScuola" };

	String[] htArray = { "Top 250", "Now Showing", "Coming Soon..",
			"Activity List", "Scuola.Tables" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.table_list_exp);

		// get the listview
		expListView = (ExpandableListView) findViewById(R.id.expandableListView1);

		// preparing list data
		prepareListData();

		listAdapter = new ExpandableListAdapter(this, listDataHeader,
				listDataChild, "TableListExpActivity");

		// setting list adapter
		expListView.setAdapter(listAdapter);

		expListView.setOnGroupClickListener(new OnGroupClickListener() {

			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				// Toast.makeText(getApplicationContext(),
				// "Group Clicked " + listDataHeader.get(groupPosition),
				// Toast.LENGTH_SHORT).show();
				return false;
			}

		});
		// Listview Group expanded listener
		expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

			@Override
			public void onGroupExpand(int groupPosition) {
				// Toast.makeText(getApplicationContext(),
				// listDataHeader.get(groupPosition) + " Expanded",
				// Toast.LENGTH_SHORT).show();
			}
		});
		// Listview Group collasped listener
		expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

			@Override
			public void onGroupCollapse(int groupPosition) {
				// Toast.makeText(getApplicationContext(),
				// listDataHeader.get(groupPosition) + " Collapsed",
				// Toast.LENGTH_SHORT).show();

			}
		});
		// Listview on child click listener
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Toast.makeText(
						getApplicationContext(),
						listDataHeader.get(groupPosition)
								+ " : "
								+ listDataChild.get(
										listDataHeader.get(groupPosition)).get(
										childPosition), Toast.LENGTH_SHORT)
						.show();

				String cheese = null;

				if (listDataHeader.get(groupPosition).equals("Activity List"))
					cheese = activities[childPosition];
				else if (listDataHeader.get(groupPosition).equals(
						"Scuola.Tables"))
					cheese = tables[childPosition];
				else
					return false;
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
					} else if (cheese.equals("UtentiScuola")) {
						path += "scuola.";
					}
					ourClass = Class.forName(path + cheese);
					Intent ourIntent = new Intent(TableListExpActivity.this,
							ourClass);
					startActivity(ourIntent);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return false;
			}

		});

	}

	/*
	 * Preparing the list data
	 */
	private void prepareListData() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();

		// Adding header data
		for (int j = 0; j < htArray.length; j++)
			listDataHeader.add(htArray[j]);

		// listDataHeader.add("Top 250");
		// listDataHeader.add("Now Showing");
		// listDataHeader.add("Coming Soon..");
		// listDataHeader.add("Activity List");
		// listDataHeader.add("Scuola.Tables");

		// Adding child data
		List<String> top250 = new ArrayList<String>();
		top250.add("The Shawshank Redemption");
		top250.add("The Godfather");
		top250.add("The Godfather: Part II");
		top250.add("Pulp Fiction");
		top250.add("The Good, the Bad and the Ugly");
		top250.add("The Dark Knight");
		top250.add("12 Angry Men");

		List<String> nowShowing = new ArrayList<String>();
		nowShowing.add("The Conjuring");
		nowShowing.add("Despicable Me 2");
		nowShowing.add("Turbo");
		nowShowing.add("Grown Ups 2");
		nowShowing.add("Red 2");
		nowShowing.add("The Wolverine");

		List<String> comingSoon = new ArrayList<String>();
		comingSoon.add("2 Guns");
		comingSoon.add("The Smurfs 2");
		comingSoon.add("The Spectacular Now");
		comingSoon.add("The Canyons");
		comingSoon.add("Europa Report");

		// Adding child data
		ArrayList<String> activityList = new ArrayList<String>();
		for (int j = 0; j < activities.length; j++) {
			activityList.add(activities[j]);
		}

		// Adding child data
		ArrayList<String> tablesList = new ArrayList<String>();
		for (int j = 0; j < tables.length; j++) {
			tablesList.add(tables[j]);
		}

		listDataChild.put(listDataHeader.get(0), top250); // Header, Child data
		listDataChild.put(listDataHeader.get(1), nowShowing);
		listDataChild.put(listDataHeader.get(2), comingSoon);
		listDataChild.put(listDataHeader.get(3), activityList); // Header, Child
		// data
		listDataChild.put(listDataHeader.get(4), tablesList); // Header, Child
		// data
	}

}
