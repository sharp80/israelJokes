package com.IsraeliJokes;

import com.IsraeliJokes.GlobalsManager.eGlobalResource;

import android.content.Context;
import android.view.Gravity;

public class GlobalGravity{
	
	public static int ToggleGlobalGravity(Context ctxt)
	{
    	if (GlobalsManager.GetGlobalInt(ctxt, eGlobalResource.GRAVITY, Gravity.RIGHT) == Gravity.LEFT )
		{
    		GlobalsManager.SetGlobalInt(ctxt, eGlobalResource.GRAVITY, Gravity.RIGHT);
		}
		else
		{
			GlobalsManager.SetGlobalInt(ctxt, eGlobalResource.GRAVITY, Gravity.LEFT);
		}
    	return GlobalsManager.GetGlobalInt(ctxt, eGlobalResource.GRAVITY, Gravity.RIGHT);
	}
	
	public static int GetGlobalGravity(Context ctxt)
	{
		int defaultValue = Gravity.LEFT;;
		if (IsDynamicGravitySupport(ctxt))
			defaultValue = Gravity.RIGHT;
		if (!ctxt.getResources().getBoolean(R.bool.left_to_right))
			defaultValue = Gravity.RIGHT;
    	return GlobalsManager.GetGlobalInt(ctxt, eGlobalResource.GRAVITY, defaultValue);
	}
	
	public static void SetGlobalInt(Context ctxt, eGlobalResource globalResource, int resVal)
	{
		GlobalsManager.SetGlobalInt(ctxt, eGlobalResource.GRAVITY, resVal);
	}
	
	public static boolean IsDynamicGravitySupport(Context ctxt){
		return ctxt.getResources().getBoolean(R.bool.isDynamicGravitySupport) ;
	}
}
