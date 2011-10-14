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
 * @author Ondrej Kosatka
 */
public class QueueAdapter extends ArrayAdapter<PlayRequest> {

	private MainActivity mainActivity;
	private int resource;
//	private String response;

	//Initialize adapter
	public QueueAdapter(MainActivity mainActivity, int resource, List<PlayRequest> items) {
		super(mainActivity, resource, items);
		this.resource = resource;
	}

	public void addAll(List<PlayRequest> tweets) {
		for (PlayRequest tweet : tweets) {
			this.add(tweet);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout alertView;
		//Get the current alert object
		PlayRequest al = getItem(position);

		//Inflate the view
		if (convertView == null) {
			alertView = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, alertView, true);
		} else {
			alertView = (LinearLayout) convertView;
		}
		//Get the text boxes from the listitem.xml file
		TextView status = (TextView) alertView.findViewById(R.id.status);
		TextView username = (TextView) alertView.findViewById(R.id.username);
		TextView datetime = (TextView) alertView.findViewById(R.id.datetime);

		//Assign the appropriate data from our alert object above
		status.setText(al.getText());
		username.setText("@" + al.getAuthor());
		datetime.setText(al.getCreatedAt().toLocaleString());

		alertView.setOnCreateContextMenuListener(mainActivity);

		return alertView;
	}

}
