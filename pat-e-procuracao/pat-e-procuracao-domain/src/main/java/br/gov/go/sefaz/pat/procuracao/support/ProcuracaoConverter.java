package br.gov.go.sefaz.pat.procuracao.support;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.commons.formatting.FormattingSupport;
import br.gov.go.sefaz.javaee.corporativo.model.DocumentoPessoaId;
import br.gov.go.sefaz.javaee.corporativo.model.EmailPessoa;
import br.gov.go.sefaz.javaee.corporativo.model.Endereco;
import br.gov.go.sefaz.javaee.corporativo.model.Logradouro;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEndereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaEnderecoPK;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.model.TelefonePessoa;
import br.gov.go.sefaz.javaee.corporativo.model.TipoDocumento;
import br.gov.go.sefaz.javaee.corporativo.model.Uf;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.enumerator.EnumTipoProcurador;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;
import br.gov.go.sefaz.pat.model.EnderecoProcurador;
import br.gov.go.sefaz.pat.model.MovimentacaoProcuracao;
import br.gov.go.sefaz.pat.model.Procuracao;
import br.gov.go.sefaz.pat.model.Procurador;
import br.gov.go.sefaz.pat.model.SubEstabelecimentoProcuracao;
import br.gov.go.sefaz.pat.model.SujeitoPassivoProcesso;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusProcuracao;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusSubstabelecimento;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEmail;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoEndereco;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumTipoTelefone;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcessoSujeitoPassivoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuradorDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.SujeitoPassivoDto;
import br.gov.go.sefaz.pat.procuracao.service.MovimentacaoProcuracaoService;
import br.gov.go.sefaz.pat.service.PatSujeitoPassivoProcessoService;
@Component 
public class ProcuracaoConverter {
	
	@Autowired
	MovimentacaoProcuracaoService movimentacaoProcuracaoService;
	
	@Autowired
	PatSujeitoPassivoProcessoService patSujeitoPassivoProcessoService;
	
	/**
	 * Provides a relational object ready to be persisted.
	 * 
	 * @param pecaEletronicaDto
	 * @return PecaEletronica
	 * 
	 * @throws SefazException 
	 * 
	 */
	public static Procuracao toProcuracao(ProcuracaoDto procuracaoDTO, String[] sujeitosPassivos)  throws SefazException{
		if(procuracaoDTO == null || sujeitosPassivos == null) { 
			throw new SefazException("The parameter null is not allowed.");
		}
			
		Date dataProcuracao = new Date();
		boolean registrouComplementoPessoa = false;
		Procuracao procuracao = new Procuracao(); 
				
		if(procuracaoDTO.getDataValidade() != null) {
			procuracao.setDataValidade(procuracaoDTO.getDataValidade());
		}
		
		if(!procuracaoDTO.isPermiteSubstabelecimento()) {
			procuracao.setTipoPoderSubstabelecimento('1');
		}			
		else {
			procuracao.setTipoPoderSubstabelecimento(procuracaoDTO.getReservaPoderes().charAt(0)); 
		}				
		procuracao.setTipoNaturezaJuridica(procuracaoDTO.getTipoNaturezaJuridica());
		procuracao.setDataEmissao(new Date());
		procuracao.setStatus(EnumStatusProcuracao.PendenteAssinatura.getCodigo());  
		procuracao.setIndiPrazoDeterminado(procuracaoDTO.getDataValidade() == null ? "N" : "S");
		/*procuracao.setInfoProcuracao(
				"Pelo presente instrumento particular de procuração, os outorgantes nomeiam e constituem seu bastante procurador");*/
		if(procuracaoDTO.getReservaPoderes() != null && !procuracaoDTO.getReservaPoderes().equals("")) {
			procuracao.setTipoPoderSubstabelecimento(procuracaoDTO.getReservaPoderes().charAt(0));
		} else {
			//Sem poderes para substabelecer
			procuracao.setTipoPoderSubstabelecimento('1'); 
		}
		procuracao.setPessoaAssinante(new Pessoa());
		procuracao.getPessoaAssinante().setIdPessoa(procuracaoDTO.getIdPessoaAssinante());				
		
		procuracao.setListaMovimentacaoProcuracao(new ArrayList<MovimentacaoProcuracao>());
		
		List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = new ArrayList<MovimentacaoProcuracao>();
		MovimentacaoProcuracao movimentacaoProcuracao;
		Procurador procurador;
		SujeitoPassivoProcesso sujeitoPassivoProcesso;						
			
		for (int contSujeitoPassivo = 0; contSujeitoPassivo < sujeitosPassivos.length; contSujeitoPassivo++) {
				
			for (ProcuradorDto procuradorDTO : procuracaoDTO.getProcuradores()) {
			
				movimentacaoProcuracao = new MovimentacaoProcuracao();
				procurador = new Procurador();
				
				//Dados do procurador
				movimentacaoProcuracao.setProcurador(procurador);
				procurador.setId(procuradorDTO.getIdProcurador());
				movimentacaoProcuracao.setTipoRepresentacao(EnumTipoProcurador.ADVOGADO.getTipo()); //Advogado
				//Pessoa
				procurador.setPessoa(new Pessoa());
				procurador.getPessoa().setIdPessoa(procuradorDTO.getIdPessoa());
				if(procuradorDTO.getIdEscritorio() != null && procuradorDTO.getIdEscritorio() != 0) { 
					
					procurador.setEscritorioProcurador(new PessoaJuridica());
					procurador.getEscritorioProcurador().setIdPessoa(procuradorDTO.getIdEscritorio()); 
					
				}
				if(!registrouComplementoPessoa) {
					List listaComplementoPessoa = getListaComplementoPessoa(procuracaoDTO.getProcuradores());
					procurador.setListaComplementoPessoa(listaComplementoPessoa);
					registrouComplementoPessoa = true;
				}
				
				movimentacaoProcuracao.setDataInicioMovimentacao(dataProcuracao);
				movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.N); //Inativa - Renuncia todos representantes
				
				
				sujeitoPassivoProcesso = new SujeitoPassivoProcesso();
				
				sujeitoPassivoProcesso.setId(Integer.valueOf(sujeitosPassivos[contSujeitoPassivo]));
				movimentacaoProcuracao.setSujeitoPassivoProcesso(sujeitoPassivoProcesso);
				
				listaMovimentacaoProcuracao.add(movimentacaoProcuracao);
				procuracao.getListaMovimentacaoProcuracao().add(movimentacaoProcuracao);
			}
		}
		
		return procuracao;
	}
	
	private List<List<String>> dividirSujeitoPassivoPorProcesso(String[] sujeitosPassivos) {
		
		List<List<String>> listaProcessos = new ArrayList<List<String>>();
		List<String> listaSujeitosPassivos = new ArrayList<String>();
		
		String[] sujeitoPassivo;
		
		Integer idProcessoAnterior = 0;
		Integer idProcessoAtual = 0;
		
		for (int contSujeitoPassivo = 0; contSujeitoPassivo < sujeitosPassivos.length; contSujeitoPassivo++) {
			SujeitoPassivoProcesso sujeitoPassivoProcesso = patSujeitoPassivoProcessoService.find(Integer.valueOf(sujeitosPassivos[contSujeitoPassivo]));
			idProcessoAtual = sujeitoPassivoProcesso.getProcessoAdministrativoTributarioEletronico().getIdProcessoAdministrativoTributarioEletronico();
			if(contSujeitoPassivo == 0) {
				listaSujeitosPassivos.add(sujeitosPassivos[contSujeitoPassivo]);
				
			}
			
			idProcessoAnterior = idProcessoAtual;
		}
		
		return listaProcessos;
		
	}
	
	/**
	 * Provides a relational object ready to be persisted.
	 * 
	 * @param pecaEletronicaDto
	 * @return PecaEletronica
	 * 
	 * @throws SefazException 
	 * 
	 */
	public static List<Procuracao> toListProcuracao(ProcuracaoDto procuracaoDTO, String[] sujeitosPassivos)  throws SefazException{ 
		if(procuracaoDTO == null || procuracaoDTO.getSujeitoPassivos() == null) { 
			throw new SefazException("The parameter null is not allowed.");
		}
		
		List<Procuracao> listaProcuracao = new ArrayList<Procuracao>();
			
		Date dataProcuracao = new Date();
		boolean registrouComplementoPessoa = false;
		
		for (ProcessoSujeitoPassivoDto processoSujeitoPassivoDto : procuracaoDTO.getSujeitoPassivos()) {
			Procuracao procuracao = new Procuracao(); 
			procuracao.setListaMovimentacaoProcuracao(new ArrayList<MovimentacaoProcuracao>());
			List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = new ArrayList<MovimentacaoProcuracao>();
			MovimentacaoProcuracao movimentacaoProcuracao;
			Procurador procurador;
			SujeitoPassivoProcesso sujeitoPassivoProcesso;	
			
			for (SujeitoPassivoDto sujeitoPassivoDto :processoSujeitoPassivoDto.getListaSujeitoPassivo()) {
				
				boolean encontrouSP = false;
				for (int contSPAux = 0; contSPAux < sujeitosPassivos.length; contSPAux++) {
					if(sujeitosPassivos[contSPAux].equals(String.valueOf(sujeitoPassivoDto.getId()))) {
						encontrouSP = true;
					}
				}
				
				if(!encontrouSP) {
					continue;
				}				
				
				for (ProcuradorDto procuradorDto : procuracaoDTO.getProcuradores()) {
				
					movimentacaoProcuracao = new MovimentacaoProcuracao();
					procurador = new Procurador();
					
					//Dados do procurador
					movimentacaoProcuracao.setProcurador(procurador);
					procurador.setId(procuradorDto.getIdProcurador());
					movimentacaoProcuracao.setTipoRepresentacao(EnumTipoProcurador.ADVOGADO.getTipo()); //Advogado
					//Pessoa
					procurador.setPessoa(new Pessoa());
					procurador.getPessoa().setIdPessoa(procuradorDto.getIdPessoa());
					if(procuradorDto.getIdEscritorio() != null && procuradorDto.getIdEscritorio() != 0) { 
						
						procurador.setEscritorioProcurador(new PessoaJuridica());
						procurador.getEscritorioProcurador().setIdPessoa(procuradorDto.getIdEscritorio()); 
						
					}					
					
					procurador.setEnderecoProcurador(getEnderecoProcurador(procuradorDto));
					
					if(!registrouComplementoPessoa) {
						List listaComplementoPessoa = getListaComplementoPessoa(procuracaoDTO.getProcuradores());
						procurador.setListaComplementoPessoa(listaComplementoPessoa);
						registrouComplementoPessoa = true;
					}
					
					movimentacaoProcuracao.setDataInicioMovimentacao(dataProcuracao);
					if(procuracaoDTO.getIndiProcuracaoPresencial().equals("S")){
						movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.S); //Ativa
					} else if(procuracaoDTO.getIndiProcuracaoPresencial() == null || procuracaoDTO.getIndiProcuracaoPresencial().equals("N") || procuracaoDTO.getIndiProcuracaoPresencial().isEmpty()){
						movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.N); //Inativa
					}
					
					sujeitoPassivoProcesso = new SujeitoPassivoProcesso();
					
					sujeitoPassivoProcesso.setId(sujeitoPassivoDto.getId());
					movimentacaoProcuracao.setSujeitoPassivoProcesso(sujeitoPassivoProcesso);
					
					listaMovimentacaoProcuracao.add(movimentacaoProcuracao);
					procuracao.getListaMovimentacaoProcuracao().add(movimentacaoProcuracao);
				}
			}
			
			if(listaMovimentacaoProcuracao.size() == 0) {
				continue;
			}
					
			if(procuracaoDTO.getDataValidade() != null) {
				procuracao.setDataValidade(procuracaoDTO.getDataValidade());
			}
			
			if(!procuracaoDTO.isPermiteSubstabelecimento()) {
				procuracao.setTipoPoderSubstabelecimento(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido());
			}			
			else {
				procuracao.setTipoPoderSubstabelecimento(procuracaoDTO.getReservaPoderes().charAt(0)); 
			}				
			procuracao.setTipoNaturezaJuridica(procuracaoDTO.getTipoNaturezaJuridica());
			procuracao.setDataEmissao(new Date());
			
			procuracao.setStatus(procuracaoDTO.getIndiProcuracaoPresencial().equals("N") ? EnumStatusProcuracao.PendenteAssinatura.getCodigo() : EnumStatusProcuracao.Ativa.getCodigo());
			
			procuracao.setIndiPrazoDeterminado(procuracaoDTO.getDataValidade() == null ? "N" : "S");
			/*procuracao.setInfoProcuracao("Pelo presente instrumento particular de procuração, os outorgantes nomeiam e constituem seu bastante procurador");*/
			if(procuracaoDTO.getReservaPoderes() != null && !procuracaoDTO.getReservaPoderes().equals("")) {
				procuracao.setTipoPoderSubstabelecimento(procuracaoDTO.getReservaPoderes().charAt(0));
			} else {
				//Sem poderes para substabelecer
				procuracao.setTipoPoderSubstabelecimento('1'); 
			}
			procuracao.setPessoaAssinante(new Pessoa());
			procuracao.getPessoaAssinante().setIdPessoa(procuracaoDTO.getIdPessoaAssinante());		
			procuracao.setIndiProcuracaoPresencial(procuracaoDTO.getIndiProcuracaoPresencial());
			
			listaProcuracao.add(procuracao);			
			
		}
		
		return listaProcuracao;
	}
	
	/**
	 * Provides a relational object ready to be persisted.
	 * 
	 * @param procuracaoDto
	 * @return SubEstabelecimentoProcuracao
	 * 
	 * @throws SefazException 
	 * 
	 */
	public SubEstabelecimentoProcuracao toSubestabelecimento(ProcuracaoDto procuracaoDto)  throws SefazException{
		
		if(procuracaoDto == null || procuracaoDto.getListaMovimentacoes() == null) { 
			throw new SefazException("The parameter null is not allowed.");
		}
			
		Date dataSubestabelecimento = new Date();
		boolean registrouComplementoPessoa = false;
		SubEstabelecimentoProcuracao subestabelecimento = new SubEstabelecimentoProcuracao(); 
			
		if(procuracaoDto.getIdSubstabelecimento() == null) {
			subestabelecimento.setStatus(EnumStatusSubstabelecimento.Incompleto.getCodigo());				
		}
		
		if(procuracaoDto.getIndiSubstabelecimentoPresencial() != null && procuracaoDto.getIndiSubstabelecimentoPresencial().equals("S")) {
			subestabelecimento.setIndiSubstabelecimentoPresencial(procuracaoDto.getIndiSubstabelecimentoPresencial().charAt(0));
			subestabelecimento.setStatus(EnumStatusSubstabelecimento.Completo.getCodigo());
		}
		
		else if(procuracaoDto.getIndiSubstabelecimentoPresencial() != null && procuracaoDto.getIndiSubstabelecimentoPresencial().equals("N")) {
			subestabelecimento.setIndiSubstabelecimentoPresencial(procuracaoDto.getIndiSubstabelecimentoPresencial().charAt(0));
			subestabelecimento.setStatus(EnumStatusSubstabelecimento.Incompleto.getCodigo());
		}
	
		if(procuracaoDto.getDataSubstabelecimento() != null) {
			subestabelecimento.setDataSubEstabelecimento(procuracaoDto.getDataSubstabelecimento());
		}
		
		if(procuracaoDto.getDataValidadeSubstabelecimento() != null) {
			subestabelecimento.setDataValidade(procuracaoDto.getDataValidadeSubstabelecimento());
		}
		
		if(procuracaoDto.getDataLimiteSubestabelecimento() != null) {
			subestabelecimento.setDataLimite(procuracaoDto.getDataLimiteSubestabelecimento());
		}
		
		if(!procuracaoDto.isPermiteSubstabelecimento()) {
			subestabelecimento.setIndiReserva('S');
		}			
		else {
			subestabelecimento.setIndiReserva('N'); 
		}				
		subestabelecimento.setDataSubEstabelecimento(new Date());			
		subestabelecimento.setListaMovimentacaoProcuracao(new ArrayList<MovimentacaoProcuracao>());
		
		List<MovimentacaoProcuracao> listaMovimentacaoProcuracao = new ArrayList<MovimentacaoProcuracao>();
		MovimentacaoProcuracao movimentacaoProcuracao;
		Procurador procurador;
		SujeitoPassivoProcesso sujeitoPassivoProcesso;						
			
		for (int contMovimentacao = 0; contMovimentacao < procuracaoDto.getListaMovimentacoes().size(); contMovimentacao++) {
			
			MovimentacaoProcuracao moviProcuracaoOrigem = movimentacaoProcuracaoService.find(procuracaoDto.getListaMovimentacoes().get(contMovimentacao));
				
			for (ProcuradorDto procuradorDto : procuracaoDto.getProcuradores()) {
			
				movimentacaoProcuracao = new MovimentacaoProcuracao();
				procurador = new Procurador();
				
				//Dados do procurador
				movimentacaoProcuracao.setProcurador(procurador);
				procurador.setId(procuradorDto.getIdProcurador());
				movimentacaoProcuracao.setTipoRepresentacao(EnumTipoProcurador.ADVOGADO.getTipo()); //Advogado
				//Pessoa
				procurador.setPessoa(new Pessoa());
				procurador.getPessoa().setIdPessoa(procuradorDto.getIdPessoa());
				if(procuradorDto.getIdEscritorio() != null && procuradorDto.getIdEscritorio() != 0) { 
					
					procurador.setEscritorioProcurador(new PessoaJuridica());
					procurador.getEscritorioProcurador().setIdPessoa(procuradorDto.getIdEscritorio()); 
					
				}
				
				procurador.setEnderecoProcurador(getEnderecoProcurador(procuradorDto));
				
				if(!registrouComplementoPessoa) {
					List listaComplementoPessoa = getListaComplementoPessoa(procuracaoDto.getProcuradores());
					procurador.setListaComplementoPessoa(listaComplementoPessoa);
					registrouComplementoPessoa = true;
				}
				
				movimentacaoProcuracao.setDataInicioMovimentacao(dataSubestabelecimento);
				if(procuracaoDto.getIndiSubstabelecimentoPresencial() != null && procuracaoDto.getIndiSubstabelecimentoPresencial().equals("S")) {
					movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.S); //Ativa 
				} else {
					movimentacaoProcuracao.setIndiMovmtProcuracaoAtiva(EnumSimNao.N); //Inativa
				}
				
				sujeitoPassivoProcesso = new SujeitoPassivoProcesso();
				
				sujeitoPassivoProcesso.setId(moviProcuracaoOrigem.getSujeitoPassivoProcesso().getId());
				movimentacaoProcuracao.setSujeitoPassivoProcesso(moviProcuracaoOrigem.getSujeitoPassivoProcesso());
				movimentacaoProcuracao.setMovimentacaoOrigemProcuracao(moviProcuracaoOrigem); 
				
				Procuracao procuracao = new Procuracao();
				procuracao.setId(procuracaoDto.getId());
				movimentacaoProcuracao.setProcuracao(procuracao);
				
				listaMovimentacaoProcuracao.add(movimentacaoProcuracao);
				subestabelecimento.getListaMovimentacaoProcuracao().add(movimentacaoProcuracao);
			}
		}
	
		return subestabelecimento;
	}
	
	/**
	 * Retorna as listas de complemento pessoa para serem persistidas
	 * @return
	 */
	private static List<List> getListaComplementoPessoa(List<ProcuradorDto> listaProcuradores) {
		
		FormattingSupport fs = new FormattingSupport();		
		List<EmailPessoa> listaEmailPessoa = new ArrayList<EmailPessoa>();
		List<TelefonePessoa> listaTelefone = new ArrayList<TelefonePessoa>();
		List<DocumentoAdvogado> listaDocumento = new ArrayList<DocumentoAdvogado>();
		
		/**
		 * Setando as constantes que serão implementadas posteriormentes como Enuns no projeto corporativo
		 */
		Integer tipoEmailPessoa = EnumTipoEmail.COMERCIAL.getCodigo(); //tipo email comercial utilizado no pat-e
		Integer tipoTelefonePessoa = EnumTipoTelefone.COMERCIAL.getCodigo(); //tipo telefone comercial utilizado no pat-e
		Integer tipoDocumento = 11; //tipo documento identidade conselho utilizado no pat-e
		
		List listaComplementoPessoa = new ArrayList();
		
		for(ProcuradorDto procuradorDto : listaProcuradores) {
			
			EmailPessoa emailPessoa = new EmailPessoa();
			emailPessoa.setEmail(procuradorDto.getEmailPessoa());
			emailPessoa.setTipoEmail(tipoEmailPessoa);
			emailPessoa.setIdPessoa(procuradorDto.getIdPessoa());
			
			if(emailPessoa.getId() != null || (emailPessoa.getEmail() != null && !emailPessoa.getEmail().equals(""))) {
				listaEmailPessoa.add(emailPessoa);
			}
			
			TelefonePessoa telefonePessoa = new TelefonePessoa();
			if(procuradorDto.getTelefone() != null && !procuradorDto.getTelefone().equals("")) {
				String telefone = fs.removeFormatacao(procuradorDto.getTelefone());
				telefonePessoa.setNumeroDDD(Integer.valueOf(telefone.substring(0, 2)));
				telefonePessoa.setNumeroTelefone(Integer.valueOf(telefone.substring(2))); 
			}
			telefonePessoa.setIdPessoa(procuradorDto.getIdPessoa());
			telefonePessoa.setTipoTelefone(tipoTelefonePessoa);
			
			if(telefonePessoa.getId() != null || (telefonePessoa.getNumeroDDD() != null && telefonePessoa.getNumeroTelefone() != null)) {
				listaTelefone.add(telefonePessoa);
			}
			
			DocumentoAdvogado documentoPessoa = new DocumentoAdvogado();
			documentoPessoa.setNumeroDocumento(procuradorDto.getNumeroOAB());
			Uf uf = new Uf();
			uf.setCodigo(procuradorDto.getUfAdvogado());
			documentoPessoa.setUf(uf);
			documentoPessoa.setSiglaOrgaoEmissor("OAB");
			DocumentoPessoaId documentoPessoaId = new DocumentoPessoaId();
			Pessoa pessoaDocumento = new Pessoa();
			pessoaDocumento.setIdPessoa(procuradorDto.getIdPessoa());
			documentoPessoaId.setPessoa(pessoaDocumento);
			TipoDocumento tipoDocumentoPessoa = new TipoDocumento();
			tipoDocumentoPessoa.setIdTipoDocumento(tipoDocumento);
			documentoPessoaId.setTipoDocumento(tipoDocumentoPessoa);
			documentoPessoa.setDocumentoPessoaId(documentoPessoaId);			
			
			if(documentoPessoa.getIdDocumentoPessoa() != null || (documentoPessoa.getNumeroDocumento() != null && !documentoPessoa.getNumeroDocumento().equals(""))) {
				listaDocumento.add(documentoPessoa);
			}		
			
		}
		listaComplementoPessoa.add(listaEmailPessoa);
		listaComplementoPessoa.add(listaTelefone);
		listaComplementoPessoa.add(listaDocumento);    
		
		return listaComplementoPessoa;
	}
	
	private static EnderecoProcurador getEnderecoProcurador(ProcuradorDto procuradorDto) {
		
		EnderecoProcurador enderecoProcurador = new EnderecoProcurador();
		enderecoProcurador.setTipoEndereco(EnumTipoEndereco.COMERCIAL.getCodigo()); //Endereço comercial
		
		Endereco endereco = new Endereco();				
		
		Logradouro logradouro = new Logradouro();
		logradouro.setCodigo(procuradorDto.getCodgLogradouro());
		endereco.setLogradouro(logradouro);
		
		endereco.setLogradouro(logradouro);
		endereco.setNumero(procuradorDto.getNumero());
		endereco.setNumeroLote(procuradorDto.getLote());
		endereco.setNumeroQuadra(procuradorDto.getQuadra());
		endereco.setComplemento(procuradorDto.getComplemento());
		
		enderecoProcurador.setEndereco(endereco);
		return enderecoProcurador;
	}	
	
}