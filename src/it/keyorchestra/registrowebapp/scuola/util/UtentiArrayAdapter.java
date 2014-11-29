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

public class UtentiArrayAdapter extends ArrayAdapter<String> {

	private final Context _context;
	private final JSONArray _objects;
	
	public UtentiArrayAdapter(Context context, 
			JSONArray objects,  ArrayList<String> values) {
		super(context, R.layout.icon_row_layout , values);
		// TODO Auto-generated constructor stub
		this._context = context;
		this._objects = objects;
	}

	

	/* (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) _context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);
		
		try {
			JSONObject s = (JSONObject) _objects.get(position);
			textView.setText("["+s.getLong("id_utente")+"] "+
					s.getString("cognome")+" "+s.getString("nome")+
					" <"+s.getString("email")+">"
					);
			textView.setTextColor(_context.getResources().getColor(R.color.colorOrange));
			textView.setBackgroundColor(_context.getResources().getColor(R.color.colorBlack));
			textView.setTextSize(15);
			setIconIsAdmin(imageView, s.getLong("user_is_admin"));
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
		View rowView = inflater.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);

		try {
			JSONObject s = (JSONObject) _objects.get(position);
			textView.setText("["+s.getLong("id_utente")+"] "+
					s.getString("cognome")+" "+s.getString("nome")+
					" <"+s.getString("email")+">"
					);
			textView.setTextColor(_context.getResources().getColor(R.color.colorOrange));
			textView.setBackgroundColor(_context.getResources().getColor(R.color.colorBlack));
			textView.setTextSize(15);
			setIconIsAdmin(imageView, s.getLong("user_is_admin"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rowView;
	}

	private void setIconIsAdmin(ImageView ivIsAdmin, Long user_is_admin) {
		// TODO Auto-generated method stub
		Resources res = _context.getResources();
		if (user_is_admin == 1) {
			ivIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.administrator64));
		} else
			ivIsAdmin.setImageDrawable(res
					.getDrawable(R.drawable.ruolo_utente64));

	}
	

}
