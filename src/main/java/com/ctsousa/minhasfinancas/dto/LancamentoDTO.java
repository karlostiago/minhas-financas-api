package com.ctsousa.minhasfinancas.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LancamentoDTO {
	
	private Long id;
	private String descricao;
	private Integer mes;
	private Integer ano;
	private String valor;
	private Long usuario;
	private String tipo;
	private String status;
}
