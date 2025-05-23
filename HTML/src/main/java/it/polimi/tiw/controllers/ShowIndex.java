package it.polimi.tiw.controllers;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

public class ShowIndex extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    public ShowIndex() {
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
		ServletContext servletContext = getServletContext();
		WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		String indexpath = "/index.html";
		String registerOutcome = null;
		String loginOutcome = null;
		registerOutcome = request.getParameter("registerOutcome");
		loginOutcome = request.getParameter("loginOutcome");
		
		if(registerOutcome != null && !registerOutcome.equals("")) {
			ctx.setVariable("registerOutcome", registerOutcome);
		}
		if(loginOutcome != null && !loginOutcome.equals("")) {
			ctx.setVariable("loginOutcome", loginOutcome);
		}

		templateEngine.process(indexpath, ctx, response.getWriter());
	}
}
