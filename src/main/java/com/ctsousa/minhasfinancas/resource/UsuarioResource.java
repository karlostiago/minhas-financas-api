package com.ctsousa.minhasfinancas.resource;

import java.io.Serializable;
import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ctsousa.minhasfinancas.dto.UsuarioDTO;
import com.ctsousa.minhasfinancas.exception.AutenticarException;
import com.ctsousa.minhasfinancas.exception.EmailException;
import com.ctsousa.minhasfinancas.exception.UsuarioException;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.service.LancamentoService;
import com.ctsousa.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource implements Serializable {

	private static final long serialVersionUID = -2429966892728904908L;
	
	private final UsuarioService usuarioService;
	
	private final LancamentoService lancamentoService;
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar(@RequestBody UsuarioDTO usuarioDTO) {
		
		try {
			Usuario usuario = usuarioService.autenticar(usuarioDTO.getEmail().toUpperCase(), usuarioDTO.getSenha());
			return ResponseEntity.ok(usuario);
		}
		catch(AutenticarException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody UsuarioDTO usuarioDTO ) {
		Usuario usuario = new Usuario();
		usuario.setEmail(usuarioDTO.getEmail());
		usuario.setNome(usuarioDTO.getNome());
		usuario.setSenha(usuarioDTO.getSenha());
		
		try {
			return new ResponseEntity<Usuario>(usuarioService.salvar(usuario), HttpStatus.CREATED);
		}
		catch(EmailException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity<BigDecimal> saldo(@PathVariable("id") Long id) {
		
		try {	
			
			Usuario usuario = usuarioService.porId(id);
			BigDecimal saldo = lancamentoService.saldoPorUsuario(usuario.getId());
			
			return ResponseEntity.ok(saldo);
		}
		catch(UsuarioException e) {			
			return new ResponseEntity<BigDecimal>( HttpStatus.NOT_FOUND );
		}
	}
}
