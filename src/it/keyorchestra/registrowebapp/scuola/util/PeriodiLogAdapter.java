package it.keyorchestra.registrowebapp.scuola.util;

import it.keyorchestra.registrowebapp.R;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PeriodiLogAdapter extends ArrayAdapter<String> {

	private final Context _context;
	
	private final JSONArray _jArray;
	private ArrayList<String> _values;

	public PeriodiLogAdapter(Context context, JSONArray jArray,
			ArrayList<String> values) {
		super(context, R.layout.icon_row_layout, values);
		// TODO Auto-generated constructor stub
		this._context = context;
		this._jArray = jArray;
		this._values = values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater
				.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);

		textView.setText(_values.get(position));
		textView.setTextColor(_context.getResources().getColor(
				R.color.colorOrange));
		textView.setBackgroundColor(_context.getResources().getColor(
				R.color.colorBlack));
		textView.setTextSize(12);
		
		try {
			JSONObject jsonObject = _jArray.getJSONObject(position);
			textView.setTag(new String[]{jsonObject.getString("anno"),
					jsonObject.getString("mese")});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Resources res = _context.getResources();

		imageView.setImageDrawable(res.getDrawable(R.drawable.calendar48));

		return rowView;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getDropDownView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater
				.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);

		textView.setText(_values.get(position));
		textView.setTextColor(_context.getResources().getColor(
				R.color.colorOrange));
		textView.setBackgroundColor(_context.getResources().getColor(
				R.color.colorBlack));
		textView.setTextSize(12);
		
		try {
			JSONObject jsonObject = _jArray.getJSONObject(position);
			textView.setTag(new String[]{jsonObject.getString("anno"),
					jsonObject.getString("mese")});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Resources res = _context.getResources();
		imageView.setImageDrawable(res.getDrawable(R.drawable.calendar48));
			
		return rowView;
	}

}
