package it.keyorchestra.registrowebapp.mysqlandroid;

import it.keyorchestra.registrowebapp.interfaces.ActivitiesCommonFunctions.NewPasswordRequestState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

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

/**
 * La classe si occupa della comunicazione tra Android e MySql tramite files PHP
 * depositati nel folder htdocs del server Apache di XAMPP
 * 
 * @author rdgmus
 * 
 */
public class MySqlAndroid {

	private InputStream getInputStreamFromUri(Context context, String uri) {

		InputStream is = null;

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

		// http post
		try {
			HttpClient httpclient = new DefaultHttpClient();

			// Why to use 10.0.2.2 http://localhost/mySqlAndroidTest.php
			HttpPost httppost = new HttpPost(uri);
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			HttpEntity entity = response.getEntity();
			is = entity.getContent();
		} catch (IOException e) {
			Toast.makeText(context,
					"Error in http connection: " + e.toString(),
					Toast.LENGTH_LONG).show();
			// Log.e("log_tag", "Error in http connection"+e.toString());
		}
		return is;
	}

	private String getResultFromInputStream(Context context, InputStream is) {
		String result = null;
		// convert response to string
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			sb.append(reader.readLine());

			String line = "0";
			while ((line = reader.readLine()) != null) {
				sb.append("\n" + line);
			}
			is.close();
			result = sb.toString();
		} catch (Exception e) {
			Toast.makeText(context, "Error converting result: " + e.toString(),
					Toast.LENGTH_SHORT).show();
			// Log.e("log_tag", "Error converting result "+e.toString());
		}
		return result;
	}

	public ArrayList<String> mysqlAndroidTest(Context context, String uri) {

		InputStream is = null;
		is = getInputStreamFromUri(context, uri);
		if (is == null) {
			return null;
		}
		String result = null;
		result = getResultFromInputStream(context, is);

		JSONArray jArray = null;

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

	public String getEncodedStringFromUri(Context context, String uri) {
		InputStream is = null;
		is = getInputStreamFromUri(context, uri);

		String result = null;
		result = getResultFromInputStream(context, is);

		return result;
	}

	/**
	 * Carica i dati di una tabella
	 * 
	 * @param context
	 * @param uri
	 * @param tableName
	 * @return
	 */
	public JSONArray retrieveTableData(Context context, String uri,
			String tableName) {
		InputStream is = null;
		is = getInputStreamFromUri(context, uri);
		if (is == null) {
			return null;
		}
		String result = null;
		result = getResultFromInputStream(context, is);

		JSONArray jArray = null;

		try {
			jArray = new JSONArray(result);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		return jArray;
	}

	public NewPasswordRequestState SendRequestChangePassword(
			Context applicationContext, String uri) {
		// TODO Auto-generated method stub
		InputStream is = null;
		is = getInputStreamFromUri(applicationContext, uri);

		int result=-1;
		result = Integer.valueOf(getResultFromInputStream(applicationContext, is)).intValue();

		switch (result){
		case 0:
			return NewPasswordRequestState.REQUEST_ABORTED;
		case 1:
			return NewPasswordRequestState.REQUEST_SUCCESS;
		case 2:
			return NewPasswordRequestState.REQUEST_EXISTS;
		}
		return NewPasswordRequestState.NONE;
	}

	public JSONArray getDailyConnectionAsJSON(Context applicationContext,
			String uri) {
		// TODO Auto-generated method stub
		InputStream is = null;
		is = getInputStreamFromUri(applicationContext, uri);
		if (is == null) {
			return null;
		}
		String result = null;
		result = getResultFromInputStream(applicationContext, is);

		JSONArray jArray = null;

		try {
			jArray = new JSONArray(result);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		return jArray;
	}

	public JSONArray getMontlyConnectionAsJSON(Context applicationContext,
			String uri) {
		// TODO Auto-generated method stub
		InputStream is = null;
		is = getInputStreamFromUri(applicationContext, uri);
		if (is == null) {
			return null;
		}
		String result = null;
		result = getResultFromInputStream(applicationContext, is);

		JSONArray jArray = null;

		try {
			jArray = new JSONArray(result);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (ParseException e1) {
			e1.printStackTrace();
		}

		return jArray;
	}
}
