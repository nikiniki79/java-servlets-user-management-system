package net.usermanagement.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.usermanagement.model.User;

public class UserDAO {
	private String jdbcURL = "jdbc:mysql://localhost:3306/userdb?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=CONVERT_TO_NULL&serverTimezone=GMT";
	private String jdbcUsername = "root";
	private String jdbcPassword = "";
	
	private static final String INSERT_USERS_SQL = "INSERT INTO users" + " (name, email, country) VALUES"
			+ " (?, ?, ?);";
	
	public static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?";
	public static final String SELECT_ALL_USERS = "select * from users";
	public static final String DELETE_USERS_SQL = "delete from users where id = ?;";
	public static final String UPDATE_USERS_SQL = "update users set name =?, email =?, country =? where id =?;";
	
	public UserDAO() {
	}
	
	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	}
	
	// Create or insert user
	public void insertUser(User user) throws SQLException {
		System.out.println(INSERT_USERS_SQL);
		try(Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USERS_SQL)) {
			preparedStatement.setString(1, user.getName());
			preparedStatement.setString(2, user.getEmail());
			preparedStatement.setString(3, user.getCountry());
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			printSQLException(e);
		}
		
	} 
	
	
	// update user
	public boolean updateUser(User user) throws SQLException {
		boolean rowUpdated;
		try (Connection connection = getConnection();
				PreparedStatement statement = 	connection.prepareStatement(UPDATE_USERS_SQL);) {
			statement.setString(1, user.getName());
			statement.setString(2, user.getEmail());
			statement.setString(3, user.getCountry());
			statement.setInt(4, user.getId());
			
			rowUpdated = statement.executeUpdate() > 0;
		}
		return rowUpdated;
	}
	
	// select user by id
	public User selectUser(int id) {
		User user = null;
		// Establishing a connection
		try (Connection connection = getConnection();
				// Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
			preparedStatement.setInt(1, id); // placeholder for id
			System.out.println(preparedStatement);
			// execute the query
			ResultSet rs = preparedStatement.executeQuery();
			
			// Process the ResultSet object
			while(rs.next()) {
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				user = new User(id, name, email, country);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return user;
	}
	
	// select users
	public List<User> selectAllUsers() {
		List<User> users = new ArrayList<>();
		// Establishing a connection
		try (Connection connection = getConnection();
				// Create a statement using connection object
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID);) {
			System.out.println(preparedStatement);
			// execute the query
			ResultSet rs = preparedStatement.executeQuery();
			
			// Process the ResultSet object
			while(rs.next()) {
				int id = rs.getInt("id");
				String name = rs.getString("name");
				String email = rs.getString("email");
				String country = rs.getString("country");
				users.add(new User(id, name, email, country));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return users;
	}
	
	// delete user
	public boolean deleteUser(int id) throws SQLException {
		boolean rowDeleted;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_USERS_SQL);) {
			statement.setInt(1, id);
			rowDeleted = statement.executeUpdate() > 0;
		}
		return rowDeleted;
	}
	
	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}
}
