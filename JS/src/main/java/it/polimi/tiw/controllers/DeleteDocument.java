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
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;


import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.utilities.ConnectionHandler;

@WebServlet("/DeleteDocument")
@MultipartConfig

public class DeleteDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
    public DeleteDocument() {
        super();
        // TODO Auto-generated constructor stub
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
		//Session authentication
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendMessage(response, "User is not authenticated.");
			return;
		}
		int userId = (int) request.getSession().getAttribute("userId");
		
		String documentId = null;
		Integer id = null;
		
		documentId = StringEscapeUtils.escapeJava(request.getParameter("fileId"));

		if((documentId == null) || (documentId.equals(""))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document ID can't be left empty.");
			return;
		}
		try {
			id = Integer.parseInt(documentId);
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document ID is invalid.");
			return;
		}
		DocumentDAO documentDao = new DocumentDAO(connection);
		try {
			documentDao.isDocumentAccessible(userId, id);
			documentDao.deleteDocument(id);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(id);
			return;
		} 
		catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendMessage(response, "There was an exception during the creation.");
			return;
		}
		catch (NoAccessException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			sendMessage(response, e.getMessage());
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
