package com.ctsousa.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.repository.query.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {	
}
