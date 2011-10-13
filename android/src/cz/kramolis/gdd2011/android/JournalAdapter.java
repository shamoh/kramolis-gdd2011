package cz.kramolis.gdd2011.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * @author Libor Kramolis
 */
public class JournalAdapter extends ArrayAdapter<LaPardonApplication.JournalItem> {

	int resource;
	Context context;

	//Initialize adapter
	public JournalAdapter(Context context, int resource, List<LaPardonApplication.JournalItem> items) {
		super(context, resource, items);
		this.resource = resource;

	}

	public void addAll(List<LaPardonApplication.JournalItem> items) {
		for (LaPardonApplication.JournalItem item : items) {
			this.add(item);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout rowView;
		//Get the current alert object
		LaPardonApplication.JournalItem item = getItem(position);

		//Inflate the view
		if (convertView == null) {
			rowView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, rowView, true);
		} else {
			rowView = (LinearLayout) convertView;
		}
		TextView type = (TextView) rowView.findViewById(R.id.type);
		TextView text = (TextView) rowView.findViewById(R.id.text);
		TextView createdAt = (TextView) rowView.findViewById(R.id.createdAt);

		type.setText(item.getType().getDisplayText());
		text.setText("@" + item.getText());
		createdAt.setText(item.getCreatedAt().toLocaleString());

		return rowView;
	}

}
