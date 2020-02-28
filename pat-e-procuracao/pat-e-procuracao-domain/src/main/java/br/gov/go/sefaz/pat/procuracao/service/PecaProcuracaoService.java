package br.gov.go.sefaz.pat.procuracao.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumProfilesDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumSubCategoriaDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.ModeloDocumentoService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.documento.suport.PecaEletronicaConverter;
import br.gov.go.sefaz.pat.model.CategoriaDocumento;
import br.gov.go.sefaz.pat.model.ModeloDocumento;
import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.SubCategoriaDocumento;
import br.gov.go.sefaz.pat.procuracao.model.dto.DocumentoMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.PecaProcurcaoJpaRepository;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;
@Service
public class PecaProcuracaoService {	
	
	@Autowired
	private PecaProcurcaoJpaRepository pecaProcurcaoJpaRepository;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService;
	
	@Autowired
	private ModeloDocumentoService modeloDocumentoService;
	
//	@Autowired
//	private AssinadorDigital assinadorDigital;
	
/*	@Autowired
	private RecuperadorPath recuperadorPath;*/
	
	private static Logger logger = LogManager.getLogger(ProcuracaoService.class);
	
	@HistoryJpa
	@Transactional
	public void save(Integer usuarioHistorico, PecaProcuracao pecaProcuracao, PecaEletronicaDto pecaEletronicaDto) throws SefazException {
		
		try {
			pecaEletronicaEcmService.salvar(usuarioHistorico, pecaEletronicaDto);
			
			this.pecaProcurcaoJpaRepository.save(pecaProcuracao);
			
		} catch (Exception e) {
			throw new SefazException("Ocorreu algum problema ao salvar um documento no ucm." + e.getMessage());
		} 
	}
	
	@HistoryJpa
	@Transactional
	public void savePecaProcuracaoAndUcmDocument(Integer usuarioHistorico, Integer idProcuracao, PecaEletronicaDto pecaEletronicaDto) throws SefazException{
		
		try {
			logger.debug("PecaProcuracaoService.savePecaProcuracaoAndUcmDocument: Antes de salvar ucm");
			UcmDocument ucmDocument = pecaEletronicaEcmService.salvarUcmDocument(pecaEletronicaDto);
			logger.debug("PecaProcuracaoService.savePecaProcuracaoAndUcmDocument: Depois de salvar ucm");
			pecaEletronicaDto.setChaveAcessoPecaEletronica(ucmDocument.getName());
			PecaEletronica pecaEletronica = PecaEletronicaConverter.toPecaEletronica(pecaEletronicaDto);			
			try {
				logger.debug("PecaProcuracaoService.savePecaProcuracaoAndUcmDocument: Antes de salvar pecaEletronica");
				pecaEletronica = pecaEletronicaService.salvar(usuarioHistorico, pecaEletronica);
				logger.debug("PecaProcuracaoService.savePecaProcuracaoAndUcmDocument: Depois de salvar pecaEletronica");
				PecaProcuracao pecaProcuracao = new PecaProcuracao();		
				PecaEletronica peca = new PecaEletronica();
				Procuracao procuracao = new Procuracao();
				procuracao.setId(idProcuracao);
				peca.setId(pecaEletronica.getId()); 		
				pecaProcuracao.setProcuracao(procuracao);
				pecaProcuracao.setPecaEletronica(peca);
				logger.debug("PecaProcuracaoService.savePecaProcuracaoAndUcmDocument: Antes de salvar pecaProcuracao");
				this.pecaProcurcaoJpaRepository.save(pecaProcuracao);
				logger.debug("PecaProcuracaoService.savePecaProcuracaoAndUcmDocument: Depois de salvar pecaProcuracao");
			
			} catch (Exception e) {				
				pecaEletronicaEcmService.deleteRevision(ucmDocument);
				throw new SefazException(e.getMessage());
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new SefazException(e.getMessage());
		} 
	}	
	
	public PecaEletronicaDto gerarPeca(Integer usuarioAutenticadoId,
			byte[] documento, Integer idModeloDocumento) throws SefazException{		
		return gerarPeca(usuarioAutenticadoId, usuarioAutenticadoId, documento, idModeloDocumento);		
	}
	
	public PecaEletronicaDto gerarPeca(Integer usuarioAutenticadoId, Integer usuarioAutor, 
			byte[] documento, Integer idModeloDocumento) throws SefazException{
		
		PecaEletronicaDto pecaEletronicaDTO = null;	 
		
		try {			
			//Procurar o modelo do documento.
			ModeloDocumento modeloDocumento = modeloDocumentoService.findOne(idModeloDocumento);
			SubCategoriaDocumento subCategoriaDoc = modeloDocumento.getSubCategoriaDocumento();
			CategoriaDocumento  categoriaDoc = subCategoriaDoc.getCategoriaDocumento();
/*			logger.debug("pecaProcuracaoService.gerarPeca Antes de assinar peça");
			byte[] pdfDataSigned = assinadorDigital.assinarPdf(documento, modeloDocumento.getDescricao(), recuperadorPath.recuperarPath(ConstantesKeyStore.keyStorePathTest));
			logger.debug("pecaProcuracaoService.gerarPeca Assinou");*/
			Pessoa pessoaAutenticada = new Pessoa();
			pessoaAutenticada.setIdPessoa(usuarioAutenticadoId);
			Pessoa pessoaAutor = new Pessoa();
			pessoaAutor.setIdPessoa(usuarioAutor);
			
			logger.debug("pecaProcuracaoService.gerarPeca Antes de montarPecaEletronica");
			pecaEletronicaDTO = pecaEletronicaService.montarPecaEletronica(pessoaAutenticada, pessoaAutor, documento, 
					EnumProfilesDocumentEcm.getByDescricao(categoriaDoc.getDescricao()), 
					EnumModeloDocumentEcm.getByDescricao(modeloDocumento.getDescricao()),
					EnumSubCategoriaDocumentEcm.getByDescricao(subCategoriaDoc.getDescricao()));	
			logger.debug("pecaProcuracaoService.gerarPeca montou");
		} catch (Exception e) {
			throw new SefazException("Falha no processo de montagem da Peça Eletrônica Presencial. " + e.getMessage(), e); 
		}
		
		return pecaEletronicaDTO;	
	
	}
	

	public List<PecaEletronicaDto> gerarPecaEletronica(Integer usuarioHistorico, Integer usuarioAutor, List<DocumentoMovimentacaoPresencialDto> listaArquivosPecaEletronica) throws IOException, SefazException{		
		List<PecaEletronicaDto> listaPecaEletronicaDto = null; 
		if(listaArquivosPecaEletronica != null) {
			listaPecaEletronicaDto = new ArrayList<PecaEletronicaDto>();
			for (DocumentoMovimentacaoPresencialDto documentoMovimentacaoPresencialDto : listaArquivosPecaEletronica) {
				byte[] docContent = documentoMovimentacaoPresencialDto.getDocumento().getBytes();
				logger.debug("pecaProcuracaoService.gerarPecaEletronica ModeloDocumento " + documentoMovimentacaoPresencialDto.getModeloDocumento());
				PecaEletronicaDto  pecaEletronicaDto = gerarPeca(usuarioHistorico, usuarioAutor, docContent, documentoMovimentacaoPresencialDto.getModeloDocumento());
				listaPecaEletronicaDto.add(pecaEletronicaDto);
			}
		}	
		return listaPecaEletronicaDto;
	}
	
	public PecaProcuracao findPecaProcuracao(Integer idPecaEletronica){
		return this.pecaProcurcaoJpaRepository.findByPecaEletronicaId(idPecaEletronica);
	}
	
	public PecaProcuracao findPecaProcuracao(Integer idProcuracao, String descricaoModelo){
		return this.pecaProcurcaoJpaRepository.findByProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(idProcuracao, descricaoModelo);
	}
	
	public List<PecaProcuracao> findByPecaIdProcuracao(Integer idProcuracao){
		return this.pecaProcurcaoJpaRepository.findByProcuracaoId(idProcuracao);
	}
	
}