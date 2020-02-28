package br.gov.go.sefaz.pat.procuracao.validation.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.RevogacaoService;

@Component
public class RevogacaoBusinessValidator {
	
	@Autowired
	private RevogacaoService revogacaoService;
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	public void validateExisteRenuncia(Integer[] detalhesRevogacao) throws SefazValidationException{
		if((revogacaoService.existeRenuncia(detalhesRevogacao))){
			throw new SefazValidationException("Já existe uma renuncia em andamento na procuração.",null,null);
		}
	}
	
	public void validateSubEstabelecimentoIncompleto(Integer[] detalhesRevogacao) throws SefazValidationException{
		for(Integer movimentacoes : detalhesRevogacao){
			if((procuracaoService.haSubstabelecimentoIncompleto(movimentacoes))){
				throw new SefazValidationException("Já existe uma SubEstabelecimento em andamento na procuração.",null,null);
			}			
		}
	}
}
