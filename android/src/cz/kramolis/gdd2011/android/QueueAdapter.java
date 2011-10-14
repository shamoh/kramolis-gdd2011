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
		LinearLayout layout;
		//Get the current alert object
		PlayRequest playRequest = getItem(position);

		//Inflate the view
		if (convertView == null) {
			layout = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater vi;
			vi = (LayoutInflater) getContext().getSystemService(inflater);
			vi.inflate(resource, layout, true);
		} else {
			layout = (LinearLayout) convertView;
		}
		//Get the text boxes from the listitem.xml file
		TextView id = (TextView) layout.findViewById(R.id.id);
		TextView status = (TextView) layout.findViewById(R.id.status);
		TextView username = (TextView) layout.findViewById(R.id.username);
		TextView datetime = (TextView) layout.findViewById(R.id.datetime);

		//Assign the appropriate data from our alert object above
		id.setText(String.valueOf(playRequest.getId()));
		status.setText(playRequest.getText());
		username.setText("@" + playRequest.getAuthor());
		datetime.setText(playRequest.getCreatedAt().toLocaleString());

		layout.setOnCreateContextMenuListener(mainActivity);

		return layout;
	}

}
