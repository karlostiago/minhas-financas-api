package com.ctsousa.minhasfinancas.resource;

import java.io.Serializable;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctsousa.minhasfinancas.dto.LancamentoDTO;
import com.ctsousa.minhasfinancas.dto.StatusLancamentoDTO;
import com.ctsousa.minhasfinancas.dto.builder.LancamentoDTOBuilder;
import com.ctsousa.minhasfinancas.exception.LancamentoException;
import com.ctsousa.minhasfinancas.exception.UsuarioException;
import com.ctsousa.minhasfinancas.model.Lancamento;
import com.ctsousa.minhasfinancas.model.enumeration.StatusLancamento;
import com.ctsousa.minhasfinancas.model.enumeration.TipoLancamento;
import com.ctsousa.minhasfinancas.service.LancamentoService;
import com.ctsousa.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource implements Serializable {

	private static final long serialVersionUID = -1073125981469219806L;
	
	private final LancamentoService lancamentoService;
	
	private final UsuarioService usuarioService;
	
	@GetMapping
	public ResponseEntity<?> buscar(
			@RequestParam(value = "descricao", required = false) String descricao, 
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "tipo", required = false) String tipo,
			@RequestParam("usuario") Long usuarioID ) {
		
		try {
			
			Lancamento filtro = new Lancamento();
			filtro.setDescricao(descricao);
			filtro.setAno(ano);
			filtro.setMes(mes);
			
			if(tipo != null) {
				filtro.setTipo(TipoLancamento.valueOf(tipo));
			}
			
			filtro.setUsuario(usuarioService.porId(usuarioID));
			List<Lancamento> lancamentos = lancamentoService.buscar(filtro);
			return ResponseEntity.ok(lancamentos);
		}
		catch(UsuarioException | LancamentoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PostMapping
	public ResponseEntity<?> salvar(@RequestBody LancamentoDTO lancamentoDTO) {
		try {
			Lancamento lancamento = toConverterLancamento(lancamentoDTO);
			lancamento = lancamentoService.salvar(lancamento);
			return new ResponseEntity<Lancamento>(lancamento, HttpStatus.CREATED);
		}
		catch(LancamentoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		catch(UsuarioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity<?> porId(@PathVariable("id") Long id) {
		try {
			Lancamento lancamento = lancamentoService.porId(id);
			return ResponseEntity.ok(toConverterLancamentoDTO(lancamento));
		}
		catch(LancamentoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}")
	public ResponseEntity<?> atualizar(@PathVariable("id") Long id, @RequestBody LancamentoDTO lancamentoDTO) {
		
		try {
			Lancamento lancamento = lancamentoService.porId(id);
			
			Lancamento cLancamento = toConverterLancamento(lancamentoDTO);
			cLancamento.setId(lancamento.getId());
			lancamentoService.atualizar(cLancamento);
			
			return ResponseEntity.ok(cLancamento);
		}
		catch(LancamentoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@PutMapping("{id}/atualizaStatus")
	public ResponseEntity<?> atualizaStatus(@PathVariable("id") Long id, @RequestBody StatusLancamentoDTO statusLancamentoDTO) {
		
		Lancamento lancamento = null;
		StatusLancamento status = null;

		try {
			
			lancamento = lancamentoService.porId(id);
			status = StatusLancamento.valueOf(statusLancamentoDTO.getStatus());

			lancamento.setStatus(status);
			lancamentoService.atualizar(lancamento);
			
			return ResponseEntity.ok(lancamento);
		}
		catch(LancamentoException | IllegalArgumentException e) {
			if(status == null) {
				return ResponseEntity.badRequest().body("Erro ao atualizar o lançamento, status informado é inválido.");
			}
			
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<?> deletar(@PathVariable("id") Long id) {
		
		try {			
			Lancamento lancamento = lancamentoService.porId(id);
			lancamentoService.deletar(lancamento);
			
			return new ResponseEntity<Lancamento>( HttpStatus.NO_CONTENT );
		}
		catch(LancamentoException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	private LancamentoDTO toConverterLancamentoDTO(Lancamento lancamento) {
		LancamentoDTOBuilder builder = new LancamentoDTOBuilder()
				.comID(lancamento.getId())
				.comAno(lancamento.getAno())
				.comMes(lancamento.getMes())
				.comDescricao(lancamento.getDescricao())
				.comUsuario(lancamento.getUsuario().getId())
				.comTipoLancamento(lancamento.getTipo().name())
				.comValor(lancamento.getValor())
				.comStatus(lancamento.getStatus().name());
		
		return builder.builde();
	}
	
	private Lancamento toConverterLancamento(LancamentoDTO lancamentoDTO) {
		Lancamento lancamento = new Lancamento();
		lancamento.setId(lancamentoDTO.getId());
		lancamento.setAno(lancamentoDTO.getAno());
		lancamento.setMes(lancamentoDTO.getMes());
		lancamento.setDescricao(lancamentoDTO.getDescricao());
		lancamento.setUsuario(usuarioService.porId(lancamentoDTO.getUsuario()));

		if(lancamentoDTO.getStatus() != null) {			
			lancamento.setStatus(StatusLancamento.valueOf(lancamentoDTO.getStatus()));	
		}
			
		if(lancamentoDTO.getTipo() != null) {			
			lancamento.setTipo(TipoLancamento.valueOf(lancamentoDTO.getTipo()));
		}
		
		lancamento.setValor(lancamentoDTO.getValor());
		return lancamento;
	}
}
