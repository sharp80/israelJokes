package com.IsraeliJokes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.View;

public class ImageFileCreator extends Handler{
	ShareContainer m_shareContiner;
	JokeEntry m_jokeEntry;
	Handler m_finishedHandler;
	ImageFileCreator(ShareContainer shareContiner,
			Handler finishedHandler){
		m_shareContiner = shareContiner;
		m_finishedHandler = finishedHandler;
	}
	
	@Override
	public void handleMessage(Message msg) {
		Bitmap bitmap = Bitmap.createBitmap(m_shareContiner.getWidth(), 
				m_shareContiner.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		m_shareContiner.draw(canvas);
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			Time time = new Time();
			time.setToNow();
			String suffix = String.valueOf(time.toMillis(false))+".png";;
			String filePath = path +"/"+"temp"+suffix;
			File file = new File(filePath);
			if (file.exists())
				file.delete();
			fOut = new FileOutputStream(file);	
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
			fOut.flush();
			fOut.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
