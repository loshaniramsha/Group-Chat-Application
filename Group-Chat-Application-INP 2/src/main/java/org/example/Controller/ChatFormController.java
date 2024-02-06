package org.example.Controller;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Setter;
import org.example.Dto.ChatDto;
import org.example.Model.ChatModel;
import org.example.client.Client;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

public class ChatFormController implements Initializable {
    public static String UserName;
    public AnchorPane root;
    public TextField textfield;
    public Label lblUserName;
    public VBox vBox;
    public ScrollPane Scpane;
    public AnchorPane emojiPane;
    public ImageView propic;
    ChatModel model=new ChatModel();

    public void setClient(Client client) throws SQLException, IOException {
        this.client = client;
        String msg = "Joing the chat";
       // retrieveMsg();
        appendText(msg);
        client.sendMessage(msg);
    }

    public void retrieveMsg() throws IOException, SQLException {
        ArrayList<ChatDto> chat = model.getChat(client.getName());
        System.out.println(chat);

        if (!(chat == null)) {
            for (ChatDto chatDto : chat) {
                if (chatDto.getId().equals("me") && chatDto.getImage() == null) {
                    retriveMyMsg(chatDto.getMassege());
                } else if (chatDto.getId().equals("me") && chatDto.getMassege() == null){
                    setImage(chatDto.getImage().readAllBytes(),chatDto.getUserName());
                } else if (!chatDto.getId().equals("me") && chatDto.getImage() == null) {
                    retriveOtherMsg(chatDto.getMassege());
                } else if (!chatDto.getId().equals("me") && chatDto.getMassege() == null) {
                    setOthersImage(chatDto.getImage().readAllBytes(), chatDto.getId());
                }
            }
        }
    }

    public void setOthersImage(byte[] bytes, String sender) {
        HBox hBox = new HBox();
        Label messageLbl = new Label(sender);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");

        hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; " + (sender.equals(client.getName()) ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;"));
        // Display the image in an ImageView or any other UI component
        Platform.runLater(() -> {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
            imageView.setStyle("-fx-padding: 10px;");
            imageView.setFitHeight(180);
            imageView.setFitWidth(100);

            hBox.getChildren().addAll(messageLbl, imageView);
            vBox.getChildren().add(hBox);
        });
    }

    public void retriveOtherMsg(String message) throws SQLException {
        //print msg on other clients
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: center-left;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
        Label messageLbl = new Label(message);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        hBox.getChildren().add(messageLbl);
        Platform.runLater(() -> vBox.getChildren().add(hBox));
        //   System.out.println(message);  //with sender name
    }
    void retriveMyMsg(String message) throws SQLException {
        // print in my chat
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: center-right;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
        Label messageLbl = new Label(message);
        messageLbl.setStyle("-fx-background-color:  purple;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        hBox.getChildren().add(messageLbl);
        vBox.getChildren().add(hBox);
        //   System.out.println(message);   //my msg

    }


    private File file;
    @Setter
    private Client client;
    private String message;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        emojiPane.setVisible(false);
        lblUserName.setText(UserName);
    }

    public void lblSendEmojis(MouseEvent mouseEvent) {
        emojiPane.setVisible(!emojiPane.isVisible());

    }

    public void lblsendPhoto(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg","*.gif");
        fileChooser.getExtensionFilters().add(imageFilter);
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            try {
                InputStream InputStream = new FileInputStream(selectedFile);
                byte[] bytes = Files.readAllBytes(selectedFile.toPath());
                HBox hBox = new HBox();
                hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; -fx-alignment: center-right;");

                // Display the image in an ImageView or any other UI component
                ImageView imageView = new ImageView(new Image(new FileInputStream(selectedFile)));
                imageView.setStyle("-fx-padding: 10px;");
                imageView.setFitHeight(180);
                imageView.setFitWidth(100);

                hBox.getChildren().addAll(imageView);
                vBox.getChildren().add(hBox);

                client.sendImage(bytes);
                model.saveChat(new ChatDto(lblUserName.getText(),"me",null,
                        InputStream,LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void writeMessage(String message) throws SQLException {
        //print msg on other clients
        HBox hBox = new HBox();
        hBox.setStyle("-fx-alignment: center-left;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
        Label messageLbl = new Label(message);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
        hBox.getChildren().add(messageLbl);
        model.saveChat(new ChatDto(lblUserName.getText(),"from",message,null,LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
        Platform.runLater(() -> vBox.getChildren().add(hBox));
        //   System.out.println(message);  //with sender name
    }

    public void setImage(byte[] bytes, String sender) {
        HBox hBox = new HBox();
        Label messageLbl = new Label(sender);
        messageLbl.setStyle("-fx-background-color:   #2980b9;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");

        hBox.setStyle("-fx-fill-height: true; -fx-min-height: 50; -fx-pref-width: 520; -fx-max-width: 520; -fx-padding: 10; " + (sender.equals(client.getName()) ? "-fx-alignment: center-right;" : "-fx-alignment: center-left;"));
        // Display the image in an ImageView or any other UI component
        Platform.runLater(() -> {
            ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(bytes)));
            imageView.setStyle("-fx-padding: 10px;");
            imageView.setFitHeight(180);
            imageView.setFitWidth(100);

            try {
                model.saveChat(new ChatDto(lblUserName.getText(),sender,null,new ByteArrayInputStream(bytes),LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            hBox.getChildren().addAll(messageLbl, imageView);
            vBox.getChildren().add(hBox);
        });
    }

    public void lblSend(MouseEvent mouseEvent) {
        try {
            String text = textfield.getText();
            if (text != null) {
                appendText(text);
                client.sendMessage(text);
                textfield.clear();
            } else {
                ButtonType ok = new ButtonType("Ok");
                ButtonType cancel = new ButtonType("Cancel");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Empty message. Is it ok?", ok, cancel);
                alert.showAndWait();
                ButtonType result = alert.getResult();
                if (result.equals(ok)) {
                    client.sendMessage(null);
                }
                textfield.clear();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void appendText(String message) throws SQLException {
        // print in my chat
        if (message.startsWith(" joined")) {
            HBox hBox = new HBox();
            hBox.setStyle("-fx-alignment: center;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
            Label messageLbl = new Label(message);
            messageLbl.setStyle("-fx-background-color: rgb(128,128,128);-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
            hBox.getChildren().add(messageLbl);
            vBox.getChildren().add(hBox);
            //    System.out.println(message);   //my msg
        } else {
            HBox hBox = new HBox();
            hBox.setStyle("-fx-alignment: center-right;-fx-fill-height: true;-fx-min-height: 50;-fx-pref-width: 520;-fx-max-width: 520;-fx-padding: 10");
            Label messageLbl = new Label(message);
            messageLbl.setStyle("-fx-background-color:  purple;-fx-background-radius:15;-fx-font-size: 18;-fx-font-weight: normal;-fx-text-fill: white;-fx-wrap-text: true;-fx-alignment: center-left;-fx-content-display: left;-fx-padding: 10;-fx-max-width: 350;");
            hBox.getChildren().add(messageLbl);
            vBox.getChildren().add(hBox);
            model.saveChat(new ChatDto(lblUserName.getText(),"me",message,null,LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
            //   System.out.println(message);   //my msg

        }
    }


    public void lblMinimizeOnAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    public void lblCloseOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }

    public void image1OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE00");
        emojiPane.setVisible(false);
    }

    public void image3OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83E\uDD17");
        emojiPane.setVisible(false);
    }

    public void image4OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE05");
        emojiPane.setVisible(false);
    }

    public void image5Onaction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE02");
        emojiPane.setVisible(false);
    }

    public void image6OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE22");
        emojiPane.setVisible(false);
    }

    public void image7OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE0E");
        emojiPane.setVisible(false);
    }

    public void image8OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE0D");
        emojiPane.setVisible(false);
    }

    public void image2OnAction(MouseEvent mouseEvent) {
        textfield.appendText("\uD83D\uDE06");
        emojiPane.setVisible(false);
    }
}


