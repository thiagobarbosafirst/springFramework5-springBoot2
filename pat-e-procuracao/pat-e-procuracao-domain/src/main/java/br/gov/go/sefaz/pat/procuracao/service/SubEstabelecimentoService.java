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
import java.util.List;
import java.util.Map;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.support.Data;
import br.gov.go.sefaz.javaee.corporativo.model.DocumentoPessoaId;
import br.gov.go.sefaz.javaee.corporativo.model.Endereco;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEnderecoPK;
import br.gov.go.sefaz.javaee.corporativo.model.TipoDocumento;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.javaee.security.dto.PessoaAutenticadaDto;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumProfilesDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumSubCategoriaDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;
import br.gov.go.sefaz.pat.model.EnderecoProcurador;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaSubstabelecimento;
import br.gov.go.sefaz.pat.model.ProcessoAdministrativoTributarioEletronico;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusSubstabelecimento;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEndereco;
import br.gov.go.sefaz.pat.procuracao.model.dto.DocumentoMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuradorDto;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.SubEstabelecimentoJpaRepository;
import br.gov.go.sefaz.pat.service.PatEnderecoService;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;
import br.gov.go.sefaz.pat.support.formatting.PatJasperReportSupport;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;

@Service
public class SubEstabelecimentoService {
	
	private static Logger logger = LogManager.getLogger(SubEstabelecimentoService.class);
	
	@Autowired
	private SubEstabelecimentoJpaRepository subEstabelecimentoJpaRepository;
	
	@Autowired
	PatSujeitoPassivoProcessoService patSujeitoPassivoProcessoService; 
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private TelefoneService telefoneService;
	
	@Autowired
	private DocumentoService documentoService;
	
	@Autowired
	private EnderecoPessoaService enderecoPessoaService;
	
	@Autowired
	private ProcuradorService procuradorService;
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoService;
	
	@Autowired
	private PatEnderecoService enderecoService;	

	@Autowired
	private PecaSubstabelecimentoService pecaSubstabelecimentoService;
	
	@Autowired
	private PecaProcuracaoService pecaProcuracaoService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService;
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;	
	
	@Autowired
	private ProcuracaoService procuracaoService;
	
	@Autowired
	private ComplementoProcuradorService complementoProcuradorService;
	
	@Autowired
	private EnderecoProcuradorService enderecoProcuradorService;
	
	public void prepararSubstabelecimento(SubEstabelecimentoProcuracao subEstabelecimentoProcuracao) throws SefazException{
		procuradorService.prepararProcurador(subEstabelecimentoProcuracao.getListaMovimentacaoProcuracao());		
		prepararReservaPoderesSubstabelecimento(subEstabelecimentoProcuracao);  
	}
	
	public List<PecaEletronicaDto> criarPecasSubstabelecimento(Integer usuarioLogado, Integer usuarioAutor, SubEstabelecimentoProcuracao subEstabelecimentoProcuracao, List<DocumentoMovimentacaoPresencialDto> listaArquivosPecaEletronica) throws SefazException, IOException, Exception{
		List<PecaEletronicaDto> listaPecaEletronicaDto = new ArrayList<PecaEletronicaDto>();
		if(subEstabelecimentoProcuracao.getIndiSubstabelecimentoPresencial().equals('S')) {
			logger.debug("pecaProcuracaoService.gerarPecaEletronica: Antes de gerar listaArquivosPecaEletronica.size " + listaArquivosPecaEletronica == null ? 0 : listaArquivosPecaEletronica.size());
			listaPecaEletronicaDto = pecaProcuracaoService.gerarPecaEletronica(usuarioLogado, usuarioAutor, listaArquivosPecaEletronica);
			logger.debug("pecaProcuracaoService.gerarPecaEletronica: Depois de gerar listaPecaEletronicaDto.size " + listaPecaEletronicaDto == null ? 0 : listaPecaEletronicaDto.size());
			String peca = procuracaoService.prepararPecaReciboJuntada(listaPecaEletronicaDto);
			logger.debug("pecaProcuracaoService.gerarPecaEletronica: Antes de gerarReciboDeJuntada listaPecaEletronicaDto.size " + listaPecaEletronicaDto == null ? 0 : listaPecaEletronicaDto.size());
			listaPecaEletronicaDto.add(procuracaoService.gerarReciboDeJuntada(usuarioLogado, usuarioAutor, subEstabelecimentoProcuracao.getListaMovimentacaoProcuracao(), peca, "Termo de Juntada Substabelecimento"));
			logger.debug("pecaProcuracaoService.gerarPecaEletronica: Depois de gerarReciboDeJuntada listaPecaEletronicaDto.size " + listaPecaEletronicaDto == null ? 0 : listaPecaEletronicaDto.size());
			return listaPecaEletronicaDto;
		}
		
		return listaPecaEletronicaDto;
	}
	
	@HistoryJpa
	@Transactional
	public void saveSubstabelecimento(Integer usuarioHistorico, PessoaAutenticadaDto pessoaAutenticada, SubEstabelecimentoProcuracao subEstabelecimentoProcuracao, String matriculaUsuarioGestor, List<PecaEletronicaDto> listaPecaEletronicaDto) throws SefazException, IOException, Exception{
		
		subEstabelecimentoJpaRepository.save(subEstabelecimentoProcuracao);	
		
		for (PecaEletronicaDto pecaEletronicaDto : listaPecaEletronicaDto) {
			pecaSubstabelecimentoService.savePecaSubstabelecimentoAndUcmDocument(usuarioHistorico, subEstabelecimentoProcuracao.getId(), pecaEletronicaDto); 
		}
		
		boolean semReservaPoderes = false;
		if(EnumReservaPoderPermitido.SomenteSemReserva.getIndiReservaPermitido().equals(subEstabelecimentoProcuracao.getIndiReserva())) {
			semReservaPoderes = true;
		}
		
		List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = subEstabelecimentoProcuracao.getListaMovimentacaoProcuracao();
		for (MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacaoProcuracao) {			
			
			if(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa() != null && movimentacaoProcuracao.getProcurador().getListaComplementoPessoa().size() > 0) {
				complementoProcuradorService.saveComplementoProcurador(matriculaUsuarioGestor, movimentacaoProcuracao.getProcurador().getListaComplementoPessoa()); 
			}					
			
			Procurador procurador = procuradorService.consultar(movimentacaoProcuracao.getProcurador().getPessoa());
			if(procurador != null) {
				movimentacaoProcuracao.getProcurador().setId(procurador.getId());
			}
			
			procuradorService.save(pessoaAutenticada.getId(), movimentacaoProcuracao.getProcurador());
			
			EnderecoProcurador enderecoProcuradorConsulta = enderecoProcuradorService.consultar(movimentacaoProcuracao.getProcurador().getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
			
			if(enderecoProcuradorConsulta != null) {					
				Endereco enderecoProcurador = movimentacaoProcuracao.getProcurador().getEnderecoProcurador().getEndereco();	
				Endereco endereco = enderecoProcuradorConsulta.getEndereco();
				endereco.setLogradouro(enderecoProcurador.getLogradouro());
				endereco.setNumero(enderecoProcurador.getNumero());
				endereco.setNumeroLote(enderecoProcurador.getNumeroLote());
				endereco.setNumeroQuadra(enderecoProcurador.getNumeroQuadra());
				endereco.setComplemento(enderecoProcurador.getComplemento());
				movimentacaoProcuracao.getProcurador().setEnderecoProcurador(enderecoProcuradorConsulta);					
			}
			
			movimentacaoProcuracao.getProcurador().getEnderecoProcurador().setProcurador(movimentacaoProcuracao.getProcurador());
			enderecoProcuradorService.save(usuarioHistorico, matriculaUsuarioGestor, movimentacaoProcuracao.getProcurador().getEnderecoProcurador()); 
			
			movimentacaoProcuracao.setSubEstabelecimentoProcuracao(subEstabelecimentoProcuracao);  
			movimentacaoService.saveAndFlush(pessoaAutenticada.getId(), movimentacaoProcuracao);	 
			
			//Inativa a movimentacao de origem caso seja sem reserva de poderes
			if(semReservaPoderes) {
				movimentacaoService.save(pessoaAutenticada.getId(), movimentacaoProcuracao.getMovimentacaoOrigemProcuracao());	
			}
			
		} 
		
	}
	
	public String prepararPecaReciboJuntada(SubEstabelecimentoProcuracao subsEstabelecimentoProcuracao) {
		
		List<PecaSubstabelecimento> listaPecaSubestabelecimento = pecaSubstabelecimentoService.findByPecaIdSubEstabelecimentoProcuracao(subsEstabelecimentoProcuracao.getId());
		int contador = 0;
		StringBuilder peca = new StringBuilder();		
		for (PecaSubstabelecimento pecaSubstabelecimento : listaPecaSubestabelecimento) {
			if(contador > 0 && (contador + 1) == listaPecaSubestabelecimento.size()) {
				peca.append(" e ");
			} else if(contador > 0) {
				peca.append(", ");
			}			
			peca.append(pecaSubstabelecimento.getPecaEletronica().getNomePecaEletronica());	
			contador++;
		}
		
		return peca.toString();
	}
	
	private void prepararReservaPoderesSubstabelecimento(SubEstabelecimentoProcuracao subEstabelecimentoProcuracao) {
		if(EnumReservaPoderPermitido.SomenteSemReserva.getIndiReservaPermitido().equals(subEstabelecimentoProcuracao.getIndiReserva())) {
			for (MovimentacaoProcuracao movimentacaoProcuracao : subEstabelecimentoProcuracao.getListaMovimentacaoProcuracao()) {
				MovimentacaoProcuracao movimentacaoOrigem = movimentacaoService.find(movimentacaoProcuracao.getMovimentacaoOrigemProcuracao().getId());
				movimentacaoOrigem.setIndiMovmtProcuracaoAtiva(EnumSimNao.N);
				movimentacaoProcuracao.setMovimentacaoOrigemProcuracao(movimentacaoOrigem);	
				//saveMovimentacaoOrgem
			}
		}
		
	}	
	
	public PecaEletronicaDto getTermoJuntada(SubEstabelecimentoProcuracao subEstabelecimentoProcuracao) throws SefazException{ 
		
		PecaEletronicaDto pecaEletronicaDto = null;
	
		PecaSubstabelecimento pecaSubstabelecimento = pecaSubstabelecimentoService.findPecaSubstabelecimento(subEstabelecimentoProcuracao.getId(), EnumModeloDocumentEcm.REVOGACAO_DA_PROCURACAO.getDescricaoModeloDocumento());
		try {
			
			if(pecaSubstabelecimento != null && pecaSubstabelecimento.getPecaEletronica() != null){
				pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaSubstabelecimento.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			}
			
		} catch (Exception e) {
			throw new SefazException("Ocorreu um problema ao gerar o documento no ECM. " + e.getMessage(), e);
		}
		
		return pecaEletronicaDto;
		
	}
	
	public Integer checkStepOfSubstabelecimento(Integer id) {
		Character statusSubestabelecimento = subEstabelecimentoJpaRepository.findSubstabelecimentoById(id);
		
		if(statusSubestabelecimento == null) return 0;
		if(statusSubestabelecimento == EnumStatusSubstabelecimento.Incompleto.getCodigo()) return 1;  
		if(statusSubestabelecimento == EnumStatusSubstabelecimento.Completo.getCodigo()) return 2;
		return null;
	}
	
	public SubEstabelecimentoProcuracao buscaSubEstabelecimento(Integer id_substab){
		return subEstabelecimentoJpaRepository.buscaSubEstabelecimento(id_substab);
	}
	
	public SubEstabelecimentoProcuracao find(Integer id) {
		return this.subEstabelecimentoJpaRepository.findOne(id);
	}
	
	/**
	 * Gera e assina digitalmente um arquivo jasper
	 * @param context
	 * @param usuarioAutenticadoDetails
	 * @return
	 * @throws InvalidAttributeValueException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SefazException
	 * @throws UnrecoverableKeyException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws JRException
	 */
	public PecaEletronicaDto criarPecaEletronica(ServletContext context, UsuarioAutenticadoDetails usuarioAutenticadoDetails, 
			Integer idSubstabelecimento) throws IllegalAccessException, InvocationTargetException,
	NoSuchMethodException, SefazException, UnrecoverableKeyException, InvalidAttributeValueException, KeyStoreException, NoSuchAlgorithmException, JRException {
		
		InputStream reportStream = null;
		PecaEletronicaDto pecaEletronicaDTO = null;
		SubEstabelecimentoProcuracao subEstabelecimentoProcuracao = this.find(idSubstabelecimento); 
		PecaSubstabelecimento pecaSubstabelecimento = this.pecaSubstabelecimentoService.findPecaSubstabelecimento(subEstabelecimentoProcuracao.getId(), EnumModeloDocumentEcm.SUBESTABELECIMENTO_DA_PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		PatJasperReportSupport patJasperReportSupport = new PatJasperReportSupport();
		try {
			
			if(pecaSubstabelecimento != null && pecaSubstabelecimento.getPecaEletronica() != null){
				pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaSubstabelecimento.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			}
			else {
				logger.debug("SubEstabelecimentoService.criarPecaEletronica antes de retornarDadosPDFSubstabelecimento");
				ModelMap modelMap = retornarDadosPDFSubstabelecimento(context, subEstabelecimentoProcuracao);
				logger.debug("SubEstabelecimentoService.criarPecaEletronica depois de retornarDadosPDFSubstabelecimento");
				reportStream = new FileInputStream(new File(context.getRealPath("jasper/substabelecimento-report.jasper"))); 
				logger.debug("SubEstabelecimentoService.criarPecaEletronica depois de criar Jasper Subestabelecimento");
				byte[] arquivoPDF = patJasperReportSupport.exportarRelatorio(reportStream, modelMap,  patJasperReportSupport.setNovosDados());
				logger.debug("SubEstabelecimentoService.criarPecaEletronica depois de criar byte");
				
				if (arquivoPDF != null) {			    	
					Pessoa pessoa = new Pessoa();
					pessoa.setIdPessoa(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());
					logger.debug("SubEstabelecimentoService.criarPecaEletronica antes de montarPecaEletronica");
					pecaEletronicaDTO = pecaEletronicaService.montarPecaEletronica(pessoa, arquivoPDF, EnumProfilesDocumentEcm.TERMOS_E_PROCURACOES, EnumModeloDocumentEcm.SUBESTABELECIMENTO_DA_PROCURACAO_ELETRONICA, EnumSubCategoriaDocumentEcm.PROCURACAO);			
				}									
			}
		}catch (Exception e) {
			throw new SefazException("Ocorreu um erro ao gerar uma Peça Eletronica. " + e);
		}		
		return pecaEletronicaDTO;		
	}
	
	private ModelMap retornarDadosPDFSubstabelecimento(ServletContext context, SubEstabelecimentoProcuracao subEstabelecimentoProcuracao) {
		
		String caminhoLogoSefaz					=	"resources/images/sefazgo-logo.png";
		String caminhoHeaderJasper				=	"jasper/header.jasper"; 
		String caminhoFooterJasper				=	"jasper/footer.jasper";
		String titulo				   			= 	"Termo de Substabelecimento da Procuração";
		
		FormatSupport formatSupport = new FormatSupport();	 	
		String cnpjMF = ", CNPJ/MF sob nº ";
		String cpfMF = ", CPF/MF sob nº ";
		String oab = ", OAB/UF nº ";
		String endereco = ", com endereço profissional à ";
		
		String processos = "";
		
		String sujeitoPassivoProcuracao = "";
		
		String outorgante = "";
		
		String dataLimite = "";
		
		int contador = 0;
		
		String procuradores = "";
		
		List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = movimentacaoProcuracaoService.findBySubstabelecimento(subEstabelecimentoProcuracao.getId());
		
		for(MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacaoProcuracao) {
			if (contador > 0 && (contador + 1) == listaMovimentacaoProcuracao.size()) {
				sujeitoPassivoProcuracao += " e ";
				procuradores += " e ";
			} else if (contador > 0) {
				sujeitoPassivoProcuracao += ", ";
				procuradores += ", ";
			}
			if (movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaFisica() != null) {
				sujeitoPassivoProcuracao += movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaFisica().getNome()
						+ cnpjMF
						+ formatSupport.formatarCPF(movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaFisica().getNumeroCpf());
			} else {
				sujeitoPassivoProcuracao += movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaJuridica()
						.getNomeFantasia() + cnpjMF
						+ formatSupport.formatarCNPJ(movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaJuridica().getNumeroCnpj());
			}
			
			if(contador == 0) {
				Procurador procurador = movimentacaoProcuracao.getMovimentacaoOrigemProcuracao().getProcurador();
				EnderecoProcurador enderecoProcurador = enderecoProcuradorService.consultar(movimentacaoProcuracao.getProcurador().getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
				String enderecoFormatado = new FormatSupport().formatEndereco(enderecoProcurador.getEndereco());
				outorgante = procurador.getPessoa().getPessoaFisica().getNome() + cnpjMF +  formatSupport.formatarCPF(procurador.getPessoa().getPessoaFisica().getNumeroCpf()) + endereco + enderecoFormatado;
			}
			
			DocumentoPessoaId documentoPessoaId = new DocumentoPessoaId();			
			documentoPessoaId.setPessoa(movimentacaoProcuracao.getProcurador().getPessoa());
			TipoDocumento tipoDocumentoPessoa = new TipoDocumento();
			tipoDocumentoPessoa.setIdTipoDocumento(11); // tipo documento advogado
			documentoPessoaId.setTipoDocumento(tipoDocumentoPessoa);
			DocumentoAdvogado documentoPessoaConsulta = documentoService.consultar(documentoPessoaId);				
			
			
			EnderecoProcurador enderecoProcurador = enderecoProcuradorService.consultar(movimentacaoProcuracao.getProcurador().getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
			
			if(movimentacaoProcuracao.getProcurador().getPessoa().getPessoaFisica() != null) {
				procuradores += movimentacaoProcuracao.getProcurador().getPessoa().getPessoaFisica().getNome() + oab + documentoPessoaConsulta.getNumeroDocumento() + cpfMF + formatSupport.formatarCPF(movimentacaoProcuracao.getProcurador().getPessoa().getPessoaFisica().getNumeroCpf()) + endereco + new FormatSupport().formatEndereco(enderecoProcurador.getEndereco());
			}	
			
			subEstabelecimentoProcuracao = movimentacaoProcuracao.getSubEstabelecimentoProcuracao();
			
			SujeitoPassivoProcesso sujeitoPassivoProcesso = movimentacaoProcuracao.getSujeitoPassivoProcesso();
			
			processos += (sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getTipoDocumentoOrigem() == null ? "" :  sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getTipoDocumentoOrigem()) + "" + sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getNumeroSequencial() + "" + sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getNumeroDigitoVerificador();
			
			contador++;
		}	
		
		String reservaPoderes = "";
		
		if(!subEstabelecimentoProcuracao.getIndiReserva().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
			reservaPoderes = ", sem reserva de poderes";
		} 
		
		for(EnumReservaPoderPermitido enumReservaPoderPermitido : EnumReservaPoderPermitido.values()) {
			if(subEstabelecimentoProcuracao.getIndiReserva().equals(enumReservaPoderPermitido.getIndiReservaPermitido())) {
				reservaPoderes = ", " + enumReservaPoderPermitido.getSignificadoReservaPermitido().toLowerCase();				
				if(!subEstabelecimentoProcuracao.getIndiReserva().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
					dataLimite = " Podendo ainda o substabelecido, substabelecer os poderes a outrem";
					if(subEstabelecimentoProcuracao.getDataLimite() != null) {
						dataLimite += ", até a data limite de " + Data.formatar(subEstabelecimentoProcuracao.getDataLimite(), Data.DATA_PADRAO);
					}
					dataLimite += ".";
				}	
			}
		}	
		
		String prazoValidade = subEstabelecimentoProcuracao.getDataValidade() == null ? "" : Data.formatar(subEstabelecimentoProcuracao.getDataValidade(), Data.DATA_PADRAO);
		
		String dataExtenso = "Goiânia, " + formatSupport.retornarDataFormatada(); 
		
		ModelMap modelMap =  new ModelMap();			
		modelMap.put("sujeitosPassivos", sujeitoPassivoProcuracao); 
		modelMap.put("outorgantes", outorgante); 
		modelMap.put("procuradores", procuradores); 
		modelMap.put("processo", processos);
		modelMap.put("reservaPoderes", reservaPoderes);
		modelMap.put("prazoValidade", prazoValidade == null || prazoValidade.equals("") ? "" : "O presente substabelecimento terá o prazo de validade até " + prazoValidade + ".");
		modelMap.put("dataLimite", dataLimite);
		modelMap.put("dataAtual", dataExtenso);
		modelMap.put("datasource", new JREmptyDataSource());
		modelMap.put("logo", context.getRealPath(caminhoLogoSefaz));
		modelMap.put("header.pat", context.getRealPath(caminhoHeaderJasper));
		modelMap.put("footer.pat", context.getRealPath(caminhoFooterJasper));
		modelMap.put("titulo", titulo);      
		
		return modelMap;
	}
	
	public byte[] gerarMinuta(ServletContext context, ProcuracaoDto procuracaoDTO, String[] movimentacoes) throws SefazException{
		byte[] arquivoPDF = null;
		InputStream reportStream = null;
		PatJasperReportSupport patJasperReportSupport = new PatJasperReportSupport();
		try {
			Map<String, Object> parametros = retornarDadosPDFMinutaSubstabelecimento(context, procuracaoDTO, movimentacoes);
			reportStream = new FileInputStream(new File(context.getRealPath("jasper/minuta-substab-report.jasper")));
			arquivoPDF = patJasperReportSupport.exportarRelatorio(reportStream, parametros,  patJasperReportSupport.setNovosDados());
		} catch (Exception e) {
			throw new SefazException("Ocorreu algum problema ao carregar os parâmetros da minuta. " + e.getMessage(), e);
		}
		return arquivoPDF;
	}
	
	public ModelMap retornarDadosPDFMinutaSubstabelecimento(ServletContext context, ProcuracaoDto procuracaoDTO, String[] movimentacoes) {
		
		String caminhoLogoSefaz					=	"resources/images/sefazgo-logo.png";
		String caminhoHeaderJasper				=	"jasper/header.jasper"; 
		String caminhoFooterJasper				=	"jasper/footer.jasper";
		String tituloMinuta					   	= 	"[Minuta] Termo de Substabelecimento da Procuração";
		
		FormatSupport formatSupport = new FormatSupport();		
		String cnpjMF = ", CNPJ/MF sob nº ";
		String cpfMF = ", CPF/MF sob nº ";
		String oab = ", OAB/UF nº ";
		String endereco = ", com endereço profissional à ";
		
		ModelMap modelMap =  new ModelMap();	
		
		modelMap = montarProcuradorAndOutorganteMinuta(modelMap, movimentacoes);
		
		String dataLimite = "";
		
		int contador = 0;
		
		String procuradores = "";
		contador = 0;
		
		for(ProcuradorDto procuradorDTO : procuracaoDTO.getProcuradores()) {
			if(contador > 0 && (contador + 1) == procuracaoDTO.getProcuradores().size()) { 
				procuradores += " e ";
			}
			else if(contador > 0) {
				procuradores += ", ";
			}
			procuradores += procuradorDTO.getNome() + oab + procuradorDTO.getNumeroOAB() + cpfMF + formatSupport.formatarCPF(procuradorDTO.getCpf()) + endereco + procuradorDTO.getEnderecoFormatado();
			contador++;
		}	
		
		String reservaPoderes = "";
		if(procuracaoDTO.getReservaPoderesSubestabelecimento() == null || procuracaoDTO.getReservaPoderesSubestabelecimento().equals("")) {
			reservaPoderes = ", sem reserva de poderes";
		} else {
			for(EnumReservaPoderPermitido enumReservaPoderPermitido : EnumReservaPoderPermitido.values()) {
				if(procuracaoDTO.getReservaPoderesSubestabelecimento().equals(enumReservaPoderPermitido.getIndiReservaPermitido())) {
					reservaPoderes = ", " + enumReservaPoderPermitido.getSignificadoReservaPermitido().toLowerCase();				
					if(!procuracaoDTO.getReservaPoderesSubestabelecimento().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
						dataLimite = " Podendo ainda o substabelecido, substabelecer os poderes a outrem";
						if(procuracaoDTO.getDataLimiteSubestabelecimento() != null) {
							dataLimite += ", até a data limite de " + Data.formatar(procuracaoDTO.getDataLimiteSubestabelecimento(), Data.DATA_PADRAO);
						}
						dataLimite += ".";
						}				
				}
			}
		}			
		
		String prazoValidade = procuracaoDTO.getDataValidadeSubstabelecimento() == null ? "" : Data.formatar(procuracaoDTO.getDataValidadeSubstabelecimento(), Data.DATA_PADRAO);
		
		String dataExtenso = "Goiânia, " + formatSupport.retornarDataFormatada(); 
		
				
		modelMap.put("procuradores", procuradores); 
		modelMap.put("reservaPoderes", reservaPoderes);
		modelMap.put("prazoValidade", prazoValidade == null || prazoValidade.equals("") ? "" : "O presente substabelecimento terá o prazo de validade até " + prazoValidade + ".");
		modelMap.put("dataLimite", dataLimite);
		modelMap.put("dataAtual", dataExtenso);
		modelMap.put("datasource", new JREmptyDataSource());
		modelMap.put("logo", context.getRealPath(caminhoLogoSefaz));
		modelMap.put("header.pat", context.getRealPath(caminhoHeaderJasper));
		modelMap.put("footer.pat", context.getRealPath(caminhoFooterJasper));
		modelMap.put("titulo", tituloMinuta);     
		
		return modelMap;
	}
	
	private ModelMap montarProcuradorAndOutorganteMinuta(ModelMap modelMap, String[] movimentacoes) {
		FormatSupport formatSupport = new FormatSupport();		
		String cnpjMF = ", CNPJ/MF sob nº ";
		String cpfMF = ", CPF/MF sob nº ";
		String oab = ", OAB/UF nº ";
		String endereco = ", com endereço profissional à ";
		
		String processos = "";
		
		String sujeitoPassivoProcuracao = "";
		
		String outorgante = "";
		
		String dataLimite = "";
		
		int contador = 0;
		
		List<Integer> processosList = new ArrayList<Integer>();
		
		List<SujeitoPassivoProcesso> listaSujeitoPassivoProcesso = new ArrayList<SujeitoPassivoProcesso>();
		for (String idMovimentacao : movimentacoes) {
			
			MovimentacaoProcuracao moviProcuracaoOrigem = movimentacaoProcuracaoService.find(Integer.valueOf(idMovimentacao));
			
			if(contador == 0) {
				Procurador procurador = moviProcuracaoOrigem.getProcurador();
				PessoaEnderecoPK pessoaEnderecoPK = new PessoaEnderecoPK();			
				pessoaEnderecoPK.setIdPessoa(moviProcuracaoOrigem.getProcurador().getPessoa().getIdPessoa());
				pessoaEnderecoPK.setTipoEndereco(EnumTipoEndereco.COMERCIAL.getCodigo()); //Endereço comercial
				PessoaEndereco pessoaEnderecoConsulta = enderecoPessoaService.consultar(pessoaEnderecoPK);
				String enderecoFormatado = new FormatSupport().formatEndereco(pessoaEnderecoConsulta.getEndereco());
				outorgante = procurador.getPessoa().getPessoaFisica().getNome() + cnpjMF +  formatSupport.formatarCPF(procurador.getPessoa().getPessoaFisica().getNumeroCpf()) + endereco + enderecoFormatado;
			}
			SujeitoPassivoProcesso sujeitoPassivoProcesso = patSujeitoPassivoProcessoService.find(moviProcuracaoOrigem.getSujeitoPassivoProcesso().getId());
			if(!listaSujeitoPassivoProcesso.contains(sujeitoPassivoProcesso)) {
				listaSujeitoPassivoProcesso.add(sujeitoPassivoProcesso);
			}
			
			
			contador++;
		}
		
		contador = 0;
		
		for (SujeitoPassivoProcesso sujeitoPassivoProcesso : listaSujeitoPassivoProcesso) {
			
			if(contador > 0 && (contador + 1) == listaSujeitoPassivoProcesso.size()) {
				sujeitoPassivoProcuracao += " e ";
			}
			else if(contador > 0) {
				sujeitoPassivoProcuracao += ", ";
			}
			
			if(sujeitoPassivoProcesso.getPessoa().getTipoPessoa() == 'J') { 
				
				sujeitoPassivoProcuracao += sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNomeFantasia() + cnpjMF +  formatSupport.formatarCNPJ(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNumeroCnpj());
				
			} else {				
				
				sujeitoPassivoProcuracao += sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNome() + cpfMF +  formatSupport.formatarCPF(sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNumeroCpf());
			}
			
			ProcessoAdministrativoTributarioEletronico pat = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico();
			
			if(processosList.indexOf(pat.getIdProcessoAdministrativoTributarioEletronico()) == -1){
				processos += formatSupport.formatarPAT(pat.getTipoDocumentoOrigem(), 
						pat.getNumeroSequencial(),
						pat.getNumeroDigitoVerificador());	
				processosList.add(pat.getIdProcessoAdministrativoTributarioEletronico());
			}
			contador++;	 	
		}
		
		modelMap.put("outorgantes", outorgante);
		modelMap.put("sujeitosPassivos", sujeitoPassivoProcuracao);
		modelMap.put("processo", processos);
		
		return modelMap;
	}
	
	@HistoryJpa
	@Transactional
	public void ativarSubstabelecimento(Integer usuarioHistorico, SubEstabelecimentoProcuracao subEstabelecimentoProcuracao, PecaEletronicaDto pEletronicaDto, PecaEletronicaDto pecaTermoJuntada) throws SefazException{
		UcmDocument ucmDocument = null;
		try {
			ucmDocument = pecaEletronicaEcmService.salvarNovaRevisaoUcm(usuarioHistorico, pEletronicaDto);
			subEstabelecimentoProcuracao.setStatus(EnumStatusSubstabelecimento.Completo.getCodigo());
			subEstabelecimentoJpaRepository.save(subEstabelecimentoProcuracao);
			pecaSubstabelecimentoService.savePecaSubstabelecimentoAndUcmDocument(usuarioHistorico, subEstabelecimentoProcuracao.getId(), pecaTermoJuntada);
			List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = movimentacaoProcuracaoService.findBySubstabelecimento(subEstabelecimentoProcuracao.getId());
			for (MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacaoProcuracao) {
				movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.S);
				movimentacaoProcuracaoService.save(usuarioHistorico, movimentacaoProcuracao);
			}
		} catch (Exception e) {
			if(ucmDocument != null) {
				pecaEletronicaEcmService.deleteRevision(ucmDocument); 
				throw new SefazException(e.getMessage());
			}
		} 
	}

}
