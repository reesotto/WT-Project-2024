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

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.FolderDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.utilities.ConnectionHandler;

//Servlet used for printing the folders in which the document can be moved to
public class AvailableFolders extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    public AvailableFolders() {
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
		String availableFolderspath = "/WEB-INF/AvailableFolders.html";
		if (session.isNew() || session.getAttribute("userId") == null) {
			response.sendRedirect(indexpath);
			return;
		}
		userId = (int) session.getAttribute("userId"); 
		
		Integer documentId = null;
		try {
			documentId = Integer.parseInt(request.getParameter("id"));
		}
		catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect parameter values.");
			return;
		}
		
		try {
			DocumentDAO documentDao = new DocumentDAO(connection);
			Document document = documentDao.getDocumentById(userId, documentId);
			
			//Folder tree logic
			FolderDAO folderDao = new FolderDAO(connection);
			List<Folder> rootFolders = new ArrayList<>();
			Folder rootFolder = folderDao.getRootFolder(userId);
			if(rootFolder == null) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Root folders error.");
				return;
			}
			rootFolders.add(rootFolder);
			if(!rootFolders.isEmpty()) {
				folderDao.setSubFolders(userId, rootFolders);
			}
			
			//Rendering page
			ServletContext servletContext = getServletContext();
			WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			ctx.setVariable("rootFolders", rootFolders);
			ctx.setVariable("document", document);
			templateEngine.process(availableFolderspath, ctx, response.getWriter());
			
		}
		catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "There was an exception during the query.");
			return;
		}
		catch(NoAccessException e) {
			String path = getServletContext().getContextPath() + "/Home?homeMessage=" + e.getMessage();
			response.sendRedirect(path);
			return;
		}
	}
	
	public void destroy() {
		ConnectionHandler.closeConnection(connection);
	}

}
