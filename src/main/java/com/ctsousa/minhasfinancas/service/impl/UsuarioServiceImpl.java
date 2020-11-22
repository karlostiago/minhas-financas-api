package com.ctsousa.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ctsousa.minhasfinancas.exception.AutenticarException;
import com.ctsousa.minhasfinancas.exception.EmailException;
import com.ctsousa.minhasfinancas.exception.UsuarioException;
import com.ctsousa.minhasfinancas.model.Usuario;
import com.ctsousa.minhasfinancas.model.repository.UsuarioRepository;
import com.ctsousa.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

	private UsuarioRepository usuarioRepository;
	
	public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}
	
	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = usuarioRepository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new AutenticarException("Usuário não encontrado, para o email informado.");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new AutenticarException("Senha inválida");
		}
		
		return usuario.get();
	}

	@Override
	@Transactional
	public Usuario salvar(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return usuarioRepository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean exist = usuarioRepository.existsByEmail(email);
		
		if(exist) {
			throw new EmailException("Existe email já existe");
		}
		
		if(email == null || email.isEmpty()) {
			throw new EmailException("Nenhum email foi informado.");
		}
	}

	@Override
	public Usuario porId(Long id) {
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		if(!usuario.isPresent()) {
			throw new UsuarioException("Nenhum usuário encontrado com o id '" + id + "', informado");
		}
		
		return usuario.get();
	}
}
