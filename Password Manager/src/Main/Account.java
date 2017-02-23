package Main;

import javafx.beans.property.SimpleStringProperty;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Bohdan on 2/21/17.
 */
public class Account implements Serializable {



    private String title;
    private String userName;
    private String password;
    private String maskedPassword;
    private String comment;
    private String type;
    private String URL;

    public Account(String title, String userName, String password, String comment, String type, String URL) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        this.title = title;
        this.userName = userName;
        this.password = Main.fileManager.encrypt(password);
        maskedPassword = maskPass(this.password);
        this.comment = comment;
        this.type = type;
        this.URL = URL;
    }

    private String maskPass(String password) {
        String output = "";
        for(int i = 0; i < password.length(); i++) {
            output += "*";
        }
        return output;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        return Main.fileManager.decrypt(password);
    }

    public void setPassword(String password) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        this.password = Main.fileManager.encrypt(password);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMaskedPassword() {
        return maskedPassword;
    }

    public void setMaskedPassword(String maskedPassword) {
        this.maskedPassword = maskedPassword;
    }

}