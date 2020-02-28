package br.gov.go.sefaz.pat.procuracao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import br.gov.go.sefaz.javaee.corporativo.model.Logradouro;
import br.gov.go.sefaz.javaee.corporativo.service.LogradouroService;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.ENDERECO_PATH_ROOT)
public class EnderecoController {
	
	@Autowired
	LogradouroService enderecoService;
	
	@GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Logradouro> ListarCep(String cep) throws IllegalArgumentException{
		if(cep.equals("00000000")) throw new IllegalArgumentException("CEP Inválido!");	
		
		List<Logradouro> enderecos = enderecoService.consultarPorCep(Integer.parseInt(cep));		
		return enderecos;
	}
	
}
