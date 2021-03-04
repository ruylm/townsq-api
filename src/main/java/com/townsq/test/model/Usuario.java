package com.townsq.test.model;

import java.util.List;

import lombok.Data;

@Data
public class Usuario {
	
	private String email;
	private List<Grupo> grupos;

}
