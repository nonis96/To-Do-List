package database;

import com.mysql.cj.jdbc.Driver;
import com.mysql.cj.jdbc.JdbcConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * author : W.P.A.M.Nonis <ameshnonis8@gmail.com>
 * contact : 0717730167
 * date : 12/1/2022
 **/
public class DBConnection {
    private static DBConnection dbConnection;
    private Connection connection;
    private DBConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");

            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/todolist","root","");

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static DBConnection getInstance(){
        return (dbConnection == null) ? dbConnection = new DBConnection() : dbConnection;
    }

    public Connection getConnection(){
        return connection;
    }

}
