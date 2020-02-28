package br.gov.go.sefaz.pat.procuracao.service;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;

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
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.model.CategoriaDocumento;
import br.gov.go.sefaz.pat.model.ModeloDocumento;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.RenunciaProcuracao;
import br.gov.go.sefaz.pat.model.SubCategoriaDocumento;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusAssinaturaDigital;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.DocumentoMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.MovimentacaoProcuracaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.MovimentacaoProcuracaoJpaRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.RenunciaProcuracaoJpaRepository;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;

@Service
public class RenunciaProcuracaoService {
	
	@Autowired
	private MovimentacaoProcuracaoJdbcRepository movimentacaoProcuracaoJdbcRepository;	
	
	@Autowired
	private RenunciaProcuracaoJpaRepository renunciaProcuracaoJpaRepository;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private MovimentacaoProcuracaoJpaRepository movimentacaoProcuracaoJpaRepository;
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService; 
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService;
	
	@Autowired
	private ModeloDocumentoService modeloDocumentoService;
	
	@Autowired
	private PecaRenunciaProcuracaoService pecaRenunciaProcuracaoService; 
	
	@Autowired
	private ProcuracaoService procuracaoService; 
	
	@HistoryJpa
	@Transactional
	public void salvar(Integer idUsuarioHistorico,RenunciaProcuracao renuncia) {
		renunciaProcuracaoJpaRepository.save(renuncia);
	}
	
	@HistoryJpa
	@Transactional
	public void ativarRenunciaEletronica(Integer usuarioHistorico,RenunciaProcuracao renuncia, PecaEletronicaDto pecaEletronicaDTO) 
			throws InvalidAttributeValueException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SefazException{
		
		UcmDocument ucmDocument = null;
		
		try 
		{
			ucmDocument = pecaEletronicaEcmService.salvarNovaRevisaoUcm(usuarioHistorico, pecaEletronicaDTO);
						
			List<MovimentacaoProcuracao> detalhes = movimentacaoProcuracaoJpaRepository.findByRenunciaProcuracaoId(renuncia.getId());
							
			renuncia.setPendenteAssinaturaDigital(EnumStatusAssinaturaDigital.Assinado.getCodigo());
			renunciaProcuracaoJpaRepository.saveAndFlush(renuncia);
			MovimentacaoProcuracao detalheEdit = null;
			
			for (MovimentacaoProcuracao detalhe : detalhes) {			
				detalheEdit = movimentacaoProcuracaoJpaRepository.findOne(detalhe.getId());
				detalheEdit.setIndiMovmtProcuracaoAtiva(EnumSimNao.N);
				movimentacaoProcuracaoJpaRepository.saveAndFlush(detalheEdit);
			}
			
			this.inativarProcuracaoAusenciaProcuradores(usuarioHistorico, renuncia.getId());
			
		} catch (Exception e) {
			if(ucmDocument != null) {
				pecaEletronicaEcmService.deleteRevision(ucmDocument); 
			}
		}
		
	}
	
	@HistoryJpa
	@Transactional
	public Integer criarRenunciaEletronica(Integer idUsuarioCadastrador,Integer[] idsMovimentacao) 
			throws SefazException{
		
		try {			
			//EnumSimNao.N -> Renúncia Eletrônica.			
			RenunciaProcuracao renuncia = criarSalvarObjetoRenuncia(EnumSimNao.N);				
			vincularMovimentacoesRenuncia(idUsuarioCadastrador,idsMovimentacao,renuncia);			
			return renuncia.getId();
			
		} catch (Exception e) {
			String mensagem = "Falha no processo de salvamento da renúncia ou algum problema nas movimentações. Verifique e tente novamente.";
			throw new SefazException((e instanceof SefazException) ? e.getMessage() : mensagem);
		}	
	}
	
	@HistoryJpa
	@Transactional(rollbackOn={Exception.class,SefazException.class}) 
	public RenunciaProcuracao renunciaPresencial(Integer idUsuarioCadastrador,AgrupadorMovimentacaoPresencialDto renuncia,ServletContext context)
			throws SefazException{	
		
		try {
		
			byte[] docContent = null;
			
			RenunciaProcuracao renunciaObj = criarSalvarObjetoRenuncia(EnumSimNao.S);
			
			Integer[] movimentacoes = new Integer[renuncia.getMovimentacoes().size()];
			renuncia.getMovimentacoes().toArray(movimentacoes);	
			
			vincularMovimentacoesRenuncia(idUsuarioCadastrador,movimentacoes,renunciaObj);
			
			for (DocumentoMovimentacaoPresencialDto documento : renuncia.getFiles()) {								
				docContent = documento.getDocumento().getBytes();
				PecaEletronicaDto  pecaEletronicaDto = criarPecaEletronicaAdm(context, idUsuarioCadastrador,docContent,documento.getModeloDocumento(),renuncia.getProcurador());
				pecaRenunciaProcuracaoService.savePecaRenunciaAndUcmDocument(idUsuarioCadastrador, renunciaObj.getId(), pecaEletronicaDto);
			}	
			
			this.inativarProcuracaoAusenciaProcuradores(idUsuarioCadastrador, renunciaObj.getId());
			
			return renunciaObj;
			
		} catch (Exception e) {
			throw new SefazException(e.getMessage());
		}
			
	}

	private List<MovimentacaoProcuracao> vincularMovimentacoesRenuncia(Integer idUsuarioCadastrador,
			Integer[] movimentacoes, RenunciaProcuracao renunciaObj) throws SefazException{
				
		try {
			
			//EnumSimNao.S -> Renuncia Presencial.
			
			List<MovimentacaoProcuracao> detalhes =  movimentacaoProcuracaoService.findByIdIn(movimentacoes);	
					
			for (MovimentacaoProcuracao movimentoProcuracao : detalhes) {				
				movimentoProcuracao.setRenunciaProcuracao(renunciaObj);
				
				//Renúncia Presencial -> Inativar movimentação da procuração no momento da vinculação.
				if(renunciaObj.getIndiceRenunciaPresencial().equalsIgnoreCase("S"))
					movimentoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.N);
				
				movimentacaoProcuracaoService.save(idUsuarioCadastrador,movimentoProcuracao);
			}		
			
			return detalhes;
			
		} catch (Exception e) {
			throw new SefazException("Um problema aconteceu ao tentar salvar as movimentações. Verifique e tente novamente.");
		}

	}
	
	/**
	 * Cria o objeto de renuncia de procuração e o salva no banco de dados
	 * @param tipoRenuncia Informe o tipo da renuncia EnumSimNao.S para indicar uma renúncia presencial e
	 *  EnumSimNao.N para uma renúncia eletrônica.
	 * @return Objeto que foi criado e persistido.
	 * @throws SefazException
	 */
	private RenunciaProcuracao criarSalvarObjetoRenuncia(EnumSimNao tipoRenuncia) throws SefazException{
		try {
			RenunciaProcuracao renunciaObj = new RenunciaProcuracao();
			renunciaObj.setDataRenuncia(new Date());
			
			//S -> Presencial; N => Eletrônica
			String pendenteAssinatura = EnumSimNao.N.equals(tipoRenuncia) 
					? EnumStatusAssinaturaDigital.PendenteAssinatura.getCodigo() 
					: EnumStatusAssinaturaDigital.Assinado.getCodigo();
					
			renunciaObj.setPendenteAssinaturaDigital(pendenteAssinatura);
			renunciaObj.setIndiceRenunciaPresencial(tipoRenuncia.getValor().toString());
			
			renunciaProcuracaoJpaRepository.saveAndFlush(renunciaObj);
			return renunciaObj;
		} catch (Exception e) {
			throw new SefazException("Um problema ocorreu impedindo a criação de uma nova renúncia.");
		}
	}	
	
	/**
	 * Verifica se o procurador é correspondente para todas as movimentações de procuração selecionadas.
	 * @param renuncia Objeto do tipo RenunciaCadastroDTO com as movimentações e o ID do procurador.
	 * @return True caso não haja inconsistência e False caso o procurador não seja correspondente.
	*/
	public boolean checkProcuradorMovimentacao(AgrupadorMovimentacaoPresencialDto renuncia){
		
		Pessoa procurador = null;
		
		Integer[] movimentacoesArray = new Integer[renuncia.getMovimentacoes().size()]; 
		renuncia.getMovimentacoes().toArray(movimentacoesArray);
		
		List<MovimentacaoProcuracao> movimentacoes = movimentacaoProcuracaoService.findByIdIn(movimentacoesArray);	
		
		for (MovimentacaoProcuracao item : movimentacoes) {					
			procurador = item.getProcurador().getPessoa();

			if(!procurador.getIdPessoa().equals(renuncia.getProcurador()))
				return false;
		}
		
		return true;
	}
	
	public void inativarProcuracaoAusenciaProcuradores(Integer idUsuarioAutenticado, Integer idRenuncia) throws SefazException{	
		
		List<MovimentacaoProcuracao> movimentacoes = movimentacaoProcuracaoService.findByMovimentacaoProcuracaoByRenunciaId(idRenuncia);	
		
		for (MovimentacaoProcuracao movimentacao : movimentacoes) {
			if(movimentacao.getProcuracao().getStatus().equals("1"))
				procuracaoService.inativarProcuracaoSemProcurador(idUsuarioAutenticado, movimentacao.getProcuracao().getId(),  
						EnumStatusProcuracao.RenunciaRepresentantes.getCodigo());
		}
	}
	
	public PecaEletronicaDto criarPecaEletronicaAdm(ServletContext context, Integer usuarioAutenticadoId, 
			byte[] documento, Integer idModeloDocumento, Integer idUsuarioAutor) throws SefazException{
		
		PecaEletronicaDto pecaEletronicaDTO = null;	
		
		try {
			
			//Deve ter o conteúdo do documento.
			if(documento == null || documento.length <= 0)
				throw new Exception();
				
			//Procurar o modelo do documento.
			ModeloDocumento modeloDocumento = modeloDocumentoService.findOne(idModeloDocumento);
			SubCategoriaDocumento subCategoriaDoc = modeloDocumento.getSubCategoriaDocumento();
			CategoriaDocumento  categoriaDoc = subCategoriaDoc.getCategoriaDocumento();
			
			Pessoa pessoaCadastro = new Pessoa();
			pessoaCadastro.setIdPessoa(usuarioAutenticadoId);
			
			Pessoa pessoaAutor = new Pessoa();
			pessoaAutor.setIdPessoa(idUsuarioAutor);
					
			pecaEletronicaDTO = pecaEletronicaService.montarPecaEletronica(pessoaCadastro,pessoaAutor,documento, 
					EnumProfilesDocumentEcm.getByDescricao(categoriaDoc.getDescricao()), 
					EnumModeloDocumentEcm.getByDescricao(modeloDocumento.getDescricao()),
					EnumSubCategoriaDocumentEcm.getByDescricao(subCategoriaDoc.getDescricao()));	
				
		} catch (Exception e) {
			throw new SefazException("Falha no processo de montagem da Peça Eletrônica. Verifique os documentos cadastrados e tente novamente."); 
		}
		
		return pecaEletronicaDTO;	
	
	}
	
	public Integer checkStepOfRenuncia(Integer id) {
		RenunciaProcuracao status = renunciaProcuracaoJpaRepository.findOne(id);
		
		String pendenteAssinatura = EnumStatusAssinaturaDigital.PendenteAssinatura.getCodigo();
		String documentoAssinado = EnumStatusAssinaturaDigital.Assinado.getCodigo();
				
		//Não existe renuncia;
		if(status == null) return 0;
				
		//Existe a peça eletrônica, mas ela NÃO está assinada.
		if(status.getPendenteAssinaturaDigital().equals(pendenteAssinatura)) return 1; 
		
		//Existem renuncia, COM peça eletrônica assinada.
		if(status.getPendenteAssinaturaDigital().equals(documentoAssinado)) return 2;
		
		return null;		
	}
	
	/**
	 * Consultar itens para RENUNCIAR. Processos, sujeitos e procurações. 
	*/
	public List<AgrupadorMovimentacaoDto> findRenunciarByIdPessoaProcurador(Integer idPessoa) {
		return this.movimentacaoProcuracaoJdbcRepository.findRenunciarByIdPessoaProcurador(idPessoa);
	}	
	
	/**
	 * Consultar itens para RENUNCIAR. Processos, sujeitos e procurações por ID Pessoa do Procurador e ID Procuração. 
	*/
	public List<AgrupadorMovimentacaoDto> findRenunciarByIdPessoaProcuradorAndProcuracao(Integer idPessoa, Integer idProcuracao) {
		return this.movimentacaoProcuracaoJdbcRepository.findRenunciarByIdPessoaProcuradorAndProcuracao(idPessoa,idProcuracao);
	}
	
	/**
	 * Consultar itens JÁ RENUNCIADOS.  
	*/
	public List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcurador(Integer idPessoa) {
		return this.movimentacaoProcuracaoJdbcRepository.findRenunciadosByIdPessoaProcurador(idPessoa);
	}
	
	/**
	 * Consultar itens JÁ RENUNCIADOS por ID Pessoa Procurador e Status Assinatura Documento.  
	*/
	public List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcuradorAndStatusAssinatura(Integer idPessoa, String pendenteAssinatura) {
		return this.movimentacaoProcuracaoJdbcRepository.findRenunciadosByIdPessoaProcuradorAndStatusAssinatura(idPessoa,pendenteAssinatura);
	}
	
	/**
	 * Consultar itens JÁ RENUNCIADOS por CNPJ Base.  
	*/
	public List<AgrupadorMovimentacaoDto> findRenunciadosByIdPessoaProcuradorPJ(String cnpjBase) {
		return this.movimentacaoProcuracaoJdbcRepository.findRenunciadosByIdPessoaProcuradorPJ(cnpjBase);
	}
	
	/**
	 * Consultar itens JÁ RENUNCIADOS - TODOS.  
	*/
	public List<AgrupadorMovimentacaoDto> findTodosRenunciados() {
		return this.movimentacaoProcuracaoJdbcRepository.findTodosRenunciados();
	}
	
	public RenunciaProcuracao findRenunciaById(Integer id){
		return renunciaProcuracaoJpaRepository.findOne(id);
	}
	
	/**
	 * Verificar a existência de revogação cadastrada para as movimentação de procuração selecionado.
	 * @param ids Array com os códigos das movimentações de procuração.
	 * @return True caso exista algum item com revogação cadastrada.
	 */
	public boolean existsRevogacao(Integer[] ids){
		List<MovimentacaoProcuracao> detalhesProcuracao = movimentacaoProcuracaoService.findByIdIn(ids);				
		for (MovimentacaoProcuracao detalhe : detalhesProcuracao)
			if(detalhe.getRevogacaoProcuracao() != null)
				return true;
		
		return false;
	}
	
}
