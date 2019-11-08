/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.IsraeliJokes;


import java.util.Vector;
import com.IsraeliJokes.GlobalsManager.eGlobalResource;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 *
 * <p>This class is used by the {@link CardFlipActivity} and {@link
 * ScreenSlideActivity} samples.</p>
 */
@SuppressLint("NewApi")
public class ScreenSlidePageFragment extends Fragment {
    public static final String ARG_POSITION = "page";
    public static final String ARG_CATEGORY = "category";

    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_POSITION}.
     */
    private int mPageNumber;
    
    private String mCategory;
	private static final String TAG = "";

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber, String category) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pageNumber);
        args.putString(ARG_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_POSITION);
        mCategory = getArguments().getString(ARG_CATEGORY);
    }

    @Override
/*    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);

        // Set the title view to show the page number.
        ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                getString(R.string.title_template_step, mPageNumber + 1));

        return rootView;
    }
  */
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {
		
    	ViewGroup layout =  (ViewGroup)inflater.inflate(R.layout.joke_layout, container, false);

    	if (!GlobalGravity.IsDynamicGravitySupport(getActivity())){
			ImageView globalGravityIcon = (ImageView)layout.findViewById(R.id.GlobalGravityIcon) ;
			if (globalGravityIcon != null)
			{
				android.widget.LinearLayout.LayoutParams paramsIcon =
						new android.widget.LinearLayout.LayoutParams(
								0, 0,0F);
				globalGravityIcon.setLayoutParams(paramsIcon);
				globalGravityIcon.setPadding(0, 0, 0, 0);
				globalGravityIcon.setVisibility(View.INVISIBLE);
				TextView jokeTitleString = (TextView)layout.findViewById(R.id.JokeTitleString);
				if (jokeTitleString!=null){
					android.widget.LinearLayout.LayoutParams params =
							new android.widget.LinearLayout.LayoutParams(
									LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,10F);
					params.setMargins(0, 0, 0, 0);
					jokeTitleString.setLayoutParams(params);
				}
			}
		}
    	
    	TextView JokeTitleString = (TextView )layout.findViewById( R.id.JokeTitleString);
    	//	JokeTitleString.setText( m_JokePage.m_JokeEntry.JokeTitle );
    	JokeTitleString.setText(String.valueOf( mPageNumber ));
    	JokeEntry jokeEntry ;
		jokeEntry = JokePageAbove11._GetShowableJoke( mCategory , mPageNumber);
		if ( jokeEntry != null )
		{
			//m_MaxJokesToShow = Category.getNumOfShowableCategoryJokes(jokeEntry.JokeCategory);
			//layout.setTag( jokeEntry );
			displayJoke( getActivity(), layout , jokeEntry);

	    	if (GlobalGravity.IsDynamicGravitySupport(getActivity()))
	    		UpdateAlignmentIcon(layout , jokeEntry);
			//((ViewPager) container).addView(layout,0);
			return layout;
		}
		
		return layout;
		
	}
    
    
    void UpdateAlignmentIcon(final ViewGroup layout, final JokeEntry jokeEntry)
	{
		ImageView GlobalGravityIcon = (ImageView)layout.findViewById(R.id.GlobalGravityIcon);
		if (GlobalGravityIcon != null){
		GlobalGravityIcon.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						// update server
						if (GlobalsManager.GetGlobalInt(	getActivity(), 
								eGlobalResource.GRAVITY, 
								Gravity.RIGHT ) == Gravity.LEFT )
						{
							GlobalsManager.SetGlobalInt(getActivity(), eGlobalResource.GRAVITY, Gravity.RIGHT);
						}
						else
						{
							GlobalsManager.SetGlobalInt(getActivity(), eGlobalResource.GRAVITY, Gravity.LEFT);
						}
						displayJoke(getActivity(), layout, jokeEntry );
					}
				}
				);
		}
	}
    
    
    static public boolean displayJoke(final Activity activity, View parent, final JokeEntry jokeEntry)
	{
		Log.d(TAG, "m_CategoryId: " + jokeEntry.JokeCategory);
		Log.d(TAG, "m_JokeIndex: " + jokeEntry.JokeIndex);
		ScrollView  scroll = (ScrollView)parent.findViewById(R.id.ScrollView01);
		if (scroll!=null)
			scroll.fullScroll(View.FOCUS_UP);
		
		if ( jokeEntry.JokeCategory != null && jokeEntry.JokeIndex != -1  )
		{
			Vector<JokeEntry> vector= Category.m_JokesArr.get(jokeEntry.JokeCategory);
			Log.d(TAG, " vector.size(): " +  vector.size());

			LinearLayout container = (LinearLayout)parent.findViewById( R.id.container );
			container.removeAllViews();
			if ( jokeEntry.JokeIndex < vector.size() )
			{
				//m_JokeEntry = jokeEntry;
				TextView jokeTitle = (TextView)parent.findViewById(R.id.JokeTitleString) ;
				//String formattedJokeStr = Category.StringSplitter(m_JokeEntry.JokeStr, 35, 100);
				String formattedJokeStr = jokeEntry.JokeStr;
				int gravity =  GlobalsManager.GetGlobalInt(	activity, 
															eGlobalResource.GRAVITY, 
															Gravity.RIGHT );
																
				
				AutoResizeTextView jokeTextView = null;
				if ( jokeEntry.JokeStr.length() > 0 )
				{
					//if (m_JokeTextView == null )
					{
						jokeTextView = new AutoResizeTextView(activity);
						jokeTextView.setTextColor(Color.BLACK);
						jokeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
						LayoutParams params = new LayoutParams( LayoutParams.FILL_PARENT,
																LayoutParams.FILL_PARENT,
																0);
						jokeTextView.setLayoutParams( params );
						jokeTextView.setVisibility(View.VISIBLE);
					}
					jokeTextView.setGravity( gravity );
					container.addView(jokeTextView);
					jokeTextView.setText(  formattedJokeStr );
				}
				else
				{
					if (jokeTextView !=null)
					{
						jokeTextView.destroyDrawingCache();
						jokeTextView.setVisibility(View.INVISIBLE);
						jokeTextView = null;
					}
				}
				//jokeStr.setBackgroundColor(Color.RED);R
				jokeTitle.setText( jokeEntry.JokeTitle ); 
				jokeTitle.setTextColor(Color.BLACK);
				jokeTitle.setGravity( gravity );

				/* ***  Display image joke *** */
				if ( jokeEntry.JokePic.length() > 0)
				{
					Display display = activity.getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int screenWidth = size.x;
					new ReadImageFromServer(parent).execute
					( CategoriesPage.ImagesUrlPrefix +"/" + jokeEntry.JokePic,
						String.valueOf(screenWidth));
				}
				
				/* ***  Display video joke *** */
				if ( jokeEntry.JokeVideo.length() > 0)
				{
					String imageUrl = "https://img.youtube.com/vi/" + jokeEntry.JokeVideo + "/0.jpg";// the message to post to the wall
					
					Display display = activity.getWindowManager().getDefaultDisplay();
					Point size = new Point();
					display.getSize(size);
					int screenWidth = size.x;
					new ReadImageFromServer(parent).execute(imageUrl, String.valueOf(screenWidth));
					LinearLayout container1 = (LinearLayout)parent.findViewById(R.id.container);
					container1.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							String url = CategoriesPage.m_YouTubePrefix + jokeEntry.JokeVideo;
							Intent intent = new Intent(Intent.ACTION_VIEW); 
							intent.setData(Uri.parse(url)); 
							activity.startActivity(intent); 
						}
					});
				}
				else
				{
				/*	if (m_JokeVideo != null)
					{
						m_JokeVideo.setVisibility(View.GONE);
						m_JokeVideo.destroy();
						m_JokeVideo = null;
					}*/
				}
				
				Log.d(TAG,"string:" + jokeEntry.JokeStr);
				return true;
			}

		}
		return false;
	}
    private int getScreenWidth(){
    	WindowManager winman = getActivity().getWindowManager();
		int screenWidth = 0; 
		if ( winman  != null)
		{
			Display display = winman.getDefaultDisplay(); 
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR2)
				screenWidth = display.getWidth();
			else {
				Point outSize = new Point();
				display.getSize(outSize);
				screenWidth = outSize.x;
			}
		}
		return screenWidth;
    }
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }
}
