package br.gov.go.sefaz.pat.procuracao.validation.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.validation.business.RevogacaoBusinessValidator;

@Component
public class RevogacaoInputValidator implements Validator{
	
	@Autowired
	RevogacaoBusinessValidator revogacaoBusinessValidator;
	
	@Autowired
	MessageSource messageSource;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return RevogacaoProcuracao.class.equals(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
	}
	
	private void validateDetalhesRevogacao(Integer[] detalhesRevogacao) throws SefazValidationException{
		if(detalhesRevogacao == null){
			throw new SefazValidationException("Selecione o(s) sujeito(s) passivo(s).", null, null);
		}
	}
	
	public void validateSave(Integer[] detalhesRevogacao, Errors errors) throws SefazValidationException{
		this.validateDetalhesRevogacao(detalhesRevogacao);
		this.revogacaoBusinessValidator.validateExisteRenuncia(detalhesRevogacao);
		this.revogacaoBusinessValidator.validateSubEstabelecimentoIncompleto(detalhesRevogacao);
	}
	
	public void validateMinutaRevogacao(Integer[] detalhesRevogacao) throws SefazValidationException{
		if(detalhesRevogacao == null)
			throw new SefazValidationException("Informe o(s) sujeito(s) passivo(s) para geração da minuta. ", null, null);
	}
	
	public void validateRevogacaoNula(Boolean existeRevogacao) throws SefazValidationException{
		if(existeRevogacao == false || existeRevogacao == null)
			throw new SefazValidationException("Já existe revogação eletrônica em aberto.", null, null);
	}
	
	public void validateListaRevogacoes(Boolean existeRevogacoes) throws SefazValidationException{
		if(existeRevogacoes == false || existeRevogacoes == null)
			throw new SefazValidationException("Não foi encontrado revogações.", null, null);
	}
}
