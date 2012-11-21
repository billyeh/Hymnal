package bill.com.hymnal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
		
		OnClickListener downloadListener = new OnClickListener() {
			public void onClick(View v) {
				if (isNetworkAvailable()) {
					EditText etUrl = (EditText) findViewById(R.id.et_url);
					DownloadTask downloadTask = new DownloadTask();
					downloadTask.execute(etUrl.getText().toString());
				} else {
					Toast.makeText(getBaseContext(), "Network is not available", Toast.LENGTH_SHORT).show();
				}
			}
		};
		btnDownload.setOnClickListener(downloadListener);
	}

	private String downloadUrl(String strUrl) throws IOException {
		InputStream iStream = null;
		String song = null;
		if (inArray(fileList(), parseUrl(strUrl))){
			Log.d("IO", "File found in cache");
			iStream = openFileInput(parseUrl(strUrl));
		}
		try {
			URL url = new URL(strUrl);
			StringBuilder total = new StringBuilder();
			String line = null;

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();
			BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
			song = total.toString();
		} catch(Exception e) {
			Log.d("Exception while downloading url", e.toString());
		} finally {
			iStream.close();
		}
		return song;
	}
	
	private class DownloadTask extends AsyncTask<String, Integer, String> {
		String json = null;
		@Override
		protected String doInBackground(String... url) {
			try {
				json = downloadUrl(url[0]);
			} catch(Exception e) {
				Log.d("Background task", e.toString());
			}
			return json;
		}
		@Override
		protected void onPostExecute(String result) {
			ArrayList<String> stanza = null;
			JSONArray jArray = null;
			FileOutputStream songSaver = null;
			String url = null;
			try {
				jArray = new JSONArray(result);
				stanza = jsonToArrayList(jArray);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Toast.makeText(getBaseContext(), "Song downloaded succesfully", Toast.LENGTH_SHORT).show();
			try {
				url = parseUrl(((EditText) findViewById(R.id.et_url)).getText().toString());
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
			Intent show_song = new Intent(getBaseContext(), HymnList.class);
			show_song.putExtra("song", stanza);
			startActivity(show_song);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public static ArrayList<String> jsonToArrayList(JSONArray json) throws JSONException {
		ArrayList<String> songArray = new ArrayList<String>();
		if (json != null) {
			for (int i = 0; i < json.length(); i ++) {
				songArray.add(json.get(i).toString());
			}
		}
		return songArray;
	}
	
	private boolean isNetworkAvailable() {
		boolean available = false;
		
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		if(networkInfo != null && networkInfo.isAvailable())
			available = true;
		return available;
	}
	
	public static String parseUrl(String url){
        String [] split = url.split("/");
        url = split[split.length - 1];
        split = url.split("=");
        String hymnType = split[2];
        char hymnNumber = split[1].charAt(0);
        return "hymn" + hymnNumber + "type" + hymnType;
	}

	public static boolean inArray(String [] arr, String val){
		int i = 0;
		while (i < arr.length){
			if (arr[i].equals(val)){
				return true;
			}
			i++;
		}
		return false;}
}
