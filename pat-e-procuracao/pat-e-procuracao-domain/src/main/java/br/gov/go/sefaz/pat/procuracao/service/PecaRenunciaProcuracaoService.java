package br.gov.go.sefaz.pat.procuracao.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.management.InvalidAttributeValueException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEnderecoPK;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaEnderecoService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.documento.dto.PecaEletronicaDto;
import br.gov.go.sefaz.pat.documento.enumerator.EnumModeloDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumProfilesDocumentEcm;
import br.gov.go.sefaz.pat.documento.enumerator.EnumSubCategoriaDocumentEcm;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaEcmService;
import br.gov.go.sefaz.pat.documento.service.PecaEletronicaService;
import br.gov.go.sefaz.pat.documento.suport.PecaEletronicaConverter;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;
import br.gov.go.sefaz.pat.model.EnderecoProcurador;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.PecaEletronica;
import br.gov.go.sefaz.pat.model.PecaRenunciaProcuracao;
import br.gov.go.sefaz.pat.model.ProcessoAdministrativoTributarioEletronico;
import br.gov.go.sefaz.pat.model.RenunciaProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEndereco;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.PecaRenunciaProcuracaoJpaRepository;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;
import br.gov.go.sefaz.ucm.api.document.UcmDocument;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;

@Service
public class PecaRenunciaProcuracaoService {
	
	@Autowired
	private PecaRenunciaProcuracaoJpaRepository pecaRenunciaProcuracaoJpaRepository;
	
	@Autowired
	private PecaEletronicaEcmService pecaEletronicaEcmService;
	
	@Autowired
	private PecaEletronicaService pecaEletronicaService; 
	
	@Autowired
	private RenunciaProcuracaoService renunciaProcuracaoService;
	
	@Autowired
	private PecaRenunciaProcuracaoService pecaRenunciaProcuracaoService;
	
	@Autowired
	private MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	PessoaService pessoaService;
	
	@Autowired
	private DocumentoService documentoService;
	
	@Autowired
	private PessoaEnderecoService pessoaEnderecoService;
	
	@Autowired
	private EnderecoProcuradorService enderecoProcuradorService;
	
	@HistoryJpa
	@Transactional
	public void savePecaRenunciaEletronica(Integer idUsuarioHistorico, RenunciaProcuracao renuncia,PecaEletronicaDto pecaEletronicaDTO) 
			throws NoSuchMethodException,InvocationTargetException,IllegalAccessException,InvalidAttributeValueException,SefazException {
				
		pecaEletronicaDTO = pecaEletronicaEcmService.salvar(idUsuarioHistorico, pecaEletronicaDTO);
		
		PecaEletronica peca = new PecaEletronica();
		peca.setId(pecaEletronicaDTO.getIdPecaEletronica());
		
		PecaRenunciaProcuracao pecaRenunciaProcuracao = new PecaRenunciaProcuracao();
		pecaRenunciaProcuracao.setRenunciaProcuracao(renuncia);
		pecaRenunciaProcuracao.setPecaEletronica(peca);
				
		this.save(idUsuarioHistorico,pecaRenunciaProcuracao);
	}
	
	@HistoryJpa
	@Transactional
	public void save(Integer idUsuarioHistorico, PecaRenunciaProcuracao pecaRenunciaProcuracao) {
		pecaRenunciaProcuracaoJpaRepository.save(pecaRenunciaProcuracao);
	}
	
	public PecaRenunciaProcuracao findPecaRenuncia(Integer idPecaEletronica){
		return this.pecaRenunciaProcuracaoJpaRepository.findByPecaEletronicaId(idPecaEletronica);
	}
	
	public PecaRenunciaProcuracao findPecaRenuncia(Integer idRenuncia, String descricaoModelo){
		return this.pecaRenunciaProcuracaoJpaRepository.findByRenunciaProcuracaoIdAndPecaEletronicaModeloDocumentoDescricao(idRenuncia, descricaoModelo);
	}
	
	public List<PecaRenunciaProcuracao> findPecaByIdRenuncia(Integer idRenuncia){
		return this.pecaRenunciaProcuracaoJpaRepository.findByRenunciaProcuracaoId(idRenuncia);
	}
		
	public void savePecaRenunciaAndUcmDocument(Integer usuarioHistorico, Integer idRenuncia, PecaEletronicaDto pecaEletronicaDto) 
			throws SefazException{
	
		UcmDocument ucmDocument = null;
		
		try {
			
			ucmDocument = pecaEletronicaEcmService.salvarUcmDocument(pecaEletronicaDto);
			pecaEletronicaDto.setChaveAcessoPecaEletronica(ucmDocument.getName());
			PecaEletronica pecaEletronica = PecaEletronicaConverter.toPecaEletronica(pecaEletronicaDto);
			
			pecaEletronica = pecaEletronicaService.salvar(usuarioHistorico, pecaEletronica);
							
			PecaRenunciaProcuracao pecaRenuncia = new PecaRenunciaProcuracao();		
			PecaEletronica peca = new PecaEletronica();
			
			RenunciaProcuracao renuncia = new RenunciaProcuracao();
			renuncia.setId(idRenuncia);
			peca.setId(pecaEletronica.getId()); 		
			pecaRenuncia.setRenunciaProcuracao(renuncia);
			pecaRenuncia.setPecaEletronica(peca);
			
			this.pecaRenunciaProcuracaoJpaRepository.save(pecaRenuncia);
		
		} catch(Exception e) {
			if(ucmDocument != null)
				pecaEletronicaEcmService.deleteRevision(ucmDocument);
			
			throw new SefazException("Falha no processo de salvamento da Peça Eletrônica.");
		}
	}
	
	public void saveReciboJuntaDocumentos(HttpServletRequest request, Integer idUsuarioAutenticado, Integer usuarioAutor, Integer idRenuncia) 
				throws JRException, IOException, ServletException, SefazException, UnrecoverableKeyException, 
				KeyStoreException, NoSuchAlgorithmException, IllegalAccessException,InvocationTargetException, 
				NoSuchMethodException, InvalidAttributeValueException{ 
			
		RenunciaProcuracao renuncia = renunciaProcuracaoService.findRenunciaById(idRenuncia);
		String modeloRecibo = EnumModeloDocumentEcm.TERMO_DE_JUNTADA.getDescricaoModeloDocumento();
		
		PecaRenunciaProcuracao pecaReciboJuntada = pecaRenunciaProcuracaoService.findPecaRenuncia(idRenuncia,modeloRecibo);
				
		ServletContext context = request.getServletContext();
		PecaEletronicaDto pecaEletronicaDTO = null;
		
		if(pecaReciboJuntada == null){
			
			List<MovimentacaoProcuracao> detalhes = movimentacaoProcuracaoService.findByMovimentacaoProcuracaoByRenunciaId(renuncia.getId());
			
			//Buscar todas as peças da renúncia em questão, para mostrar no recibo.
			List<PecaRenunciaProcuracao> pecasRenuncia = pecaRenunciaProcuracaoService.findPecaByIdRenuncia(idRenuncia);			
			List<PecaEletronica> pecas = new ArrayList<PecaEletronica>();
			
			for (PecaRenunciaProcuracao itemPeca : pecasRenuncia)
				pecas.add(itemPeca.getPecaEletronica());
			
			JasperPrint jasperPrint = getReciboJuntadaJasper(detalhes, pecas, usuarioAutor, context);
			
			if (jasperPrint != null) {			    	
				byte[] pdfData = JasperExportManager.exportReportToPdf(jasperPrint);
								
//				byte[] pdfDataSigned = assinadorDigital.assinarPdf(pdfData,  "Renúncia de Procuração - " + modeloRecibo, context.getRealPath("/SefazWeblogicDesenvolvimento.jks"));
				
				Pessoa pessoaCadastro = new Pessoa();
				pessoaCadastro.setIdPessoa(idUsuarioAutenticado);
				
				Pessoa pessoaAutora = new Pessoa();
				pessoaAutora.setIdPessoa(usuarioAutor);
								
				pecaEletronicaDTO =  pecaEletronicaService.montarPecaEletronica(pessoaCadastro,pessoaAutora,pdfData, EnumProfilesDocumentEcm.TERMOS_E_PROCURACOES, 
						EnumModeloDocumentEcm.TERMO_DE_JUNTADA, EnumSubCategoriaDocumentEcm.TERMO_RITO_PROCESSUAL);
				
				pecaEletronicaDTO = pecaEletronicaEcmService.salvar(idUsuarioAutenticado, pecaEletronicaDTO);
				
				PecaEletronica peca = new PecaEletronica();
				peca.setId(pecaEletronicaDTO.getIdPecaEletronica());
				
				PecaRenunciaProcuracao pecaRenunciaProcuracao = new PecaRenunciaProcuracao();
				pecaRenunciaProcuracao.setRenunciaProcuracao(renuncia);
				pecaRenunciaProcuracao.setPecaEletronica(peca);
				
				pecaRenunciaProcuracaoService.save(idUsuarioAutenticado, pecaRenunciaProcuracao);
				
			}
			else{
				throw new SefazException("Documento não pôde ser carregado. Um problema ocorreu na sua criação.");
			}
		}	
	}
	
	public PecaEletronicaDto getReciboJuntaDocumentos(Integer idRenuncia) 
			throws SefazException,IllegalAccessException,InvocationTargetException,	NoSuchMethodException{ 
				
		PecaRenunciaProcuracao pecaReciboJuntada = pecaRenunciaProcuracaoService.findPecaRenuncia(idRenuncia, EnumModeloDocumentEcm.TERMO_DE_JUNTADA.getDescricaoModeloDocumento());
		PecaEletronicaDto pecaEletronicaDTO = null;
	
		if(pecaReciboJuntada != null)
			pecaEletronicaDTO = pecaEletronicaEcmService.consultar(pecaReciboJuntada.getPecaEletronica().getNumeroChaveAcessoPecaEletronica());
		else
			throw new SefazException("Peça Eletrônica não foi encontrada.");
				
		return pecaEletronicaDTO;	
}
	
	private JasperPrint getReciboJuntadaJasper(List<MovimentacaoProcuracao> detalhesProcuracao, List<PecaEletronica> pecas, 
			Integer idUsuarioProcurador,ServletContext context) 
					throws SefazException, FileNotFoundException, JRException{
		
		ModelMap modelMap = new ModelMap();
		
		modelMap.put("logo.sefaz", context.getRealPath("resources/images/sefazgo-logo.png"));
		modelMap.put("header.pat", context.getRealPath("jasper/header_renuncia.jasper"));
		modelMap.put("footer.pat", context.getRealPath("jasper/footer.jasper"));
		
		modelMap.put("processos", getProcessosRenuncia(detalhesProcuracao));	
		modelMap.put("sujeitos", getAutorgantes(detalhesProcuracao));
		
		modelMap.put("procurador", getProcuradorRecibo(pessoaService.consultar(idUsuarioProcurador)));
		modelMap.put("usuario", "Sistema PAT-e");
		modelMap.put("dataHora", retornarDataFormatada("dd/MM/yyyy HH:mm:ss"));
		modelMap.put("pecas", getPecas(pecas));				
		modelMap.put("titulo", "Recibo de Juntada de Documentos");
					
		FileInputStream reportStream = new FileInputStream(new File(context.getRealPath("jasper/recibo-juntada-renuncia.jasper")));
						
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
		
		Map<String, Object> map = new HashMap<String, Object>();				
		List<Map<String, Object>> lista = new ArrayList<Map<String,Object>>();
		
		lista.add(map);
		
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);		
		return JasperFillManager.fillReport(jasperReport,modelMap,dataSource);
	}
	
	
	public JasperPrint getDocumentoRenuncia(List<MovimentacaoProcuracao> detalhesProcuracao, boolean eMinuta, 
			Integer idUsuarioProcurador,ServletContext context) throws SefazException, FileNotFoundException, JRException{
		
		String outorgantes = getAutorgantes(detalhesProcuracao);		
		String processos = getProcessosRenuncia(detalhesProcuracao);
		
		Pessoa pessoa = pessoaService.consultar(idUsuarioProcurador);		
		String procurador = getProcurador(pessoa);
		
		DocumentoAdvogado documentoAdvogado = documentoService.getDocumentoAdvogado(pessoa.getIdPessoa());		
		ValidarDocumentoAdvogado(documentoAdvogado);
		
		String documentoProcurador = getDocumentoProcurador(documentoAdvogado);
		
//		PessoaJuridica escritorioProcurador = detalhesProcuracao.get(0).getProcurador().getEscritorioProcurador();		
		EnderecoProcurador enderecoIntimacao = enderecoProcuradorService.consultar(detalhesProcuracao.get(0).getProcurador().getId(), EnumTipoEndereco.COMERCIAL.getCodigo());
		validarEnderecoRecebimentoIntimacao(enderecoIntimacao);
				
//		PessoaEndereco pessoaEndereco = getEnderecoProfissional(escritorioProcurador);		
//		validarEnderecoProfissional(pessoaEndereco);
				
		String procuradorEndereco = new FormatSupport().formatEndereco(enderecoIntimacao.getEndereco());
			
		String dataExtenso = "Goiânia, " + retornarDataFormatada("dd 'de' MMMM 'de' yyyy"); 
						
		ModelMap modelMap = new ModelMap();
		
		modelMap.put("logo.sefaz", context.getRealPath("resources/images/sefazgo-logo.png"));
		modelMap.put("header.pat", context.getRealPath("jasper/header_renuncia.jasper"));
		modelMap.put("footer.pat", context.getRealPath("jasper/footer.jasper"));
		
		modelMap.put("outorgantes", outorgantes);		
		modelMap.put("processos", processos);	
		
		modelMap.put("procurador", procurador);
		modelMap.put("procuradorDocumento", documentoProcurador);
		modelMap.put("procuradorEndereco", procuradorEndereco);
		
		modelMap.put("dataAtual", dataExtenso);
		modelMap.put("titulo", getTituloRelatorio(eMinuta));
					
		FileInputStream reportStream = new FileInputStream(new File(context.getRealPath("jasper/renuncia-report.jasper")));
						
		JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
		
		Map<String, Object> map = new HashMap<String, Object>();				
		List<Map<String, Object>> lista = new ArrayList<Map<String,Object>>();
		
		lista.add(map);
		
		JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);		
		return JasperFillManager.fillReport(jasperReport,modelMap,dataSource);
	}
	
	private String getDocumentoProcurador(DocumentoAdvogado documentoAdvogado) {
		String procuradorDocumento = "OAB/UF nº " + documentoAdvogado.getNumeroDocumento() + "/" + documentoAdvogado.getUf().getCodigo();
		return procuradorDocumento;
	}

	private String getTituloRelatorio(boolean eMinuta) {
		String tituloRelatorio = eMinuta ? "[Minuta] da Renúncia de Procuração" : "Renúncia de Procuração";
		return tituloRelatorio;
	}

	private void ValidarDocumentoAdvogado(DocumentoAdvogado documentoAdvogado) throws SefazException {
		if(documentoAdvogado == null) 
			throw new SefazException("Documento de registro do Procurador não foi encontrado. Documento da OAB deve estar cadastrado.");
	}
	
	private String getProcurador(Pessoa pessoa){
		return pessoa.getPessoaFisica().getNome() + ", advogado, inscrito no CPF/MF nº " + new FormatSupport().formatarCPF(pessoa.getPessoaFisica().getNumeroCpf());
	}
	
	
	private void validarEnderecoProfissional(PessoaEndereco pessoaEndereco) throws SefazException {
		if(pessoaEndereco == null) 
			throw new SefazException("Endereço Profissional (Tipo Comercial) do Procurador não foi encontrado.");
	}

	private void validarEscritorioProcurador(PessoaJuridica escritorioProcurador) throws SefazException {
		if(escritorioProcurador == null)
			throw new SefazException("Endereço Profissional do Procurador não foi encontrado.");
	}

	private void validarEnderecoRecebimentoIntimacao(EnderecoProcurador enderecoRecebimentoIntimacao) throws SefazException{
		if(enderecoRecebimentoIntimacao == null)
			throw new SefazException("Endereço Profissional do Procurador não foi encontrado.");
	}
	
	private PessoaEndereco getEnderecoProfissional(PessoaJuridica escritorioProcurador) {
		PessoaEnderecoPK pessoaEnderecoPK = new PessoaEnderecoPK();
		pessoaEnderecoPK.setIdPessoa(escritorioProcurador.getIdPessoa());
		pessoaEnderecoPK.setTipoEndereco(2);
		
		PessoaEndereco pessoaEndereco = pessoaEnderecoService.consultar(pessoaEnderecoPK);
		return pessoaEndereco;
	}
	
	private String getProcessosRenuncia(List<MovimentacaoProcuracao> detalhesProcuracao) {
		Integer contador = 0;
		List<Integer> processosList = new ArrayList<Integer>();
		
		StringBuilder processos = new StringBuilder();
				
		for(MovimentacaoProcuracao detalhe : detalhesProcuracao) {
			
			if(contador > 0 && (contador + 1) == detalhesProcuracao.size()) {
				processos.append(" e ");
			}
			else if(contador > 0) {
				processos.append(", ");
			}
			
			ProcessoAdministrativoTributarioEletronico processoObj = detalhe.getSujeitoPassivoProcesso().getProcessoAdministrativoTributarioEletronico();
								
			if(processosList.indexOf(processoObj.getIdProcessoAdministrativoTributarioEletronico()) == -1){
				processos.append(formatarNumeroPAT(processoObj.getTipoDocumentoOrigem().toString(), 
						processoObj.getNumeroSequencial().toString(), processoObj.getNumeroDigitoVerificador().toString()));	
			}
			
			contador++;
		}
		return processos.toString();
	}
	
	private String formatarNumeroPAT(String documentoOrigem, String numeroSequancial, String digitoVerificador ){		
		String numeroSequencialFormat = StringUtils.leftPad(numeroSequancial, 10, "0");
		String digitoVerificadorFormat = StringUtils.leftPad(digitoVerificador, 2, "0");
		
		return String.format("%s%s%s", documentoOrigem.trim(),numeroSequencialFormat.trim(),digitoVerificadorFormat.trim());
	}
	
	private String getAutorgantes(List<MovimentacaoProcuracao> detalhesProcuracao) {
		
		int contador = 0;		
		StringBuilder outorgantes = new StringBuilder();		
						
		for(MovimentacaoProcuracao detalhe : detalhesProcuracao) {
			
			if(contador > 0 && (contador + 1) == detalhesProcuracao.size()) {
				outorgantes.append(" e ");
			}
			else if(contador > 0) {
				outorgantes.append(", ");
			}
			
			Pessoa pessoa = detalhe.getSujeitoPassivoProcesso().getPessoa();
			
			if (pessoa instanceof PessoaFisica) 
				outorgantes.append(formatNomePessoaFisica(pessoa));
			else
				outorgantes.append(formatNomePessoaJuridica(pessoa));
									
			contador++;
		}
		
		return outorgantes.toString();
	}
	
	private String formatNomePessoaJuridica(Pessoa pessoa){
		String cnpjMF = ", CNPJ/MF nº ";		
		return ((PessoaJuridica) pessoa).getNomeFantasia() +  cnpjMF + new FormatSupport().formatarCNPJ(((PessoaJuridica) pessoa).getNumeroCnpj());
	}

	private String formatNomePessoaFisica(Pessoa pessoa) {
		FormatSupport format = new FormatSupport();
		String cpfMF = ", CPF/MF nº ";
		
		return ((PessoaFisica) pessoa).getNome() +  cpfMF + format.formatarCPF(((PessoaFisica) pessoa).getNumeroCpf());
	}	
	
	private String getProcuradorRecibo(Pessoa pessoa){
		return pessoa.getPessoaFisica().getNome() + " - CPF: " + new FormatSupport().formatarCPF(pessoa.getPessoaFisica().getNumeroCpf());
	}
	
	public String retornarDataFormatada(String format) {
		Date data =  new Date();
		Locale local = new Locale("pt","BR");
		DateFormat dateFormat = new SimpleDateFormat(format,local); 
		return dateFormat.format(data);
	}
		
	private String getPecas(List<PecaEletronica> pecas) {
		
		int contador = 0;		
		StringBuilder pecasString = new StringBuilder();		
						
		for(PecaEletronica item : pecas) {
			
			if(contador > 0 && (contador + 1) == pecas.size()) {
				pecasString.append(" e ");
			}
			else if(contador > 0) {
				pecasString.append(", ");
			}
			
			pecasString.append(item.getNomePecaEletronica());
									
			contador++;
		}
		
		return pecasString.toString();
	}
	
}
