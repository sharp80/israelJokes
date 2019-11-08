package com.IsraeliJokes;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

public class ExternalStorage extends Activity {
	private String m_ExternalStoragePath;
	private String TAG;
	private String m_PackageName;

	private void InitExternalStoragePath()
	{
		m_PackageName = getApplicationContext().getPackageName();
        
		if (!JokesWebBuffer.IsExternalStorageWritable())
		{
			Log.e(TAG, "external storage is not writable!");
		}
		else
		{
			if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR)
			{
				File f = Environment.getExternalStorageDirectory();
				m_ExternalStoragePath = f.getAbsolutePath() ;
				m_ExternalStoragePath += "/Android/data/";
				m_ExternalStoragePath += m_PackageName; 
				m_ExternalStoragePath += "/files/";
				
				String path =  m_ExternalStoragePath ; 
				File directory = new File(path);
				directory.mkdirs();
				
				File fileNoMedia = new File(m_ExternalStoragePath, ".nomedia" );
				if (fileNoMedia.exists() == false )
				{
					try {
						fileNoMedia.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
							Log.e(TAG,"InitExternalStoragePath: "+e.toString());
					}
				}
				
			}
			else
			{
				//File f = getExternalCacheDir();
				File f =  this.getExternalFilesDir(null);
				m_ExternalStoragePath = f.getAbsolutePath();
			}
		}
	}
}
