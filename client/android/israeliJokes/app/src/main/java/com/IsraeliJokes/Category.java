package com.IsraeliJokes;

import java.io.BufferedInputStream;

import android.graphics.drawable.BitmapDrawable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.IsraeliJokes.GlobalsManager.eGlobalResource;
import com.IsraeliJokes.JokesWebBuffer.eOrdering;
import com.google.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Category extends Activity{
	 /** Called when the activity is first created. */
	private static final String TAG = "IsrealiJokes::Category";
    public static HashMap<String,Vector<JokeEntry>> m_JokesArr = new HashMap<String, Vector<JokeEntry>>(1);
	public static String m_SiteUrl = "http://israelijokes.mayaron.com";
//	public static String m_SiteUrl = "http://israeliJokes.apphb.com/server";
    public static ArrayList<CategoryEntry> m_CategoriesArr; 
	public final static int ICON_HEIGHT = 150;
	private final static int ICON_THUMB_HEIGHT = 90;

	public static HashMap<String,Integer> m_CategoriesIdHash ;// <categoryId,Index>
	public static HashMap<Integer,Float> m_JokeLikeRateHash = new HashMap<Integer,Float>();// <JokeId, like rate>

	private String m_SelectedCategoryId = null;
	public ProgressDialog pd = null; 
	public String m_DataLine = null;
	private static final int NUM_OF_JOKES_TO_READ_IN_LIST = 5;
	public static Vector<JokesWebBuffer> m_JokeWebBuffer = null ;
	public static String FAVORITE_CATEGORY_ID = "9999";
	public static String ALL_JOKES_CATEGORY_ID = "4444";
	public static String VIDEOS_CATEGORY_ID = "2";
	public static String TOP_TEN_CATEGORY = "9998";
	public static String RANDOM_JOKE_CATEGORY_ID = "9997";
	public boolean m_AllJokes;
	public static int m_NumOfCategories = 0;
	private Timer m_longTimer = null;

	public enum eShowJokes
	{
		SHOW_UNREAD_JOKES,
		SHOW_READ_JOKES,
		SHOW_ALL_JOKES
	};
	static public eShowJokes m_ShowJokes = eShowJokes.SHOW_ALL_JOKES;
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.category_menu, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_highscore:
	        	 String newCatName = getResources().getString(R.string.top_ten);
            	 ChangeCategory( newCatName, TOP_TEN_CATEGORY );
            	 UpdateReadButtons();
	            return true;
	        case  R.id.menu_text_align_change:
	        	GlobalGravity.ToggleGlobalGravity(this);
	        	DisplayJokes(false);
	        	return true;
	        case  R.id.menu_change_order:
	        	CharSequence []arr = {
	        			 getResources().getString(R.string.order_by_random),
            			 getResources().getString(R.string.order_by_popular)
            	};	        	
	        	ShowList(arr);
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void ShowList( final CharSequence[] categoryListArr )
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
	    alert.setTitle( getResources().getString(R.string.order_by));
	    final int selected = GlobalsManager.GetGlobalInt(this, eGlobalResource.ORDER_BY, 0);
	    alert.setSingleChoiceItems(categoryListArr,selected, new DialogInterface.OnClickListener()
	    {
	        public void onClick(DialogInterface dialog, int which) 
	        {
	        	if ( selected != which)
	        	{
	    			JokesWebBuffer.eOrdering oredering = eOrdering.ORDERING_RANDOM;
	    		    
		            if( which == 0 )
		            {
		            	oredering = eOrdering.ORDERING_RANDOM;
		            	GlobalsManager.SetGlobalInt(Category.this, eGlobalResource.ORDER_BY, 0);
		            }
		            else if ( which == 1 )
		            {
		            	oredering = eOrdering.ORDERING_RATING;
		            	GlobalsManager.SetGlobalInt(Category.this, eGlobalResource.ORDER_BY, 1);
		            }
		            UpdateOrdering( oredering, dialog );
	        	}
	        	else
	        	{
	        		dialog.dismiss();
	        	}
	        }
	    });
	    alert.show();
		
	}
	
	private void UpdateOrdering( final JokesWebBuffer.eOrdering ordering,
								final DialogInterface dialog)
	{
		Handler clearSessionFin = new Handler() { 
	        @Override public void handleMessage(Message msg) { 
	        	if (msg.arg1 != -1)
	        	{
			    	for (int ind = 0 ; ind < m_JokeWebBuffer.size() ; ind++ )
			    	{
			    		m_JokeWebBuffer.get( ind ).ChangeOrdering(ordering);
				    	ClearCategoryFromDb(ConvertCategoryIndexToId(ind));
			    	}
					Handler appsReadHandler = new Handler() { 
				        @Override public void handleMessage(Message msg) { 
				        	dialog.dismiss();
				        	DisplayJokes(false);
				        }
					};
					
					getNewJokes( GetSelectedCategoryId(), 
								NUM_OF_JOKES_TO_READ_IN_LIST, 
								appsReadHandler );
	        	}
	        }
	    };
	    UpdateServerAsync clearSession = new UpdateServerAsync(clearSessionFin);
 		String url = Category.m_SiteUrl + "/NewUserSession.aspx?userId=" + CategoriesPage.m_UserId;
	    clearSession.execute(url);
		pd = ProgressDialog.show(this, getResources().getString(R.string.loading), "", true, false);
	}
	
	public static String GetRandomCategoryId()
	{
	    Random randInt = new Random();

		int RandCatIndex = randInt.nextInt( m_NumOfCategories-1 );
		
		return ConvertCategoryIndexToId( RandCatIndex );
	}
	
	private void DismissLoading()
	{
		if (pd != null)
		{
			pd.dismiss();
		}
	}
	
	public void getNewJokes(final String categoryId, 
			int numOfJokesToShow, 
			final Handler appsReadHandler )
	{
		final int categoryIndex = ConvertCategoryIdToIndex( categoryId );
		if (categoryIndex  == -1 || categoryIndex >= m_JokeWebBuffer.size())//|| categoryId == FAVORITE_CATEGORY_ID)
		{
			AllJokesReadNotify( appsReadHandler );
			Log.e( TAG, "GetNewJokes - got wrong category index:" + categoryIndex );
			return;
		}
		Handler dataHandler = new Handler() { 
			@Override 
	        public void handleMessage( Message msg ) { 
				if (m_longTimer != null)
				{
					m_longTimer.cancel();
					m_longTimer = null;
				}
				int numBufferedJokes = m_JokeWebBuffer.get(categoryIndex).NumOfJokes();
				boolean hasMoreJokes = true;
				for ( int ind=0 ; ind < numBufferedJokes && hasMoreJokes ; ind++)
				{
					JokeEntry jokeEntry = m_JokeWebBuffer.get(categoryIndex).GetJoke();
					if (jokeEntry == null)
					{ // all jokes for this category are finished
						hasMoreJokes = false;
					}
					AddJokeToDb( jokeEntry );
				//	String categoryId = GetSelectedCategoryId();
				//	int selectedCatIndex = GetSelectedCategoryIndex();
				//	AddJokeRow( categoryId,  selectedCatIndex, jokeEntry);
					
				}
				AllJokesReadNotify( appsReadHandler );
			}
		};
		
		// Read the jokes from the buffer, until we got the number of jokes we need or until the buffer gets empty 
		int numOfJokesCollected = 0;
		for (;
				numOfJokesCollected < numOfJokesToShow && m_JokeWebBuffer.get(categoryIndex).NumOfJokes(  ) > 0 ; 
				numOfJokesCollected++ )
		{
			JokeEntry jokeEntry = m_JokeWebBuffer.get(categoryIndex).GetJoke();
			AddJokeToDb( jokeEntry );
		}
		
		if ( numOfJokesCollected < numOfJokesToShow )
		{ 	// More jokes are needed, but the buffer is empty. 
			// Wait until buffer will read the needed jokes, then the handler will b called
			setupLongTimeout(20000, dataHandler);
			m_JokeWebBuffer.get(categoryIndex).WaitForJokesRead( dataHandler, numOfJokesToShow - numOfJokesCollected );
		}
		else
		{ // All needed jokes have been read
			AllJokesReadNotify( appsReadHandler );
		}
		
	} // private void GetNewJokes(final String categoryId)
	
	
	private ImageView m_GetMoreJokesBtn;
	private synchronized void setupLongTimeout(long timeout, final Handler timeoutHandler) {
	  if(m_longTimer != null) {
	    m_longTimer.cancel();
	    m_longTimer = null;
	  }
	  if(m_longTimer == null) {
	    m_longTimer = new Timer();
	    m_longTimer.schedule(new TimerTask() {
	      public void run() {
	        m_longTimer.cancel();
	        m_longTimer = null;
	        //do your stuff, i.e. finishing activity etc.
	    	Log.e(TAG,"TIMEOUT!!! category:" + GetSelectedCategoryId());
	    	timeoutHandler.sendEmptyMessage(0);
	    	
	      }
	    }, timeout /*delay in milliseconds i.e. 5 min = 300000 ms or use timeout argument*/);
	  }
	}
	
	public static int getNumOfShowableCategoryJokes(String categoryId)
	{
		int ctr = 0;
		if (m_JokesArr.containsKey(categoryId))
		{
			Vector<JokeEntry> vec =  m_JokesArr.get(categoryId);
			for( JokeEntry joke: vec)
			{
				if(CanShowJoke(joke))
					ctr++;
			}
		}
		return ctr;
	}
	
	public static JokeEntry getJoke(String categoryId, int jokeId)
	{
		if (m_JokesArr.containsKey(categoryId))
		{
			Vector<JokeEntry> vec =  m_JokesArr.get(categoryId);
			for( JokeEntry joke: vec)
			{
				if (joke.JokeId == jokeId)
					return joke;
			}
		}
		return null;
	}
	
	public void AddJokeToDb( JokeEntry jokeEntry )
	{
		jokeEntry.NewJoke = true;
		if ( m_JokesArr.containsKey( jokeEntry.JokeCategory ) == false )
		{ // Category does not exist yet - create category vector
			// Add Joke to new vector
			Vector<JokeEntry> vec = new Vector<JokeEntry>();
			jokeEntry.JokeIndex = 0;
			vec.add( jokeEntry );
			// Add new vector to DB
			m_JokesArr.put(jokeEntry.JokeCategory, vec);
		}
		else
		{
			int ordering = GlobalsManager.GetGlobalInt(this, 
					eGlobalResource.ORDER_BY, 0);
			if (ordering == eOrdering.ORDERING_RATING.ordinal() )
			{
				Vector<JokeEntry> jokes = m_JokesArr.get( jokeEntry.JokeCategory );
				for (int ind = 0 ; ind < jokes.size() ; ind++)
				{
					if (jokes.get(ind).LikeRate < jokeEntry.LikeRate )
					{
						jokeEntry.JokeIndex = ind;
						jokes.add(ind, jokeEntry);
						UpdateJokesIndexes(jokes);
						return;
					}
				}
			}
			
			// Add new Joke to existing category
			jokeEntry.JokeIndex = m_JokesArr.get( jokeEntry.JokeCategory ).size();
			m_JokesArr.get( jokeEntry.JokeCategory ).addElement( jokeEntry );
		}
	}
	
	private static void UpdateJokesIndexes(Vector<JokeEntry> jokes)
	{
		int ind = 0;
		for (JokeEntry joke : jokes)
		{
			joke.JokeIndex = ind;
			ind++;
		}
	}
	
	public void ReorderJokesDb(String JokeCategory)
	{
		int ordering =  GlobalsManager.GetGlobalInt(this, eGlobalResource.ORDER_BY, 0);
		
		if (ordering == eOrdering.ORDERING_RATING.ordinal() )
		{
			Vector<JokeEntry> jokes = m_JokesArr.get( JokeCategory );
			for (int pivot = 0 ; pivot < jokes.size() ; pivot++)
			{
				JokeEntry jokePivot = jokes.get( pivot ); 
				for (int ind = 0 ; ind < jokes.size() ; ind++)
				{
					if ( jokePivot.LikeRate > jokes.get(ind).LikeRate )
					{
						if ( ind == pivot+1 )
							break;// keep pivot in its place
						jokes.remove(pivot);
						jokes.add(ind, jokePivot);						
						break;
					}
				}
			}
		}
	}

	public static void RemoveJokeFromDb( JokeEntry jokeEntry )
	{
		if ( m_JokesArr.containsKey( jokeEntry.JokeCategory ) == false )
		{ // Category does not exist yet 
			return;
		}
		else
		{
			Vector<JokeEntry> vector = m_JokesArr.get( jokeEntry.JokeCategory );
			boolean found = false;
			for (int index=0 ; index < vector.size() && !found ; index++)
			{
				if (vector.get(index).JokeId == jokeEntry.JokeId )
				{
					vector.remove(index);
					found = true;
				}
			}
			if (found) {
				UpdateJokesIndexes(vector);
			}
		}
	}
	
	private void AllJokesReadNotify(final Handler appsInfoHandler)
	{
		Message msg1 = new Message();
		if (appsInfoHandler != null)
		{
			appsInfoHandler.sendMessage(msg1);
		}
	}
	
	private View createSpacer() 
	{
		View spacer = new View(this);
	
		spacer.setMinimumHeight(1);
		LayoutParams layoutParamas =  new TableLayout.LayoutParams(
			  									LayoutParams.FILL_PARENT,
			  									2);
		spacer.setLayoutParams(layoutParamas);
		spacer.setBackgroundDrawable( this.getResources().getDrawable( R.drawable.line ) );
		return spacer;
	}
	
	private static int findClosestEndWord(String input)
	{
		char[] buf = input.toCharArray();
		for (int ind = input.length()-1 ; ind >= 0 ; ind--)
			if (buf[ind] == ' ' || buf[ind] == ',' || buf[ind] == '-')
				return ind;
		return input.length();
	}
	
    public static String StringSplitter(String input, 
    								int sizeOfLine,
    								int MaxNimOfLines)
    {
    	int procLength = 0;
    	String res = "";
    	int start = 0;
    //	while (procLength < input.length())
    	int numOfLines = 0;
    	input = input.replace("\n", " ")
    			.replace("\r", " ");
    	while (procLength < input.length() && numOfLines < MaxNimOfLines) 
    	{
    		while (input.charAt(start) == ' ' && start < input.length())
    		{
    			start++;
    		}
    		int end = input.length() < start +  sizeOfLine? input.length() :  start +  sizeOfLine ;
    		if (end < input.length() )
    		{
    			end = start + findClosestEndWord(input.substring(start , end));
    		}
    		res = res + input.substring(start , end);
    		procLength += (end - start +1);
    		if (procLength < input.length() && numOfLines < MaxNimOfLines -1)
    		{
    			res = res + "\n";
    		}
    		start = end ;
    		numOfLines++;
    	}
    	return res;
    }
    
    private void AddGetMoreJokesButton()
    {
		LinearLayout table = (LinearLayout)findViewById( R.id.linearLayout2 );

    	LinearLayout tableRow = new LinearLayout(this);
		tableRow.setOrientation(LinearLayout.HORIZONTAL);
		tableRow.setWeightSum(1);
		tableRow.setPadding(0, 0, 5, 0);
		tableRow.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT,
                									LayoutParams.WRAP_CONTENT));
		tableRow.setGravity( Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		table.addView(tableRow);
		
		m_GetMoreJokesBtn = new ImageView(this);
		Drawable d = getResources().getDrawable( R.drawable.more_jokes );
		m_GetMoreJokesBtn.setImageDrawable( d );
		tableRow.addView( m_GetMoreJokesBtn );
		m_GetMoreJokesBtn.setOnClickListener(new View.OnClickListener() {
			// joke string was touched - open joke window
	          public void onClick(View v) {
	  			String categoryId = GetSelectedCategoryId();
	  			getNewJokes( categoryId, NUM_OF_JOKES_TO_READ_IN_LIST, null );
	  			RemoveGetMoreJokesButton();
	  			DisplayJokes(true);
	          }
		});
    }
    
    private void RemoveGetMoreJokesButton()
    {
    	LinearLayout table = (LinearLayout)findViewById( R.id.linearLayout2 );
		table.removeViewAt(table.getChildCount()-1);
    }
    
    private Drawable resize(Drawable image, int width, int height) {
    	
    	
        Bitmap d = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, width, height, false);
        
        return new BitmapDrawable(bitmapOrig);
    }
    
    private void SaveImageOnPhone( Context context, Bitmap image, String FileName )
	{
		try {
			/*if (!IsExternalStorageWritable())
			{
				Log.e(TAG, "external storage is not writable!");
				return;
			}
			String path = Environment.getExternalStorageDirectory().toString()  + "/jokes/" ;
			*/
			FileOutputStream fos = context.openFileOutput( FileName, Context.MODE_PRIVATE);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			
			
			/*OutputStream fOut = null;
			File directory = new File(path);
			directory.mkdirs();
			
			
			File file = new File(path, FileName.replace(" ", "") );
			
			file.createNewFile();
			fOut = new FileOutputStream(file);*/
		    
		
		} catch (FileNotFoundException e) {
			Log.e( TAG, e.toString());
		} catch (IOException e) {
			Log.e( TAG, e.toString());
		}
		catch (Exception e)
		{
			Log.e( TAG, e.toString());
		}

			//MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
	}
    
    private void startJokePage(final JokeEntry jokeEntry){
    	Intent i ;
    	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
    		i = new Intent(Category.this, JokePageAbove11.class);
    	else
    		i = new Intent(Category.this, JokePage.class);
    	i.putExtra( "CategoryId", jokeEntry.JokeCategory );
    	i.putExtra( "JokeId" , jokeEntry.JokeId );

    	startActivity(i);  
    	finish();
    }
    
    private void AddThumb( final JokeEntry jokeEntry )
    {
    	LinearLayout table = (LinearLayout)findViewById( R.id.linearLayout2 );
    	
		LinearLayout tableRow = new LinearLayout(this);
		tableRow.setOnClickListener(new View.OnClickListener() {
			// joke string was touched - open joke window
	          public void onClick(View v) {
	        	startJokePage(jokeEntry);  
	          }
		});
		
		
		tableRow.setOrientation(LinearLayout.HORIZONTAL);
		tableRow.setWeightSum(1);
		tableRow.setPadding(0, 0, 5, 0);
		tableRow.setLayoutParams(new LayoutParams( LayoutParams.MATCH_PARENT,
                									LayoutParams.WRAP_CONTENT));
		tableRow.setGravity( Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		
		LinearLayout leftRowSide = new LinearLayout(this);
		leftRowSide.setOrientation(LinearLayout.VERTICAL);
		leftRowSide.setWeightSum(1);
		leftRowSide.setPadding(0, 0, 0, 0);
		leftRowSide.setGravity( Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		leftRowSide.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		tableRow.addView(leftRowSide);
		
		int globalGravity = GlobalGravity.GetGlobalGravity(this);
		
		TextView titleText = new TextView(this);
		titleText.setGravity(  Gravity.TOP | globalGravity );
		titleText.setPadding(10, 2, 5, 2);
		titleText.setTextColor(Color.BLUE);
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
		titleText.setVerticalScrollBarEnabled(true);
		titleText.setText( jokeEntry.JokeTitle  );
		titleText.setLayoutParams(new LayoutParams( LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		leftRowSide.addView(titleText);
		
		
		View review = GetReview(jokeEntry);
		leftRowSide.addView(review);
		
		ProgressBar progressBar = new ProgressBar( this );
		tableRow.addView( progressBar );
		
		ImageView entryImageView = new ImageView(this);
		Drawable d = getResources().getDrawable(R.drawable.icon1);
		d = resize(d, 60, 60 );
		entryImageView.setBackgroundDrawable(d);
		entryImageView.setPadding(10, 2, 10, 2);
		//tableRow.addView( entryImageView );

		AsyncImageGetter imageGetter = new AsyncImageGetter(entryImageView , progressBar);
		imageGetter.execute( CategoriesPage.ImagesUrlPrefix +"/" + jokeEntry.JokePic, "72" );
		table.addView(tableRow);
        table.addView(createSpacer());

    }
    private View GetReview(JokeEntry jokeEntry)
    {
    	RatingBar ratingBar = new RatingBar(this,null, android.R.attr.ratingBarStyleSmall);


		ratingBar.setRating((float)jokeEntry.LikeRate);
		ratingBar.setEnabled(false);
		ratingBar.setNumStars(5);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT ,LayoutParams.WRAP_CONTENT);
		ratingBar.setLayoutParams(params);
		return ratingBar;
    }
    
	private void AddAppRaw( final JokeEntry jokeEntry, int color)
	{
		int globalGravity = GlobalGravity.GetGlobalGravity(this);
		
		LinearLayout table = (LinearLayout)findViewById( R.id.linearLayout2 );
    	
		LinearLayout tableRowTitle = new LinearLayout(this);
		tableRowTitle.setOrientation(LinearLayout.HORIZONTAL);
		tableRowTitle.setWeightSum(1);
		tableRowTitle.setPadding(0, 0, 5, 2);
		LayoutParams layoutParams = new LayoutParams( LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		tableRowTitle.setLayoutParams(layoutParams);
		tableRowTitle.setGravity( Gravity.CENTER_VERTICAL | globalGravity);
		
	//	ratingBar.setScaleX(1.1f);

		
		/*ImageView reviewImage = new ImageView(this);
		Drawable d = getResources().getDrawable(R.drawable.thumb_up_icon);
		d = resize(d, 60, 60 );
		reviewImage.setBackgroundDrawable(d);
		reviewImage.setPadding(10, 2, 10, 2);
		tableRowTitle.addView(reviewImage);
*/
		
		TextView titleText = new TextView(this);
		titleText.setGravity(  Gravity.TOP | globalGravity );
		titleText.setPadding(10, 2, 5, 2);
		titleText.setTextColor(Color.BLUE);
		titleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		titleText.setVerticalScrollBarEnabled(true);
		titleText.setText( jokeEntry.JokeTitle  );
		tableRowTitle.addView(titleText);

		View review = GetReview(jokeEntry);
		if (GlobalGravity.IsDynamicGravitySupport(this)){
			tableRowTitle.addView(review);
			table.addView(tableRowTitle);
		} else {
			table.addView(tableRowTitle);
		}

		LinearLayout tableRow = new LinearLayout(this);
		tableRow.setOrientation(LinearLayout.HORIZONTAL);
		tableRow.setWeightSum(1);
		tableRow.setPadding(0, 0, 5, 0);
		tableRow.setLayoutParams(new LayoutParams( LayoutParams.FILL_PARENT,
                									LayoutParams.WRAP_CONTENT));
		tableRow.setGravity( Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		
		tableRow.setOnClickListener(new View.OnClickListener() {
			// joke string was touched - open joke window
	          public void onClick(View v) {
	        	startJokePage(jokeEntry);
	          }
		});
		
		TextView shortDescText = new TextView(this);
	    shortDescText.setGravity(  Gravity.TOP | globalGravity );
	    shortDescText.setPadding(10, 2, 10, 2);
	//	shortDescText.setBackgroundColor(Color.BLUE);
		shortDescText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		shortDescText.setEllipsize(TruncateAt.END);
		shortDescText.setMaxLines(1);
		shortDescText.setSingleLine();
		
		//final int JokeIndex 		= jokeEntry.JokeIndex;
		
		
	//	shortDescText.setBackgroundColor(color);
		shortDescText.setVerticalScrollBarEnabled(true);
		//shortDescText.setMaxLines(5);
		shortDescText.setLayoutParams(new LayoutParams(
     			0,
     			LayoutParams.WRAP_CONTENT,
     			(float)1));
		
		// android.view.Display display = ((android.view.WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		// int newWidth = (int)((display.getWidth() - 230 ));
		 shortDescText.setHorizontallyScrolling(false);
		
		//shortDescText.setWidth( newWidth );    // set width to half
		String appShortDesc = StringSplitter(jokeEntry.JokeStr, 35, 2);
		appShortDesc = appShortDesc + "...";
		appShortDesc = jokeEntry.JokeStr;
			
		shortDescText.setText( appShortDesc );
		shortDescText.setTextColor(Color.BLACK);
		tableRow.addView(shortDescText);
		
	    ImageView image = new ImageView( this );
        image.setScaleType( ImageView.ScaleType.CENTER_CROP );
        table.addView(tableRow);
        table.addView(createSpacer());
	}
	

	public String GetAppUrl(String LinkToApp)
	{
		String appUrl = null;
	//	String linkLower = LinkToApp.toLowerCase();
		String linkLower = LinkToApp;
		String strToFind = "https://market.android.com/";
		int startInd = linkLower.indexOf( strToFind );
		
		if (startInd != -1 )
		{
			startInd = startInd + strToFind.length();
			appUrl = linkLower.substring(startInd, linkLower.length());
			appUrl = "market://" + appUrl ;
		}
		return appUrl;
		//return LinkToApp;
	}

	public void getAllCategories( final Handler postCategoryDbFill, 
									final int screenWidth,
									final String externalStoragePath, 
									final Context context,
									final String userId,
									final int ordering)
	{
		
		//Reading categories from server
		String CategoriesUrl = m_SiteUrl + "/ShowAllCategories.aspx?version=" + CategoriesPage.m_VersionNum +
				"&appId=" + CategoriesPage.m_AppId;
		m_DataLine = null;
		for (int retry = 0 ; retry < 3 && m_DataLine == null ; retry++ )
		{
			m_DataLine = GetData( CategoriesUrl);
			if (m_DataLine == null)
			{
				Log.e(TAG, "GetAllCategories : doInBackground, retry: "+ retry);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {

				}
			}
		}

		//m_DataHandler = postCategoryDbFill;
		//m_DataHandler = new Handler() { 
		//  @Override public void handleMessage(Message msg) { 
		try {
			if (m_DataLine == null)
			{
				//	pd.dismiss();
				Log.e(TAG,"GetAllCategories: dataFromServer == null");
				Message msg1 = new Message();
				msg1.arg1 = -1;
				postCategoryDbFill.sendMessage(msg1);
				return;
			}

			m_JokeWebBuffer = new Vector<JokesWebBuffer>(10); 
			m_CategoriesArr = new ArrayList<CategoryEntry>();
			m_CategoriesIdHash = new HashMap<String,Integer>();
			JSONArray jsonArr 	= new JSONArray( m_DataLine );
			m_NumOfCategories = jsonArr.length();
			for (int ind=0; ind < jsonArr.length() ; ind++)
			{
				JSONObject json = jsonArr.getJSONObject(ind);
				String categoryName =  
						json.getString("name")
						.replace("&#39;", "'")
						.replace("&amp;", "&");
				String categoryIcon = json.getString("icon")
						.replace("&#39;", "'")
						.replace("&amp;", "&") ;
				String categoryId = json.getString("id")
						.replace("&#39;", "'")
						.replace("&amp;", "&") ;
				String displayType;
				if (json.has("layout"))
				{
					displayType = json.getString("layout");
				}
				else
				{
					displayType = CategoryEntry.TEXT_LIST;
				}
				CategoryEntry catEntry = new CategoryEntry();
				catEntry.Name 		= categoryName;
				catEntry.Id 		= categoryId;
				catEntry.CatBitmap 	= GetBitMapIcon( context, categoryIcon );
				catEntry.DisplayLayoutType = displayType;
				if (catEntry.CatBitmap == null) 
				{ // some kind of weird connection problem caused that we dont have bitmap
					catEntry.CatBitmap = BitmapFactory.decodeResource(context.getResources(),
							R.drawable.icon);
				}
				int newHeight = ICON_THUMB_HEIGHT ;
				float heightToWidthFactor =(float) catEntry.CatBitmap.getWidth() / (float)catEntry.CatBitmap.getHeight();
				int newWidth = (int)((float)newHeight * heightToWidthFactor);
				catEntry.CatBitmapThumb = Bitmap.createScaledBitmap(catEntry.CatBitmap, newWidth, newHeight, true);


				m_CategoriesArr.add(catEntry);
				m_CategoriesIdHash.put( categoryId, ind );
				// Create jokes buffer for the new category
				JokesWebBuffer jokeWebBuffer = new JokesWebBuffer( categoryId, 
						screenWidth, 
						externalStoragePath,
						userId,
						ordering);
				jokeWebBuffer.start();
				m_JokeWebBuffer.add( jokeWebBuffer );
				Log.d(TAG, "getAllCategories m_JokeWebBuffer.size:"+ m_JokeWebBuffer.size());
				Log.d(TAG, "getAllCategories m_CategoriesIdHash.size:"+ m_CategoriesIdHash.size());

			}
			
	 		initStaticJokesCategories(context, userId, ordering);
			
			//	UpdateCategory();
			postCategoryDbFill.sendEmptyMessage(0);

		} catch (JSONException e) {
			Log.e(TAG,"JSON error!");
			Log.e(TAG,e.getMessage());
			finish();
		}
		//}
		// };


		return;
	}

	protected String GetData(String url)
	{
		try
		{	
			Log.d(TAG,"GetData - Starting to retrive data from server");
			String line = null;
     		StringBuilder builder = null;
 			HttpResponse response = null;
 			Log.d(TAG, "retrieving from:" + url);
 			HttpGet request = new HttpGet();
 			request.setURI(new URI(url));
 			
 			HttpParams httpParameters = new BasicHttpParams();
 			// Set the timeout in milliseconds until a connection is established.
 			int timeoutConnection = 3000;
 			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
 			
 			// Set the default socket timeout (SO_TIMEOUT) 
 			// in milliseconds which is the timeout for waiting for data.
 			int timeoutSocket = 3000;
 			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
 			
 			HttpClient client = new DefaultHttpClient(httpParameters);
 			response = client.execute( request );
 			BufferedReader bufferReader = 
 				   new BufferedReader ( new InputStreamReader(response.getEntity().getContent()) );
 			builder = new StringBuilder();
 			while((line = bufferReader.readLine()) != null)
 			{
 				builder.append(line);
 			}
 			bufferReader.close();
 			return builder.toString(); 
			}catch (SocketTimeoutException  e){
				Log.e(TAG,"GetData - IllegalStateException" + e.toString());
				
				DismissLoading();
				return null;
			}catch (ConnectTimeoutException e){
				Log.e(TAG,"GetData - ConnectTimeoutException" + e.toString());
				DismissLoading();
				return null;
 		}catch(URISyntaxException e){
 		   Log.e(TAG,"GetData - URISyntaxException" + e.toString());
 		   DismissLoading();
 		   return null;
 		}catch(IOException e){
 		   Log.e(TAG,"GetData - IOException. jason or http: " + e.toString());
 		   Log.e(TAG,e.getMessage());
 		   DismissLoading();
			   return null;
 	   	}catch(IllegalStateException e){
 		   Log.e(TAG,"GetData - Http IllegalStateException" + e.toString());
 		   DismissLoading();
 		   return null;
 	   	}
     }
 
	private Bitmap GetBitMapIcon( Context context , String imageName )
	{
		Bitmap bitmap = ReadImageFromStorage(context, imageName );
		if( bitmap == null )
		{
			bitmap = GetIconsFromServer(imageName);
			SaveImageOnPhone( context, bitmap, imageName);
		}
		return bitmap;
	}
	
	private Bitmap ReadImageFromStorage(Context context, String imageName )
	{
		try
		{
		File file = context.getFileStreamPath(imageName);
		if(file.exists() == false) 
		{
			return null;
		}
		else
		{
			 Bitmap bMap = BitmapFactory.decodeFile( file.getAbsolutePath() );
			 return bMap;
		}
		}
		catch(Exception e)
		{
			return null;
		}
	}
	
	private Bitmap GetIconsFromServer( String imageName )
	{
		for (int retry = 0 ; retry < 5; retry++ )
		{
			try{
	    		HttpURLConnection c;
	    		URL url = new URL(CategoriesPage.IconsUrlPrefix + imageName);
	            
				c = (HttpURLConnection) url.openConnection();
				InputStream  in = c.getInputStream();
				BufferedInputStream buf = new BufferedInputStream(in, 8192);
				Bitmap bMap = BitmapFactory.decodeStream( buf );
				if (bMap == null)
					return null;
				int newHeight = ICON_HEIGHT ;
				
				float heightToWidthFactor =(float) bMap.getWidth() / (float)bMap.getHeight();
				int newWidth = (int)((float)newHeight * heightToWidthFactor);
				bMap = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, true);
		
				if (in != null){
	            	in.close();
	            	in = null;
	            }
	            if (buf != null) {
	            	buf.close();
	            	buf = null;
	            }
	            return bMap;
				//m_ComunicationFailure  = true;
			}catch (IOException e) {
				Log.e(TAG, "GetBitMap: " + e.toString());
				//m_ComunicationFailure = true;
				try {
					Thread.sleep(350);
				} catch (InterruptedException e1) {
				}
			}
		}
		return null;
	}
	
	public static void updateJokesWebBuffers()
	{
		// look in all categories
		for (int ind  = 0 ; ind < m_CategoriesIdHash.size() ; ind++ )
		{
			String CategoryId = ConvertCategoryIndexToId(ind);
			Vector<JokeEntry> vector = Category.m_JokesArr.get( CategoryId );
			if (vector == null || CategoryId == FAVORITE_CATEGORY_ID )
				continue;
			JokeEntry lastJokeEntry = vector.get(vector.size()-1);
			m_JokeWebBuffer.get(ind).SetLastJokeId( lastJokeEntry.JokeId );
		}
	}
	
	public static void DebugDisplayTitle(Cursor c)
    {
        /*Toast.makeText(this, 
                "id: " + c.getString(0) + "\n" +
                "joke: " + c.getString(1) + "\n" +
                "TITLE: " + c.getString(2) + "\n" +
                "category:  " + c.getString(3),
                Toast.LENGTH_LONG).show();   */     
    } 
	
	
	private void ChangeCategory( String newCatName , String categoryId )
	{
		// Mark which category was selected
        m_SelectedCategoryId = categoryId;
        
        // Update category to the selected category 
		UpdateCategory();
		
		// Show to user message with the category that was selected
		Toast.makeText( getApplicationContext(), newCatName, Toast.LENGTH_SHORT ).show();
	}
	
	
	
	public boolean CategoryExistInRam( String categoryId )
	{
		return m_JokesArr.containsKey( categoryId );
	}
	
	static public boolean CanShowJoke( JokeEntry jokeEntry )
	{
		if( m_ShowJokes == eShowJokes.SHOW_READ_JOKES )
		{
			if ( jokeEntry.JokeWasRead == false )
			{
				return false;
			}
		}
		else if ( m_ShowJokes == eShowJokes.SHOW_UNREAD_JOKES )
		{
			if ( jokeEntry.JokeWasRead == true )
			{
				return false;
			}
		}
		return true;
	}
	
	private void DisplayJokes(boolean newJokesOnly)
	{
		if (pd != null && pd.isShowing()){
			pd.dismiss();
			pd = null;
		}
		LinearLayout table = (LinearLayout)findViewById( R.id.linearLayout2 );
		if (newJokesOnly == false)
		{
			table.removeAllViewsInLayout();
	    	table.addView(createSpacer());
		}
        
		String categoryId = GetSelectedCategoryId();
		if ( CategoryExistInRam( categoryId ) == false )
			return; // might get here if the category is empty..
		
		Vector<JokeEntry> appArray = m_JokesArr.get( categoryId );
		Iterator<JokeEntry> it = appArray.iterator();
		int ind = 0;
		int selectedCatIndex = GetSelectedCategoryIndex();
		while ( it.hasNext()  )
		{
			JokeEntry jokeEntry = (JokeEntry)it.next();
			if ( selectedCatIndex != -1 )
			{
				if ( (newJokesOnly && jokeEntry.NewJoke) || newJokesOnly == false)
				{
					jokeEntry.NewJoke = false;
					if ( ShowJokeRow(categoryId, selectedCatIndex, jokeEntry) )
						ind++;
				}
			}
		}
		if ( m_ShowJokes != eShowJokes.SHOW_READ_JOKES ) // &&categoryId.equals(FAVORITE_CATEGORY_ID) == false )
		{
			if (m_JokeWebBuffer.get(GetSelectedCategoryIndex()).GetFinishedJokesOnServer() == false  )
			{
				AddGetMoreJokesButton();
			}
			else
			{
				RemoveGetMoreJokesButton();
			}
		}
	}
	
	private boolean ShowJokeRow(String categoryId, int selectedCatIndex, JokeEntry jokeEntry)
	{
		if ( categoryId.equals(FAVORITE_CATEGORY_ID) == false &&
			categoryId.equals(RANDOM_JOKE_CATEGORY_ID) == false &&
			categoryId.equals(TOP_TEN_CATEGORY) == false) 
		{
			if ( m_CategoriesArr.get(selectedCatIndex).DisplayLayoutType.equals(CategoryEntry.THUMBS_LIST) )
			{
				if ( CanShowJoke( jokeEntry ) )
				{
					AddThumb ( jokeEntry );
					return true;
				}
			}
			else
			{
				if ( CanShowJoke( jokeEntry  ) )
				{
					AddAppRaw( jokeEntry, R.color.highscore_1  );
					return true;
				}
			}
		}
		else
		{
			if ( CanShowJoke( jokeEntry  ) )
			{
				AddAppRaw( jokeEntry, R.color.highscore_1  );
				return true;
			}
		}
		return false;
	}
	
	public static void ClearCategoryFromDb( String CategoryId )
	{
		if ( m_JokesArr.containsKey(CategoryId) )
		{ 
			m_JokesArr.get( CategoryId ).removeAllElements();
			m_JokesArr.remove(CategoryId);
		}
	}

	public void UpdateCategory()
	{
		// If the category is favorite - clear DB and restart download of favorite jokes
		if(  GetSelectedCategoryId().equals(FAVORITE_CATEGORY_ID) ||
			GetSelectedCategoryId().equals(TOP_TEN_CATEGORY))
		{
			ClearCategoryFromDb(GetSelectedCategoryId());
			int index = GetSelectedCategoryIndex();
			if(index == -1)
				return;
			Log.d(TAG,"UpdateCategory index:"+index);
			m_JokeWebBuffer.get(index).InitWebBuffer();
		}
		
		// First thing - update category icon in the categories selection button
		ImageView categoriesImage = (ImageView)findViewById( R.id.categoryImage );
        int selectedCatIndex = GetSelectedCategoryIndex();
        if ( GetSelectedCategoryId().equals( FAVORITE_CATEGORY_ID ))
        {
        	Drawable favoriteDraw = getResources().getDrawable( R.drawable.random_icon );
    		categoriesImage.setImageDrawable( favoriteDraw );
    		android.view.ViewGroup.LayoutParams params = categoriesImage.getLayoutParams();
    		Resources r = getResources();
    		params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
    		params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
    		categoriesImage.setLayoutParams(params);
    		// All jokes are seen in favorites
    		m_ShowJokes = eShowJokes.SHOW_ALL_JOKES;
        }
        else if ( GetSelectedCategoryId().equals(Category.TOP_TEN_CATEGORY))
        {
        	Drawable favoriteDraw = getResources().getDrawable( R.drawable.top10thumb );
    		categoriesImage.setImageDrawable( favoriteDraw );
    		android.view.ViewGroup.LayoutParams params = categoriesImage.getLayoutParams();
    		Resources r = getResources();
    		params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
    		params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
    		categoriesImage.setLayoutParams(params);
    		// All jokes are seen in top 10
    		m_ShowJokes = eShowJokes.SHOW_ALL_JOKES;
        }
        else if (selectedCatIndex != -1) // All categories except favorites
        {
        	Bitmap catBmp = m_CategoriesArr.get(selectedCatIndex).CatBitmapThumb;
        	if (catBmp != null)
        		categoriesImage.setImageBitmap(catBmp);
        }
		// Before getting category entries from server - check if we already did it
		String categoryId = GetSelectedCategoryId();
		if (CategoryExistInRam( categoryId ) == false)
		{
			Handler appsReadHandler = new Handler() { 
		        @Override public void handleMessage(Message msg) { 
		        	DisplayJokes(false);
		        }
			};
			//if (categoryId == FAVORITE_CATEGORY_ID)
			{
			//	return; // no favorite jokes exist
			}
			getNewJokes( categoryId, NUM_OF_JOKES_TO_READ_IN_LIST, appsReadHandler );
			pd = ProgressDialog.show(this, 
					getResources().getString(R.string.loading), "", true, false);
		}
		else
		{
			DisplayJokes(false);
		}
	}
	
	
	
	private int GetSelectedCategoryIndex()
	{
		/*int selectedIndex =  m_CategoriesSelectionButton.getSelectedItemPosition() ;
		if (selectedIndex == -1)
			return 0;
		return selectedIndex;*/
		return ConvertCategoryIdToIndex(GetSelectedCategoryId());
		
	}
	
	private String GetSelectedCategoryId()
	{
		if (m_SelectedCategoryId == null)
		{
			if (m_CategoriesIdHash.size() > 0 )
			{
				m_SelectedCategoryId = ConvertCategoryIndexToId(0);
			}
		}
		Log.d(TAG,"GetSelectedCategoryId :"+ m_SelectedCategoryId);
		return m_SelectedCategoryId;
	}
	
	static String ConvertCategoryIndexToId( Integer CategoryIndex )
	{
		if ( m_CategoriesIdHash.containsValue( CategoryIndex ) == false )
			return null;
		Set<Entry<String, Integer>> set = m_CategoriesIdHash.entrySet();
		Iterator<Entry<String, Integer>> it = set.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			if ( entry.getValue() == CategoryIndex )
			{
				return (String)entry.getKey();
			}
		//      System.out.println(entry.getKey() + " : " + entry.getValue());
		}
		return null;
	}

	public static int ConvertCategoryIdToIndex(String CategoryId)
	{
		if(CategoryId==null)
			return -1;
		try
		{
			return m_CategoriesIdHash.get(CategoryId);
		}
		catch(NullPointerException e)
		{
			Log.e(TAG, " ConvertCategoryIdToIndex - " + e.toString());
			return -1;
		}
	}
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
    	super.onCreate(savedInstanceState);
        setContentView( R.layout.category );
        Intent intent = getIntent();
        m_SelectedCategoryId = intent.getStringExtra( "CategoryId" );
        boolean backFromJokePage = intent.getBooleanExtra("BackFromJokePage", false);
        if ( backFromJokePage  == true )
        	ReorderJokesDb( m_SelectedCategoryId );
           
        Button BackToCategories = (Button)findViewById( R.id.BackToCategories ); 
    	BackToCategories.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	finish();
            }
        });

        Button FavoriteBtn = (Button)findViewById(R.id.SortingBtn);
        FavoriteBtn.setOnClickListener(new OnClickListener() {
             public void onClick(View v) {
            	 CharSequence []arr = { 
            			 getResources().getString(R.string.order_by_random),
            			 getResources().getString(R.string.order_by_popular)
            			 };	        	
 	        	ShowList(arr);
             }   });
        
        UpdateCategory();
        
        Button ShowUnreadJokes = (Button)findViewById(R.id.ShowUnreadJokes);
		ShowUnreadJokes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	m_ShowJokes = eShowJokes.SHOW_UNREAD_JOKES;
            	DisplayJokes(false);
            	UpdateReadButtons();
            }   });
		
		Button ShowReadJokes = (Button)findViewById(R.id.ShowReadJokes);
		ShowReadJokes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	m_ShowJokes = eShowJokes.SHOW_READ_JOKES;
            	DisplayJokes(false);
            	UpdateReadButtons();
            }   });
		
		Button ShowAllJokes = (Button)findViewById(R.id.ShowAllJokes);
		ShowAllJokes.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	m_ShowJokes = eShowJokes.SHOW_ALL_JOKES;
            	DisplayJokes(false);
            	UpdateReadButtons();
            }   });
    	UpdateReadButtons();
    	LoadAdMob();

    }

	private void LoadAdMob()
	{
		AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
	}
	private void UpdateReadButtons()
	{
		Button ShowUnreadJokes = (Button)findViewById(R.id.ShowUnreadJokes);
		Button ShowReadJokes = (Button)findViewById(R.id.ShowReadJokes);
		Button ShowAllJokes = (Button)findViewById(R.id.ShowAllJokes);
		
		if (GetSelectedCategoryId().equals( FAVORITE_CATEGORY_ID ))
		{
			ShowReadJokes.setVisibility(View.INVISIBLE);
			ShowUnreadJokes.setVisibility(View.INVISIBLE);
			ShowAllJokes.setVisibility(View.INVISIBLE);
			
		}
		else
		{
			boolean selected = false;
			ShowReadJokes.setSelected(selected);
			ShowUnreadJokes.setSelected(selected);
			ShowAllJokes.setSelected(selected);
			switch( m_ShowJokes )
			{
				case SHOW_READ_JOKES :
					ShowReadJokes.setSelected(!selected);
					break;
				case SHOW_UNREAD_JOKES:
					ShowUnreadJokes.setSelected(!selected);
					break;
				case SHOW_ALL_JOKES:
					ShowAllJokes.setSelected(!selected);
					break;
			}
		}
	}
	

	
	private static void initFavoriteCategory(Context context, String UserId, int Ordering)
	{
		// Add an empty vector of favorites jokes, for init, this is needed
        // later when reading the jokes. If there are no jokes this vector will exist
        // but will be empty
    	if (m_JokesArr == null || m_CategoriesIdHash == null || m_CategoriesArr==null)
    		return;
		int categoryIndex  = m_CategoriesIdHash.size();
		Log.d(TAG, "initFavoriteCategory categoryIndex:"+
				categoryIndex + " FAVORITE_CATEGORY_ID");
    	m_CategoriesIdHash.put( FAVORITE_CATEGORY_ID, categoryIndex );
    	
    	// Add to categories array favorite category
    	CategoryEntry catEntry = new CategoryEntry();
		catEntry.Name = context.getResources().getString(R.string.favorites);
		catEntry.Id = FAVORITE_CATEGORY_ID;
		m_CategoriesArr.add(catEntry);
		
		JokesWebBuffer jokeWebBuffer = new JokesWebBuffer( FAVORITE_CATEGORY_ID, 
					0, 
					null,
					UserId,
					Ordering
					);
		jokeWebBuffer.start();
		m_JokeWebBuffer.add( jokeWebBuffer );
		Log.d(TAG, "initFavoriteCategory m_JokeWebBuffer.size:"+ m_JokeWebBuffer.size());
				
	}
	
	private void initTop10Category(Context context, String UserId, int Ordering)
	{
		// Add an empty vector of favorites jokes, for init, this is needed
        // later when reading the jokes. If there are no jokes this vector will exist
        // but will be empty
    	if (m_JokesArr == null || m_CategoriesIdHash == null || m_CategoriesArr==null)
    		return;
		int categoryIndex  = m_CategoriesIdHash.size();
    	m_CategoriesIdHash.put( TOP_TEN_CATEGORY, categoryIndex );
    	
    	// Add to categories array favorite category
    	CategoryEntry catEntry = new CategoryEntry();
		catEntry.Name = context.getResources().getString(R.string.top_ten);
		catEntry.Id = TOP_TEN_CATEGORY;
		m_CategoriesArr.add(catEntry);
		
		JokesWebBuffer jokeWebBuffer = new JokesWebBuffer( TOP_TEN_CATEGORY, 
					0, 
					null,
					UserId,
					Ordering);
		jokeWebBuffer.start();
		m_JokeWebBuffer.add( jokeWebBuffer );
		Log.d(TAG, "initTop10Category m_JokeWebBuffer.size:"+ m_JokeWebBuffer.size());

	}
	
	private void initRandomJokeCategory(Context context, String UserId, int Ordering)
	{
		// Add an empty vector of favorites jokes, for init, this is needed
        // later when reading the jokes. If there are no jokes this vector will exist
        // but will be empty
    	if (m_JokesArr == null || m_CategoriesIdHash == null || m_CategoriesArr==null)
    		return;
		int categoryIndex  = m_CategoriesIdHash.size();
    	m_CategoriesIdHash.put( RANDOM_JOKE_CATEGORY_ID, categoryIndex );
    	
    	// Add to categories array favorite category
    	CategoryEntry catEntry = new CategoryEntry();
		catEntry.Name = context.getResources().getString(R.string.randon_joke);
		catEntry.Id = RANDOM_JOKE_CATEGORY_ID;
		m_CategoriesArr.add(catEntry);
		
		JokesWebBuffer jokeWebBuffer = new JokesWebBuffer( RANDOM_JOKE_CATEGORY_ID, 
					0, 
					null,
					UserId,
					Ordering);
		jokeWebBuffer.start();
		m_JokeWebBuffer.add( jokeWebBuffer );
		Log.d(TAG, "initRandomJokeCategory m_JokeWebBuffer.size:"+ m_JokeWebBuffer.size());

	//	getNewJokes(RANDOM_JOKE_CATEGORY_ID, NUM_OF_JOKES_TO_READ_IN_LIST, null);
	}
	
	public static void Stop()
	{
		if ( m_JokeWebBuffer == null )
			return;
				
		for (int ind = 0 ; ind < m_JokeWebBuffer.size() ; ind++)
	    {
	    	m_JokeWebBuffer.get(ind).m_KeepRunning = false;
	    }
	}
	
	public void initStaticJokesCategories(Context context, String UserId, int Ordering)
	{
		Log.d(TAG, "+initStaticJokesCategories");
		initFavoriteCategory(context, UserId, Ordering);
		initTop10Category(context, UserId, Ordering);
		initRandomJokeCategory(context, UserId, Ordering);
		//ReadAllJokesFromStorage( ExternalStoragePath  );		
		updateJokesWebBuffers(); // after reading from storage web buffers should know which jokes were read
		Log.d(TAG, "-initStaticJokesCategories");
	}
}
