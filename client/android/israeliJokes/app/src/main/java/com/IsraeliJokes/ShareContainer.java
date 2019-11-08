package com.IsraeliJokes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.LinearLayout;

@SuppressLint("NewApi")
public class ShareContainer extends LinearLayout {

	Handler mHandler;
	public ShareContainer(Context context) {
		super(context);
	}
	public ShareContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public ShareContainer(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	public void setHandler(Handler handler)
	{
		mHandler = handler;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mHandler != null) {
			Handler h = mHandler;
			mHandler = null;
			Message msg = new Message();
			msg.obj = this;
			h.sendMessage(msg);
		}
	}
}
