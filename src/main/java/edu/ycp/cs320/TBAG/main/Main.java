package edu.ycp.cs320.TBAG.main;

import java.io.File;

import org.eclipse.jetty.server.Server;

public class Main {
	final static Integer PORT = 3000;

	public static void main(String[] args) throws Exception {
		String webappCodeBase = "./war";
		File warFile = new File(webappCodeBase);
		Launcher launcher = new Launcher();

		// get a server for port
		System.out.println("CREATING: web server on port " + PORT);
		Server server = launcher.launch(true, PORT, warFile.getAbsolutePath(), "/");

		// Start things up!
		System.out.println("STARTING: web server on port " + PORT);
		server.start();

		// dump the console output - this will produce a lot of red text - no worries,
		// that is normal
		server.dumpStdErr();

		// Inform user that server is running
		System.out.println("RUNNING: web server on port " + PORT);

		// The use of server.join() the will make the current thread join and
		// wait until the server is done executing.
		// See http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
		server.join();
	}
}
