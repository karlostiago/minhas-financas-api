package com.ctsousa.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.ctsousa.minhasfinancas.exception.AutenticarException;
import com.ctsousa.minhasfinancas.exception.EmailException;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.repository.UsuarioRepository;
import com.ctsousa.minhasfinancas.service.impl.UsuarioServiceImpl;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl usuarioService;
	
	@MockBean
	UsuarioRepository usuarioRepository;
	
	@Test(expected = EmailException.class)
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
		
		String email = "usuario@email.com";
		Usuario usuario = new Usuario();
		usuario.setEmail(email);
		
		Mockito
			.doThrow(EmailException.class)
			.when(usuarioService)
			.validarEmail(email);
		
		usuarioService.salvar(usuario);
		
		Mockito
			.verify(usuarioRepository, Mockito.never())
			.save(usuario);
	}
	
	@Test(expected = Test.None.class)
	public void deveSalvarUsuario() {
		Mockito
			.doNothing()
			.when(usuarioService)
			.validarEmail(Mockito.anyString());
		
		String email = "usuario@email.com";
		String senha = "123456";
		
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setNome("usuario");
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito
			.when(usuarioRepository.save(Mockito.any(Usuario.class)))
			.thenReturn(usuario);
		
		Usuario usuarioSalvo = usuarioService.salvar(usuario);
		
		Assertions
			.assertThat(usuarioSalvo)
			.isNotNull();
		
		Assertions
			.assertThat(usuario.getId())
			.isEqualTo(1L);
		
		Assertions
			.assertThat(usuario.getNome())
			.isEqualTo("usuario");
		
		Assertions
			.assertThat(usuario.getSenha())
			.isEqualTo("123456");
		
		Assertions
			.assertThat(usuario.getEmail())
			.isEqualTo("usuario@email.com");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaForInvalida() {
		String email = "usuario@email.com";
		String senha = "123456";
		
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito
			.when(usuarioRepository.findByEmail(Mockito.anyString()))
			.thenReturn(Optional.of(usuario));
		
		Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("usuario@email.com", "12345"));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(AutenticarException.class)
			.hasMessage("Senha inválida");
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioComEmailInformado() {
		Mockito.when(usuarioRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		Throwable exception = Assertions.catchThrowable(() -> usuarioService.autenticar("usuario@email.com", "123456"));
		
		Assertions
			.assertThat(exception)
			.isInstanceOf(AutenticarException.class)
			.hasMessage("Usuário não encontrado, para o email informado.");
	}
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUsuarioComSucesso() {
		String email = "usuario@email.com";
		String senha = "123456";
		
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail(email);
		usuario.setSenha(senha);
		
		Mockito
			.when( usuarioRepository.findByEmail(email))
			.thenReturn(Optional.of(usuario));
		
		Usuario result = usuarioService.autenticar(email, senha);
		
		Assertions
			.assertThat(result)
			.isNotNull();
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		Mockito
			.when(usuarioRepository.existsByEmail(Mockito.anyString()))
			.thenReturn(false);
		
		usuarioService.validarEmail("email@email.com");
	}
	
	@Test(expected = EmailException.class)
	public void deveLancarErroAoValidarEmailQuantoExistirEmailCadastrado() {
		Mockito
			.when(usuarioRepository.existsByEmail(Mockito.anyString()))
			.thenReturn(true);
		
		usuarioService.validarEmail("usuario@email.com");
	}
}
