package bill.com.hymnal;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class HymnList extends ListActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent song = getIntent();
		ArrayList<String> songArrayList = song.getStringArrayListExtra("song");
		String [] songArray = songArrayList.toArray(new String[songArrayList.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, songArray);
		this.setListAdapter(adapter);
	}
}
