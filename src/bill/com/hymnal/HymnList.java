package bill.com.hymnal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

public class HymnList extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.list);
        
		Intent song = getIntent();
		String[] songArray = song.getStringArrayExtra("song");
		String title = song.getStringExtra("title");
		String sheetMusic = song.getStringExtra("sheetMusic");
		Song songObj = Utility.reformatChorus(title, songArray, sheetMusic);
		songArray = songObj.getSongArray();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, songArray);
		ListView songList = (ListView) findViewById(R.id.songList);
		songList.setSelector(android.R.color.transparent); // No dividers between song verses
		songList.setAdapter(adapter);

		TextView titleText = (TextView) findViewById(R.id.songTitle);
		titleText.setText(Utility.expandUrl(title));
		
		SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.str_shared_prefs), Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
        String firstLine = title + ":" + songArray[0].split("\n")[0].replace("1 ", "");
		if (!prefs.getString("recentHymns", "").contains(firstLine)) {
			editor.putString("recentHymns", prefs.getString("recentHymns", "") + "::" + firstLine);
		}
		else {
            List<String> firstLines = new ArrayList<String>(Arrays.asList(prefs.getString("recentHymns", "").split("::")));
            firstLines.remove(firstLine);
            firstLines.add(firstLine);
            editor.putString("recentHymns", TextUtils.join("::", firstLines));
		}
		editor.commit();
		
		ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
	}
	
	private Bitmap downloadSheetMusic(String url) throws IOException {
		InputStream iStream;
		if (Utility.inArray(fileList(), Utility.parseUrl(url))){
			Log.d("IO", "File found in cache");
			iStream = openFileInput(Utility.parseUrl(url));
		} else {
			iStream = Utility.openConnection(url);
		}
		return Utility.convertStream(iStream);
	}
	
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		Bitmap sheetMusicBitmap = null;
		
		@Override
		protected String doInBackground(String... url) {
			try {
				sheetMusicBitmap = downloadSheetMusic(url[0]);
				FileOutputStream out;
				out = openFileOutput(Utility.parseUrl(url[0]), Context.MODE_PRIVATE);
				sheetMusicBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return Utility.parseUrl(url[0]);
		}
		
		@Override
		protected void onPostExecute(String result) {
			Intent sheet_music = new Intent(getBaseContext(), SheetMusic.class);
			sheet_music.putExtra("sheetMusic", result);
			startActivity(sheet_music);
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
	    /* URLs for sheet music don't work anymore :(
		Intent song = getIntent();
		final String sheetMusic = song.getStringExtra("sheetMusic");

		if (sheetMusic != null && !sheetMusic.isEmpty()) {
			MenuItem sheetMusicItem = menu.add("Get Sheet Music");
			
			sheetMusicItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(sheetMusic);
					return true;
				}
			});
		}
		*/
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