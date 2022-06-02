package ui;

import business.*;
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

        HBox memberBox = new HBox(0);
        memberBox.setAlignment(Pos.BOTTOM_LEFT);
        HBox bookBox = new HBox(1);
        bookBox.setAlignment(Pos.BOTTOM_CENTER);
        HBox buttonBox = new HBox(2);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);


        HashMap<String, TextField> fields = new HashMap<>();
        addField(memberBox, new TField(), "MemberID", fields);
        grid.add(memberBox, 0, 1);
        addField(bookBox, new TField(), "ISBN", fields);
        grid.add(bookBox, 1, 1);
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
                    messageBar.setText(member.getMemberId() + "\t" + member.getFirstName());
                    Book book = ci.getRentableBookByIsbn(fields.get("isbn").getText());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Title\tAvailable\tCopy Number\n");
                    for (BookCopy b : book.getCopies()) {
                        sb.append(b.getBook().getTitle() +  "\t" + b.getBook().isAvailable()  + "\t"  + b.getCopyNum() + "\n");
                    }
                    setData(sb.toString());
                } catch (LibrarySystemException | InvalidFieldException exception) {
                    messageBar.setText(exception.getMessage());
                }
            }
        });
        buttonBox.getChildren().add(searchButton);
        grid.add(buttonBox, 3, 1);


        Text scenetitle = new Text("All the book copies");
        scenetitle.setFont(Font.font("Harlow Solid Italic", FontWeight.NORMAL, 20)); //Tahoma
        grid.add(scenetitle, 0, 0, 2, 1);

        grid.add(messageBar, 0, 4);
        ta = new TextArea();
        grid.add(ta, 0,5);
        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Start.hideAllWindows();
                Start.primStage().show();
            }
        });
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //
            }
        });
//        HBox hBack = new HBox(10);
//        hBack.setAlignment(Pos.BOTTOM_LEFT);
//        hBack.getChildren().add(backBtn);
        grid.add(backBtn, 0, 6);
        grid.add(checkoutBtn, 1, 6);
        Scene scene = new Scene(grid);
        scene.getStylesheets().add(getClass().getResource("library.css").toExternalForm());
        setScene(scene);
        isInitialized(true);
    }

    private void addField(HBox grid, TextField field, String text, HashMap<String, TextField> fields) {
        Label label = new Label(text + ": ");
        grid.getChildren().add(label);
        grid.getChildren().add(field);
        fields.put(text.toLowerCase(), field);
    }
}
