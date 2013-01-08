package bill.com.hymnal;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
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
		
		if (true) {
			DialogInterface.OnClickListener positive = new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					DownloadDatabase.startDownload(MainActivity.this);
					Toast.makeText(MainActivity.this, "Download started", Toast.LENGTH_SHORT).show();
				}
			};
			DialogInterface.OnClickListener negative = new DialogInterface.OnClickListener() {	
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(MainActivity.this, "Download not started", Toast.LENGTH_SHORT).show();
				}
			};
			Utility.showDialog(MainActivity.this, 
					getResources().getString(R.string.str_dialog_title), 
					getResources().getString(R.string.str_dialog_message), 
					positive, negative);
		}
		
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

	private String downloadHymn(String strUrl) throws IOException {
		InputStream iStream = null;
		if (Utility.inArray(fileList(), Utility.parseUrl(strUrl))){
			Log.d("IO", "File found in cache");
			iStream = openFileInput(Utility.parseUrl(strUrl));
		} else {
			iStream = Utility.openConnection(strUrl);
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
				Log.d("Background task", e.toString());
			}
			return json;
		}
		@Override
		protected void onPostExecute(String result) {
			ArrayList<String> song = null;
			JSONArray jArray = null;
			FileOutputStream songSaver = null;
			String url = null;
			
			// Catches the case where the file is not retrieved from the URL
			if (result == null){
				Toast.makeText(getBaseContext(), "Invalid song selection", Toast.LENGTH_SHORT).show();
			} else {
				
				// Catches errors in JSON parsing
				try {
					jArray = new JSONArray(result);
					song = Utility.jsonToArrayList(jArray);
				} catch (JSONException e) {
					Toast.makeText(getBaseContext(), "Error downloading song", Toast.LENGTH_SHORT).show();
				}
				
				url = Utility.parseUrl(((EditText) findViewById(R.id.et_url)).getText().toString());
				// Catches errors with saving the file to internal storage
				try {
					songSaver = openFileOutput(url, Context.MODE_PRIVATE);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					songSaver.write(result.getBytes());
					songSaver.close();
				} catch (IOException e) {
					e.printStackTrace();
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

}
