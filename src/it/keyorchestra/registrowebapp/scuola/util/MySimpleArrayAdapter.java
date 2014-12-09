package it.keyorchestra.registrowebapp.scuola.util;

import it.keyorchestra.registrowebapp.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList<String> values;

	public MySimpleArrayAdapter(Context context, ArrayList<String> arrayListOfDatabases) {
		super(context, R.layout.icon_row_layout, arrayListOfDatabases);
		this.context = context;
		this.values = arrayListOfDatabases;
	}

	
	
	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);
		textView.setText((CharSequence) values.get(position));

		return rowView;
	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
//		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);
		textView.setText((CharSequence) values.get(position));

		return rowView;
	}
}
