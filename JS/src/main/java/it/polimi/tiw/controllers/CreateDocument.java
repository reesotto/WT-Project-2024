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
import javax.servlet.http.HttpSession;

import org.apache.commons.text.StringEscapeUtils;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.SameFileNameException;
import it.polimi.tiw.utilities.ConnectionHandler;
import it.polimi.tiw.utilities.PatternChecker;

@WebServlet("/CreateDocument")
@MultipartConfig
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
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendMessage(response, "User is not authenticated.");
			return;
		}
		int userId = (int) request.getSession().getAttribute("userId");
		
		String documentName = null;
		String summary = null;
		//Convert it to integer as it's the folder ID in which the document is created
		String documentFolder = null;
		Integer documentFolderId;
		
		documentName = StringEscapeUtils.escapeJava(request.getParameter("documentname"));
		summary = StringEscapeUtils.escapeJava(request.getParameter("summary"));
		documentFolder = StringEscapeUtils.escapeJava(request.getParameter("documentfolder"));
		
		if((documentName == null || summary == null || documentFolder == null) || 
				(documentName.equals("") || summary.equals("") || documentFolder.equals(""))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document name, summary and folder location can't be left empty");
			return;
		}
		if(summary.length() > 255) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document summary is too long.");
			return;
		}
		if(!PatternChecker.isNameTypeValid(documentName)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document name and type can't have special characters, type can't be longer than 4 characters");
			return;
		}
		try {
			documentFolderId = Integer.parseInt(documentFolder);
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Document folder is invalid.");
			return;
		}
		
		
		String[] nameFormat = documentName.split("\\.");
		FolderDAO folderDao = new FolderDAO(connection);
		DocumentDAO documentDao = new DocumentDAO(connection);
		
		Date currentDate = new Date(System.currentTimeMillis());
		Document document = new Document();
		document.setName(nameFormat[0]);
		document.setOwner(userId);
		document.setDate(currentDate);
		document.setType(nameFormat[1]);
		document.setSummary(summary);
		document.setFolderLocation(documentFolderId);
		
		try {
			folderDao.isFolderAccessible(userId, documentFolderId);
			documentDao.checkDocumentFolderChildren(document);
			documentDao.createDocument(document);
			
			Document createdDocument = documentDao.getDocumentByParameters(document.getName(), document.getType(), userId, documentFolderId);
			Gson gson = new Gson();
			String json = gson.toJson(createdDocument);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
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
		catch(SameFileNameException e) {
			response.setStatus(HttpServletResponse.SC_CONFLICT);
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
