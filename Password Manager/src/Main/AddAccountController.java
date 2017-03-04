package main;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bohdan on 2/23/17.
 */
public class AddAccountController {


    //create window variabels
    @FXML private TextField title;
    @FXML private TextField userName;
    @FXML private PasswordField passField;
    @FXML private TextField url;
    @FXML private ChoiceBox typeBox;
    @FXML private TextArea note;


    @FXML
    private void createBtnPressed() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        //System.out.println(typeBox.getValue());
        Account newAccount  = new Account(title.getText(),userName.getText(), passField.getText(),note.getText(),(String)typeBox.getValue(),url.getText());

        MainWindowController.accountToAdd = newAccount;

        //close the window
        Stage sc = (Stage)title.getScene().getWindow();
        sc.close();
    }


}
