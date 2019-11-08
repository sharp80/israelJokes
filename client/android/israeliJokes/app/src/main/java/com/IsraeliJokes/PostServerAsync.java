package com.IsraeliJokes;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class PostServerAsync extends AsyncTask<String, Void, String> {
	private List<NameValuePair> m_Pairs;
	private Handler m_FinishedPostHandler;
	private String TAG = "PostServerAsync";
	public PostServerAsync(	List<NameValuePair> params, 
							Handler finishedPosting)
	{
		m_Pairs = params;
		m_FinishedPostHandler = finishedPosting;
	}
 	@Override
    protected String doInBackground(String... url) {
 		
		return postPage(url[0], m_Pairs);
 	}
 	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Message msg = new Message();
		if (result == null )
		{
			msg.arg1 = -1;
		}
		else
		{
			if ( result.contains("OK") )
			{
				msg.arg1 = 0;
			}
			else
			{
				msg.arg1 = -1;
			}
		}
		if (m_FinishedPostHandler != null)
		{
			m_FinishedPostHandler.sendMessage( msg );
		}
	}
 	

	public String postPage(String url, List<NameValuePair> params) 
	{
		HttpClient httpclient = new DefaultHttpClient();
	
	
		String ret = null;
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response = null;
	
		httpPost.setHeader("User-Agent", "Mozilla/5.0 (X11; U; Linux " +
			"i686; en-US; rv:1.8.1.6) Gecko/20061201 Firefox/2.0.0.6 (Ubuntu-feisty)");
		httpPost.setHeader("Accept", "text/html,application/xml," +
			"application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
	
		
	    UrlEncodedFormEntity ent;
		try {
			ent = new UrlEncodedFormEntity(params,HTTP.UTF_8);
			httpPost.setEntity(ent);
			try {
				for (int retry = 0 ; retry < 3 && response == null ; retry++ )
				{
					HttpContext localContext = new BasicHttpContext();
					response = httpclient.execute(httpPost, localContext);
				}
	    	} catch (ClientProtocolException e) {
	    		Log.e(TAG,"HTTPHelp : ClientProtocolException : "+e.toString());
	    	} catch (IOException e) {
	    		Log.e(TAG, "HTTPHelp : IOException : " + e.toString());
	    	} 
			if (response != null)
			{
				ret = response.getStatusLine().toString();
			}
			else
			{
				ret = null;
			}
			try {
        	   String responseBody = EntityUtils.toString(response.getEntity());
        	   Log.d(TAG,responseBody );
			} catch (IllegalStateException e) {
				Log.e(TAG, e.toString());
			} catch (IOException e) {
				Log.e(TAG, e.toString());
			}
	             
		} catch (Exception e1) {
			Log.e(TAG, e1.toString());
		}
		   return ret;
	}
}
	    