package model;

import java.io.Serializable;

/**
 * Songs that will be played through the juke box. Each song holds 
 * the name of the song, artist, length (in seconds), and the filename/path
 * to the song itself. This class is only used to store information about each Song
 * in a concise way.
 */
public class Song implements Serializable{
	
	private String name;
	private int seconds;
	private String artist;
	private String filename;
	private String fpath;
	private int numTimesPlayed;
	
	public Song(String newName, int newSeconds, String newArtist, String newFileName) {
		name = newName;
		seconds = newSeconds;
		artist = newArtist;
		filename = newFileName;
		fpath = "songfiles/" + filename;
		numTimesPlayed = 0;
	}	
	
	//getters
	public String getName() {
		return name;
	}
	public int getSeconds() {
		return seconds;
	}
	public String getArtist() {
		return artist;
	}
	public String getFpath() {
		return fpath;
	}
	public int getNumTimesPlayed() {
		return numTimesPlayed;
	}
	
	/**
	 * Returns a song as string
	 */
	public String toString() {
		return name + "-" + artist;
	}
	
	/**
	 * getSecondsAsString() -- get a song's seconds as string
	 * 
	 * @return - a song's seconds as a string
	 */
	public String getSecondsAsString() {
		return String.format("%d:%02d", seconds / 60, seconds % 60);
	}
	
	/**
	 * playSong() -- increment number of times song has been played
	 */
	public void playSong() {
		numTimesPlayed++;
	}
	
	/**
	 * resetPlays() -- set numTimesPlayed to 0
	 */
	public void resetPlays() {
		numTimesPlayed = 0;
	}
	
	/**
	 * canPlay() -- checks if the song has already been played three times that day
	 * 
	 * @param song - song to be played
	 * @return true if the song can be played, false otherwise
	 */
	public boolean canPlay() {
		if (numTimesPlayed >= 3)
			return false;
		return true;
	}
	
}