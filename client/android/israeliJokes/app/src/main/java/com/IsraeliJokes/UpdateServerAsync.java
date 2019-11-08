package com.IsraeliJokes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class UpdateServerAsync extends AsyncTask<String, Void, String> {
	public final static String BUNDLE_RESULT = "BUNDLE_RESULT";
	Handler m_Handler ;
	public boolean m_ComunicationFailure = false;
	private String TAG = "IsrealiJokes::UpdateServerAsync";
	UpdateServerAsync(Handler a_Handler)
	{
		m_Handler = a_Handler;
	}
	public static final String MESSAGE_STRING = "MESSAGE_STRING"; 
 	@Override
    protected String doInBackground(String... params) {
 		m_ComunicationFailure = false;
        return RetrieveFromServer( params[0] );
    }
 	@Override
 	protected void onPostExecute(String result) {
 		super.onPostExecute(result);
 		if (m_Handler != null)
 		{
 			Bundle bundle = new Bundle();
 			bundle.putString(BUNDLE_RESULT, result);
 			Message msg = new Message();
 			msg.setData(bundle);
 			m_Handler.sendMessage(msg);
 		}
 	}
 	
 	public String RetrieveFromServer( String url )
	{
		try
		{
			String line = null;
			StringBuilder builder = null;
			HttpResponse response = null;
			Log.d(TAG, "retrieving from: " + url);
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			
			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			int timeoutConnection = 5000;
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			
			// Set the default socket timeout (SO_TIMEOUT) 
			// in milliseconds which is the timeout for waiting for data.
			int timeoutSocket = 5000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			
			HttpClient client = new DefaultHttpClient(httpParameters);
			response = client.execute( request );
			
			BufferedReader bufferReader = 
				   new BufferedReader ( new InputStreamReader(response.getEntity().getContent()),8192 );
			builder = new StringBuilder();
			while((line = bufferReader.readLine()) != null)
			{
				builder.append(line);
			}
			bufferReader.close();
			Log.d(TAG, "RetrieveFromServer:" + builder.toString());
			String strFromServer = builder.toString();
			m_ComunicationFailure = false;
			return  strFromServer;
		}catch(URISyntaxException e){
		   Log.e(TAG," URISyntaxException");
		  // pd.dismiss();
		   m_ComunicationFailure = true;
		   return null;
		}catch(IOException e){
		   Log.e(TAG ,"IOException. jason or http");
		   Log.e(TAG ,e.toString());
		   m_ComunicationFailure = true;
//		   Log.e(TAG,e.getMessage());
		 //  pd.dismiss();
		   return null;
			}catch(IllegalStateException e){
				m_ComunicationFailure = true;
				Log.e(TAG,"Http IllegalStateException");
		//  pd.dismiss();
				return null;
		}
	}
}

