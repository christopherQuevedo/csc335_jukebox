package model;
/**
 * ID reader for JukeBox. Holds all base student accounts, and adds a student when the Admin
 * creates a new one. Used for authenticating log ins in the JukeBox.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class IDReader implements Serializable{

	private ArrayList<Student> students = new ArrayList<>();
	
	public IDReader() {
		students.add(new Student("Chris", "1"));
		students.add(new Student("Devon", "22"));
		students.add(new Student("River", "333"));
		students.add(new Student("Ryan", "4444"));
		students.add(new Admin("Merlin", "7777777"));
	}
	
	//getters
	public ArrayList<Student> getStudents() {
		return students;
	}
	
	/**
	 * authenticate() -- checks if the given user name and password is in the collection of student logins.
	 * 
	 * @param name -- user name entered by user
	 * @param password -- password entered by user
	 * @return true if the user name and password combo is in the database of log ins, false otherwise
	 */
	public Student authenticate(String name, String password) {
		for (Student student : students) {
			if (student.getUsername().equals(name) && student.getPassword().equals(password))
				return student;
		}
		
		return null;
	}
	
	/**
	 * addStudent(Student) -- Admin adding a new student to the database of student log ins
	 * @param newStudent - student to be added
	 */
	public void addStudent(Student newStudent) {
		students.add(newStudent);
	}
	
}
