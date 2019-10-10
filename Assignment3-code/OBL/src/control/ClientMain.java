package control;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * The main class on the client side.
 */
public class ClientMain extends Application {
	/**
	 * instance variable:
	 * hostServices - an instance of HostServices to enable the user to open PDF files.
	 */
    private static HostServices hostServices ;

    /**
     * a getter for the HostServices instance variable.
     * @return the HostServices instance
     */
    public static HostServices myGetHostServices() {
        return hostServices ;
    }
    /**
     * initializes the hostServices variable and launches the window where the user inputs the servers' IP address.
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
		hostServices = getHostServices();
		Parent root = FXMLLoader.load(getClass().getResource("/boundary/ConnectToServerGUI.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("IP Configuration");
		primaryStage.show();
	}

	/**
	 * the main function, calls the {@code start} method.
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		launch(args);
	}
	
}
