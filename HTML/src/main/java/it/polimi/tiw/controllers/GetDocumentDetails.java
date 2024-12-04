package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import it.polimi.tiw.dao.DocumentDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.utilities.ConnectionHandler;

public class GetDocumentDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection;
	private TemplateEngine templateEngine;
       
    public GetDocumentDetails() {
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
		String documentDetailspath = "/WEB-INF/DocumentDetails.html";
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
			UserDAO userDao = new UserDAO(connection);
			Document document = documentDao.getDocumentById(userId, documentId);
			//To shortcut the queries I just used the userId in the session instead of getting it from the document...
			String username = userDao.getUsernameById(userId);
			
			//Rendering page
			ServletContext servletContext = getServletContext();
			WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			
			ctx.setVariable("document", document);
			ctx.setVariable("owner", username);
			templateEngine.process(documentDetailspath, ctx, response.getWriter());
			return;
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
