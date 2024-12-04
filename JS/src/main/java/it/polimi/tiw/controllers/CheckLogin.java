package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.NoUsernameException;
import it.polimi.tiw.exceptions.WrongPasswordException;
import it.polimi.tiw.utilities.ConnectionHandler;

@WebServlet("/CheckLogin")
@MultipartConfig
public class CheckLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection; 
    
    public CheckLogin() {
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

		username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		
		if( username == null || password == null || username.equals("") || password.equals("")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(user.getUsername());
			
			return;
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendMessage(response, "There was an exception during the login.");
			return;
		}
		catch(NoUsernameException | WrongPasswordException e) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendMessage(response, e.getMessage());
			return;
		}
	}
	
	private void sendMessage(HttpServletResponse response, String message) throws IOException {
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(message);	
		return;
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

}
