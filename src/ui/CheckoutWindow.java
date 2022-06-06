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

import java.util.Arrays;
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

    private Book book;
    private LibraryMember member;
    private Checkout checkout = new Checkout(new CheckoutEntry[0]);

    public void setTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title\tAvailable\tCopy Number\n");
        Arrays.stream(book.getCopies())
                .filter(c -> Boolean.TRUE.equals(c.isAvailable()))
                .filter(c -> Arrays.stream(checkout.getEntries()).noneMatch(e -> e.getBookCopy().equals(c)))
                .forEach(b -> sb.append(b.getBook().getTitle())
                        .append("\t")
                        .append(b.getBook().isAvailable())
                        .append("\t")
                        .append(b.getCopyNum())
                        .append("\n"));
        ta.setText(sb.toString());
    }

    public void setCart() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title\tCopy Number\n");
        for (CheckoutEntry c : checkout.getEntries()) {
            sb.append(c.getBookCopy().getBook().getTitle())
                    .append("\t")
                    .append(c.getBookCopy().getCopyNum())
                    .append("\n");
        }
        cart.setText(sb.toString());
    }

    /* This class is a singleton */

    public void clear() {
        messageBar.setText("");
        ta.setText("");
        cart.setText("");
        book = null;
        member = null;
        this.checkout = new Checkout(new CheckoutEntry[0]);
    }

    private CheckoutWindow() {
    }

    public void init() {
        GridPane grid = new GridPane();
        grid.setId("top-container");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Checkout book");
        scenetitle.setFont(Font.font("Harlow Solid Italic", FontWeight.NORMAL, 20)); //Tahoma
        grid.add(scenetitle, 0, 0, 2, 1);

        HBox memberBox = new HBox(10);
        memberBox.setAlignment(Pos.CENTER_LEFT);
        HBox bookBox = new HBox(10);
        bookBox.setAlignment(Pos.CENTER);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        HBox checkoutBox = new HBox(10);
        checkoutBox.setAlignment(Pos.CENTER_RIGHT);

        HashMap<String, TextField> fields = new HashMap<>();
        addField(memberBox, new TField(), "MemberID", fields);
        addField(bookBox, new TField(), "ISBN", fields);

        Button searchBtn = getSearchBtn(fields);
        buttonBox.getChildren().add(searchBtn);
        checkoutBox.getChildren().add(getCheckoutBtn(searchBtn, fields));

        HBox firstRow = new HBox(10);
        firstRow.getChildren().addAll(bookBox, buttonBox, memberBox, checkoutBox);
        grid.add(new VBox(firstRow), 0, 2);

        VBox messageBox = new VBox();
        messageBox.getChildren().add(messageBar);
        grid.add(messageBox, 0, 3);

        HBox actionBox = new HBox(10);
        actionBox.setAlignment(Pos.CENTER);
        VBox actions = new VBox(10);
        actions.getChildren().addAll(getAddBtn(), getRemoveBtn());
        actionBox.getChildren().add(actions);

        HBox taBox = new HBox(10);
        taBox.setAlignment(Pos.CENTER_LEFT);
        ta = new TextArea();
        taBox.getChildren().add(ta);

        HBox cartBox = new HBox(10);
        cartBox.setAlignment(Pos.CENTER_RIGHT);
        cart = new TextArea();
        cartBox.getChildren().add(cart);

        HBox thirdRow = new HBox(10);
        thirdRow.getChildren().addAll(taBox, actionBox, cartBox);
        grid.add(thirdRow, 0, 4);
        // back button
        grid.add(getBackBtn(fields), 0, 5);
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
    private Button getAddBtn() {
        Button addBtn = new Button(">");
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (Objects.nonNull(book)) {
                    try {
                        BookCopy copy = book.getNextAvailableCopy();
                        checkout.addEntry(copy);
                        setCart();
                        setTable();
                    } catch (NullPointerException exception) {
                        messageBar.setText("Book copy is not available");
                    }
                } else {
                    messageBar.setText("Book doesn't exist");
                }
            }
        });
        return addBtn;
    }
    private Button getRemoveBtn() {
        Button removeBtn = new Button("<");
        removeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                if (Objects.nonNull(checkout) && checkout.getEntries().length > 0) {
                    checkout.removeLastEntry();
                    setCart();
                    setTable();
                }
            }
        });
        return removeBtn;
    }
    private Button getBackBtn(HashMap<String, TextField> fields) {
        Button backBtn = new Button("Back to Main");
        backBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                fields.forEach((key, value) -> value.setText(""));
                Start.hideAllWindows();
                Start.primStage().show();
            }
        });
        return backBtn;
    }
    private Button getCheckoutBtn(Button searchBtn, HashMap<String, TextField> fields) {
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                try {
                    ValidationFactory.getValidation(fields.get("memberid").getClass().getSimpleName())
                            .validate(fields.get("memberid").getText());
                    if (checkout.getEntries().length > 0) {
                        SystemController ci = new SystemController();
                        member = ci.getMemberById(fields.get("memberid").getText());
                        for (CheckoutEntry entry : checkout.getEntries()) {
                            BookCopy copy = entry.getBookCopy();
                            copy.changeAvailability(); // change book as unavailable
                            copy.getBook().updateCopies(copy); // update the book instance
                            ci.addBook(copy.getBook()); // persist the data
                        }
                        member.addCheckout(checkout); // change member checkout list
                        ci.checkoutBook(member); // save member updated
                        messageBar.setText("Book is checked out"); // show message
                        checkout = new Checkout(new CheckoutEntry[0]);
                        fields.get("memberid").setText("");
                        setCart(); // empty the cart
                        searchBtn.fire();
                    } else {
                        messageBar.setText("Select the books to checking out");
                    }
                } catch (NullPointerException exception) {
                    messageBar.setText("Book copy is not available");
                } catch (InvalidFieldException | LibrarySystemException exception) {
                    messageBar.setText(exception.getMessage());
                }
            }
        });
        return checkoutBtn;
    }
    private Button getSearchBtn(HashMap<String, TextField> fields) {
        Button searchBtn = new Button("Search");
        searchBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // search
                messageBar.setText("");
                try {
                    ValidationFactory.getValidation(fields.get("isbn").getClass().getSimpleName())
                            .validate(fields.get("isbn").getText());
                    SystemController ci = new SystemController();
                    book = ci.getRentableBookByIsbn(fields.get("isbn").getText());
                    setTable();
                } catch (LibrarySystemException | InvalidFieldException exception) {
                    messageBar.setText(exception.getMessage());
                }
            }
        });
        return searchBtn;
    }
}
