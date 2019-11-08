package com.IsraeliJokes;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.IsraeliJokes.GlobalsManager.eGlobalResource;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

//import com.inneractive.api.ads.InneractiveAd;

public class CategoriesPage  extends Activity{
	private ProgressDialog m_ProgressDialog = null;
	private static int NUM_OF_CATEGORIES_IMAGES_PER_ROW = 2;
	private static int DEFAULT_NUM_OF_JOKES = 5;
	private Handler m_postGetCategoriesHandler;
	private Category m_Categories;
	public static DBAdapter m_FileDb = null; 
	public static String m_ExternalStoragePath;
	private String TAG = "CategoriesPage";
	public String m_PackageName;
	private String m_AppMarketLink ;
	public static String m_UserId;
	private WebView mWebView ;
	private Vector<TableRow> m_TableRowsVector = new Vector<TableRow>(10);
	private boolean m_AlboAds = false;
	private boolean m_OfflineModeEnabled = false;
	private boolean m_ComErrDlg = false;
	public static AdRequest m_Request;
	private AdView m_AdView;
	public static String m_YouTubePrefix = null;
	public static String m_YouTubePrefixForFacebook = null;
	public static final int m_AppId = 0;
	public static int m_VersionNum = 0;
	public static String ImagesUrlPrefix;
	public static String IconsUrlPrefix;
	
	public void onDestroy() {
	    super.onDestroy();

	    if (m_AdView != null) {       
	    	m_AdView.destroy();     
	    }
	    
	    Category.Stop();
	    if (m_FileDb != null )
	    {
	    	try{
	    		m_FileDb.close();
	    	}
	    	finally{}
	    }
	    /*
	     * Notify the system to finalize and collect all objects of the
	     * application on exit so that the process running the application can
	     * be killed by the system without causing issues. NOTE: If this is set
	     * to true then the process will not be killed until all of its threads
	     * have closed.
	     */
	    System.runFinalizersOnExit(true);

	  
	}
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
	
	@Override
    public void onBackPressed()
    {
    	AlertDialog alertDialog = new AlertDialog.Builder(this).create();
    	alertDialog.setTitle(getString(R.string.close));
    	alertDialog.setMessage(getString(R.string.close_app_msg));
    	alertDialog.setIcon(R.drawable.monkey);
    	alertDialog.setButton(AlertDialog.BUTTON_POSITIVE,getString(R.string.yes),
    			new DialogInterface.OnClickListener() {
    	   public void onClick(DialogInterface dialog, int which) {
    	       finish();
    	   }
    	});
    	alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,getString(R.string.no)
    			, new DialogInterface.OnClickListener() {
     	   public void onClick(DialogInterface dialog, int which) {
     	   }
     	});
    	alertDialog.show();
    }
	
	@Override
    public void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
        setContentView( R.layout.categories_page );
        
        GlobalsManager global = new GlobalsManager();
        global.SetGlobalInt( CategoriesPage.this, 
							 eGlobalResource.OFFLINE_MODE, 
							 1 );
 		
        if ( m_OfflineModeEnabled == false )
        {
        	if ( CheckIfWebConencted() == false )
        		return;
        }
        
        ShowHashKey();

    //    AppsFlyerLib.sendTracking(this,"Dev_Key");
        
        try {
			PackageInfo  pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			m_VersionNum = pinfo.versionCode;

		} catch (NameNotFoundException e) {
			Log.e(TAG,e.toString());
		}
		
        m_PackageName = getApplicationContext().getPackageName();
        m_AppMarketLink = "market://details?id="+m_PackageName;
        // init DB
        m_FileDb =  new DBAdapter(this);
        m_FileDb.open();
        
      //  InitExternalStoragePath();
        if ( m_OfflineModeEnabled == false )
        {
        	LoadAdds();
        }
        GetAllCategories();
        
        Button favoritesBtn = (Button)findViewById(R.id.GoToFavorites);
        favoritesBtn.setOnClickListener
	    ( 
	    	new OnClickListener() 
	    	{
				public void onClick(View v) 
				{
					Intent i = new Intent(CategoriesPage.this, Category.class);
					i.putExtra( "CategoryId", Category.FAVORITE_CATEGORY_ID);
					startActivity(i);  
				}
	    	}
	    );
        
        
        Button RandomJoke = (Button)findViewById(R.id.RandomJoke);
        RandomJoke.setOnClickListener
        ( 
	    	new OnClickListener() 
	    	{
				public void onClick(View v) 
				{
					Intent i = new Intent(CategoriesPage.this, Category.class);
		            i.putExtra( "CategoryId", Category.RANDOM_JOKE_CATEGORY_ID );
		            i.putExtra( "RandomJoke" , true );
		              
		            startActivity(i);  
				}
	    	}
	    );
        
        Button TopTen = (Button)findViewById(R.id.TopTen);
        TopTen.setOnClickListener
        ( 
	    	new OnClickListener() 
	    	{
				public void onClick(View v) 
				{
					Intent i = new Intent(CategoriesPage.this, Category.class);
					i.putExtra( "CategoryId", Category.TOP_TEN_CATEGORY );
					i.putExtra( "RandomJoke" , false );
					startActivity(i);  
					return ;
				}
	    	}
	    );
        
        Button add_joke = (Button)findViewById(R.id.AddJoke);
        add_joke.setOnClickListener
        ( 
	    	new OnClickListener() 
	    	{
				public void onClick(View v) 
				{
					Intent i = new Intent( CategoriesPage.this, RegisterNewJoke.class );
		            startActivity(i);  
				}
	    	}
	    );
        
        ImageView ReviewUs = (ImageView)findViewById(R.id.ReviewUs);
        ReviewUs.setOnClickListener
	    ( 
	    	new OnClickListener() 
	    	{
				public void onClick(View v) 
				{
					Intent i = new Intent(Intent.ACTION_VIEW).setData( Uri.parse(m_AppMarketLink) );
					startActivity( i ); 	  
				}
	    	}
	    );
        
        ShowMessages();
 	}
	
	public void ShowHashKey()
	{
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo("com.IsraeliJokes", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encodeBytes(md.digest()));
				Log.e("hash key", something);
			} 
		}
		catch (NameNotFoundException e1) {
			Log.e("name not found", e1.toString());
		}

		catch (NoSuchAlgorithmException e) {
			Log.e("no such an algorithm", e.toString());
		}
		catch (Exception e){
			Log.e("exception", e.toString());
		}
	}
	
	class GetMessages extends AsyncTask<String, Void, String> {
	 	Context m_Context;
	 	GetMessages(Context ctx)
	 	{
	 		m_Context = ctx;
	 	}
	 	@Override
	    protected String doInBackground(String... params) {
	        String fromServer = null;
	        for (int retry = 0 ; retry < 3 && fromServer == null ; retry++ )
	        {
	        	UpdateServerAsync updateServer = new UpdateServerAsync(null);
	        	fromServer = updateServer.RetrieveFromServer( params[0] );
	        	if (fromServer == null)
	        		Log.e(TAG,"GetMessages: retry:"+ retry);
	        }
	        return fromServer;
	    }
	 	@Override
	 	protected void onPostExecute(String result) {
	 		if (result != null)
	 		{
	 			try {
					JSONArray jsonArr 	= new JSONArray( result );
						ShowSingleMessage(jsonArr, 0);
					}
				 catch (JSONException e) {
					Log.e(TAG,e.toString());
				}
     			
	 		}
	 	}
	 	
	 	private void ShowSingleMessage(final JSONArray jsonArr, final int curInd) 
		{
	 		if (jsonArr.length() <= curInd)
	 			return;
	 		try
	 		{
	 		JSONObject jsonObj = jsonArr.getJSONObject(curInd);
			final int MsgId = jsonObj.getInt("MsgId");
			String msg = jsonObj.getString("MsgStr");
			String title = jsonObj.getString("MsgTitle");
	 		
			AlertDialog alertDialog = new AlertDialog.Builder(m_Context).create();
	    	alertDialog.setTitle(title);
	    	alertDialog.setMessage(msg);
	    	
	    	alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
	    			getString(R.string.close_and_dont_show),
	    			new DialogInterface.OnClickListener() {
	     	   public void onClick(DialogInterface dialog, int which) {
	     		   	GlobalsManager.SetGlobalInt( m_Context, eGlobalResource.LAST_MESSAGE, MsgId);
					dialog.dismiss();
					int nextInd = curInd+1;
		    		ShowSingleMessage(jsonArr, nextInd);
	     	   }
	     	});
	    	alertDialog.show();
	 		}
 		 catch (JSONException e) {
				Log.e(TAG,e.toString());
			}
	 		
		}
	}
	
	private void ShowMessages()
	{
    	int lastMessageRead = GlobalsManager.GetGlobalInt(this, eGlobalResource.LAST_MESSAGE, -1);
    	String url = Category.m_SiteUrl + "/GetUserMsg.aspx?lastMsg=" + lastMessageRead + 
    				"&ApiVer=" + Build.VERSION.SDK_INT +
    				"&AppId=" + m_AppId +
    				"&AppVer=" + m_VersionNum+ 
    				"&uid=" + m_UserId; 
    	GetMessages getMsg = new GetMessages(this);
    	getMsg.execute(url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_menu, menu);
	    return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menu_highscore:
	        	Intent i = new Intent(CategoriesPage.this, Category.class);
				i.putExtra( "CategoryId", Category.TOP_TEN_CATEGORY );
				i.putExtra( "RandomJoke" , false );
				startActivity(i);  
	            return true;
	        case  R.id.menu_text_align_change:
	        	if (GlobalsManager.GetGlobalInt(this, eGlobalResource.GRAVITY, Gravity.RIGHT) == Gravity.LEFT )
				{
	        		GlobalsManager.SetGlobalInt(this, eGlobalResource.GRAVITY, Gravity.RIGHT);
				}
				else
				{
					GlobalsManager.SetGlobalInt(this, eGlobalResource.GRAVITY, Gravity.LEFT);
				}
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	private void LoadAdds()
	{
		GetConfiguration postServer = new GetConfiguration();
		postServer.execute(Category.m_SiteUrl + "/GetConfiguration.aspx?appId="+m_AppId+"&version="+m_VersionNum);
	}

class GetConfiguration extends AsyncTask<String, Void, String> {
 	@Override
    protected String doInBackground(String... params) {
        String fromServer = null;
        for (int retry = 0 ; retry < 3 && fromServer == null ; retry++ )
        {
        	UpdateServerAsync updateServer = new UpdateServerAsync(null);
        	fromServer = updateServer.RetrieveFromServer( params[0] );
        	if (fromServer == null)
        		Log.e(TAG,"GetConfiguration: retry:"+ retry);
        }
        return fromServer;
    }
 	@Override
 	protected void onPostExecute(String result) {
 		super.onPostExecute(result);
 		mWebView = (WebView) findViewById(R.id.WebView);
		if (result == null)
		{
			mWebView.setVisibility( View.GONE );
 			m_AlboAds = false;
			ImagesUrlPrefix = Category.m_SiteUrl + "/images/imagesStack/";
			IconsUrlPrefix = Category.m_SiteUrl + "/images/icons/";
 			LoadAdMob();
			//LoadInneractive();
			
 			return;
		}
		JSONObject json;
		try {
			json = new JSONObject(result);
			
			if (json.get("AlboAds").equals("true"))
			{
	 			m_AlboAds = true;
				mWebView.getSettings().setJavaScriptEnabled(true);
				String locale = getResources().getConfiguration().locale.getDisplayName();
				String AlboAdditionalApi = "";
				if (json.has("AlboAdditionalApi") )
				{
					AlboAdditionalApi = json.getString("AlboAdditionalApi");
				}
				
				String url = "http://banners.albos.co.il/ban.aspx?type=9&locale=" + locale +
						"&identifier="+m_UserId + "&appID=11" + AlboAdditionalApi;
				mWebView.loadUrl(url);
				m_AdView.setVisibility(View.GONE);
			}
	 		else
	 		{
	 			mWebView.setVisibility( View.GONE );
	 			m_AlboAds = false;
	 			LoadAdMob();
	 			//LoadInneractive();
	 		}
			if ( json.has("YoutubPrefix") )
	 		{
	 			m_YouTubePrefix = json.getString("YoutubPrefix") ;
	 		}
			if ( json.has("YoutubPrefixForFacebook") )
	 		{
				m_YouTubePrefixForFacebook = json.getString("YoutubPrefixForFacebook") ;
	 			//Log.d(TAG, YouTubePrefix);
	 		}
			
			if ( json.has("SiteUrlPrefix") )
			{
				Category.m_SiteUrl =  json.getString("SiteUrlPrefix");
			}
			
			if ( json.has("ImagesUrlPrefix") )
			{
				ImagesUrlPrefix =  json.getString("ImagesUrlPrefix");
			}
			else
			{
				ImagesUrlPrefix = Category.m_SiteUrl + "/images/imagesStack/";
			}
			
			if ( json.has("IconsUrlPrefix") )
			{
				IconsUrlPrefix =  json.getString("IconsUrlPrefix");
			}
			else
			{
				IconsUrlPrefix = Category.m_SiteUrl + "/images/icons/";
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			Log.e(TAG,e.toString());
		}
		
		
		// code of versions<=8
 		if ( result.contains("AlboAds=true;") )
		{
 			m_AlboAds = true;
			mWebView.getSettings().setJavaScriptEnabled(true);
			String locale = getResources().getConfiguration().locale.getDisplayName();
			String AlboAdditionalApi = "";
			if (result.contains("AlboAdsAddionalApi=") )
			{
				int startInd =  result.indexOf("AlboAdsAddionalApi=");
				int endInd = result.indexOf(";", startInd);
				AlboAdditionalApi = result.substring(startInd +"AlboAdsAddionalApi=".length() , endInd);
			}
			
			String url = "http://banners.albos.co.il/ban.aspx?type=9&locale=" + locale +
					"&identifier="+m_UserId + "&appID=11" + AlboAdditionalApi;
			mWebView.loadUrl(url);
			AdView ad = (AdView)findViewById(R.id.adView);
			ad.setVisibility(View.GONE);
		}
 		
 		else
 		{
 			mWebView.setVisibility( View.GONE );
 			m_AlboAds = false;
 		}
	}//onPostExecute
}
	public void LoadInneractive()
	{
		String AppId = "mayaron_euro_Android";
		LinearLayout adLayout = (LinearLayout)findViewById(R.id.adView);
	//	InneractiveAd.displayAd(this, adLayout , AppId, InneractiveAd.IaAdType.Banner, 20  );
	}
	public void LoadAdMob()
	{
		 // Create the adView     
		/*m_AdView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID );     
		// Lookup your LinearLayout assuming it�s been given    
		// the attribute android:id="@+id/mainLayout"    
		LinearLayout layout = (LinearLayout)findViewById(R.id.adView);      
		// Add the adView to it     
		layout.addView(m_AdView);      // Initiate a generic request to load it with an ad   
		m_Request = new AdRequest();
		m_Request.addTestDevice(AdRequest.TEST_EMULATOR); 

		m_AdView.loadAd(m_Request); */
		AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
	}
	
	private boolean CheckIfWebConencted()
	{
		boolean wifi = isUsingWiFi();
		boolean internet_b = isUsingMobileData();
		if (!wifi && ! internet_b )
		{
			CommunicationErrorDialogShow(this);
		}
		return (wifi || internet_b);
	}
	void CommunicationErrorDialogShow(Context cntx)
	{
		if ( m_ComErrDlg  == false)
		{
			m_ComErrDlg = true;
		}
		else
		{
			return;
		}
		if (m_ProgressDialog != null )
		{
			if ( m_ProgressDialog.isShowing() )
				m_ProgressDialog.dismiss();
		}
		AlertDialog alertDialog = new AlertDialog.Builder(cntx).create();
		alertDialog.setTitle(getString(R.string.network_inactive));
		alertDialog.setMessage(getString(R.string.reconnect_network));
		alertDialog.setButton(getString(R.string.exit), 
				new DialogInterface.OnClickListener() {
			   public void onClick(DialogInterface dialog, int which) {
				   finish();
			   }
			});
	
		alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
	}
	public boolean isUsingWiFi() {
	    ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	    NetworkInfo wifiInfo = connectivity
	            .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

	    if (wifiInfo.getState() == NetworkInfo.State.CONNECTED
	            || wifiInfo.getState() == NetworkInfo.State.CONNECTING) {
	        return true;
	    }

	    return false;
	}
	
	public boolean isUsingMobileData() {
		try{
		    ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	
		    NetworkInfo mobileInfo = connectivity
		            .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	
		    if (mobileInfo.getState() == NetworkInfo.State.CONNECTED
		            || mobileInfo.getState() == NetworkInfo.State.CONNECTING) {
		        return true;
		    }
	
		    return false;
		}
		catch (NullPointerException e)
		{
			Log.e(TAG,e.toString());
			return false;
		}
	}
	
	
	private void ShowNewCategory(CategoryEntry categoryEntry, int index)
	{
		if (categoryEntry.Id == Category.TOP_TEN_CATEGORY ||
			categoryEntry.Id == Category.FAVORITE_CATEGORY_ID)
			return;
		View categoryView = CreateCategoryView( categoryEntry );
		AddCategoryView( categoryView , index);
	}
	
	private View CreateCategoryView( final CategoryEntry categoryEntry )
	{
		ImageView categoryBtn = new ImageView(this);
		
		categoryBtn.setOnClickListener
	    ( 
	    	new OnClickListener() 
	    	{
				public void onClick(View v) 
				{
					Intent i = new Intent(CategoriesPage.this, Category.class);
					i.putExtra( "CategoryId", categoryEntry.Id );
					i.putExtra( "RandomJoke" , false );
					startActivity(i);  
				}
	    	}
	    );
		
		//Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay(); 
		//int width = display.getWidth(); 
		categoryBtn.setPadding( 0, 10, 0, 5);
		categoryBtn.setImageBitmap( categoryEntry.CatBitmap );
		//categoryBtn.setBackgroundColor(Color.BLACK);

		LinearLayout container = new LinearLayout(this);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setGravity(Gravity.CENTER);
		container.addView(categoryBtn);
		TextView textView = new TextView(this);
		textView.setText(categoryEntry.Name);
		textView.setGravity(Gravity.CENTER);
		container.addView(textView);
		return container;
	}
	
	private void AddCategoryView(View categoryView, int index)
	{
		TableRow row;
		//First find the Correct table row
		if (index % NUM_OF_CATEGORIES_IMAGES_PER_ROW == 0 )
		{// first category in this row, create new table row
			row = AddNewRow();
		}
		else
		{
			row = GetTableRow( index / NUM_OF_CATEGORIES_IMAGES_PER_ROW );

		}
		if (row != null)
		{
			row.setGravity( Gravity.CENTER );
			TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams
					  	(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
			//tableRowParams.gravity = Gravity.CENTER_HORIZONTAL;
			row.setLayoutParams(tableRowParams);
			row.addView( categoryView );
		}
	}
	
	private TableRow GetTableRow(int rowNumber)
	{
		if ( m_TableRowsVector.size() <= rowNumber)
	    {
	    	return null;
	    }
		return m_TableRowsVector.get(rowNumber);
		  
	}
	private TableRow AddNewRow()
	{
	    TableRow tr = new TableRow(this);
        //tr.setWeightSum(1);
        TableLayout table = (TableLayout)findViewById(R.id.categoriesTable);
        
    	table.addView(tr);
    	m_TableRowsVector.add(tr);
    	return tr; 
	}
	
	
	public void initGetCategoriesHandler( final String userId )
	{
		m_postGetCategoriesHandler = new Handler()	
		{
			@Override public void handleMessage(Message msg) {
				if (longTimer != null)
				{
					longTimer.cancel();
					longTimer = null;
				}
				if ( m_OfflineModeEnabled == false )
				{
					if (msg.arg1 == -1 || Category.m_CategoriesArr == null)
					{
						Log.e(TAG,"initGetCategoriesHandler - got msg arg:" + msg.arg1   );
						m_ProgressDialog.dismiss();
						CommunicationErrorDialogShow(CategoriesPage.this);
						return;
					}
				}
				
		        if (m_OfflineModeEnabled == false)
		        {
					for ( int ind = 0 ; ind < Category.m_CategoriesArr.size() - 1 ; ind++ )
					{ // last category is favorite, which we already show hard coded
						ShowNewCategory(Category.m_CategoriesArr.get(ind), ind);
						/*m_Categories.GetNewJokes( Category.ConvertCategoryIndexToId(ind), 
													DEFAULT_NUM_OF_JOKES, 
													null );*/
					}
		        }
		        if (m_ProgressDialog.isShowing())
		        	m_ProgressDialog.dismiss();
			}
		};
	}
	public void GetAllCategories()
	{
		m_ProgressDialog = ProgressDialog.show(	this, 
				getResources().getString(R.string.loading), 
				"", 
				true,
				false
				);
		m_ProgressDialog.show();
		m_UserId = GetUserId();

		m_Categories = new Category();

		Handler getCategoriesFailedHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				CommunicationErrorDialogShow(CategoriesPage.this);
				return;
			}
		};
        if ( m_OfflineModeEnabled == false )
        {
        	setupLongTimeout( 50000, getCategoriesFailedHandler );
        }
        
		initGetCategoriesHandler( m_UserId );
		InitCategories initCategories = new InitCategories( this );
		initCategories.execute();
	}
	
	
	class InitCategories extends AsyncTask<Void, Void, Integer> {
		private Context m_Ctxt;
		
		InitCategories(Context a_Context)
		{
			m_Ctxt = a_Context;
		}
		
		@Override
	    protected Integer doInBackground(Void... params) {
			//Starting new user sessions to clean all old session
	 		if (startNewUserSession() == -1)
	 			return -1;
	 		
	 		//Starting all categories
	 		WindowManager winman = getWindowManager();
			int screenWidth = 0; 
			if ( winman  != null)
			{
				Display display = winman.getDefaultDisplay(); 
				screenWidth = display.getWidth();
			}
	 		int ordering = GlobalsManager.GetGlobalInt(CategoriesPage.this, eGlobalResource.ORDER_BY, 0);
			m_Categories.getAllCategories( m_postGetCategoriesHandler, 
											screenWidth, 
											m_ExternalStoragePath, 
											m_Ctxt,
											m_UserId,
											ordering);
			return 0;
	    }
	 	
		private int startNewUserSession() {
			String url = Category.m_SiteUrl + "/NewUserSession.aspx?userId=" + m_UserId;
			int retry  = 0 ;
			for (; retry < 3 ; retry++ )
			{
	        	UpdateServerAsync updateServer = new UpdateServerAsync(null);
				String res = updateServer.RetrieveFromServer(url);
				if ( res != null)
				{
					if ( res.equals("OK") == true )
						break;
				}
				else
				{
					Log.e( TAG , "GetAllCategories error: retry:"+retry);
				}
			}
			if (retry == 3 && m_OfflineModeEnabled == false)
			{
				Log.e( TAG , "New User Session could not be reached");
				return -1;
			}
			return 0;
		}
		
	 	@Override
	 	protected void onPostExecute(Integer result) {
	 		if (result == -1)
	 		{
				CommunicationErrorDialogShow(m_Ctxt);
	 		}
	 	}
	}

	private Timer longTimer = null;
	private synchronized void setupLongTimeout(long timeout, final Handler timeoutHandler) {
	  if(longTimer != null) {
	    longTimer.cancel();
	    longTimer = null;
	  }
	  if(longTimer == null) {
	    longTimer = new Timer();
	    longTimer.schedule(new TimerTask() {
	      public void run() {
	        longTimer.cancel();
	        longTimer = null;
	        //do your stuff, i.e. finishing activity etc.
	    	Log.e(TAG,"TIMEOUT!!!");
	    	timeoutHandler.sendEmptyMessage(0);
	    	
	      }
	    }, timeout /*delay in milliseconds i.e. 5 min = 300000 ms or use timeout argument*/);
	  }
	}
	
	private String GetUserId()
	{
		DeviceUuidFactory uuid = new DeviceUuidFactory(this);
		UUID deviceId = uuid.getDeviceUuid();
	    return deviceId.toString();
	}
	
}
