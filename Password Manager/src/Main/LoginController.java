package Main;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

public class LoginController{

    @FXML private PasswordField passField;
    @FXML private Label invalidLabel;
    @FXML private Label promtLabel;
    @FXML private Button openBtn;
    @FXML private TextField createName;
    @FXML private PasswordField createPass;
    @FXML private TextField createPath;
    @FXML private Label passShortLabel;
    @FXML private Button createBtn;
    @FXML private Button pathBtn;



    private Stage fileWindow;
    private Stage newFileWindow;
    private Scene createPanel;
    private Stage MainWindow;
    private File  file;

    @FXML
    public void initialize(){
        if(Main.fileManager != null) {
            File selectedFile = Main.fileManager.getDbFile();
        }
        /*if(selectedFile!= null){
            passField.setDisable(false);
            openBtn.setDisable(false);
            promtLabel.setText("Enter the password: ");
            this.file = selectedFile;
        }
        */
    }

    @FXML
    private void openPressed(){

        if(Main.fileManager.tryOpen(file, toByte(passField.getText()))){
            passField.setBorder(new Border(new BorderStroke(Color.LIGHTGREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            passField.requestFocus();
            invalidLabel.setText("Logged In!");
            invalidLabel.setTextFill(Color.LIGHTGREEN);
            invalidLabel.setVisible(true);

            //Close Login Window
            Stage stage = (Stage) openBtn.getScene().getWindow();
            stage.close();
            //Main.fileManager.populateTable();

            //Open Main Window
            this.MainWindow = new Stage();
            try {
                AnchorPane pane = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
                Scene mainScene = new Scene(pane);
                MainWindow.setTitle("Password Manager");
                MainWindow.setScene(mainScene);
                //Save on-Close
                MainWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent we) {
                        Main.fileManager.save();
                    }
                });
                MainWindow.showAndWait();
            } catch(IOException e){
                e.printStackTrace();
            }

        }else{
            passField.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            invalidLabel.setText("Invalid Password, try again...");
            invalidLabel.setTextFill(Color.RED);
            invalidLabel.setVisible(true);
            passField.requestFocus();
            passField.selectAll();
        }

    }


    @FXML
    private void openFilePressed(){
        fileWindow = new Stage();
        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("*.db", "*.db"));
        chooser.setTitle("Choose DB file");
        chooser.setInitialDirectory(new File(System.getProperty("user.dir")));


        File selectedFile = chooser.showOpenDialog(fileWindow);


        if(selectedFile != null){
            passField.setDisable(false);
            openBtn.setDisable(false);
            passField.requestFocus();
            promtLabel.setText("Enter the password: ");
            this.file = selectedFile;
        }
    }

    @FXML
    private void newFilePressed(){
        this.newFileWindow = new Stage();
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("CreateDB.fxml"));
            createPanel = new Scene(pane);

            newFileWindow.setTitle("Create New DB");
            newFileWindow.initModality(Modality.APPLICATION_MODAL);
            newFileWindow.setScene(createPanel);
            newFileWindow.showAndWait();

        } catch (Exception e) {System.err.println(e);
            e.printStackTrace();}


    }


    @FXML
    private void createBtnPressed(){
        //System.out.println("\""+createName.getText()+"\"");
        passShortLabel.setVisible(false);
        passShortLabel.setBorder(null);
        createName.setBorder(null);
        createPass.setBorder(null);

        if(createName.getText().length() < 1){
            createName.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        if(createPass.getText().length() < 5){
            createPass.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.DASHED, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
            passShortLabel.setVisible(true);
            passShortLabel.setBorder(new Border(new BorderStroke(Color.CORAL, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        }
        if(createName.getText().length() >= 1 && createPass.getText().length() >= 5) {
            Main.fileManager.createNewDB(createName.getText(), toByte(createPass.getText()), System.getProperty("user.dir"));
            Stage stage = (Stage) createBtn.getScene().getWindow();
            stage.close();
        }

    }

    @FXML
    private void nameEnter(){
        createPath.setText(System.getProperty("user.dir")+ "/" + createName.getText() + ".db");
    }

    @FXML
    private void pathBtnPressed(){
        openFilePressed();
    }


    private byte[] toByte(String s){
        byte[] ar;
        ar = s.getBytes();
        return ar;
    }

}