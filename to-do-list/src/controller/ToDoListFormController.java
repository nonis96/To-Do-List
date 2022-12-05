package controller;

import database.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.TodoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

/**
 * author : W.P.A.M.Nonis <ameshnonis8@gmail.com>
 * contact : 0717730167
 * date : 11/28/2022
 **/
public class ToDoListFormController {
    public AnchorPane root;
    public Pane subroot;
    public TextField txtToDo;
    public Label lblId;
    public ListView<TodoTM> lstTodos;
    public TextField txtSelectedTodo;
    public Button btnDelete;
    public Button btnUpdate;
    String id;

    public void initialize(){

        subroot.setVisible(false);
        lblId.setText(LoginFormController.enteredID);

        loadList();

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        txtSelectedTodo.setDisable(true);

        lstTodos.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoTM>() {
            @Override
            public void changed(ObservableValue<? extends TodoTM> observable, TodoTM oldValue, TodoTM newValue) {

                btnDelete.setDisable(false);
                btnUpdate.setDisable(false);
                txtSelectedTodo.setDisable(false);

                subroot.setVisible(false);

                TodoTM selectedItem = lstTodos.getSelectionModel().getSelectedItem();
                txtSelectedTodo.setText(selectedItem.getDescription());

                id = selectedItem.getId();

            }
        });

    }

    public void btnLogoutOnAction(ActionEvent actionEvent) throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Logout", ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){

            Parent parent = FXMLLoader.load(this.getClass().getResource("../view/LoginForm.fxml"));

            Scene scene = new Scene(parent);
            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
        }


    }

    public void btnAddToDoListOnAction(ActionEvent actionEvent) {
        subroot.setVisible(true);

        btnDelete.setDisable(true);
        btnUpdate.setDisable(true);
        txtSelectedTodo.setDisable(true);

        lstTodos.getSelectionModel().clearSelection();
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {

        String id = autoGenarateId();
        String description = txtToDo.getText();
        String user_id = lblId.getText();


        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("insert into todo values (?,?,?)");

            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_id);

            int i = preparedStatement.executeUpdate();
            System.out.println(i);

            txtToDo.clear();
            subroot.setVisible(false);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        loadList();

    }

    public String autoGenarateId(){

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist){

                String oldId = resultSet.getString(1);

                int length = oldId.length();
                String Id = oldId.substring(1, length);

                int intId = Integer.parseInt(Id);

                intId = intId+1;
                if (intId<10){
                    return "T00" + intId;
                }
                else if (intId<100) {
                    return "T0" + intId;

                }else {
                    return "T" + intId;
                }
            }
            else {
                return "T001";
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void loadList(){

        ObservableList<TodoTM> todos = lstTodos.getItems();
        todos.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
            preparedStatement.setObject(1,lblId.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()){
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                TodoTM object = new TodoTM(id,description,user_id);

                todos.add(object);
            }
            lstTodos.refresh();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this todo..?",ButtonType.YES,ButtonType.NO);

        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1,id);
                preparedStatement.executeUpdate();
                loadList();
                txtSelectedTodo.clear();
                btnUpdate.setDisable(true);
                btnDelete.setDisable(true);
                txtSelectedTodo.setDisable(true);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ?  where id = ?");
            preparedStatement.setObject(1,txtSelectedTodo.getText());
            preparedStatement.setObject(2,id);
            preparedStatement.executeUpdate();

            loadList();

            txtSelectedTodo.clear();
            btnUpdate.setDisable(true);
            btnDelete.setDisable(true);
            txtSelectedTodo.setDisable(true);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
