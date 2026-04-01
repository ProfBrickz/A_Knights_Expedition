package edu.ycp.cs320.TBAG.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serial;

public class IndexServlet extends HttpServlet {
	@Serial
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {

		System.out.println("Index Servlet: doGet");

		req.getRequestDispatcher("/_view/index.jsp").forward(req, resp);
	}
}
