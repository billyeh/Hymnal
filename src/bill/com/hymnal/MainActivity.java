package bill.com.hymnal;

import java.io.File;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
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
		
	    SearchManager searchManager =
	            (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    SearchView searchView =
	             (SearchView) menu.findItem(R.id.menu_search).getActionView();
	    searchView.setSearchableInfo(
	             searchManager.getSearchableInfo(getComponentName()));
		
		return super.onCreateOptionsMenu(menu);
	}

}
