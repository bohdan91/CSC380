package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bohdan on 2/22/17.
 */
public class MainWindowController {

    @FXML private TableView table;
    @FXML private FlowPane statusPane;
    @FXML private ProgressBar loadBar;
    @FXML private TableColumn titleColumn;
    @FXML private TableColumn userColumn;
    @FXML private TableColumn passColumn;
    @FXML private TableColumn urlColumn;
    @FXML private TableColumn noteColumn;

    //Constructor
    @FXML
    public void initialize() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        table.setEditable(false);
        statusPane.setBorder(new Border(new BorderStroke(Color.BLACK,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

        //Defining Table Data in an Observable List
        ObservableList<Account> data = FXCollections.observableArrayList();

        Account testAccount = new Account("gmail1","username1", "password1", "comment1", "type1","gmail.com1");
        Account testAccount2 = new Account("gmail2","username2", "password2", "comment2", "type","gmail.com2");
        data.add(testAccount);
        data.add(testAccount2);


        table.setItems(data);

    }

    @FXML
    private void addAccountPressed(){

    }

    @FXML
    private void copyUsernamePressed(){
        Account selected = (Account)table.getSelectionModel().getSelectedItem();
        System.out.println(selected.getUserName());
        loadBar.setProgress(0.6);
    }

    @FXML
    private void copyPassPressed(){

    }

}


