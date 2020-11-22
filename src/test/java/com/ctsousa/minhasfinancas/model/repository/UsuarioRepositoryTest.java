package com.ctsousa.minhasfinancas.model.repository;

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

import com.ctsousa.minhasfinancas.model.Usuario;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase( replace = Replace.NONE )
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveValidarSeExisteUmEmailCadastrado() {
		entityManager.persist(getUsuario());
		boolean exist = usuarioRepository.existsByEmail("usuario@email.com");
		Assertions.assertThat(exist).isTrue();
	}
	
	@Test
	public void deveRetonarFalsoQuandoNaoHouverUsuarioCadastradoComEmail() {
		boolean exist = usuarioRepository.existsByEmail("usuario@email.com");
		Assertions.assertThat(exist).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		Usuario usuario = usuarioRepository.save(getUsuario());
		Assertions.assertThat(usuario.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		entityManager.persist(getUsuario());
		Optional<Usuario> usuario = usuarioRepository.findByEmail("usuario@email.com");
		Assertions.assertThat(usuario.isPresent()).isTrue();
	}
	
	@Test
	public void naoDeveRetornarUmUsuarioPorEmailQuandoNaoExistirNaBase() {
		Optional<Usuario> usuario = usuarioRepository.findByEmail("usuario@email.com");
		Assertions.assertThat(usuario.isPresent()).isFalse();
	}
	
	private Usuario getUsuario() {
		Usuario usuario = new Usuario();
		usuario.setNome("usuario");
		usuario.setEmail("usuario@email.com");
		usuario.setSenha("123456");
		return usuario;
	}
}
