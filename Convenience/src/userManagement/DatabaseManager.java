package userManagement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
	// 현재 mySQL이 구동되어지는 서버의 Host 주소와, 스케마의 이름을 입력
    private static final String DB_URL = "jdbc:mysql://localhost:3306/convenience";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
}