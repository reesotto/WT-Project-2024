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

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoPathException;
import it.polimi.tiw.exceptions.SameFileNameException;
import it.polimi.tiw.utilities.ConnectionHandler;
import it.polimi.tiw.utilities.PatternChecker;

public class CreateFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
       
    public CreateFolder() {
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
		name = StringEscapeUtils.escapeJava(request.getParameter("name"));
		folderpath = StringEscapeUtils.escapeJava(request.getParameter("folderpath"));
		
		if(name == null || name.equals("")) {
			sendMessage(response, "Folder name can't be left empty.");
			return;
		}
		if(folderpath == null || folderpath.equals("")) {
			sendMessage(response, "Folder can be created only inside root folder.");
			return;
		}
		
		//FolderDAO and folder pre-creation
		FolderDAO folderDao = new FolderDAO(connection);
		Date currentDate = new Date(System.currentTimeMillis());

		//Folder creation in a path	
		if(!PatternChecker.isAlphaNumericWithSpace(name) || !PatternChecker.isFolderPathValid(folderpath)) {
			sendMessage(response, "Folder name can't have special characters and folderpath must follow path/to/folder pattern.");
			return;
		}
		String[] pathParentFolders = folderpath.split("/");
		Folder folder = new Folder();
		folder.setName(name);
		folder.setOwner(userId);
		folder.setDate(currentDate);
		try {
			folderDao.checkFolderPath(folder, pathParentFolders);
			folderDao.createFolder(folder);
			String path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
			return;
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception during the creation.");
			e.printStackTrace();
			return;
		}
		catch(SameFileNameException | NoPathException e) {
			sendMessage(response, e.getMessage());
			return;
		}

	}

	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}
	
	private void sendMessage(HttpServletResponse response, String message) throws IOException {
		String path = getServletContext().getContextPath() + "/FolderManager?createFolderOutcome=" + message;
		response.sendRedirect(path);
		return;
	}
}
