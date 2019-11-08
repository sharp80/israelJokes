package com.IsraeliJokes;
import java.io.File;
import java.io.FilenameFilter;

class ImagesFilter implements FilenameFilter {
    public boolean accept(File dir, String name) {
    	name = name.toLowerCase(); 
        return ( name.endsWith(".jpg") ||
        		 name.endsWith(".jpeg") || 
        		 name.endsWith(".png"));
    }
}