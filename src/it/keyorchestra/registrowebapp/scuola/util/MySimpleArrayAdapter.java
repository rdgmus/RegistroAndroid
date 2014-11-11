package it.keyorchestra.registrowebapp.scuola.util;

import java.util.ArrayList;

import it.keyorchestra.registrowebapp.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final ArrayList values;

	public MySimpleArrayAdapter(Context context, ArrayList arrayListOfDatabases) {
		super(context, R.layout.icon_row_layout, arrayListOfDatabases);
		this.context = context;
		this.values = arrayListOfDatabases;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.icon_row_layout, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.tvMyText);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.ivMyIcon);
		textView.setText((CharSequence) values.get(position));
		// Change the icon for Windows and iPhone
		String s = (String) values.get(position);
		// if (s.startsWith("Windows7") || s.startsWith("iPhone")
		// || s.startsWith("Solaris")) {
		// imageView.setImageResource(R.drawable.no);
		// } else {
		// imageView.setImageResource(R.drawable.ok);
		// }

		return rowView;
	}
}
