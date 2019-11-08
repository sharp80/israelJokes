package com.IsraeliJokes;


public class JokeEntry {
	public int JokeId;
	public String JokeTitle;
	public String JokeStr;
	public String JokePic;
	public String JokeCategory;
	public boolean JokeReadInRandom;
	public boolean JokeInFavorite;
	public boolean JokeWasRead;
	public boolean AddedLike;
	public double LikeRate;
	public String JokeVideo;
	public int JokeIndex;
	public boolean NewJoke;

	public JokeEntry(){
		JokeId=-1;
		JokeStr = "";
		JokePic ="";
		JokeTitle = "";
		JokeCategory = null;
		JokeReadInRandom = false;
		JokeInFavorite = false;
		JokeWasRead = false;
		AddedLike = false;
		LikeRate = 0;
		JokeVideo = null;
		JokeIndex = -1;
		NewJoke = false;
	}
	public JokeEntry(JokeEntry jokeEntry) {
		JokeId = jokeEntry.JokeId;
		JokeStr = jokeEntry.JokeStr;
		JokePic = jokeEntry.JokePic;
		JokeTitle = jokeEntry.JokeTitle;
		JokeCategory = jokeEntry.JokeCategory;
		JokeReadInRandom = jokeEntry.JokeReadInRandom;
		JokeInFavorite = jokeEntry.JokeInFavorite;
		JokeWasRead = jokeEntry.JokeWasRead;
		AddedLike = jokeEntry.AddedLike;
		LikeRate =  jokeEntry.LikeRate;
		JokeVideo = jokeEntry.JokeVideo;
		JokeIndex = jokeEntry.JokeIndex ;
		NewJoke = jokeEntry.NewJoke ;
	}
}
