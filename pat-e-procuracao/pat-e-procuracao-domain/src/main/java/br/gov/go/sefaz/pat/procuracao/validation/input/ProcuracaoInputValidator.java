package br.gov.go.sefaz.pat.procuracao.validation.input;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.validation.business.ProcuracaoBusinessValidator;
import br.gov.go.sefaz.pat.support.UtilSupport;

@Component
public class ProcuracaoInputValidator implements Validator{
	
	@Autowired
	ProcuracaoBusinessValidator procuracaoServiceValidator;
	
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
	
	private void validateProcuracao(ProcuracaoDto procuracaoDto) throws SefazValidationException{
		
		if(procuracaoDto.getDataValidade() != null) {
			if(procuracaoDto.getDataValidade().before(new java.util.Date())) {
				throw new SefazValidationException("Data de validade não pode ser menor que a data atual.", "procuradores", "procuracao.dataValidade.notEmpty"); 
			}
		}
		if(procuracaoDto.getDataLimite() != null) {
			if(procuracaoDto.getDataLimite().before(new java.util.Date())) {
				throw new SefazValidationException("Data limite para substabelecer não pode ser menor que a data atual.", "procuradores", "procuracao.dataValidade.notEmpty"); 
			}
			if(procuracaoDto.getDataValidade() != null) {
				if(procuracaoDto.getDataLimite().after(procuracaoDto.getDataValidade())) {
					throw new SefazValidationException("Data limite para substabelecer não pode ser maior que a data de validade da procuração.", "procuradores", "procuracao.dataValidade.notEmpty"); 
				}
			}
		}
	}
	
	private void validateSujeitoPassivo(String[] sujeitosPassivos) throws SefazValidationException{
		
		if(sujeitosPassivos == null || sujeitosPassivos.length == 0){
			throw new SefazValidationException("Selecione o(s) sujeito(s) passivo(s).", "sujeitoPassivos", "procuracao.sujeitosPassivos.notEmpty"); 
		}
	}
	
	private void validateProcurador(ProcuracaoDto procuracaoDTO) throws SefazValidationException{
		
		if(procuracaoDTO.getProcuradores() == null || procuracaoDTO.getProcuradores().size() == 0){
			throw new SefazValidationException("Informe o(s) procurador(es).", "procuradores", "procuracao.procuradores.notEmpty"); 
		}
	}
	
	public void validateSave(ProcuracaoDto procuracaoDto, String[] sujeitosPassivos, Errors errors) throws SefazValidationException{
		this.validate(procuracaoDto, errors);		
		if(!errors.hasErrors()){				
				this.validateProcurador(procuracaoDto);
				this.validateSujeitoPassivo(sujeitosPassivos);
				this.validateProcuracao(procuracaoDto); 
				this.procuracaoServiceValidator.validateSave(procuracaoDto, sujeitosPassivos);
				
		}
	}	
	
	/**
	 * Validador da lista de procuração
	 * @param haProcuradoresAtivos
	 * @throws SefazValidationException
	 */
	public void validateList(Boolean haProcuradoresAtivos) throws SefazValidationException{
		if(haProcuradoresAtivos == false || haProcuradoresAtivos == null)
			throw new SefazValidationException("Não existe procurador(a) ativo.", null, null);
	}
	/**
	 * Validação Presencial a seleção do(s) procurador(es) no collapse
	 * @param agrupadorMovimentacaoPresencialDto
	 */
	public void validateItens(AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto){
		if(agrupadorMovimentacaoPresencialDto.getMovimentacoes() == null) 
			throw new AjaxRequestException("Informe o(s) procurador(es).");				
	}
	
	/**
	 * Validar a seleção do(s) procurador(es) por array de Códigos de movimentação. 
	 * Disparando uma SefazValidationException em caso dos dados estejam invalidos.
	 * @param Array com os códigos de Movimentação de procuração a serem validados.
	 */
	public void validateItens(Integer[] codigosMovimentacao) throws SefazValidationException{
		if(codigosMovimentacao == null) 
			throw new SefazValidationException("Informe o(s) procurador(es).");				
	}
	
	/**
	 * Validação Presencial se há documentos inseridos
	 * @param agrupadorMovimentacaoPresencialDto
	 */
	public void validateDocumentos(AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto){
		if(agrupadorMovimentacaoPresencialDto.getFiles() == null) 
			throw new AjaxRequestException("Informe o(s) documento(s).");		
	}
	
	/**
	 * Validação Presencial se há documentos inseridos
	 * @param agrupadorMovimentacaoPresencialDto
	 */
	public void validateCnpj(String cnpj){
		if(!new UtilSupport().isCnpj(cnpj)) { 
			throw new AjaxRequestException("Cnpj inválido.");
		}		
	}
	
}
