package com.IsraeliJokes;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class RegisterNewJoke extends Activity{
	protected ProgressDialog m_ProgressDialog;

	class MyOnItemSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent,
				View view, int pos, long id) {
			TextView jokeStr = (TextView)findViewById( R.id.jokeStrDesc );
			if ( id == Category.ConvertCategoryIdToIndex( Category.VIDEOS_CATEGORY_ID ) )
				jokeStr.setText(getString(R.string.add_new_joke_utube));
			else
				jokeStr.setText(getString(R.string.add_new_joke_joke));

		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView( R.layout.register_new_joke );

		Spinner spinner = (Spinner)findViewById( R.id.categoriesList ); 
		String categoriesList[] = GetCategoriesNames();
		GetAllCategories( spinner, categoriesList);
		spinner.setOnItemSelectedListener(new MyOnItemSelectedListener());



		Button CancelJokeAdd = (Button)findViewById( R.id.CancelJokeAdd );
		CancelJokeAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});


		Button SendNewJoke = (Button)findViewById( R.id.SendNewJoke);
		SendNewJoke.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String url = Category.m_SiteUrl + "/AddNewJoke.aspx";
				PostServer( url );
			}
		});
		setLayout();
	}
	private void setLayout(){
		EditText addJokeTitle = (EditText)findViewById( R.id.addJokeTitle );
		addJokeTitle.setGravity(GlobalGravity.GetGlobalGravity(this));
		EditText addJokeStr = (EditText)findViewById( R.id.addJokeStr );
		addJokeStr.setGravity(GlobalGravity.GetGlobalGravity(this));
	}

	private void PostServer(String url) 
	{
		EditText addJokeTitle = (EditText)findViewById( R.id.addJokeTitle );
		Spinner categoriesList= (Spinner)findViewById( R.id.categoriesList );
		EditText addJokeStr = (EditText)findViewById( R.id.addJokeStr );
		EditText addUserEmail = (EditText)findViewById( R.id.addUserEmail );
		EditText addUserName = (EditText)findViewById( R.id.addUserName );

		String JokeHeadLine = addJokeTitle.getText().toString();
		String JokeText = addJokeStr.getText().toString();
		String UserEmail = addUserEmail.getText().toString();
		String UserName = addUserName.getText().toString();
		String jokeCategory = Category.ConvertCategoryIndexToId( 
				(int)categoriesList.getSelectedItemPosition());

		if (JokeHeadLine.length() == 0 || JokeText.length() == 0 	)
		{
			return ;
		}
		// Prepare all data to send to server
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		BasicNameValuePair par;

		par = new BasicNameValuePair( "JokeHeadline", JokeHeadLine);
		params.add( par  );

		par = new BasicNameValuePair( "JokeText", JokeText);
		params.add( par  );

		par = new BasicNameValuePair( "UserEmail", UserEmail);
		params.add( par  );

		par = new BasicNameValuePair( "UserName", UserName);
		params.add( par  );

		par = new BasicNameValuePair( "JokeCategory",jokeCategory);
		params.add( par  );
		final TextView textOutput = (TextView)findViewById(R.id.RegisterJokeResult);

		m_ProgressDialog = ProgressDialog.show(	RegisterNewJoke.this, 
				getString(R.string.adding_new_joke), 
				"", 
				true,
				false
				);
		m_ProgressDialog.show();

		Handler finishedPosting = new Handler()
		{
			@Override 
			public void handleMessage(Message msg){
				if (msg.arg1 == -1)
				{
					textOutput.setText(getString(R.string.adding_new_joke_failed));
					m_ProgressDialog.dismiss();
				}
				else
				{
					textOutput.setText(getString(R.string.adding_new_joke_done));
					m_ProgressDialog.dismiss();
					finish();
				}

			}

		};
		PostServerAsync postAsync = new PostServerAsync(params,
				finishedPosting);
		postAsync.execute(url);
	}



	String[] GetCategoriesNames()
	{
		int numOfCategories = Category.m_CategoriesArr.size();
		String res[] = new String[numOfCategories-3];
		for (int ind = 0 ; ind < numOfCategories ; ind++)
		{
			String catId = Category.ConvertCategoryIndexToId(ind);
			if ( catId.equals( Category.FAVORITE_CATEGORY_ID) || 
					catId.equals( Category.TOP_TEN_CATEGORY) ||
					catId.equals( Category.RANDOM_JOKE_CATEGORY_ID)  )
				continue;
			res[ind]=Category.m_CategoriesArr.get(ind).Name;
		}
		return res;

	}

	private void GetAllCategories(Spinner spinner,  String[] categoryListArr )
	{
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, 
				android.R.layout.simple_spinner_dropdown_item, 
				categoryListArr);
		arrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item  );

		spinner.setAdapter(arrayAdapter);
	}

	/*
	public void sendImage() 
	{

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.icon);           ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream); //compress to which format you want.
        byte [] byte_arr = stream.toByteArray();
        //String image_str = new String(byte_arr);
        String image_str; 
        StringBuffer hexString = new StringBuffer();
        for (int i=0; i < byte_arr.length; i++)
            hexString.append(Integer.toHexString(0xFF & byte_arr[i]) );
        image_str = hexString.toString();

      //  String image_str = Base64.encode(byte_arr);
       // String image_str = byte_arr.toString();
        ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("image",image_str));
        try{
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://www.mayaron.com/appsboard/uploadForm.aspx");
            httppost.setEntity( new UrlEncodedFormEntity(nameValuePairs) );
            HttpResponse response = httpclient.execute(httppost);

           // String the_string_response = convertResponseToString(response);
           // Log.d(TAG,the_string_response);
          //  Toast.makeText(UploadImage.this, "Response " + the_string_response, Toast.LENGTH_LONG).show();
        }catch(Exception e){
         //     Toast.makeText(UploadImage.this, "ERROR " + e.getMessage(), Toast.LENGTH_LONG).show();
              System.out.println("Error in http connection "+e.toString());
        }
    }

	public String convertResponseToString(HttpResponse response) throws IllegalStateException, IOException
	{
	  String res = "";
	  StringBuffer buffer = new StringBuffer();
	  InputStream inputStream = response.getEntity().getContent();
	  int contentLength = (int) response.getEntity().getContentLength(); //getting content length�..
	  Toast.makeText(this, "contentLength : " + contentLength, Toast.LENGTH_LONG).show();
	  if (contentLength < 0){
	  }
	  else{
	         byte[] data = new byte[512];
	         int len = 0;
	         try
	         {
	             while (-1 != (len = inputStream.read(data)) )
	             {
	                 buffer.append(new String(data, 0, len)); //converting to string and appending  to stringbuffer�..
	             }
	         }
	         catch (IOException e)
	         {
	             e.printStackTrace();
	         }

	         try
	         {
	             inputStream.close(); // closing the stream�..
	         }

	         catch (IOException e)
	         {
	             e.printStackTrace();
	         }

	         res = buffer.toString();     // converting stringbuffer to string�..

	         Toast.makeText(this, "Result : " + res, Toast.LENGTH_LONG).show();

	         //System.out.println("Response => " +  EntityUtils.toString(response.getEntity()));
	  }
	  return res;
	}
	 */

}
