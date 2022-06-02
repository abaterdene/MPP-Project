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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
        addField(grid, new NumberField(), "Maximum Checkout length", fields);
        addField(grid, new NumberField(), "Number of copies", fields);

        List<String> selectedAuthors = new ArrayList<>();
        Label selectedLabel = new Label("");
        ComboBox comboBox = new ComboBox(FXCollections
                .observableArrayList(ci.allAuthorIds()));

        comboBox.setOnAction( new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                selectedLabel.setText(comboBox.getValue() + " added");
                selectedAuthors.add(comboBox.getValue().toString());
            }
        });

        Label authorLabel = new Label("Authors: ");
        grid.add(authorLabel, 0, 5);
        grid.add(comboBox, 1, 5);
        grid.add(selectedLabel, 1, 6);

        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.BOTTOM_RIGHT);
        messageBox.getChildren().add(messageBar);
        grid.add(messageBox, 1, fields.size() + 3);

        Button addButton = new Button("Add book");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(addButton);
        grid.add(hbBtn, 1, fields.size() + 4);

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
                        Integer maxCheckoutLength = Integer.parseInt(fields.get("maximum checkout length").getText());
                        ArrayList<Author> authors = new ArrayList<>();
                        for(String authorId: selectedAuthors) {
                            authors.add(ci.getAuthorById(authorId));
                        }
                        book = new Book(fields.get("isbn").getText(), fields.get("title").getText(), maxCheckoutLength, authors);
                        ci.addBook(book);
                        ci.addBookCopy(book, numberOfCopies);
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
        grid.add(hBack, 0, fields.size() + 4);
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
