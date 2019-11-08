package com.IsraeliJokes;


//package net.learn2develop.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter 
{
    public static final String KEY_ROWID 		= "_id";
    public static final String KEY_JOKE 		= "joke";
    public static final String KEY_TITLE 		= "title";
    public static final String KEY_IMAGE 		= "image";
    public static final String KEY_CATEGORY 	= "category";  
    public static final String KEY_FAVORITE 	= "favorite";  
    public static final String KEY_WAS_READ 	= "was_read";  
    public static final String KEY_LIKE_ADDED 	= "like_added";  

    private static final String TAG = "DBAdapter";
    
    private static final String DATABASE_NAME = "jokesDb";
    private static final String DATABASE_TABLE = "favorites";
    private static final String DATABASE_TABLE_REVIEWS = "reviews";

    private static final int DATABASE_VERSION = 8;
    private static final String REVIEWS_TABLE_CREATE = 
    		"create table " + DATABASE_TABLE_REVIEWS + " ("
    		        + KEY_ROWID + " text primary key "
    		        + ");";
    		       
    private static final String DATABASE_CREATE =
        "create table " + DATABASE_TABLE + " ("
        + KEY_ROWID + " text primary key, "
        + KEY_JOKE  + " text not null,"
        + KEY_TITLE + " text not null, " 
        + KEY_IMAGE + " text, "
        + KEY_CATEGORY + " text not null,"
        + KEY_FAVORITE + " integer not null,"
        + KEY_WAS_READ + " integer not null,"
        + KEY_LIKE_ADDED + " integer not null"
        + ");";
        
    private final Context context; 
    
    private DatabaseHelper DBHelper;
    private SQLiteDatabase m_db;

    public DBAdapter(Context ctx) 
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }
        
    private static class DatabaseHelper extends SQLiteOpenHelper 
    {
        DatabaseHelper(Context context) 
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) 
        {
            db.execSQL(DATABASE_CREATE);
            db.execSQL(REVIEWS_TABLE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, 
        int newVersion) 
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion 
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE );
            db.execSQL("DROP TABLE IF EXISTS "+ DATABASE_TABLE_REVIEWS );

            onCreate(db);
        }
    }    
    
    //---opens the database---
    public DBAdapter open() throws SQLException 
    {
        m_db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---    
    public void close() 
    {
        DBHelper.close();
    }
    
    //---insert a joke into the database---
    public long insertJoke( int jokeId, 
    						String joke, 
    						String title, 
    						String image, 
    						String category,
    						boolean IsFavorite,
    						boolean WasRead,
    						boolean LikeAdded) 
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, jokeId);
        initialValues.put(KEY_JOKE, joke);
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_IMAGE, image);
        initialValues.put(KEY_CATEGORY, category);
        initialValues.put(KEY_FAVORITE, IsFavorite ? 1 : 0);
        initialValues.put(KEY_WAS_READ, WasRead ? 1 : 0);
        initialValues.put(KEY_LIKE_ADDED, LikeAdded ? 1 : 0);
        
        return m_db.insert(DATABASE_TABLE, null, initialValues);
    }

    //---deletes a particular title---
    public boolean deleteTitle(long rowId) 
    {
        return m_db.delete(DATABASE_TABLE, KEY_ROWID + 
        		"=" + rowId, null) > 0;
    }

    //---retrieves all the jokes---
    public Cursor getAllJokes() 
    {
        return m_db.query(DATABASE_TABLE, new String[] {
        		KEY_ROWID, 
        		KEY_JOKE,
        		KEY_TITLE,
        		KEY_IMAGE,
                KEY_CATEGORY,
                KEY_FAVORITE,
                KEY_WAS_READ,
                KEY_LIKE_ADDED}, 
                null, 
                null, 
                null, 
                null, 
                null,
                null
                );
    }

    //---retrieves a particular joke---
    public Cursor getJoke(long rowId) throws SQLException 
    {
        Cursor mCursor =
                m_db.query(true, DATABASE_TABLE, new String[] {
                		KEY_ROWID,
                		KEY_JOKE, 
                		KEY_TITLE,
                		KEY_IMAGE,
                		KEY_CATEGORY,
                		KEY_FAVORITE,
                        KEY_WAS_READ,
                        KEY_LIKE_ADDED
                		}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    //---updates a was read---
    public boolean updateWasRead(long rowId, boolean WasRead) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_WAS_READ, WasRead ? 1 : 0);
        
        return m_db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    //---updates if joke was added to removed from favorites ---
    public boolean updateFavorite(long rowId, boolean InFavorite) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_FAVORITE, InFavorite ? 1 : 0);
        return m_db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
    }
    
    //---updates a that like was added---
    public boolean updateLikeAdded(long rowId, boolean LikeAdded) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_LIKE_ADDED, LikeAdded ? 1 : 0);
        
        return m_db.update(DATABASE_TABLE, args, 
                         KEY_ROWID + "=" + rowId, null) > 0;
	}
    
    private long insertReviewedJoke( long jokeId )
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_ROWID, jokeId);
        return m_db.insert(DATABASE_TABLE_REVIEWS, null, initialValues);
    }
    
    private Cursor GetReviewedJoke(long rowId)
    {
    	try{
    	Cursor mCursor =
                m_db.query(true, DATABASE_TABLE_REVIEWS, new String[] {
                		KEY_ROWID
                		}, 
                		KEY_ROWID + "=" + rowId, 
                		null,
                		null, 
                		null, 
                		null, 
                		null);
        return mCursor;
    	} catch (Exception e) {
    		Log.e(TAG,e.toString());
    		return null;
    	}
    }
    public boolean wasJokeReviewed(long rowId)
    {
    	Cursor mCursor = GetReviewedJoke( rowId );
        if (mCursor != null) {
        	boolean jokeReviewed = mCursor.getCount() > 0; 
        	mCursor.close();
            return  jokeReviewed;
        } else {
        	return false;
        }
    }
    public boolean updateJokeReviewed(long rowId, boolean reviewed)
    {
    	Cursor mCursor = GetReviewedJoke( rowId );
        if (mCursor != null && !reviewed) {
        	mCursor.close();
        	return m_db.delete(DATABASE_TABLE_REVIEWS, KEY_ROWID + 
              					"=" + rowId, null) > 0;
        }
        else
        {
        	mCursor.close();
        	return insertReviewedJoke( rowId ) > 0;
        }
    }
}
