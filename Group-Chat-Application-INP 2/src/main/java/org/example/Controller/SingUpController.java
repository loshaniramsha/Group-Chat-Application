package org.example.Controller;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.Dto.UserDto;
import org.example.Model.UserModel;

import java.io.*;
import java.sql.SQLException;

public class SingUpController {
    public AnchorPane root;
    public JFXTextField txtUserName;
    public JFXTextField txtPassword;
    public JFXTextField txtConfirm;
    public Button btncreate;
    public Button btnBack;
    public ImageView imageView;
    public ImageView chooserDp;
    private File file;

    public void createAccountOnAction(ActionEvent actionEvent) {
        if (!(txtUserName.getText().isEmpty() || txtPassword.getText().isEmpty() || txtConfirm.getText().isEmpty())) {
            try {
                String username = txtUserName.getText();
                String password = txtPassword.getText();
                String confirmPassword = txtConfirm.getText();

                if (!password.equals(confirmPassword)) {
                    new Alert(Alert.AlertType.WARNING, "Password does not match", ButtonType.OK).show();
                } else {
                    boolean isExists = UserModel.existsUser(username);
                    if (!isExists) {
                        boolean isSaved;
                        if (file != null) {
                            InputStream inputStream = new FileInputStream(file);
                            isSaved = UserModel.saveUser(new UserDto(username, password, inputStream));
                        } else {
                            isSaved = UserModel.saveUser(new UserDto(username, password, null));
                        }
                        if (isSaved) {
                            new Alert(Alert.AlertType.INFORMATION, "Saved", ButtonType.OK).show();
                            backOnAction(actionEvent);
                        }
                    } else {
                        new Alert(Alert.AlertType.WARNING, "Username already exists", ButtonType.OK).show();
                    }
                }

            }catch(FileNotFoundException | SQLException e){
                e.printStackTrace();
            } catch(IOException e){
                throw new RuntimeException(e);
            }
        } else {
            new Alert(Alert.AlertType.WARNING, "All fields are required", ButtonType.OK).show();
        }
    }

    public void backOnAction(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) txtUserName.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/Login_form.fxml"))));
        stage.setTitle("Login");
        stage.show();
    }

    public void lblChooserImage(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select the image");
        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp");
        fileChooser.getExtensionFilters().add(imageFilter);
        file = fileChooser.showOpenDialog(txtUserName.getScene().getWindow());
        if (file != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                imageView.setImage(new Image(fileInputStream));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
