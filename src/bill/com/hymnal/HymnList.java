package bill.com.hymnal;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

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
		
		songList.setSelector(android.R.color.transparent); // No dividers between song verses
		songList.setAdapter(adapter);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
	    SearchManager searchManager =
	            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	             (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSearchableInfo(
	             searchManager.getSearchableInfo(getComponentName()));
		
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		else {
			return super.onOptionsItemSelected(item);
		}
	}
	
}