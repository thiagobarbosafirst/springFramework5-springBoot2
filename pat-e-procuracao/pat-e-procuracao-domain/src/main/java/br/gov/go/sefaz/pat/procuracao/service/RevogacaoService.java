package br.gov.go.sefaz.pat.procuracao.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.management.InvalidAttributeValueException;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.constants.ConstantesJasper;
import br.gov.go.sefaz.pat.constants.ConstantesKeyStore;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumProfilesDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumSubCategoriaDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.enumerator.EnumIndiPendenteAssinatura;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.RevogacaoProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.DocumentoMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.RevogacaoJpaRepository;
import br.gov.go.sefaz.pat.procuracao.support.RecuperadorPath;
import br.gov.go.sefaz.pat.procuracao.support.RelatorioRevogacaoPdf;
import br.gov.go.sefaz.pat.support.formatting.PatJasperReportSupport;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;
import net.sf.jasperreports.engine.JRException;

@Service
public class RevogacaoService {
	
	private static Logger logger = LogManager.getLogger(RevogacaoService.class);
	
	@Autowired
	private RevogacaoJpaRepository revogacaoJpaRepository;
	
	@Autowired
	private PecaRevogacaoService pecaRevogacaoService;
	
	@Autowired
	private RelatorioRevogacaoPdf relatorioRevogacaoPdf;
	
	@Autowired 
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService;
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	@Autowired
	private PecaProcuracaoService pecaProcuracaoService;
	
	@Autowired
	private RecuperadorPath recuperadorPath;
	
	private PatJasperReportSupport patJasperReportSupport = new PatJasperReportSupport();
	
	public RevogacaoProcuracao findOne(Integer idRevogacao){
		return this.revogacaoJpaRepository.findOne(idRevogacao);
	}
	
	@HistoryJpa
	@Transactional
	public void salvarRevogacaoEletronica(Integer usuarioHistorico, Integer[] detalhesRevogacao, RevogacaoProcuracao revogacao) throws SefazException {
		try {
				revogacao = criaRevogacao(revogacao);		
				revogacaoJpaRepository.save(revogacao);
				logger.debug(String.format("Salvando -> Revogacao ID: %s salvo com sucesso!", revogacao.getId()));
				
				salvarMovimentacaoProcuracaoEletronica(detalhesRevogacao, revogacao);							
		} catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu um problema ao salvar uma revogação. " + e.getMessage(), e);
		}
	}
	
	private RevogacaoProcuracao criaRevogacao(RevogacaoProcuracao revogacao) {	
		revogacao.setDataRevogacao(new Date());
		revogacao.setIndiPendenteAssinaturaDig(EnumIndiPendenteAssinatura.PENDENTE);
		revogacao.setIndiRevogacaoPresencial(EnumSimNao.N);
		return revogacao;
	}

	private void salvarMovimentacaoProcuracaoEletronica(Integer[] detalhesRevogacao, RevogacaoProcuracao revogacao) {
		MovimentacaoProcuracao movimentacaoProcuracao = null;				
		for (Integer saveDetalhesMovimentacao : detalhesRevogacao){
			movimentacaoProcuracao = movimentacaoProcuracaoService.find(saveDetalhesMovimentacao);
			movimentacaoProcuracao.setRevogacaoProcuracao(revogacao);
			movimentacaoProcuracaoService.saveAndFlush(movimentacaoProcuracao);
			logger.debug(String.format("Atualizando -> Movimentacao ID: %s salvo com sucesso!", movimentacaoProcuracao.getId()));
		}
	}
	
	//métodos da transação de update e peça eletrônica assinada 
	@HistoryJpa
	@Transactional
	public void ativarRevogacao(Integer usuarioHistorico, Integer idRevogacao, RevogacaoProcuracao revogacao, PecaEletronicaDto pecaEletronicaDTO, PecaEletronicaDto pecaTermoJuntada) throws SefazException{
		UcmDocument ucmDocument = null;
		try{
			//salva um novo documento no mesmo docName
			ucmDocument = pecaEletronicaEcmService.salvarNovaRevisaoUcm(usuarioHistorico, pecaEletronicaDTO);
			logger.debug(String.format("Salvando -> UcmDocument ID: %s salvo com sucesso!", ucmDocument.getId()));
			//atualiza status da revogacao no banco
			revogacao.setIndiPendenteAssinaturaDig(EnumIndiPendenteAssinatura.ASSINADO);
			revogacaoJpaRepository.save(revogacao);
			pecaRevogacaoService.savePecaRevogacaoAndUcmDocument(usuarioHistorico, idRevogacao, pecaTermoJuntada);
			logger.debug(String.format("Salvando -> Revogacao ID: %s salvo com sucesso!", revogacao.getId()));
			salvarDetalhesAtivarRevogacao(usuarioHistorico, idRevogacao);						
		}catch (Exception e) {
			if(ucmDocument != null) {
				pecaEletronicaEcmService.deleteRevision(ucmDocument);
				throw new SefazException(e.getMessage());
			}
		}
	}
	
	@HistoryJpa
	@Transactional
	private void salvarDetalhesAtivarRevogacao(Integer usuarioHistorico, Integer idRevogacao) throws SefazException {	
		MovimentacaoProcuracao movimentacaoProcuracao = null;		
		try {
			List<MovimentacaoProcuracao> listaRevogacoes = this.movimentacaoProcuracaoService.findRevogacao(idRevogacao);
			for(MovimentacaoProcuracao saveDetalhesNovaRevisao : listaRevogacoes){
				Procuracao procuracao = procuracaoService.find(saveDetalhesNovaRevisao.getProcuracao().getId());
				if(procuracao.getStatus().equals(EnumStatusProcuracao.Ativa.getCodigo())){
					movimentacaoProcuracao = movimentacaoProcuracaoService.find(saveDetalhesNovaRevisao.getId());
					movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.N);
					movimentacaoProcuracaoService.save(usuarioHistorico, movimentacaoProcuracao);
					logger.debug(String.format("Atualizando -> Movimentacao ID: %s salvo com sucesso!", movimentacaoProcuracao.getId()));
					procuracaoService.inativarProcuracaoSemProcurador(usuarioHistorico, saveDetalhesNovaRevisao.getProcuracao().getId(), EnumStatusProcuracao.Revogada.getCodigo());
					logger.debug(String.format("Atualizando -> Revogacao ID: %s salvo com sucesso!", procuracao.getId()));
				}
			}			
		} catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu um problema no update da revogação. " + e.getMessage(), e);
		}
	}
	
	//Metodos da transação da revogação presencial
	public RevogacaoProcuracao prepararRevogacaoPresencial() {
		RevogacaoProcuracao revogacao = new RevogacaoProcuracao();
		revogacao.setDataRevogacao(new Date());
		revogacao.setIndiPendenteAssinaturaDig(EnumIndiPendenteAssinatura.ASSINADO);
		revogacao.setIndiRevogacaoPresencial(EnumSimNao.S);				
		return revogacao;
	}
	
	public List<PecaEletronicaDto> criarPecasRevogacao(Integer usuarioHistorico, Integer usuarioAutor, RevogacaoProcuracao revogacao, 
			AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto) throws SefazException, IOException, Exception{
		
		List<DocumentoMovimentacaoPresencialDto> listaArquivosPecaEletronica = agrupadorMovimentacaoPresencialDto.getFiles();
		List<PecaEletronicaDto> listaPecaEletronicaDto = new ArrayList<PecaEletronicaDto>();
		
		List<MovimentacaoProcuracao> listaMovimentacoes = new ArrayList<MovimentacaoProcuracao>();
		MovimentacaoProcuracao movimentacaoProcuracao = null;
		for (Integer detalhe : agrupadorMovimentacaoPresencialDto.getMovimentacoes()) {
			movimentacaoProcuracao = movimentacaoProcuracaoService.find(detalhe);			
			listaMovimentacoes.add(movimentacaoProcuracao);
		}
		
		listaPecaEletronicaDto = pecaProcuracaoService.gerarPecaEletronica(usuarioHistorico, usuarioAutor, listaArquivosPecaEletronica);
		String pecas = procuracaoService.prepararPecaReciboJuntada(listaPecaEletronicaDto);
		listaPecaEletronicaDto.add(procuracaoService.gerarReciboDeJuntada(usuarioHistorico, usuarioAutor, listaMovimentacoes, pecas, "Recibo de Juntada da Revogação"));
		return listaPecaEletronicaDto;
	}
	
	@HistoryJpa
	@Transactional(rollbackOn={Exception.class,SefazException.class}) 
	public void salvarRevogacaoPresencial(Integer usuarioHistorico, Integer usuarioAutor, RevogacaoProcuracao revogacao, List<PecaEletronicaDto> listaPecaEletronicaDto, AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto)
			throws SefazException{		
		try {
			revogacaoJpaRepository.save(revogacao);
			logger.debug(String.format("Salvando -> Revogacao ID: %s salvo com sucesso!", revogacao.getId()));
			
			for (PecaEletronicaDto pecaEletronicaDto : listaPecaEletronicaDto) {
				pecaRevogacaoService.savePecaRevogacaoAndUcmDocument(usuarioHistorico, revogacao.getId(), pecaEletronicaDto); 
			}
		
			salvarDetalhesMovimentacaoPresencial(usuarioHistorico, agrupadorMovimentacaoPresencialDto, revogacao);						
		} catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu um problema ao salvar uma revogação presencial." + e.getMessage(), e);
		}			
	}	
	
	private void salvarDetalhesMovimentacaoPresencial(Integer usuarioHistorico, AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto, 
			RevogacaoProcuracao revogacao) throws SefazException {		
		MovimentacaoProcuracao movimentacaoProcuracao;
		try {
			for (Integer detalhe : agrupadorMovimentacaoPresencialDto.getMovimentacoes()) {
				movimentacaoProcuracao = movimentacaoProcuracaoService.find(detalhe);
				Procuracao procuracao = movimentacaoProcuracao.getProcuracao();
				if(procuracao.getStatus().equals(EnumStatusProcuracao.Ativa.getCodigo())){					
					movimentacaoProcuracao.setRevogacaoProcuracao(revogacao);
					movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.N);
					movimentacaoProcuracaoService.save(usuarioHistorico,movimentacaoProcuracao);
					logger.debug(String.format("Atualizando -> Movimentacao ID: %s salvo com sucesso!", movimentacaoProcuracao.getId()));
					procuracaoService.inativarProcuracaoSemProcurador(usuarioHistorico, procuracao.getId(), EnumStatusProcuracao.Revogada.getCodigo());
					logger.debug(String.format("Atualizando -> Revogacao ID: %s salvo com sucesso!", procuracao.getId()));
				}
			}			
		} catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu um problema ao salvar as movimentações da revogação presencial." + e.getMessage(), e);
		}
	}
	
	public PecaEletronicaDto gerarTermoRevogacao(Integer idUsuarioLogado, 
			Integer idRevogacao) throws IllegalAccessException, InvocationTargetException,
	NoSuchMethodException, UnrecoverableKeyException, InvalidAttributeValueException, KeyStoreException, NoSuchAlgorithmException, JRException, SefazException {
		
		InputStream reportStream = null;
		PecaEletronicaDto pecaEletronicaDTO = null;
		RevogacaoProcuracao revogacao = this.findOne(idRevogacao);
		PecaRevogacaoProcuracao pecaRevogacao = this.pecaRevogacaoService.consultarPecaRevogacaoPecaEletronica(revogacao.getId(), EnumModeloDocumentEcm.REVOGACAO_DA_PROCURACAO.getDescricaoModeloDocumento());
		try {
			
			if(pecaRevogacao != null && pecaRevogacao.getPecaEletronica() != null){
				pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaRevogacao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			}
			else {				
				Map<String, Object> parametros = relatorioRevogacaoPdf.parametrosTermoDeRevogacao(revogacao);
				reportStream = new FileInputStream(new File(recuperadorPath.recuperarPath(ConstantesJasper.jasperReportRevogacao))); 
				byte[] arquivoPDF = patJasperReportSupport.exportarRelatorio(reportStream, parametros,  patJasperReportSupport.setNovosDados());
				
				if (arquivoPDF != null) {			    	
					Pessoa pessoa = new Pessoa();
					pessoa.setIdPessoa(idUsuarioLogado);				
					pecaEletronicaDTO = pecaEletronicaService.montarPecaEletronica(pessoa, arquivoPDF, EnumProfilesDocumentEcm.TERMOS_E_PROCURACOES, 
							EnumModeloDocumentEcm.REVOGACAO_DA_PROCURACAO, EnumSubCategoriaDocumentEcm.PROCURACAO);
					logger.debug(String.format("Gerado -> Peca Eletronica Termo CHAVE: %s gerada com sucesso!", pecaEletronicaDTO.getChaveAcessoPecaEletronica()));
				}									
			}
		}catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu um problema ao gerar o documento no ECM. " + e.getMessage(), e);
		}		
		return pecaEletronicaDTO;		
	}
	
	public byte[] gerarMinuta(Integer[] detalhesRevogacao) throws SefazException{
		byte[] arquivoPDF = null;
		InputStream reportStream = null;
		try {
			Map<String, Object> parametros = relatorioRevogacaoPdf.parametrosMinuta(detalhesRevogacao);
			reportStream = new FileInputStream(new File(recuperadorPath.recuperarPath(ConstantesJasper.jasperReportRevogacao)));
			arquivoPDF = patJasperReportSupport.exportarRelatorio(reportStream, parametros,  patJasperReportSupport.setNovosDados());
			logger.debug("Gerado -> Peca Eletronica Minuta: gerada com sucesso!");
		} catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu algum problema ao carregar os parâmetros da minuta. " + e.getMessage(), e);
		}
		return arquivoPDF;
	}
	
	public boolean existeRenuncia(Integer[] ids){
		List<MovimentacaoProcuracao> detalhesProcuracao = movimentacaoProcuracaoService.findByIdIn(ids);				
		for (MovimentacaoProcuracao detalhe : detalhesProcuracao)
			if(detalhe.getRenunciaProcuracao() != null)
				return true;
		return false;
	}
	
	/**
	 * Retorna true se for nula
	 * @param idProcuracao
	 * @return
	 */
	public boolean revogacaoNula(Integer idProcuracao){
		List<MovimentacaoProcuracao> listaMovimentacoes = movimentacaoProcuracaoService.findProcuracao(idProcuracao);
		for(MovimentacaoProcuracao mp : listaMovimentacoes)
			if(mp.getRevogacaoProcuracao().getId() == null || mp.getRevogacaoProcuracao().getId() == 0) 
				return true;
		return false;
	}
	
	public boolean existeRevogacoes(String cnpjBase){		
		List<MovimentacaoProcuracao> listaMovimentacoes = movimentacaoProcuracaoService.findByPessoa(cnpjBase);
		for(MovimentacaoProcuracao mp : listaMovimentacoes) 
			if(mp.getRevogacaoProcuracao().getId() != null && mp.getRevogacaoProcuracao().getId() > 0) 
				return true;
		return false;
	}
	
	public boolean existeRevogacoes(Integer idPessoa){		 
		 List<MovimentacaoProcuracao>listaMovimentacoes = movimentacaoProcuracaoService.findByPessoa(idPessoa);
		for(MovimentacaoProcuracao mp : listaMovimentacoes) 
			if(mp.getRevogacaoProcuracao().getId() != null && mp.getRevogacaoProcuracao().getId() > 0) 
				return true;
		return false;
	}
	
	/**
	 * Controle de etapas do wizard para gerar peça eletrônica
	 * @param idRevogacao
	 * @return
	 */
	public Integer checkStepOfWizardRevogacao(Integer idRevogacao) {
		RevogacaoProcuracao indiPendenteAssinatura = this.findOne(idRevogacao);
				
		if(indiPendenteAssinatura == null) 
			return 0;				
		if(indiPendenteAssinatura.getIndiPendenteAssinaturaDigAsChar().equals('S')) 
			return 1; 		
		if(indiPendenteAssinatura.getIndiPendenteAssinaturaDigAsChar().equals('N'))
			return 2;
		
		return null;		
	}
}
