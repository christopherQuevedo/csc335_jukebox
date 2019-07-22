package model;

/**
 * Class that represents a JukeBox Administrator. This is only used as a way to differentiate
 * between a student and admin.
 *
 */
public class Admin extends Student {

	public Admin(String name, String pass) {
		super(name, pass);
	}

}
