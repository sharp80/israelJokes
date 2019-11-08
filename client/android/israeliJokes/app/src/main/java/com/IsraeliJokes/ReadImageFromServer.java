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
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.LinearLayout.LayoutParams;

class ReadImageFromServer extends AsyncTask<String, Void, Bitmap> {
	//private ProgressDialog m_ProgressDialog;
	private View m_Parent;
	private ImageView m_Image = null;
	private String TAG = "ReadImageFromServer";
	private ProgressBar m_Progress ;
	ReadImageFromServer(View Parent) {
		m_Parent = Parent;
	}
	@Override
	protected void onPreExecute() {
		m_Progress = new ProgressBar(m_Parent.getContext());
		m_Progress.setVisibility( View.VISIBLE );
		LayoutParams params = new LayoutParams(40,40,0);
		m_Progress.setLayoutParams(params);
		LinearLayout container = (LinearLayout)m_Parent.findViewById(R.id.container);
		container.removeAllViews();
		container.addView(m_Progress);

	}
	@Override
	protected Bitmap doInBackground(String... params) {
		String imageUrl =  params[0];
		int screenWidth =  Integer.parseInt(params[1]);
		return GetBitMap( imageUrl, screenWidth );
	}
	@Override
	protected void onPostExecute(final Bitmap bMap) {
		if (m_Image  == null )
		{
			m_Image = new ImageView( m_Parent.getContext() );
			m_Image.setScaleType( ImageView.ScaleType.CENTER_CROP );
			LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT,
					0);

			m_Image.setLayoutParams( params );

		}
		LinearLayout ll = (LinearLayout)m_Parent.findViewById(R.id.container);
		ll.removeAllViews();
		ll.addView(m_Image);

		m_Image.setImageBitmap( bMap ); 
		m_Image.setVisibility(View.VISIBLE);

		//m_ProgressDialog.dismiss();
		if (m_Progress != null)
			m_Progress.setVisibility(View.GONE);
		m_Progress = null;

	}
	
	private Bitmap GetBitMap( String imageUrl, int ScreenWidth  )
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
						Log.e( TAG  , e.toString() );
					}
				}
			}
			if( bMap != null )
			{
				int newWidth = ScreenWidth - 20;
				float widthToHeightFactor =(float) bMap.getHeight() / (float)bMap.getWidth();
				int newHeight = (int)((float)newWidth * widthToHeightFactor);
				bMap = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, true);
			}
			if (in != null){
				in.close();
			}
			if (buf != null) {
			//	buf.close();
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
