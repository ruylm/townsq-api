package com.townsq.test.model;

import java.util.List;

import lombok.Data;

@Data
public class Grupo {

	private String tipo;
	private Integer idCondominio;
	private List<Funcionalidade> funcionalidades;
}
