package bill.com.hymnal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.SearchView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	private String downloadHymn(String url) throws IOException {
		InputStream iStream = null;
		if (Utility.inArray(fileList(), Utility.parseUrl(url))){
			Log.d("IO", "File found in cache");
			iStream = openFileInput(Utility.parseUrl(url));
		} else {
			iStream = Utility.openConnection(url);
		}
		return Utility.convertStream(iStream);
	}
	
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		String json = null;
		
		@Override
		protected String doInBackground(String... url) {
			try {
				json = downloadHymn(url[0]);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return json;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// Get ArrayList from JSONArray, start new activity to display hymn,
			// Save hymn if not there before
			ArrayList<String> song = null;
			JSONArray jArray = null;
			String url = null;
			
			if (result == null){
				Toast.makeText(getBaseContext(), "Invalid song selection", Toast.LENGTH_SHORT).show();
			} else {
				try {
					jArray = new JSONArray(result);
					song = Utility.jsonToArrayList(jArray);
				} catch (JSONException e) {
					Toast.makeText(getBaseContext(), "Error downloading song", Toast.LENGTH_SHORT).show();
				}
				
				url = Utility.parseUrl(getResources().getString(R.string.str_tv_url));
				
				if (!Utility.inArray(fileList(), url)) {
					//TODO Remove Logging
					Log.d("IO", "File not found in cache, saving " + url);
					Utility.saveBytes(getBaseContext(), url, result.getBytes());
				}
				
				// Start the new activity that shows the song
				Intent show_song = new Intent(getBaseContext(), HymnList.class);
				show_song.putExtra("song", song);
				startActivity(show_song);
			}
		}
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
						negative, positive);
			}
		}
	}

}
