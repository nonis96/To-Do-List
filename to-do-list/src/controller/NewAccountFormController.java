package controller;

import database.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

/**
 * author : W.P.A.M.Nonis <ameshnonis8@gmail.com>
 * contact : 0717730167
 * date : 11/28/2022
 **/
public class NewAccountFormController {
    public Label lblID;
    public PasswordField txtNewPassword;
    public PasswordField txtConformPassword;
    public TextField txtName;
    public TextField txtEMail;
    public Button btnRegister;
    public Label lblPasswordNotMatch1;
    public Label lblPasswordNotMatch2;
    public AnchorPane root;

    public void initialize(){
        txtName.setDisable(true);
        txtEMail.setDisable(true);
        txtNewPassword.setDisable(true);
        txtConformPassword.setDisable(true);
        btnRegister.setDisable(true);
        lblPasswordNotMatch1.setVisible(false);
        lblPasswordNotMatch2.setVisible(false);
    }



    public void btnAddNewUserOnAction(ActionEvent actionEvent) {

        autoGenarateID();

        txtName.setDisable(false);
        txtEMail.setDisable(false);
        txtNewPassword.setDisable(false);
        txtConformPassword.setDisable(false);
        btnRegister.setDisable(false);
    }

    public void autoGenarateID(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select user_id from user order by user_id desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist){

                String oldId = resultSet.getString(1);

                int length = oldId.length();
                String Id = oldId.substring(1, length);

                int intId = Integer.parseInt(Id);

                intId = intId+1;
                if (intId<10){
                    lblID.setText("U00"+intId);
                }
                else if (intId<100) {
                    lblID.setText("U0"+intId);

                }else {
                    lblID.setText("U"+intId);
                }

            }
            else {
                lblID.setText("U001");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        String newPassword = txtNewPassword.getText();
        String conformPassword = txtConformPassword.getText();

        if (newPassword.equals(conformPassword)){
            txtNewPassword.setStyle("-fx-border-color: transparent");
            txtConformPassword.setStyle("-fx-border-color: transparent");

            lblPasswordNotMatch1.setVisible(false);
            lblPasswordNotMatch2.setVisible(false);

            register();

        }else {
            txtNewPassword.setStyle("-fx-border-color: red");
            txtConformPassword.setStyle("-fx-border-color: red");

            lblPasswordNotMatch1.setVisible(true);
            lblPasswordNotMatch2.setVisible(true);

            txtNewPassword.requestFocus();
        }
    }

    public void register(){

        String Id = lblID.getText();
        String UserName = txtName.getText();
        String email = txtEMail.getText();
        String password = txtConformPassword.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into user values (?,?,?,?)");
            preparedStatement.setObject(1,Id);
            preparedStatement.setObject(2,UserName);
            preparedStatement.setObject(3,email);
            preparedStatement.setObject(4,password);

            int i = preparedStatement.executeUpdate();

            if (i !=0){
                new Alert(Alert.AlertType.CONFIRMATION,"Success......").showAndWait();

                Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));

                Scene scene = new Scene(parent);
                Stage primaryStage = (Stage) root.getScene().getWindow();
                primaryStage.setScene(scene);
                primaryStage.setTitle("Login Form");
                primaryStage.centerOnScreen();

            }else {
                new Alert(Alert.AlertType.ERROR,"Some Thing Went Wrong").showAndWait();
            }

        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }
}
