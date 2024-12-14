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

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.SameFileNameException;
import it.polimi.tiw.utilities.ConnectionHandler;
import it.polimi.tiw.utilities.PatternChecker;

@WebServlet("/CreateFolder")
@MultipartConfig
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
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendMessage(response, "User is not authenticated.");
			return;
		}
		int userId = (int) request.getSession().getAttribute("userId");
		String parentFolder = null;
		String folderName = null;
		
		parentFolder = StringEscapeUtils.escapeJava(request.getParameter("parentfolder"));
		folderName = StringEscapeUtils.escapeJava(request.getParameter("foldername"));
		
		if((parentFolder == null || folderName == null) || (parentFolder.equals("") || folderName.equals(""))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Folder name or parent folder can't be left empty");
			return;
		}
		
		if(!PatternChecker.isAlphaNumericWithSpace(folderName)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Folder name can't have special characters.");
			return;
		}
		
		Integer parentFolderId;
		try {
			parentFolderId = Integer.parseInt(parentFolder);
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Parent folder is invalid.");
			return;
		}
		
		FolderDAO folderDao = new FolderDAO(connection);
		Date currentDate = new Date(System.currentTimeMillis());
		
		Folder folder = new Folder();
		folder.setName(folderName);
		folder.setOwner(userId);
		folder.setDate(currentDate);
		folder.setParentFolder(parentFolderId);
		
		try {
			folderDao.isFolderAccessible(userId, parentFolderId);
			folderDao.checkParentFolderChildren(folder);
			folderDao.createFolder(folder);
			
			Folder createdFolder = folderDao.getFolderByParameters(folderName, userId, parentFolderId);
			Gson gson = new Gson();
			String json = gson.toJson(createdFolder);
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			return;
		}
		catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendMessage(response, "There was an exception during the creation.");
			return;
		}
		catch(NoAccessException e) {
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
