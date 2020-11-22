package com.ctsousa.minhasfinancas.model.repository.query;

import java.math.BigDecimal;

import org.springframework.data.domain.Example;

import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.enumeration.TipoLancamento;

public interface LancamentoRepositoryQuery {
	
	public Lancamento porID(Long id);
	
	public Example<Lancamento> buscarPor(Lancamento lancamentoFiltro);
	
	public BigDecimal saldoPorTipoLancamentoUsuario(Usuario usuario, TipoLancamento tipo);
}
