package bill.com.hymnal;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

/* 
 * Many utility classes dealing with the Android environment. Highly reusable code.
 * 
 * What's in here?
 * 		- Array-parsing methods
 * 		- Url-parsing method
 * 		- HTTP downloading method 
 * 		- Displaying an AlertDialog
 * 		- Turning an InputStream into String
 * 		- Check if SD card mounted
*/

public class Utility {
	
	// Methods for parsing arrays
	public static ArrayList<String> jsonToArrayList(JSONArray json) throws JSONException {
		ArrayList<String> songArray = new ArrayList<String>();
		if (json != null) {
			for (int i = 0; i < json.length(); i ++) {
				songArray.add(json.get(i).toString());
			}
		}
		return songArray;
	}
	
	public static boolean inArray(String [] arr, String val) {
		int i = 0;
		while (i < arr.length){
			if (arr[i].equals(val)){
				return true;
			}
			i++;
		}
		return false;
	}
	
	// Extracts the relevant song number and type from an http URL.
	// http://hymn.aws.af.cm/hymn?hymn=5&type=ns --> hymn5typens 
	public static String parseUrl(String url ) {
        String [] split = url.split("/");
        url = split[split.length - 1];
        return url;
	}
	
	// Check if network is available, then
	// Open an HTTP connection and return the inputStream
	public static boolean isNetworkAvailable(Context context) {
		boolean available = false;
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isAvailable())
			available = true;
		return available;
	}
	
	public static InputStream openConnection(String strUrl) {
		//TODO Remove Logging
		Log.d("IO", "Opening HTTP connection");
		InputStream iStream = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();
		} catch(Exception e) {
			//TODO Remove Logging
			Log.d("Exception while downloading url", e.toString());
		}
		return iStream;
	}
	
	// Pops up a dialog box with strings
	public static void showDialog(final Context context, String title, String message, 
			DialogInterface.OnClickListener positive, DialogInterface.OnClickListener negative) {
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle(title);
		alert.setMessage(message);
		alert.setPositiveButton("OK", positive);
		alert.setNegativeButton("Not now", negative);
		AlertDialog dialog = alert.create();
		dialog.show();
	}
	
	// Given InputStream, returns Bitmap
	public static Bitmap convertStream(InputStream iStream) throws IOException {
		return BitmapFactory.decodeStream(iStream);
	}
	
	// Check if SD card mounted
	public static boolean SDCardAvailable() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}
	
	//Returns whether string is Integer or not
	public static boolean isInt(String num) {
		try {
			Integer.parseInt(num);
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}
	
	public static String[] cursorToArray(Cursor cursor) {
		String [] result = new String[cursor.getCount()];
		for (int i=0; i < cursor.getCount(); i++) {
			result[i] = cursor.getString(1).replace("\\n", " / ").replace("[", "").replace("]", "");
			cursor.moveToNext();
		}
		return result;
	}

}
