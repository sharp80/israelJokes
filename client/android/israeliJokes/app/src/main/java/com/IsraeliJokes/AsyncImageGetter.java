package com.IsraeliJokes;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;

public class AsyncImageGetter extends AsyncTask<String, Void, Bitmap> {
	private String TAG = "AsyncImageGetter";
 	private ImageView m_ImageView;
 	private ProgressBar m_Progress;
	//private ProgressDialog m_ProgressDialog;
	AsyncImageGetter(ImageView imageView,
						ProgressBar progressBar)
	{
		m_ImageView = imageView;
		m_Progress = progressBar;
	}
	@Override
    protected void onPreExecute() {
		
    }
	@Override
	// params[0] - image url
	// params[1] - new widht of the image
    protected Bitmap doInBackground(String... params) {
 		String imageUrl =  params[0];
 		int newWidth =  Integer.parseInt( params[1] );
 		return GetBitMap( imageUrl, newWidth );
    }
 	@Override
      protected void onPostExecute(final Bitmap bMap) {
 		if (m_ImageView != null )
 		{
 			m_ImageView.setScaleType( ImageView.ScaleType.CENTER_CROP );
            LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT,
            										LayoutParams.WRAP_CONTENT,
            										0);
            
       
 		}
 		
 		m_ImageView.setImageBitmap( bMap ); 
 		m_ImageView.setVisibility(View.VISIBLE);
		
 		LinearLayout parent = (LinearLayout) m_Progress.getParent();
 		if (parent != null)
 		{
 			parent.removeView( m_Progress );
 			parent.addView( m_ImageView );
 		}
 	}
	 
	
	private Bitmap GetBitMap( String imageUrl, int a_NewWidth  )
	{
		try{
    		HttpURLConnection c;
    		URL url = new URL( imageUrl );
            
			c = (HttpURLConnection) url.openConnection();
			Bitmap bMap = null;
			InputStream  in = null;
			BufferedInputStream buf = null;
			for (int retry=0 ; retry < 3 && bMap == null ; retry++)
			{
				in = c.getInputStream();
				buf = new BufferedInputStream(in);
				bMap = BitmapFactory.decodeStream( buf );
				if (bMap == null)
				{
					try {
						Thread.sleep(350);
					} catch (InterruptedException e) {
						Log.e( TAG , e.toString() );
					}
				}
			}
			if( bMap != null )
			{
				float widthToHeightFactor =(float) bMap.getHeight() / (float)bMap.getWidth();
				int newHeight = (int)((float)a_NewWidth * widthToHeightFactor);
				bMap = Bitmap.createScaledBitmap(bMap, a_NewWidth, newHeight, true);
			}
			if (in != null){
            	in.close();
            }
            if (buf != null) {
            	buf.close();
            }
            return bMap;
			//m_ComunicationFailure  = true;
		}catch (IOException e) {
			Log.e(TAG, e.toString());
			//m_ComunicationFailure = true;
			return null;
		}
	}
}
