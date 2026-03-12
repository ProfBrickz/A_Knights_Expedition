package edu.ycp.cs320.TBAG.servlet;

import edu.ycp.cs320.TBAG.controller.GameEngine;
import edu.ycp.cs320.TBAG.model.Player;
import edu.ycp.cs320.TBAG.model.Room;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

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
		// create room models
		HashMap<String, Room> rooms = new HashMap<String, Room>();

		// create GameEngine controller - controller does not persist between requests
		// must recreate it each time a Post comes in
		GameEngine gameEngine = new GameEngine(player, rooms);

		// Get running dialog text
		String dialog = req.getParameter("dialog");

		// get direction command from jsp
		String input = req.getParameter("command");
		// Append user's command
		dialog += input + "\n";

		String command = "";
		ArrayList<String> arguments = new ArrayList<String>();

		String[] parts = input.split(" ");
        if (parts.length>0){
            command = parts[0];
        }
        for (int i = 1; i < parts.length; i++) {
            arguments.add(parts[i]);
        }

		// Run command
		dialog += gameEngine.inputCommand(command, arguments);

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
