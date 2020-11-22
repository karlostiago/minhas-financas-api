package com.ctsousa.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.List;

import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.enumeration.StatusLancamento;

public interface LancamentoService {

	public Lancamento salvar(Lancamento lancamento);
	
	public Lancamento atualizar(Lancamento lancamento);
	
	public void deletar(Lancamento lancamento);
	
	public List<Lancamento> buscar(Lancamento filtro);
	
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	public void validar(Lancamento lancamento);
	
	public Lancamento porId(Long id);
	
	public BigDecimal saldoPorUsuario(Long id);
}
