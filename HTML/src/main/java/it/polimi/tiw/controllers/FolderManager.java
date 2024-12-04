package it.polimi.tiw.controllers;

import java.io.IOException;

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

public class FolderManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

    public FolderManager() {
        super();
    }
    
    public void init() throws ServletException{
    	ServletContext servletContext = getServletContext();
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
    	this.templateEngine.setTemplateResolver(templateResolver);
    	templateResolver.setSuffix(".html");
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Session authentication
		HttpSession session = request.getSession();
		String indexpath = getServletContext().getContextPath() + "/index.html";
		String folderManagerpath = "/WEB-INF/FolderManager.html";
		if (session.isNew() || session.getAttribute("userId") == null) {
		    response.sendRedirect(indexpath);
		    return;
		}
		
		//Rendering page
		ServletContext servletContext = getServletContext();
		String createFolderOutcome = null;
		createFolderOutcome = request.getParameter("createFolderOutcome");

		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if(createFolderOutcome != null && !createFolderOutcome.equals("")) {
			ctx.setVariable("createFolderOutcome", createFolderOutcome);
		}		
		templateEngine.process(folderManagerpath, ctx, response.getWriter());
	}

}
