package controller_view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Optional;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;
import model.Admin;
import model.JukeBox;
import model.Song;
import model.Student;

/**
 * This program is a functional spike to determine the interactions are 
 * actually working. It is an event-driven program with a graphical user
 * interface to affirm the functionality all Iteration 1 tasks have been 
 * completed and are working correctly. This program will be used to 
 * test your code for the first 100 points of the JukeBox project.
 */

public class JukeboxStartGUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private JukeBox jukeBox;
	private GridPane songController = new GridPane();
	private BorderPane all = new BorderPane();
	private Button loginButton = new Button("Login");
	private Button logoutButton = new Button("Logout");
	private Button playButton = new Button("Add To Queue");

	private Label nameLabel = new Label("Account Name");
	private Label passLabel = new Label("     Password");
	private Label message = new Label("Login first");

	private ObservableList<Song> songCollection;
	private TableView<Song> tableView = new TableView<>();
	private GridPane loginPane = new GridPane();
	private ListView<Song> songQueueView = new ListView<>();
	private ObservableList<Song> observableSongQueue = FXCollections.observableArrayList();

	private TextField nameField = new TextField();
	private PasswordField passField = new PasswordField();
	private MediaPlayer mediaPlayer;
	private Alert alert = new Alert(AlertType.INFORMATION);
	private final static String persistedFileName = "jukeBoxState";

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("JukeBox");
		jukeBox = new JukeBox();

		makeLoginPane();
		initializeJukeBox();
		songCollection = FXCollections.observableArrayList(jukeBox.getSongList());
		setUpSongController();

		playButton.setOnAction(new SongButtonHandler());

		Scene scene = new Scene(all, 720, 630);
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setOnCloseRequest(new WritePersistentObjectOrNot());
	}

	/**
	 * playSong() -- plays a song using JavaFX MediaPlayer.
	 */
	public void playSong() {
		File file = new File(jukeBox.getCurrentSong().getFpath());
		URI uri = file.toURI();
		Media media = new Media(uri.toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setOnEndOfMedia(new EndOfSongHandler());
		mediaPlayer.play();
	}

	/**
	 * Handler for adding a song to the queue once the last one ends.
	 */
	private class EndOfSongHandler implements Runnable {
		@Override
		public void run() {

			observableSongQueue.remove(0);
			jukeBox.dequeue();

			if(!jukeBox.getSongQueue().isEmpty()) {
				jukeBox.setCurrentSong(jukeBox.getSongQueue().get(0));
				playSong();
			}
		}
	}

	/**
	 * setUpSongController() -- makes the tableView for the song 
	 * collection and the songQueueView for the songQueue.
	 */
	public void setUpSongController() {
		TableColumn<Song, String> numPlays = new TableColumn<>("Plays");
		TableColumn<Song, String> title = new TableColumn<>("Title");
		TableColumn<Song, String> artist = new TableColumn<>("Artist");
		TableColumn<Song, String> time = new TableColumn<>("Time");

		tableView.getColumns().addAll(numPlays, title, artist, time);

		numPlays.setCellValueFactory(
				new PropertyValueFactory<Song,String>("numTimesPlayed")
				);

		title.setCellValueFactory(
				new PropertyValueFactory<Song,String>("name")
				);
		artist.setCellValueFactory(
				new PropertyValueFactory<Song,String>("artist")
				);

		time.setCellValueFactory(
				new PropertyValueFactory<Song,String>("secondsAsString")
				);

		tableView.setItems(songCollection);
		tableView.setMinWidth(365);
		songController.add(tableView, 0, 1);
		
		playButton.setPadding(new Insets(10));
		playButton.setMinWidth(365);

		songController.add(playButton, 0, 2);
		
		DropShadow ds = new DropShadow();
		ds.setOffsetY(2.0f);
		ds.setColor(Color.color(0.4f, 0.4f, 0.4f));
		
	    
		Label songListLabel = new Label("Song List");
		songListLabel.setFont(Font.font("Century Gothic", FontWeight.BOLD, 20));
		songListLabel.setEffect(ds);
		songController.add(songListLabel, 0, 0);

		songController.setHgap(15);
		songController.setVgap(15);
		songController.setPadding(new Insets(10));

		observableSongQueue = FXCollections.observableArrayList(jukeBox.getSongQueue());
		songQueueView.setItems(observableSongQueue);
		songQueueView.setMinWidth(300);

		Label songQueueLabel = new Label("Song Queue");
		songQueueLabel.setFont(Font.font("Century Gothic", FontWeight.BOLD, 20));
		songQueueLabel.setEffect(ds);

		songController.add(songQueueLabel, 1, 0);
		songController.add(songQueueView, 1, 1);

		all.setTop(songController);

	}

	/**
	 * makeLogButtons() -- adds the login/logout buttons to the pane and creates
	 * handlers for both
	 */
	public void makeLoginPane() {
		loginButton.setMinWidth(60);
		loginButton.setMinHeight(30);
		loginButton.setStyle("-fx-background-radius:50px;");
		
		nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
		passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 13));
		message = new Label("Login first");
		
		loginPane.add(loginButton, 2, 1);
		loginPane.add(logoutButton, 2, 2);
		loginPane.add(nameLabel, 0, 1);
		loginPane.add(passLabel, 0, 2);
		loginPane.add(passField, 1, 2);
		loginPane.add(nameField, 1, 1);
		loginPane.add(message, 1, 0);

		// authenticates the log in credentials and updates the current user in the juke box
		loginButton.setOnAction((event) -> {
			if (jukeBox.verifyStudent(nameField.getText(), passField.getText())) {
				message.setText(jukeBox.getSessionText());
				if (jukeBox.getCurrentStudent() instanceof Admin) {
					addNewUser();
				}
				nameField.clear();
				passField.clear();
			} else
				message.setText("Invalid Credentials");
		});
		
		logoutButton.setMinHeight(30);
		logoutButton.setMinWidth(62);
		logoutButton.setStyle("-fx-background-radius:50px");
		
		// on log out, current user is set to null 
		logoutButton.setOnAction((event) -> {
			jukeBox.setCurrentStudent(null);
			message.setText("Login first");
		});

		loginPane.setHgap(10);
		loginPane.setVgap(10);
		loginPane.setPadding(new Insets(0, 10, 20, 150));
		all.setBottom(loginPane);

	}

	// Note: This code snippet is a modified version of the Custom Login Dialog
	// example found at: http://code.makery.ch/blog/javafx-dialogs-official/.
	// Modifications by Rick Mercer.
	//
	// Rick is providing this to use "as is" for your Jukebox project
	// and long as you in the above attribution.
	private void addNewUser() {
		// Create a custom dialog with two input fields
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Adding new user");
		dialog.setHeaderText("Enter the new user ID and password");

		// Set the button types
		ButtonType loginButtonType = new ButtonType("Add new user", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the Account Name and password labels and fields
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField username = new TextField();
		username.setPromptText("Account Name");
		PasswordField password = new PasswordField();
		password.setPromptText("Password");

		grid.add(new Label("Account Name:"), 0, 0);
		grid.add(username, 1, 0);
		grid.add(new Label("Password:"), 0, 1);
		grid.add(password, 1, 1);

		dialog.getDialogPane().setContent(grid);

		// Convert the result to a username-password-pair when the Add user button is
		// clicked.
		// This is lambda instead of an instance of a new event handler: shorter code.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new Pair<>(username.getText(), password.getText());
			}
			return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(usernamePassword -> {
			System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
			jukeBox.getIDReader().addStudent(new Student(usernamePassword.getKey(), usernamePassword.getValue()));
		});

	}

	/**
	 * SongButtonHandler. Adds a song to the queue if there are still plays left
	 * for that daily session and if the user has not yet played 3 songs. This handler
	 * will show an alert if either of those two conditions are false. 
	 */
	private class SongButtonHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent arg) {
			Song songToBePlayed = tableView.getSelectionModel().getSelectedItem();
			if (songToBePlayed != null) {
				if (jukeBox.getCurrentStudent() != null) {
					if (jukeBox.getCurrentStudent().canPlaySong(songToBePlayed)) {
						if (jukeBox.enqueue(songToBePlayed)) {
							if (observableSongQueue.isEmpty()) {
								playSong();
							}
							observableSongQueue.add(songToBePlayed);
							message.setText(jukeBox.getSessionText());
							songToBePlayed.playSong();

							tableView.refresh();
							songQueueView.refresh();
						}
						else {
							alert.setHeaderText(songToBePlayed.getName() + " max plays reached");
							alert.showAndWait();
						}
					}
					else {
						alert.setHeaderText(jukeBox.getCurrentStudent().getUsername() + " has reached the limit");
						alert.showAndWait();
					}
				}
				else {
					alert.setHeaderText("Please log in before trying to play a song.");
					alert.showAndWait();
				}
			}
		}
	}

	/**
	 * WritePersistentObjectOrNot.  Prompts user to choose whether or not to save 
	 * the current state of the jukebox.
	 */
	private class WritePersistentObjectOrNot implements EventHandler<WindowEvent> {

		@Override
		public void handle(WindowEvent event) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Shut Down Option");
			alert.setHeaderText("Press ok to write persistent object(s)");
			alert.setContentText("Press cancel while system testing.");
			Optional<ButtonType> result = alert.showAndWait();

			if (result.get() == ButtonType.OK) {
				writeJukeBoxState();
			}
		}
	}

	/**
	 * writeJukeBoxState() -- writes the jukebox object as it is to a file
	 * to allow for persistence.
	 */
	private void writeJukeBoxState() {
		try {
			FileOutputStream fileOutput = new FileOutputStream(persistedFileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOutput);

			out.writeObject(jukeBox);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * initializeJukeBox() -- prompts user whether or not to begin this
	 * jukebox session from the last saved state or to begin a new state.
	 */
	private void initializeJukeBox() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Start up Option");
		alert.setHeaderText("Press ok to read persistent object(s)");
		alert.setContentText("Press cancel while system testing.");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			readJukeBoxState();
		} 
	}

	/**
	 * readJukeBoxState() -- reads the saved jukebox object and sets it 
	 * to the current jukebox.  Calls helper function refreshJukeBox() 
	 * to help get it going again.
	 */
	private void readJukeBoxState() {
		try {
			FileInputStream fileInput = new FileInputStream(persistedFileName);
			ObjectInputStream in = new ObjectInputStream(fileInput);
			jukeBox = (JukeBox) in.readObject();
			in.close();
			refreshJukeBox();
		} catch (FileNotFoundException e) {
			// if file not found, JukeBox has not been started yet so we just return
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * refreshJukeBox() -- refreshes the jukeBox state message to 
	 * the appropriate message and starts the playing of a song
	 * if the songQueue has songs in it upon startup.
	 */
	private void refreshJukeBox() {
		if (jukeBox.getCurrentStudent() != null) {
			message.setText(jukeBox.getSessionText());
		}
		else {
			message.setText("Login first");
		}
		if(jukeBox.getCurrentSong() != null) {
			playSong();
		}
	}
}
