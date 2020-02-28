package br.gov.go.sefaz.pat.procuracao.validation.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;

@Component
public class ProcuracaoBusinessValidator {	
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	public void validateSave(ProcuracaoDto procuracaoDTO, String[] sujeitosPassivos) throws SefazValidationException {
		
		for (int contSujeitoPassivo = 0; contSujeitoPassivo < sujeitosPassivos.length; contSujeitoPassivo++) {
			if(procuracaoService.verificarProcuracao(Integer.valueOf(sujeitosPassivos[contSujeitoPassivo]), EnumStatusProcuracao.Ativa.getCodigo())) {				
				throw new SefazValidationException("Já existe procuração ativa para esses sujeitos passivos.", "procuracao", "procuracao.notUpdate");
			}
			if(procuracaoService.verificarProcuracao(Integer.valueOf(sujeitosPassivos[contSujeitoPassivo]), EnumStatusProcuracao.PendenteAssinatura.getCodigo())) {				
				throw new SefazValidationException("Já existe procuração pendente de assinatura para esses sujeitos passivos.", "procuracao", "procuracao.notUpdate");
			}
		}
		
	}
	
}
