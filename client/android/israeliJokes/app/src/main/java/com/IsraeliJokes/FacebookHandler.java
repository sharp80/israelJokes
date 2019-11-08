///*
// * Copyright 2010 Facebook, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *    http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package com.IsraeliJokes;
//
//
//import java.util.Vector;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Activity;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.IsraeliJokes.SessionEvents.AuthListener;
//import com.IsraeliJokes.SessionEvents.LogoutListener;
//import com.facebook.android.AsyncFacebookRunner;
//import com.facebook.android.Facebook;
//import com.facebook.android.FacebookError;
//import com.facebook.android.Util;
//
//
//public class FacebookHandler extends Activity {
//
//    // Your Facebook Application ID must be set before running this example
//    // See http://www.facebook.com/developers/createapp.php
//    public static final String APP_ID = "170936369682906";
//
//    private LoginButton mLoginButton;
//    private TextView mText;
//
//    private Facebook mFacebook;
//    private AsyncFacebookRunner mAsyncRunner;
//    private String [] m_Permissions = {"publish_stream"};
//
//	private String m_CategoryId;
//
//	private int m_JokeId;
//    /** Called when the activity is first created. */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//       /* try {
//    	   PackageInfo info = getPackageManager().getPackageInfo( "com.IsraeliJokes",
//    			   												PackageManager.GET_SIGNATURES);
//    	   for (Signature signature : info.signatures) {
//    	        MessageDigest md = MessageDigest.getInstance("SHA");
//    	        md.update(signature.toByteArray());
//    	        Log.i("PXR", Base64.encodeBytes(md.digest()));
//    	   }
//    	}
//    	catch (NameNotFoundException e) {}
//    	catch (NoSuchAlgorithmException e) {}*/
//
//
//        if (APP_ID == null) {
//            Util.showAlert(this, "Warning", "Error");
//            finish();
//        }
//        Intent intent = getIntent();
//        m_CategoryId = intent.getStringExtra( "CategoryId"  );
//        m_JokeId = intent.getIntExtra("JokeId", -1);
//
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView( R.layout.mainfacebook ) ;
//
///*
//
//        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
//        setContentView( R.layout.mainfacebook ) ;
//        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.facebook_page_title);
//        */
//
//
//        mLoginButton = (LoginButton) findViewById(R.id.login);
//        mText = (TextView) FacebookHandler.this.findViewById(R.id.facebookTxt);
//
//       	mFacebook = new Facebook(APP_ID);
//       	mAsyncRunner = new AsyncFacebookRunner(mFacebook);
//
//        SessionStore.restore(mFacebook, this);
//        SessionEvents.addAuthListener(new SampleAuthListener());
//        SessionEvents.addLogoutListener(new SampleLogoutListener());
//        mLoginButton.init(this, mFacebook, m_Permissions);
//
//        if (mFacebook.isSessionValid())
//        {
//        	Post();
//        }
//        Button CloseFacebookDialog = (Button)findViewById(R.id.CloseFacebookDialog);
//        CloseFacebookDialog.setOnClickListener(
//    			new OnClickListener() {
//    				public void onClick(View v) {
//    					finish();
//    				}
//    			});
//
//    }
//    public void postToWall(){
//    	Bundle parameters = new Bundle();
//    	if ( Category.m_JokesArr == null )
//    		finish();
//    	JokeEntry jokeEntry = Category.getJoke(m_CategoryId, m_JokeId);
//    	if (jokeEntry == null)
//    		finish();
//    	parameters.putString( "name" 		, getString(R.string.fb_name) );// the message to post to the wall
//    	parameters.putString("link", "http://apps.facebook.com/IsraeliJokes");// the message to post to the wall
//
//    	if ( jokeEntry.JokeVideo.length() > 0 )
//    	{
//    		String youTubeHtmlPref =  CategoriesPage.m_YouTubePrefixForFacebook;
//    		parameters.putString("picture", "https://img.youtube.com/vi/" + jokeEntry.JokeVideo + "/0.jpg" );// the message to post to the wall
//    		parameters.putString("source",youTubeHtmlPref + jokeEntry.JokeVideo);// the message to post to the wall
//    		parameters.putString("message",jokeEntry.JokeTitle);
//
//    	}
//    	else
//    	{
//    		parameters.putString("message", jokeEntry.JokeStr );
//    		parameters.putString("picture", "https://lh5.ggpht.com/O1DaTtKh_IXpKrehiafkuqkiAv8g13xi1dmIIq2-Qo7PPj1SGd9Bvr8ynvrJJ5e9-lo=w124");// the message to post to the wall
//    	}
//
//    	final Bundle parametersFinal = parameters;
//    	Thread thread = new Thread()
//    	{
//    		@Override
//    		public void run()
//    		{
//    			try {
//    				String response = mFacebook.request("me/feed", parametersFinal, "POST");
//    				Log.d("Tests", "got response: " + response);
//    			} catch (Exception e) {
//    				e.printStackTrace();
//    			}
//    		}
//
//    	};
//    	thread.start();
//    	finish();
//    }
//    private void Post()
//    {
//		postToWall( );
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode,
//                                    Intent data) {
//        mFacebook.authorizeCallback(requestCode, resultCode, data);
//    }
//
//    public class SampleAuthListener implements AuthListener {
//
//        public void onAuthSucceed() {
//            mText.setText("You have logged in! ");
//            //mPostButton.setVisibility(View.VISIBLE);
//            Post();
//        }
//
//        public void onAuthFail(String error) {
//            mText.setText("Login Failed: " + error);
//        }
//    }
//
//    public class SampleLogoutListener implements LogoutListener {
//        public void onLogoutBegin() {
//            mText.setText("Logging out...");
//        }
//
//        public void onLogoutFinish() {
//            mText.setText("You have logged out! ");
//           // mPostButton.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    public class SampleRequestListener extends BaseRequestListener {
//
//        public void onComplete(final String response, final Object state) {
//            try {
//                // process the response here: executed in background thread
//                Log.d("Facebook-Example", "Response: " + response.toString());
//                JSONObject json = Util.parseJson(response);
//                final String name = json.getString("name");
//
//                // then post the processed result back to the UI thread
//                // if we do not do this, an runtime exception will be generated
//                // e.g. "CalledFromWrongThreadException: Only the original
//                // thread that created a view hierarchy can touch its views."
//                FacebookHandler.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        mText.setText("Hello there, " + name + "!");
//                    }
//                });
//            }
//            catch (JSONException e) {
//                Log.w("Facebook-Example", "JSON Error in response");
//            } catch (FacebookError e) {
//                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
//            }
//        }
//    }
//
//    public class SampleUploadListener extends BaseRequestListener {
//
//        public void onComplete(final String response, final Object state) {
//            try {
//                // process the response here: (executed in background thread)
//                Log.d("Facebook-Example", "Response: " + response.toString());
//                JSONObject json = Util.parseJson(response);
//                final String src = json.getString("src");
//
//                // then post the processed result back to the UI thread
//                // if we do not do this, an runtime exception will be generated
//                // e.g. "CalledFromWrongThreadException: Only the original
//                // thread that created a view hierarchy can touch its views."
//                FacebookHandler.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        mText.setText("Hello there, photo has been uploaded at \n" + src);
//                    }
//                });
//            } catch (JSONException e) {
//                Log.w("Facebook-Example", "JSON Error in response");
//            } catch (FacebookError e) {
//                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
//            }
//        }
//    }
//    public class WallPostRequestListener extends BaseRequestListener {
//
//        public void onComplete(final String response, final Object state) {
//            Log.d("Facebook-Example", "Got response: " + response);
//            String message = "<empty>";
//            try {
//                JSONObject json = Util.parseJson(response);
//                message = json.getString("message");
//            } catch (JSONException e) {
//                Log.w("Facebook-Example", "JSON Error in response");
//            } catch (FacebookError e) {
//                Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
//            }
//            FacebookHandler.this.runOnUiThread(new Runnable() {
//                public void run() {
//             //       mText.setText(text);
//                }
//            });
//        }
//    }
//
//    public class WallPostDeleteListener extends BaseRequestListener {
//
//        public void onComplete(final String response, final Object state) {
//            if (response.equals("true")) {
//                Log.d("Facebook-Example", "Successfully deleted wall post");
//                FacebookHandler.this.runOnUiThread(new Runnable() {
//                    public void run() {
//                        mText.setText("Deleted Wall Post");
//                    }
//                });
//            } else {
//                Log.d("Facebook-Example", "Could not delete wall post");
//            }
//        }
//    }
//
//    public class SampleDialogListener extends BaseDialogListener {
//
//        public void onComplete(Bundle values) {
//            final String postId = values.getString("post_id");
//            if (postId != null) {
//                Log.d("Facebook-Example", "Dialog Success! post_id=" + postId);
//                mAsyncRunner.request(postId, new WallPostRequestListener());
//
//            } else {
//                Log.d("Facebook-Example", "No wall post made");
//            }
//        }
//    }
//
//}
