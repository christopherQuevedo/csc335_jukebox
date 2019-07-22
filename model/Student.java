package model;

import java.io.Serializable;

/**
 * Represents a student that uses the juke box. Holds their
 * login information, number of songs played that day, and
 * total seconds of songs that have been played that day. 
 */

public class Student implements Serializable{
	
	private String username;
	private String password;
	private int numSongsToday, totalSeconds;

	public Student(String name, String pass) {
		username = name;
		password = pass;
		numSongsToday = 0;
	}
	
	//getters
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public int getNumSongsToday() {
		return numSongsToday;
	}
	public int getTotalSeconds() {
		return totalSeconds;
	}
	
	/**
	 * updateDay() -- on a new day, number of songs and total seconds reset.
	 */
	public void updateDay() {
		numSongsToday = 0;
		totalSeconds = 0;
	}
	
	/**
	 * incrementSeconds(int) -- Adds to a students totalSeconds 
	 * by seconds.
	 * @param seconds - amount of seconds to increment totalSeconds
	 */
	public void incrementSeconds(int seconds) {
		totalSeconds += seconds;
	}
	
	/**
	 * incrementSongsToday() -- increments the number of songs student has played
	 * today.
	 */
	public void incrementSongsToday() {
		numSongsToday++;
	}
	
	/**
	 * canPlaySong(Song) -- checks if the song has already been played three times that day
	 * and if the student has the ability to play the song in the current session
	 * 
	 * @param song - song to be played
	 * @return true if the song can be played, false otherwise
	 */
	public boolean canPlaySong(Song song) {
		if (numSongsToday >= 3)
			return false;
		if (totalSeconds + song.getSeconds() > 90000 )
			return false;
		return true;
	}

}
