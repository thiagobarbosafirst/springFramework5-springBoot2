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
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.itextpdf.text.DocumentException;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.exception.SefazValidationException;
import br.gov.go.sefaz.javaee.corporativo.constants.RegimeFiscal;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.Uf;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.javaee.corporativo.service.UfService;
import br.gov.go.sefaz.javaee.security.dto.PessoaAutenticadaDto;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticado;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.enumerator.EnumStatusAssinaturaPeca;
import br.gov.go.sefaz.pat.enumerator.EnumTipoAssinantePeca;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcessoSujeitoPassivoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.SujeitoPassivoDto;
import br.gov.go.sefaz.pat.procuracao.service.AgrupadorMovimentacaoService;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoConverter;
import br.gov.go.sefaz.pat.procuracao.validation.input.ProcuracaoInputValidator;
import br.gov.go.sefaz.pat.service.AssinantePecaService;
import br.gov.go.sefaz.pat.service.PatSignatureService;
import br.gov.go.sefaz.pat.service.PatUploadService;
import br.gov.go.sefaz.pat.support.formatting.UfComparator;
import br.gov.go.sefaz.pat.web.support.Support;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.PROCURACAO_PATH_ROOT)
public class ProcuracaoController {	
	
	private static Logger logger = LogManager.getLogger(ProcuracaoController.class);
	
	@Autowired
	private ProcuracaoService procuracaoService;	
	@Autowired
	private PessoaService pessoaService;	
	@Autowired
	private UfService ufService;	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	@Autowired
	private PatUploadService patUploadService;
	@Autowired
	private PatSignatureService patSignatureService;
	@Autowired
	private AssinantePecaService assinantePecaService;
	@Autowired
	private ProcuracaoInputValidator procuracaoInputValidator;
	@Autowired
	private PecaProcuracaoService pecaProcuracaoService;
	@Autowired
	private AgrupadorMovimentacaoService agrupadorMovimentacaoService;
	@Autowired
	MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_NEW)
	
	public ModelAndView add(ProcuracaoDto procuracaoDto, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){	
		UsuarioAutenticado usuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado();
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURACAO_VIEW_FORM);		
		List<Uf> listUf = ufService.listar();
		listUf.sort(new UfComparator());		
		mv.addObject(ControllerMappingConfigProcuracao.UF_MODEL_ENTITIES_NAME,listUf);

		List<EnumReservaPoderPermitido> listaTipoPoderesSubstabelecimento = new ArrayList<EnumReservaPoderPermitido>(Arrays.asList(EnumReservaPoderPermitido.values()));
		listaTipoPoderesSubstabelecimento.remove(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer);
		
		mv.addObject("usuarioAutenticado", usuarioAutenticado.getFromType());
		mv.addObject("tipoPoderesSubstabelecimento", listaTipoPoderesSubstabelecimento);
		
		mv.addObject("regimesFiscais", montarListaRegimeFiscal());		
		procuracaoDto = procuracaoService.criarProcuracaoDto(procuracaoDto, usuarioAutenticadoDetails, Procuracao.TIPO_NATUR_JUR_PARTICULAR);	 	
		return mv;	 	
	}	
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST)
	public ModelAndView listarPorSujeitoPassivo(ProcuracaoDto procuracaoDto, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){	
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURACAO_VIEW_LIST);

		List<AgrupadorMovimentacaoDto> lista = null;
		try {
			if(usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada().getTipoPessoa() == 'J') {
				mv.addObject("pessoaJuridica", PessoaAutenticadaDto.PESSOA_JURIDICA);
				Pessoa pessoa = pessoaService.consultar(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());
				lista = agrupadorMovimentacaoService.listarProcuracaoPorSujeitoPassivoPJ(pessoa.getPessoaJuridica().getEmpresa().getNumeroCnpjBase());
			} else {
				mv.addObject("pessoaFisica", PessoaAutenticadaDto.PESSOA_FISICA);
				Pessoa pessoa = pessoaService.consultar(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());
				lista = agrupadorMovimentacaoService.listarProcuracaoPorSujeitoPassivoPF(pessoa.getPessoaFisica().getNumeroCpf());
			}
		} catch (SefazValidationException e) {
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getMessage());
		}
		
		mv.addObject("listaProcuracao", lista); 
		return mv;				
	}	
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST_PROCURADOR)
	public ModelAndView listarPorProcurador(ProcuracaoDto procuracaoDto, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){	
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURACAO_POR_PROCURADOR_VIEW_LIST);
		
		try {
			List<AgrupadorMovimentacaoDto> lista = agrupadorMovimentacaoService.listarProcuracaoPorProcurador(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());
			mv.addObject("listaProcuracao", lista); 
		} catch (SefazValidationException e) {
			logger.error("Erro ao consultar procurador. Usuario " + usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa() + " " + e.getStackTrace());
			e.printStackTrace();
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getMessage()); 
		}	
		
		return mv;				
	}	
	
	@PostMapping(ControllerMappingConfigProcuracao.PATH_ACTION_SAVE)
	public ModelAndView save(@RequestParam(name="sujeitosPassivos[]",required=false) String[] sujeitosPassivos, ProcuracaoDto procuracaoDto, BindingResult result, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws IllegalAccessException, 
	InvocationTargetException, NoSuchMethodException, SefazException{
		
		try {
			procuracaoInputValidator.validateSave(procuracaoDto, sujeitosPassivos, result);	 
			if (!result.hasErrors()) {
				try {				
					List<Procuracao> listaProcuracao = ProcuracaoConverter.toListProcuracao(procuracaoDto, sujeitosPassivos);
					procuracaoService.prepararProcuracao(listaProcuracao);
					procuracaoService.saveProcuracao(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), listaProcuracao, Support.MATRICULA_USUARIO_GESTOR);
					ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.PROCURACAO_VIEW_LIST_REDIRECT);
					return mv;
				} catch (SefazException se) {
					ModelAndView mv = add(procuracaoDto, usuarioAutenticadoDetails);
					mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, se.getMessage());
					return mv;	
				} catch (Exception e) {
					ModelAndView mv = add(procuracaoDto, usuarioAutenticadoDetails);
					mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, "Ocorreu algum problema ao salvar a procuração. " + e.getMessage());
					return mv;
				}	
			}
		} catch (SefazValidationException e) {
			ModelAndView mv = add(procuracaoDto, usuarioAutenticadoDetails);
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getMessage());
			return mv;
		}
		
		ModelAndView mv = add(procuracaoDto, usuarioAutenticadoDetails);
		return mv;
				
	}	
	
	@PostMapping("/minuta/{id}") 
	public ModelAndView gerarMinuta(@RequestParam(name="sujeitosPassivos[]", required=false) String[] sujeitosPassivos, HttpServletRequest request, HttpServletResponse response, ProcuracaoDto procuracaoDto, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) {
		
		try {			
			
			List<String> listaSujeitoPassivo = new ArrayList<String>();
			
			for (ProcessoSujeitoPassivoDto processoSujeitoPassivoDto : procuracaoDto.getSujeitoPassivos()) {
			
				for (SujeitoPassivoDto sujeitoPassivoDto :processoSujeitoPassivoDto.getListaSujeitoPassivo()) {
					
					for (int contSPAux = 0; contSPAux < sujeitosPassivos.length; contSPAux++) {
						if(sujeitosPassivos[contSPAux].equals(String.valueOf(sujeitoPassivoDto.getId()))) {
							listaSujeitoPassivo.add(sujeitosPassivos[contSPAux]);
						}
					}					
				}
			}
			
			String[] sujeitosPassivosMinuta = new String[listaSujeitoPassivo.size()];
			for (int i=0; i<listaSujeitoPassivo.size();i++) {
				sujeitosPassivosMinuta[i] = listaSujeitoPassivo.get(i);
			}
			
			
			ModelMap modelMap = procuracaoService.retornarDadosPDFMinutaProcuracao(request.getServletContext(), procuracaoDto, sujeitosPassivosMinuta); 			
			return new ModelAndView(ControllerMappingConfigProcuracao.MINUTA_PROCR_REPORT_PATH_JASPER_RESOLVER_PJ, modelMap);
			
		} catch (Exception e) {
			throw new AjaxRequestException("Erro ao gerar a minuta da procuração.");
		}
	}	
	
	@PostMapping(value="/verifyStep/{id}", produces= MediaType.APPLICATION_JSON_VALUE)	
	public @ResponseBody Integer verifyStep(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Integer step = procuracaoService.checkStepOfProcuration(id);
		if (step != null)
			return step;
		else
			throw new AjaxRequestException("Erro ao verificar a etapa da procuração.");
	}

	@GetMapping("/download/{id}")   
	public void gerarPecaProcuracao(HttpServletRequest request, HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, KeyStoreException, 
					NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		
		PecaProcuracao pecaProcuracao = null;
		PecaEletronicaDto pecaEletronicaDto = null;
		
		logger.debug("Procuração -> Antes de procurar a peça");
		pecaProcuracao = this.pecaProcuracaoService.findPecaProcuracao(id, EnumModeloDocumentEcm.PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		
		try {
			if(pecaProcuracao == null){
				
				logger.debug("Procuração -> Não encontrou a peça. Tentando criar");
				
				try {
					pecaEletronicaDto = procuracaoService.criarPecaEletronica(request.getServletContext(), usuarioAutenticadoDetails, id);
				} catch (Exception e) {
					logger.debug("Procuração -> Erro ao criar a peça eletrônica " + e.getMessage());
					throw new SefazException("Ocorreu um problema ao criar a peça eletrônica. ");
				}
				
				logger.debug("Procuração -> Conseguiu criar a peça. Tentando salvar a peça no UCM e oracle.");
				
				try {
					pecaProcuracaoService.savePecaProcuracaoAndUcmDocument(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), id, pecaEletronicaDto);
				} catch (Exception e) {
					logger.debug("Procuração -> Erro ao salvar a peça." + e.getStackTrace());
					throw new SefazException("Ocorreu um problema ao tentar salvar a peça eletrônica. ");
				}
			} else {
				pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaProcuracao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica()); 				
			}
			//envia a resposta com o MIME Type PDF
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			//força a abertura de download
			response.setHeader("Content-disposition", "attachment; filename="+pecaEletronicaDto.getNomePecaEletronica());
		}catch (Exception e) {
			throw new SefazException("Ocorreu um problema ao fazer o download do arquivo. " + e.getMessage());
		}
		
		OutputStream outPut = response.getOutputStream();
		outPut.write(pecaEletronicaDto.getContent());
		outPut.close(); 		
	}
	
	@PostMapping("/upload/{id}")
	@ResponseStatus(HttpStatus.OK)
    public void upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id, HttpServletRequest request) throws AjaxRequestException, IOException, SefazException, DocumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException {			
		
		ServletContext context = request.getServletContext(); 
		Integer idUsuarioLogado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
		// Validação de arquivo (modal)
		patUploadService.validarArquivo(file);    		

		PecaProcuracao pecaProcuracao = pecaProcuracaoService.findPecaProcuracao(id, EnumModeloDocumentEcm.PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		
		if(pecaProcuracao != null){
			
			PecaEletronicaDto pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaProcuracao.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			Procuracao procuracao = pecaProcuracao.getProcuracao(); 
			List<MovimentacaoProcuracao> listaMovimentacao = movimentacaoProcuracaoService.findByProcuracao(procuracao.getId());
			
			//validação arquivo PKCS7
			patSignatureService.validarArquivoCades(pecaEletronicaDto.getContent(), file);
			assinantePecaService.saveAssinantePeca(idUsuarioLogado, EnumStatusAssinaturaPeca.ASSINADO_DIGITALMENTE, 
					EnumTipoAssinantePeca.DEFESA, pecaEletronicaDto.getIdPecaEletronica(), file.getBytes());
			
			/*// Validação de certificado
			patSignatureService.validarArquivo(file, pecaEletronicaDto.getHashCode(), context.getRealPath(ConstantesKeyStore.keyStorePathTest));
			pecaEletronicaDto.setHashCode(Hash.generate(Hash.ALGORITHM_SHA_256, file.getBytes()));
			pecaEletronicaDto.setContent(file.getBytes());*/		
			
			try{
				PecaEletronicaDto pecaReciboJuntada = procuracaoService.gerarReciboDeJuntada(usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada().getId(), usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada().getId(), listaMovimentacao, pecaEletronicaDto.getNomePecaEletronica(), "Termo de Juntada Procuração");
				procuracaoService.ativarProcuracao(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), procuracao, pecaEletronicaDto, pecaReciboJuntada);				
			}catch(Exception e){
				throw new AjaxRequestException("Erro ao salvar a procuração."); 
			}
			
		} else {
			throw new AjaxRequestException("Não conseguimos identificar o documento gerado, por favor faça download do documento novamente.");
		}
    }
	
	private List<RegimeFiscal> montarListaRegimeFiscal() {
		List<RegimeFiscal> listaRegimeFiscais = new ArrayList<RegimeFiscal>();
		listaRegimeFiscais.add(RegimeFiscal.MICRO_EMPRESA);
		listaRegimeFiscais.add(RegimeFiscal.NORMAL);
		return listaRegimeFiscais;
	}
	
}