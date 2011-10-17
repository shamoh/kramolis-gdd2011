package cz.kramolis.gdd2011.android;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Libor Kramolis
 * @todo
 */
public abstract class AboutActivity extends ListActivity {

	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.about);
		setTitle(R.string.about);

		addItems();
		SimpleAdapter notes = new SimpleAdapter(this, list, R.layout.list_item,
				new String[]{"title", "description", "link"},
				new int[]{R.id.text1, R.id.text2, R.id.text3});
		setListAdapter(notes);
		getListView().setTextFilterEnabled(true);
	}

	private void addItems() {
		{
			Map<String, String> item = new HashMap<String, String>();
			item.put("title", "About");
			item.put("description", "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.");
			item.put("link", "http://gdd2011.kramolis.cz");
			list.add(item);
		}
		{
			Map<String, String> item = new HashMap<String, String>();
			item.put("title", "Libor Kramolis");
			item.put("description", "TODO");
			item.put("link", "https://plus.google.com/115270016494231681069/about");
			list.add(item);
		}
		{
			Map<String, String> item = new HashMap<String, String>();
			item.put("title", "Ondrej Kosatka");
			item.put("description", "TODO");
			item.put("link", "https://plus.google.com/117246369712480977490/about");
			list.add(item);
		}
		{
			Map<String, String> item = new HashMap<String, String>();
			item.put("title", "Martin Mares");
			item.put("description", "TODO");
			item.put("link", "https://plus.google.com/117017068727290273829/about");
			list.add(item);
		}
		{
			Map<String, String> item = new HashMap<String, String>();
			item.put("title", "Petr Blazek");
			item.put("description", "TODO");
			item.put("link", "https://plus.google.com/100342760152037874082/about");
			list.add(item);
		}
	}

}
