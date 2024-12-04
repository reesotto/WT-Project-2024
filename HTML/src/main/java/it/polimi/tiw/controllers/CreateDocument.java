package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.exceptions.NoPathException;
import it.polimi.tiw.exceptions.SameFileNameException;
import it.polimi.tiw.utilities.ConnectionHandler;
import it.polimi.tiw.utilities.PatternChecker;

public class CreateDocument extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    public CreateDocument() {
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
		HttpSession session = request.getSession();
		String indexpath = getServletContext().getContextPath() + "/index.html";
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.sendRedirect(indexpath);
			return;
		}
		//userId needed for the owner attribute of folder
		userId = (int) session.getAttribute("userId"); 

		//Name and folderPath check
		String name = null;
		String folderpath = null;
		String summary = null;
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		folderpath = StringEscapeUtils.escapeJava(request.getParameter("folderpath"));
		summary = StringEscapeUtils.escapeJava(request.getParameter("summary"));

		if(name == null || name.equals("")) {
			sendMessage(response, "Document name can't be left empty.");
			return;
		}
		if(folderpath == null || folderpath.equals("")) {
			sendMessage(response, "Document can't be created in no folder.");
			return;
		}
		if(summary == null || summary.equals("")) {
			sendMessage(response, "Document summary can't be left empty.");
			return;
		}
		if(summary.length() > 255) {
			sendMessage(response, "Document summary is too long.");
			return;
		}
		
		if(!PatternChecker.isFolderPathValid(folderpath) || !PatternChecker.isNameTypeValid(name)) {
			sendMessage(response, "Document name and type can't have special characters, type can't be longer than 4 characters, folderpath must follow path/to/folder pattern.");
			return;
		}
		//DocumentDAO and document creation
		String[] documentPathFolders = folderpath.split("/");
		String[] nameFormat = name.split("\\.");
		DocumentDAO documentDao = new DocumentDAO(connection);
		Date currentDate = new Date(System.currentTimeMillis());
		
		Document document = new Document();
		document.setName(nameFormat[0]);
		document.setOwner(userId);
		document.setDate(currentDate);
		document.setType(nameFormat[1]);
		document.setSummary(summary);
		try {
			//Check if the folder path is right
			documentDao.checkFolderPath(document, documentPathFolders);
			documentDao.createDocument(document);
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception during the document creation.");
			return;
		}
		catch(SameFileNameException | NoPathException e) {
			sendMessage(response, e.getMessage());
			return;
		}	
	}
	
	private void sendMessage(HttpServletResponse response, String message) throws IOException{
		String path = getServletContext().getContextPath() + "/DocumentManager?createDocumentOutcome=" + message;
		response.sendRedirect(path);
		return;
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

}
