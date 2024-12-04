package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.mindrot.jbcrypt.BCrypt;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.exceptions.NoUsernameException;
import it.polimi.tiw.exceptions.WrongPasswordException;

public class UserDAO {
	
	private Connection connection;
	
	public UserDAO(Connection connection) {
		this.connection = connection;
	}
	
	public String getUsernameById(int userId) throws SQLException {
		String query = "SELECT username FROM user WHERE userid = ?";

		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setInt(1, userId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					return resultSet.getString("username");
				}
				else {
					throw new SQLException("SQLException, can't find user with such userId");
				}
			}
		}
	}
	
	public void loginUser(User user) throws SQLException, NoUsernameException, WrongPasswordException{
		String query = "SELECT password, email, userid FROM user WHERE username = ?";

		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			pstatement.setString(1, user.getUsername());
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					String storedHash = resultSet.getString("password");
					if(BCrypt.checkpw(user.getPassword(), storedHash)) {
						user.setEmail(resultSet.getString("email"));
						user.setUserId(resultSet.getInt("userid"));
						return;
					}
					else {
						throw new WrongPasswordException();
					}
				}
				else {
					throw new NoUsernameException();
				}
			}
		}
	}
	
	public void registerUser(User user) throws SQLException{
		String query = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
		
		try(PreparedStatement pstatement = connection.prepareStatement(query)) {
			String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
			
			pstatement.setString(1, user.getUsername());
			pstatement.setString(2, hashedPassword);
			pstatement.setString(3, user.getEmail());
			
			int rowsChanged = pstatement.executeUpdate();
			if(rowsChanged == 1) {
				String getUserId = "SELECT userId FROM user WHERE username = ?";
				try(PreparedStatement userIdStatement = connection.prepareStatement(getUserId)){
					userIdStatement.setString(1, user.getUsername());
					try(ResultSet resultUserId = userIdStatement.executeQuery()){
						if(resultUserId.next()) {
							user.setUserId(resultUserId.getInt("UserId"));
						}
						else {
							throw new SQLException("No UserId was found associated with" + user.getUsername());
						}
					}
				}
				return;
			}
			else {
				throw new SQLException("There was a SQLException no user was created.");
			}	
		}
	}
	
	public boolean isUsernameTaken(User user) throws SQLException {
		String query = "SELECT 1 FROM user WHERE username = ?";

		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, user.getUsername());

			try(ResultSet resultSet = pstatement.executeQuery()){
				return resultSet.next(); 
			}
		}
	}
}
