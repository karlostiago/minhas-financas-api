package com.ctsousa.minhasfinancas.resource;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.ctsousa.minhasfinancas.dto.UsuarioDTO;
import com.ctsousa.minhasfinancas.exception.AutenticarException;
import com.ctsousa.minhasfinancas.exception.EmailException;
import com.ctsousa.minhasfinancas.exception.UsuarioException;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.service.LancamentoService;
import com.ctsousa.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTest {
	
	private static final String URL_API = "/api/usuarios";
	private static final MediaType APPLICATION_JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService usuarioService;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail("usuario@email.com");
		dto.setNome("usuario");
		dto.setSenha("senha");
		
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail(dto.getEmail());
		usuario.setNome(dto.getNome());
		usuario.setSenha(dto.getSenha());
		
		Mockito
			.when(usuarioService.autenticar(dto.getEmail(), dto.getSenha()))
			.thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc
			.perform(request(json, "autenticar"))
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
			
	}
	
	@Test
	public void naoDeveAutenticarUmUsuario() throws Exception {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail("usuario@email.com");
		dto.setNome("usuario");
		dto.setSenha("senha");
		
		Mockito
			.when(usuarioService.autenticar(dto.getEmail(), dto.getSenha()))
			.thenThrow(AutenticarException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc
			.perform(request(json, "autenticar"))
			.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveCriarUmUsuario() throws Exception {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail("usuario@email.com");
		dto.setNome("usuario");
		dto.setSenha("senha");
		
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setEmail(dto.getEmail());
		usuario.setSenha(dto.getSenha());
		usuario.setNome(dto.getNome());
		
		Mockito
			.when(usuarioService.salvar(Mockito.any(Usuario.class)))
			.thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc
		.perform(request(json, ""))
		.andExpect(MockMvcResultMatchers.status().isCreated());
	}
	
	@Test
	public void naoDeveCriarUmUsuario() throws Exception {
		UsuarioDTO dto = new UsuarioDTO();
		dto.setEmail("usuario@email.com");
		dto.setNome("usuario");
		dto.setSenha("senha");
		
		Mockito
		.when(usuarioService.salvar(Mockito.any(Usuario.class)))
		.thenThrow(EmailException.class);
	
		String json = new ObjectMapper().writeValueAsString(dto);
		
		mvc
		.perform(request(json, ""))
		.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void deveObterSaldoPorUsuario() throws Exception {
		Long id = 1L;
		
		Usuario usuario = new Usuario();
		usuario.setId(id);
		
		BigDecimal saldo = BigDecimal.valueOf(1000);
		
		Mockito
			.when(usuarioService.porId(id))
			.thenReturn(usuario);
		
		Mockito
			.when(lancamentoService.saldoPorUsuario(id))
			.thenReturn(saldo);
		
		mvc
			.perform(get("/{id}/saldo", id))
			.andExpect(MockMvcResultMatchers.status().isOk());
	}
	
	@Test
	public void naoDeveObterSaldoParaUsuarioInexistente() throws Exception {
		Long id = 1L;
		
		Mockito
			.when(usuarioService.porId(id))
			.thenThrow(UsuarioException.class);
		
		mvc
		.perform(get("/{id}/saldo", id))
		.andExpect(MockMvcResultMatchers.status().isNotFound());
	}
	
	private MockHttpServletRequestBuilder request(String json, String url) {
		return MockMvcRequestBuilders
				.post(URL_API.concat("/" + url))
				.accept(APPLICATION_JSON)
				.contentType(APPLICATION_JSON)
				.content(json);
	}
	
	private MockHttpServletRequestBuilder get(String url, Object...param) {
		return MockMvcRequestBuilders
				.get(URL_API.concat(url), param)
				.accept(APPLICATION_JSON)
				.contentType(APPLICATION_JSON);
	}
}
