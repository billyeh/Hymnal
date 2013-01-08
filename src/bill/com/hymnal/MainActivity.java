package bill.com.hymnal;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

	private String downloadHymn(String strUrl) throws IOException {
		InputStream iStream = null;
		if (Utility.inArray(fileList(), Utility.parseUrl(strUrl))){
			Log.d("IO", "File found in cache");
			iStream = openFileInput(Utility.parseUrl(strUrl));
		} else {
			iStream = Utility.openConnection(strUrl);
		}
		return convertStream(iStream);
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
			if (result == null){
				Toast.makeText(getBaseContext(), "Invalid song selection", Toast.LENGTH_SHORT).show();
			} else{
				try {
					jArray = new JSONArray(result);
					song = Utility.jsonToArrayList(jArray);
				} catch (JSONException e) {
					Toast.makeText(getBaseContext(), "Error downloading song", Toast.LENGTH_SHORT).show();
				}
				try {
					url = Utility.parseUrl(((EditText) findViewById(R.id.et_url)).getText().toString());
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
	
	boolean isNetworkAvailable() {
		boolean available = false;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isAvailable())
			available = true;
		return available;
	}
	
	public String convertStream(InputStream iStream) throws IOException{
		String line = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
		StringBuilder total = new StringBuilder();
		while ((line = r.readLine()) != null){
			total.append(line);
		}
		iStream.close();
		return total.toString();
	}

}
