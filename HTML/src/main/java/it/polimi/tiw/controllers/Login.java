package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.NoUsernameException;
import it.polimi.tiw.exceptions.WrongPasswordException;
import it.polimi.tiw.utilities.ConnectionHandler;

public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
    public Login() {
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
		String path = null;

		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));

		if( username == null || password == null || username.equals("") || password.equals("")) {
			sendMessage(response, "Username or password can't be left empty.");
			return;
		}
		UserDAO userDao = new UserDAO(connection);
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);

		try {
			userDao.loginUser(user);
			request.getSession().setAttribute("userId", user.getUserId());
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception during the login.");
			return;
		}
		catch(NoUsernameException | WrongPasswordException e) {
			sendMessage(response, e.getMessage());
			return;
		}
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}
	
	private void sendMessage(HttpServletResponse response, String message) throws IOException{
		String path = getServletContext().getContextPath() + "/ShowIndex?loginOutcome=" + message;
		response.sendRedirect(path);
		return;
	}
	
	
}
