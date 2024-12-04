package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.SameFileNameException;
import it.polimi.tiw.utilities.ConnectionHandler;

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
		int userId;
		String stringfolderId;
		String stringdocumentId;
		Integer folderId;
		Integer documentId;
		HttpSession session = request.getSession();
		String indexpath = getServletContext().getContextPath() + "/index.html";
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.sendRedirect(indexpath);
			return;
		}
		userId = (int) session.getAttribute("userId"); 
		
		//Parameters sanitizing
		stringfolderId = StringEscapeUtils.escapeJava(request.getParameter("folderId"));
		stringdocumentId = StringEscapeUtils.escapeJava(request.getParameter("documentId"));

		if((stringfolderId == null || stringdocumentId == null) || (stringfolderId.equals("") || stringdocumentId.equals(""))) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing folder or document values.");
			return;
		}
		
		try {
			folderId = Integer.parseInt(stringfolderId);
			documentId = Integer.parseInt(stringdocumentId);
		}
		catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect parameter values");
			return;
		}
		
		try {
			DocumentDAO documentDao = new DocumentDAO(connection);
			FolderDAO folderDao = new FolderDAO(connection);
			folderDao.isFolderAccessible(userId, folderId);
			Document document = documentDao.getDocumentById(userId, documentId);
			if(document.getFolderLocation() == folderId) {
				String path = getServletContext().getContextPath() + "/Home?homeMessage=" + "Document is already in this folder.";
				response.sendRedirect(path);
				return;
			}
			documentDao.moveDocument(document, folderId);
			String path = getServletContext().getContextPath() + "/ShowFolderContent?id=" + folderId;
			response.sendRedirect(path);
			return;
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception during the transfer.");
			return;
		}
		catch(NoAccessException e) {
			String path = getServletContext().getContextPath() + "/Home?homeMessage=" + e.getMessage();
			response.sendRedirect(path);
			return;
		}
		catch(SameFileNameException e) {
			String path = getServletContext().getContextPath() + "/Home?homeMessage=" + e.getMessage();
			response.sendRedirect(path);
			return;
		}
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

}
