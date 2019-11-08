package com.IsraeliJokes;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Handler;
import android.os.Message;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;


public class JokesPagerAdapter  extends FragmentStatePagerAdapter{
	protected static final String TAG = "JokesPagerAdapter";
	private int m_MaxJokesToShow = 1;
	private Handler m_Handler;
	private int m_LastPosition = -1;
	private String m_Category;
	
	public JokesPagerAdapter(FragmentManager fm, String category, int maxNumOfJokes, Handler handler) {
        super(fm);
        m_Category = category;
        setCount(maxNumOfJokes);
		m_Handler = handler;
    }
	
	@Override
    public Fragment getItem(int position) {
        return ScreenSlidePageFragment.create(position,m_Category);
    }

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		try{
			super.setPrimaryItem(container, 1, object);
			if (container == null)
				return;
			Log.d("setPrimaryItem"," " + position);
			if ( m_LastPosition != position )//swipe occurred
			{
				m_MaxJokesToShow = Category.getNumOfShowableCategoryJokes(m_Category);
				m_LastPosition = position ;
				Message msg = new Message();
				msg.arg1 = position; 
				m_Handler.sendMessage( msg );
			}
		}
		catch(Exception e)
		{
			Log.e(TAG,e.toString());
		}
	}
	
	@Override
	public int getCount() {
		return m_MaxJokesToShow;
	}
	
	public void setCount(int count){
		m_MaxJokesToShow = count;
	}

}
