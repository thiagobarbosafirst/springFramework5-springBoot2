package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.Date;

/**
 * Este DTO representa a movimentação da procuração.
 * 
 * @author Alex Carlos
 */
public class MovimentacaoDto {

	private Integer idProcesso;
	private String numeroPAT;

	private Integer idPessoaProcurador;
	private String nomeProcurador;
	private String cpfProcurador;

	private String tipoSujeito;
	private Integer idSujeito;
	private Integer idMovimentacaoProcuracao;

	private String cnpjCpf;
	private String nomeSujeito;
	private String tipoPessoa;

	private String codigoStatusMovimentacaoProcuracao;
	private String descricaoStatusMovimentacaoProcuracao;

	private Integer idProcuracao;
	private Date dataEmissaoProcuracao;
	private Date dataValidadeProcuracao;
	private String codigoStatusProcuracao;
	private String descricaoStatusProcuracao;

	private Integer idRenuncia;

	private Integer idRevogacao;

	private String indiReservaPoder;
	private String tipoPoder;
	private boolean permiteSubstabelecimento;

	private Integer idSubstabelecimento;
	private boolean substabelecimentoValidado;
	private String poderProcuracao;
	private String dataMaximaSubstab;
	
	private boolean checked;
	

	public Integer getIdProcesso() {
		return idProcesso;
	}

	public void setIdProcesso(Integer idProcesso) {
		this.idProcesso = idProcesso;
	}

	public String getNumeroPAT() {
		return numeroPAT;
	}

	public void setNumeroPAT(String numeroPAT) {
		this.numeroPAT = numeroPAT;
	}

	public Integer getIdPessoaProcurador() {
		return idPessoaProcurador;
	}

	public void setIdPessoaProcurador(Integer idPessoaProcurador) {
		this.idPessoaProcurador = idPessoaProcurador;
	}

	public String getNomeProcurador() {
		return nomeProcurador;
	}

	public void setNomeProcurador(String nomeProcurador) {
		this.nomeProcurador = nomeProcurador;
	}

	public String getCpfProcurador() {
		return cpfProcurador;
	}

	public void setCpfProcurador(String cpfProcurador) {
		this.cpfProcurador = cpfProcurador;
	}

	public String getTipoSujeito() {
		return tipoSujeito;
	}

	public void setTipoSujeito(String tipoSujeito) {
		this.tipoSujeito = tipoSujeito;
	}

	public Integer getIdMovimentacaoProcuracao() {
		return idMovimentacaoProcuracao;
	}

	public void setIdMovimentacaoProcuracao(Integer idMovimentacaoProcuracao) {
		this.idMovimentacaoProcuracao = idMovimentacaoProcuracao;
	}

	public String getCnpjCpf() {
		return cnpjCpf;
	}

	public void setCnpjCpf(String cnpjCpf) {
		this.cnpjCpf = cnpjCpf;
	}

	public String getNomeSujeito() {
		return nomeSujeito;
	}

	public void setNomeSujeito(String nomeSujeito) {
		this.nomeSujeito = nomeSujeito;
	}

	public String getCodigoStatusMovimentacaoProcuracao() {
		return codigoStatusMovimentacaoProcuracao;
	}

	public void setCodigoStatusMovimentacaoProcuracao(String codigoStatusMovimentacaoProcuracao) {
		this.codigoStatusMovimentacaoProcuracao = codigoStatusMovimentacaoProcuracao;
	}

	public String getDescricaoStatusMovimentacaoProcuracao() {
		return descricaoStatusMovimentacaoProcuracao;
	}

	public void setDescricaoStatusMovimentacaoProcuracao(String descricaoStatusMovimentacaoProcuracao) {
		this.descricaoStatusMovimentacaoProcuracao = descricaoStatusMovimentacaoProcuracao;
	}

	public Integer getIdProcuracao() {
		return idProcuracao;
	}

	public void setIdProcuracao(Integer idProcuracao) {
		this.idProcuracao = idProcuracao;
	}

	public Date getDataEmissaoProcuracao() {
		return dataEmissaoProcuracao;
	}

	public void setDataEmissaoProcuracao(Date dataEmissaoProcuracao) {
		this.dataEmissaoProcuracao = dataEmissaoProcuracao;
	}

	public Date getDataValidadeProcuracao() {
		return dataValidadeProcuracao;
	}

	public void setDataValidadeProcuracao(Date dataValidadeProcuracao) {
		this.dataValidadeProcuracao = dataValidadeProcuracao;
	}

	public String getCodigoStatusProcuracao() {
		return codigoStatusProcuracao;
	}

	public void setCodigoStatusProcuracao(String codigoStatusProcuracao) {
		this.codigoStatusProcuracao = codigoStatusProcuracao;
	}

	public String getDescricaoStatusProcuracao() {
		return descricaoStatusProcuracao;
	}

	public void setDescricaoStatusProcuracao(String descricaoStatusProcuracao) {
		this.descricaoStatusProcuracao = descricaoStatusProcuracao;
	}

	public Integer getIdRenuncia() {
		return idRenuncia;
	}

	public void setIdRenuncia(Integer idRenuncia) {
		this.idRenuncia = idRenuncia;
	}

	public Integer getIdRevogacao() {
		return idRevogacao;
	}

	public void setIdRevogacao(Integer idRevogacao) {
		this.idRevogacao = idRevogacao;
	}

	public String getIndiReservaPoder() {
		return indiReservaPoder;
	}

	public void setIndiReservaPoder(String indiReservaPoder) {
		this.indiReservaPoder = indiReservaPoder;
	}

	public String getTipoPoder() {
		return tipoPoder;
	}

	public void setTipoPoder(String tipoPoder) {
		this.tipoPoder = tipoPoder;
	}

	public boolean getPermiteSubstabelecimento() {
		return permiteSubstabelecimento;
	}

	public void setPermiteSubstabelecimento(boolean permiteSubstabelecimento) {
		this.permiteSubstabelecimento = permiteSubstabelecimento;
	}

	public Integer getIdSujeito() {
		return idSujeito;
	}

	public void setIdSujeito(Integer idSujeito) {
		this.idSujeito = idSujeito;
	}

	public Integer getIdSubstabelecimento() {
		return idSubstabelecimento;
	}

	public void setIdSubstabelecimento(Integer idSubstabelecimento) {
		this.idSubstabelecimento = idSubstabelecimento;
	}

	public String getTipoPessoa() {
		return tipoPessoa;
	}

	public void setTipoPessoa(String tipoPessoa) {
		this.tipoPessoa = tipoPessoa;
	}

	public boolean isSubstabelecimentoValidado() {
		return substabelecimentoValidado;
	}

	public void setSubstabelecimentoValidado(boolean substabelecimentoValidado) {
		this.substabelecimentoValidado = substabelecimentoValidado;
	}

	public String getPoderProcuracao() {
		return poderProcuracao;
	}

	public void setPoderProcuracao(String poderProcuracao) {
		this.poderProcuracao = poderProcuracao;
	}

	public String getDataMaximaSubstab() {
		return dataMaximaSubstab;
	}

	public void setDataMaximaSubstab(String dataMaximaSubstab) {
		this.dataMaximaSubstab = dataMaximaSubstab;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

}
