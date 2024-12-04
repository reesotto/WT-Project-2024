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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.SameFileNameException;
import it.polimi.tiw.utilities.ConnectionHandler;

@WebServlet("/MoveDocument")
@MultipartConfig
public class MoveDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection;
	
    public MoveDocument() {
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
		//Session authentication
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendMessage(response, "User is not authenticated.");
			return;
		}
		int userId = (int) request.getSession().getAttribute("userId");
		
		Integer documentId = null;
		Integer targetFolderId = null;
		
		try {
			documentId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("documentId")));
			targetFolderId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("targetFolderId")));
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document or folder is invalid.");
			return;
		}
		
		DocumentDAO documentDao = new DocumentDAO(connection);
		FolderDAO folderDao = new FolderDAO(connection);
		try {
			documentDao.isDocumentAccessible(userId, documentId);
			folderDao.isFolderAccessible(userId, targetFolderId);
			documentDao.moveDocument(documentId, targetFolderId);
			Document document = documentDao.getDocumentById(userId, documentId);
			Gson gson = new Gson();
			String json = gson.toJson(document);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			return;
			
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendMessage(response, "There was an exception during the transfer.");
			return;
		}
		catch(NoAccessException | SameFileNameException e) {
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
