package com.ctsousa.minhasfinancas.exception;

public class LancamentoException extends RuntimeException {
	
	private static final long serialVersionUID = -9147884636985056944L;

	public LancamentoException(String mensagem) {
		super(mensagem);
	}
}
