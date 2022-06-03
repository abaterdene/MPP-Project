package ui;

import business.Author;
import business.Book;
import business.LibrarySystemException;
import business.SystemController;
import fields.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.*;

public class AddBookWindow extends Stage implements LibWindow{

    public static final AddBookWindow INSTANCE = new AddBookWindow();

    private boolean isInitialized = false;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void isInitialized(boolean val) {
        isInitialized = val;
    }

    private Text messageBar = new Text();
    public void clear() {
        messageBar.setText("");
    }

    /* This class is a singleton */
    private AddBookWindow() {
    }

    public void init() {

        SystemController ci = new SystemController();

        GridPane grid = new GridPane();
        grid.setId("top-container");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Add Book");
        scenetitle.setFont(Font.font("Harlow Solid Italic", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        HashMap<String, TextField> fields = new HashMap<>();
        addField(grid, new IsbnField(), "ISBN", fields);
        addField(grid, new TField(), "Title", fields);
        addField(grid, new NumberField(), "Number of copies", fields);


        Label checkoutLbl = new Label( "Maximum Checkout length:");
        grid.add(checkoutLbl, 0, fields.size() + 1);
        String [] checkoutLength = {"7", "21"};
        ComboBox checkoutCbx = new ComboBox(FXCollections
                .observableArrayList(checkoutLength));
        checkoutCbx.getSelectionModel().selectFirst();
        grid.add(checkoutCbx, 1, fields.size() + 1);

        Set<String> selectedAuthors = new HashSet<>();
        Label selectedLabel = new Label("");
        ComboBox authorCbx = new ComboBox(FXCollections
                .observableArrayList(ci.allAuthorIds()));
//        comboBox.setValue("Add Author(s)");
        authorCbx.setOnAction( new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                selectedLabel.setText(authorCbx.getValue() + " added");
                selectedAuthors.add(authorCbx.getValue().toString());
            }
        });

        Label authorLabel = new Label("Authors: ");
        grid.add(authorLabel, 0, fields.size() + 2);
        grid.add(authorCbx, 1, fields.size() + 2);
        grid.add(selectedLabel, 1, fields.size() + 3);

        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.BOTTOM_RIGHT);
        messageBox.getChildren().add(messageBar);
        grid.add(messageBox, 1, fields.size() + 4);

        Button addButton = new Button("Add book");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(addButton);
        grid.add(hbBtn, 1, fields.size() + 5);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    fields.entrySet().forEach(f -> {
                        ValidationFactory.getValidation(f.getValue().getClass().getSimpleName())
                                .validate(f.getValue().getText());
                    });

                    Book book;
                    try {
                        book = ci.getBookByIsbn(fields.get("isbn").getText());
                    } catch (LibrarySystemException excp) {
                        book = null;
                    }


                    if(book != null){
                        messageBar.setText("Book with this ISBN already registered");
                    } else if(selectedAuthors.size() == 0) {
                        messageBar.setText("Book author is not selected");
                    } else {
                        Integer numberOfCopies = Integer.parseInt(fields.get("number of copies").getText());
                        if(numberOfCopies < 0) {
                            messageBar.setText("Number of copies cannot be negative number");
                        }
                        Integer maxCheckoutLength = Integer.parseInt(checkoutCbx.getValue().toString());
                        ArrayList<Author> authors = new ArrayList<>();
                        for(String authorId: selectedAuthors) {
                            authors.add(ci.getAuthorById(authorId));
                        }
                        book = new Book(fields.get("isbn").getText(), fields.get("title").getText(), maxCheckoutLength, authors);
                        for (int i = 1; i < numberOfCopies; i++) {
                            book.addCopy();
                        }
                        ci.addBook(book);
                        //add book copy
                        Start.hideAllWindows();
                        Start.primStage().show();
                    }

                } catch (LibrarySystemException | InvalidFieldException exception) {
                    messageBar.setText(exception.getMessage());
                }
            }
        });

        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Start.hideAllWindows();
                Start.primStage().show();
            }
        });
        HBox hBack = new HBox(10);
        hBack.setAlignment(Pos.BOTTOM_LEFT);
        hBack.getChildren().add(backBtn);
        grid.add(hBack, 0, fields.size() + 5);
        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("library.css").toExternalForm());
        setScene(scene);
    }

    private void addField(GridPane grid, TextField field, String text, HashMap<String, TextField> fields) {
        Label label = new Label(text + ": ");
        grid.add(label, 0, fields.size() + 1);
        grid.add(field, 1, fields.size() + 1);
        fields.put(text.toLowerCase(), field);
    }

}
