package bill.com.hymnal;

import java.io.File;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

public class DatabaseHandler extends Activity {
	public static SQLiteDatabase openDatabase(Context context) {
		File dbPath = context.getExternalFilesDir(null);
		File database = new File(dbPath, context.getResources().getString(R.string.str_database_name));
		return SQLiteDatabase.openDatabase(database.getAbsolutePath(), null, 0);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			String query = intent.getStringExtra(SearchManager.QUERY);
			final Cursor cursor = queryDatabase(query);
			
			if (cursor.moveToFirst()) {
				String [] results = cursorToArray(cursor);
				ArrayAdapter<String> searchResults = new ArrayAdapter<String>(this, R.layout.list_item, results);
				ListView resultsList = (ListView) findViewById(R.id.resultsList);
				
				resultsList.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						
						JSONArray jArray = new JSONArray();
						ArrayList<String> song = null;
						String sheetMusic = cursor.getString(2);
						
						cursor.moveToPosition(position);
						
						try {
							jArray = new JSONArray(getSong(cursor.getString(0)));
							song = Utility.jsonToArrayList(jArray);
						} catch (JSONException e) {
							Toast.makeText(getBaseContext(), "Error retrieving song", Toast.LENGTH_SHORT).show();
						}
						finally {
							cursor.close();
						}
						
						if (song != null) {
							Intent show_song = new Intent(getBaseContext(), HymnList.class);
							show_song.putExtra("song", song);
							show_song.putExtra("sheetMusic", sheetMusic);
							startActivity(show_song);
						}
						else {
							Toast.makeText(getBaseContext(), "Error retrieving song", Toast.LENGTH_SHORT).show();
						}
					}
				});
				
				resultsList.setAdapter(searchResults);
			}
			else {
				TextView noResults = (TextView) findViewById(R.id.noResults);
				noResults.setText(getResources().getString(R.string.str_no_results));
			}
		}
	}
	
	public Cursor queryDatabase(String query) {
		Cursor cursor = null;
		
		SQLiteDatabase db = openDatabase(DatabaseHandler.this);
		cursor = db.rawQuery("SELECT url, SNIPPET(songdb, '', '', '...'), sheet_music FROM songdb WHERE song MATCH ?", new String [] {query});
		return cursor;
	}
	
	public String getSong(String url) {
		Cursor cursor = null;
		
		SQLiteDatabase db = openDatabase(DatabaseHandler.this);
		cursor = db.rawQuery("SELECT song, sheet_music FROM songdb WHERE url = ?", new String [] {url});
		try {
			cursor.moveToFirst();
			return cursor.getString(0);
		}
		finally {
			cursor.close();
		}
	}
	
	public String[] cursorToArray(Cursor cursor) {
		String [] result = new String[cursor.getCount()];
		for (int i=0; i < cursor.getCount(); i++) {
			result[i] = cursor.getString(1).replace("\\n", " / ").replace("[", "").replace("]", "");
			cursor.moveToNext();
		}
		return result;
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
