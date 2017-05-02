package Main;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bohdan Yevdokymov
 */
public class AddAccountController {


    //create window variabels
    @FXML private TextField title;
    @FXML private TextField userName;
    @FXML private PasswordField passField;
    @FXML private TextField url;
    @FXML private ChoiceBox typeBox;
    @FXML private TextArea note;

    public static Account editAccount;
    //

    /**
     * Looks if it is edit account request and prefilles all
     * information if needed.
     */
    @FXML
    public void initialize(){
        if(editAccount != null){
            try {
                title.setText(editAccount.getTitle());
                userName.setText(editAccount.getUserName());
                passField.setText(editAccount.getPassword());
                if(editAccount.getURL() != null) {
                    url.setText(editAccount.getURL());
                }
                if(editAccount.getType() != null && typeBox.getItems().contains(editAccount.getType())){
                    typeBox.getSelectionModel().select(editAccount.getType());
                }
                if(editAccount.getComment() != null){
                    note.setText(editAccount.getComment());
                }
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Saves account back to main window controller.
     * @throws InvalidKeyException
     * @throws BadPaddingException
     * @throws NoSuchAlgorithmException
     * @throws IllegalBlockSizeException
     * @throws NoSuchPaddingException
     */
    @FXML
    private void createBtnPressed() throws InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, NoSuchPaddingException {
        //System.out.println(typeBox.getValue());
        if(title.getText().length() < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Title");
            alert.setHeaderText(null);
            alert.setContentText("Title cannot be empty !");
            alert.showAndWait();
        }else if(!title.getText().equals(editAccount.getTitle()) && Main.accountTable.containsKey(title.getText())) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Title");
            alert.setHeaderText(null);
            alert.setContentText("Title already exists !");
            alert.showAndWait();
        }else
        {
            Account newAccount = new Account(title.getText(), userName.getText(), passField.getText(), note.getText(), (String) typeBox.getValue(), url.getText());

            MainWindowController.accountToAdd = newAccount;

            //close the window
            Stage sc = (Stage) title.getScene().getWindow();
            sc.close();
        }
    }


}
