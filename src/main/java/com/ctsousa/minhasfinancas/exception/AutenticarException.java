package com.ctsousa.minhasfinancas.exception;

public class AutenticarException extends RuntimeException {
	
	private static final long serialVersionUID = 4799698267382168189L;

	public AutenticarException(String mensagem) {
		super(mensagem);
	}
}
