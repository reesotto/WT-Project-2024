package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utilities.ConnectionHandler;
import it.polimi.tiw.utilities.PatternChecker;

public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;

	public Register() {
		super();
	}

	public void init() throws ServletException{
		try {
			connection = ConnectionHandler.getConnection(getServletContext());
		}
		catch(SQLException e) {
			throw new ServletException("There was a SQLException connecting to the database");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password = null;
		String email = null;

		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));	

		if((username == null || password == null || email == null) || (username.equals("") || password.equals("") || email.equals(""))) {
			sendMessage(response, "Username, password or email can't be left empty.");
			return;
		}

		if(!PatternChecker.isAlphaNumeric(username)){
			sendMessage(response, "Credential values contain special characters.");
			return;
		}

		if(!email.matches("^[a-zA-Z0-9\\.]+@[a-zA-Z0-9\\.]+\\.[a-zA-Z]{2,6}$")) {
			sendMessage(response, "Email is not valid, (e.g., user@example.com).");
			return;
		}

		UserDAO userDao = new UserDAO(connection);
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);

		try {
			if(userDao.isUsernameTaken(user)) {
				sendMessage(response, "Username is already taken, please choose another one.");
				return;
			}
			else {
				userDao.registerUser(user);
				
				FolderDAO folderDao = new FolderDAO(connection);
				Date currentDate = new Date(System.currentTimeMillis());

				//Creates a root folder upon user registering
				Folder folder = new Folder();
				folder.setDate(currentDate);
				folder.setName("root");
				folder.setOwner(user.getUserId());
				folder.setParentFolder(null);
				
				folderDao.createFolder(folder);
				sendMessage(response, "Successful registration, user was created.");
				return;
			}
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
	}
	
	private void sendMessage(HttpServletResponse response, String message) throws IOException{
		String path = getServletContext().getContextPath() + "/ShowIndex?registerOutcome=" + message;
		response.sendRedirect(path);
		return;
	}


	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

}
