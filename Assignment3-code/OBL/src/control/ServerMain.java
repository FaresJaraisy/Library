package control;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
/**
 * the main class on the server side.
 */
public class ServerMain extends Application {
	/**
	 * opens a window with the server configurations which has a connect button to connect to the server.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// TODO Auto-generated method stub
		Parent root = FXMLLoader.load(getClass().getResource("/boundary/ServerConfigurationGUI.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setTitle("Server Configuration");
		primaryStage.setOnCloseRequest(e->close());
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
    /**
     * the method to be called should the user close the window(either by the X button on the top left corner of the window or by pressing alt-F4)
     * it disconnects the server and exits.
     */
	private void close() {
		if(Server.sv != null)
			Server.sv.stopListening();
		System.exit(0);
	}
	
	

}
