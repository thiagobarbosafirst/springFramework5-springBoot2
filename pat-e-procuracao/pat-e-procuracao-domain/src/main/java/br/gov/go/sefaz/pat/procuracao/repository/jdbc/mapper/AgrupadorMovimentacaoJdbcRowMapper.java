package br.gov.go.sefaz.pat.procuracao.repository.jdbc.mapper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import br.gov.go.sefaz.javaee.commons.support.Data;
import br.gov.go.sefaz.pat.enumerator.EnumSimNao;
import br.gov.go.sefaz.pat.procuracao.constants.TipoAgrupadorMovimentacaoMapper;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumReservaPoderPermitido;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumStatusSubstabelecimento;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoDto;
import br.gov.go.sefaz.pat.procuracao.model.dto.MovimentacaoDto;

public class AgrupadorMovimentacaoJdbcRowMapper {

	public List<AgrupadorMovimentacaoDto> converterQuery(List<Map<String,Object>> lista, int tipoAgrupadorMovimentacaoMapper){
		List<MovimentacaoDto> sujeitos = construirListaSujeitos(lista);		
		return criarListaAgrupadores(lista, sujeitos, tipoAgrupadorMovimentacaoMapper);		
	}

	private List<MovimentacaoDto> construirListaSujeitos(List<Map<String, Object>> lista) {
		List<MovimentacaoDto> sujeitos = new ArrayList<MovimentacaoDto>();
		
		for (Map<String, Object> item : lista) {			
			MovimentacaoDto sujeito = criarMovimentacaoObj(item);			
			sujeitos.add(sujeito);			
		}
		
		return sujeitos;
	}

	private MovimentacaoDto criarMovimentacaoObj(Map<String, Object> item) {
		MovimentacaoDto movimentacao = new MovimentacaoDto();			
		
		movimentacao.setIdProcesso(getInt(item.get("ID_PROCESSO")));
		movimentacao.setNumeroPAT(getString(item.get("NUMR_PAT")));
		movimentacao.setIdPessoaProcurador(getInt(item.get("ID_PESSOA_PROCURADOR")));
		movimentacao.setNomeProcurador(getString(item.get("NOME_PROCURADOR")));
		movimentacao.setCpfProcurador(getString(item.get("CPF_PROCURADOR")));
		movimentacao.setCnpjCpf(getString(item.get("CNPJ_CPF")));
		if(movimentacao.getCnpjCpf().length() == 14) {
			movimentacao.setTipoPessoa("J");
		} else {
			movimentacao.setTipoPessoa("F"); 
		}
		movimentacao.setNomeSujeito(getString(item.get("NOME_SUJEITO")));
		movimentacao.setTipoSujeito(getString(item.get("TIPO_SUJEITO_PASSIVO")));
		movimentacao.setIdSujeito(getInt(item.get("ID_SUJEITO_PASSIVO_PROCESSO")));		
		movimentacao.setIdMovimentacaoProcuracao(getInt(item.get("ID_MOVMT_PROCURACAO")));		
		movimentacao.setCodigoStatusMovimentacaoProcuracao(getString(item.get("STATUS_MOVIMENTACAO_CODIGO")));
		movimentacao.setDescricaoStatusMovimentacaoProcuracao(getString(item.get("STATUS_MOVIMENTACAO_DESC")));
		
		movimentacao.setIdProcuracao(getInt(item.get("ID_PROCURACAO")));
		movimentacao.setDataEmissaoProcuracao(getDate(item.get("DATA_EMISSAO_PROCURACAO")));
		movimentacao.setDataValidadeProcuracao(getDate(item.get("DATA_VALIDADE_PROCURACAO")));
		movimentacao.setCodigoStatusProcuracao(getString(item.get("STAT_PROCURACAO")));
		movimentacao.setDescricaoStatusProcuracao(getString(item.get("STATUS_PROCURACAO_DESC")));
		
		movimentacao.setIdRevogacao(getInt(item.get("ID_REVOGACAO_PROCURACAO")));
		
		movimentacao.setIdRenuncia(getInt(item.get("ID_RENUNCIA_OUTORGA")));
		
		movimentacao.setIdSubstabelecimento(getInt(item.get("ID_SUBSTAB_PROCR")));
		movimentacao.setIndiReservaPoder(getString(item.get("INDI_RESERVA_SUBSTAB_PROCR")));
		if(movimentacao.getIndiReservaPoder() != null){
			for(EnumReservaPoderPermitido enumReservaPoderPermitido : EnumReservaPoderPermitido.values()) {
				movimentacao.setTipoPoder("Sem poderes para substabelecer");
				movimentacao.setPermiteSubstabelecimento(false);
				if(movimentacao.getIndiReservaPoder().equals(enumReservaPoderPermitido.getIndiReservaPermitido())) {
					movimentacao.setTipoPoder(enumReservaPoderPermitido.getSignificadoReservaPermitido());
					movimentacao.setPermiteSubstabelecimento(true);
					break;
				}
			}			
		}	
		
		Character reservaPoder =  getString(item.get("TIPO_PODER_SUBSTAB")).charAt(0);	
		EnumReservaPoderPermitido enumReservaPoderPermitido = EnumReservaPoderPermitido.parse(reservaPoder);
		movimentacao.setPoderProcuracao(enumReservaPoderPermitido.getSignificadoReservaPermitido());

		movimentacao.setDataMaximaSubstab(item.get("DATA_MAXIMA_SUBSTAB") == null ? "" : Data.formatar(getDate(item.get("DATA_MAXIMA_SUBSTAB")), "dd/MM/yyyy"));
		
		if(enumReservaPoderPermitido.getIndiReservaPermitido().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
			movimentacao.setPermiteSubstabelecimento(false);
			movimentacao.setSubstabelecimentoValidado(false);
		}
		else {
			movimentacao.setPermiteSubstabelecimento(true);
			movimentacao.setSubstabelecimentoValidado(true);
			
			Date dataAtual = null;
			try {
				dataAtual = Data.toDate(Data.formatar(new java.util.Date(), Data.DATA_PADRAO), Data.DATA_PADRAO);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(item.get("DATA_VALIDADE_PROCURACAO") != null && getDate(item.get("DATA_VALIDADE_PROCURACAO")).before(dataAtual)) {
				movimentacao.setSubstabelecimentoValidado(false);
			}
			if(item.get("DATA_MAXIMA_SUBSTAB") != null && getDate(item.get("DATA_MAXIMA_SUBSTAB")).before(dataAtual)) {
				movimentacao.setSubstabelecimentoValidado(false);
			}
		}	
		
		return movimentacao;
	}
	
	private AgrupadorMovimentacaoDto criarAgrupadorRenuncia(List<MovimentacaoDto> movimentacao, Map<String, Object> item, List<AgrupadorMovimentacaoDto> agrupadores) {
		
		AgrupadorMovimentacaoDto agrupador = new AgrupadorMovimentacaoDto();
		boolean renuncia = tipoConsultaRenuncia(movimentacao);

		Integer idAgrupador = renuncia ? getInt(item.get("ID_RENUNCIA_OUTORGA")) : getInt(item.get("ID_PROCESSO"));
		boolean existeAgrupador = renuncia ? existeRenuncia(agrupadores, idAgrupador) : existeProcesso(agrupadores, idAgrupador); 
		
		if(!existeAgrupador){							
			
			agrupador.setIdProcesso(getInt(item.get("ID_PROCESSO")));
			agrupador.setNumeroPAT(getString(item.get("NUMR_PAT")));
			agrupador.setDataFormalizacao(getDate(item.get("DATA_FORMLZCAO_PAT")));
			agrupador.setStatusProcesso(getString(item.get("STAT_PAT")));		
			agrupador.setRitoProcessual(getString(item.get("DESC_RITO_PROCESSUAL")));			
			
			if(renuncia){
				agrupador.setIdRenuncia(getInt(item.get("ID_RENUNCIA_OUTORGA")));
				agrupador.setDataRenuncia(getDate(item.get("DATA_RENUNCIA_OUTORGA")));
				agrupador.setDataRenuncia(getDate(item.get("DATA_RENUNCIA_OUTORGA")));
				agrupador.setRenunciaPendenteAssinaturaDigital(getString(item.get("PENDENTE_ASSINATURA_DIG")));
				agrupador.setIndiRenunciaPresencial(getString(item.get("INDI_RENUNCIA_PRESENCIAL")));
			}				
			
			agrupador.setMovimentacoesDto(getSujeitos(movimentacao, idAgrupador));
			return agrupador;
	    }
		
		return null;		
		
	}
	
	private AgrupadorMovimentacaoDto criarAgrupadorPorProcesso(List<MovimentacaoDto> movimentacao, Map<String, Object> item, List<AgrupadorMovimentacaoDto> agrupadores) {
		
		AgrupadorMovimentacaoDto agrupador = new AgrupadorMovimentacaoDto();
		Integer idAgrupador = getInt(item.get("ID_PROCESSO"));			
		
		if(!existeProcesso(agrupadores, idAgrupador)){										
			agrupador.setIdProcesso(getInt(item.get("ID_PROCESSO")));
			agrupador.setNumeroPAT(getString(item.get("NUMR_PAT")));
			agrupador.setDataFormalizacao(getDate(item.get("DATA_FORMLZCAO_PAT")));
			agrupador.setIdRevogacao(getInt(item.get("ID_REVOGACAO_PROCURACAO")));
			agrupador.setDataRevogacao(getDate(item.get("DATA_REVOGACAO_PROCURACAO")));
			String indiPendenteAssinatura =(getString(item.get("INDI_PENDENTE_ASSINATURA_DIG")));
			agrupador.setIndiPendenteAssinaturaAsChar((!StringUtils.isEmpty(indiPendenteAssinatura) ? indiPendenteAssinatura.charAt(0) : null));
			
			agrupador.setMovimentacoesDto(getMovimentacoesPorProcesso(movimentacao, idAgrupador));					
			return agrupador;
		}								
		return null;									
	}
	
	private AgrupadorMovimentacaoDto criarAgrupadorRevogacao(List<MovimentacaoDto> movimentacao, Map<String, Object> item, List<AgrupadorMovimentacaoDto> agrupadores) {
		
		AgrupadorMovimentacaoDto agrupador = new AgrupadorMovimentacaoDto();
		Integer idAgrupador = getInt(item.get("ID_REVOGACAO_PROCURACAO")); 	
		
		if(!existeRevogacao(agrupadores, idAgrupador)){							
			agrupador.setIdProcesso(getInt(item.get("ID_PROCESSO")));
			agrupador.setNumeroPAT(getString(item.get("NUMR_PAT")));
			agrupador.setDataFormalizacao(getDate(item.get("DATA_FORMLZCAO_PAT")));			
			agrupador.setIdRevogacao(getInt(item.get("ID_REVOGACAO_PROCURACAO")));
			agrupador.setDataRevogacao(getDate(item.get("DATA_REVOGACAO_PROCURACAO")));
			String indiPendenteAssinatura =(getString(item.get("INDI_PENDENTE_ASSINATURA_DIG")));
			agrupador.setIndiPendenteAssinaturaAsChar((!StringUtils.isEmpty(indiPendenteAssinatura) ? indiPendenteAssinatura.charAt(0) : null));	
			String indiRevogacaoPresencial =(getString(item.get("INDI_REVOGACAO_PRESENCIAL")));
			agrupador.setIndiModalidadeAsChar((!StringUtils.isEmpty(indiRevogacaoPresencial) ? indiRevogacaoPresencial.charAt(0) : null));
			
			agrupador.setMovimentacoesDto(getSujeitosRevogacao(movimentacao, idAgrupador));
			return agrupador;
		}											
		return null;									
	}
	
	private AgrupadorMovimentacaoDto criarAgrupadorSubstabelecimento(List<MovimentacaoDto> movimentacao, Map<String, Object> item, List<AgrupadorMovimentacaoDto> agrupadores) {
		
		AgrupadorMovimentacaoDto agrupador = new AgrupadorMovimentacaoDto();
		Integer idAgrupador = getInt(item.get("ID_SUBSTAB_PROCR"));
		if(!existeSubstabelecimento(agrupadores, idAgrupador)){
			agrupador.setNumeroPAT(getString(item.get("NUMR_PAT")));
			agrupador.setIdSubstabelecimento(getInt(item.get("ID_SUBSTAB_PROCR"))); 
			agrupador.setDataSubstabelecimento(getDate(item.get("DATA_SUBSTAB_PROCR")));
			agrupador.setPermiteSubstabelecimento(EnumSimNao.valueOf(getString(item.get("INDI_RESERVA_SUBSTAB_PROCR"))).getDescricao());				
			agrupador.setDataValidadeSubstabelecimento(getDate(item.get("DATA_VALIDADE_SUBSTAB_PROCR")));
			agrupador.setDataLimiteSubstabelecimento(getDate(item.get("DATA_LIMITE_SUBSTAB")));
			agrupador.setDescricaoStatusSubstabelecimento(EnumStatusSubstabelecimento.parse(getString(item.get("STAT_SUBSTAB_PROCR")).charAt(0)).getDescricao());
			agrupador.setStatusSubstabelecimento(getString(item.get("STAT_SUBSTAB_PROCR")));
			String indiPresencial =(getString(item.get("INDI_SUBSTAB_PRESENCIAL")));
			agrupador.setIndiModalidadeAsChar((!StringUtils.isEmpty(indiPresencial) ? indiPresencial.charAt(0) : null)); 
			
			agrupador.setMovimentacoesDto(getMovimentacoesPorSubstabelecimento(movimentacao, idAgrupador));
			return agrupador;
		}
			
		return null;				
	}
	
	private AgrupadorMovimentacaoDto criarAgrupadorProcuracao(List<MovimentacaoDto> movimentacao, Map<String, Object> item, List<AgrupadorMovimentacaoDto> agrupadores) {
		
		AgrupadorMovimentacaoDto agrupador = new AgrupadorMovimentacaoDto();		
		Integer idAgrupador = getInt(item.get("ID_PROCURACAO"));
					
		if(!existeProcuracao(agrupadores, idAgrupador)){										
			agrupador.setIdProcuracao(getInt(item.get("ID_PROCURACAO")));
			agrupador.setDataEmissaoProcuracao(getDate(item.get("DATA_EMISSAO_PROCURACAO")));
			agrupador.setDataFormalizacao(getDate(item.get("DATA_FORMLZCAO_PROCURACAO")));
			agrupador.setDataValidadeProcuracao(getDate(item.get("DATA_VALIDADE_PROCURACAO")));
			agrupador.setStatusProcuracao(getString(item.get("STAT_PROCURACAO")));
			agrupador.setPoderProcuracao(getString(item.get("TIPO_PODER_SUBSTAB")));
			agrupador.setDescricaoStatusProcuracao(getString(item.get("DESC_STATUS_PROC")));
			agrupador.setDescricaoPoderProcuracao(getString(item.get("DESC_TIPO_PODER_PROC")));
			agrupador.setNumeroPAT(getString(item.get("NUMR_PAT")));
			String indiProcuracaooPresencial = (getString(item.get("INDI_PROCURACAO_PRESENCIAL")));
			agrupador.setIndiModalidadeAsChar((!StringUtils.isEmpty(indiProcuracaooPresencial) ? indiProcuracaooPresencial.charAt(0) : null));
			
			agrupador.setIdPessoaProcurador(getInt(item.get("ID_PESSOA_PROCURADOR")));
			Character reservaPoder =  getString(item.get("TIPO_PODER_SUBSTAB")).charAt(0);	
			EnumReservaPoderPermitido enumReservaPoderPermitido = EnumReservaPoderPermitido.parse(reservaPoder);
			
			if(enumReservaPoderPermitido.getIndiReservaPermitido().equals(EnumReservaPoderPermitido.SemPoderesParaSubstabelecer.getIndiReservaPermitido())) {
				agrupador.setSubstabelecimentoValidado(false);
			}
			else {
				agrupador.setSubstabelecimentoValidado(true);
				
				Date dataAtual = null;
				try {
					dataAtual = Data.toDate(Data.formatar(new java.util.Date(), Data.DATA_PADRAO), Data.DATA_PADRAO);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(item.get("DATA_VALIDADE_PROCURACAO") != null && getDate(item.get("DATA_VALIDADE_PROCURACAO")).before(dataAtual)) {
					agrupador.setSubstabelecimentoValidado(false);
				}
				if(item.get("DATA_MAXIMA_SUBSTAB") != null && getDate(item.get("DATA_MAXIMA_SUBSTAB")).before(dataAtual)) {
					agrupador.setSubstabelecimentoValidado(false);
				}
			}	

			agrupador.setMovimentacoesDto(getSujeitosProcuracao(movimentacao, idAgrupador));
			
			return agrupador;
	    }		
		return null;				
	}
	
	private List<AgrupadorMovimentacaoDto> criarListaAgrupadores(List<Map<String, Object>> query, List<MovimentacaoDto> sujeitos, int tipoAgrupadorMovimentacaoMapper) {		
		
		List<AgrupadorMovimentacaoDto> agrupadores = new ArrayList<AgrupadorMovimentacaoDto>();
		AgrupadorMovimentacaoDto agrupador = null;
		
		for (Map<String, Object> item : query){			
			
			if(tipoAgrupadorMovimentacaoMapper == TipoAgrupadorMovimentacaoMapper.RENUNCIA) {
				agrupador = criarAgrupadorRenuncia(sujeitos, item,agrupadores);
			}
			if(tipoAgrupadorMovimentacaoMapper == TipoAgrupadorMovimentacaoMapper.SUBSTABELECIMENTO) {
				agrupador = criarAgrupadorSubstabelecimento(sujeitos, item,agrupadores);
			}
			if(tipoAgrupadorMovimentacaoMapper == TipoAgrupadorMovimentacaoMapper.PROCESSO) {
				agrupador = criarAgrupadorPorProcesso(sujeitos, item,agrupadores);
			}
			if(tipoAgrupadorMovimentacaoMapper == TipoAgrupadorMovimentacaoMapper.REVOGACAO) {
				agrupador = criarAgrupadorRevogacao(sujeitos, item,agrupadores);
			}
			if(tipoAgrupadorMovimentacaoMapper == TipoAgrupadorMovimentacaoMapper.PROCURACAO) {
				agrupador = criarAgrupadorProcuracao(sujeitos, item,agrupadores);
			}
			if(tipoAgrupadorMovimentacaoMapper == TipoAgrupadorMovimentacaoMapper.PROCURACAO_PROCURADOR) {
				agrupador = criarAgrupadorProcuracao(sujeitos, item,agrupadores); 
			}
				
			if(agrupador != null) 
				agrupadores.add(agrupador);	
		}
		
		return agrupadores;
	}

	private boolean tipoConsultaRenuncia(List<MovimentacaoDto> sujeitos) {
		boolean renuncia;
		renuncia = sujeitos.stream().filter(f -> f.getIdRenuncia() != null).count() > 0;
		return renuncia;
	}
	
	private boolean tipoConsultaSubstabelecimento(List<MovimentacaoDto> sujeitos) {
		boolean substabelecimento;
		substabelecimento = sujeitos.stream().filter(f -> f.getIdSubstabelecimento() != null).count() > 0;
		return substabelecimento; 
	}
	
	private List<MovimentacaoDto> getSujeitos(List<MovimentacaoDto> sujeitos, Integer idAgrupador) {
		List<MovimentacaoDto> retorno = new ArrayList<MovimentacaoDto>();
		Integer idConsulta;
		
		for (MovimentacaoDto sujeito : sujeitos) {
			idConsulta =  tipoConsultaRenuncia(sujeitos) ? sujeito.getIdRenuncia() : sujeito.getIdProcesso();
			
			if(idConsulta.equals(idAgrupador))
				retorno.add(sujeito);
		}
		
		return retorno;
	}
	
	private List<MovimentacaoDto> getMovimentacoesPorSubstabelecimento(List<MovimentacaoDto> listaMovimentacaoDto, Integer idAgrupador) {
		List<MovimentacaoDto> retorno = new ArrayList<MovimentacaoDto>();
		Integer idConsulta;
		
		for (MovimentacaoDto movimentacaoDto : listaMovimentacaoDto) { 
			idConsulta =  movimentacaoDto.getIdSubstabelecimento();
			if(idConsulta.equals(idAgrupador))
				retorno.add(movimentacaoDto); 
		}
		
		return retorno;
	}	
	
	/**
	 * Agrupador por idProcesso
	 * @param sujeitos
	 * @param idAgrupador
	 * @return
	 */
	private List<MovimentacaoDto> getMovimentacoesPorProcesso(List<MovimentacaoDto> sujeitos, Integer idAgrupador) {
		List<MovimentacaoDto> retorno = new ArrayList<MovimentacaoDto>();
		Integer idConsulta;
		for (MovimentacaoDto sujeito : sujeitos) {
			idConsulta =  sujeito.getIdProcesso();
			if(idConsulta.equals(idAgrupador))
				retorno.add(sujeito);
		}					
		return retorno;
	}

	private List<MovimentacaoDto> getSujeitosRevogacao(List<MovimentacaoDto> sujeitos, Integer idAgrupador) {
		List<MovimentacaoDto> retorno = new ArrayList<MovimentacaoDto>();
		Integer idConsulta;
		
		for (MovimentacaoDto sujeito : sujeitos) {
			idConsulta = sujeito.getIdRevogacao();
			if(idConsulta.equals(idAgrupador))
				retorno.add(sujeito);
		}
		return retorno;
	}
	
	private List<MovimentacaoDto> getSujeitosProcuracao(List<MovimentacaoDto> sujeitos, Integer idAgrupador) {
		List<MovimentacaoDto> retorno = new ArrayList<MovimentacaoDto>();
		Integer idConsulta;
		
		for (MovimentacaoDto sujeito : sujeitos) {
			idConsulta =  sujeito.getIdProcuracao();
			if(idConsulta.equals(idAgrupador))
				retorno.add(sujeito);
		}		
		return retorno;
	}
		
	private boolean existeProcesso(List<AgrupadorMovimentacaoDto> processos,Integer idProcesso){
		return (processos.stream().filter(f -> f.getIdProcesso().equals(idProcesso)).count() > 0);		
	}
	
	private boolean existeRenuncia(List<AgrupadorMovimentacaoDto> processos,Integer idRenuncia){
		return (processos.stream().filter(f -> f.getIdRenuncia().equals(idRenuncia)).count() > 0);			
	}
	
	private boolean existeRevogacao(List<AgrupadorMovimentacaoDto> processos,Integer idRevogacao){
		return (processos.stream().filter(f -> f.getIdRevogacao().equals(idRevogacao)).count() > 0);			
	}
	
	private boolean existeProcuracao(List<AgrupadorMovimentacaoDto> processos,Integer idProcuracao){
		return (processos.stream().filter(f -> f.getIdProcuracao().equals(idProcuracao)).count() > 0);			
	}
	
	private boolean existeSubstabelecimento(List<AgrupadorMovimentacaoDto> agrupadorMovimentacao,Integer idSubstabelecimento){
		return (agrupadorMovimentacao.stream().filter(f -> f.getIdSubstabelecimento().equals(idSubstabelecimento)).count() > 0); 			
	}
	
	private Integer getInt(Object field){
		return (field == null) ? null : Integer.parseInt(field.toString());
	}
	
	private String getString(Object field){
		return (field == null) ? null : field.toString();
	}
	
	private Date getDate(Object field){
		return (field == null) ? null : ((Date)field);
	}
}
