package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.utilities.ConnectionHandler;


@WebServlet("/GetFolders")
@MultipartConfig
public class GetFolders extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	
    public GetFolders() {
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
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Session authentication
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			sendMessage(response, "User is not authenticated.");
			return;
		}
		int userId = (int) request.getSession().getAttribute("userId");
    
  		FolderDAO folderDao = new FolderDAO(connection);
  		Folder rootFolder = null;
  		try {
			rootFolder = folderDao.getRootFolder(userId);
			if(rootFolder == null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				sendMessage(response, "There was an exception fetching the root folder.");
				return;
			}
			List<Folder> rootFolders = new ArrayList<>();
			rootFolders.add(rootFolder);
			//setSubFolders also sets the documents in the folders for each!
			folderDao.setSubFolders(userId, rootFolders);
			
			//Serializing of the folders
			Gson gson = new Gson();
			String json = gson.toJson(rootFolder);
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendMessage(response, "There was an exception getting the folders and subfolders.");
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
