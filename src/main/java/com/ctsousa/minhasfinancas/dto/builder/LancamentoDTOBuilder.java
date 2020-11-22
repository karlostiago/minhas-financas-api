package com.ctsousa.minhasfinancas.dto.builder;

import java.math.BigDecimal;

import com.ctsousa.minhasfinancas.dto.LancamentoDTO;

public class LancamentoDTOBuilder {
	
	private LancamentoDTO instance;
	
	public LancamentoDTOBuilder() {
		instance = new LancamentoDTO();
	}
	
	public LancamentoDTOBuilder comID(Long id) {
		instance.setId(id);
		return this;
	}
	
	public LancamentoDTOBuilder comAno(Integer ano) {
		instance.setAno(ano);
		return this;
	}
	
	public LancamentoDTOBuilder comMes(Integer mes) {
		instance.setMes(mes);
		return this;
	}
	
	public LancamentoDTOBuilder comDescricao(String descricao) {
		instance.setDescricao(descricao);
		return this;
	}
	
	public LancamentoDTOBuilder comValor(BigDecimal valor) {
		instance.setValor(valor);
		return this;
	}
	
	public LancamentoDTOBuilder comUsuario(Long usuario) {
		instance.setUsuario(usuario);
		return this;
	}
	
	public LancamentoDTOBuilder comTipoLancamento(String tipo) {
		instance.setTipo(tipo);
		return this;
	}
	
	public LancamentoDTOBuilder comStatus(String status) {
		instance.setStatus(status);
		return this;
	}
	
	public LancamentoDTO builde() {
		return instance;
	}
}
