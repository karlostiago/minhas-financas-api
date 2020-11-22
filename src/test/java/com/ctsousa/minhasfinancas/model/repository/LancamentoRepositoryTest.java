package com.ctsousa.minhasfinancas.model.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.enumeration.StatusLancamento;
import com.ctsousa.minhasfinancas.model.enumeration.TipoLancamento;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@ActiveProfiles("test")
public class LancamentoRepositoryTest {
	
	@Autowired
	LancamentoRepository lancamentoRepository;
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveSalvarUmLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento = lancamentoRepository.save(lancamento);
		Assertions.assertThat(lancamento.getId()).isNotNull();
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		Lancamento lancamento = getLancamento();
		lancamento = entityManager.persist(lancamento);
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		lancamentoRepository.delete(lancamento);
		
		Assertions.assertThat(entityManager.find(Lancamento.class, lancamento.getId())).isNull();
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		StatusLancamento status = StatusLancamento.EFETIVADO;
		
		Lancamento lancamento = getLancamento();
		lancamento = entityManager.persist(lancamento);
		
		lancamento = entityManager.find(Lancamento.class, lancamento.getId());
		lancamento.setStatus(status);
		lancamentoRepository.save(lancamento);
		
		Lancamento lancamentoAtualizado = entityManager.find(Lancamento.class, lancamento.getId());
		Assertions.assertThat(lancamentoAtualizado.getStatus().equals(status)).isTrue();
	}
	
	@Test
	public void deveBuscarUmLancamentoPorID() {
		Lancamento lancamento = getLancamento();
		lancamento = entityManager.persist(lancamento);
		
		Optional<Lancamento> lancamentoLocalizado = lancamentoRepository.findById(lancamento.getId());
		Assertions.assertThat(lancamentoLocalizado.isPresent()).isTrue();
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
		
		Usuario usuario = new Usuario();
		usuario.setEmail("usuario@email.com");
		usuario.setNome("usuario");
		usuario.setSenha("senha");
		
		usuario = usuarioRepository.save(usuario);
		lancamento.setUsuario(usuario);
		
		return lancamento;
	}
}
