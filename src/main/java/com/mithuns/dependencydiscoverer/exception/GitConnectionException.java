package com.mithuns.dependencydiscoverer.exception;

public class GitConnectionException extends Exception {

	/**
	 * Default serial version ID 
	 */
	private static final long serialVersionUID = 1L;

	public GitConnectionException(final String message)
    {
        super(message);
    }
}
