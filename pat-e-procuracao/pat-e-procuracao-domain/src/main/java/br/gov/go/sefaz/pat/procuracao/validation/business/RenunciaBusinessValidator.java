package br.gov.go.sefaz.pat.procuracao.validation.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.DocumentoMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.RenunciaProcuracaoService;

@Component
public class RenunciaBusinessValidator {
	
	@Autowired
	private  RenunciaProcuracaoService renunciaProcuracaoService;
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	/**
	 * Validação para renúncias na modalidade eletrônica.
	 * @param detalhesRenuncia Lista dos detalhes da procuração
	 * @throws SefazValidationException
	 */
	public void validateSaveEletronica(Integer[] detalhesRenuncia) throws SefazValidationException{		
		validateSaveDefault(detalhesRenuncia);
	}
	
	/**
	 * 	 * Validação para renúncias na modalidade eletrônica.
	 * @param renuncia Objeto agrupador com os itens necessários para a validação da renuncia candidata ao cadastro presencial.
	 * @throws SefazValidationException
	 */
	public void validateSavePresencial(AgrupadorMovimentacaoPresencialDto renuncia) throws SefazValidationException{
		Integer[] itens = new Integer[renuncia.getMovimentacoes().size()];
		validateSaveDefault(renuncia.getMovimentacoes().toArray(itens));
		validateModeloDocFiles(renuncia.getFiles());
		
		if(!renunciaProcuracaoService.checkProcuradorMovimentacao(renuncia))
			throw new SefazValidationException("O procurador e os itens de Procuração (Movimentação) não são correspondentes.","renuncia","renuncia.notPersist");		
	}
	
	
	
	/**
	 * Método de suporte. Validação padrão chamada tanto pela validação para renúncia eletrônica, como para a presencial.
	 * @param detalhesRenuncia Lista dos detalhes da procuração.
	 * @throws SefazValidationException
	 */
	public void validateSaveDefault(Integer[] detalhesRenuncia) throws SefazValidationException{
		validateExisteRevogacaoVinculada(detalhesRenuncia);		
		validateSubEstabelecimentoIncompleto(detalhesRenuncia);
	}
		
	/**
	 * Método de suporte. Valida se todos os arquivos estão com seus respectivos modelos de documento.	
	 * @param files Arquivos
	 * @throws SefazValidationException
	 */
	public void validateModeloDocFiles(List<DocumentoMovimentacaoPresencialDto> files) throws SefazValidationException{		
		for (DocumentoMovimentacaoPresencialDto documento : files)
			if(documento.getModeloDocumento() == null)
				throw new SefazValidationException("O Modelo de Documento não foi encontrado para o arquivo:  " + documento.getDocumento().getOriginalFilename(),"renuncia","renuncia.files.notModeloDocFound");
		
	}
	
	/**
	 * Método de suporte. Valida a existência de algum substabelecimento em andamento em alguma movimentação de procuração. 
	 * @param detalhes Lista de movimentações a serem testadas.
	 * @throws SefazValidationException
	 */
	public void validateSubEstabelecimentoIncompleto(Integer[] detalhes) throws SefazValidationException{
		for(Integer movimentacao : detalhes){
			if((procuracaoService.haSubstabelecimentoIncompleto(movimentacao))){
				throw new SefazValidationException("Já existe uma SubEstabelecimento em andamento na procuração.",null,null);
			}			
		}
	}
	
	/**
	 * Método de suporte das validações principais. Faz a verificação da existência de alguma revogação vinculada a uma movimentação.
	 * @param detalhesRenuncia Lista com os Ids de movimentação de procuração a ser verificada.
	 * @throws SefazValidationException
	 */
	public void validateExisteRevogacaoVinculada(Integer[] detalhesRenuncia) throws SefazValidationException{
		if((renunciaProcuracaoService.existsRevogacao(detalhesRenuncia))){
			throw new SefazValidationException("Já existe uma revogação em andamento na procuração.","renuncia","renuncia.notPersist");
		}
	}
	
}
