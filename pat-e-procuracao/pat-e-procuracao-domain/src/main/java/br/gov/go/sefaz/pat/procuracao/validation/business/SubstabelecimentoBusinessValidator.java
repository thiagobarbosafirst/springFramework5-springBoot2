package br.gov.go.sefaz.pat.procuracao.validation.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusSubstabelecimento;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuradorDto;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;

@Component
public class SubstabelecimentoBusinessValidator {	
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	public void validateSave(ProcuracaoDto procuracaoDto) throws SefazValidationException {
		
		List<MovimentacaoProcuracao> listaMovimentacao = movimentacaoProcuracaoService.findByProcuracao(procuracaoDto.getId());
		for (MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacao) {
			for (ProcuradorDto procuradorDto : procuracaoDto.getProcuradores()) {
				if(procuradorDto.getIdPessoa().equals(movimentacaoProcuracao.getProcurador().getPessoa().getIdPessoa())) {
					for (int contMovimentacoes = 0; contMovimentacoes < procuracaoDto.getListaMovimentacoes().size(); contMovimentacoes++) {	
						MovimentacaoProcuracao movimentacaoProcuracaoSubstabelecer = movimentacaoProcuracaoService.find(procuracaoDto.getListaMovimentacoes().get(contMovimentacoes));
						Integer idSujeitoPassivo = movimentacaoProcuracaoSubstabelecer.getSujeitoPassivoProcesso().getId();
						if(idSujeitoPassivo.equals(movimentacaoProcuracao.getSujeitoPassivoProcesso().getId())) {
							if(movimentacaoProcuracao.getIndiMovmtProcuracaoAtivaAsChar() == 'S') {
								throw new SefazValidationException("Procurador já está ativo para essa procuração.", "procuracao", "procuracao.notUpdate");
							}
							else if(movimentacaoProcuracao.getSubEstabelecimentoProcuracao() != null && movimentacaoProcuracao.getSubEstabelecimentoProcuracao().getStatus().equals(EnumStatusSubstabelecimento.Incompleto)) {
								throw new SefazValidationException("Já existe substabelecimento pendente de assinatura para o procurador.", "procuracao", "procuracao.notUpdate");
							}
							else if(movimentacaoProcuracao.getRenunciaProcuracao() != null && movimentacaoProcuracao.getRenunciaProcuracao().getPendenteAssinaturaDigital().equals(String.valueOf(EnumSimNao.S.getValor()))) { 
								throw new SefazValidationException("Já existe renúncia pendente de assinatura para o procurador.", "procuracao", "procuracao.notUpdate");
							}
							else if(movimentacaoProcuracao.getRevogacaoProcuracao() != null && movimentacaoProcuracao.getRevogacaoProcuracao().getIndiPendenteAssinaturaDigAsChar().equals(EnumSimNao.S.getValor())) {
								throw new SefazValidationException("Já existe revogação pendente de assinatura para o procurador.", "procuracao", "procuracao.notUpdate");
							}
						}
					}					
				}
			}			 
		}	
		
	}
	
}
