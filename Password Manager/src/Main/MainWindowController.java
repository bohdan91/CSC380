package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
<<<<<<< HEAD
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
=======
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
>>>>>>> origin/master
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
          private Stage addWindow;

    //Constructor
    @FXML
    public void initialize() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        table.setEditable(false);
        statusPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //Defining Table Data in an Observable List
        data  = FXCollections.observableArrayList();

        Account testAccount = new Account("gmail1","username1", "password1", "comment1", "type1","gmail.com1");
        Account testAccount2 = new Account("gmail2","username2", "password2", "comment2", "type","gmail.com2");
        data.add(testAccount);
        data.add(testAccount2);


        table.setItems(data);

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void copyUsernamePressed(){
        Account selected = (Account)table.getSelectionModel().getSelectedItem();
        System.out.println(selected.getUserName());
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
        System.out.println(selected.getPassword());

        //copying password to clipboard
        String copyPassword = selected.getPassword();
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(copyPassword);
        clipboard.setContent(content);

    }

}


