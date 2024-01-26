package org.example.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.Dto.UserDto;
import org.example.Model.UserModel;
import org.example.client.Client;

import java.io.IOException;
import java.sql.SQLException;

import static sun.nio.ch.IOUtil.load;

public class LoginFormController {
    public AnchorPane root;
    public TextField textfield;
    public Button btn01;
    private static double xOffset = 0;
    private static double yOffset = 0;
    public TextField textUserNamer;
    public TextField textPassWord;
    public Hyperlink creatAccount;
    public ImageView imageView;

    public void loginOnAction(ActionEvent actionEvent) throws Exception, SQLException {
        if (!textUserNamer.getText().isEmpty() || !textPassWord.getText().isEmpty()) {
            String username = textUserNamer.getText();
            String password = textPassWord.getText();

            boolean isExists = false;
            isExists = UserModel.existsUser(username);
            if (isExists) {
                UserDto userDto = UserModel.userDetails(username);
                if (!userDto.getPassword().equals(password)) {
                    new Alert(Alert.AlertType.WARNING, "Invalid username or password", ButtonType.OK).show();
                } else {

                    if (userDto.getProfilePic() != null){
                        imageView = new ImageView(new Image(userDto.getProfilePic()));
                    } else {
                        imageView = new ImageView(new Image("/icons/profile.png"));
                    }
                    loadpage();


                    textUserNamer.clear();
                    textPassWord.clear();
                }

            }else{
                new Alert(Alert.AlertType.WARNING, "Invalid username or password", ButtonType.OK).show();
            }
        }else{
            new Alert(Alert.AlertType.WARNING, "Username and password are required", ButtonType.OK).show();
        }



        /*Stage stage=(Stage) textUserNamer.getScene().getWindow();
        stage.setTitle("ChatRoom");
        ChatFormController.UserName=textUserNamer.getText();
        Parent root = FXMLLoader.load(this.getClass().getResource("/view/Chat_form.fxml"));
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        Scene scene=new Scene(root);
       // scene.setFill(null);
        stage.setScene(scene);
        stage.show();*/
    }

    private void loadpage() throws IOException {
        Client client = new Client(textUserNamer.getText(), imageView);
        Thread thread = new Thread(client);
        thread.start();

       // ChatFormController.UserName=textUserNamer.getText();
        /*Stage stage = (Stage) textUserNamer.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Chat_form.fxml"))));
        stage.setTitle("ChatRoom");
        stage.show();*/



    }


    public void lblCloseOnAction(MouseEvent mouseEvent) {
        System.exit(0);
    }



    public void lblMinimizeOnAction(MouseEvent mouseEvent) {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setIconified(true);
    }

    public void lblCreateAccountOnAction(MouseEvent mouseEvent) throws Exception {

        Stage stage = (Stage) textUserNamer.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/SingUp_Form.fxml"))));
        stage.setTitle("Sign up");
        stage.show();

    }
}
