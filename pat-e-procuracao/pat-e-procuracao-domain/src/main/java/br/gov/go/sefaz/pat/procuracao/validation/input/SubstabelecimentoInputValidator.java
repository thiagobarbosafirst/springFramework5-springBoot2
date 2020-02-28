package br.gov.go.sefaz.pat.procuracao.validation.input;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.validation.business.SubstabelecimentoBusinessValidator;

@Component
public class SubstabelecimentoInputValidator implements Validator{
	
	@Autowired
	SubstabelecimentoBusinessValidator substabelecimentoBusinessValidator;
	
	@Autowired
	MessageSource messageSource;

	@Override
	public boolean supports(Class<?> clazz) {
		return PessoaFisica.class.equals(clazz);
	}
	
	@Override
	public void validate(Object object, Errors errors) {
		
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "numeroCpf", "pessoaFisica.cpf.notEmpty");
//		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "nome", "pessoaFisica.nome.notEmpty");
//		
//		PessoaFisica pessoaFisica = (PessoaFisica) object;
//		
//		if(pessoaFisica.getNumeroCpf() != null && pessoaFisica.getNumeroCpf().length() < 11){
//			errors.rejectValue("numeroCpf", "pessoaFisica.cpf.min.character");
//		}
	}
	
	private void validateSubstabelecimento(ProcuracaoDto procuracaoDto) throws SefazValidationException{
		
		if(procuracaoDto.getDataValidadeSubstabelecimento() != null) {
			if(procuracaoDto.getDataValidade() != null) {
				if(procuracaoDto.getDataValidadeSubstabelecimento().after(procuracaoDto.getDataValidade())) {
					throw new SefazValidationException("Data de validade do substabelecimento não pode ser maior que a data de validade da procuração.", "procuradores", "procuracao.dataValidade.notEmpty"); 
				}
			}
			if(procuracaoDto.getDataValidadeSubstabelecimento().before(new java.util.Date())) {
				throw new SefazValidationException("Data de validade do substabelecimento não pode ser menor que a data atual.", "procuradores", "procuracao.dataValidade.notEmpty"); 
			}
		}
		if(procuracaoDto.getDataLimite() != null) { 
			if(procuracaoDto.getDataLimite().before(new java.util.Date())) {
				throw new SefazValidationException("Data limite para substabelecer não pode ser menor que a data atual.", "procuradores", "procuracao.dataValidade.notEmpty"); 
			}
			if(procuracaoDto.getDataValidadeSubstabelecimento() != null) {
				if(procuracaoDto.getDataLimite().after(procuracaoDto.getDataValidadeSubstabelecimento())) {
					throw new SefazValidationException("Data limite para substabelecer não pode ser maior que a data de validade do substabelecimento.", "procuradores", "procuracao.dataValidade.notEmpty"); 
				}
			}
		}
	}
	
	private void validateSujeitoPassivo(List<Integer> movimentacoes) throws SefazValidationException{
		
		if(movimentacoes == null || movimentacoes.size() == 0){
			throw new SefazValidationException("Selecione o(s) sujeito(s) passivo(s).", "sujeitoPassivos", "procuracao.sujeitosPassivos.notEmpty"); 
		}
	}
	
	private void validateProcurador(ProcuracaoDto procuracaoDTO) throws SefazValidationException{
		
		if(procuracaoDTO.getProcuradores() == null || procuracaoDTO.getProcuradores().size() == 0){
			throw new SefazValidationException("Informe o(s) procurador(es).", "procuradores", "procuracao.procuradores.notEmpty"); 
		}
	}
	
	public void validateSave(ProcuracaoDto procuracaoDto, Errors errors) throws SefazValidationException{ 
		this.validate(procuracaoDto, errors);		
		if(!errors.hasErrors()){				
				this.validateProcurador(procuracaoDto);
				this.validateSujeitoPassivo(procuracaoDto.getListaMovimentacoes());
				this.validateSubstabelecimento(procuracaoDto);
				this.substabelecimentoBusinessValidator.validateSave(procuracaoDto);
				
		}
	}	
	
}
