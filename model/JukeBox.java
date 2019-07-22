package model;

import java.io.Serializable;

/**
 * Models a JukeBox. This class handles playing songs, verifying students, and everything related
 * to the JukeBox queue.
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;


public class JukeBox implements Serializable{
	private IDReader reader;
	private ArrayList<Song> songQueue;
	private Student curStudent;
	private Song curSong;
	private LocalDate session;
	private ArrayList<Song> songList;
	
	public JukeBox() {
		this.reader = new IDReader();
		songQueue = new ArrayList<Song>();
		curSong = null;
		curStudent = null;
		session = LocalDate.now();
		songList = new ArrayList<Song>();
		songList.add(new Song("Pokemon Capture", 5, "Pikachu", "Capture.mp3"));
		songList.add(new Song("Loping Sting", 5, "Kevin MacLeod", "LopingSting.mp3"));
		songList.add(new Song("Danse Macabre", 34, "Kevin MacLeod", "DanseMacabreViolinHook.mp3"));
		songList.add(new Song("Determined Tumbao", 20, "FreePlay Music", "DeterminedTumbao.mp3"));
		songList.add(new Song("Swing Cheese", 15, "FreePlay Music", "SwingCheese.mp3"));
		songList.add(new Song("The Curtain Rises", 28, "Kevin MacLeod", "TheCurtainRises.mp3"));
		songList.add(new Song("UntameableFire", 282, "Pierre Langer", "UntameableFire.mp3"));
	}
	
	//getters
	public IDReader getIDReader() {
		return reader;
	}
	public ArrayList<Song> getSongQueue() {
		return songQueue;
	}
	public Student getCurrentStudent() {
		return curStudent;
	}
	public Song getCurrentSong() {
		return curSong;
	}
	public ArrayList<Song> getSongList() {
		return songList;
	}
	
	//setters
	public void setCurrentStudent(Student student) {
		curStudent = student;
	}
	public void setCurrentSong(Song song) {
		curSong = song;
	}
	
	/**
	 * verifyStudent(String, String) -- if the correct credentials are given at login, this method
	 * updates the current user.
	 * 
	 * @param username - username for user
	 * @param password - password for user
	 * @return true if the correct credentials were given, false otherwise
	 */
	public boolean verifyStudent(String username, String password) {
		Student newUser = reader.authenticate(username, password);
		if (newUser != null) {
			curStudent = newUser;
			return true;
		}
		return false;
	}

	/**
	 * getSessionText() -- creates the String that displays how many songs are on the queue and
	 * how much time left in that session.
	 * 
	 * @return string with details of current session
	 */
	public String getSessionText() {
		long time = 90000;
		int songsPlayed = 0;
		if (curStudent != null) {
			time = 90000 - curStudent.getTotalSeconds();
			songsPlayed = curStudent.getNumSongsToday();
		}
		
		long hours = TimeUnit.SECONDS.toHours(time);
		time -= TimeUnit.HOURS.toSeconds(hours);
		
		long minutes = TimeUnit.SECONDS.toMinutes(time);
		time -= TimeUnit.MINUTES.toSeconds(minutes);
		
		long seconds = TimeUnit.SECONDS.toSeconds(time);
		return String.format("%d selected, %02d:%02d:%02d", songsPlayed, hours, minutes, seconds);
	}

	/**
	 * dequeue() -- removes the song at index 0 from songQueue and returns it.
	 * Updates curSong to the song after the dequeued song.
	 * 
	 * @return song that was just dequeued
	 */
	public Song dequeue() {
		Song toRemove = songQueue.remove(0);
		if(!songQueue.isEmpty()) {
			curSong = songQueue.get(0);
			return toRemove;
		}
		curSong = null;
		return toRemove;
	}
	
	/**
	 * Used for testing. "Fast forwards" to tomorrow to test if a new juke box session
	 * is started on a new day.
	 */
	public void makeItTomorrow() {
		session = session.plusDays(1);
		if (curStudent != null)
			curStudent.updateDay();
	}
	
	/**
	 * isNewDay() -- after midnight, this resets how many times each song has been played
	 * for the new day.
	 * 
	 * @return true if after midnight, false otherwise
	 */
	public boolean isNewDay() {
		if (LocalDate.now().getDayOfYear() != session.getDayOfYear()) {
			session = LocalDate.now();
			for (Song song : songList)
				song.resetPlays();
			if (curStudent != null)
				curStudent.updateDay();
			return true;
		}
		
		return false;
	}

	/**
	 * enqueue(Song) -- 	if student is allowed to enqueue song (meaning check if student 
	 * can still play music today, in their remaining career, and if the song hasn't been played
	 * 3 times today
	 * 
	 * @param newSong - song to be added to the queue
	 * @return true if the student is allowed to play the song, false otherwise
	 */
	public boolean enqueue(Song newSong) {
		if (curStudent == null)
			return false;
		else if (newSong.canPlay() && curStudent.canPlaySong(newSong)) {
			if (songQueue.isEmpty()) {
				curSong = newSong;
				//playSong();
			}
			
			songQueue.add(newSong);
			curStudent.incrementSongsToday();
			curStudent.incrementSeconds(newSong.getSeconds());
			return true;
		}
		return false;
	}

}
