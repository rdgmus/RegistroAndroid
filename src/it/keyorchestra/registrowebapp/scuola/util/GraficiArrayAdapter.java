package it.keyorchestra.registrowebapp.scuola.util;

import it.keyorchestra.registrowebapp.R;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GraficiArrayAdapter extends ArrayAdapter<String> {
	private final Context _context;
	private final String[] _graficiArrayList;

	public GraficiArrayAdapter(Context context, String[] graficiArrayList) {
		super(context, R.layout.icon_row_layout, graficiArrayList);
		// TODO Auto-generated constructor stub
		this._context = context;
		this._graficiArrayList = graficiArrayList;
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

		Resources res = _context.getResources();

		textView.setText(_graficiArrayList[position]);
		textView.setTextColor(_context.getResources().getColor(
				R.color.colorOrange));
		textView.setBackgroundColor(_context.getResources().getColor(
				R.color.colorBlack));
		textView.setTextSize(12);
		// textView.setTag(s.getLong("id_ruolo"));
		if (_graficiArrayList[position].contains("Line")) {
			imageView
					.setImageDrawable(res.getDrawable(R.drawable.line_chart48));
		} else if (_graficiArrayList[position].contains("Bar")) {
			imageView.setImageDrawable(res.getDrawable(R.drawable.bar_chart48));
		} else
			imageView.setImageDrawable(res
					.getDrawable(R.drawable.graphview_logo48));

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

		Resources res = _context.getResources();

		textView.setText(_graficiArrayList[position]);
		textView.setTextColor(_context.getResources().getColor(
				R.color.colorOrange));
		textView.setBackgroundColor(_context.getResources().getColor(
				R.color.colorBlack));
		textView.setTextSize(12);
		// textView.setTag(s.getLong("id_ruolo"));
		if (_graficiArrayList[position].contains("Line")) {
			imageView
					.setImageDrawable(res.getDrawable(R.drawable.line_chart64));
		} else if (_graficiArrayList[position].contains("Bar")) {
			imageView.setImageDrawable(res.getDrawable(R.drawable.bar_chart64));
		} else
			imageView.setImageDrawable(res
					.getDrawable(R.drawable.graphview_logo64));

		return rowView;
	}

}
