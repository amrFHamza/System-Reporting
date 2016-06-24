package wedo.database;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;

public class OracleJDBC {

	public static Connection createJDBCConnection(String ip, String port, String serviceName, String username, String password) {

		//System.out.println("-------- Oracle JDBC Connection Testing ------");

		try {

			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (ClassNotFoundException e) {

			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return null;

		}

		//System.out.println("Oracle JDBC Driver Registered!");

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@"+ ip + ":" + port + ":" + serviceName , username,
					password);
			
			/*connection = DriverManager.getConnection(
					"jdbc:oracle:thin:@localhost:1523:RAIDPRD", "RP7STCRASAPP",
					"RP7STCRASAPP");*/
			return connection;

		} catch (SQLException e) {

			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return connection;

		}

	}

}