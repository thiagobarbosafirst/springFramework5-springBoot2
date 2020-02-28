package br.gov.go.sefaz.pat.procuracao.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.security.user.UsuarioAutenticadoDetails;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.dto.PecaEletronicaModalDownloadDto;
import br.gov.go.sefaz.pat.exception.AjaxRequestException;
import br.gov.go.sefaz.pat.model.AssinantePeca;
import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaProcuracao;
import br.gov.go.sefaz.pat.model.PecaRenunciaProcuracao;
import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaSubstabelecimento;
import br.gov.go.sefaz.pat.procuracao.constants.TipoAgrupadorMovimentacaoMapper;
import br.gov.go.sefaz.pat.procuracao.controller.config.ControllerMappingConfigProcuracao;
import br.gov.go.sefaz.pat.procuracao.service.PecaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaRenunciaProcuracaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaRevogacaoService;
import br.gov.go.sefaz.pat.procuracao.service.PecaSubstabelecimentoService;
import br.gov.go.sefaz.pat.service.AssinantePecaService;
import br.gov.go.sefaz.pat.signature.AssinadorDigitalCades;
import net.sf.jasperreports.engine.JRException;

@Controller
@RequestMapping(ControllerMappingConfigProcuracao.CENTRAL_DOCUMENTOS_PATH_ROOT)
public class CentralDocumentosController {
	
	@Autowired
	private PecaRenunciaProcuracaoService pecaRenunciaProcuracaoService;
	
	@Autowired
	private PecaProcuracaoService pecaProcuracaoService;
	
	@Autowired
	private PecaSubstabelecimentoService pecaSubstabelecimentoService;
	
	@Autowired
	private PecaRevogacaoService pecaRevogcaoService;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private AssinantePecaService assinantePecaService;
	
	@Autowired
	private AssinadorDigitalCades assinadorDigitalCades;
	
	@GetMapping(value="/getListFiles/",produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<PecaEletronicaModalDownloadDto> getListFiles(@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, 
			@RequestParam Integer id, @RequestParam Integer tipoMovimentacao){
		try {
			
			//@PathVariable Id -> Id Procuração, Id Renúncia ou Id Revogação.									
			List<PecaEletronicaModalDownloadDto> pecas = new ArrayList<PecaEletronicaModalDownloadDto>();
			
			switch (tipoMovimentacao) {
				case TipoAgrupadorMovimentacaoMapper.PROCURACAO:
					getPecasProcuracao(id, pecas);
					break;
				case TipoAgrupadorMovimentacaoMapper.SUBSTABELECIMENTO:
					getPecasSubstabelecimento(id, pecas);
					break;
				case TipoAgrupadorMovimentacaoMapper.RENUNCIA:
					getPecasRenuncia(id, pecas);
					break;
				case TipoAgrupadorMovimentacaoMapper.REVOGACAO:
					getPecasRevogacao(id, pecas);
					break;
				default:
					throw new SefazException("O parâmetro 'tipoMovimentacao' não possui um valor correspondente na lista de tipos de movimentações.");				
			}
			
			return (pecas.size() > 0) ? pecas : null;		
			
		} catch (Exception e) {
			throw new AjaxRequestException((e instanceof SefazException) ? e.getLocalizedMessage() 
					: "Falha no processo de busca de peças eletrônicas. Tente novamente mais tarde.");
		}
		
	}
	
	@GetMapping("/downloadFile/{id}")   
	public @ResponseBody byte[] downloadFile(HttpServletResponse response, 
			@AuthenticationPrincipal UsuarioAutenticadoDetails usuarioAutenticadoDetails, @PathVariable String id) 
					throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, 
					KeyStoreException, NoSuchAlgorithmException, IllegalAccessException,InvocationTargetException, NoSuchMethodException, InvalidAttributeValueException{ 		
		byte[] pdfAssinado = null;
		try {
			//O parâmetro 'id' é o valor para 'numeroChaveAcesso'; 
			PecaEletronicaDto pecaEletronicaDTO = pecaEletronicaEcmService.consultar(id);
			AssinantePeca assinantePeca = assinantePecaService.findByPecaEletronicaId(pecaEletronicaDTO.getIdPecaEletronica());
			
			if(assinantePeca != null) {
				pdfAssinado = assinadorDigitalCades.assinadorPdfCades(pecaEletronicaDTO.getContent(), assinantePeca.getInfoAssinaturaPeca());
			}
			
			response.setContentType(MediaType.APPLICATION_PDF_VALUE);
			response.setHeader("Content-disposition", "attachment; filename="+pecaEletronicaDTO.getNomePecaEletronica());
			
			return pdfAssinado != null ? pdfAssinado : pecaEletronicaDTO.getContent();
			
		} catch (Exception e) {
			throw new SefazException("Falha na consulta da peça utilizando a chave de acesso informada.");
		}
	}
		
	private void getPecasProcuracao(Integer idProcuracao, List<PecaEletronicaModalDownloadDto> pecas) {			
		PecaEletronicaModalDownloadDto pecaDto;
		List<PecaProcuracao> pecasMovimentacao = pecaProcuracaoService.findByPecaIdProcuracao(idProcuracao);
		
		for (PecaProcuracao item : pecasMovimentacao){
			pecaDto = new PecaEletronicaModalDownloadDto();
			configurarDtoPeca(item.getPecaEletronica(),pecaDto);
			pecas.add(pecaDto);
		}
	}
	
	private void getPecasSubstabelecimento(Integer idSubstabelecimento, List<PecaEletronicaModalDownloadDto> pecas) {				
		PecaEletronicaModalDownloadDto pecaDto;
		List<PecaSubstabelecimento> pecasMovimentacao = pecaSubstabelecimentoService.findBySubstabelecimentoId(idSubstabelecimento);
		
		for (PecaSubstabelecimento item : pecasMovimentacao){
			pecaDto = new PecaEletronicaModalDownloadDto();
			configurarDtoPeca(item.getPecaEletronica(),pecaDto);
			pecas.add(pecaDto);
		}
	}
	
	private void getPecasRenuncia(Integer idRenuncia, List<PecaEletronicaModalDownloadDto> pecas) {
		
		//id -> Id Renúncia		
		PecaEletronicaModalDownloadDto pecaDto;
		List<PecaRenunciaProcuracao> pecasMovimentacao = pecaRenunciaProcuracaoService.findPecaByIdRenuncia(idRenuncia);
		
		for (PecaRenunciaProcuracao item : pecasMovimentacao){
			pecaDto = new PecaEletronicaModalDownloadDto();
			configurarDtoPeca(item.getPecaEletronica(),pecaDto);
			pecas.add(pecaDto);
		}
	}
	
	private void getPecasRevogacao(Integer idRevogacao, List<PecaEletronicaModalDownloadDto> pecas) {
		
		//id -> Id Renúncia		
		PecaEletronicaModalDownloadDto pecaDto;
		List<PecaRevogacaoProcuracao> pecasMovimentacao = pecaRevogcaoService.listarPecaRevogacaoProcuracao(idRevogacao);
		
		for (PecaRevogacaoProcuracao item : pecasMovimentacao){
			pecaDto = new PecaEletronicaModalDownloadDto();
			configurarDtoPeca(item.getPecaEletronica(),pecaDto);
			pecas.add(pecaDto);
		}
	}
	
	private void configurarDtoPeca(PecaEletronica pecaEletronica,PecaEletronicaModalDownloadDto pecaDto) {		
		pecaDto.setIdPecaEletronica(pecaEletronica.getId());
		pecaDto.setNumeroChaveDeAcesso(pecaEletronica.getNumeroChaveAcessoPecaEletronica());
		pecaDto.setNomePecaEletronica(pecaEletronica.getNomePecaEletronica());
		pecaDto.setNomeModeloDocumento(pecaEletronica.getModeloDocumento().getDescricao());
		pecaDto.setDataHoraApensamento( new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
				.format(pecaEletronica.getDataHoraApensPecaEletronica()).toString());		
	}
	
}
