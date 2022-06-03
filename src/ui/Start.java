package ui;

import java.util.Collections;
import java.util.List;

import business.ControllerInterface;
import business.SystemController;
import dataaccess.Auth;
import dataaccess.User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;


public class Start extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    private static Stage primStage = null;

    public static Stage primStage() {
        return primStage;
    }

    public static class Colors {
        static Color green = Color.web("#034220");
        static Color red = Color.FIREBRICK;
    }

    private static Stage[] allWindows = {
            LoginWindow.INSTANCE,
            AllMembersWindow.INSTANCE,
            AllBooksWindow.INSTANCE,
            AddMemberWindow.INSTANCE,
            CheckoutWindow.INSTANCE,
            AddBookCopyWindow.INSTANCE,
            AddBookWindow.INSTANCE
    };

    public static void hideAllWindows() {
        primStage.hide();
        for (Stage st : allWindows) {
            st.hide();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        display(primaryStage);
    }

    public static void display(Stage primaryStage) {
        primStage = primaryStage;
        primaryStage.setTitle("Main Page");

        VBox topContainer = new VBox();
        topContainer.setId("top-container");
        MenuBar mainMenu = new MenuBar();
        VBox imageHolder = new VBox();
        Image image = new Image("ui/library.jpeg", 400, 300, false, false);

        // simply displays in ImageView the image as is
        ImageView iv = new ImageView();
        iv.setImage(image);
        imageHolder.getChildren().add(iv);
        imageHolder.setAlignment(Pos.CENTER);
        HBox splashBox = new HBox();
        Label splashLabel = new Label("The Library System ");
        splashLabel.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 30));
        splashBox.getChildren().add(splashLabel);
        splashBox.setAlignment(Pos.CENTER);
        topContainer.getChildren().add(mainMenu);
        topContainer.getChildren().add(splashBox);
        addUserInfo(topContainer);
        topContainer.getChildren().add(imageHolder);

        Menu optionsMenu = new Menu("Menu");
        MenuItem login = getLoginItem();
        MenuItem logout = getLogoutItem(primaryStage);
        MenuItem bookIds = getBooksIdsItem();
        MenuItem memberIds = getMemberIdsItem();
        if (getUser() == null) {
            optionsMenu.getItems().addAll(login);
            mainMenu.getMenus().addAll(optionsMenu);
        } else {
            optionsMenu.getItems().addAll(logout, bookIds, memberIds);
            mainMenu.getMenus().addAll(optionsMenu);
            if (Auth.LIBRARIAN.equals(getUser().getAuthorization()) ||
                    Auth.BOTH.equals(getUser().getAuthorization())) {
                Menu libMenu = new Menu("Librarian");
                MenuItem checkoutMenu = getCheckoutItem();
                libMenu.getItems().add(checkoutMenu);
                mainMenu.getMenus().addAll(libMenu);
            }
            if (Auth.ADMIN.equals(getUser().getAuthorization()) ||
                    Auth.BOTH.equals(getUser().getAuthorization())) {
                Menu adminMenu = new Menu("Admin");
                MenuItem memberAdd = getMemberAddItem();
				MenuItem bookAdd = getBookAddItem(); // 0.5
                MenuItem bookCopyAdd = getBookCopyAddItem();
                adminMenu.getItems().addAll(memberAdd, bookAdd, bookCopyAdd);
                mainMenu.getMenus().addAll(adminMenu);
            }
        }


        Scene scene = new Scene(topContainer, 420, 375);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Start.class.getResource("library.css").toExternalForm());
        primaryStage.show();
    }

    private static void addUserInfo(VBox outer) {
        if (getUser() != null) {
            HBox userBox = new HBox();
            Label userLabel = new Label("Welcome " + getUser().getId() + " as " + getUser().getAuthorization());
            userLabel.setFont(Font.font("Trajan Pro", FontWeight.BOLD, 18));
            userBox.getChildren().add(userLabel);
            userBox.setAlignment(Pos.CENTER);
            outer.getChildren().add(userBox);
        }
    }

    private static User getUser() {
        SystemController ci = new SystemController();
        return ci.currentUser;
    }

    private static MenuItem getCheckoutItem() {
        MenuItem checkout = new MenuItem("Checkout");

        checkout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!CheckoutWindow.INSTANCE.isInitialized()) {
                    CheckoutWindow.INSTANCE.init();
                }
                CheckoutWindow.INSTANCE.clear();
                CheckoutWindow.INSTANCE.show();
            }
        });
        return checkout;
    }

    private static MenuItem getLoginItem() {
        MenuItem login = new MenuItem("Login");

        login.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!LoginWindow.INSTANCE.isInitialized()) {
                    LoginWindow.INSTANCE.init();
                }
                LoginWindow.INSTANCE.clear();
                LoginWindow.INSTANCE.show();
            }
        });
        return login;
    }

    private static MenuItem getLogoutItem(Stage primaryStage) {
        MenuItem logout = new MenuItem("Logout");
        logout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                SystemController ci = new SystemController();
                ci.logout();
                display(primaryStage);
            }
        });
        return logout;
    }

    private static MenuItem getBooksIdsItem() {
        MenuItem bookIds = new MenuItem("All Book Ids");
        bookIds.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!AllBooksWindow.INSTANCE.isInitialized()) {
                    AllBooksWindow.INSTANCE.init();
                }
                ControllerInterface ci = new SystemController();
                List<String> ids = ci.allBookIds();
                Collections.sort(ids);
                StringBuilder sb = new StringBuilder();
                for (String s : ids) {
                    sb.append(s + "\n");
                }
                AllBooksWindow.INSTANCE.setData(sb.toString());
                AllBooksWindow.INSTANCE.show();
            }
        });
        return bookIds;
    }

    private static MenuItem getMemberIdsItem() {
        MenuItem memberIds = new MenuItem("All Member Ids");
        memberIds.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!AllMembersWindow.INSTANCE.isInitialized()) {
                    AllMembersWindow.INSTANCE.init();
                }
                ControllerInterface ci = new SystemController();
                List<String> ids = ci.allMemberIds();
                Collections.sort(ids);
                System.out.println(ids);
                StringBuilder sb = new StringBuilder();
                for (String s : ids) {
                    sb.append(s).append("\n");
                }
                System.out.println(sb.toString());
                AllMembersWindow.INSTANCE.setData(sb.toString());
                AllMembersWindow.INSTANCE.show();
            }
        });
        return memberIds;
    }

    private static MenuItem getMemberAddItem() {
        MenuItem memberAdd = new MenuItem("Add Member");
        memberAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!AddMemberWindow.INSTANCE.isInitialized()) {
                    AddMemberWindow.INSTANCE.init();
                }
                AddMemberWindow.INSTANCE.clear();
                AddMemberWindow.INSTANCE.show();
            }
        });
        return memberAdd;
    }

    private static MenuItem getBookAddItem() {
        MenuItem bookAdd = new MenuItem("Add Book");
        bookAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!AddBookWindow.INSTANCE.isInitialized()) {
                    AddBookWindow.INSTANCE.init();
                }
                AddBookWindow.INSTANCE.clear();
                AddBookWindow.INSTANCE.show();
            }
        });
        return bookAdd;
    }

    private static MenuItem getBookCopyAddItem() {
        MenuItem bookCopyAdd = new MenuItem("Add Book Copy");
        bookCopyAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                hideAllWindows();
                if (!AddBookCopyWindow.INSTANCE.isInitialized()) {
                    AddBookCopyWindow.INSTANCE.init();
                }
                AddBookCopyWindow.INSTANCE.clear();
                AddBookCopyWindow.INSTANCE.show();
            }
        });
        return bookCopyAdd;
    }
}
