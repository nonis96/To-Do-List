package controller;

import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.xml.bind.SchemaOutputResolver;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * author : W.P.A.M.Nonis <ameshnonis8@gmail.com>
 * contact : 0717730167
 * date : 11/28/2022
 **/
public class LoginFormController {
    public AnchorPane root;
    public TextField txtUserName;
    public PasswordField txtPassword;

    public static String enteredID;

    public void btnCreateNewAccountOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/NewAccountForm.fxml "));
        Scene scene = new Scene(parent);

        Stage primarystage = (Stage) root.getScene().getWindow();
        primarystage.setScene(scene);
        primarystage.setTitle("Create New Account");
        primarystage.centerOnScreen();


    }

    public void btnLoginOnAction(ActionEvent actionEvent) {

        String userName = txtUserName.getText();
        String password = txtPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from user where user_name=? and passward=?");
            preparedStatement.setObject(1,userName);
            preparedStatement.setObject(2,password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()){

                enteredID = resultSet.getString(1);

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/ToDoListForm.fxml"));
                Scene scene = new Scene(parent);
                Stage primarystage = (Stage) root.getScene().getWindow();
                primarystage.setScene(scene);
                primarystage.setTitle("To Do Form");
                primarystage.centerOnScreen();

            }else{
                new Alert(Alert.AlertType.ERROR,"Invalid User name or Password").showAndWait();
                txtUserName.clear();
                txtPassword.clear();

                txtUserName.requestFocus();
            }


        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
