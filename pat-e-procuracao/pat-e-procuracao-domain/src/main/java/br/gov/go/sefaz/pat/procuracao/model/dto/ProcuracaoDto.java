package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.gov.go.sefaz.javaee.commons.constants.StatusSimNao;
import br.gov.go.sefaz.javaee.corporativo.model.Uf;

public class ProcuracaoDto {
	
	private String numeroProcesso;
	private Integer idProcesso;
	private String dataProcuracao;
	private String validade;
	private String poderProcuracao;
	private String tipoPessoaOutorgante;
	private String dataMaximaSubstab;
	private boolean substabelecimentoValidado;	
	
	private Integer id;
	private String usuarioLogado;
	private String nomeOutorgante;
	private String nomeOutorganteConsultado;
	private Integer idOutorgante;
	private String tipoNaturezaJuridica;
	private Date dataValidade;
	private Date dataValidadeSubstabelecimento;
	private Integer idPessoaAssinante;
	private ProcuradorDto procurador;
	private String processoSelecionado;
	private Date dataLimite;
	private String status;
	private String descricaoStatus;
	
	private boolean existeProcuracaoAtiva;
	private boolean existeProcesso;
	
//	private PessoaFisica pessoaFisica;
//	
//	private Endereco endereco;
	
	
	private String numeroOAB;
	private Uf ufAdvogado;
	
	private String email;
	private String telefone;
	
	private boolean permiteSubstabelecimento;
	private StatusSimNao simNao;
	
	private String reservaPoderes;
	
	private List<ProcessoSujeitoPassivoDto> sujeitoPassivos; 	
	private List<ProcuradorDto> procuradores; 	
	private List<AgrupadorMovimentacaoDto> listaProcessos;
	
	private Integer idSubstabelecimento;
	private Date dataSubstabelecimento;
	private Date dataLimiteSubestabelecimento;
	private String reservaPoderesSubestabelecimento;	
	private String indiSubstabelecimentoPresencial;
	
	private String indiProcuracaoPresencial;
	
	private List<DocumentoMovimentacaoPresencialDto> files;
	private List<Integer> listaMovimentacoes; 
	private String[] listaSujeitoPassivos; 
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsuarioLogado() {
		return usuarioLogado;
	}
	
	public void setUsuarioLogado(String usuarioLogado) {
		this.usuarioLogado = usuarioLogado;
	}
	
	public String getNomeOutorgante() {
		return nomeOutorgante;
	}
	
	public void setNomeOutorgante(String nomeOutorgante) {
		this.nomeOutorgante = nomeOutorgante;
	}

	public String getNomeOutorganteConsultado() {
		return nomeOutorganteConsultado;
	}

	public void setNomeOutorganteConsultado(String nomeOutorganteConsultado) {
		this.nomeOutorganteConsultado = nomeOutorganteConsultado;
	}

	public Integer getIdOutorgante() {
		return idOutorgante;
	}

	public void setIdOutorgante(Integer idOutorgante) {
		this.idOutorgante = idOutorgante;
	}

//	public PessoaFisica getPessoaFisica() {
//		return pessoaFisica == null ? new PessoaFisica() : pessoaFisica;
//	}
//
//	public void setPessoaFisica(PessoaFisica pessoaFisica) {
//		this.pessoaFisica = pessoaFisica;
//	}
//
//	public Endereco getEndereco() {
//		return endereco;
//	}
//
//	public void setEndereco(Endereco endereco) {
//		this.endereco = endereco;
//	}

	public Uf getUfAdvogado() {
		return ufAdvogado;
	}

	public void setUfAdvogado(Uf ufAdvogado) {
		this.ufAdvogado = ufAdvogado;
	}

	public String getNumeroOAB() {
		return numeroOAB;
	}

	public void setNumeroOAB(String numeroOAB) {
		this.numeroOAB = numeroOAB;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public boolean isPermiteSubstabelecimento() {
		return permiteSubstabelecimento;
	}

	public void setPermiteSubstabelecimento(boolean permiteSubstabelecimento) {
		this.permiteSubstabelecimento = permiteSubstabelecimento;
	}

	public String getReservaPoderes() {
		return reservaPoderes;
	}

	public void setReservaPoderes(String reservaPoderes) {
		this.reservaPoderes = reservaPoderes;
	}

//	public PessoaJuridica getEscritorioAdvogado() {
//		return escritorioAdvogado;
//	}
//
//	public void setEscritorioAdvogado(PessoaJuridica escritorioAdvogado) {
//		this.escritorioAdvogado = escritorioAdvogado;
//	}
//
//	public Endereco getEnderecoEscritorio() {
//		return enderecoEscritorio;
//	}
//
//	public void setEnderecoEscritorio(Endereco enderecoEscritorio) {
//		this.enderecoEscritorio = enderecoEscritorio;
//	}

	public List<ProcessoSujeitoPassivoDto> getSujeitoPassivos() {
		return sujeitoPassivos;
	}
	
	public String[] getSujeitoPassivoIdsAsArray(){
		List<String> ids = new ArrayList<String>();
		for(ProcessoSujeitoPassivoDto pspDto : sujeitoPassivos){
			for(SujeitoPassivoDto spDto : pspDto.getListaSujeitoPassivo()){
				if(!ids.contains(spDto.getId())){
					ids.add(spDto.getId().toString());
				}
			}
		}
		return ids.toArray(new String[0]);
	}

	public void setSujeitoPassivos(List<ProcessoSujeitoPassivoDto> sujeitoPassivos) {
		this.sujeitoPassivos = sujeitoPassivos;
	}

	public List<ProcuradorDto> getProcuradores() {
		return procuradores;
	}

	public void setProcuradores(List<ProcuradorDto> procuradores) {
		this.procuradores = procuradores;
	}

	public ProcuradorDto getProcurador() {
		return procurador;
	}

	public void setProcurador(ProcuradorDto procurador) {
		this.procurador = procurador;
	}

	public String getTipoNaturezaJuridica() {
		return tipoNaturezaJuridica;
	}

	public void setTipoNaturezaJuridica(String tipoNaturezaJuridica) {
		this.tipoNaturezaJuridica = tipoNaturezaJuridica;
	}

	public Date getDataValidade() {
		return dataValidade;
	}

	public void setDataValidade(Date dataValidade) {
		this.dataValidade = dataValidade;
	}

	public Integer getIdPessoaAssinante() {
		return idPessoaAssinante;
	}

	public void setIdPessoaAssinante(Integer idPessoaAssinante) {
		this.idPessoaAssinante = idPessoaAssinante;
	}

	public String getProcessoSelecionado() {
		return processoSelecionado;
	}

	public void setProcessoSelecionado(String processoSelecionado) {
		this.processoSelecionado = processoSelecionado;
	}

	public StatusSimNao getSimNao() {
		return simNao;
	}

	public void setSimNao(StatusSimNao simNao) {
		this.simNao = simNao;
	}

	public Date getDataLimite() {
		return dataLimite;
	}

	public void setDataLimite(Date dataLimite) {
		this.dataLimite = dataLimite;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public String getDataProcuracao() {
		return dataProcuracao;
	}

	public void setDataProcuracao(String dataProcuracao) {
		this.dataProcuracao = dataProcuracao;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescricaoStatus() {
		return descricaoStatus;
	}

	public void setDescricaoStatus(String descricaoStatus) {
		this.descricaoStatus = descricaoStatus;
	}

	public Integer getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(Integer idProcesso) {
		this.idProcesso = idProcesso;
	}

	public boolean isExisteProcuracaoAtiva() {
		return existeProcuracaoAtiva;
	}

	public void setExisteProcuracaoAtiva(boolean existeProcuracaoAtiva) {
		this.existeProcuracaoAtiva = existeProcuracaoAtiva;
	}

	public boolean isExisteProcesso() {
		return existeProcesso;
	}

	public void setExisteProcesso(boolean existeProcesso) {
		this.existeProcesso = existeProcesso;
	}

	public String getValidade() {
		return validade;
	}

	public void setValidade(String validade) {
		this.validade = validade;
	}

	public String getPoderProcuracao() {
		return poderProcuracao;
	}

	public void setPoderProcuracao(String poderProcuracao) {
		this.poderProcuracao = poderProcuracao;
	}

	public List<AgrupadorMovimentacaoDto> getListaProcessos() {
		return listaProcessos;
	}

	public void setListaProcessos(List<AgrupadorMovimentacaoDto> listaProcessos) {
		this.listaProcessos = listaProcessos;
	}

	public String getTipoPessoaOutorgante() {
		return tipoPessoaOutorgante;
	}

	public void setTipoPessoaOutorgante(String tipoPessoaOutorgante) {
		this.tipoPessoaOutorgante = tipoPessoaOutorgante;
	}

	public Date getDataSubstabelecimento() {
		return dataSubstabelecimento;
	}

	public void setDataSubstabelecimento(Date dataSubstabelecimento) {
		this.dataSubstabelecimento = dataSubstabelecimento;
	}

	public Date getDataLimiteSubestabelecimento() {
		return dataLimiteSubestabelecimento;
	}

	public void setDataLimiteSubestabelecimento(Date dataLimiteSubestabelecimento) {
		this.dataLimiteSubestabelecimento = dataLimiteSubestabelecimento;
	}

	public String getReservaPoderesSubestabelecimento() {
		return reservaPoderesSubestabelecimento;
	}

	public void setReservaPoderesSubestabelecimento(String reservaPoderesSubestabelecimento) {
		this.reservaPoderesSubestabelecimento = reservaPoderesSubestabelecimento;
	}

	public Integer getIdSubstabelecimento() {
		return idSubstabelecimento;
	}

	public void setIdSubstabelecimento(Integer idSubstabelecimento) {
		this.idSubstabelecimento = idSubstabelecimento;
	}

	public String getIndiSubstabelecimentoPresencial() {
		return indiSubstabelecimentoPresencial;
	}

	public void setIndiSubstabelecimentoPresencial(String indiSubstabelecimentoPresencial) {
		this.indiSubstabelecimentoPresencial = indiSubstabelecimentoPresencial;
	}

	public String getDataMaximaSubstab() {
		return dataMaximaSubstab;
	}

	public void setDataMaximaSubstab(String dataMaximaSubstab) {
		this.dataMaximaSubstab = dataMaximaSubstab;
	}

	public boolean isSubstabelecimentoValidado() {
		return substabelecimentoValidado;
	}

	public void setSubstabelecimentoValidado(boolean substabelecimentoValidado) {
		this.substabelecimentoValidado = substabelecimentoValidado;
	}

	public Date getDataValidadeSubstabelecimento() {
		return dataValidadeSubstabelecimento;
	}

	public void setDataValidadeSubstabelecimento(Date dataValidadeSubstabelecimento) {
		this.dataValidadeSubstabelecimento = dataValidadeSubstabelecimento;
	}

	public String getIndiProcuracaoPresencial() {
		if(this.indiProcuracaoPresencial == null) return "N";
		return this.indiProcuracaoPresencial;
	}

	public void setIndiProcuracaoPresencial(String indiProcuracaoPresencial) {
		this.indiProcuracaoPresencial = indiProcuracaoPresencial;
	}

	public List<DocumentoMovimentacaoPresencialDto> getFiles() {
		return files;
	}

	public void setFiles(List<DocumentoMovimentacaoPresencialDto> files) {
		this.files = files;
	}

	public List<Integer> getListaMovimentacoes() {
		return listaMovimentacoes;
	}

	public void setListaMovimentacoes(List<Integer> listaMovimentacoes) {
		this.listaMovimentacoes = listaMovimentacoes;
	}

	public String[] getListaSujeitoPassivos() {
		return listaSujeitoPassivos;
	}

	public void setListaSujeitoPassivos(String[] listaSujeitoPassivos) {
		this.listaSujeitoPassivos = listaSujeitoPassivos;
	}

}
