package Main;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
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
import java.util.HashMap;

import java.util.Timer;
import java.util.TimerTask;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;


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
    //Edit account window
    @FXML
    //Edit account window
          private Stage addWindow;
          private HashMap<String, Integer> listItems;
          
          Image img;
          TreeView<String> tree;

          public static Account accountToAdd;
          
          Timer timer = new Timer();

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
        MenuItem item3 = new MenuItem("Edit");
        item3.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                try {
                    if(table.getSelectionModel().getSelectedItem() != null) {
                        editRequested((Account) table.getSelectionModel().getSelectedItem());
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
        contextMenu.getItems().addAll(item1, item2, item3);

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
        populateTypeList();
        typeList.getSelectionModel().select(0);

    }
    private void editRequested(Account ac){
        this.addWindow = new Stage();
        try {
            String oldTitle = ac.getTitle();
            AddAccountController.editAccount = ac;
            AnchorPane pane = FXMLLoader.load(getClass().getResource("AddAccountWindow.fxml"));
            Scene mainScene = new Scene(pane);
            addWindow.setTitle("Edit Account");
            addWindow.setScene(mainScene);
            addWindow.showAndWait();

            if(accountToAdd != null){
                if(!accountToAdd.getTitle().equals(oldTitle)){
                    Main.fileManager.changeTitle(oldTitle, accountToAdd.getTitle());
                    Main.accountTable.remove(oldTitle);
                }
                Main.fileManager.updateAccount(accountToAdd);
                //Main.accountTable.remove(ac.getTitle());
                //Main.accountTable.put(accountToAdd.getTitle(), accountToAdd);
                accountToAdd = null;
                populateTypeList();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        AddAccountController.editAccount = null;
    }

    private void populateTypeList(){
        listItems = new HashMap<>();
        data  = FXCollections.observableArrayList();

        listItems.put("All", 0);
        for(Account ac : Main.accountTable.values()){

            data.add(ac);
            listItems.put("All", listItems.get("All") + 1);
            if(ac.getType() != null && !ac.getType().equals("null") && !ac.getType().equals("")){
                if(listItems.containsKey(ac.getType())){
                    listItems.put(ac.getType(), listItems.get(ac.getType()) + 1);
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
                System.out.println(Main.fileManager.insertAccount(accountToAdd));
                accountToAdd = null;
                populateTypeList();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void copyUsernamePressed()
    {
        Account selected = (Account)table.getSelectionModel().getSelectedItem();

        //copying username to clipboard
        if (selected != null)
        {
        	String copyUsername = selected.getUserName();
        	final Clipboard clipboard = Clipboard.getSystemClipboard();
        	final ClipboardContent content = new ClipboardContent();
        	content.putString(copyUsername);
        	clipboard.setContent(content);
        }
        else
        {
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("No User Name Selected");
        	alert.setHeaderText(null);
        	alert.setContentText("Sorry, No User Name Has Been Selected, Try Again.");
        	alert.showAndWait();
        }
    }

    @FXML
    private void copyPassPressed() throws NoSuchPaddingException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, Exception 
    {
        Account selected = (Account)table.getSelectionModel().getSelectedItem();

        //copying password to clipboard
        if (selected != null)
        {
        	String copyPassword = selected.getPassword();
        	final Clipboard clipboard = Clipboard.getSystemClipboard();
        	final ClipboardContent content = new ClipboardContent();
        	content.putString(copyPassword);
        	clipboard.setContent(content);
        	
        	//clear clipboard after 30 seconds & starts progressBar
        	start();
        	clipboardTimer();
        }
        else
        {
        	Alert alert = new Alert(AlertType.INFORMATION);
        	alert.setTitle("No Password Selected");
        	alert.setHeaderText(null);
        	alert.setContentText("Sorry, No Password Has Been Selected, Try Again.");
        	alert.showAndWait();
        }
    }
    
    private void clipboardTimer()
    {
    	Timer timer = new Timer();
    	TimerTask task = new TimerTask()
    	{
    		int secondsPassed = 0;
    		
    		public void run()
    		{
    			secondsPassed++;
    			
    			if (secondsPassed == 30)
    			{
    				timer.cancel();
    				clearClipboard();
    			}
    		}
    	};
    	
    	timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private void clearClipboard()
    {
    	timer.purge();
    	Toolkit toolkit = Toolkit.getDefaultToolkit();
    	java.awt.datatransfer.Clipboard clipboard = toolkit.getSystemClipboard();
    	StringSelection strClear = new StringSelection(" ");
    	clipboard.setContents(strClear, null);
    }

    @FXML
    private void saveFile(){
       // Main.fileManager.save();
    }

    @FXML
    private void lockButtonPressed(){
    	clearClipboard();
        try {
            //Main.fileManager.save();
            Main.fileManager.resetPassword();

            Stage stage = (Stage) table.getScene().getWindow();
            stage.close();

            Stage loginWindow = new Stage();
            AnchorPane pane = FXMLLoader.load(getClass().getResource("Login.fxml"));

            Scene loginScene = new Scene(pane);
            //When login screen is loaded back up - sets title to last used .db file
            loginWindow.addEventHandler(WindowEvent.WINDOW_SHOWING, new  EventHandler<WindowEvent>()
            {
                @Override
                public void handle(WindowEvent window)
                {
                    //String file = Main.fileManager.getDbFile().toString();
                    //String fileName = file.substring(file.lastIndexOf("/") + 1);
                    //loginWindow.setTitle(fileName);
                }
            });

            loginWindow.setTitle("Password Manager");
            loginWindow.setResizable(false);
            loginWindow.setScene(loginScene);
            loginWindow.show();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @FXML
    private void typeSwitched(){
        String selected = typeList.getSelectionModel().getSelectedItem();
        data  = FXCollections.observableArrayList();


        if(selected != null){
            for(Account ac : Main.accountTable.values()){
                if(selected.contains(ac.getType()) || selected.contains("All")){
                    data.add(ac);
                }
            }
        }
        table.setItems(data);
    }

    @FXML
    private void removeAccountPressed(){
        Account acToRemove = (Account)table.getSelectionModel().getSelectedItem();
        if(acToRemove != null) {
            Main.accountTable.remove(acToRemove.getTitle());
            populateTypeList();
        }
    }
    
    @FXML
    private void helpAboutPressed()
    {
    	addWindow = new Stage();
    	addWindow.setTitle("About");
    	
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setHgap(10);
        grid.setVgap(10);

        final HBox txtRegion = new HBox();
        txtRegion.getChildren().add(helpWindowText());
        grid.add(txtRegion, 6/3, 2/3);

        final HBox pictureRegion = new HBox();
        pictureRegion.getChildren().add(helpWindowImage());
        grid.add(pictureRegion, 0, 0);

        BorderPane layout = new BorderPane();
        layout.setCenter(helpWindowCreateTree());
        layout.setTop(grid);
        Scene scene = new Scene(layout, 650, 500);
        addWindow.setScene(scene);
        addWindow.show();
    }
    
    public TreeView<String> helpWindowCreateTree()
    {
        TreeItem<String> root, branchA, branchB, branchC;
        TreeItem<String> branchA_1, branchA_2;
        TreeItem<String> branchB_1, branchB_2;
        TreeItem<String> branchC_1, branchC_2, branchD;

        //Root
        root = new TreeItem<>();
        root.setExpanded(true);
        
        //branchA
        branchA = makeBranch("Getting Started", root);
        branchA.setExpanded(true);
        branchA_1 = makeBranch("Creating Database", branchA);
        makeBranch("- Upon first introduction, you will be prompted to create a unique 'Username'" +
                "and 'Password'." + "\n\n" +
                "- This creates a unique login and access to your own unique database, which will hold all" +
                "\n" + "   'Accounts', or your specific information you wish to secure.", branchA_1);
        branchA_2 = makeBranch("Creating Accounts", branchA);
        makeBranch("- Once having access to your database, you can freely add your 'Accounts'" + "\n" +
                "   ranging from Banking, Social, Email, Gaming, and Other " + "\n\n" +
                "- Click 'Add Account' to be prompted to enter the specified accounts information" +
                "\n\n" + "- This includes: " +
                " Title - of the account (e.g. Gaming_Account_1) \n\t\t\t   Username - for the specified" +
                "account (Gaming_User123) \n\t\t\t   Password - to be secured for the account (Gaming_password)" +
                "\n\t\t\t   URL - of the website/etc. to be saved (gaming.com) \n\t\t\t" +
                "   Type - type of saved password for ease of finding (gaming)" +
                "\n\t\t\t   Notes - for any extra information you need to save (trial expires xx/xx/xxxx " , branchA_2);

        //branchB
        branchB = makeBranch("Maintaining Your Database", root);
        branchB.setExpanded(true);
        branchB_1 = makeBranch("Keeping Your Database Up to Date", branchB);
        makeBranch("- No need to worry, we automatically update your database once they're added", branchB_1);
        branchB_2 = makeBranch("Duplicate Accounts", branchB);
        makeBranch("- You don't have to deal with all the clutter, as once a account or username" +
                "\n" + "   is added, you will receive an error not allowing a duplicate", branchB_2);

        //branchC
        branchC = makeBranch("Features of Password Manager", root);
        branchC.setExpanded(true);
        branchC_1 = makeBranch("Safety Assurance", branchC);
        makeBranch("- Rest assured, we encrypt all sensitive information and passwords", branchC_1);
        branchC_2 = makeBranch("Copying Usernames & Passwords", branchC);
        makeBranch("- Clicking 'Copy Username' will copy your username to the clipboard so you can " +
                "\n   paste it where ever you'd like \n\n - Clicking 'Copy Password' will do the same, except " +
                "a 30 second timer will start, \n   which at completion, will erase the clipboard \n\n" +
                "- A progress bar on the bottom right of your screen will allow you to visually see " +
                "\n   how much time you have remaining to paste your password before it is erased ", branchC_2);

        //branchD
        branchD = makeBranch("Thank You!", root);
        branchD.setExpanded(true);
        makeBranch("- Thank you for using our application, we hope you enjoy it and find it helpful", branchD);
        
        //create tree
        tree = new TreeView<>(root);
        tree.setShowRoot(false);

        return tree;
    }
    
    //create treeBranches
    public TreeItem<String> makeBranch(String title, TreeItem<String> parent)
    {
        TreeItem<String> item = new TreeItem<>(title);
        item.setExpanded(false);
        parent.getChildren().add(item);
        return item;
    }
    
    public ImageView helpWindowImage()
    {
        final ImageView imv = new ImageView();
        final Image img2 = new Image("file:src/resorces/lockIcon2.png");
        imv.setImage(img2);
        return imv;
    }
    
    public Text helpWindowText()
    {
        Text t = new Text();
        t.setText("About Password Manager");
        t.setFont(Font.font("American Typewriter", 36));
        t.setFill(Color.BLACK);
        return t;
    }
    
    @FXML
    public void start() throws Exception
    {
    	double time = 0.01;
    	
    	final Task<Void> task = new Task<Void>()
    	{
    		int seconds = 3000;
    		
			@Override
			protected Void call() throws Exception 
			{
				for (int i = 0; i < seconds; i++)
				{
					updateProgress(i + 1, seconds);
				}
				return null;
			}
    	};
    	
    	loadBar.progressProperty().bind(task.progressProperty());
    	
    	loadBar.progressProperty().addListener(observable -> 
    	{
    		if (loadBar.getProgress() >= 1 - time)
    		{
    			loadBar.setStyle("-fx-accent: lightgreen;");
    		}
    	});
    	
    	Thread thread = new Thread(task);
    	thread.setDaemon(true);
    	thread.start();
    }
    
}


