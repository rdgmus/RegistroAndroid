package it.keyorchestra.registrowebapp.scuola.util;

import it.keyorchestra.registrowebapp.R;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; // header titles
	// child data in format of header title, child title
	private HashMap<String, List<String>> _listDataChild;
	private String _activityMenu;
	private TextView txtListChild;
	private TextView lblListItemNome, lblListItemTipo, lblListItemNotNull,
			lblListItemDfltValue, lblListItemPK;
	private ImageView childImg;

	private LinearLayout llListFields;

	// private HashMap<String, TableLayout> _listTableChild;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData, String activityMenu) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this._activityMenu = activityMenu;
		// this._listTableChild=listTableChild;
	}



	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.get(childPosititon);
		// return
		// this._listTableChild.get(this._listDataHeader.get(groupPosition))
		// .getChildAt(childPosititon);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		final String childText = (String) getChild(groupPosition, childPosition);

		// INFLATER
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (_activityMenu.equals("TableListExpActivity"))
				convertView = infalInflater.inflate(R.layout.list_item, null);
			else if (_activityMenu.equals("AdminTables"))
				convertView = infalInflater.inflate(
						R.layout.list_database_fields, null);// USA
																// list_database_fields.xml
		}

		if (_activityMenu.equals("TableListExpActivity")) {
			txtListChild = (TextView) convertView
					.findViewById(R.id.lblListItem);

			childImg = (ImageView) convertView
					.findViewById(R.id.imageViewChild);

			String headerTitle = (String) getGroup(groupPosition);

			if (headerTitle.equals("Scuola.Tables")) {
				childImg.setImageResource(R.drawable.tables_list_ok48);
			} else if (headerTitle.equals("Activity List")) {
				if (childText.equals("Login")) {
					childImg.setImageResource(R.drawable.goto_login48);
				} else if (childText.equals("Iscrizione")) {
					childImg.setImageResource(R.drawable.user_add48);
				}else
					childImg.setImageResource(R.drawable.database_list48);

			}else if(headerTitle.equals("Anni") || headerTitle.equals("Mesi") ){
				childImg.setImageResource(R.drawable.calendar48);
			}else
				childImg.setImageResource(R.drawable.database_list48);

			txtListChild.setTypeface(null, Typeface.BOLD);

			txtListChild.setText(childText);

		} else if (_activityMenu.equals("AdminTables")) {
			// TableLayout tl = (TableLayout) convertView
			// .findViewById(R.id.tlFieldParameters);
			// tl.removeAllViews();
			// tl.addView(anOthereConvertView(convertView, childText));

			llListFields = (LinearLayout) convertView
					.findViewById(R.id.llListFields);

			childImg = (ImageView) convertView
					.findViewById(R.id.imageViewChild);
			childImg.setImageResource(R.drawable.field);

			lblListItemNome = (TextView) convertView
					.findViewById(R.id.lblListItemNome);
			lblListItemNome.setText(getInfoField(childText, 0));

			lblListItemTipo = (TextView) convertView
					.findViewById(R.id.lblListItemTipo);
			lblListItemTipo.setText(getInfoField(childText, 1));

			lblListItemNotNull = (TextView) convertView
					.findViewById(R.id.lblListItemNotNull);
			lblListItemNotNull.setText(getInfoField(childText, 2));

			lblListItemDfltValue = (TextView) convertView
					.findViewById(R.id.lblListItemDfltValue);
			lblListItemDfltValue.setText(getInfoField(childText, 3));

			lblListItemPK = (TextView) convertView
					.findViewById(R.id.lblListItemPK);
			lblListItemPK.setText(getInfoField(childText, 4));
		}

		return convertView;
	}

	private TextView getTextViewForColumnName(CharSequence charSequence) {
		// TODO Auto-generated method stub
		Typeface tf = Typeface.createFromAsset(_context.getAssets(),
				"font/Roboto-Bold.ttf");

		TextView textView = new TextView(_context);
		textView.setText(charSequence);
		textView.setMaxLines(1);
		textView.setHorizontallyScrolling(true);
		textView.setTextAppearance(_context,
				android.R.style.TextAppearance_Medium);
		textView.setBackgroundDrawable(_context.getResources().getDrawable(
				R.drawable.cell_shape));
		textView.setGravity(Gravity.CENTER);
		textView.setTypeface(tf);
		// textView.setTextSize(8f);
		return textView;
	}

	private EditText getEditTextForColumnName(CharSequence charSequence) {
		// TODO Auto-generated method stub
		Typeface tf = Typeface.createFromAsset(_context.getAssets(),
				"font/Roboto-Bold.ttf");
		EditText editText = new EditText(_context);
		editText.setText(charSequence);
		editText.setMaxLines(1);
		editText.setHorizontallyScrolling(true);
		editText.setTextSize(12f);
		editText.setTextAppearance(_context,
				android.R.style.TextAppearance_Medium);
		editText.setBackgroundDrawable(_context.getResources().getDrawable(
				R.drawable.cell_shape));
		editText.setGravity(Gravity.CENTER);
		editText.setHorizontallyScrolling(true);
		editText.setTypeface(tf);
		return editText;
	}

	private TableRow anOthereConvertView(View convertView, String childText) {
		// TODO Auto-generated method stub
		TableRow tableRow = new TableRow(_context);

		tableRow.setLayoutParams(new TableRow.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, 1));
		tableRow.setPadding(0, 0, 0, 0);

		for (int j = 0; j <= 4; j++) {
			TextView fieldName = getTextViewForColumnName(getInfoField(
					childText, j));
			// EditText fieldName = getEditTextForColumnName(getInfoField(
			// childText, j));
			fieldName.setLayoutParams(new TableRow.LayoutParams(
					TableRow.LayoutParams.WRAP_CONTENT,
					TableRow.LayoutParams.WRAP_CONTENT, 0.8f));
			fieldName.setHorizontallyScrolling(true);
			tableRow.addView(fieldName);
		}
		// tableRow.setOnClickListener(this);
		return tableRow;
	}

	private CharSequence getInfoField(String childText, int position) {
		// TODO Auto-generated method stub
		String[] splitted = childText.split(";");
		return splitted[position];
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition))
				.size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView lblListHeader = (TextView) convertView
				.findViewById(R.id.lblListHeader);

		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

}