package bill.com.hymnal;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.str_shared_prefs), Context.MODE_PRIVATE);
		String[] pairs = prefs.getString("recentHymns", "::").split("::");
		final String[] songs = new String[pairs.length];
		final String[] urls = new String[pairs.length];
		if (pairs.length > 0) {
			if (pairs[0] != "") {
				for (int i = 0; i < pairs.length; i ++) {
					Log.d("pairs", pairs[i]);
					songs[i] = pairs[i].split(":")[1];
					urls[i] = pairs[i].split(":")[0];
				}	
			}
		}
		Collections.reverse(Arrays.asList(songs));
		Collections.reverse(Arrays.asList(urls));
		setContentView(R.layout.activity_main);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, songs);
		ListView recentList = (ListView) findViewById(R.id.recentList);
		if (adapter != null && recentList != null) {
			recentList.setAdapter(adapter);
			recentList.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					String url = urls[position];
					DatabaseHandler.showSong(DatabaseHandler.getSong(url, getBaseContext()), getBaseContext());
				}
				});
		}
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		File dbPath = getExternalFilesDir(null);
		File database = new File(dbPath, getResources().getString(R.string.str_database_name));
		
		if (Utility.SDCardAvailable()) {
			//TODO Remove Logging
			Log.d("IO", "SD Card is available");
			if (!database.exists()) { // Prompt user to download hymn database
				//TODO Remove Logging
				Log.d("IO", "Database already downloaded");
				DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						DownloadDatabase.startDownload(MainActivity.this);
						Toast.makeText(MainActivity.this, "Download started", Toast.LENGTH_SHORT).show();
					}
				};
				DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {	
					public void onClick(DialogInterface dialog, int which) {
					}
				};
				
				Utility.showDialog(MainActivity.this, 
						getResources().getString(R.string.str_dialog_title), 
						getResources().getString(R.string.str_dialog_message), 
						positive, negative);
			}
		}
	}
	
	//////////////////////////////////
	///MENU CREATION AND MANAGEMENT///
	//////////////////////////////////
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
	    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
    
		return super.onCreateOptionsMenu(menu);
	}
}
