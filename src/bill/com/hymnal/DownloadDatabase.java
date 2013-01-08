package bill.com.hymnal;

import android.app.DownloadManager;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Environment;

public class DownloadDatabase {
	
	static Resources res;
	public static void startDownload(Context context) {
		res = context.getResources();
		
		String url = res.getString(R.string.str_database_url);
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
		
		request.setTitle(res.getString(R.string.str_download_title));
		request.setDescription(res.getString(R.string.str_download_description));
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, 
				res.getString(R.string.str_database_name));
		
		DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

}
