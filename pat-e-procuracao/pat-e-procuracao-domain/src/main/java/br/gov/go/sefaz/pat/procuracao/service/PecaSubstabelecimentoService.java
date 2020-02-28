package br.gov.go.sefaz.pat.procuracao.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.documento.suport.PecaEletronicaConverter;
import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaProcuracao;
import br.gov.go.sefaz.pat.model.PecaSubstabelecimento;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.PecaSubstabelecimentoJpaRepository;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;

@Service
public class PecaSubstabelecimentoService {	
	
	@Autowired
	private PecaSubstabelecimentoJpaRepository pecaSubstabelecimentoJpaRepository;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService; 
	
	@HistoryJpa
	@Transactional
	public void save(Integer usuarioHistorico, PecaSubstabelecimento pecaSubstabelecimento, PecaEletronicaDto pecaEletronicaDto) {
		
		try {
			pecaEletronicaEcmService.salvar(usuarioHistorico, pecaEletronicaDto);
			
			this.pecaSubstabelecimentoJpaRepository.save(pecaSubstabelecimento);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	@HistoryJpa
	@Transactional
	public void savePecaSubstabelecimentoAndUcmDocument(Integer usuarioHistorico, Integer idSubstabelecimento, PecaEletronicaDto pecaEletronicaDto) {
		
		try {
			
			UcmDocument ucmDocument = pecaEletronicaEcmService.salvarUcmDocument(pecaEletronicaDto);
			pecaEletronicaDto.setChaveAcessoPecaEletronica(ucmDocument.getName());
			PecaEletronica pecaEletronica = PecaEletronicaConverter.toPecaEletronica(pecaEletronicaDto);			
			try {
				
				pecaEletronica = pecaEletronicaService.salvar(usuarioHistorico, pecaEletronica);
				PecaSubstabelecimento pecaSubstabelecimento = new PecaSubstabelecimento();		
				PecaEletronica peca = new PecaEletronica();
				SubEstabelecimentoProcuracao subEstabelecimentoProcuracao = new SubEstabelecimentoProcuracao();
				subEstabelecimentoProcuracao.setId(idSubstabelecimento);
				peca.setId(pecaEletronica.getId()); 		
				pecaSubstabelecimento.setSubEstabelecimentoProcuracao(subEstabelecimentoProcuracao);
				pecaSubstabelecimento.setPecaEletronica(peca);
				
				this.pecaSubstabelecimentoJpaRepository.save(pecaSubstabelecimento);
			
			} catch (Exception e) {				
				pecaEletronicaEcmService.deleteRevision(ucmDocument);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public PecaSubstabelecimento findPecaSubstabelecimento(Integer idPecaEletronica){
		return this.pecaSubstabelecimentoJpaRepository.findByPecaEletronicaId(idPecaEletronica);
	}
	
	public PecaSubstabelecimento findPecaSubstabelecimento(Integer idSubstabelecimento, String descricaoModelo){
		return this.pecaSubstabelecimentoJpaRepository.findBySubEstabelecimentoProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(idSubstabelecimento, descricaoModelo);
	}
	
	public List<PecaSubstabelecimento> findBySubstabelecimentoId(Integer idSubstabelecimento){
		return this.pecaSubstabelecimentoJpaRepository.findBySubEstabelecimentoProcuracaoId(idSubstabelecimento);
	}
	
	public List<PecaSubstabelecimento> findByPecaIdSubEstabelecimentoProcuracao(Integer idSubstabelecimento){
		return this.pecaSubstabelecimentoJpaRepository.findBySubEstabelecimentoProcuracaoId(idSubstabelecimento);
	}
	
}
