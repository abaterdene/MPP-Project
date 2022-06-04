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

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PrintCheckoutsWindow extends Stage implements LibWindow {
    public static final PrintCheckoutsWindow INSTANCE = new PrintCheckoutsWindow();

    private boolean isInitialized = false;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void isInitialized(boolean val) {
        isInitialized = val;
    }

    private Text messageBar = new Text();

    private List<Checkout> checkouts;
    private TextArea ta;
    public void setTable() {
        ta.setText(getPrintText());
    }

    public String getPrintText() {
        StringBuilder sb = new StringBuilder();
        sb.append("ISBN\tTitle\tCopy number\tCheckout Date\tDue Date\n");
        for (Checkout c : checkouts) {
            for (CheckoutEntry e : c.getEntries()) {
                sb.append(e.getBookCopy().getBook().getIsbn())
                        .append("\t")
                        .append(e.getBookCopy().getBook().getTitle())
                        .append("\t")
                        .append(e.getBookCopy().getCopyNum())
                        .append("\t")
                        .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .withZone(ZoneId.of("UTC"))
                                .format(c.getCheckoutDate()))
                        .append("\t")
                        .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                                .withZone(ZoneId.of("UTC"))
                                .format(e.getDueDate()))
                        .append("\n");
            }
        }
        return sb.toString();
    }

    /* This class is a singleton */

    public void clear() {
        messageBar.setText("");
    }

    private PrintCheckoutsWindow() {
    }

    public void init() {
        GridPane grid = new GridPane();
        grid.setId("top-container");
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Print page");
        scenetitle.setFont(Font.font("Harlow Solid Italic", FontWeight.NORMAL, 20)); //Tahoma
        grid.add(scenetitle, 0, 0, 2, 1);

        HBox memberBox = new HBox(10);
        memberBox.setAlignment(Pos.CENTER_LEFT);
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        HBox printBox = new HBox(10);
        printBox.setAlignment(Pos.CENTER_RIGHT);

        HashMap<String, TextField> fields = new HashMap<>();
        addField(memberBox, new TField(), "MemberID", fields);

        Button searchBtn = getSearchBtn(fields);
        buttonBox.getChildren().add(searchBtn);
        printBox.getChildren().add(getPrintBtn());

        HBox firstRow = new HBox(10);
        firstRow.getChildren().addAll(memberBox, buttonBox, printBox);
        grid.add(new VBox(firstRow), 0,2);

        VBox messageBox = new VBox();
        messageBox.getChildren().add(messageBar);
        grid.add(messageBox, 0, 3);

        HBox taBox = new HBox(10);
        taBox.setAlignment(Pos.CENTER_LEFT);
        ta = new TextArea();
        taBox.getChildren().add(ta);

        HBox thirdRow = new HBox(10);
        thirdRow.getChildren().addAll(taBox);
        grid.add(thirdRow, 0, 4);
        // back button
        grid.add(getBackBtn(fields), 0 ,5);
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

    private Button getPrintBtn() {
        Button checkoutBtn = new Button("Printout");
        checkoutBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println(getPrintText());
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
                SystemController ci = new SystemController();
                checkouts = ci.checkoutsByMemberId(fields.get("memberid").getText());
                setTable();
            }
        });
        return searchBtn;
    }
}
