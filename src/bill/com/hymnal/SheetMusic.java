package bill.com.hymnal;

import java.io.FileInputStream;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SearchView;

public class SheetMusic extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_music);
		
		Intent music = getIntent();
		String url = music.getStringExtra("sheetMusic");
		/*
		ImageView image = (ImageView) findViewById(R.id.sheet_music);
		FileInputStream is = null;
		try {
			is = openFileInput(url);
		} catch(Exception e) {
			e.printStackTrace();
		}
		try {
			Bitmap sheet = BitmapFactory.decodeStream(is);
			image.setImageBitmap(sheet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
		WebView webview = (WebView) findViewById(R.id.wv_music);
		Log.d("Webview", "<body><img src=\"" + "file://" + getFilesDir().getAbsolutePath().toString() + "/" + url + "\" /></body>");
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.loadDataWithBaseURL("", "<body><img src=\"" + "file://" + getFilesDir().getAbsolutePath().toString() + "/" + url + "\" / ></body>", "text/html", "utf-8", null);
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
