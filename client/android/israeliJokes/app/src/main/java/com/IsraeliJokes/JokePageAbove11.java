package com.IsraeliJokes;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class JokePageAbove11 extends FragmentActivity {
	private String m_CategoryId;
	public JokeEntry m_JokeEntry; 
	private boolean m_IsFavoriteCategory = false;
	//private Handler m_RandomJokeHandler;
	private static final String TAG = "JokePage";
	private WebView m_JokeVideo = null;

	public ProgressBar m_Progress;
	JokesPagerAdapter m_JokePagerAdapter;
	private final int m_resultCode = 100;
	private String m_fileFullPath;
	private AlertDialog m_alert = null;

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(JokePageAbove11.this, Category.class);
		i.putExtra( "CategoryId", m_CategoryId);
		i.putExtra( "BackFromJokePage" , true);

		startActivity(i);  
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (m_JokeVideo != null)
			m_JokeVideo.destroy();
		ImageView categoriesImage = (ImageView)findViewById( R.id.ToCategory );
		categoriesImage.destroyDrawingCache();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_highscore:
			Intent i = new Intent(this, Category.class);
			i.putExtra( "CategoryId", Category.TOP_TEN_CATEGORY );
			i.putExtra( "BackFromJokePage" , true);
			startActivity(i);  
			finish();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView( R.layout.joke_page);

		m_JokeEntry 	= null;
		Intent intent 	= getIntent();
		m_CategoryId 	= intent.getStringExtra( "CategoryId"  );
		int jokeId	 	= intent.getIntExtra("JokeId", -1);
		if (jokeId == -1)
			finish();
		m_JokeEntry = Category.getJoke(m_CategoryId, jokeId);

		initPager();

		m_IsFavoriteCategory = m_CategoryId.equals( Category.FAVORITE_CATEGORY_ID );

		ImageView categoriesImage = (ImageView)findViewById( R.id.ToCategory );

		if( m_IsFavoriteCategory || m_CategoryId.equals( Category.RANDOM_JOKE_CATEGORY_ID ) ) 
		{
			Drawable d = getResources().getDrawable( R.drawable.random_icon );
			categoriesImage.setImageDrawable(d );
			android.view.ViewGroup.LayoutParams params = categoriesImage.getLayoutParams();
			Resources r = getResources();
			params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
			categoriesImage.setLayoutParams(params);
		}
		else if ( m_CategoryId.equals(Category.TOP_TEN_CATEGORY) )
		{
			Drawable d = getResources().getDrawable( R.drawable.top10thumb );
			categoriesImage.setImageDrawable(d );
			android.view.ViewGroup.LayoutParams params = categoriesImage.getLayoutParams();
			Resources r = getResources();
			params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, r.getDisplayMetrics());
			params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 60, r.getDisplayMetrics());
			categoriesImage.setLayoutParams(params);
		}

		else if (m_CategoryId != null)
		{
			int catInd = Category.ConvertCategoryIdToIndex(m_CategoryId);
			if (catInd == -1 )
			{
				this.finish();
				return;
			}
			Bitmap catBmp = Category.m_CategoriesArr.get(catInd).CatBitmapThumb;
			if (catBmp != null)
			{
				categoriesImage.setImageBitmap(catBmp);
			}
		}

		Button ToCategories = (Button)findViewById( R.id.ToCategories  ); 
		ToCategories.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		categoriesImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(JokePageAbove11.this, Category.class);
				i.putExtra( "CategoryId", m_CategoryId);
				i.putExtra( "BackFromJokePage" , true);
				startActivity(i);  
				finish();
			}
		});

		Button addToFavoritesBtn = (Button)findViewById( R.id.addToFavorites ); 
		addToFavoritesBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// add/remove joke to favorite
				AddFavoritesButtonHandler();
			}
		});


		Button goNext = (Button)findViewById( R.id.goBack ); 
		goNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DisplayPrevJoke() ;
			}
		});
		Button goPrev = (Button)findViewById( R.id.goNext ); 
		goPrev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DisplayNextJoke();
			}
		});

		Button ReportJokeBtn = (Button)findViewById( R.id.ReportJokeBtn );
		ReportJokeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String TitleString = getString( R.string.ReportPrbolemTitle );
				String ContentString = getString( R.string.ReportContent );
				OpenDialog( v, TitleString, ContentString );
			}
		});

		Button share = (Button)findViewById( R.id.share );
		share.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				OpenShareDialog(v);
			}
		});


		UpdateButtons( m_JokeEntry );
		
		LoadAdMob();
	}
	
	public void LoadAdMob()
	{
		AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
	}

	public void UpdateButtons(JokeEntry jokeEntry)
	{
		if ( jokeEntry == null)
			return;
		CheckButtonsVisability( jokeEntry );
		UpdateAddLikeBtn( jokeEntry );
		UpdateFavoriteBtn( jokeEntry.JokeInFavorite,jokeEntry.JokeCategory  );
		//	UpdateAlignmentIcon();

	}

	@SuppressLint("NewApi")
	private void initPager()
	{
		Handler newPageHandler = new Handler()
		{
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				int showableJokeInd= msg.arg1;
				JokeEntry joke = GetShowableJoke(showableJokeInd);
				m_JokeEntry = joke;
				UpdateButtons( joke );
				if ( joke!=null && joke.JokeWasRead == false)
					UpdateJokeRead(joke, CategoriesPage.m_UserId);
			}
		};
		m_JokePagerAdapter = new JokesPagerAdapter( getFragmentManager(), 
				m_CategoryId, 
				Category.getNumOfShowableCategoryJokes(m_CategoryId),
				newPageHandler);
		final ViewPager jokePager = (ViewPager) findViewById( R.id.jokespager );
		jokePager.setAdapter( m_JokePagerAdapter );
		int showableIndex = GetShowableIndex(m_JokeEntry.JokeId);
		jokePager.setCurrentItem(showableIndex); // this is the only way i know to change the first item

	}



	public void UpdateShareBtn( boolean enable )
	{
		if (m_JokeEntry == null)
		{
			Log.e(TAG, "UpdateFavoriteBtn - m_JokeEntry is null!");
			return;
		}
		Button shareBtn = (Button)findViewById( R.id.share ); 
		Drawable d = null;
		if ( !enable )
		{ 
			d = getResources().getDrawable(R.drawable.rss128_disable);
		}
		else
		{ 
			d = getResources().getDrawable(R.drawable.rss128);
		}

		shareBtn.setCompoundDrawablesWithIntrinsicBounds(null,d,null,null);
	}
	private void OpenShareDialog( View v )
	{
		final Dialog fb_dialog  = new Dialog( v.getContext(), R.style.WrongAnswerDialog);

		LayoutInflater inflater = (LayoutInflater) v.getContext()
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View layout = inflater.inflate( R.layout.share_dialog , null );
		fb_dialog.addContentView( layout, new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.WRAP_CONTENT ));

		/* TextView faceBookShareText = (TextView) fb_dialog.findViewById(R.id.facebook_share_button);
		faceBookShareText.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						Intent i = new Intent(JokePageAbove11.this, FacebookHandler.class);
						i.putExtra("CategoryId",m_CategoryId  );
						i.putExtra("JokeId", m_JokeEntry.JokeId);
						startActivity(i);
						fb_dialog.dismiss();
					}
				}
				);
*/
		TextView notFb = (TextView) fb_dialog.findViewById(R.id.not_fb_share_button);
		notFb.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						final AlertDialog.Builder builder = new AlertDialog.Builder(JokePageAbove11.this);
						
						Handler finishedDrawingHandler = new Handler() {
							public void handleMessage(Message msg) {
								RelativeLayout shareContainer = (RelativeLayout)msg.obj;
								if (shareContainer != null) {
									// Convert container to file
									String filePath = sharedContainerToFile(shareContainer);
									// For a file in shared storage.  For data in private storage, use a ContentProvider.
									Intent shareIntent = new Intent(Intent.ACTION_SEND);
									shareIntent.setType("image/*");

									shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, m_JokeEntry.JokeTitle);  
									shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///"+filePath));
									startActivityForResult(Intent.createChooser(shareIntent, "Share with"), 
											m_resultCode);
								}
						       m_alert.dismiss();
							}
						};
						fb_dialog.dismiss();
						final View view = createShareView(JokePageAbove11.this, finishedDrawingHandler);
						builder.setView(view);
						m_alert = builder.show();
					}

				}
				);
		fb_dialog.show();
	}

	public View createShareView(Context ctx, 			
			final Handler finishedDrawingHandler) {
		LayoutInflater inflater = (LayoutInflater) ctx
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		final ViewGroup layout =  
				(ViewGroup)inflater.inflate(R.layout.joke_share_preview, null);
		TextView JokeTitleString = (TextView )layout.findViewById( R.id.JokeTitleString);
		JokeTitleString.setText(m_JokeEntry.JokeTitle);
		ScreenSlidePageFragment.displayJoke(this, layout , m_JokeEntry);
		
		Button shareBtn = (Button)layout.findViewById(R.id.footer_share_btn);
		shareBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				RelativeLayout shareContainer = (RelativeLayout)layout.findViewById(R.id.share_container);
				Message msg = new Message();
				msg.obj = shareContainer;
				finishedDrawingHandler.sendMessage(msg);
			}
		});
		Button cancelBtn = (Button)layout.findViewById(R.id.cancel);
		cancelBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finishedDrawingHandler.sendEmptyMessage(0);
			}
		});

		return layout;

	}

	public String sharedContainerToFile(RelativeLayout shareContiner) {
		Bitmap bitmap = Bitmap.createBitmap(shareContiner.getWidth(), 
				shareContiner.getHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		shareContiner.draw(canvas);
		try {
			String path = Environment.getExternalStorageDirectory().toString();
			OutputStream fOut = null;
			Time time = new Time();
			time.setToNow();
			String suffix = String.valueOf(time.toMillis(false))+".png";
			m_fileFullPath = path +"/"+"temp"+suffix;
			File file = new File(m_fileFullPath);
			if (file.exists())
				file.delete();
			fOut = new FileOutputStream(file);	
			bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
			fOut.flush();
			fOut.close();
			return m_fileFullPath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private void OpenReviewDialog( View v )
	{
		final Dialog dialog  = new Dialog( v.getContext(), R.style.WrongAnswerDialog);

		LayoutInflater inflater = (LayoutInflater) v.getContext()
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View layout = inflater.inflate( R.layout.review_dialog , null );
		dialog.addContentView( layout, new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.WRAP_CONTENT ));

		Button send_review = (Button) dialog.findViewById(R.id.send_review);
		send_review .setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						RatingBar ratingBar = (RatingBar)dialog.findViewById( R.id.ratingBar );
						float rating = ratingBar.getRating();

						String ratingStr  = String.format("%.1f", rating) ;

						String url = Category.m_SiteUrl + 
								"/addLikeStars.aspx?JokeId=" + m_JokeEntry.JokeId + "&rating=" + ratingStr +
								"&UserId=" + CategoriesPage.m_UserId
								+"&AppId=" + CategoriesPage.m_AppId;
						Handler handler = new Handler() {
							@Override 
							public void handleMessage(Message msg){
								if (msg.arg1 == -1)
								{

								}
								else
								{
									Bundle resultBundle = msg.getData();
									String resultFromServer = resultBundle.getString( UpdateServerAsync.BUNDLE_RESULT );
									Double newRating = ParseNewRating( resultFromServer, m_JokeEntry.LikeRate );
									m_JokeEntry.LikeRate = newRating;
									m_JokeEntry.AddedLike = true;
									CategoriesPage.m_FileDb.updateJokeReviewed( m_JokeEntry.JokeId, true );
									UpdateAddLikeBtn( m_JokeEntry );
								}
							}
						};

						new UpdateServerAsync( handler ).execute(url); 
						if (dialog != null)
							dialog.dismiss();
					}
				}
				);
		dialog.show();
	}


	private double ParseNewRating( String strFromServer, double oldReview )
	{
		double newReview = oldReview;
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject( strFromServer  );
			if ( jsonObj.has("reviewStars") )
			{
				newReview = jsonObj.getDouble("reviewStars"); 
			}
		} catch (JSONException e) {
			Log.e(TAG,e.toString());
		}
		catch (Exception e)
		{
			Log.e(TAG,e.toString());
		}

		return newReview;
	}



	private void UpdateAddLikeBtn(JokeEntry jokeEntry)
	{
		if ( jokeEntry == null )
		{
			Log.e(TAG, "UpdateAddLikeBtn - jokeEntry is null!");
			return;
		}
		jokeEntry.AddedLike = CategoriesPage.m_FileDb.wasJokeReviewed( jokeEntry.JokeId );

		Button add_like = (Button) findViewById( R.id.add_like );
		if ( jokeEntry.AddedLike == true)  
		{
			add_like.setEnabled(false);
		}
		else
		{
			add_like.setEnabled(true);
		}
		add_like.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						OpenReviewDialog( v );
					}
				}
				);
	}

	private void OpenDialog(View v, String TitleString, String ContentString)
	{
		final Dialog dialog  = new Dialog( v.getContext(), R.style.WrongAnswerDialog);

		LayoutInflater inflater = (LayoutInflater) v.getContext()
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View layout = inflater.inflate( R.layout.report_joke , null );
		dialog.addContentView( layout, new LayoutParams(
				LayoutParams.MATCH_PARENT, 
				LayoutParams.WRAP_CONTENT ));

		Button sendReport = (Button) dialog.findViewById(R.id.sendReport);

		sendReport.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						EditText editText = (EditText)dialog.findViewById( R.id.msgText );

						// Prepare all data to send to server
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						BasicNameValuePair par;

						par = new BasicNameValuePair( "jokeId", String.valueOf(m_JokeEntry.JokeId));
						params.add( par  );

						par = new BasicNameValuePair( "msgText", editText.getText().toString());
						params.add( par  );

						PostServerAsync postAsync = new PostServerAsync( params,
								null);

						String url = Category.m_SiteUrl + "/AddNewMessage.aspx";
						postAsync.execute(url);
						dialog.dismiss();
					}
				}
				);

		Button closeDialog = (Button) dialog.findViewById(R.id.closeDialog);

		closeDialog.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dialog.dismiss();
					}
				}
				);


		TextView ttl = (TextView)dialog.findViewById(R.id.dialogTitle);
		ttl.setText(TitleString);
		TextView textview = (TextView)dialog.findViewById(R.id.correctAnswer);
		textview.setText(ContentString);
		dialog.show();
	}


	public boolean DisplayNextJoke()
	{
		JokeEntry jokeEntry = GetNewJoke() ; 
		if ( jokeEntry == null )
		{
			m_JokeEntry = null;
			return false;
		}

		UpdateButtons( jokeEntry );
		UpdateShareBtn(true);
		final ViewPager jokePager = (ViewPager) findViewById( R.id.jokespager );
		jokePager.arrowScroll(ViewPager.FOCUS_RIGHT);
		return true;
	}

	public void UpdateFavoriteBtn (boolean JokeInFavorite , String JokeCategory)
	{
		Button addToFavoritesBtn = (Button)findViewById( R.id.addToFavorites ); 
		Drawable d = null;

		if ( JokeInFavorite == true )
		{ // jokes already in favorites 
			d = getResources().getDrawable(R.drawable.favorite_add_icon);
		}
		else
		{ // joke is not in favorites
			d = getResources().getDrawable(R.drawable.favorite_icon);
		}

		addToFavoritesBtn.setCompoundDrawablesWithIntrinsicBounds(null,d,null,null);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (m_JokeVideo != null)
		{
			LinearLayout container = (LinearLayout)findViewById(R.id.container);

			LayoutParams params = new LayoutParams( LayoutParams.MATCH_PARENT,
					container.getHeight()-10,		
					0);

			m_JokeVideo.setLayoutParams(params);
		}
	}

	private void ConfirmRemoveFromFavorites()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(getString(R.string.DeleteFromFavoriteTitle));
		alertDialog.setMessage(getString(R.string.DeleteFromFavoriteContent));
		alertDialog.setIcon(R.drawable.monkey);
		alertDialog.setButton(	AlertDialog.BUTTON_POSITIVE,
				getString(R.string.yes), 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				//boolean AddToFavorite = !m_JokeEntry.JokeInFavorite;
				CommitChangeToFavorite(false);
				UpdateButtons(m_JokeEntry);
			}
		});
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,
				getString(R.string.no), 
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return ;
			}
		});
		alertDialog.show();
	}


	JokeEntry getJokeFromRamUsingId(int jokeId)
	{
		Vector<JokeEntry> vector= Category.m_JokesArr.get(m_CategoryId);
		if (vector == null )
			return null;
		for (JokeEntry jokeEntry: vector)
		{
			if (jokeEntry.JokeId == jokeId)
			{
				return jokeEntry;
			}
		}
		return null;
	}

	private void AddFavoritesButtonHandler()
	{
		boolean AddToFavorite = !m_JokeEntry.JokeInFavorite;
		if (!AddToFavorite)
		{
			ConfirmRemoveFromFavorites();
			return;
		}

		CommitChangeToFavorite( AddToFavorite );
	}
	private void CommitChangeToFavorite(boolean AddToFavorite)
	{
		// * - Update current copy of joke 
		m_JokeEntry.JokeInFavorite = AddToFavorite;

		if ( AddToFavorite )
		{
			//Category.AddJokeToDb( jokeEntry ); - no need to add it to DB, its updated in server
		}
		else
		{

			// this is a patch: change state of joke - its no longer in favorites
			if ( m_CategoryId.equals(Category.FAVORITE_CATEGORY_ID) )
			{
				boolean found = false;
				for(int ind = 0 ; ind < Category.m_CategoriesArr.size() && !found; ind++)
				{
					Vector<JokeEntry> vector = Category.m_JokesArr.get( Category.ConvertCategoryIndexToId(ind) );
					if (vector != null )
					{
						for (int jokeInd=0 ; jokeInd < vector.size() && !found ; jokeInd++)
						{
							if (vector.get(jokeInd).JokeId == m_JokeEntry.JokeId )
							{
								vector.get(jokeInd).JokeInFavorite = false;
								found = true;
							}
						}
					}
				}
			}

			// * - remove the joke to/from RAM DB
			JokeEntry jokeEntryFavorite = new JokeEntry( m_JokeEntry ); // Create new joke entry with favorite category 
			jokeEntryFavorite.JokeCategory = Category.FAVORITE_CATEGORY_ID ; 
			// Remove entry 
			Category.RemoveJokeFromDb( jokeEntryFavorite );
			//m_JokePagerAdapter.setCount(m_JokePagerAdapter.getCount()-1);
			if ( m_CategoryId.equals(Category.FAVORITE_CATEGORY_ID) )
			{
				initPager();
			}
		}
		// * - Update in server new state of favorite
		UpdateFavoriteServer( m_JokeEntry.JokeId, AddToFavorite);

		// * - Update favorites button image
		if ( !m_CategoryId.equals(Category.FAVORITE_CATEGORY_ID) )
			UpdateFavoriteBtn( AddToFavorite,  m_CategoryId);
	}

	static public void UpdateFavoriteServer( int jokeId, boolean InFavorite )
	{
		String url = Category.m_SiteUrl + "/AddJokeToFav.aspx?JokeId=" + jokeId +
				"&UserId=" + CategoriesPage.m_UserId +
				"&AddToFavorite="+InFavorite;
		new UpdateServerAsync(null).execute(url);

	}

	private void CheckButtonsVisability( JokeEntry jokeEntry)
	{
		Vector<JokeEntry> vector = Category.m_JokesArr.get(m_CategoryId);
		if (vector == null)
			return ;
		if ( jokeEntry.JokeIndex + 1 >= vector.size())
		{
			Button goNext = (Button)findViewById( R.id.goNext ); 
			goNext.setClickable(false);
			goNext.setEnabled(false);
		}
		else
		{
			Button goNext = (Button)findViewById( R.id.goNext ); 
			goNext.setClickable(true);
			goNext.setEnabled(true);

		}

		if ( jokeEntry.JokeIndex  <= 0 )
		{
			Button goBack = (Button)findViewById( R.id.goBack ); 
			goBack.setClickable(false);
			goBack.setEnabled(false);

		}
		else
		{
			Button goBack = (Button)findViewById( R.id.goBack ); 
			goBack.setClickable(true);
			goBack.setEnabled(true);
		}

	}

	private JokeEntry GetNewJoke()
	{
		// Ask for new joke from server
		Category cat = new Category();
		cat.getNewJokes( m_CategoryId, 1, null );

		// Find category vector of requested joke
		Vector<JokeEntry> vector = Category.m_JokesArr.get( m_CategoryId );
		if (vector == null)
			return null;
		int jokeIndex = m_JokeEntry.JokeIndex;
		// Find the first joke that can be shown and store it
		if ( vector.size() > jokeIndex )
		{
			for ( int ind = jokeIndex; ind < vector.size() ; ind++)
			{
				JokeEntry jokeEntry = vector.elementAt( jokeIndex );
				if ( Category.CanShowJoke( jokeEntry ) || m_CategoryId.equals(Category.RANDOM_JOKE_CATEGORY_ID))
				{
					return jokeEntry;
				}
			}
			return null;
		}
		else
		{
			Toast.makeText(this, 
					getResources().getString(R.string.no_joke_found), 
					Toast.LENGTH_LONG).show();
			return null;
		}
	}
	public int GetShowableIndex( int jokeId)
	{
		// Find category vector of requested joke
		Vector<JokeEntry> vector = Category.m_JokesArr.get( m_CategoryId );
		if (vector == null)
			return 0;

		int showableJokeCounter = 0;
		for ( JokeEntry jokeEntry  : vector)
		{
			if (jokeEntry.JokeId == jokeId)
				return showableJokeCounter;
			if ( Category.CanShowJoke( jokeEntry ) )
			{
				showableJokeCounter++;
			}
		}
		return 0;
	}
	public JokeEntry GetShowableJoke( int showableJokeNum )
	{
		return _GetShowableJoke( m_CategoryId, showableJokeNum );
	}

	public static JokeEntry _GetShowableJoke(String categoryId, int showableJokeNum)
	{
		// Ask for new joke from server
		Category cat = new Category();
		cat.getNewJokes( categoryId, 1, null );

		// Find category vector of requested joke
		Vector<JokeEntry> vector = Category.m_JokesArr.get( categoryId );
		if (vector == null)
			return null;

		// Loop over all showable jokes 
		if ( vector.size() > showableJokeNum )
		{
			int showableJokeCounter = 0;
			for ( int ind = 0; ind < vector.size() ; ind++)
			{
				JokeEntry jokeEntry = vector.elementAt( ind );
				if ( Category.CanShowJoke( jokeEntry ) || categoryId.equals(Category.RANDOM_JOKE_CATEGORY_ID))
				{
					if (showableJokeCounter == showableJokeNum)
						return jokeEntry;
					showableJokeCounter++;
				}
			}
			return null;
		}
		else
		{
			//	Toast.makeText(this, "No joke found", 
			//			Toast.LENGTH_LONG).show();
			return null;
		}
	}

	private boolean DisplayPrevJoke() {
		// Take new joke from RAM DB
		Vector<JokeEntry> vector = Category.m_JokesArr.get( m_CategoryId );
		boolean found = false;

		if (vector != null)
		{
			if ( m_JokeEntry.JokeIndex >= 0 && m_JokeEntry.JokeIndex - 1< vector.size() )
			{
				for (int ind = m_JokeEntry.JokeIndex - 1 ; ind >= 0 && !found; ind--)
				{
					JokeEntry jokeEntry = vector.elementAt( ind );
					if ( Category.CanShowJoke( jokeEntry ) )
					{
						int savedInd = m_JokeEntry.JokeIndex;
						JokeEntry saveJoke = m_JokeEntry;
						m_JokeEntry = jokeEntry;
						/*if (DisplayJoke() == false)
						{
							m_JokeIndex = savedInd;
							m_JokeEntry = saveJoke;
						}*/
						found = true;
					}
				}			
			}
		}

		UpdateButtons( m_JokeEntry );
		UpdateShareBtn(true);
		if (found)
		{
			ViewPager jokePager = (ViewPager) findViewById( R.id.jokespager );
			jokePager.arrowScroll(ViewPager.FOCUS_LEFT);
		}
		return found;
	}

	public JokeEntry GetPrevJoke(int jokeIndex) {
		// Take new joke from RAM DB
		Vector<JokeEntry> vector = Category.m_JokesArr.get( m_CategoryId );
		boolean found = false;

		if (vector != null)
		{
			if ( jokeIndex >= 0 && jokeIndex - 1< vector.size() )
			{
				for (int ind = jokeIndex - 1 ; ind >= 0 && !found; ind--)
				{
					JokeEntry jokeEntry = vector.elementAt( ind );
					if ( Category.CanShowJoke( jokeEntry ) )
					{
						return jokeEntry;
					}
				}			
			}
		}
		return null;
	}

	public void DebugDisplayTitle(Cursor c)
	{
		Toast.makeText(this, 
				"id: " + c.getString(0) + "\n" +
						"joke: " + c.getString(1) + "\n" +
						"TITLE: " + c.getString(2) + "\n" +
						"category:  " + c.getString(3) + "\n" +
						"Size Of Table: " + c.getCount(),
						Toast.LENGTH_LONG).show();        
	} 



	private void UpdateJokeRead( JokeEntry jokeEntry , String UserId )
	{
		//DBAdapter db = CategoriesPage.m_FileDb ;
		//db.open();
		//db.updateWasRead( m_JokeEntry.JokeId , true);
		String url = Category.m_SiteUrl + 
				"/UpdateJokeRead.aspx?JokeId=" +
				jokeEntry.JokeId + "&UserId=" + UserId
				+"&AppId=" +CategoriesPage.m_AppId;
		new UpdateServerAsync(null).execute(url); 

		//		jokeEntry.JokeWasRead = true;
	}




	/*private int GetJokeIndex(int jokeId)
	{
		Vector<JokeEntry> vector= Category.m_JokesArr.get(m_CategoryId);
    	for (int ind = 0 ; ind < vector.size() ; ind++)
    	{
    		if ( vector.get(ind).JokeId == (jokeId) )
    			return ind;
    	}
    	return -1;
	}*/
}
