package live.daniel.TaskManager.controllers;

import com.oracle.deploy.update.UpdateInfo;
import com.oracle.deploy.update.Updater;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import live.daniel.TaskManager.CollectionTasks;
import live.daniel.TaskManager.Manager;
import live.daniel.TaskManager.Task;

import javax.management.ObjectName;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;

/**
 * Created by Daniel on 24.11.2016.
 */
public class mainForm {
    private static CollectionTasks collectionTasks = new CollectionTasks();

    @FXML
    protected Label countTasks;
    @FXML
    protected Button addTask;
    @FXML
    protected Button delTask;
    @FXML
    protected TreeView<String> listTasks;
    @FXML
    protected Label countProcessor;
    @FXML
    protected Label timeMain;

    @FXML
    protected TableView tableTasks;
    @FXML
    protected TableColumn<Task, String> nameTask;
    @FXML
    protected TableColumn<Task, Integer> priorityTask;
    @FXML
    protected TableColumn<Task, Integer> timeActivationTask;
    @FXML
    protected TableColumn<Task, Integer> timeExecuteTask;
    @FXML
    protected TableColumn<Task, Boolean> execute;

    static int countP = 1;

    StringProperty timeM = new SimpleStringProperty();

    public void setRunning(boolean running) {
        this.running = running;
    }

    static volatile boolean running = false;
    static volatile int countTimeMain = 0;
    public static int getCountTimeMain() {
        return countTimeMain;
    }

    TreeItem<String> item;
    Image icon = new Image(getClass().getResourceAsStream("/img/icon.ico"));

    //It is checked method getName(), PropertyValueFactory - "Name" or nameProperty(), PropertyValueFactory - "name"
    @FXML
    protected void initialize() {
        nameTask.setCellValueFactory(new PropertyValueFactory<>("name"));
        priorityTask.setCellValueFactory(new PropertyValueFactory<>("priority"));
        timeActivationTask.setCellValueFactory(new PropertyValueFactory<>("timeActivation"));
        timeExecuteTask.setCellValueFactory(new PropertyValueFactory<>("timeUsing"));
        execute.setCellValueFactory(new PropertyValueFactory<>("using"));
        tableTasks.setItems(CollectionTasks.getTasks());

        tested();

        collectionTasks.getTasks().addListener((ListChangeListener<Task>) c -> {
            updateCountTasks();
        });
    }

    protected void updateCountTasks() {
        countTasks.setText("Count tasks: " + CollectionTasks.getTasks().size());
    }
    protected void updateCountProcessor() {
        countProcessor.setText("Count processor: " + countP);
    }
    protected void updateTimeMain() {
        timeMain.setText("Time: " + timeM.getValue());
    }

    protected int getTimeActivation() {
        return collectionTasks.getTasks().get(0).getTimeActivation();
    }
    protected int getCountP() {
        return countP;
    }

    @FXML
    protected void setTreeView() {
        TreeItem<String> root = new TreeItem<>("Tasks", new ImageView(icon));
        root.setExpanded(true);

        File rootTasks = new File("D:\\Projects\\IdeaProjects\\TaskManager\\src\\main\\resources\\tasks");
        ArrayList<File> al = new ArrayList<File>();
        File[] files = rootTasks.listFiles();

        for (File singleFile : files) {
            root.getChildren().add(new TreeItem<>(singleFile.getName()));
            al.add(singleFile);
        }
        listTasks.setRoot(root);
    }

    @FXML
    protected void clickMouseTreeView() {
        item = listTasks.getSelectionModel().getSelectedItem();
    }

    @FXML
    protected void setAddTask() {
        if (item.getValue() == "Tasks") return;
        else {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(mainForm.class.getResource("/fxml/addTask.fxml"));
                Parent content = loader.load();
                Stage dialogStage = new Stage();
                dialogStage.setTitle("Add task");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                Scene scene = new Scene(content);
                dialogStage.setScene(scene);
                live.daniel.TaskManager.controllers.addTask controller = loader.getController();
                controller.setDialogStage(dialogStage);
                dialogStage.setResizable(false);
                dialogStage.showAndWait();

                if (controller.isOkClicked()) {
                    if (item.getValue().endsWith(".exe"))
                        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_" + item.getValue(), 1, controller.getTimeActivation(), controller.getTimeExecute(), false));
                    else if (item.getValue().endsWith(".txt"))
                        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_" + item.getValue(), 2, controller.getTimeActivation(), controller.getTimeExecute(), false));
                    else if (item.getValue().endsWith(".mp3"))
                        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_" + item.getValue(), 3, controller.getTimeActivation(), controller.getTimeExecute(), false));
                    else if (item.getValue().endsWith(".jpeg"))
                        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_" + item.getValue(), 4, controller.getTimeActivation(), controller.getTimeExecute(), false));
                    else
                        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_" + item.getValue() + " default", 4, controller.getTimeActivation(), controller.getTimeExecute(), false));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void setDelTask() {
        String temp;
        Task selectedTask = (Task) tableTasks.getSelectionModel().getSelectedItem();
        collectionTasks.getTasks().remove(selectedTask);
        for (int i = 0; i < CollectionTasks.getTasks().size(); i++) {
            temp = collectionTasks.getTasks().get(i).getName().replaceAll("\\d_", "");
            collectionTasks.getTasks().get(i).setName(temp);
            collectionTasks.getTasks().get(i).setName(i+1 + "_" + collectionTasks.getTasks().get(i).getName());
        }
    }

    @FXML
    protected void exitProgram() {
        Platform.exit();
    }

    @FXML
    protected void setCountProcessor() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(mainForm.class.getResource("/fxml/setProcessor.fxml"));
            Parent content = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Count processor");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(content);
            dialogStage.setScene(scene);
            live.daniel.TaskManager.controllers.setProcessor controller = loader.getController();
            controller.setDialogStage(dialogStage);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (controller.isOkClicked()) {
                countP = controller.getCountP();
                updateCountProcessor();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void startProgram() throws InterruptedException {
        runClock();
        sortingTasks();
        executeTasks();
    }

    // TODO: 09.12.2016  ~Можно переделать в инт а не стринг
    protected void runClock() {
        running = true;
        countTimeMain = 0;
        timeM.setValue("000");
        new Thread() {
            public void run() {
                long last = System.nanoTime();
                double delta = 0;
                double ns = 1000000000;
                while (running) {
                    long now = System.nanoTime();
                    delta += (now - last) / ns;
                    last = now;
                    while (delta >= 1) {
                        countTimeMain = (countTimeMain + 1) % 999;
                        DecimalFormat df = new DecimalFormat("000");
                        timeM.setValue(df.format(countTimeMain));
                        delta--;
                        Platform.runLater(() -> updateTimeMain());
                    }
                }
            }
        }.start();
    }

    // TODO: 09.12.2016  ~Переделать в обычный тред
    protected void executeTasks() {
        Manager m = new Manager();
        new Thread() {
            public void run() {
                try {
                    m.execute(CollectionTasks.getTasks().size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    protected void sortingTasks() {
        int countD = 1; int tCountD = 1;
        //Sorting to TimeActivation
        FXCollections.sort(collectionTasks.getTasks(), (o1, o2) ->
                Integer.compare(o1.getTimeActivation(), o2.getTimeActivation()));
        //Sorting to Priority and find all equals TimeActivation for total sorting priorities
        for (int i = 0; i < collectionTasks.getTasks().size(); i++) {
            if (collectionTasks.getTasks().size() != i + 1) {
                if (collectionTasks.getTasks().get(i).getTimeActivation() == collectionTasks.getTasks().get(i + 1).getTimeActivation()) {
                    tCountD = tCountD + 1;
                    if (collectionTasks.getTasks().get(i).getPriority() > collectionTasks.getTasks().get(i + 1).getPriority())
                        Collections.swap(collectionTasks.getTasks(), i, i + 1);
                } else tCountD = 1;
            }
            if (tCountD > countD) countD = tCountD;
        }
        //Total sorting priorities
        for (int d = 0; d < countD; d++) {
            for (int i = 0; i < collectionTasks.getTasks().size(); i++) {
                if (collectionTasks.getTasks().size() != i + 1)
                    if (collectionTasks.getTasks().get(i).getTimeActivation() == collectionTasks.getTasks().get(i + 1).getTimeActivation()) {
                        if (collectionTasks.getTasks().get(i).getPriority() > collectionTasks.getTasks().get(i + 1).getPriority())
                            Collections.swap(collectionTasks.getTasks(), i, i + 1);
                    }
            }
        }
    }

    private void tested() {
        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_TEST", 4, 2, 4, false));
        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_TEST", 3, 4, 6, false));
        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_TEST", 1, 4, 2, false));
        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_TEST", 2, 3, 3, false));
        CollectionTasks.getTasks().add(new Task(CollectionTasks.getTasks().size()+ 1 + "_TEST", 1, 6, 1, false));

    }
}
