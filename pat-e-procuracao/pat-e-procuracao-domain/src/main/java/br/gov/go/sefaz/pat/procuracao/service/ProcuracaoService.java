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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.support.Data;
import br.gov.go.sefaz.javaee.corporativo.model.DocumentoPessoaId;
import br.gov.go.sefaz.javaee.corporativo.model.EmailPessoa;
import br.gov.go.sefaz.javaee.corporativo.model.Endereco;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.model.TelefonePessoa;
import br.gov.go.sefaz.javaee.corporativo.model.TipoDocumento;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.pat.constants.ConstantesJasper;
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
import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaProcuracao;
import br.gov.go.sefaz.pat.model.ProcessoAdministrativoTributarioEletronico;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEndereco;
import br.gov.go.sefaz.pat.procuracao.model.dto.DocumentoMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcessoSujeitoPassivoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuradorDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.SujeitoPassivoDto;
import br.gov.go.sefaz.pat.procuracao.repository.jdbc.ProcuracaoJdbcRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.ProcuracaoJpaRepository;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.ProcuradorJpaRepository;
import br.gov.go.sefaz.pat.procuracao.support.DadosUsuarioSupport;
import br.gov.go.sefaz.pat.procuracao.support.RecuperadorPath;
import br.gov.go.sefaz.pat.procuracao.support.RelatorioReciboJuntadaPdf;
import br.gov.go.sefaz.pat.service.PatEnderecoService;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;
import br.gov.go.sefaz.pat.support.formatting.PatJasperReportSupport;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
@Service
public class ProcuracaoService {
	
	private static Logger logger = LogManager.getLogger(ProcuracaoService.class);
	
	@Autowired
	private ProcuracaoJpaRepository procuracaoJpaRepository;
	
	@Autowired
	private ProcuracaoJdbcRepository procuracaoJdbcRepository;
	
	@Autowired
	private PecaProcuracaoService pecaProcuracaoService;
	
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
	private ProcuradorJpaRepository procuradorJpaRepository;
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoService;
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	private PatEnderecoService enderecoService;
	
	@Autowired
	private PessoaService pessoaService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService;
	
	@Autowired
	private SubEstabelecimentoService subEstabelecimentoService; 
	
	@Autowired
	private RelatorioReciboJuntadaPdf relatorioProcuracaoPdf;	
	
	@Autowired
	private RecuperadorPath recuperadorPath;
	
	@Autowired
	private ComplementoProcuradorService complementoProcuradorService;
	
	@Autowired
	private DadosUsuarioSupport dadosUsuarioSupport;
	
	@Autowired
	private EnderecoProcuradorService enderecoProcuradorService;
	
	@Autowired 
	private PessoaFisicaService pessoaFisicaService;
	
	@Autowired 
	private PessoaJuridicaService pessoaJuridicaService;
	
	
	public void prepararProcuracao(List<Procuracao> listaProcuracao) throws SefazException{
		
		for (Procuracao procuracao : listaProcuracao) {
			procuradorService.prepararProcurador(procuracao.getListaMovimentacaoProcuracao());
		}		
	}
	
	public void prepararProcuracaoPresencial(Integer usuarioLogado, Integer usuarioAutor, List<Procuracao> listaProcuracao, List<DocumentoMovimentacaoPresencialDto> listaArquivosPecaEletronica) throws SefazException, IOException, Exception{
		
		for (Procuracao procuracao : listaProcuracao) {
			procuradorService.prepararProcurador(procuracao.getListaMovimentacaoProcuracao());
			List<PecaEletronicaDto> listaPecaEletronicaDto = pecaProcuracaoService.gerarPecaEletronica(usuarioLogado, usuarioAutor, listaArquivosPecaEletronica);
			String peca = prepararPecaReciboJuntada(listaPecaEletronicaDto);
			listaPecaEletronicaDto.add(gerarReciboDeJuntada(usuarioLogado, usuarioAutor, procuracao.getListaMovimentacaoProcuracao(), peca, "Termo de Juntada Procuração"));
			procuracao.setListaPecaEletronicaDto(listaPecaEletronicaDto); 
		}		
	}
	
	public String prepararPecaReciboJuntada(List<PecaEletronicaDto> listaPecaEletronicaDto) {
		int contador = 0;
		StringBuilder peca = new StringBuilder();		
		for (PecaEletronicaDto pecaEletronicaDto : listaPecaEletronicaDto) {
			if(contador > 0 && (contador + 1) == listaPecaEletronicaDto.size()) {
				peca.append(" e ");
			} else if(contador > 0) {
				peca.append(", ");
			}			
			peca.append(pecaEletronicaDto.getNomePecaEletronica());	
			contador++;
		}
		
		return peca.toString();
	} 
	
	@HistoryJpa
	@Transactional
	public void saveProcuracao(Integer usuarioHistorico, List<Procuracao> listaProcuracao, String matriculaUsuarioGestor) throws SefazException{
		for (Procuracao procuracao : listaProcuracao) {
			
			List<MovimentacaoProcuracao> listaDetalheProcuracao = procuracao.getListaMovimentacaoProcuracao();
			procuracaoJpaRepository.save(procuracao);
			
			if(procuracao.getListaPecaEletronicaDto() != null) {
				for (int cont=0; cont < procuracao.getListaPecaEletronicaDto().size() ;cont++) {
					PecaEletronicaDto pecaEletronicaDto = (PecaEletronicaDto) procuracao.getListaPecaEletronicaDto().get(cont);				
					pecaProcuracaoService.savePecaProcuracaoAndUcmDocument(usuarioHistorico, procuracao.getId(), pecaEletronicaDto); 
				}				
			}
			
			for (MovimentacaoProcuracao movimentacaoProcuracao : listaDetalheProcuracao) {
				
				if(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa() != null && movimentacaoProcuracao.getProcurador().getListaComplementoPessoa().size() > 0) {
					complementoProcuradorService.saveComplementoProcurador(matriculaUsuarioGestor, movimentacaoProcuracao.getProcurador().getListaComplementoPessoa());
				}			
				
				Procurador procurador = procuradorService.consultar(movimentacaoProcuracao.getProcurador().getPessoa());
				if(procurador != null) {
					movimentacaoProcuracao.getProcurador().setId(procurador.getId());
				}
				procuradorJpaRepository.save(movimentacaoProcuracao.getProcurador()); 
				
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
				
				movimentacaoProcuracao.setProcuracao(procuracao);
				movimentacaoService.saveAndFlush(usuarioHistorico, movimentacaoProcuracao); 	 	
				
			}
			
		}	
	}
	
	@HistoryJpa
	@Transactional
	public void save(Integer usuarioHistorico, List<Procuracao> listaProcuracao, String matriculaUsuarioGestor) {
		
		for (Procuracao procuracao : listaProcuracao) {
			
			List<MovimentacaoProcuracao> listaDetalheProcuracao = procuracao.getListaMovimentacaoProcuracao();
			procuracaoJpaRepository.save(procuracao);	
			
			for (MovimentacaoProcuracao movimentacaoProcuracao : listaDetalheProcuracao) {
				
				Procurador procurador = procuradorService.consultar(
						movimentacaoProcuracao.getProcurador().getPessoa());
				if(procurador != null) {
					movimentacaoProcuracao.getProcurador().setId(procurador.getId());
					if(procurador.getEscritorioProcurador() != null && procurador.getEscritorioProcurador().getIdPessoa() != null) {
						PessoaJuridica pj = new PessoaJuridica();
						pj.setIdPessoa(procurador.getEscritorioProcurador().getIdPessoa());				
						movimentacaoProcuracao.getProcurador().setEscritorioProcurador(pj);
					} 
				}	
				
				if(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa() != null && movimentacaoProcuracao.getProcurador().getListaComplementoPessoa().size() > 0) {
					saveComplementoPessoa(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa(), matriculaUsuarioGestor);
				}			
				
				procuradorJpaRepository.save(movimentacaoProcuracao.getProcurador()); 
				movimentacaoProcuracao.setProcuracao(procuracao);
				movimentacaoService.saveAndFlush(usuarioHistorico, movimentacaoProcuracao); 	 	
				
			}
			
		}		
		
	}
	
	@HistoryJpa
	@Transactional
	public void save(Integer usuarioHistorico, Procuracao procuracao, String matriculaUsuarioGestor) throws SefazException{
		
		List<MovimentacaoProcuracao> listaDetalheProcuracao = procuracao.getListaMovimentacaoProcuracao();
		procuracaoJpaRepository.save(procuracao);	
		
		for (MovimentacaoProcuracao movimentacaoProcuracao : listaDetalheProcuracao) {
			
			Procurador procurador = procuradorService.consultar(
					movimentacaoProcuracao.getProcurador().getPessoa()); 
			if(procurador != null) {
				movimentacaoProcuracao.getProcurador().setId(procurador.getId());
				
				if(procurador.getEscritorioProcurador() != null && procurador.getEscritorioProcurador().getIdPessoa() != null) {
					PessoaJuridica pj = new PessoaJuridica();
					pj.setIdPessoa(procurador.getEscritorioProcurador().getIdPessoa());				
					movimentacaoProcuracao.getProcurador().setEscritorioProcurador(pj);
				} 	
				
			}		
			
			if(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa() != null && movimentacaoProcuracao.getProcurador().getListaComplementoPessoa().size() > 0) {
				saveComplementoPessoa(movimentacaoProcuracao.getProcurador().getListaComplementoPessoa(), matriculaUsuarioGestor);
			}					
			
			procuradorService.save(usuarioHistorico, movimentacaoProcuracao.getProcurador());
			movimentacaoProcuracao.setProcuracao(procuracao);  
			movimentacaoService.saveAndFlush(usuarioHistorico, movimentacaoProcuracao);	    
			
		}
			
	}
	
	public Integer checkStepOfProcuration(Integer id) {
		Integer statusProcuracao = procuracaoJpaRepository.findIdPecaEletronicaProcuracaoByIdProcuracao(id);
		
		if(statusProcuracao == null) return 0;
		if(statusProcuracao == 6) return 1; 
		if(statusProcuracao == 1) return 2;
		return null;
	}	 
	
	private void saveComplementoPessoa(List<List> listaComplementoPessoa, String matriculaUsuarioGestor) { 
		
		List<PessoaEndereco> listaEndereco = listaComplementoPessoa.get(0);
		List<EmailPessoa> listaEmailPessoa = listaComplementoPessoa.get(1);
		List<TelefonePessoa> listaTelefone = listaComplementoPessoa.get(2);
		List<DocumentoAdvogado> listaDocumento = listaComplementoPessoa.get(3);
		for (EmailPessoa emailPessoa : listaEmailPessoa) {
			
			EmailPessoa emailPessoaConsulta = emailService.consultarPorPessoaTipoEmail(emailPessoa.getIdPessoa(), emailPessoa.getTipoEmail());
			if(emailPessoaConsulta != null) {
				emailPessoa.setId(emailPessoaConsulta.getId());
			}			
			
			emailService.save(matriculaUsuarioGestor, emailPessoa);
		}
		for (TelefonePessoa telefonePessoa : listaTelefone) {
			
			TelefonePessoa telefonePessoaConsulta = telefoneService.consultarPorPessoaTipoTelefone(telefonePessoa.getIdPessoa(), telefonePessoa.getTipoTelefone());
			if(telefonePessoaConsulta != null) {
				telefonePessoa.setId(telefonePessoaConsulta.getId());
			}
			
			telefoneService.save(matriculaUsuarioGestor, telefonePessoa);
		}
		
		for (DocumentoAdvogado documentoPessoa : listaDocumento) {
			
			DocumentoAdvogado documentoPessoaConsulta = documentoService.consultar(documentoPessoa.getDocumentoPessoaId());
			if(documentoPessoaConsulta != null) {
				documentoPessoa.setIdDocumentoPessoa(documentoPessoaConsulta.getIdDocumentoPessoa());
				documentoPessoa.setDocumentoPessoaId(documentoPessoaConsulta.getDocumentoPessoaId());
			}
			
			documentoService.save(matriculaUsuarioGestor, documentoPessoa);
		}
		
		for(PessoaEndereco pessoaEndereco : listaEndereco) {
			
			PessoaEndereco pessoaEnderecoConsulta = enderecoPessoaService.consultar(pessoaEndereco.getPessoaEnderecoPk());
			
			if(pessoaEnderecoConsulta != null) {
				
				Endereco endereco = pessoaEnderecoConsulta.getEndereco();
				endereco.setLogradouro(pessoaEndereco.getEndereco().getLogradouro());
				endereco.setNumero(pessoaEndereco.getEndereco().getNumero());
				endereco.setNumeroLote(pessoaEndereco.getEndereco().getNumeroLote());
				endereco.setNumeroQuadra(pessoaEndereco.getEndereco().getNumeroQuadra());
				endereco.setComplemento(pessoaEndereco.getEndereco().getComplemento());
				pessoaEndereco.setPessoaEnderecoPk(pessoaEnderecoConsulta.getPessoaEnderecoPk());
				pessoaEndereco.setEndereco(endereco);		
				
			}			
			
			enderecoService.save(matriculaUsuarioGestor, pessoaEndereco.getEndereco()); 
			enderecoPessoaService.save(matriculaUsuarioGestor, pessoaEndereco);
		}
	}	
	
	@HistoryJpa
	@Transactional
	public void salvarProcuracao(Integer usuarioHistorico, Procuracao procuracao) {
		procuracaoJpaRepository.save(procuracao);
	}
	
	@HistoryJpa
	@Transactional
	public void ativarProcuracao(Integer usuarioHistorico, Procuracao procuracao, PecaEletronicaDto pEletronicaDto, PecaEletronicaDto pecaReciboJuntada) {
		UcmDocument ucmDocument = null;
		try {
			logger.debug("ProcuracaoService.ativarProcuracao: antes de  savePecaProcuracaoAndUcmDocument pecaReciboJuntada");
			pecaProcuracaoService.savePecaProcuracaoAndUcmDocument(usuarioHistorico, procuracao.getId(), pecaReciboJuntada);
			logger.debug("ProcuracaoService.ativarProcuracao: depois de  savePecaProcuracaoAndUcmDocument pecaReciboJuntada");
			logger.debug("ProcuracaoService.ativarProcuracao: antes de  salvarNovaRevisaoUcm");
			ucmDocument = pecaEletronicaEcmService.salvarNovaRevisaoUcm(usuarioHistorico, pEletronicaDto);
			logger.debug("ProcuracaoService.ativarProcuracao: depois de  salvarNovaRevisaoUcm");
			procuracao.setStatus(EnumStatusProcuracao.Ativa.getCodigo());
			logger.debug("ProcuracaoService.ativarProcuracao: antes de  procuracaoJpaRepository.save");
			procuracaoJpaRepository.save(procuracao);
			logger.debug("ProcuracaoService.ativarProcuracao: depois de  procuracaoJpaRepository.save");
			List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = movimentacaoProcuracaoService.findByProcuracao(procuracao.getId());
			logger.debug("ProcuracaoService.ativarProcuracao: antes de salvar listaMovimentacaoProcuracao");
			for (MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacaoProcuracao) {
				movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.S);
				movimentacaoProcuracaoService.save(usuarioHistorico, movimentacaoProcuracao);
			}
			logger.debug("ProcuracaoService.ativarProcuracao: depois de salvar listaMovimentacaoProcuracao");
		} catch (Exception e) {
			if(ucmDocument != null) {
				logger.debug("ProcuracaoService.ativarProcuracao: antes deleteRevision");
				pecaEletronicaEcmService.deleteRevision(ucmDocument); 
				logger.debug("ProcuracaoService.ativarProcuracao: depois deleteRevision");
			}
		} 
	}
	
	@HistoryJpa
	@Transactional
	public void delete(Integer usuarioHistorico, Procuracao procuracao) {	
		
		this.procuracaoJpaRepository.delete(procuracao.getId());	
		
	}	
	
	public ProcuracaoDto criarProcuracaoDtoForProcuracaoPresencial(ProcuracaoDto procuracaoDTO, UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws SefazException {
		
		Pessoa pessoaAutenticada = pessoaService.consultar(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());
		if(pessoaAutenticada.getTipoPessoa() == 'J') { 
			throw new SefazException("Uma Procuração Presencial só pode ser feita por um Servidor Estadual.");
		}
		
		pessoaAutenticada.setPessoaFisica(pessoaFisicaService.consultarPorId(pessoaAutenticada.getIdPessoa()));
		
		Pessoa outorgante = pessoaService.consultar(procuracaoDTO.getIdOutorgante());
		
		if(outorgante.getTipoPessoa() == 'F') {
			outorgante.setPessoaFisica(pessoaFisicaService.consultarPorId(outorgante.getIdPessoa()));
		}
		
		else {
			outorgante.setPessoaJuridica(pessoaJuridicaService.consultarPorId(outorgante.getIdPessoa()));
		}		
		
		List<SujeitoPassivoProcesso> listaSujeitoPassivoConsulta = null;
		List<ProcessoSujeitoPassivoDto> listaSujeitoPassivo = null;
		
		procuracaoDTO.setIdPessoaAssinante(pessoaAutenticada.getPessoaFisica().getIdPessoa());
		procuracaoDTO.setUsuarioLogado(pessoaAutenticada.getPessoaFisica().getNome());
		procuracaoDTO.setTipoPessoaOutorgante(outorgante.getTipoPessoa().toString());

		if(outorgante.getPessoaFisica() != null){
			procuracaoDTO.setNomeOutorgante(outorgante.getPessoaFisica().getNome());
			procuracaoDTO.setTipoPessoaOutorgante("F");
			
			procuracaoDTO.setExisteProcesso(patSujeitoPassivoProcessoService.verificarExisteProcessoPF(procuracaoDTO.getIdOutorgante()));
			if(!procuracaoDTO.isExisteProcesso()) {
				return procuracaoDTO;		
			}
			
			listaSujeitoPassivoConsulta = movimentacaoProcuracaoService.listarSujeitoPassivoPFSemProcuracaoAtiva(procuracaoDTO.getIdOutorgante());
			if(listaSujeitoPassivoConsulta == null || listaSujeitoPassivoConsulta.size() == 0) {
				procuracaoDTO.setExisteProcuracaoAtiva(true);
				return procuracaoDTO;
			}
		} else if(outorgante.getPessoaJuridica() != null){
			procuracaoDTO.setNomeOutorgante(outorgante.getPessoaJuridica().getEmpresa().getNomeEmpresa());
			procuracaoDTO.setTipoPessoaOutorgante("J");
			
			procuracaoDTO.setExisteProcesso(patSujeitoPassivoProcessoService.verificarExisteProcesso(outorgante.getPessoaJuridica().getEmpresa().getNumeroCnpjBase()));
			if(!procuracaoDTO.isExisteProcesso()) {
				return procuracaoDTO;		
			}			
			
			listaSujeitoPassivoConsulta = movimentacaoProcuracaoService.listarSujeitoPassivoSemProcuracaoAtiva(outorgante.getPessoaJuridica().getEmpresa().getNumeroCnpjBase());
			if(listaSujeitoPassivoConsulta == null || listaSujeitoPassivoConsulta.size() == 0) {
				procuracaoDTO.setExisteProcuracaoAtiva(true);
				return procuracaoDTO;
			}		
		}
		
		listaSujeitoPassivo = montarListaSujeitoPassivoDTO(listaSujeitoPassivoConsulta);
		
		procuracaoDTO.setSujeitoPassivos(listaSujeitoPassivo);		
		
		return procuracaoDTO;
	}
	
	public ProcuracaoDto criarProcuracaoDto(ProcuracaoDto procuracaoDTO, UsuarioAutenticadoDetails usuarioAutenticadoDetails, String tipoNaturezaJuridica) {
		procuracaoDTO.setTipoNaturezaJuridica(tipoNaturezaJuridica);
		Pessoa pessoaAutenticada = pessoaService.consultar(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());
		
		List<SujeitoPassivoProcesso> listaSujeitoPassivoConsulta = null;
		List<ProcessoSujeitoPassivoDto> listaSujeitoPassivo = null;
		
		if(pessoaAutenticada.getPessoaJuridica() != null){
			
			procuracaoDTO.setIdPessoaAssinante(pessoaAutenticada.getPessoaJuridica().getIdPessoa());
			procuracaoDTO.setUsuarioLogado(pessoaAutenticada.getPessoaJuridica().getNomeFantasia());
			procuracaoDTO.setNomeOutorgante(pessoaAutenticada.getPessoaJuridica().getNomeFantasia());
			procuracaoDTO.setTipoPessoaOutorgante("J");
			
			procuracaoDTO.setExisteProcesso(patSujeitoPassivoProcessoService.verificarExisteProcesso(pessoaAutenticada.getPessoaJuridica().getEmpresa().getNumeroCnpjBase()));
			if(!procuracaoDTO.isExisteProcesso()) {
				return procuracaoDTO;		
			}			
			
			listaSujeitoPassivoConsulta = movimentacaoProcuracaoService.listarSujeitoPassivoSemProcuracaoAtiva(pessoaAutenticada.getPessoaJuridica().getEmpresa().getNumeroCnpjBase());
			if(listaSujeitoPassivoConsulta == null || listaSujeitoPassivoConsulta.size() == 0) {
				procuracaoDTO.setExisteProcuracaoAtiva(true);
				return procuracaoDTO;
			}		
		} else if(pessoaAutenticada.getPessoaFisica() != null){
			procuracaoDTO.setIdPessoaAssinante(pessoaAutenticada.getPessoaFisica().getIdPessoa());
			procuracaoDTO.setUsuarioLogado(pessoaAutenticada.getPessoaFisica().getNome());
			if(procuracaoDTO.getIndiProcuracaoPresencial().equals("N")) procuracaoDTO.setNomeOutorgante(procuracaoDTO.getUsuarioLogado());
			procuracaoDTO.setTipoPessoaOutorgante("F");
			
			procuracaoDTO.setExisteProcesso(patSujeitoPassivoProcessoService.verificarExisteProcessoPF(pessoaAutenticada.getIdPessoa()));
			if(!procuracaoDTO.isExisteProcesso()) {
				return procuracaoDTO;		
			}
			
			listaSujeitoPassivoConsulta = movimentacaoProcuracaoService.listarSujeitoPassivoPFSemProcuracaoAtiva(pessoaAutenticada.getIdPessoa());
			if(listaSujeitoPassivoConsulta == null || listaSujeitoPassivoConsulta.size() == 0) {
				procuracaoDTO.setExisteProcuracaoAtiva(true);
				return procuracaoDTO;
			}
		}
		
		listaSujeitoPassivo = montarListaSujeitoPassivoDTO(listaSujeitoPassivoConsulta);
		
		procuracaoDTO.setSujeitoPassivos(listaSujeitoPassivo);		
		
		return procuracaoDTO;
	}
	
	public List<ProcuracaoDto> listarProcuracaoProcurador(Integer idPessoa) {
		return this.procuracaoJdbcRepository.listarProcuracaoPorProcurador(idPessoa);
	}
	
	public List<ProcuracaoDto> listarProcuracaoSujeitoPassivo(Integer idPessoa) {
		return this.procuracaoJdbcRepository.listarProcuracaoPorSujeitoPassivo(idPessoa); 
	}
	
	public List<ProcuracaoDto> listarProcuracaoSujeitoPassivoPJ(String numrCnpjBase) {
		return this.procuracaoJdbcRepository.listarProcuracaoPorSujeitoPassivo(numrCnpjBase);
	} 
	
	public Procuracao find(Integer id) {
		return this.procuracaoJpaRepository.findOne(id);
	}
	
	public List<Procuracao> findAll() {
		return this.procuracaoJpaRepository.findAll();
	}
	
	public Page<Procuracao> findAll(Pageable pageable) {
		return this.procuracaoJpaRepository.findAll(pageable); 
	}
	
	public boolean verificarProcuracao(Integer idSujeitoPassivo, String status) {
		return procuracaoJdbcRepository.verificarProcuracao(idSujeitoPassivo, status);
	}
	
	public boolean haProcuradoresAtivos(Integer idProcuracao){
		List<MovimentacaoProcuracao> detalhesProcuracao = movimentacaoService.findByProcuracao(idProcuracao);		
		Character statusAtivo = EnumSimNao.S.getValor();
				
		for (MovimentacaoProcuracao detalhe : detalhesProcuracao)
			if(statusAtivo.equals(detalhe.getIndiMovmtProcuracaoAtivaAsChar()))
				return true;
		return false;
	}
	
	public boolean haProcuradoresAtivosPorPessoa(Integer idPessoa){
		List<MovimentacaoProcuracao> detalhesProcuracao = movimentacaoService.findByPessoa(idPessoa);		
		Character statusAtivo = EnumSimNao.S.getValor();				
		for (MovimentacaoProcuracao detalhe : detalhesProcuracao)
			if(statusAtivo.equals(detalhe.getIndiMovmtProcuracaoAtivaAsChar()))
				return true;
		return false;
	}
	
	public boolean haSubstabelecimentoIncompleto(Integer idMovimentacoes){
		MovimentacaoProcuracao movimentacaoProcuracao = movimentacaoProcuracaoService.find(idMovimentacoes);				
				List<MovimentacaoProcuracao> listaMovimentacoesPorProcuracao = movimentacaoProcuracaoService.findByProcuracao(movimentacaoProcuracao.getProcuracao().getId(), EnumSimNao.N.getValor());
				for(MovimentacaoProcuracao movimentacaoAux : listaMovimentacoesPorProcuracao){
					if(movimentacaoAux.getMovimentacaoOrigemProcuracao() != null && movimentacaoAux.getMovimentacaoOrigemProcuracao().getId().equals(movimentacaoProcuracao.getId())){
						SubEstabelecimentoProcuracao subEstabelecimentoProcuracao = subEstabelecimentoService.buscaSubEstabelecimento(movimentacaoAux.getSubEstabelecimentoProcuracao().getId());
						if(subEstabelecimentoProcuracao.getIndiReserva() != 3)
							return true;				
					}																	
				}
		return false;			
	}
	
	@HistoryJpa
	@Transactional
	public void inativarProcuracaoSemProcurador(Integer usuarioHistorico, Integer idProcuracao, String statusProcuracao) 
	throws SefazException{
		try{			
			boolean haProcuradoresAtivos = this.haProcuradoresAtivos(idProcuracao);		
			Procuracao procuracao = null;
			
			if(!haProcuradoresAtivos){
				procuracao = this.find(idProcuracao);
				procuracao.setStatus(statusProcuracao);
				this.salvarProcuracao(usuarioHistorico, procuracao);
			}
		}catch (Exception e) {
			throw new SefazException("Um problema ocorreu ao tentar inativar a procuração. Verifique e tente novamente.");
		}
	}
	private List<ProcessoSujeitoPassivoDto> montarListaSujeitoPassivoDTO(List<SujeitoPassivoProcesso> listaSujeitoPassivoConsulta) {
		
		List<ProcessoSujeitoPassivoDto> listaSujeitoPassivo = null;
		
		String processoAnterior = "";
		listaSujeitoPassivo = new ArrayList<ProcessoSujeitoPassivoDto>();
		
		int qtdeSujeitoPassivo = 0;
		
		int cont = 0;
		
		for (SujeitoPassivoProcesso sujeitoPassivoProcesso : listaSujeitoPassivoConsulta) {
			
			if(!sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getIdProcessoAdministrativoTributarioEletronico().toString().equals(processoAnterior)) {
				ProcessoSujeitoPassivoDto processoSujeitoPassivoDTO = new ProcessoSujeitoPassivoDto();
				SujeitoPassivoDto sujeitoPassivoDTO = new SujeitoPassivoDto();
				
				if(cont == 0) {
					processoSujeitoPassivoDTO.setEstiloLinha("white");
					cont++;
				} else {
					processoSujeitoPassivoDTO.setEstiloLinha("dark");
					cont = 0;
				}
				
				processoSujeitoPassivoDTO.setId(sujeitoPassivoProcesso.getId());
				processoSujeitoPassivoDTO.setIdProcessoAdmEletronico(sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getIdProcessoAdministrativoTributarioEletronico());
				processoSujeitoPassivoDTO.setNumeroProcesso((sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getTipoDocumentoOrigem() == null ? "" :  sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getTipoDocumentoOrigem()) + "" + sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getNumeroSequencial() + "" + sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getNumeroDigitoVerificador());
				processoSujeitoPassivoDTO.setDataFormalizacao(sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getDataDaFormalizacao());
				
				if(sujeitoPassivoProcesso.getPessoa() != null && sujeitoPassivoProcesso.getPessoa().getPessoaJuridica() != null) {
					sujeitoPassivoDTO.setCpfCnpj(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNumeroCnpj());
					sujeitoPassivoDTO.setNomeSujeitoPassivo(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNomeFantasia());
				} else {
					sujeitoPassivoDTO.setCpfCnpj(sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNumeroCpf());
					sujeitoPassivoDTO.setNomeSujeitoPassivo(sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNome());
				}
				sujeitoPassivoDTO.setIdProcessoAdmEletronico(sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getIdProcessoAdministrativoTributarioEletronico());
				sujeitoPassivoDTO.setId(sujeitoPassivoProcesso.getId());
				
				processoSujeitoPassivoDTO.setListaSujeitoPassivo(new ArrayList<SujeitoPassivoDto>());
				
				qtdeSujeitoPassivo = 1;
				processoSujeitoPassivoDTO.setQteSujeitoPassivo(qtdeSujeitoPassivo);
				
				processoSujeitoPassivoDTO.getListaSujeitoPassivo().add(sujeitoPassivoDTO); 
				listaSujeitoPassivo.add(processoSujeitoPassivoDTO);
				
				
				
			} else {
				
				qtdeSujeitoPassivo++;
				
				SujeitoPassivoDto sujeitoPassivoDTO = new SujeitoPassivoDto();
				if(sujeitoPassivoProcesso.getPessoa() != null) {
					sujeitoPassivoDTO.setCpfCnpj(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNumeroCnpj());
					sujeitoPassivoDTO.setNomeSujeitoPassivo(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNomeFantasia());
				} 
				
				sujeitoPassivoDTO.setId(sujeitoPassivoProcesso.getId());
				
				sujeitoPassivoDTO.setIdProcessoAdmEletronico(sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getIdProcessoAdministrativoTributarioEletronico());
				
				ProcessoSujeitoPassivoDto processoSujeitoPassivoDTO = listaSujeitoPassivo.get(listaSujeitoPassivo.size()-1);
				processoSujeitoPassivoDTO.setQteSujeitoPassivo(qtdeSujeitoPassivo);
				processoSujeitoPassivoDTO.getListaSujeitoPassivo().add(sujeitoPassivoDTO);
				
			}
			
			processoAnterior = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getIdProcessoAdministrativoTributarioEletronico().toString();
		}
		
		return listaSujeitoPassivo;
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
			Integer idProcuracao) throws IllegalAccessException, InvocationTargetException,
	NoSuchMethodException, SefazException, UnrecoverableKeyException, InvalidAttributeValueException, KeyStoreException, NoSuchAlgorithmException, JRException {
		
		InputStream reportStream = null;
		PecaEletronicaDto pecaEletronicaDTO = null;
		Procuracao procuracao = this.find(idProcuracao);
		PecaProcuracao pecaProcuracao = this.pecaProcuracaoService.findPecaProcuracao(procuracao.getId(), EnumModeloDocumentEcm.PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		PatJasperReportSupport patJasperReportSupport = new PatJasperReportSupport();
		try {
			
			if(pecaProcuracao != null && pecaProcuracao.getPecaEletronica() != null){ 
				pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaProcuracao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			}
			else {
				logger.debug("ProcuracaoService.criarPecaEletronica: tentando montar Dados Pdf");
				ModelMap modelMap = retornarDadosPDFProcuracao(context, procuracao);
				logger.debug("ProcuracaoService.criarPecaEletronica: conseguiu criar Pdf");
				reportStream = new FileInputStream(new File(context.getRealPath("jasper/procuracao-report.jasper"))); 
				byte[] arquivoPDF = patJasperReportSupport.exportarRelatorio(reportStream, modelMap,  patJasperReportSupport.setNovosDados());
				logger.debug("ProcuracaoService.criarPecaEletronica: exportou relatório");
				if (arquivoPDF != null) {			    	
//					byte[] pdfDataSigned = assinadorDigital.assinarPdf(arquivoPDF, "Procuração", context.getRealPath("/SefazWeblogicDesenvolvimento.jks"));
					logger.debug("ProcuracaoService.criarPecaEletronica: assinou pdf");
					Pessoa pessoa = new Pessoa();
					pessoa.setIdPessoa(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());				
					pecaEletronicaDTO = pecaEletronicaService.montarPecaEletronica(pessoa, arquivoPDF, EnumProfilesDocumentEcm.TERMOS_E_PROCURACOES, EnumModeloDocumentEcm.PROCURACAO_ELETRONICA, EnumSubCategoriaDocumentEcm.PROCURACAO);
					logger.debug("ProcuracaoService.criarPecaEletronica: montou peca");
				}									
			}
		}catch (Exception e) {
			logger.error("ProcuracaoService.criarPecaEletronica " + e.getMessage());
			throw new SefazException("Ocorreu um erro ao gerar uma Peça Eletronica. " + e.getMessage());
		}		
		return pecaEletronicaDTO;		
	}
	
	public PecaEletronicaDto gerarReciboDeJuntada(Integer idUsuarioLogado, Integer usuarioAutor, List<MovimentacaoProcuracao> listaMovimentacoes, String pecas, String titulo) throws IllegalAccessException, InvocationTargetException,
	NoSuchMethodException, UnrecoverableKeyException, InvalidAttributeValueException, KeyStoreException, NoSuchAlgorithmException, JRException, SefazException {
		
		InputStream reportStream = null; 
		PecaEletronicaDto pecaEletronicaDto = null;
		try {
			String dadosUsuarioLogado = dadosUsuarioSupport.filtroUsuarioLogado(idUsuarioLogado);
			Map<String, Object> parametros = relatorioProcuracaoPdf.parametrosReciboDeJuntada(dadosUsuarioLogado, listaMovimentacoes, pecas, titulo);			
			reportStream = new FileInputStream(new File(recuperadorPath.recuperarPath(ConstantesJasper.jasperReciboJuntada))); 
			
			PatJasperReportSupport patJasperReportSupport = new PatJasperReportSupport();
			byte[] arquivoPDF = patJasperReportSupport.exportarRelatorio(reportStream, parametros,  patJasperReportSupport.setNovosDados());
			
			if (arquivoPDF != null) {			    	
//				byte[] pdfDataSigned = assinadorDigital.assinarPdf(arquivoPDF, "Recibo de Juntada", recuperadorPath.recuperarPath(ConstantesKeyStore.keyStorePathTest));									
				Pessoa pessoa = new Pessoa();
				pessoa.setIdPessoa(idUsuarioLogado);
				Pessoa pessoaAutor = new Pessoa();
				pessoaAutor.setIdPessoa(usuarioAutor);
				pecaEletronicaDto = pecaEletronicaService.montarPecaEletronica(pessoa, pessoaAutor, arquivoPDF, 
						EnumProfilesDocumentEcm.TERMOS_E_PROCURACOES, EnumModeloDocumentEcm.TERMO_DE_JUNTADA, EnumSubCategoriaDocumentEcm.TERMO_RITO_PROCESSUAL);			
			}									
		}catch (Exception e) {
			throw new SefazException("Ocorreu um problema ao gerar o documento no ECM. " + e.getMessage(), e);
		}		
		return pecaEletronicaDto;		
	}
	
	
	public PecaProcuracao gerarPecaProcuracao(Integer id, PecaEletronicaDto pecaEletronicaDTO) throws SefazException {
			
			Procuracao procuracao = this.find(id);
			PecaProcuracao pecaProcuracao = new PecaProcuracao();		
			PecaEletronica peca = new PecaEletronica();
			
		try {
			peca.setId(pecaEletronicaDTO.getIdPecaEletronica());		
			pecaProcuracao.setProcuracao(procuracao);
			pecaProcuracao.setPecaEletronica(peca);
			
		}catch (Exception e) {
			throw new SefazException("Ocorreu um erro ao gerar uma peça procuração. ");
		}
		return pecaProcuracao;
	}
	
	private ModelMap retornarDadosPDFProcuracao(ServletContext context, Procuracao procuracao) throws SefazException{ 
		
		FormatSupport formatSupport = new FormatSupport();
		
		String cnpjMF = ", CNPJ/MF sob nº ";
		String cpfMF = ", CPF/MF sob nº ";
		String oab = ", OAB/UF nº ";
		String endereco = ", com endereço profissional à ";		
		String titulo = 	"Procuração";
		
		List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = null;
		try {
			listaMovimentacaoProcuracao = movimentacaoProcuracaoService.findByProcuracao(procuracao.getId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Erro ao consultar a movimentação procuração");
			throw new SefazException("Erro ao consultar a movimentação procuração" + e.getMessage()); 
		}
		
		String outorgantes = "";
		String procuradores = "";
		int contador = 0;
		
		String processo = null; 
		
		
		List<Integer> listaIdsProcurador = new ArrayList<Integer>();
		List<Integer> listaIdsSujeitoPassivo = new ArrayList<Integer>();
		
		for(MovimentacaoProcuracao movimentacaoProcuracao : listaMovimentacaoProcuracao) {			
			
			if(!listaIdsSujeitoPassivo.contains(movimentacaoProcuracao.getSujeitoPassivoProcesso().getId())) {
				listaIdsSujeitoPassivo.add(movimentacaoProcuracao.getSujeitoPassivoProcesso().getId());
				if(listaIdsSujeitoPassivo.size() > 0) {
					outorgantes += ", ";
				}
				if (movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaFisica() != null) {
					outorgantes += movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaFisica().getNome()
							+ cnpjMF
							+ formatSupport.formatarCPF(movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaFisica().getNumeroCpf());
				} else {
					outorgantes += movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaJuridica()
							.getNomeFantasia() + cnpjMF
							+ formatSupport.formatarCNPJ(movimentacaoProcuracao.getSujeitoPassivoProcesso().getPessoa().getPessoaJuridica().getNumeroCnpj());
				}
			}
			
			if(!listaIdsProcurador.contains(movimentacaoProcuracao.getProcurador().getId())) {
				DocumentoPessoaId documentoPessoaId = new DocumentoPessoaId();			
				documentoPessoaId.setPessoa(movimentacaoProcuracao.getProcurador().getPessoa());
				TipoDocumento tipoDocumentoPessoa = new TipoDocumento();
				tipoDocumentoPessoa.setIdTipoDocumento(11); // tipo documento advogado
				documentoPessoaId.setTipoDocumento(tipoDocumentoPessoa);
				DocumentoAdvogado documentoPessoaConsulta;
				try {
					documentoPessoaConsulta = documentoService.consultar(documentoPessoaId);
				} catch (Exception e) {
					logger.error("Erro ao consultar o documento.");
					throw new SefazException("Erro ao consultar o documento" + e.getMessage());
				}				
				
				EnderecoProcurador enderecoProcurador = enderecoProcuradorService.consultar(movimentacaoProcuracao.getProcurador().getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
			
				listaIdsProcurador.add(movimentacaoProcuracao.getProcurador().getId());
				if(movimentacaoProcuracao.getProcurador().getPessoa().getPessoaFisica() != null) {
					if(listaIdsProcurador.size() > 0) {
						procuradores += ", ";
					}
					procuradores += movimentacaoProcuracao.getProcurador().getPessoa().getPessoaFisica().getNome() + oab + documentoPessoaConsulta.getNumeroDocumento() + cpfMF + movimentacaoProcuracao.getProcurador().getPessoa().getPessoaFisica().getNumeroCpf() + endereco + new FormatSupport().formatEndereco(enderecoProcurador.getEndereco());
				}	
			}
			
			procuracao = movimentacaoProcuracao.getProcuracao();
			
			SujeitoPassivoProcesso sujeitoPassivoProcesso = movimentacaoProcuracao.getSujeitoPassivoProcesso();
			
			processo = (sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getTipoDocumentoOrigem() == null ? "" :  sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getTipoDocumentoOrigem()) + "" + sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getNumeroSequencial() + "" + sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getNumeroDigitoVerificador();
			
			contador++;
		}			
		
		String reservaPoderes = "";
		if(procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.ComOuSemReserva.getIndiReservaPermitido()) ||
			procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.SomenteComReserva.getIndiReservaPermitido()) ||
			procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.SomenteSemReserva.getIndiReservaPermitido())) {
		for(EnumReservaPoderPermitido enumReservaPoderPermitido : EnumReservaPoderPermitido.values()) {
				reservaPoderes = "Podendo ainda, substabelecer esta a outrem, " + enumReservaPoderPermitido.getSignificadoReservaPermitido();
				
				if(procuracao.getDataValidade() != null) {
					reservaPoderes += ", até a data limite de " + Data.formatar(procuracao.getDataValidade(), Data.DATA_PADRAO); 
				}
				
				reservaPoderes += ".";
			}
		}
			
		
		String prazoValidade = procuracao.getDataValidade() == null ? "" : Data.formatar(procuracao.getDataValidade(), Data.DATA_PADRAO);
		
		String dataExtenso = "Goiânia, " + formatSupport.retornarDataFormatada(); 
		
		JRBeanCollectionDataSource datasource = null;
		
		ModelMap modelMap = new ModelMap();		
		
		modelMap.put("logo.sefaz", context.getRealPath("resources/images/sefazgo-logo.png"));
		modelMap.put("header.pat", context.getRealPath("jasper/header.jasper"));
		modelMap.put("footer.pat", context.getRealPath("jasper/footer.jasper"));
		modelMap.put("procuracaoDTO.outorgantes", outorgantes); 
		modelMap.put("procuradores", procuradores);
		modelMap.put("processo", processo);
		modelMap.put("reservaPoderes", reservaPoderes);
		modelMap.put("prazoValidade", prazoValidade == null || prazoValidade.equals("") ? "" : "A presente procuração terá o prazo de validade até " + prazoValidade + ".");	
		modelMap.put("dataAtual", dataExtenso);
		modelMap.put("titulo", titulo);  
		
		return modelMap;
	}
	public ModelMap retornarDadosPDFMinutaProcuracao(ServletContext context, ProcuracaoDto procuracaoDTO, String[] sujeitosPassivos) {
		
		
		FormatSupport formatSupport = new FormatSupport();		
		String cnpjMF = ", CNPJ/MF sob nº ";
		String cpfMF = ", CPF/MF sob nº ";
		String oab = ", OAB/UF nº ";
		String endereco = ", com endereço profissional à ";
		
		String processos = "";
		
		String outorgantes = "";
		int contador = 0;
		
		List<Integer> processosList = new ArrayList<Integer>();
		for (String sujeitoPassivo : sujeitosPassivos) {
			SujeitoPassivoProcesso sujeitoPassivoProcesso = null;
			ProcessoAdministrativoTributarioEletronico pat = null;
			if(procuracaoDTO.getTipoPessoaOutorgante().equals("J")) {
				sujeitoPassivoProcesso = patSujeitoPassivoProcessoService.consultarSujeitoPassivoPorProcessosPJ(Integer.valueOf(sujeitoPassivo));
				
				if(contador > 0 && (contador + 1) == procuracaoDTO.getSujeitoPassivos().size()) {
					outorgantes += " e ";
				}
				else if(contador > 0) {
					outorgantes += ", ";
				}
				outorgantes += sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNomeFantasia() + cnpjMF +  formatSupport.formatarCNPJ(sujeitoPassivoProcesso.getPessoa().getPessoaJuridica().getNumeroCnpj());
				ProcessoAdministrativoTributarioEletronico processo = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico();
				outorgantes += " no Processo Administrativo Tributário nº " + formatSupport.formatarPAT(processo.getTipoDocumentoOrigem(), 
						processo.getNumeroSequencial(),
						processo.getNumeroDigitoVerificador());
				
				
			} else {
				sujeitoPassivoProcesso = patSujeitoPassivoProcessoService.consultarSujeitoPassivoPorProcessosPF(Integer.valueOf(sujeitoPassivo));
				
				if(contador > 0 && (contador + 1) == procuracaoDTO.getSujeitoPassivos().size()) {
					outorgantes += " e ";
				}
				else if(contador > 0) {
					outorgantes += ", ";
				}
				outorgantes += sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNome() + cnpjMF +  formatSupport.formatarCPF(sujeitoPassivoProcesso.getPessoa().getPessoaFisica().getNumeroCpf());
				ProcessoAdministrativoTributarioEletronico processo = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico();
				outorgantes += " no Processo Administrativo Tributário nº " + formatSupport.formatarPAT(processo.getTipoDocumentoOrigem(), 
						processo.getNumeroSequencial(),
						processo.getNumeroDigitoVerificador());
			}
			
			pat = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico();
			
			if(processosList.indexOf(pat.getIdProcessoAdministrativoTributarioEletronico()) == -1){
				if(contador > 0) {
					processos += ", ";
				}
				processos += formatSupport.formatarPAT(pat.getTipoDocumentoOrigem(), 
						pat.getNumeroSequencial(),
						pat.getNumeroDigitoVerificador());	
				processosList.add(pat.getIdProcessoAdministrativoTributarioEletronico());
			}
			contador++;	 	
		}
		
		String procuradores = "";
		contador = 0;
		
		for(ProcuradorDto procuradorDTO : procuracaoDTO.getProcuradores()) {
			if(contador > 0 && (contador + 1) == procuracaoDTO.getSujeitoPassivos().size()) { 
				procuradores += " e ";
			}
			else if(contador > 0) {
				procuradores += ", ";
			}
			procuradores += procuradorDTO.getNome() + oab + procuradorDTO.getNumeroOAB() + cpfMF + procuradorDTO.getCpf() + endereco + procuradorDTO.getEnderecoFormatado();
			contador++;
		}	
		
		
		String reservaPoderes = "";
		if(procuracaoDTO.getReservaPoderes() != null && !procuracaoDTO.getReservaPoderes().equals("")) {
			for(EnumReservaPoderPermitido enumReservaPoderPermitido : EnumReservaPoderPermitido.values()) {
				if(procuracaoDTO.getReservaPoderes().equals(enumReservaPoderPermitido.getIndiReservaPermitido())) {
					if(!procuracaoDTO.getReservaPoderes().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
						reservaPoderes = "Podendo ainda, substabelecer esta a outrem, " + enumReservaPoderPermitido.getSignificadoReservaPermitido();
						
						if(procuracaoDTO.getDataLimite() != null) {
							reservaPoderes += ", até a data limite de " + Data.formatar(procuracaoDTO.getDataLimite(), Data.DATA_PADRAO);
						}
						
						reservaPoderes += ".";
					}
				}
			}
		}
			
		
		String prazoValidade = procuracaoDTO.getDataValidade() == null ? "" : Data.formatar(procuracaoDTO.getDataValidade(), Data.DATA_PADRAO);
		
		String dataExtenso = "Goiânia, " + formatSupport.retornarDataFormatada(); 
		
		ModelMap modelMap =  new ModelMap();			
		modelMap.put("procuracaoDTO.outorgantes", outorgantes); 
		modelMap.put("procuradores", procuradores);
		modelMap.put("processo", processos);
		modelMap.put("reservaPoderes", reservaPoderes);
		modelMap.put("prazoValidade", prazoValidade == null || prazoValidade.equals("") ? "" : "A presente procuração terá o prazo de validade até " + prazoValidade + ".");	
		modelMap.put("dataAtual", "");
		modelMap.put("datasource", new JREmptyDataSource());
		
		return modelMap;
	}
	
		
}