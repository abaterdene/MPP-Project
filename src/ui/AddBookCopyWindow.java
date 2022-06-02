package ui;

import business.*;
import fields.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;

public class AddBookCopyWindow extends Stage implements LibWindow{
    public static final AddBookCopyWindow INSTANCE = new AddBookCopyWindow();

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
    private AddBookCopyWindow() {
    }

    public void init() {
        GridPane grid = new GridPane();
        grid.setId("top-container");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Book Copy");
        scenetitle.setFont(Font.font("Harlow Solid Italic", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        HashMap<String, TextField> fields = new HashMap<>();
        addField(grid, new IsbnField(), "ISBN", fields);
        addField(grid, new NumberField(), "Number of copies", fields);

        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.BOTTOM_RIGHT);
        messageBox.getChildren().add(messageBar);
        grid.add(messageBox, 1, fields.size() + 1);

        Button addButton = new Button("Add book copy");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(addButton);
        grid.add(hbBtn, 1, fields.size() + 2);

        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    fields.entrySet().forEach(f -> {
                        ValidationFactory.getValidation(f.getValue().getClass().getSimpleName())
                                .validate(f.getValue().getText());
                    });

                    SystemController ci = new SystemController();
                    Book book = ci.getBookByIsbn(fields.get("isbn").getText());
                    Integer numberOfCopies = Integer.parseInt(fields.get("number of copies").getText());
                    ci.addBookCopy(book, numberOfCopies);
                    //add book copy
                    Start.hideAllWindows();
                    Start.primStage().show();
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
        grid.add(hBack, 0, fields.size() + 2);
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
