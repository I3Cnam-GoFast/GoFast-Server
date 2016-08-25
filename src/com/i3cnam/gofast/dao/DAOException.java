package com.i3cnam.gofast.dao;

public class DAOException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 4526520144651464398L;

	/*
     * Constructeurs
     */
    public DAOException( String message ) {
        super( message );
    }

    public DAOException( String message, Throwable cause ) {
        super( message, cause );
    }

    public DAOException( Throwable cause ) {
        super( cause );
    	cause.printStackTrace();
    }
}