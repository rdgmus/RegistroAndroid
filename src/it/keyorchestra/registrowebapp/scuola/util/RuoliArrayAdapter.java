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

public class RuoliArrayAdapter extends ArrayAdapter<String> {
	private final Context _context;
	private final JSONArray _objects;

	public RuoliArrayAdapter(Context context, JSONArray objects,
			ArrayList<String> values) {
		super(context, R.layout.icon_row_layout, values);
		// TODO Auto-generated constructor stub
		this._context = context;
		this._objects = objects;
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

		try {
			JSONObject s = (JSONObject) _objects.get(position);
			textView.setText("[" + s.getLong("id_ruolo") + "] "
					+ s.getString("ruolo"));
			textView.setTextColor(_context.getResources().getColor(
					R.color.colorOrange));
			textView.setBackgroundColor(_context.getResources().getColor(
					R.color.colorBlack));
			textView.setTextSize(15);
			textView.setTag(s.getLong("id_ruolo"));
			setIconForRoles(imageView, s.getString("ruolo"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rowView;
	}

	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater
				.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);

		try {
			JSONObject s = (JSONObject) _objects.get(position);
			textView.setText("[" + s.getLong("id_ruolo") + "] "
					+ s.getString("ruolo"));
			textView.setTextColor(_context.getResources().getColor(
					R.color.colorOrange));
			textView.setBackgroundColor(_context.getResources().getColor(
					R.color.colorBlack));
			textView.setTextSize(15);
			textView.setTag(s.getLong("id_ruolo"));
			setIconForRoles(imageView, s.getString("ruolo"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rowView;
	}

	private void setIconForRoles(ImageView ivIsAdmin, String role) {
		// TODO Auto-generated method stub
		Resources res = _context.getResources();
		if (role.contains("Amministratore".toUpperCase())) {
			ivIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.admin64));
		}
		if (role.contains("Insegnante".toUpperCase())) {
			ivIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.professor64));
		}
		if (role.contains("Ata".toUpperCase())) {
			ivIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.ata64));
		}
		if (role.contains("Segreteria".toUpperCase())) {
			ivIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.secretary64));
		}
	}

}
