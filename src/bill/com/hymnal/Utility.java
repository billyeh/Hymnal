package bill.com.hymnal;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;

public class Utility {
	public static ArrayList<String> jsonToArrayList(JSONArray json) throws JSONException {
		ArrayList<String> songArray = new ArrayList<String>();
		if (json != null) {
			for (int i = 0; i < json.length(); i ++) {
				songArray.add(json.get(i).toString());
			}
		}
		return songArray;
	}
	
	public static String parseUrl(String url){
        String [] split = url.split("/");
        url = split[split.length - 1];
        split = url.split("=");
        String hymnType = split[2];
        String hymnNumber = split[1].split("&")[0];
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
		return false;
	}
	public static InputStream openConnection(String strUrl){
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
}
