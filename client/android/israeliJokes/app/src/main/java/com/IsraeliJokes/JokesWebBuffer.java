package com.IsraeliJokes;

import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class JokesWebBuffer extends Thread {
	public boolean m_ComunicationFailure = false;
	private static String TAG = "JokesWebBuffer";
	private  Vector<JokeEntry> m_JokesArr = new Vector<JokeEntry>(10);
	private Vector<JokesHandlerEntry> m_JokesMsgQueue = new Vector<JokesHandlerEntry>(10);
	private String m_CategoryId;
	private int m_MaxNumOfJokes = 0;
	public boolean m_KeepRunning = true;
	private int m_LastJokeId = -1;
	public String m_ExtranlStoragePath;
	private boolean m_FinishedJokesOnServer;
	private String m_UserId;
	private int m_JokesNumToRetrieve = 10;
	
	enum eOrdering
	{
		ORDERING_RANDOM,
		ORDERING_RATING
	};
	private eOrdering m_Ordering = eOrdering.ORDERING_RANDOM;
	private double m_LowestJokeRate;
	private boolean m_BusyInLoop;
	
	JokesWebBuffer( String categoryId, int screenWidth, String StoragePath, String UserId, int Ordering ) {
		m_CategoryId = categoryId;
		m_ExtranlStoragePath = StoragePath;
		m_UserId = UserId;
		m_LowestJokeRate = 9999;
		m_Ordering = eOrdering.values()[ Ordering ];
	}
	private boolean retrieveJokes( Category.eShowJokes showJokes )
	{
		String action ="" ;
		if (m_Ordering == eOrdering.ORDERING_RANDOM)
		{
		}
		else if (m_Ordering == eOrdering.ORDERING_RATING)
		{
			action = "lastRating" ;
		}
		String RandStr ="";
		if (m_CategoryId.equals( Category.RANDOM_JOKE_CATEGORY_ID))
			RandStr = "&Rand=true";
		String newUrl = Category.m_SiteUrl + 
				"/RetrieveFromServerRand.aspx?categoryId=" + 
				m_CategoryId + "&userId=" + m_UserId+ "&lastJokeId=" + m_LastJokeId
				+ "&readType=" + showJokes.ordinal() + "&action=" + action +
				"&LowestRating="+m_LowestJokeRate + RandStr + "&stars=true&jokesNum="+m_JokesNumToRetrieve;
		UpdateServerAsync updateServer = new UpdateServerAsync(null);
		String stringFromServer = updateServer.RetrieveFromServer( newUrl ); 
		m_ComunicationFailure = updateServer.m_ComunicationFailure;
		if (m_ComunicationFailure == true)
			return false;
		int numOfJokesGot = 0;
		if ( CheckIfFinishedJokes( stringFromServer ) == false)
		{
			numOfJokesGot = ParseJsonString( stringFromServer, showJokes == Category.eShowJokes.SHOW_READ_JOKES );
		}
		return CheckIfFinishedJokes( stringFromServer ) || numOfJokesGot < m_JokesNumToRetrieve;
		
	}
	public void InitWebBuffer()
	{
		if (m_JokesMsgQueue!=null)
			m_JokesMsgQueue.removeAllElements();
		m_MaxNumOfJokes = 0;
		m_KeepRunning = true;
		m_FinishedJokesOnServer = false;
		m_LastJokeId = -1;
		m_LowestJokeRate = 9999;
		m_Ordering = eOrdering.ORDERING_RANDOM;
	}
	@Override
	public void run() 
	{
		m_KeepRunning = true;
		m_FinishedJokesOnServer = false;
		int numOfFailures = 0;
		while ( m_KeepRunning )
		{
			m_BusyInLoop = false;
			
			while ( m_JokesArr.size() < m_MaxNumOfJokes && !m_FinishedJokesOnServer )
			{
				if ( m_ComunicationFailure )
				{
					m_ComunicationFailure = false;
					Log.e(TAG, "Communication error. " + numOfFailures + " retries.");
					numOfFailures++;
				}
				else
				{
					numOfFailures = 0;
				}
				if ( numOfFailures > 30 )
				{ // some kind of communication error - freeze this process
					m_MaxNumOfJokes = 0; // clear num of jokes to read
					Log.e(TAG, "stopped retreiving from server after " + numOfFailures + " retries.");
					continue;
				}
				//String newUrl = Category.m_SiteUrl + 
				//		"/RetrieveFromServer.aspx?categoryId=" + 
				//		m_CategoryId + "&lastJokeId=" + m_LastJokeId;
				m_BusyInLoop = true;
				boolean IsFinished = false;
				switch (Category.m_ShowJokes)
				{
					case SHOW_ALL_JOKES:
					{
						IsFinished = retrieveJokes(Category.eShowJokes.SHOW_READ_JOKES );
						boolean IsFinishedTemp = retrieveJokes(Category.eShowJokes.SHOW_UNREAD_JOKES );
						IsFinished = IsFinished && IsFinishedTemp ;
					}
					break;
					case SHOW_READ_JOKES:
					{
						IsFinished = retrieveJokes(Category.eShowJokes.SHOW_READ_JOKES);
					}
					break;
					case SHOW_UNREAD_JOKES:
					{
						IsFinished = retrieveJokes(Category.eShowJokes.SHOW_UNREAD_JOKES);
					}
					break;
				}
				
				m_FinishedJokesOnServer = IsFinished;
				NotifyRegisteredObjs(IsFinished);
				
			}
			try {
				//if (m_ComunicationFailure)
					Thread.sleep(300);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}//while ( m_KeepRunning )
		
	}
	
	private boolean CheckIfFinishedJokes(String strFromServer)
	{
		if (strFromServer == null)
			return true;
		return strFromServer.equals("[]");
	}
	public void SetLastJokeId(int lastJokeId)
	{
		m_LastJokeId = lastJokeId;
	}
	
	int NumOfJokes(  )
	{
		return m_JokesArr.size();
	}
	
	void WaitForJokesRead( Handler dataHandler,  int NumOfJokesToRead )
	{
		JokesHandlerEntry jokeHandlerEntry = new JokesHandlerEntry();
		jokeHandlerEntry.m_Handler = dataHandler;
		jokeHandlerEntry.m_NumOfJokesToRead = NumOfJokesToRead;
		// Add handler to message queue, when the buffer read will read the requested amount of jokes it will
		// signal the reader
		m_JokesMsgQueue.add( jokeHandlerEntry );
		m_MaxNumOfJokes = NumOfJokesToRead;
		//m_FinishedJokesOnServer = false;
	//	m_MaxNumOfJokes = 3;
	}
	
	
	
	int ParseJsonString( String stringFromServer, boolean jokeWasRead )
	{
		int numOfJokesRetrieved = 0;
		if ( stringFromServer == null )
		{
			Log.e(TAG,"ParseJsonString: stringFromServer == null");
			return numOfJokesRetrieved;
		}
		try {
			JSONArray jsonArr = new JSONArray( stringFromServer );
			if (jsonArr.length() == 0)
			{// probably we dont have any joke for this category.. just quite pulling more jokes
				m_MaxNumOfJokes = 0;
				NotifyRegisteredObjs(true);
			}
			numOfJokesRetrieved = jsonArr.length();
			for (int ind=0; ind < jsonArr.length() ; ind++)
			{
				JSONObject json = jsonArr.getJSONObject(ind);
				JokeEntry JokeEntry = new JokeEntry();
				String JokeId = json.getString("id")
						.replace("&quot;", "\"")
	           			.replace("&#39;", "'")
	           			.replace("&amp;", "&") ;
				JokeEntry.JokeId = Integer.parseInt( JokeId );
				if (m_LastJokeId < JokeEntry.JokeId )
					m_LastJokeId = JokeEntry.JokeId;
				JokeEntry.JokeStr =  json.getString("joke")
						.replace("quot;", "\"")
	           			.replace("#39;", "'")
	           			.replace("#180;", "'")
	           			.replace("&amp;", "&") ;
				JokeEntry.JokeStr = JokeEntry.JokeStr
						.replace("&\"", "\"")
						.replace("&'", "'");
				JokeEntry.JokeTitle =  json.getString("headline")
						.replace("quot;", "\"")
	           			.replace("#39;", "'")
	           			.replace("#180;", "'")
	           			.replace("&amp;", "&") ;
				JokeEntry.JokeTitle = JokeEntry.JokeTitle
						.replace("&\"", "\"")
						.replace("&'", "'");
				JokeEntry.JokePic =  json.getString("pic")
	           			.replace("&#39;", "'")
	           			.replace("&amp;", "&") ;
				JokeEntry.JokeVideo =  json.getString("video")
	           			.replace("&#39;", "'")
	           			.replace("&amp;", "&") ;
				if (  json.has("rating") )
				{
					JokeEntry.LikeRate =  Float.parseFloat( json.getString("rating")
							.replace("&#39;", "'")
							.replace("&amp;", "&")) ;
				}
									
				if (m_LowestJokeRate > JokeEntry.LikeRate )
					m_LowestJokeRate = JokeEntry.LikeRate;
				
				JokeEntry.JokeWasRead = jokeWasRead;
				
				if ( json.has("Fav") )
				{
					String Fav = json.getString("Fav");
					JokeEntry.JokeInFavorite =  !Fav.equals("null");
				}
				else
				{
					JokeEntry.JokeInFavorite =  false;
				}
				if ( m_CategoryId.equals( Category.FAVORITE_CATEGORY_ID) )
				{
					JokeEntry.JokeInFavorite = true;
				}
				
				/*if ( JokeEntry.JokePic.length() > 0 )
				{
					SetNewJokeImage( JokeEntry );
				}*/
				
				JokeEntry.JokeCategory = m_CategoryId; 
				AddJokeToArr( JokeEntry );
			//	AddNewJokeToStorage(JokeEntry);
				
				
			}
			
		//	Message msg1 = new Message();
	//		m_AppsInfoHandler.sendMessage(msg1);
		} catch (JSONException e) { 
			Log.e(TAG,"JSON error! category: "+m_CategoryId+" string from server: "+stringFromServer);
			Log.e(TAG,e.getMessage());
			m_ComunicationFailure = true;
		}    	
	//  	String url = m_SiteUrl + "/RetrieveFromServer.aspx?categoryId=" + categoryId;
		return numOfJokesRetrieved;
	}
	private void AddJokeToArr( JokeEntry jokeEntry )
	{
		if ( m_Ordering  == eOrdering.ORDERING_RATING )
		{
			for (int ind = 0 ; ind < m_JokesArr.size() ; ind++)
			{
				if (m_JokesArr.get(ind).LikeRate < jokeEntry.LikeRate )
				{
					m_JokesArr.add(ind, jokeEntry);
					return;
				}
			}
		}
		
		m_JokesArr.add( jokeEntry );
		
	}
	static public String DecodeStr(String input)
	{
		String output;
		output = input
				.replace("\"" , "quot;")
				.replace("'" , "#39;" )
				.replace("&", "&amp;") ;
		return output;
	}
	static public String EncodeStr(String input)
	{
		String output;
		output = input
				.replace( "quot;", "\"" )
				.replace( "#39;" , "'"  )
				.replace( "&amp;", "&"	) ;
		return output;
	}
	
	
	void SetNewJokeImage(JokeEntry jokeEntry)
	{
	//	jokeEntry.JokeBmap = GetBitMap ( jokeEntry.JokePic );
		//Bitmap JokeBmap = GetBitMap ( jokeEntry.JokePic );
		//SaveImageOnPhone( jokeEntry.JokeBmap, jokeEntry.JokePic );
		//SaveImageOnPhone( JokeBmap, jokeEntry.JokePic );
	}
	 
	static boolean IsExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        return true;
	    } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return false;
	    } else {
	    	return false;
	    }
	}
	
	
	
	
	public JokeEntry GetJoke()
	{
		return m_JokesArr.remove(0);
	}

	
	void NotifyRegisteredObjs(boolean forceNotification)
	{
		for (int ind = 0 ; ind < m_JokesMsgQueue.size() ; ind++ )
		{
			JokesHandlerEntry hndEntry = m_JokesMsgQueue.get( ind );
			if ( hndEntry.m_NumOfJokesToRead <= m_JokesArr.size() || forceNotification)
			{ // notify handler
				
				Message msg1 = new Message();
				hndEntry.m_Handler.sendMessage(msg1);
			}
		}
		m_JokesMsgQueue.removeAllElements();
	}
	
	
	public void ChangeOrdering( eOrdering ordering )
	{
		if (m_Ordering != ordering)
		{
			m_Ordering = ordering;
			while (m_BusyInLoop)
			{
				
			}
			if ( ordering == eOrdering.ORDERING_RATING)
			{
				m_LowestJokeRate = 9999;
			}
			// remove all buffered jokes, so new jokes ordered by the
			// new ordering will be collected
			m_MaxNumOfJokes = 0;
			m_FinishedJokesOnServer = false;
			m_JokesArr.removeAllElements();
			
		}
	}
	public boolean GetFinishedJokesOnServer()
	{
		return m_FinishedJokesOnServer;
	}
}



