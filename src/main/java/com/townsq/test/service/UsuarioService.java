package com.townsq.test.service;

public interface UsuarioService {
	
	public void carregarBD() throws Exception;
	
	public String buscarPermissoesUsuario(String email);

}
