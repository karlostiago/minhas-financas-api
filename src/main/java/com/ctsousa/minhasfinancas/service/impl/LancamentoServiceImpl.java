package com.ctsousa.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctsousa.minhasfinancas.exception.LancamentoException;
import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.enumeration.StatusLancamento;
import com.ctsousa.minhasfinancas.model.enumeration.TipoLancamento;
import com.ctsousa.minhasfinancas.model.repository.LancamentoRepository;
import com.ctsousa.minhasfinancas.service.LancamentoService;

@Service
public class LancamentoServiceImpl implements LancamentoService {
	
	private LancamentoRepository lancamentoRepository;
	
	public LancamentoServiceImpl(LancamentoRepository lancamentoRepository) {
		this.lancamentoRepository = lancamentoRepository;
	}
	
	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setDataCriacao(LocalDate.now());
		
		if(lancamento.getId() == null) {
			lancamento.setStatus(StatusLancamento.PENDENTE);
		}
		
		return lancamentoRepository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		return salvar(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		lancamentoRepository.delete(lancamento);
	}

	@Override
	public List<Lancamento> buscar(Lancamento filtro) {
		Example<Lancamento> example = lancamentoRepository.buscarPor(filtro);
		return lancamentoRepository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().isEmpty()) {
			throw new LancamentoException("Informe uma descrição válida.");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new LancamentoException("Informe um mês válido.");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new LancamentoException("Informe um ano válido.");
		}
		
		if(lancamento.getUsuario() == null || lancamento.getUsuario().getId() == null) {
			throw new LancamentoException("Informe um usuário.");
		}
		
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new LancamentoException("Informe um valor válido.");
		}
		
		if(lancamento.getTipo() == null) {
			throw new LancamentoException("Informe um tipo de lançamento.");
		}
	}

	@Override
	public Lancamento porId(Long id) {
		Lancamento lancamento = lancamentoRepository.porID(id);
		
		if(lancamento == null) {
			throw new LancamentoException("Nenhum lançamento encontrado com o id '" + id + "', informado");
		}
		
		return lancamento;
	}

	@Override
	public BigDecimal saldoPorUsuario(Long id) {
		
		if(id == null) {
			return BigDecimal.ZERO;
		}
		
		Usuario usuario = new Usuario(id);
		
		BigDecimal receitas = lancamentoRepository.saldoPorTipoLancamentoUsuario(usuario, TipoLancamento.RECEITA);
		BigDecimal despesas = lancamentoRepository.saldoPorTipoLancamentoUsuario(usuario, TipoLancamento.DESPESA);
		
		return receitas.subtract(despesas);
	}

}
