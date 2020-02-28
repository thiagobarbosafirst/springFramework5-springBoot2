package br.gov.go.sefaz.pat.procuracao.service;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.documento.suport.PecaEletronicaConverter;
import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.PecaRevogacaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.PecaRevogacaoJpaRepository;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;

@Service
public class PecaRevogacaoService {
	
	private static Logger logger = LogManager.getLogger(RevogacaoService.class);
	
	@Autowired
	private PecaRevogacaoJpaRepository pecaRevogacaoJpaRepository;
	
	@Autowired
	private PecaRevogacaoJdbcRepository pecaRevogacaoJdbcRepository;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService; 
	
	@HistoryJpa
	@Transactional
	public void savePecaRevogacaoAndUcmDocument(Integer usuarioHistorico, Integer idRevogacao, PecaEletronicaDto pecaEletronicaDto) {
		
		try {
			UcmDocument ucmDocument = pecaEletronicaEcmService.salvarUcmDocument(pecaEletronicaDto);
			logger.debug(String.format("Salvando -> UcmDocument ID: %s salvo com sucesso!", ucmDocument.getId()));
			try {
				pecaEletronicaDto.setChaveAcessoPecaEletronica(ucmDocument.getName());
				PecaEletronica pecaEletronica = PecaEletronicaConverter.toPecaEletronica(pecaEletronicaDto);							
				pecaEletronica = pecaEletronicaService.salvar(usuarioHistorico, pecaEletronica);
				logger.debug(String.format("Salvando -> PecaEletronica ID: %s salvo com sucesso!", pecaEletronica.getId()));
				PecaRevogacaoProcuracao pecaRevogacao = factoryObjects(idRevogacao, pecaEletronica);				
				this.pecaRevogacaoJpaRepository.save(pecaRevogacao);
				logger.debug(String.format("Salvando -> Peça Revogação ID: %s salvo com sucesso!", pecaRevogacao.getId()));
			} catch (Exception e) {				
				pecaEletronicaEcmService.deleteRevision(ucmDocument);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}	
	
	private PecaRevogacaoProcuracao factoryObjects(Integer idProcuracao, PecaEletronica pecaEletronica) {
		PecaRevogacaoProcuracao pecaRevogacao = new PecaRevogacaoProcuracao();		
		PecaEletronica peca = new PecaEletronica();
		RevogacaoProcuracao revogacao = new RevogacaoProcuracao();
		revogacao.setId(idProcuracao);
		peca.setId(pecaEletronica.getId()); 		
		pecaRevogacao.setRevogacaoProcuracao(revogacao);
		pecaRevogacao.setPecaEletronica(peca);
		return pecaRevogacao;
	}
	
	public PecaRevogacaoProcuracao consultarPecaRevogacao(Integer idPecaEletronica){
		return this.pecaRevogacaoJpaRepository.findByPecaEletronicaId(idPecaEletronica);
	}
	
	public List<PecaRevogacaoProcuracao> listarPecaRevogacaoProcuracao(Integer idRevogacao){
		return this.pecaRevogacaoJpaRepository.findByRevogacaoProcuracaoId(idRevogacao);
	}
	
	public List<PecaRevogacaoProcuracao> listarPecaRevogacao(Integer idRevogacao){
		return this.pecaRevogacaoJdbcRepository.findByRevogacaoProcuracaoId(idRevogacao);
	}
	
	public PecaRevogacaoProcuracao consultarPecaRevogacaoPecaEletronica(Integer idRevogacao, String descricaoModelo){
		return this.pecaRevogacaoJpaRepository.findByRevogacaoProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(idRevogacao, descricaoModelo);
	}
}
