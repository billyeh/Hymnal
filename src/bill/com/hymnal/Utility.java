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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/* Many utility classes dealing with the Android environment. Highly reusable code.
 * 
 * What's in here?
 * 		- Array-parsing methods
 * 		- Url-parsing method
 * 		- HTTP downloading method 
 * 		- Displaying an AlertDialog
 * 		- Turning an InputStream into String*/

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
	public static String parseUrl(String url ){
        String [] split = url.split("/");
        url = split[split.length - 1];
        split = url.split("=");
        String hymnType = split[2];
        String hymnNumber = split[1].split("&")[0];
        return "hymn" + hymnNumber + "type" + hymnType;
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
		Log.d("IO", "Opening HTTP connection");
		InputStream iStream = null;
		try {
			URL url = new URL(strUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.connect();
			iStream = urlConnection.getInputStream();
		} catch(Exception e) {
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
	
	// Given InputStream, returns String
	public static String convertStream(InputStream iStream) throws IOException {
		String line = null;
		BufferedReader r = new BufferedReader(new InputStreamReader(iStream));
		StringBuilder total = new StringBuilder();
		while ((line = r.readLine()) != null) {
			total.append(line);
		}
		iStream.close();
		return total.toString();
	}
	
	// Given filename, bytes, save file in memory
	public static void saveBytes(Context context, String filename, byte[] bs) {
		FileOutputStream saver = null;
		try {
			saver = context.openFileOutput(filename, Context.MODE_PRIVATE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			saver.write(bs);
			saver.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
