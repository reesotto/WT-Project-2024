package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
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

@WebServlet("/CheckRegister")
@MultipartConfig
public class CheckRegister extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
    
    public CheckRegister() {
        super();
    }
    
    public void init() throws ServletException{
    	try {
    		connection = ConnectionHandler.getConnection(getServletContext());
    	}
    	catch(SQLException e) {
    		throw new ServletException("There was a SQLException connecting to the database.");
    	}
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = null;
		String password = null;
		String passwordconfirm = null;
		String email = null;

		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		passwordconfirm = StringEscapeUtils.escapeJava(request.getParameter("passwordconfirm"));
		email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		
		if((username == null || password == null || email == null || passwordconfirm == null) || 
				(username.equals("") || password.equals("") || email.equals("") || passwordconfirm.equals(""))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Username or password can't be left empty.");
			return;
		}

		if(!PatternChecker.isAlphaNumeric(username)){
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Credential values contain special characters.");
			return;
		}

		if(!email.matches("^[a-zA-Z0-9\\.]+@[a-zA-Z0-9\\.]+\\.[a-zA-Z]{2,6}$")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Email is not valid, (e.g., user@example.com).");
			return;
		}
		
		if(!password.equals(passwordconfirm)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Password confirmation isn't equal to password.");
			return;
		}
		
		UserDAO userDao = new UserDAO(connection);
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);

		try {
			if(userDao.isUsernameTaken(user)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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
				
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				response.setCharacterEncoding("UTF-8");
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
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(message);
		return;
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}
}
