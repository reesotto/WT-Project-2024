package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utilities.ConnectionHandler;

public class ShowHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    public ShowHome() {
        super();
    }
    
    public void init() throws ServletException{
    	try {
    		connection = ConnectionHandler.getConnection(getServletContext());
    	}
    	catch(SQLException e) {
    		throw new ServletException("There was a SQLException connecting to the database.");
    	}
    	ServletContext servletContext = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
    	this.templateEngine.setTemplateResolver(templateResolver);
    	templateResolver.setSuffix(".html");
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Session authentication
		int userId;
		HttpSession session = request.getSession();
		String indexpath = getServletContext().getContextPath() + "/index.html";
		String homepath = "/WEB-INF/Home.html";
		if (session.isNew() || session.getAttribute("userId") == null) {
		    response.sendRedirect(indexpath);
		    return;
		}
		userId = (int) session.getAttribute("userId"); 
		
		//Displaying the current user logged in
		UserDAO userDao = new UserDAO(connection);
		String username;
		try {
			username = userDao.getUsernameById(userId);
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		//Folder Tree logic
		FolderDAO folderDao = new FolderDAO(connection);
		Folder rootFolder = null;
		try {
			rootFolder = folderDao.getRootFolder(userId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception fetching the root folder.");
				return;
			}
			List<Folder> rootFolders = new ArrayList<>();
			rootFolders.add(rootFolder);
			folderDao.setSubFolders(userId, rootFolders);
		
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception getting the folders and subfolders.");
			e.printStackTrace();
			return;
		}
		
		//Rendering page
		ServletContext servletContext = getServletContext();
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		String homeMessage = request.getParameter("homeMessage");
		
		if(homeMessage != null && !homeMessage.equals("")) {
			ctx.setVariable("homeMessage", homeMessage);
		}		
		ctx.setVariable("user", username);
		ctx.setVariable("rootFolders", rootFolder);
		templateEngine.process(homepath, ctx, response.getWriter());
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

}
