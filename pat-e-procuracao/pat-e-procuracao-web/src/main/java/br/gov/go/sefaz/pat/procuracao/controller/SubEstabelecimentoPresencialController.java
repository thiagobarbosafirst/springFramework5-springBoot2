package br.gov.go.sefaz.pat.procuracao.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import br.gov.go.sefaz.javaee.corporativo.model.Uf;
import br.gov.go.sefaz.javaee.corporativo.service.MunicipioService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaEnderecoService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.javaee.corporativo.service.UfService;
import br.gov.go.sefaz.javaee.security.dto.PessoaAutenticadaDto;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.repository.jpa.ModeloDocumentoJpaRepository;
import br.gov.go.sefaz.pat.documento.service.ModeloDocumentoService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.filter.ProcessSearchFilter;
import br.gov.go.sefaz.pat.model.ModeloDocumento;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.service.DocumentoService;
import br.gov.go.sefaz.pat.procuracao.service.EmailService;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.SubEstabelecimentoService;
import br.gov.go.sefaz.pat.procuracao.service.TelefoneService;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoConverter;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoSupport;
import br.gov.go.sefaz.pat.procuracao.validation.input.SubstabelecimentoInputValidator;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
import br.gov.go.sefaz.pat.support.formatting.UfComparator;
import br.gov.go.sefaz.pat.web.support.Support;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.SUBSTABELECIMENTO_PRESENCIAL_PATH_ROOT)
public class SubEstabelecimentoPresencialController {	
	
	private static Logger logger = LogManager.getLogger(SubEstabelecimentoPresencialController.class);
	
	@Autowired
	SubEstabelecimentoService subEstabelecimentoService;
	
	@Autowired
	ProcuracaoService procuracaoService;
	
	@Autowired
	PatSujeitoPassivoProcessoService patSujeitoPassivoProcessoService; 
	
	@Autowired 
	PessoaFisicaService pessoaFisicaService;
	
	@Autowired
	PessoaService pessoaService;
	
	@Autowired
	PessoaJuridicaService pessoaJuridicaService;
	
	@Autowired
	MunicipioService municipioService;
	
	@Autowired
	UfService ufService;	
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	TelefoneService telefoneService;
	
	@Autowired
	DocumentoService documentoService;
	
	@Autowired
	MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	PessoaEnderecoService pessoaEnderecoService;
	
	@Autowired
	ModeloDocumentoJpaRepository modeloDocumentoJpaRepository; 
	
	@Autowired
	PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private SubstabelecimentoInputValidator substabelecimentoInputValidator;
	
	@Autowired
	private ProcuracaoConverter procuracaoConverter;
	
	@Autowired
	private ModeloDocumentoService modeloDocumentoService;
	
	@Autowired
	private ProcuracaoSupport procuracaoSupport; 
	
	@GetMapping(ControllerMappingConfigProcuracao.SUBSTAB_PRESENCIAL_PATH_ACTION_NEW) 
	public @ResponseBody ModelAndView inicializarSubstabelecimento(ProcuracaoDto procuracaoDto, @PathVariable Integer idProcuracao, @PathVariable Integer idPessoaProcurador) {
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.SUBSTAB_PRESENCIAL_VIEW_FORM);	 
		List<Uf> listUf = ufService.listar();
		listUf.sort(new UfComparator());		
		mv.addObject(ControllerMappingConfigProcuracao.UF_MODEL_ENTITIES_NAME,listUf);		
		mv.addObject("regimesFiscais", montarListaRegimeFiscal());	
		procuracaoDto.setId(idProcuracao);
		procuracaoDto.setIdOutorgante(idPessoaProcurador);
		mv.addObject("listaReservaPoderes", montarListaTipoReservaPoder(procuracaoDto));		
		List<AgrupadorMovimentacaoDto> listaProcessos = movimentacaoProcuracaoService.findSubstabelecerByIdPessoaProcuradorAndProcuracao(idPessoaProcurador, idProcuracao);
		procuracaoDto.setListaProcessos(listaProcessos);
		procuracaoDto.setIndiSubstabelecimentoPresencial(String.valueOf(EnumSimNao.S.getValor())); 
		Procuracao procuracao = procuracaoService.find(idProcuracao);
		procuracaoDto.setDataValidade(procuracao.getDataValidade()); 
		EnumModeloDocumentEcm[] modelos = {EnumModeloDocumentEcm.SUBESTABELECIMENTO_DA_PROCURACAO_ELETRONICA, EnumModeloDocumentEcm.ANEXO_DO_SUBESTABELECIMENTO}; 
		List<ModeloDocumento> modelosDeDocumento = modeloDocumentoService.findByEnumModelo(Arrays.asList(modelos));
		mv.addObject("modelosDeDocumento",modelosDeDocumento);
		return mv;	 			 
	}
	
	private List<RegimeFiscal> montarListaRegimeFiscal() {
		List<RegimeFiscal> listaRegimeFiscais = new ArrayList<RegimeFiscal>();
		listaRegimeFiscais.add(RegimeFiscal.MICRO_EMPRESA);
		listaRegimeFiscais.add(RegimeFiscal.NORMAL);
		return listaRegimeFiscais;
	}
	
	private List<EnumReservaPoderPermitido> montarListaTipoReservaPoder(ProcuracaoDto procuracaoDto) {
		List<EnumReservaPoderPermitido> listaPoderPermitido = new ArrayList<EnumReservaPoderPermitido>();
		Procuracao procuracao = new Procuracao();
		procuracao = procuracaoService.find(procuracaoDto.getId());
		if(procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.ComOuSemReserva.getIndiReservaPermitido())) {
			listaPoderPermitido.add(EnumReservaPoderPermitido.SomenteComReserva);
			listaPoderPermitido.add(EnumReservaPoderPermitido.SomenteSemReserva);
			listaPoderPermitido.add(EnumReservaPoderPermitido.ComOuSemReserva);  
		}
		
		if(procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.SomenteComReserva.getIndiReservaPermitido())) { 
			procuracaoDto.setReservaPoderesSubestabelecimento(String.valueOf(EnumReservaPoderPermitido.SomenteComReserva.getIndiReservaPermitido()));
			listaPoderPermitido.add(EnumReservaPoderPermitido.SomenteComReserva);
		}
		
		if(procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.SomenteSemReserva.getIndiReservaPermitido())) {
			procuracaoDto.setReservaPoderesSubestabelecimento(String.valueOf(EnumReservaPoderPermitido.SomenteSemReserva.getIndiReservaPermitido()));
			listaPoderPermitido.add(EnumReservaPoderPermitido.SomenteSemReserva);
		}
		
		if(procuracao.getTipoPoderSubstabelecimento().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
			procuracaoDto.setReservaPoderesSubestabelecimento(String.valueOf(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido()));
			listaPoderPermitido.add(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer); 
		}
		
		return listaPoderPermitido;
	}
	
	@PostMapping(ControllerMappingConfigProcuracao.PATH_ACTION_SAVE)
	public @ResponseBody String save(@ModelAttribute  ProcuracaoDto procuracaoDto, HttpServletRequest request, BindingResult result, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws IllegalAccessException, 
	InvocationTargetException, NoSuchMethodException, SefazException{
		 
		logger.debug("Files " + procuracaoDto.getFiles());
		if(procuracaoDto.getFiles() != null) {
			logger.debug("Files " + procuracaoDto.getFiles().size()); 
		}
		
		try {
			substabelecimentoInputValidator.validateSave(procuracaoDto, result);	
			
			if (!result.hasErrors()) {
				try {				
					
					SubEstabelecimentoProcuracao subestabelecimento = procuracaoConverter.toSubestabelecimento(procuracaoDto);
					
					PessoaAutenticadaDto pessoa = usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada();
					
					subEstabelecimentoService.prepararSubstabelecimento(subestabelecimento);					
					List<PecaEletronicaDto> listaPecaEletronicaDto = subEstabelecimentoService.criarPecasSubstabelecimento(usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada().getId(), procuracaoDto.getIdOutorgante(), subestabelecimento, procuracaoDto.getFiles());
					logger.debug("subEstabelecimentoPresencialController.save listaPecaEletronicaDto.size" + listaPecaEletronicaDto == null ? 0 : listaPecaEletronicaDto.size()); 
					subEstabelecimentoService.saveSubstabelecimento(pessoa.getId(), pessoa, subestabelecimento, Support.MATRICULA_USUARIO_GESTOR, listaPecaEletronicaDto);
					
					return Support.getRedirect(request) + "/substabelecimentoPresencial/listSubstabelecimento";   
				} catch (SefazException se) {
					throw new AjaxRequestException(se.getMessage());
				} catch (Exception e) {
					throw new AjaxRequestException(e.getMessage());
				}	
			} 
			throw new AjaxRequestException("Erro ao tentar substabelecer os poderes");
		} catch (SefazValidationException e) {
			throw new AjaxRequestException(e.getMessage());
		}
				
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST_SUBSTABELECIMENTO)
	public ModelAndView listarPorProcurador(ProcessSearchFilter pesquisa, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws SefazException{ 	
		
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.SUBSTAB_PRESENCIAL_VIEW_LIST);
		
		List<AgrupadorMovimentacaoDto> lista = null;
		Integer idPessoa = null;		
		try {
			if(pesquisa != null && pesquisa.getTexto() != null){
				
				idPessoa = procuracaoSupport.consultaIdPessoaSujeitoPassivo(pesquisa);
				
				if(idPessoa == null) {
					boolean idPessoaSearch = false;
					mv.addObject("idPessoa", idPessoaSearch);
				}else {
					lista = movimentacaoProcuracaoService.findSubstabelecimento(idPessoa);
					
					if(lista.isEmpty()) {
						boolean listaIsEmpty = false;			
						mv.addObject("listaIsEmpty", listaIsEmpty);
					}else {
						mv.addObject("listaSubstabelecimento", lista);   
						mv.addObject("idPessoaProcurador",idPessoa);
						mv.addObject("pesquisa", pesquisa);
					}
				}
			}
		} catch (Exception e) {
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, "Ocorreu um problema inesperado, tente novamente em instantes.");
		}
		return mv;	
	}	
}
