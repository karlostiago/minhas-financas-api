package com.ctsousa.minhasfinancas.service;

import org.springframework.stereotype.Service;

import com.ctsousa.minhasfinancas.model.Usuario;

@Service
public interface UsuarioService {

	Usuario autenticar(String email, String senha);
	
	Usuario salvar(Usuario usuario);
	
	void validarEmail(String email);
	
	Usuario porId(Long id);
}
