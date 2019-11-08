package com.IsraeliJokes;

import android.content.Context;
import android.content.SharedPreferences;

public class GlobalsManager{
	private static String GLOBAL_GRAVITY_FILE_NAME = "GlobalGravity";
	private static String GLOBAL_GRAVITY_STRING = "GlobalGravity";
	private static String GLOBAL_ORDER_BY_STRING = "GlobalOderBy";
	private static String GLOBAL_LAST_MESSAGE_STRING = "GlobalLastMessageRead";
	public enum eGlobalResource
	{
		GRAVITY,
		ORDER_BY,
		OFFLINE_MODE,
		LAST_MESSAGE,
		NUM_OF_RESOURCES
	};
	public static boolean m_GetFromStorage[] ;
	private static String[] m_GlobalStringArr;
	private static int[] m_IntValuesArr;
	
	static{
		m_GetFromStorage = new boolean[ eGlobalResource.NUM_OF_RESOURCES.ordinal() ];
		m_GlobalStringArr = new String[  eGlobalResource.NUM_OF_RESOURCES.ordinal() ];
		m_IntValuesArr = new int[ eGlobalResource.NUM_OF_RESOURCES.ordinal() ];
		for (int ind = 0 ; ind < m_GlobalStringArr.length ; ind++ )
		{
			m_GetFromStorage[ind] = true;
			m_GlobalStringArr[ind] = null;
			m_IntValuesArr[ind] = -1;
		}
		m_GlobalStringArr[ eGlobalResource.GRAVITY.ordinal() ] = GLOBAL_GRAVITY_STRING;
		m_GlobalStringArr[ eGlobalResource.ORDER_BY.ordinal() ] = GLOBAL_ORDER_BY_STRING;
		m_GlobalStringArr[ eGlobalResource.LAST_MESSAGE.ordinal() ] = GLOBAL_LAST_MESSAGE_STRING;
	}

	
	public static int GetGlobalInt(Context ctxt, eGlobalResource globalResource, 
			int defaultValue)
	{
		if (m_GlobalStringArr[globalResource.ordinal()] == null )
			return -1;
		try
		{
			if (m_GetFromStorage[globalResource.ordinal()])
			{ // read from storage only on boot, or after change of gravity
				SharedPreferences settings = ctxt.getSharedPreferences( GLOBAL_GRAVITY_FILE_NAME , 0);
	        
				m_IntValuesArr[globalResource.ordinal()] = settings.getInt(m_GlobalStringArr[globalResource.ordinal()] , defaultValue);
				m_GetFromStorage[globalResource.ordinal()] = false;
			}
			return m_IntValuesArr[globalResource.ordinal()];
		}
		catch( NullPointerException e)
		{
			return defaultValue;
		}
   }
	
	public static void SetGlobalInt(Context ctxt,eGlobalResource globalResource, int resVal)
	{
		if (m_GlobalStringArr[globalResource.ordinal()] == null )
			return;
		try
		{
			m_GetFromStorage[globalResource.ordinal()] = true;// next time we will read it will be from memory
			SharedPreferences settings = ctxt.getSharedPreferences( GLOBAL_GRAVITY_FILE_NAME , 0);
	         
			SharedPreferences.Editor editor = settings.edit();
			editor.putInt( m_GlobalStringArr[globalResource.ordinal()], resVal );
	
			// Commit the edits!
			editor.commit();
		}
		catch( NullPointerException e)
		{
			return ;
		}
	}
	
}
