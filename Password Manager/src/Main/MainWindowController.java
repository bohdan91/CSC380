package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.ObservableList;

/**
 * Created by Bohdan on 2/22/17.
 */
public class MainWindowController {

    @FXML private TableView table;
    @FXML private FlowPane statusPane;
    @FXML private ProgressBar loadBar;
          private ObservableList<Account> data;
    @FXML private TableColumn titleColumn;
    @FXML private TableColumn userColumn;
    @FXML private TableColumn passColumn;
    @FXML private TableColumn urlColumn;
    @FXML private TableColumn noteColumn;
    @FXML private ListView<String> typeList;
          private Stage addWindow;
          private HashMap<String, Integer> listItems;

          public static Account accountToAdd;



    //Constructor
    @FXML
    public void initialize() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        table.setEditable(false);

        final ContextMenu contextMenu = new ContextMenu();
        /*contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                //System.out.println("showing");
            }
        });
        contextMenu.setOnShown(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent e) {
                //System.out.println("shown");
            }
        });
        */

        MenuItem item1 = new MenuItem("Copy Username");
        item1.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                copyUsernamePressed();
            }
        });
        MenuItem item2 = new MenuItem("Copy Password");
        item2.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    copyPassPressed();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        contextMenu.getItems().addAll(item1, item2);

        table.setContextMenu(contextMenu);

        statusPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //Defining Table Data in an Observable List
        data  = FXCollections.observableArrayList();

        /*Account testAccount = new Account("gmail1","username1", "password1", "comment1", "type1","gmail.com1");
        Account testAccount2 = new Account("gmail2","username2", "password2", "comment2", "type","gmail.com2");
        data.add(testAccount);
        data.add(testAccount2);
        */
        listItems = new HashMap<>();
        listItems.put("All", 0);
        for(Account ac : Main.accountTable.values()){

            data.add(ac);
            listItems.put("All", listItems.get("All") + 1);
            if(ac.getType() != null && !ac.getType().equals("null") && ac.getType() != ""){
                if(listItems.containsKey(ac.getType())){
                    listItems.put(ac.getType(), listItems.get(listItems.get(ac.getType())) + 1);
                } else {
                    listItems.put(ac.getType(), 1);
                }
            }
        }
        ObservableList<String> list = FXCollections.observableArrayList ();
        for(String key : listItems.keySet()){
            list.add(key + " (" + listItems.get(key) + ")");
        }
        typeList.setItems(list);




        table.setItems(data);
        typeList.setItems(list);

    }

    @FXML
    private void addAccountPressed() {
        this.addWindow = new Stage();
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("AddAccountWindow.fxml"));
            Scene mainScene = new Scene(pane);
            addWindow.setTitle("Add Account");
            addWindow.setScene(mainScene);
            addWindow.showAndWait();

            if(accountToAdd != null){
                Main.accountTable.put(accountToAdd.getTitle(), accountToAdd);
                data.add(accountToAdd);
                table.setItems(data);
                accountToAdd = null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void copyUsernamePressed(){
        Account selected = (Account)table.getSelectionModel().getSelectedItem();
        loadBar.setProgress(0.6);

        //copying username to clipboard
        String copyUsername = selected.getUserName();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(copyUsername);
        clipboard.setContent(content);

    }

    @FXML
    private void copyPassPressed() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        Account selected = (Account)table.getSelectionModel().getSelectedItem();

        //copying password to clipboard
        String copyPassword = selected.getPassword();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(copyPassword);
        clipboard.setContent(content);

    }

    @FXML
    private void saveFile(){
        Main.fileManager.save();
    }

    @FXML
    private void lockButtonPressed(){
        try {

            Stage stage = (Stage) table.getScene().getWindow();
            stage.close();

            Stage loginWindow = new Stage();
            AnchorPane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));

            Scene loginScene = new Scene(pane);

            loginWindow.setTitle("Password Manager");
            loginWindow.setResizable(false);
            loginWindow.setScene(loginScene);
            loginWindow.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }


}


