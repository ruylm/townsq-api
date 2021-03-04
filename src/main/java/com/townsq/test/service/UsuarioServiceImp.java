package com.townsq.test.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.townsq.test.enums.PermissaoEnum;
import com.townsq.test.model.Funcionalidade;
import com.townsq.test.model.Grupo;
import com.townsq.test.model.Usuario;

@Service
public class UsuarioServiceImp implements UsuarioService {
	
	private static List<Usuario> bdUsuarios;
	private static List<Grupo> bdGrupos;

	public void carregarBD() throws Exception {
		
		try {

			bdUsuarios = new ArrayList<Usuario>();
			bdGrupos = new ArrayList<Grupo>();
			
			InputStream in = getClass().getResourceAsStream("/data.txt"); 
			BufferedReader b = new BufferedReader(new InputStreamReader(in));
			
            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                String[] linha = readLine.split(";");
                
                if (linha[0] != null && linha[0].equalsIgnoreCase("Usuario")) {
                	
                	adicionarUsuarioBD(linha);
                	
                } else if (linha[0] != null && linha[0].equalsIgnoreCase("Grupo")) {
                	
                	adicionarGrupoBD(linha);
                }
            }
            
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } catch (IOException e) {
			e.printStackTrace();
			throw e;
		}

    }
	
	public String buscarPermissoesUsuario(String email) {
		
		// Localizando usuario na base de dados
		Usuario usuario = bdUsuarios.stream()
				.filter(user -> user.getEmail().equals(email))
				.findFirst().orElse(null);
		
		if (usuario == null) {
			return "Nenhuma permissão encontrada para o e-mail informado";
		}

		// Buscando os grupos que o usuario esta inserido
		List<Grupo> grupos = bdGrupos.stream()
				.filter(gru -> usuario.getGrupos().stream()
						.anyMatch(usu ->
							gru.getTipo().equals(usu.getTipo()) &&
							gru.getIdCondominio().equals(usu.getIdCondominio())))
				.sorted((o1, o2) -> o1.getIdCondominio().compareTo(o2.getIdCondominio()))
				.collect(Collectors.toList());
		
		Map<Integer, List<Funcionalidade>> permissoesRetorno = new HashMap<Integer, List<Funcionalidade>>();

		// Identificando as permissoes mais altas que o usuario possui (considerando Escrita > Leitura > Nenhuma)
		for (int i = 0; i < grupos.size(); i++) {
			
			List<Funcionalidade> funcionalidades = permissoesRetorno.get(grupos.get(i).getIdCondominio());
			
			if (funcionalidades == null) {
				permissoesRetorno.put(grupos.get(i).getIdCondominio(), grupos.get(i).getFuncionalidades());
			}
			else {
				
				List<Funcionalidade> novasFuncs = new ArrayList<Funcionalidade>();
				
				for (int j = 0; j < funcionalidades.size(); j++) {
					
					Funcionalidade fun = new Funcionalidade();
					fun.setFuncionalidade(funcionalidades.get(j).getFuncionalidade());
					List<Funcionalidade> funcs = grupos.get(i).getFuncionalidades();
					
					if (funcs.get(j).getIdPermissao() > funcionalidades.get(j).getIdPermissao()) {
						
						fun.setPermissao(funcs.get(j).getPermissao());
						
					} else {
						
						fun.setPermissao(funcionalidades.get(j).getPermissao());
						
					}
					novasFuncs.add(fun);
					
				}
				
				permissoesRetorno.put(grupos.get(i).getIdCondominio(), novasFuncs);
				
			}
		
		}
		
		return formatarRetorno(permissoesRetorno);
		
	}
	
	public void adicionarUsuarioBD(String[] linha) {
		
		List<Grupo> grupos = new ArrayList<Grupo>();
    	
    	Usuario user = new Usuario();
    	
    	user.setEmail(linha[1]);
    	
    	String[] tipoGrupoIdCondominio = linha[2].split("\\),\\(");
    	
    	for (String item : tipoGrupoIdCondominio) {
    	    
    	    String[] dado = item.replaceAll( "^\\[|\\]$", "" ).replaceAll("[()]","").split(",");
    	    	
	    	Grupo grupo = new Grupo();
	    	grupo.setTipo(dado[0]);
	    	grupo.setIdCondominio(Integer.parseInt(dado[1]));
	    	
	    	grupos.add(grupo);
    	    
    	}
    	
    	user.setGrupos(grupos);
    	
    	bdUsuarios.add(user);
    	
	}
	
	public void adicionarGrupoBD(String[] linha) {
		
		List<Funcionalidade> funcionalidades = new ArrayList<Funcionalidade>();
    	
		Grupo grupo = new Grupo();
		grupo.setTipo(linha[1]);
		grupo.setIdCondominio(Integer.parseInt(linha[2]));
    	
    	String[] funcionalidadePerm = linha[3].split("\\),\\(");
    	
    	for (String item : funcionalidadePerm) {
    	    
    	    String[] dado = item.replaceAll( "^\\[|\\]$", "" ).replaceAll("[()]","").split(",");
    	    	
	    	Funcionalidade func = new Funcionalidade();
	    	func.setFuncionalidade(dado[0]);
	    	func.setPermissao(dado[1]);
	    	func.setIdPermissao(PermissaoEnum.valueOf(dado[1].toUpperCase()).getCodigo());
	    	
	    	funcionalidades.add(func);
    	    
    	}
    	
    	grupo.setFuncionalidades(funcionalidades);
    	
    	bdGrupos.add(grupo);
    	
	}
	
	public String formatarRetorno(Map<Integer, List<Funcionalidade>> lista) {
		
		StringBuffer retorno = new StringBuffer();
		
		for (Map.Entry<Integer, List<Funcionalidade>> item : lista.entrySet()) {
			
			retorno.append(item.getKey() + ";[");
			
			for (Funcionalidade func : item.getValue()) {
				
				retorno.append("(" + func.getFuncionalidade());
				retorno.append("," + func.getPermissao() + "),");
			}
			
			retorno.setLength(retorno.length() - 1);
			retorno.append("]\n");
			
		}
		retorno.setLength(retorno.length() - 1);
		
		return retorno.toString();
	}

}
