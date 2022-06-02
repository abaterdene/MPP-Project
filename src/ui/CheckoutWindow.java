package ui;

import business.Book;
import business.LibraryMember;
import business.LibrarySystemException;
import business.SystemController;
import fields.InvalidFieldException;
import fields.TField;
import fields.ValidationFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;

public class CheckoutWindow extends Stage implements LibWindow {
    public static final CheckoutWindow INSTANCE = new CheckoutWindow();

    private boolean isInitialized = false;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void isInitialized(boolean val) {
        isInitialized = val;
    }

    private Text messageBar = new Text();
    private TextArea ta;

    public void setData(String data) {
        ta.setText(data);
    }

    /* This class is a singleton */

    public void clear() {
        messageBar.setText("");
    }

    ;

    private CheckoutWindow() {
    }

    public void init() {
        GridPane grid = new GridPane();
        grid.setId("top-container");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        HashMap<String, TextField> fields = new HashMap<>();
        addField(grid, new TField(), "MemberID", fields);
        addField(grid, new TField(), "ISBN", fields);
        Button searchButton = new Button("Search");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // search
                messageBar.setText("");
                try {
                    fields.forEach((key, value) -> ValidationFactory.getValidation(value.getClass().getSimpleName())
                            .validate(value.getText()));
                    SystemController ci = new SystemController();
                    LibraryMember member = ci.getMemberById(fields.get("memberid").getText());
                    Book book = ci.getBookByIsbn(fields.get("isbn").getText());
                } catch (LibrarySystemException | InvalidFieldException exception) {
                    messageBar.setText(exception.getMessage());
                }
            }
        });
        HBox sButton = new HBox(10);
//		sButton.setAlignment(Pos.BOTTOM_LEFT);
        sButton.getChildren().add(searchButton);
        grid.add(sButton, 3, 2);


        Text scenetitle = new Text("All the book copies");
        scenetitle.setFont(Font.font("Harlow Solid Italic", FontWeight.NORMAL, 20)); //Tahoma
        grid.add(scenetitle, 0, 0, 2, 1);

//		ta = new TextArea();
//		grid.add(ta, 0,3);
        grid.add(messageBar, 0, 4);
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
        grid.add(hBack, 0, 5);
        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("library.css").toExternalForm());
        setScene(scene);
        isInitialized(true);
    }

    private void addField(GridPane grid, TextField field, String text, HashMap<String, TextField> fields) {
        Label label = new Label(text + ": ");
        grid.add(label, fields.size(), 1);
        grid.add(field, fields.size(), 2);
        fields.put(text.toLowerCase(), field);
    }
}
