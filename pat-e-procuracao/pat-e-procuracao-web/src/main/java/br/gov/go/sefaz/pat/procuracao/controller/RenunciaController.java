package br.gov.go.sefaz.pat.procuracao.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.Optional;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
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
import br.gov.go.sefaz.javaee.commons.support.Hash;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticado;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.javaee.web.pagination.PaginationConfig;
import br.gov.go.sefaz.javaee.web.thymeleaf.constants.MessageElementTagParameters;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumProfilesDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumSubCategoriaDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.enumerator.EnumStatusAssinaturaPeca;
import br.gov.go.sefaz.pat.enumerator.EnumTipoAssinantePeca;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaRenunciaProcuracao;
import br.gov.go.sefaz.pat.model.RenunciaProcuracao;
import br.gov.go.sefaz.pat.procuracao.constants.Messages;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.service.DocumentoService;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaRenunciaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.RenunciaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.validation.business.RenunciaBusinessValidator;
import br.gov.go.sefaz.pat.procuracao.validation.input.ProcuracaoInputValidator;
import br.gov.go.sefaz.pat.service.AssinantePecaService;
import br.gov.go.sefaz.pat.service.PatSignatureService;
import br.gov.go.sefaz.pat.service.PatUploadService;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.RENUNCIA_PATH_ROOT)
public class RenunciaController {	
	
	@Autowired
	PessoaService pessoaService;
	
	@Autowired
	private DocumentoService documentoService;
				
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	private RenunciaProcuracaoService renunciaProcuracaoService;
			
	@Autowired
	private PatUploadService patUploadService;
	
	@Autowired
	private PatSignatureService patSignatureService;
	
	@Autowired
	private AssinantePecaService assinantePecaService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaRenunciaProcuracaoService pecaRenunciaProcuracaoService;
		
	@Autowired
	private RenunciaBusinessValidator renunciaBusinessValidator;
	
	@Autowired
	private ProcuracaoInputValidator procuracaoInputValidator;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService;

	@GetMapping(value={ControllerMappingConfigProcuracao.PATH_ACTION_NEW,ControllerMappingConfigProcuracao.RENUNCIA_PATH_NEW_BY_PROCURACAO})
	public ModelAndView add(@PathVariable Optional<Integer> idProcuracao,@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails,
			RedirectAttributes redirectAttributes){			
		
		UsuarioAutenticado usuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado();
		
		Pessoa pessoa = pessoaService.consultar(usuarioAutenticado.getIdPessoa());
		Boolean pessoaJuridica = usuarioAutenticado.getPessoaAutenticada().getTipoPessoa().equals('J');
		
		DocumentoAdvogado documentoAdvogado = documentoService.getDocumentoAdvogado(pessoa.getIdPessoa());		
		List<AgrupadorMovimentacaoDto> processosRenunciar;
		
		processosRenunciar = (idProcuracao.isPresent()) ? 
				renunciaProcuracaoService.findRenunciarByIdPessoaProcuradorAndProcuracao(pessoa.getIdPessoa(), idProcuracao.get()) :
				renunciaProcuracaoService.findRenunciarByIdPessoaProcurador(pessoa.getIdPessoa());
								
		String nomePessoa = pessoaJuridica ? pessoa.getPessoaJuridica().getNomeFantasia() : pessoa.getPessoaFisica().getNome();	
		String documentoPessoa = pessoaJuridica ? pessoa.getPessoaJuridica().getNumeroCnpj() : pessoa.getPessoaFisica().getNumeroCpf();
		String tipoDocumentoPessoa = pessoaJuridica ? "CNPJ" : "CPF";
		
		String numeroDocumentoOAB = (documentoAdvogado != null) ? documentoAdvogado.getNumeroDocumento() : "---";
		String ufDocumentoOAB = (documentoAdvogado != null) ? documentoAdvogado.getUf().getCodigo() : "---";
								
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.RENUNCIA_VIEW_FORM);	
		
		mv.addObject("idPessoa",pessoa.getIdPessoa());
		mv.addObject("nomePessoa",nomePessoa);
		mv.addObject("documentoPessoa",documentoPessoa);	
		mv.addObject("tipoDocumentoPessoa",tipoDocumentoPessoa);		
		
		mv.addObject("numeroDocumentoOAB",numeroDocumentoOAB);
		mv.addObject("ufDocumentoOAB",ufDocumentoOAB);		
		
		mv.addObject(PaginationConfig.PAGE_MODEL_NAME,processosRenunciar);
		
		return mv;		
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST)
	public ModelAndView list(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, 
			HttpServletRequest httpRequest, Pageable pageable){	
		
		Integer idPessoa = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
		List<AgrupadorMovimentacaoDto> listaRenuncias = renunciaProcuracaoService.findRenunciadosByIdPessoaProcurador(idPessoa);
								
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.RENUNCIA_VIEW_LIST); 
		mv.addObject(PaginationConfig.PAGE_MODEL_NAME,listaRenuncias);
		
		return mv;		
	}
	
	@PostMapping(ControllerMappingConfigProcuracao.PATH_ACTION_SAVE)	
	public ModelAndView save(@RequestParam(name="detalhe[]",required=false) Integer[] detalhes,
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails,  RedirectAttributes redirectAttributes) {
				
		try {
			
			procuracaoInputValidator.validateItens(detalhes);
			renunciaBusinessValidator.validateSaveEletronica(detalhes);			
			
			Integer idPessoaCadastro = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
			renunciaProcuracaoService.criarRenunciaEletronica(idPessoaCadastro, detalhes);
			
			redirectAttributes.addFlashAttribute(MessageElementTagParameters.FORM_SUCCESS_MESSAGE_KEY, 
					String.format(Messages.SUCCESSFULLY_SAVED_PADRAO, ControllerMappingConfigProcuracao.PROCURACAO_BUSINESS_NAME,"",""));
			
			return new ModelAndView(ControllerMappingConfigProcuracao.RENUNCIA_VIEW_LIST_REDIRECT);
			
		} catch (Exception e) {
			
			boolean eSefazException = (e instanceof SefazValidationException) || (e instanceof SefazException);
			String mensagem = eSefazException ? e.getMessage() : "Um problema ocorreu impedindo o salvamento da renúncia. Verifique e tente novamente.";	
			
			redirectAttributes.addFlashAttribute(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY,mensagem);			
			return new ModelAndView(ControllerMappingConfigProcuracao.RENUNCIA_VIEW_FORM_REDIRECT);
		}
					
	}
	
	@PostMapping(value="/verifyStep/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer verifyStep(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		
		Integer step = renunciaProcuracaoService.checkStepOfRenuncia(id);
		
		if (step != null)
			return step;
		else
			throw new AjaxRequestException("Erro ao verificar a etapa da Renúncia da Procuração.");
	}
	
	@PostMapping("/minuta") 
	public void gerarMinuta(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, 
			@RequestParam(name="detalhe[]",required=false) Integer[] detalhes, HttpServletRequest request, HttpServletResponse response)
			throws SefazException,JRException, IOException{ 
		
		byte[] pdfData = null;
		
		List<MovimentacaoProcuracao> detalhesProcuracao = movimentacaoProcuracaoService.findByIdIn(detalhes);
		ServletContext context = request.getServletContext();
		Integer usuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
		
		JasperPrint jasperPrint = pecaRenunciaProcuracaoService.getDocumentoRenuncia(detalhesProcuracao, true,usuarioAutenticado, context);

		if (jasperPrint != null){											
			pdfData = JasperExportManager.exportReportToPdf(jasperPrint);	
		}				
		else
			throw new SefazException("Falha no carregamento do relatório de minuta de renúncia.");
		
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);                
		OutputStream outPut = response.getOutputStream();
		outPut.write(pdfData);
		outPut.close();				
	}
	
	@GetMapping("/download/{id}")   
	public @ResponseBody byte[] gerarRenunciaProcuracao(HttpServletRequest request, HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, 
					KeyStoreException, NoSuchAlgorithmException, IllegalAccessException,InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		try {
			
			RenunciaProcuracao renuncia = renunciaProcuracaoService.findRenunciaById(id);
			String modeloRenuncia = EnumModeloDocumentEcm.RENUNCIA_DA_PROCURACAO_ELETRONICA.getDescricaoModeloDocumento();
			
			PecaRenunciaProcuracao pecaRenuncia = pecaRenunciaProcuracaoService.findPecaRenuncia(id, modeloRenuncia);
					
			ServletContext context = request.getServletContext();
			PecaEletronicaDto pecaEletronicaDTO = null;
			
			if(pecaRenuncia == null){
				
				List<MovimentacaoProcuracao> movimentacoes = movimentacaoProcuracaoService.findByMovimentacaoProcuracaoByRenunciaId(renuncia.getId());
				Integer usuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
							
				JasperPrint jasperPrint = pecaRenunciaProcuracaoService.getDocumentoRenuncia(movimentacoes, false, usuarioAutenticado, context);
				
				if (jasperPrint != null) {			    	
					byte[] pdfData = JasperExportManager.exportReportToPdf(jasperPrint);
//					byte[] pdfDataSigned = assinadorDigital.assinarPdf(pdfData,modeloRenuncia, context.getRealPath("/SefazWeblogicDesenvolvimento.jks"));
					
					Pessoa pessoa = new Pessoa();
					pessoa.setIdPessoa(usuarioAutenticado);
									
					pecaEletronicaDTO =  pecaEletronicaService.montarPecaEletronica(pessoa, pdfData, 
							EnumProfilesDocumentEcm.TERMOS_E_PROCURACOES, EnumModeloDocumentEcm.RENUNCIA_DA_PROCURACAO_ELETRONICA, 
							EnumSubCategoriaDocumentEcm.PROCURACAO);
					
					pecaRenunciaProcuracaoService.savePecaRenunciaEletronica(usuarioAutenticado, renuncia,pecaEletronicaDTO);
				}
				else{
					throw new SefazException("Documento não pôde ser carregado. Um problema ocorreu na sua criação.");
				}
				
				
			} else {
				pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaRenuncia.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			}
			
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			response.setHeader("Content-disposition", "attachment; filename="+pecaEletronicaDTO.getNomePecaEletronica());		
			return pecaEletronicaDTO.getContent();	
			
		} catch (Exception e) {			
			throw new AjaxRequestException((e instanceof SefazException) ? e.getLocalizedMessage() 
					: "Falha no processo de salvamento/carregamento do documento solicitado.");
		}
	}
	
	@PostMapping("/upload/{id}")
	@ResponseStatus(HttpStatus.OK)
    public void upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, 
    		@PathVariable Integer id, HttpServletRequest request) 
    		throws AjaxRequestException, IOException, SefazException, DocumentException, IllegalAccessException, 
    		InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException {			
		
		patUploadService.validarArquivo(file);   
		
		RenunciaProcuracao renuncia = renunciaProcuracaoService.findRenunciaById(id);	
		
		String modeloRenuncia = EnumModeloDocumentEcm.RENUNCIA_DA_PROCURACAO_ELETRONICA.getDescricaoModeloDocumento();
		
		PecaRenunciaProcuracao pecaRenuncia = pecaRenunciaProcuracaoService.findPecaRenuncia(id, modeloRenuncia);
		
		if(pecaRenuncia != null){	
			
			PecaEletronicaDto pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaRenuncia.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			Integer idUsuarioAutenticado = usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa();
			
			//validação arquivo PKCS7
			patSignatureService.validarArquivoCades(pecaEletronicaDTO.getContent(), file);
			assinantePecaService.saveAssinantePeca(idUsuarioAutenticado, EnumStatusAssinaturaPeca.ASSINADO_DIGITALMENTE, 
					EnumTipoAssinantePeca.DEFESA, pecaEletronicaDTO.getIdPecaEletronica(), file.getBytes());
			
/*			patSignatureService.validarArquivo(file, pecaEletronicaDTO.getHashCode(), context.getRealPath(ConstantesKeyStore.keyStorePathTest));
			pecaEletronicaDTO.setHashCode(Hash.generate(Hash.ALGORITHM_SHA_256, file.getBytes()));
			pecaEletronicaDTO.setContent(file.getBytes());*/
			
			try{				
				renunciaProcuracaoService.ativarRenunciaEletronica(idUsuarioAutenticado, renuncia,pecaEletronicaDTO);				
				
				//Gerar Recibo de Juntada de Documentos.
				//Usuario Autenticado == Usuário Autor.
				pecaRenunciaProcuracaoService.saveReciboJuntaDocumentos(request, idUsuarioAutenticado,idUsuarioAutenticado, id);
				
				//TODO: INPLEMENTAR O ENVIO DE COMUNICAÇÃO AO DTE DO SUJEITO PASSIVO SOBRE A RENUNCIA DE PROCURADOR. (Ver Documentação)
				
			}catch(Exception e){
				throw new AjaxRequestException("Erro ao salvar a Renúncia da Procuração.");
			}
			
		} else {
			throw new AjaxRequestException("Não conseguimos identificar o documento gerado, por favor faça download do documento novamente.");
		}
    }

}
