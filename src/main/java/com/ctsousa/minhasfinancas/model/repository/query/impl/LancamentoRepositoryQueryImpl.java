package com.ctsousa.minhasfinancas.model.repository.query.impl;

import java.math.BigDecimal;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.transaction.annotation.Transactional;

import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.enumeration.StatusLancamento;
import com.ctsousa.minhasfinancas.model.enumeration.TipoLancamento;
import com.ctsousa.minhasfinancas.model.repository.query.LancamentoRepositoryQuery;

public class LancamentoRepositoryQueryImpl implements LancamentoRepositoryQuery {
	
	private static final String ID = "id";
	private static final String USUARIO = "usuario";
	private static final String TIPO_LANCAMENTO = "tipo";
	private static final String STATUS = "status";

	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	@Transactional(readOnly = true)
	public Lancamento porID(Long id) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT lancamento FROM Lancamento lancamento ")
			.append("INNER JOIN FETCH lancamento.usuario ")
			.append("WHERE lancamento.id = :id ");
		
		TypedQuery<Lancamento> query = entityManager.createQuery(sql.toString(), Lancamento.class);
		query.setParameter(ID, id);
		
		try {
			return query.getSingleResult();
		}
		catch(Exception e) {			
			return null;
		}
	}
	
	@Override
	@Transactional(readOnly = true)
	public Example<Lancamento> buscarPor(Lancamento lancamentoFiltro) {
		
		Example<Lancamento> example = Example.of(lancamentoFiltro, 
				ExampleMatcher
					.matching()
					.withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING));
		
		return example;
	}

	@Override
	@Transactional(readOnly = true)
	public BigDecimal saldoPorTipoLancamentoUsuario(Usuario usuario, TipoLancamento tipo) {
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT SUM(lancamento.valor) FROM Lancamento lancamento ")
		   .append("JOIN lancamento.usuario usuario ")
		   .append("WHERE usuario = :usuario AND lancamento.tipo = :tipo AND lancamento.status = :status ")
		   .append("GROUP BY usuario ");
		
		TypedQuery<BigDecimal> query = entityManager.createQuery(sql.toString(), BigDecimal.class);
		
		query.setParameter(USUARIO, usuario)
		     .setParameter(TIPO_LANCAMENTO, tipo)
		     .setParameter(STATUS, StatusLancamento.EFETIVADO);
		
		try {			
			return query.getSingleResult();
		}
		catch(Exception e) {
			return BigDecimal.ZERO;
		}
	}
}
