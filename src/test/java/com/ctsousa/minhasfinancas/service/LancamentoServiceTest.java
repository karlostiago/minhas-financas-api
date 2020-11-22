package com.ctsousa.minhasfinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ctsousa.minhasfinancas.exception.LancamentoException;
import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.enumeration.StatusLancamento;
import com.ctsousa.minhasfinancas.model.enumeration.TipoLancamento;
import com.ctsousa.minhasfinancas.model.repository.LancamentoRepository;
import com.ctsousa.minhasfinancas.model.repository.UsuarioRepository;
import com.ctsousa.minhasfinancas.service.impl.LancamentoServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	@SpyBean
	LancamentoServiceImpl lancamentoService;
	
	@MockBean
	LancamentoRepository lancamentoRepository;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = getLancamento();
		
		Mockito
			.doNothing()
			.when(lancamentoService)
			.validar(lancamento);
		
		lancamento.setId(1L);
		
		Mockito
			.when(lancamentoService.salvar(lancamento))
			.thenReturn(lancamento);
		
		Lancamento resposta = lancamentoService.salvar(lancamento);
		
		Assertions.assertThat(resposta.getId()).isNotNull();
		Assertions.assertThat(resposta.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamento() {
		Lancamento lancamento = getLancamento();
		
		Mockito
			.doThrow(LancamentoException.class)
			.when(lancamentoService).validar(lancamento);
		
		
		Assertions
			.catchThrowableOfType(() -> lancamentoService.salvar(lancamento), LancamentoException.class);
		
		Mockito
			.verify(lancamentoRepository, Mockito.never())
			.save(lancamento);
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);

		Mockito
			.doNothing()
			.when(lancamentoService)
			.validar(lancamento);		
		
		Mockito
			.when(lancamentoService.salvar(lancamento))
			.thenReturn(lancamento);
		
		lancamentoService.atualizar(lancamento);
		
		Mockito
			.verify(lancamentoRepository, Mockito.timeout(1))
			.save(lancamento);
	}
	
	@Test
	public void naoDeveAtualizarUmLancamentoQuandoLancamentoNaoTiverUmID() {
		Lancamento lancamento = getLancamento();
		
		Mockito
			.doThrow(LancamentoException.class)
			.when(lancamentoService).validar(lancamento);
		
		Assertions
			.catchThrowableOfType(() -> lancamentoService.atualizar(lancamento), NullPointerException.class);
		
		Mockito
			.verify(lancamentoRepository, Mockito.never())
			.save(lancamento);
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		
		lancamentoService.deletar(lancamento);
		
		Mockito
			.verify(lancamentoRepository).delete(lancamento);
	}
	
	@Test
	public void naoDeveLancamentoUmLancamento() {
		Lancamento lancamento = getLancamento();
		
		Assertions
			.catchThrowableOfType(() -> lancamentoService.deletar(lancamento), NullPointerException.class);
		
		Mockito
			.verify(lancamentoRepository, Mockito.never()).delete(lancamento);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void deveFiltrarLancamentos() {
		Lancamento lancamento = getLancamento();
		lancamento.setId(1L);
		
		List<Lancamento> lancamentos = Arrays.asList(lancamento);
		
		Example<Lancamento> example = Example.of(lancamento, 
				ExampleMatcher
					.matching()
					.withIgnoreCase()
					.withStringMatcher(StringMatcher.CONTAINING));
		
		Mockito
			.when(lancamentoRepository.buscarPor(lancamento))
			.thenReturn(example);
		
		Mockito
			.when(lancamentoRepository.findAll(Mockito.any(Example.class)))
			.thenReturn(lancamentos);
		
		List<Lancamento> resposta = lancamentoService.buscar(lancamento);
		
		Assertions
			.assertThat(resposta)
			.isNotEmpty()
			.hasSize(1)
			.contains(lancamento);
	}
	
	@Test
	public void deveAtualizarStatus() {
		Lancamento lancamento = getLancamento();
		
		lancamento.setId(1L);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento status = StatusLancamento.EFETIVADO;
		
		Mockito
			.doReturn(lancamento)
			.when(lancamentoService)
			.atualizar(lancamento);
		
		lancamentoService.atualizarStatus(lancamento, status);
		
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(status);
		
		Mockito
			.verify(lancamentoService)
			.atualizar(lancamento);
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		
		Long id = 1L;
		
		Lancamento lancamento = getLancamento();
		lancamento.setId(id);
		
		Mockito
			.when(lancamentoRepository.porID(id))
			.thenReturn(lancamento);
		
		Lancamento resposta = lancamentoService.porId(id);
		
		Assertions.assertThat(resposta.getId()).isNotNull();
	}
	
	@Test
	public void naoDeveObterUmLancamentoPorIDQuandoLancamentoNaoExistir() {
		
		Long id = 1L;
		
		Lancamento lancamento = getLancamento();
		lancamento.setId(id);
		
		Mockito
			.when(lancamentoRepository.porID(id))
			.thenReturn(null);
		
		Assertions
			.catchThrowableOfType(() -> lancamentoService.porId(id), LancamentoException.class);
	}
	
	@Test
	public void deveValidarUmLancamentoQuandoNaoInformarUmaDescricao() {
		Lancamento lancamento = getLancamento();
		lancamento.setDescricao(null);
		
		Throwable exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe uma descrição válida.");
		
		lancamento.setDescricao("");
		
		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe uma descrição válida.");
			
	}
	
	@Test
	public void deveValidarUmLancamentoQuandoNaoInformarUmMes() {
		Lancamento lancamento = getLancamento();
		lancamento.setMes(null);
		
		Throwable exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um mês válido.");
		
		lancamento.setMes(-1);

		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um mês válido.");
		
		lancamento.setMes(13);

		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um mês válido.");
	}
	
	@Test
	public void deveValidarUmLancamentoQuandoNaoInformarAno() {
		Lancamento lancamento = getLancamento();
		lancamento.setAno(null);
		
		Throwable exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um ano válido.");
		
		lancamento.setAno(20005);

		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um ano válido.");
		
		lancamento.setAno(205);

		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um ano válido.");
	}
	
	@Test
	public void deveValidaUmLancamentoQuandoNaoInformarUmUsuario() {
		Lancamento lancamento = getLancamento();
		lancamento.getUsuario().setId(null);
		
		Throwable exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um usuário.");
		
		lancamento.setUsuario(null);
		
		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um usuário.");
	}
	
	@Test
	public void deveValidaUmLancamentoQuandoNaoUmValor() {
		Lancamento lancamento = getLancamento();
		lancamento.setValor(null); 
		
		Throwable exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(-1020)); 
		
		exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um valor válido.");
	} 
	
	@Test
	public void deveValidarUmLancamentoQuandoNaoInformarUmTipo() {
		Lancamento lancamento = getLancamento();
		lancamento.setTipo(null);
		
		Throwable exception = Assertions.catchThrowable(() -> lancamentoService.validar(lancamento));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(LancamentoException.class)
			.hasMessage("Informe um tipo de lançamento.");
	}
	
	@Test
	public void deveObterSaldoPorUsuario() {
		Mockito
			.when(lancamentoRepository.saldoPorTipoLancamentoUsuario(new Usuario(1L), TipoLancamento.RECEITA))
			.thenReturn(BigDecimal.valueOf(500));
		
		Mockito
			.when(lancamentoRepository.saldoPorTipoLancamentoUsuario(new Usuario(1L), TipoLancamento.DESPESA))
			.thenReturn(BigDecimal.valueOf(100));
		
		BigDecimal valor = lancamentoService.saldoPorUsuario(1L);
		
		Assertions
			.assertThat(valor)
			.isEqualTo(BigDecimal.valueOf(400));
	}
	
	@Test
	public void deveRetornarZeroQuandoInformarNullParaObterSaldoPorUsuario() {
		
		BigDecimal valor = lancamentoService.saldoPorUsuario(null);
		
		Assertions
			.assertThat(valor)
			.isEqualTo(BigDecimal.ZERO);
	}
	
	private Lancamento getLancamento() {
		Lancamento lancamento = new Lancamento();
		lancamento.setAno(2019);
		lancamento.setMes(1);
		lancamento.setDescricao("pagamento de fatura");
		lancamento.setValor(BigDecimal.valueOf(500));
		lancamento.setTipo(TipoLancamento.DESPESA);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		lancamento.setDataCriacao(LocalDate.now());
		
		Usuario usuario = new Usuario(1L);
		usuario.setEmail("usuario@email.com");
		usuario.setNome("usuario");
		usuario.setSenha("senha");
		
		lancamento.setUsuario(usuario);		
		return lancamento;
	}
}
