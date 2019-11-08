package com.IsraeliJokes;

import java.util.Locale;

import android.app.Application;
import android.content.res.Configuration;

public class MyApplication extends Application
{
    private Locale locale = null;

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        if (locale != null)
        {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            getBaseContext().getResources().updateConfiguration(newConfig, getBaseContext().getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        Configuration config = 
        		new Configuration(getBaseContext().getResources().getConfiguration());
        
        String lang = getResources().getString(R.string.language) ;
        String country = getResources().getString(R.string.country) ;
        if ( lang != null  && ! config.locale.getLanguage().equals(lang))
        {
            locale = new Locale(lang, country);
            Locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, 
            		getBaseContext().getResources().getDisplayMetrics());
        }
    }
}
