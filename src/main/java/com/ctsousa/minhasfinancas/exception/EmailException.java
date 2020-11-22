package com.ctsousa.minhasfinancas.exception;

public class EmailException extends RuntimeException {
	
	private static final long serialVersionUID = 6598114299905217078L;

	public EmailException(String mensagem) {
		super(mensagem);
	}
}
