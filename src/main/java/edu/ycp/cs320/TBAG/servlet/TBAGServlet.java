package edu.ycp.cs320.TBAG.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.model.Player;

public class TBAGServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("TBAG Servlet: doGet");	
		
		// call JSP to generate empty form
		req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		System.out.println("TBAG Servlet: doPost");
		
		// create Player model
		Player player = new Player();
		
		// create GameEngine controller - controller does not persist between requests
		// must recreate it each time a Post comes in
		GameEngine engine = new GameEngine();
		
		// assign model reference to controller so that controller can access model
		engine.setPlayer(player);

		// Get running dialog text
		String dialog = req.getParameter("dialog");

		// get direction command from jsp
		String command = req.getParameter("command");
		// Append user's command
		dialog += command + "\n";
		// Attempt to move player
		if (!engine.movePlayer(command)) {
			dialog += "Sorry, command not recognized.\n";
		}

		// the JSP will display updated dialog
		req.setAttribute("dialog", dialog);
		
		// now call the JSP to render the new page
		req.getRequestDispatcher("/_view/tbag.jsp").forward(req, resp);
	}

	// gets an Integer from the Posted form data, for the given attribute name
	private int getInteger(HttpServletRequest req, String name) {
		return Integer.parseInt(req.getParameter(name));
	}
}
