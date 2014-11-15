package it.keyorchestra.registrowebapp.mysqlandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ParseException;
import android.widget.Toast;

public class MySqlAndroid {

//	private CloseableHttpClient createHttpClient() {
//		CloseableHttpClient httpClient;
//		CommonHelperFunctions helperFunctions = new CommonHelperFunctions();
//		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
//		cm.setMaxTotal(306);
//		cm.setDefaultMaxPerRoute(108);
//		RequestConfig requestConfig = RequestConfig.custom()
//				.setConnectTimeout(15000).setSocketTimeout(15000).build();
//		httpClient = HttpClients.custom().setConnectionManager(cm)
//				.setDefaultRequestConfig(requestConfig).build();
//		return httpClient;
//	}

	public ArrayList<String> mysqlAndroidTest(final Context context) {
		JSONArray jArray = null;

		String result = null;

		StringBuilder sb = null;

		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// Why to use 10.0.2.2 http://localhost/mySqlAndroidTest.php
			HttpPost httppost = new HttpPost(
					"http://192.168.0.215/PhpMySqlAndroid/mySqlAndroidTest.php");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (IOException e) {
			Toast.makeText(context,
					"Error in http connection: " + e.toString(),
					Toast.LENGTH_LONG).show();
			// Log.e("log_tag", "Error in http connection"+e.toString());
			return null;
		}
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			sb = new StringBuilder();
			sb.append(reader.readLine() + "\n");

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Toast.makeText(context, "Error converting result: " + e.toString(),
					Toast.LENGTH_SHORT).show();
			// Log.e("log_tag", "Error converting result "+e.toString());
		}

		String ruolo;
		ArrayList<String> ruoliArray = new ArrayList<String>();
		try {
			jArray = new JSONArray(result);
			JSONObject json_data = null;
			for (int i = 0; i < jArray.length(); i++) {
				json_data = jArray.getJSONObject(i);
				ruolo = json_data.getString("ruolo");// here "ruolo" is
														// the column
														// name in
														// database
				Toast.makeText(context, "Ruolo: " + ruolo, Toast.LENGTH_SHORT)
						.show();
				ruoliArray.add(ruolo);
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		return ruoliArray;
	}

}
