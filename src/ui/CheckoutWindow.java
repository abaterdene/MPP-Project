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
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Objects;

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
    private TextArea cart;

    private Book selected;
    private LibraryMember member;
    private CheckoutEntry[] entries = new CheckoutEntry[0];

    public void setTable(String data, Book book) {
        ta.setText(data);
        this.selected = book;
    }

    public void setCart(String data) {
        cart.setText(data);
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
        memberBox.setAlignment(Pos.CENTER_LEFT);
        HBox bookBox = new HBox(0);
        bookBox.setAlignment(Pos.CENTER);
        HBox buttonBox = new HBox(0);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        HashMap<String, TextField> fields = new HashMap<>();
        addField(memberBox, new TField(), "MemberID", fields);
        grid.add(memberBox, 0, 1);
        addField(bookBox, new TField(), "ISBN", fields);
        grid.add(bookBox, 1, 1);

        Button addBtn = new Button("Add book");
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (Objects.nonNull(selected)) {
                    try {
                        BookCopy copy = selected.getNextAvailableCopy();
                        copy.changeAvailability();
                        selected.updateCopies(copy);

                        CheckoutEntry[] newArr = new CheckoutEntry[entries.length + 1];
                        System.arraycopy(entries, 0, newArr, 0, entries.length);
                        newArr[entries.length] = new CheckoutEntry(copy);
                        entries = newArr;

                        System.out.println(entries.length);
                        StringBuilder sb = new StringBuilder();
                        sb.append("Title\tCopy Number\n");
                        for (CheckoutEntry c : entries) {
                            sb.append(c.getBookCopy().getBook().getTitle() +  "\t" + c.getBookCopy().getCopyNum()  +  "\n");
                        }
                        setCart(sb.toString());
                    } catch (NullPointerException exception) {
                        messageBar.setText("Book copy is not available");
                    }
                } else {
                    messageBar.setText("Book doesn't exist");
                }
            }
        });

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    System.out.println(entries.length);
                    Checkout checkout = new Checkout(member, entries);
                    SystemController ci = new SystemController();
                    ci.checkoutBook(checkout);
//                        Start.hideAllWindows();
//                        Start.primStage().show();
                } catch (NullPointerException exception) {
                    messageBar.setText("Book copy is not available");
                }
            }
        });

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
                    member = ci.getMemberById(fields.get("memberid").getText());
                    messageBar.setText(member.getMemberId() + "\t" + member.getFirstName());
                    selected = ci.getRentableBookByIsbn(fields.get("isbn").getText());
                    StringBuilder sb = new StringBuilder();
                    sb.append("Title\tAvailable\tCopy Number\n");
                    for (BookCopy b : selected.getCopies()) {
                        sb.append(b.getBook().getTitle() +  "\t" + b.getBook().isAvailable()  + "\t"  + b.getCopyNum() + "\n");
                    }
                    setTable(sb.toString(), selected);

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
        cart = new TextArea();
        grid.add(cart, 1,5);
        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                Start.hideAllWindows();
                Start.primStage().show();
            }
        });
//        HBox hBack = new HBox(10);
//        hBack.setAlignment(Pos.BOTTOM_LEFT);
//        hBack.getChildren().add(backBtn);
        grid.add(backBtn, 0, 6);
        grid.add(addBtn, 1, 6);
        grid.add(checkoutBtn, 2, 6);
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
