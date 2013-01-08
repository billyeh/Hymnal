package bill.com.hymnal;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btnDownload = (Button) findViewById(R.id.btn_download);
		btnDownload.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (Utility.isNetworkAvailable(getBaseContext())) {
					EditText etUrl = (EditText) findViewById(R.id.et_url);
					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(etUrl.getText().toString());
				} else {
					Toast.makeText(getBaseContext(), "Network is not available", Toast.LENGTH_SHORT).show();
				}
			}
		}
		);
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
				
				url = Utility.parseUrl(((EditText) findViewById(R.id.et_url)).getText().toString());
				
				if (!Utility.inArray(fileList(), url)) {
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
		return true;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		File dbPath = getExternalFilesDir(null);
		File database = new File(dbPath, getResources().getString(R.string.str_database_name));
		
		if (!database.exists()) {
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
