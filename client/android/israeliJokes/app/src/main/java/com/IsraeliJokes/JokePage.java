package com.IsraeliJokes;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.IsraeliJokes.GlobalsManager.eGlobalResource;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
//import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;



public class JokePage extends Activity{
	private String m_CategoryId;
	private int m_JokeId;
	public JokeEntry m_JokeEntry; 
	private boolean m_IsFavoriteCategory = false;
	//private Handler m_RandomJokeHandler;
	private String TAG = "JokePage";
	GlobalsManager m_GlobalGravity = new GlobalsManager();
	private WebView m_JokeVideo = null;
	
	private TextView m_JokeTextView = null;
	public ProgressBar m_Progress;
	private AsyncTask<String, Void, Bitmap> m_ReadImageFromServer = null;

	@Override
	public void onBackPressed()
	{
		Intent i = new Intent(JokePage.this, Category.class);
		i.putExtra( "CategoryId", m_CategoryId);
		i.putExtra( "BackFromJokePage" , true);

		startActivity(i);  
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (m_JokeVideo != null)
			m_JokeVideo.destroy();
		ImageView categoriesImage = (ImageView)findViewById( R.id.ToCategory );
		categoriesImage.destroyDrawingCache();
		/*if (m_Image != null)
		{
			m_Image.destroyDrawingCache();
			m_Image.setVisibility(View.GONE);
			m_Image = null;
		}*/
	/*	if (m_ReadImageFromServer != null)
		{
			m_ReadImageFromServer.cancel(true);
			m_ReadImageFromServer = null;
		}*/
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

		case  R.id.menu_text_align_change:
			GlobalsManager globalGravity = new GlobalsManager(); 
			if (globalGravity.GetGlobalInt(this, eGlobalResource.GRAVITY, Gravity.RIGHT) == Gravity.LEFT )
			{
				globalGravity.SetGlobalInt(this, eGlobalResource.GRAVITY, Gravity.RIGHT);
			}
			else
			{
				globalGravity.SetGlobalInt(this, eGlobalResource.GRAVITY, Gravity.LEFT);
			}
			DisplayJoke(m_JokeEntry);
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView( R.layout.joke_page_old);
		
		Intent intent 	= getIntent();
		m_CategoryId 	= intent.getStringExtra( "CategoryId"  );
		m_JokeId 		= intent.getIntExtra("JokeId", -1);
		m_JokeEntry 	= Category.getJoke(m_CategoryId, m_JokeId);
		
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

		ImageView GoToCategoryPageBtn = (ImageView)findViewById( R.id.ToCategory ); 
		GoToCategoryPageBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent i = new Intent(JokePage.this, Category.class);
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


		Button goNext = (Button)findViewById( R.id.goNext ); 
		goNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DisplayNextJoke();
			}
		});
		Button goPrev = (Button)findViewById( R.id.goBack ); 
		goPrev.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				DisplayPrevJoke() ;
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
				//UpdateShareBtn(false);

				/*	final Intent intent = new Intent(Intent.ACTION_SEND);
            	intent.setType("text/plain");
            	intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
            	String url = "http://blah.com";
            	String shareText = url + "\nCheck this out";

            	intent.putExtra(Intent.EXTRA_TEXT, shareText);
            	startActivity(Intent.createChooser(intent, "string"));*/
			}
		});
		UpdateButtons( m_JokeEntry );
		UpdateAlignmentIcon();
		DisplayJoke(m_JokeEntry);
		//UpdateShareBtn(true);
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

		shareBtn.setBackgroundDrawable(d);
	}
	private void OpenShareDialog( View v )
	{
		final Dialog dialog  = new Dialog( v.getContext(), R.style.WrongAnswerDialog);

		LayoutInflater inflater = (LayoutInflater) v.getContext()
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View layout = inflater.inflate( R.layout.share_dialog , null );
		dialog.addContentView( layout, new LayoutParams(
				LayoutParams.FILL_PARENT, 
				LayoutParams.WRAP_CONTENT ));

		/* TextView faceBookShareText = (TextView) dialog.findViewById(R.id.facebook_share_button);
		faceBookShareText.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {

						Intent i = new Intent(JokePage.this, FacebookHandler.class);
						i.putExtra("CategoryId",m_CategoryId  );
						i.putExtra("JokeId", m_JokeId);
						startActivity(i);

						dialog.dismiss();
					}
				}
				);
*/
		TextView notFb = (TextView) dialog.findViewById(R.id.not_fb_share_button);
		notFb.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (m_JokeEntry.JokePic.length()>0){
							LinearLayout ll = (LinearLayout)findViewById(R.id.container);
							ImageView imageView = (ImageView)ll.getChildAt(0);
							Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

							Intent shareIntent = new Intent(Intent.ACTION_SEND);
							shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
							shareIntent.setType("image/*");

							try {
								String path = Environment.getExternalStorageDirectory().toString();
								OutputStream fOut = null;
								String suffix ;
								if (m_JokeEntry.JokePic.toLowerCase().contains("jpg"))
									suffix = ".jpg";
								else if (m_JokeEntry.JokePic.toLowerCase().contains("png"))
									suffix = ".png";
								else
									return;
								File file = new File(path, "temp"+suffix);
								fOut = new FileOutputStream(file);		
								if (suffix.equals(".jpg"))
									bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
								else if (suffix.equals(".png"))
									bitmap.compress(Bitmap.CompressFormat.PNG, 90, fOut);
								fOut.flush();
								fOut.close();
								// For a file in shared storage.  For data in private storage, use a ContentProvider.
								Uri uri = Uri.fromFile(file);
								shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, m_JokeEntry.JokeTitle);
								shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, m_JokeEntry.JokeTitle);
								shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
								startActivity(Intent.createChooser(shareIntent, "Share with"));
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
						else{
							Intent waIntent = new Intent(android.content.Intent.ACTION_SEND); 
							waIntent.setType("plain/text");   
							waIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, m_JokeEntry.JokeTitle);  
							waIntent.putExtra(android.content.Intent.EXTRA_TEXT, m_JokeEntry.JokeStr + "\n\n" +
									getString(R.string.share_str));  					
	
							startActivity(Intent.createChooser(waIntent, "Share with"));
						}
						dialog.dismiss();
					}
				}
				);
		dialog.show();

	}

	private void OpenReviewDialog( View v )
	{
		final Dialog dialog  = new Dialog( v.getContext(), R.style.WrongAnswerDialog);

		LayoutInflater inflater = (LayoutInflater) v.getContext()
				.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
		View layout = inflater.inflate( R.layout.review_dialog , null );
		dialog.addContentView( layout, new LayoutParams(
				LayoutParams.FILL_PARENT, 
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

	void UpdateAlignmentIcon()
	{
		if (m_JokeEntry == null)
		{
			Log.e(TAG, "UpdateAlignmentIcon - m_JokeEntry is null!");
			return;
		}
		ImageView GlobalGravityIcon = (ImageView)findViewById(R.id.GlobalGravityIcon);
		GlobalGravityIcon.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						// update server
						if (m_GlobalGravity.GetGlobalInt(	JokePage.this, 
								eGlobalResource.GRAVITY, 
								Gravity.RIGHT ) == Gravity.LEFT )
						{
							m_GlobalGravity.SetGlobalInt(JokePage.this, eGlobalResource.GRAVITY, Gravity.RIGHT);
						}
						else
						{
							m_GlobalGravity.SetGlobalInt(JokePage.this, eGlobalResource.GRAVITY, Gravity.LEFT);
						}
						DisplayJoke(m_JokeEntry);
					}
				}
				);
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
						/*
					// update server
					String url = Category.m_SiteUrl + 
								"/AddLikeStars.aspx?JokeId=" +
								m_JokeEntry.JokeId ;
					new UpdateServerAsync(null).execute(url); 
			        Button add_like = (Button) findViewById( R.id.add_like );
			        add_like.setEnabled(false);
			        // update db that the joke was liked

			        CategoriesPage.m_FileDb.updateJokeReviewed( m_JokeEntry.JokeId, true );
					m_JokeEntry.AddedLike = true;
					m_JokeEntry.LikeRate++;
						 */

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
				LayoutParams.FILL_PARENT, 
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
		JokeEntry jokeEntry = GetNewJoke(  ) ; 
		if ( jokeEntry == null )
		{
			return false;
		}
		
		UpdateButtons( jokeEntry );
		UpdateShareBtn(true);
		DisplayJoke(jokeEntry);
		return true;
	}
	
	public JokeEntry GetNextJoke(int index, boolean firstTime)
	{
		return GetNewJoke(  ) ; 		
	}
	
	public void UpdateFavoriteBtn (boolean JokeInFavorite , String JokeCategory)
	{
		Button addToFavoritesBtn = (Button)findViewById( R.id.addToFavorites ); 

		Drawable d = null;

		if ( JokeInFavorite == true || JokeCategory.equals( Category.FAVORITE_CATEGORY_ID ) )
		{ // jokes already in favorites 
			d = getResources().getDrawable(R.drawable.favorite_add_icon);
		}
		else
		{ // joke is not in favorites
			d = getResources().getDrawable(R.drawable.favorite_icon);
		}

		addToFavoritesBtn.setBackgroundDrawable(d);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (m_JokeVideo != null)
		{
			LinearLayout container = (LinearLayout)findViewById(R.id.container);

			LayoutParams params = new LayoutParams( LayoutParams.FILL_PARENT,
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
	
	JokeEntry GetJokeFromRam( int jokeIndex )
	{
		Vector<JokeEntry> vector= Category.m_JokesArr.get(m_CategoryId);
		if (vector == null )
			return null;
		if ( vector.size() < jokeIndex )
			return null;
		return vector.get(jokeIndex );
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

		
		if ( AddToFavorite ) {
			//Adding to DB so all the tests with it will be synchronized
			// such as how many jokes in the db etc.
			Category category = new Category();
			JokeEntry jokeEntry = new JokeEntry(m_JokeEntry);
			jokeEntry.JokeCategory = Category.FAVORITE_CATEGORY_ID;
			category.AddJokeToDb( jokeEntry ); 
		} else 	{

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
		}
		// * - Update in server new state of favorite
		UpdateFavoriteServer( m_JokeEntry.JokeId, AddToFavorite);

		// * - Update favorites button image
		UpdateFavoriteBtn( AddToFavorite,  m_JokeEntry.JokeCategory);
		
		if ( !AddToFavorite ) {
			if (!DisplayNextJoke())
				if (!DisplayPrevJoke()){
					finish();
				}
		}
	}

	static public void UpdateFavoriteServer( int jokeId, boolean InFavorite )
	{
		/*DBAdapter db = CategoriesPage.m_FileDb ;
		try
		{
			db.open();
	        db.insertJoke(
	        		jokeEntry.JokeId,
	        		jokeEntry.JokeStr,
	        		jokeEntry.JokeTitle,
	        		jokeEntry.JokePic,
	        		jokeEntry.JokeCategory,
	        		true,
	        		false,
	        		false);        
		}
		catch( NullPointerException e )
		{
			Log.e("AddJokeToStorage", e.toString());
		}
		catch (SQLException e)
		{
			Log.e("AddJokeToStorage", e.toString());
		}
		catch (IllegalStateException e)
		{
			Log.e("AddJokeToStorage", e.toString());
		}*/
		String url = Category.m_SiteUrl + "/AddJokeToFav.aspx?JokeId=" + jokeId +
				"&UserId=" + CategoriesPage.m_UserId +
				"&AddToFavorite="+InFavorite;
		new UpdateServerAsync(null).execute(url);

	}

	private int GetShowableIndex( int jokeId)
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
	
	private void CheckButtonsVisability( JokeEntry jokeEntry)
	{
		Vector<JokeEntry> vector = Category.m_JokesArr.get(m_CategoryId);
		if (vector == null)
			return ;
		int jokeShowableInd = GetShowableIndex(jokeEntry.JokeId);
		if ( jokeShowableInd + 1 >= vector.size())
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

		if ( jokeShowableInd  <= 0 )
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
			for ( int ind = jokeIndex+1; ind < vector.size() ; ind++)
			{
				JokeEntry jokeEntry = vector.elementAt( ind );
				if ( Category.CanShowJoke( jokeEntry ) || m_CategoryId.equals(Category.RANDOM_JOKE_CATEGORY_ID))
				{
					return jokeEntry;
				}
			}
			return null;
		}
		else
		{
			Toast.makeText(this, "No joke found", 
					Toast.LENGTH_LONG).show();
			return null;
		}
	}
	
	
	public JokeEntry GetShowableJoke( int showableJokeNum )
	{
		return _GetShowableJoke( m_CategoryId, showableJokeNum );
	}
	private JokeEntry _GetShowableJoke( String categoryId, int showableJokeNum)
	{

		// Ask for new joke from server
		Category cat = new Category();
		cat.getNewJokes( categoryId, 1, null );
		
		// Find category vector of requested joke
		Vector<JokeEntry> vector = Category.m_JokesArr.get( categoryId );
		if (vector == null)
			return null;

		// loop over all showable jokes 
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
			Toast.makeText(this, "No joke found", 
					Toast.LENGTH_LONG).show();
			return null;
		}
	}

	

	private boolean DisplayPrevJoke() {
		// Take new joke from RAM DB
		Vector<JokeEntry> vector = Category.m_JokesArr.get( m_CategoryId );
		boolean found = false;
		int jokeIndex = m_JokeEntry.JokeIndex;
		if (vector != null)
		{
			if ( jokeIndex >= 0 && jokeIndex - 1< vector.size() )
			{
				for (int ind = jokeIndex - 1 ; ind >= 0 && !found; ind--)
				{
					JokeEntry jokeEntry = vector.elementAt( ind );
					if ( Category.CanShowJoke( jokeEntry ) )
					{
						int savedInd = jokeIndex;
						JokeEntry saveJoke = m_JokeEntry;
						m_JokeEntry = jokeEntry;
						jokeIndex = ind;
						if (DisplayJoke(jokeEntry) == false)
						{
							jokeIndex = savedInd;
							m_JokeEntry = saveJoke;
						}
						found = true;
					}
				}			
			}
		}

		UpdateButtons( m_JokeEntry );
		UpdateShareBtn(true);
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


	/*   Category.AddFavoriteJokeToDb(m_JokeEntry.JokeId, m_JokeEntry.JokeStr, m_JokeEntry.JokeTitle);
        //---get a title---
        try
        {
	        db.open();
	    	Cursor c = db.getAllJokes();
	        if (c.moveToFirst())        
	            DebugDisplayTitle(c);
	        else
	            Toast.makeText(this, "No title found", 
	            		Toast.LENGTH_LONG).show();
	        c.close();
	        db.close();
		}
        catch( NullPointerException e )
		{
			Log.e(TAG, e.toString());
		}
		/*SharedPreferences settings = getSharedPreferences( StartGame.HIGH_SCORE_FILE_NAME, 0);
        m_PersonalHighScore = settings.getString("PersonalHighScore", "0");
        int personalHighScoreInt = Integer.parseInt( m_PersonalHighScore );
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("PersonalHighScore", String.valueOf(userHighScoreInt));

        // Commit the edits!
        editor.commit();


		private String GetPersonalHighScore()
	    {
	    	 SharedPreferences settings = getSharedPreferences(HIGH_SCORE_FILE_NAME, 0);
	         String personalHighScore = settings.getString("PersonalHighScore", "0");
	         return personalHighScore;
	    }*/
	//}

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

//	public boolean DisplayJoke(View parent, final JokeEntry jokeEntry)
//	{
//		return false;
//		
//		Log.d(TAG, "m_CategoryId: " + m_CategoryId);
//		Log.d(TAG, "m_JokeIndex: " + m_JokeIndex);
//		ScrollView  scroll = (ScrollView)parent.findViewById(R.id.ScrollView01);
//		scroll.fullScroll(View.FOCUS_UP); 
//		if ( m_CategoryId != null && m_JokeIndex != -1  )
//		{
//			Vector<JokeEntry> vector= Category.m_JokesArr.get(m_CategoryId);
//			Log.d(TAG, " vector.size(): " +  vector.size());
//
//			LinearLayout container = (LinearLayout)parent.findViewById( R.id.container );
//			container.removeAllViews();
//			if ( m_JokeIndex < vector.size() )
//			{
//				TextView jokeTitle = (TextView)parent.findViewById(R.id.JokeTitleString) ;
//				//String formattedJokeStr = Category.StringSplitter(m_JokeEntry.JokeStr, 35, 100);
//				String formattedJokeStr = jokeEntry.JokeStr;
//				int gravity =  m_GlobalGravity.GetGlobalInt(	this, 
//						eGlobalResource.GRAVITY, 
//						Gravity.RIGHT );
//				if ( jokeEntry.JokeStr.length() > 0)
//				{
//					if (m_JokeTextView == null )
//					{
//						m_JokeTextView = new TextView(this);
//
//						m_JokeTextView.setTextColor(Color.BLACK);
//						m_JokeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
//
//						LayoutParams params = new LayoutParams( LayoutParams.FILL_PARENT,
//								LayoutParams.FILL_PARENT,
//								0);
//
//						m_JokeTextView.setLayoutParams( params );
//						m_JokeTextView.setVisibility(View.VISIBLE);
//
//					}
//					m_JokeTextView.setGravity( gravity );
//
//					//container.addView(m_JokeTextView);
//					m_JokeTextView.setText(  formattedJokeStr );
//
//				}
//				else
//				{
//					if (m_JokeTextView !=null)
//					{
//						m_JokeTextView.destroyDrawingCache();
//						m_JokeTextView.setVisibility(View.INVISIBLE);
//						m_JokeTextView = null;
//					}
//
//				}
//				//jokeStr.setText( "&#8207;ùé ùôø\n&#8207;îî");
//				//jokeStr.setBackgroundColor(Color.RED);
//				jokeTitle.setText( jokeEntry.JokeTitle ); 
//				jokeTitle.setTextColor(Color.BLACK);
//				jokeTitle.setGravity( gravity );
//
//				if ( jokeEntry.JokePic.length() > 0)
//				{
//					
//					if (m_ReadImageFromServer != null)
//					{
//						m_ReadImageFromServer.cancel(true);
//					}
//					m_ReadImageFromServer = new ReadImageFromServer().execute
//							(
//									CategoriesPage.ImagesUrlPrefix +"/" + jokeEntry.JokePic
//									);
//				}
//				else
//				{
//					if ( m_Image != null )
//					{
//						m_Image.destroyDrawingCache();
//						m_Image.setVisibility(View.GONE);
//						m_Image = null;
//					}
//				}
//
//				if ( jokeEntry.JokeVideo.length() > 0)
//				{
//
//					
//
//					if (m_ReadImageFromServer != null)
//					{
//						m_ReadImageFromServer.cancel(true);
//					}
//					String imageUrl = "https://img.youtube.com/vi/" + jokeEntry.JokeVideo + "/0.jpg";// the message to post to the wall
//					m_ReadImageFromServer = new ReadImageFromServer().execute
//							(
//									imageUrl 
//									);
//					LinearLayout container1 = (LinearLayout)parent.findViewById(R.id.container);
//					container1.setOnClickListener(new OnClickListener() {
//						public void onClick(View v) {
//							String url = CategoriesPage.m_YouTubePrefix + jokeEntry.JokeVideo+ "?rel=0&autoplay=1&fs=0";
//							Intent intent = new Intent(Intent.ACTION_VIEW); 
//							intent.setData(Uri.parse(url)); 
//							startActivity(intent); 
//						}
//					});
//				}
//				else
//				{
//					if (m_JokeVideo != null)
//					{
//						m_JokeVideo.setVisibility(View.GONE);
//						m_JokeVideo.destroy();
//						m_JokeVideo = null;
//					}
//				}
//				// Update storage that joke was read
//				if ( jokeEntry.JokeWasRead == false)
//				{
//					jokeEntry.JokeWasRead = true;
//					UpdateJokeRead(jokeEntry.JokeId, CategoriesPage.m_UserId);
//				}
//
//				Log.d(TAG,"string:" + jokeEntry.JokeStr);
//				return true;
//			}
//
//		}
//		return false;
//	}
	
	
	
	public boolean DisplayJoke( final JokeEntry jokeEntry)
	{
		View parent = findViewById(R.id.containerParent);
		Log.d(TAG, "m_CategoryId: " + m_CategoryId);
		ScrollView  scroll = (ScrollView)parent.findViewById(R.id.ScrollView01);
		scroll.fullScroll(View.FOCUS_UP);
		int jokeIndex = m_JokeEntry.JokeIndex;
		if ( m_CategoryId != null && jokeIndex != -1  )
		{
			Vector<JokeEntry> vector= Category.m_JokesArr.get(m_CategoryId);
			Log.d(TAG, " vector.size(): " +  vector.size());

			LinearLayout container = (LinearLayout)scroll.findViewById( R.id.container );
			container.removeAllViews();
			if ( jokeIndex < vector.size() )
			{
				m_JokeEntry = jokeEntry;
				jokeIndex = jokeEntry.JokeIndex;
				TextView jokeTitle = (TextView)findViewById(R.id.JokeTitleString) ;
				//String formattedJokeStr = Category.StringSplitter(m_JokeEntry.JokeStr, 35, 100);
				String formattedJokeStr = jokeEntry.JokeStr;
				int gravity =  m_GlobalGravity.GetGlobalInt(	this, 
																eGlobalResource.GRAVITY, 
																Gravity.RIGHT );
				if ( jokeEntry.JokeStr.length() > 0 )
				{
					//if (m_JokeTextView == null )
					{
						m_JokeTextView = new TextView(this);

						m_JokeTextView.setTextColor(Color.BLACK);
						m_JokeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);

						LayoutParams params = new LayoutParams( LayoutParams.FILL_PARENT,
																LayoutParams.FILL_PARENT,
																0);

						m_JokeTextView.setLayoutParams( params );
						m_JokeTextView.setVisibility(View.VISIBLE);

					}
					m_JokeTextView.setGravity( gravity );

					container.addView(m_JokeTextView);
					m_JokeTextView.setText(  formattedJokeStr );

				}
				else
				{
					if (m_JokeTextView !=null)
					{
						m_JokeTextView.destroyDrawingCache();
						m_JokeTextView.setVisibility(View.INVISIBLE);
						m_JokeTextView = null;
					}
				}
				//jokeStr.setBackgroundColor(Color.RED);R
				jokeTitle.setText( jokeEntry.JokeTitle ); 
				jokeTitle.setTextColor(Color.BLACK);
				jokeTitle.setGravity( gravity );

				if ( jokeEntry.JokePic.length() > 0)
				{
					
					new ReadImageFromServer(parent).execute
					(
							CategoriesPage.ImagesUrlPrefix +"/" + jokeEntry.JokePic
							);
				}
				else
				{
					/*if ( m_Image != null )
					{
						m_Image.destroyDrawingCache();
						m_Image.setVisibility(View.GONE);
						m_Image = null;
					}*/
				}

				if ( jokeEntry.JokeVideo.length() > 0)
				{

					String imageUrl = "https://img.youtube.com/vi/" + jokeEntry.JokeVideo + "/0.jpg";// the message to post to the wall
					new ReadImageFromServer(parent).execute(imageUrl);
					LinearLayout container1 = (LinearLayout)parent.findViewById(R.id.container);
					container1.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							String url = CategoriesPage.m_YouTubePrefix + jokeEntry.JokeVideo;
							Intent intent = new Intent(Intent.ACTION_VIEW); 
							intent.setData(Uri.parse(url)); 
							startActivity(intent); 
						}
					});
				}
				else
				{
					if (m_JokeVideo != null)
					{
						m_JokeVideo.setVisibility(View.GONE);
						m_JokeVideo.destroy();
						m_JokeVideo = null;
					}
				}
				// Update storage that joke was read
				if ( jokeEntry.JokeWasRead == false)
				{
					UpdateJokeRead(jokeEntry.JokeId, CategoriesPage.m_UserId);
				}

				Log.d(TAG,"string:" + jokeEntry.JokeStr);
				return true;
			}

		}
		return false;
	}

	private void UpdateJokeRead( int JokeId , String UserId )
	{
		//DBAdapter db = CategoriesPage.m_FileDb ;
		//db.open();
		//db.updateWasRead( m_JokeEntry.JokeId , true);
		String url = Category.m_SiteUrl + 
				"/UpdateJokeRead.aspx?JokeId=" +
				JokeId + "&UserId=" + UserId
				+"&AppId=" +CategoriesPage.m_AppId;
		new UpdateServerAsync(null).execute(url); 

	}

	class ReadImageFromServer extends AsyncTask<String, Void, Bitmap> {
		//private ProgressDialog m_ProgressDialog;
		private View m_Parent;
		private ImageView m_Image = null;
		ReadImageFromServer(View Parent)
		{
			m_Parent = Parent;
		}
		@Override
		protected void onPreExecute() {
			m_Progress = new ProgressBar(JokePage.this);
			m_Progress.setVisibility( View.VISIBLE );
			LayoutParams params = new LayoutParams(40,40,0);
			m_Progress.setLayoutParams(params);
			LinearLayout container = (LinearLayout)m_Parent.findViewById(R.id.container);
			container.removeAllViews();
			container.addView(m_Progress);

		}
		@Override
		protected Bitmap doInBackground(String... params) {
			String imageUrl =  params[0];
			WindowManager winman = getWindowManager();
			int screenWidth = 0; 
			if ( winman  != null)
			{
				Display display = winman.getDefaultDisplay(); 
				screenWidth = display.getWidth();
			}
			return GetBitMap( imageUrl, screenWidth );
		}
		@Override
		protected void onPostExecute(final Bitmap bMap) {
			if (m_Image  == null )
			{
				m_Image = new ImageView( JokePage.this );
				m_Image.setScaleType( ImageView.ScaleType.CENTER_CROP );
				LayoutParams params = new LayoutParams( LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT,
						0);

				m_Image.setLayoutParams( params );

			}
			LinearLayout ll = (LinearLayout)m_Parent.findViewById(R.id.container);
			ll.removeAllViews();
			ll.addView(m_Image);

			m_Image.setImageBitmap( bMap ); 
			m_Image.setVisibility(View.VISIBLE);

			//m_ProgressDialog.dismiss();
			if (m_Progress != null)
				m_Progress.setVisibility(View.GONE);
			m_Progress = null;

		}
	}

	private Bitmap GetBitMap( String imageUrl, int ScreenWidth  )
	{
		try{
			HttpURLConnection c;
			URL url = new URL( imageUrl );

			c = (HttpURLConnection) url.openConnection();
			Bitmap bMap = null;
			InputStream  in = null;
			BufferedInputStream buf = null;
			for (int retry=0 ; retry < 3 && bMap == null ; retry++)
			{
				in = c.getInputStream();
				buf = new BufferedInputStream(in);
				bMap = BitmapFactory.decodeStream( buf );
				if (bMap == null)
				{
					try {
						Thread.sleep(350);
					} catch (InterruptedException e) {
						Log.e( TAG , e.toString() );
					}
				}
			}
			if( bMap != null )
			{
				int newWidth = ScreenWidth - 20;
				float widthToHeightFactor =(float) bMap.getHeight() / (float)bMap.getWidth();
				int newHeight = (int)((float)newWidth * widthToHeightFactor);
				bMap = Bitmap.createScaledBitmap(bMap, newWidth, newHeight, true);
			}
			if (in != null){
				in.close();
			}
			if (buf != null) {
				buf.close();
			}
			return bMap;
			//m_ComunicationFailure  = true;
		}catch (IOException e) {
			Log.e(TAG, e.toString());
			//m_ComunicationFailure = true;
			return null;
		}
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
