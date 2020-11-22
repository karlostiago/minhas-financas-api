package com.ctsousa.minhasfinancas.exception;

public class UsuarioException extends RuntimeException {
	
	private static final long serialVersionUID = -8354058835706111686L;

	public UsuarioException(String mensagem) {
		super(mensagem);
	}
}
