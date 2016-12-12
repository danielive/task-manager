package live.daniel.TaskManager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/mainForm.fxml"));
        Parent content = loader.load();
        primaryStage.getIcons().add(new Image("/img/main.png"));
        primaryStage.setTitle("Task manager");
        primaryStage.setScene(new Scene(content));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
