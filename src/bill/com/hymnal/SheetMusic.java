package bill.com.hymnal;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.SearchView;

public class SheetMusic extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sheet_music);
		
		Intent music = getIntent();
		String url = music.getStringExtra("sheetMusic");
		WebView webview = (WebView) findViewById(R.id.wv_music);
		webview.getSettings().setAllowFileAccess(true);
		webview.getSettings().setBuiltInZoomControls(true);
		webview.loadDataWithBaseURL("", "<body><img src=\"" + "file://" + getFilesDir().getAbsolutePath().toString() + "/" + url + "\" / ></body>", "text/html", "utf-8", null);
		webview.getSettings().setUseWideViewPort(true);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
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
