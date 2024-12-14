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

import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.utilities.ConnectionHandler;

@WebServlet("/DeleteFolder")
@MultipartConfig
public class DeleteFolder extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;   

    public DeleteFolder() {
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
		
		String folderId = null;
		Integer id = null;
		
		folderId = StringEscapeUtils.escapeJava(request.getParameter("fileId"));

		if((folderId == null) || (folderId.equals(""))) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Folder ID can't be left empty.");
			return;
		}
		try {
			id = Integer.parseInt(folderId);
		}
		catch(NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			sendMessage(response, "Folder ID is invalid.");
			return;
		}
		
		FolderDAO folderDao = new FolderDAO(connection);
		try {
			folderDao.isFolderAccessible(userId, id);
			if(folderDao.isFolderRoot(userId, id)) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				sendMessage(response, "You can't delete the root folder!");
				return;
			}
			
			connection.setAutoCommit(false);
			folderDao.deleteFolder(id);
			
			connection.commit();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("text/plain");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().println(id);
			return;
		} 
		catch (SQLException e) {
			try {
				connection.rollback();
			}
			catch(SQLException e1) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				sendMessage(response, "Rollback failed, DB compromised");
			}
			
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			sendMessage(response, "There was an exception during the deletion.");
			return;
		}
		catch (NoAccessException e) {
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			sendMessage(response, e.getMessage());
			return;
		}
		finally {
			try {
	            connection.setAutoCommit(true);
	        } catch (SQLException e) {
	            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	            sendMessage(response, "Error resetting auto-commit: " + e.getMessage());
	        }
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
