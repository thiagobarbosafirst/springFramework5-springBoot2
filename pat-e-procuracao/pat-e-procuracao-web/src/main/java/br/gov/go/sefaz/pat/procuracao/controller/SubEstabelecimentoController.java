package br.gov.go.sefaz.pat.procuracao.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
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
import br.gov.go.sefaz.javaee.commons.support.Hash;
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
import br.gov.go.sefaz.pat.constants.ConstantesKeyStore;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.repository.jpa.ModeloDocumentoJpaRepository;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.enumerator.EnumStatusAssinaturaPeca;
import br.gov.go.sefaz.pat.enumerator.EnumTipoAssinantePeca;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaSubstabelecimento;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.MovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.service.DocumentoService;
import br.gov.go.sefaz.pat.procuracao.service.EmailService;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaSubstabelecimentoService;
import br.gov.go.sefaz.pat.procuracao.service.ProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.SubEstabelecimentoService;
import br.gov.go.sefaz.pat.procuracao.service.TelefoneService;
import br.gov.go.sefaz.pat.procuracao.support.ProcuracaoConverter;
import br.gov.go.sefaz.pat.procuracao.validation.input.SubstabelecimentoInputValidator;
import br.gov.go.sefaz.pat.service.AssinantePecaService;
import br.gov.go.sefaz.pat.service.PatSignatureService;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
import br.gov.go.sefaz.pat.service.PatUploadService;
import br.gov.go.sefaz.pat.support.formatting.UfComparator;
import br.gov.go.sefaz.pat.web.support.Support;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.SUBSTABELECIMENTO_PATH_ROOT)
public class SubEstabelecimentoController {	
	
	private static Logger logger = LogManager.getLogger(SubEstabelecimentoController.class);
	
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
	private PecaSubstabelecimentoService pecaSubstabelecimentoService;
	
	@Autowired
	ProcuracaoConverter procuracaoConverter;
	
	@Autowired
	private PatUploadService patUploadService;
	
	@Autowired
	private PatSignatureService patSignatureService;
	
	@Autowired
	private AssinantePecaService assinantePecaService;
	
	@GetMapping("/{id}")
	public @ResponseBody ModelAndView inicializarSubstabelecimento(ProcuracaoDto procuracaoDto, @PathVariable Integer id,
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) {
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.SUBSTABELECIMENTO_VIEW_FORM);	
		List<Uf> listUf = ufService.listar();
		listUf.sort(new UfComparator());		
		mv.addObject(ControllerMappingConfigProcuracao.UF_MODEL_ENTITIES_NAME,listUf);		
		mv.addObject("regimesFiscais", montarListaRegimeFiscal());	
		procuracaoDto.setId(id);
		mv.addObject("listaReservaPoderes", montarListaTipoReservaPoder(procuracaoDto));		
		List<AgrupadorMovimentacaoDto> listaProcessos = movimentacaoProcuracaoService.findSubstabelecerByIdPessoaProcuradorAndProcuracao(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), id);
		procuracaoDto.setListaProcessos(listaProcessos);
		checkListaProcessos(listaProcessos, procuracaoDto);
		procuracaoDto.setIndiSubstabelecimentoPresencial(String.valueOf(EnumSimNao.N.getValor())); 
		Procuracao procuracao = procuracaoService.find(id);
		procuracaoDto.setDataValidade(procuracao.getDataValidade()); 
		mv.addObject("procuracaoDto", procuracaoDto);	
		return mv;	 			
	}
	
	private void checkListaProcessos(List<AgrupadorMovimentacaoDto> listaProcessos, ProcuracaoDto procuracaoDto) {
		
		if(procuracaoDto.getListaMovimentacoes() != null) {
			for (AgrupadorMovimentacaoDto agrupadorMovimentacaoDto : listaProcessos) {
				
				List<MovimentacaoDto> listaMovimentacao = agrupadorMovimentacaoDto.getMovimentacoesDto();
				
				for (MovimentacaoDto movimentacaoDto : listaMovimentacao) {
					
					for (Integer movimentacao : procuracaoDto.getListaMovimentacoes()) {
						if(movimentacaoDto.getIdMovimentacaoProcuracao().equals(movimentacao)) {
							movimentacaoDto.setChecked(true);
							agrupadorMovimentacaoDto.setChecked(true);
						}
					}
					
				}
			}
		}		
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
	public ModelAndView save(HttpServletRequest request, @RequestParam(name="movimentacoes[]",required=false) String[] movimentacoes, ProcuracaoDto procuracaoDto, BindingResult result, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) throws IllegalAccessException, 
	InvocationTargetException, NoSuchMethodException, SefazException{
		
		try {			
			
			if(procuracaoDto.getListaMovimentacoes() == null && movimentacoes != null) {
				List<Integer> listaMovimentacoes = new ArrayList<Integer>();
				for (String movimentacao : movimentacoes) {
					listaMovimentacoes.add(Integer.parseInt(movimentacao));
				}
				procuracaoDto.setListaMovimentacoes(listaMovimentacoes); 
			}			
			
			substabelecimentoInputValidator.validateSave(procuracaoDto, result); 	
			if (!result.hasErrors()) {
				try {					
					PessoaAutenticadaDto pessoa = usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada();
					
					SubEstabelecimentoProcuracao subestabelecimento = procuracaoConverter.toSubestabelecimento(procuracaoDto);					
					subEstabelecimentoService.prepararSubstabelecimento(subestabelecimento);					
					
					subEstabelecimentoService.saveSubstabelecimento(pessoa.getId(), pessoa, subestabelecimento, Support.MATRICULA_USUARIO_GESTOR, new ArrayList<PecaEletronicaDto>());
					
					ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.SUBSTABELECIMENTO_VIEW_LIST_REDIRECT);
					return mv;  
				} catch (SefazException se) {
					ModelAndView mv = inicializarSubstabelecimento(procuracaoDto, procuracaoDto.getId(), usuarioAutenticadoDetails);
					mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, se.getMessage());
					return mv;	
				} catch (Exception e) {
					ModelAndView mv = inicializarSubstabelecimento(procuracaoDto, procuracaoDto.getId(), usuarioAutenticadoDetails);
					mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, "Ocorreu algum problema ao salvar o substabelecimento. " + e.getMessage());
					return mv;
				}	
			}
		} catch (SefazValidationException e) {
			ModelAndView mv = inicializarSubstabelecimento(procuracaoDto, procuracaoDto.getId(), usuarioAutenticadoDetails);
			mv.addObject(MessageElementTagParameters.FORM_ERROR_MESSAGE_KEY, e.getMessage());
			return mv;
		}
		
		ModelAndView mv = inicializarSubstabelecimento(procuracaoDto, procuracaoDto.getId(), usuarioAutenticadoDetails);
		return mv;
				
	}
	
	@GetMapping(ControllerMappingConfigProcuracao.PATH_ACTION_LIST_SUBSTABELECIMENTO)
	public ModelAndView listarPorSujeitoPassivo(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails){	
		ModelAndView mv = new ModelAndView(ControllerMappingConfigProcuracao.SUBSTABELECIMENTO_VIEW_LIST);
		List<AgrupadorMovimentacaoDto> listaSubstabelecimento = movimentacaoProcuracaoService.findSubstabelecimento(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa());	
		mv.addObject("listaSubstabelecimento", listaSubstabelecimento);   
		return mv;				
	}	
	
	@PostMapping(value="/verifyStep/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Integer verifyStep(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		Integer step = subEstabelecimentoService.checkStepOfSubstabelecimento(id);
		if (step != null)
			return step;
		else
			throw new AjaxRequestException("Erro ao verificar a etapa da procuração.");
	}
	
	@GetMapping("/download/{id}")   
	public void gerarPecaSubestabelecimento(HttpServletRequest request, HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable Integer id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, KeyStoreException, 
					NoSuchAlgorithmException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 
		
		PecaSubstabelecimento pecaSubstabelecimento = null;
		PecaEletronicaDto pecaEletronicaDto = null;
		
		pecaSubstabelecimento = this.pecaSubstabelecimentoService.findPecaSubstabelecimento(id, EnumModeloDocumentEcm.PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		
		try {
			if(pecaSubstabelecimento == null){
				logger.debug("SubestabelecimentoController.gerarPecaSubestabelecimento antes de criarPecaEletronica");			
				pecaEletronicaDto = subEstabelecimentoService.criarPecaEletronica(request.getServletContext(), usuarioAutenticadoDetails, id);
				logger.debug("SubestabelecimentoController.gerarPecaSubestabelecimento depois de criarPecaEletronica");	
				pecaSubstabelecimentoService.savePecaSubstabelecimentoAndUcmDocument(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), id, pecaEletronicaDto);
				logger.debug("SubestabelecimentoController.gerarPecaSubestabelecimento depois de savePecaSubstabelecimentoAndUcmDocument");	
			} else {
				pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaSubstabelecimento.getPecaEletronica().getNumeroChaveAcessoPecaEletronica()); 				
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

		PecaSubstabelecimento pecaSubstabelecimento = pecaSubstabelecimentoService.findPecaSubstabelecimento(id, EnumModeloDocumentEcm.SUBESTABELECIMENTO_DA_PROCURACAO_ELETRONICA.getDescricaoModeloDocumento());
		
		if(pecaSubstabelecimento != null){			
			
			PessoaAutenticadaDto pessoaAutenticada = usuarioAutenticadoDetails.getUsuarioAutenticado().getPessoaAutenticada();
			
			PecaEletronicaDto pecaEletronicaDto = pecaEletronicaEcmService.consultar(pecaSubstabelecimento.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
			SubEstabelecimentoProcuracao subsEstabelecimentoProcuracao = pecaSubstabelecimento.getSubEstabelecimentoProcuracao();
			List<MovimentacaoProcuracao> listaMovimentacao = movimentacaoProcuracaoService.findBySubstabelecimento(subsEstabelecimentoProcuracao.getId());
			
			//validação arquivo PKCS7
			patSignatureService.validarArquivoCades(pecaEletronicaDto.getContent(), file);
			assinantePecaService.saveAssinantePeca(idUsuarioLogado, EnumStatusAssinaturaPeca.ASSINADO_DIGITALMENTE, 
					EnumTipoAssinantePeca.DEFESA, pecaEletronicaDto.getIdPecaEletronica(), file.getBytes());
			
			/*// Validação de certificado
			patSignatureService.validarArquivo(file, pecaEletronicaDto.getHashCode(), context.getRealPath(ConstantesKeyStore.keyStorePathTest));			
			pecaEletronicaDto.setHashCode(Hash.generate(Hash.ALGORITHM_SHA_256, file.getBytes()));
			pecaEletronicaDto.setContent(file.getBytes());	*/
			
			try{
				PecaEletronicaDto pecaReciboJuntada = procuracaoService.gerarReciboDeJuntada(pessoaAutenticada.getId(), pessoaAutenticada.getId(), listaMovimentacao, pecaEletronicaDto.getNomePecaEletronica(), "Termo de Juntada Substabelecimento"); 
				subEstabelecimentoService.ativarSubstabelecimento(usuarioAutenticadoDetails.getUsuarioAutenticado().getIdPessoa(), subsEstabelecimentoProcuracao, pecaEletronicaDto, pecaReciboJuntada);				
			}catch(Exception e){
				throw new AjaxRequestException("Erro ao salvar a procuração." + e.getMessage()); 
			}
			 
		} else {
			throw new AjaxRequestException("Não conseguimos identificar o documento gerado, por favor faça download do documento novamente.");
		}
    }
	
	@PostMapping("/minuta") 
	public ModelAndView gerarMinuta(@RequestParam(name="movimentacoes[]", required=false) String[] movimentacoes, HttpServletRequest request, HttpServletResponse response, ProcuracaoDto procuracaoDto, @AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails) {
		
		ModelAndView mv = null;
		
		try {
			byte[] pdfData = subEstabelecimentoService.gerarMinuta(request.getServletContext(), procuracaoDto, movimentacoes); 						
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			response.setHeader("Content-disposition", "attachment; filename=" + "Minuta_Substabelecimento.pdf");
			
			OutputStream outPut = response.getOutputStream();
			outPut.write(pdfData);
			outPut.close();
			
		} catch (Exception e) {
			throw new AjaxRequestException("Erro ao gerar a minuta do substabelecimento."); 
		}
		return mv;
		
	}	
	
}
