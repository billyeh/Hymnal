package bill.com.hymnal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        final ArrayAdapter<String> adapter;
        final ArrayList<String> songsArray = new ArrayList<String>();
        final ArrayList<String> urlsArray = new ArrayList<String>();
        Log.d("HYMNAL", "Starting new");
		super.onCreate(savedInstanceState);
		try {
            SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.str_shared_prefs), Context.MODE_PRIVATE);
            String[] pairs = prefs.getString("recentHymns", "::").split("::");

            for (int i = 0; i < pairs.length; i++) {
                if (pairs[i].equals("")) {
                    continue;
                }
                songsArray.add(pairs[i].split(":")[1]);
                urlsArray.add(pairs[i].split(":")[0]);
            }
            Collections.reverse(songsArray);
            Collections.reverse(urlsArray);
            setContentView(R.layout.activity_main);
        }
		catch (Exception e) {
            Log.d("HYMNAL", e.toString());
            for (StackTraceElement ste : e.getStackTrace()) {
                Log.d("HYMNAL", ste.toString());
            }
			clearSharedPrefs();
		}
        finally {
            adapter = new ArrayAdapter<String>(this, R.layout.list_item,
                    songsArray);
            ListView recentList = (ListView) findViewById(R.id.recentList);
            if (adapter != null && recentList != null) {
                recentList.setAdapter(adapter);
                recentList.setOnItemClickListener(new OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String url = urlsArray.get(position);
                        DatabaseHandler.showSong(DatabaseHandler.getSong(url, getBaseContext()),
                                getBaseContext());
                    }
                });
            }
            Button clear = (Button) findViewById(R.id.clearRecents);
            clear.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    clearSharedPrefs();
                    if (null != adapter) {
                        adapter.clear();
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
	}

    private void clearSharedPrefs() {
        SharedPreferences prefs = getSharedPreferences(getResources().getString(R.string.str_shared_prefs), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("recentHymns", ""); //error loading recent songs, just clear them :)
        editor.commit();
    }
	
	@Override
	public void onStart() {
		super.onStart();
		File dbPath = getExternalFilesDir(null);
		File database = new File(dbPath, getResources().getString(R.string.str_database_name));

		if (Utility.SDCardAvailable()) {
			Log.d("IO", "SD Card is available");
			if (!database.exists()) { // Prompt user to download hymn database
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
