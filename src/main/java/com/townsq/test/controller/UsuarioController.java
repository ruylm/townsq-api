package com.townsq.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.townsq.test.service.UsuarioService;

@RestController
@RequestMapping("/usuario")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;

	@RequestMapping(path="/permissao/{email}")
	public ResponseEntity<String> listarPermissao(@PathVariable("email") String email) {
		
		try {
			
			usuarioService.carregarBD();
			
			String retorno = usuarioService.buscarPermissoesUsuario(email);
			
			System.out.println("Retorno e-mail " + email + ":\n" + retorno.toString());
			
			return ResponseEntity.status(HttpStatus.OK).body(retorno);
			
		} catch (Exception e) {
			
			e.printStackTrace();
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao processar a solicitação.");
		}
		
	}
	

}
