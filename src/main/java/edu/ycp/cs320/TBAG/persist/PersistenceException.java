package edu.ycp.cs320.TBAG.persist;

///  From lab 7
public class PersistenceException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PersistenceException(String msg) {
		super(msg);
	}

	public PersistenceException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
