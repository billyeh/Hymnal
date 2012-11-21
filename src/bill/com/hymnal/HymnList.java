package bill.com.hymnal;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HymnList extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
		Intent song = getIntent();
		ArrayList<String> songArrayList = song.getStringArrayListExtra("song");
		String [] songArray = songArrayList.toArray(new String[songArrayList.size()]);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, songArray);
		ListView songList = (ListView) findViewById(R.id.songList);
		songList.setSelector(android.R.color.transparent);
		songList.setAdapter(adapter);
	}
}