package br.gov.go.sefaz.pat.procuracao.controller;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.corporativo.constants.RegimeFiscal;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.model.Uf;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.corporativo.service.UfService;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.ModeloDocumentoService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.filter.ProcessSearchFilter;
import br.gov.go.sefaz.pat.model.ModeloDocumento;
import br.gov.go.sefaz.pat.model.PecaProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.service.AgrupadorMovimentacaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoConverter;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoSupport;
import br.gov.go.sefaz.pat.procuracao.validation.input.ProcuracaoInputValidator;
import br.gov.go.sefaz.pat.support.formatting.UfComparator;
import br.gov.go.sefaz.pat.web.support.Support;
import net.sf.jasperreports.engine.JRException;
@Controller
@RequestMapping("/procuracaoPresencial")
public class ProcuracaoPresencialController {
	
	@Autowired
	private AgrupadorMovimentacaoService agrupadorMovimentacaoService;
	
	@Autowired
	private ProcuracaoService procuracaoService;
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private UfService ufService;
	@Autowired
	private ModeloDocumentoService modeloDocumentoService;
	
	@Autowired
	private ProcuracaoInputValidator procuracaoInputValidator;
	@Autowired
	private PessoaFisicaService pessoaFisicaService;
	@Autowired
	private PessoaJuridicaService pessoaJuridicaService;
	@Autowired
	private PecaProcuracaoService pecaProcuracaoService;
	@Autowired
	private ProcuracaoSupport procuracaoSupport; 
	
	@GetMapping("/search")
	public ModelAndView init(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){	
		return new ModelAndView("procuracaoPresencial/procuracaoPresencial-search");
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST_PROCURADOR)
	public ModelAndView listarPorProcurador(ProcessSearchFilter pesquisa, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){	
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURACAO_PRESENCIAL_PROCURADOR_VIEW_LIST);
		
		List<AgrupadorMovimentacaoDto> lista = null;
		Integer idProcurador = null;		
		try {
			if(pesquisa != null && pesquisa.getTexto() != null){
				
				idProcurador = procuracaoSupport.consultaIdPessoaSujeitoPassivo(pesquisa);
				
				if(idProcurador == null) {
					boolean idProcuradorSearch = false;
					mv.addObject("idProcuradorSearch", idProcuradorSearch);
				}else {
					lista = agrupadorMovimentacaoService.listarProcuracaoPorProcurador(idProcurador);
					
					if(lista.isEmpty()) {
						boolean listaIsEmpty = false;			
						mv.addObject("listaIsEmpty", listaIsEmpty);
					}else {
						mv.addObject("listaProcuracao", lista); 
						mv.addObject("pesquisa", pesquisa); 
					}
				}
			}
		} catch (Exception e) {
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getLocalizedMessage());
		}
		return mv;				
	}
	
	@GetMapping("/list")
	public ModelAndView list(ProcessSearchFilter pesquisa, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws InterruptedException{	
		ModelAndView mv = new ModelAndView("procuracaoPresencial/procuracaoPresencial-list");
		
		return agrupadorMovimentacaoService.listarPorSujeitoPassivo(pesquisa, mv);
	}
	
	@GetMapping("/form")
	public ModelAndView form(ProcessSearchFilter pesquisa, ProcuracaoDto procuracaoDto, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){
		ModelAndView mv = new ModelAndView("procuracaoPresencial/procuracaoPresencial-form");
		PessoaFisica pessoaFisicaConsultada = null;
		PessoaJuridica pessoaJuridicaConsultada = null;
				
		try {
			if(pesquisa != null && !pesquisa.getTexto().isEmpty()){
				if(pesquisa.getTexto().length() == 11){
					pessoaFisicaConsultada = pessoaFisicaService.consultarPorCpf(pesquisa.getTexto());
					if(pessoaFisicaConsultada == null) {
						return new ModelAndView("procuracaoPresencial/procuracaoPresencial-search")
								.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, "Não foi encontrado nenhuma pessoa com o CPF informado.");
					}
					procuracaoDto.setIdOutorgante(pessoaFisicaConsultada.getIdPessoa());
					procuracaoDto.setNomeOutorgante(pessoaFisicaConsultada.getNome());
				} else {
					pessoaJuridicaConsultada = pessoaJuridicaService.consultarPorCnpj(pesquisa.getTexto());
					if(pessoaJuridicaConsultada == null) {
						return new ModelAndView("procuracaoPresencial/procuracaoPresencial-search")
								.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, "Não foi encontrado nenhuma pessoa com o CNPJ informado.");
					}
					procuracaoDto.setIdOutorgante(pessoaJuridicaConsultada.getIdPessoa());
					procuracaoDto.setNomeOutorgante(pessoaJuridicaConsultada.getEmpresa().getNomeEmpresa());
				}
			}
		} catch (NullPointerException npe) {
			return init(usuarioAutenticadoDetails);
		}
		
		EnumModeloDocumentEcm[] modelos = {EnumModeloDocumentEcm.PROCURACAO_ELETRONICA, EnumModeloDocumentEcm.ANEXO_DA_PROCURACAO}; 
		List<ModeloDocumento> modelosDeDocumento = modeloDocumentoService.findByEnumModelo(Arrays.asList(modelos));
		mv.addObject("modelosDeDocumento",modelosDeDocumento);
		
		List<Uf> listUf = ufService.listar();
		listUf.sort(new UfComparator());		
		mv.addObject("ufs", listUf);		
	
		mv.addObject("regimesFiscais", montarListaRegimeFiscal());
		
		procuracaoDto.setIndiProcuracaoPresencial("S");
		procuracaoDto.setTipoNaturezaJuridica(Procuracao.TIPO_NATUR_JUR_PARTICULAR);
		try {
			procuracaoDto = procuracaoService.criarProcuracaoDtoForProcuracaoPresencial(procuracaoDto, usuarioAutenticadoDetails);
		} catch (SefazException e) {
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getMessage());
		}
		mv.addObject("procuracaoDto", procuracaoDto);
		
		return mv;
	}
		 
	@PostMapping("/save")
	public @ResponseBody String save(			 
			@ModelAttribute ProcuracaoDto procuracaoDto,
			BindingResult result,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails
			) throws IllegalAccessException, NoSuchMethodException{
		
		try {
			procuracaoInputValidator.validateSave(procuracaoDto, procuracaoDto.getListaSujeitoPassivos(), result);
			procuracaoDto.setIndiProcuracaoPresencial("S");
			if (!result.hasErrors()) {
				try {				
					
					List<Procuracao> listaProcuracao = ProcuracaoConverter.toListProcuracao(procuracaoDto, procuracaoDto.getListaSujeitoPassivos());
					procuracaoService.prepararProcuracaoPresencial(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), procuracaoDto.getIdOutorgante(), listaProcuracao, procuracaoDto.getFiles());
					procuracaoService.saveProcuracao(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), listaProcuracao, Support.MATRICULA_USUARIO_GESTOR); 

					return Support.getRedirect(request) + "/procuracaoPresencial/list";
				} catch (SefazException se) {
					throw new AjaxRequestException(se.getMessage());	
				} catch (Exception e) {
					throw new AjaxRequestException(e.getMessage());
				}	
			}
		} catch (SefazValidationException e) {
			throw new AjaxRequestException(e.getMessage());
		}
		
		throw new AjaxRequestException("Erro ao Salvar a Procuração");
				
	}	
	@GetMapping("/download/{id}")   
	public void gerarPecaProcuracao(HttpServletRequest request, HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, KeyStoreException, 
					NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		
		PecaProcuracao pecaProcuracao = null;
		PecaEletronicaDto pecaEletronicaDto = null;
		
		pecaProcuracao = this.pecaProcuracaoService.findPecaProcuracao(id, EnumModeloDocumentEcm.PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		
		try {
			if(pecaProcuracao == null){
								
				pecaEletronicaDto = procuracaoService.criarPecaEletronica(request.getServletContext(), usuarioAutenticadoDetails, id);
				pecaProcuracaoService.savePecaProcuracaoAndUcmDocument(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), id, pecaEletronicaDto);
			} else {
				pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaProcuracao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica()); 				
			}
			//envia a resposta com o MIME Type PDF
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			//força a abertura de download
			response.setHeader("Content-disposition", "attachment; filename="+pecaEletronicaDto.getNomePecaEletronica());
		}catch (Exception e) {
			throw new SefazException("Ocorreu um problema ao fazer o download do arquivo. ");
		}
		
		OutputStream outPut = response.getOutputStream();
		outPut.write(pecaEletronicaDto.getContent());
		outPut.close(); 		
	}
	
	private List<RegimeFiscal> montarListaRegimeFiscal() {
		List<RegimeFiscal> listaRegimeFiscais = new ArrayList<RegimeFiscal>();
		listaRegimeFiscais.add(RegimeFiscal.MICRO_EMPRESA);
		listaRegimeFiscais.add(RegimeFiscal.NORMAL);
		return listaRegimeFiscais;
	}
}