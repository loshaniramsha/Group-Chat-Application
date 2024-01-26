package org.example.client;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import org.example.Controller.ChatFormController;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;

public class Client implements Runnable, Serializable {

    @Getter
    private final String name;

    @Getter
    private ImageView imageView;
    private final Socket socket;
    private final DataInputStream inputStream;
    private final DataOutputStream outputStream;
    private ChatFormController chatFormController;



    public Client(String name, ImageView imageView) throws IOException {
        this.name = name;
        if (imageView == null) {

        } else {
            this.imageView = imageView;
        }


        socket = new Socket("localhost", 1500);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());

        outputStream.writeUTF(name);
        outputStream.flush();

        try {
            loadScene();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void run() {
        String message = "";
        while (!message.equals("exit")) {
            try {
                message = inputStream.readUTF();
                if (message.equals("*image*")) {
                    receiveImage();
                } else {
                    chatFormController.writeMessage(message);
                }

            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ignored) {

                }
            }
        }
    }

    public void sendMessage(String msg) throws IOException {
        outputStream.writeUTF(msg);
        outputStream.flush();
    }

    public void sendImage(byte[] bytes) throws IOException {
        outputStream.writeUTF("*image*");
        outputStream.writeInt(bytes.length);
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void loadScene() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Chat_form.fxml"));
        Parent parent = loader.load();
        chatFormController = loader.getController();
        chatFormController.setClient(this);
        chatFormController.lblUserName.setText(name);
        chatFormController.propic.setImage(imageView.getImage());
        stage.setResizable(false);
        stage.setScene(new Scene(parent));
        stage.setTitle(name + "'s Chat");
        stage.show();

        stage.setOnCloseRequest(event -> {
            try {
//                System.out.println(name + " closed");
                inputStream.close();
                outputStream.close();
                socket.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        });

    }

    private void receiveImage() throws IOException {
        String utf = inputStream.readUTF();
        int size = inputStream.readInt();
        byte[] bytes = new byte[size];
        inputStream.readFully(bytes);
        System.out.println(name + "- Image received: from " + utf);
        chatFormController.setImage(bytes, utf);
        // Handle the received image bytes as needed
    }
}
