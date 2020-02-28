package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.Date;

/** 
 * Este DTO  representa os sujeitos passivos da renúncia de procuração.
@author Marcos Jr Lopez
*/
public class RenunciaSujeitoDTO {
	
	private Integer idProcesso;
	private String  numeroPAT;
	
	private Integer idPessoaProcurador;
	
	private String tipoSujeito;
	private Integer idMovimentacaoProcuracao;
	
	private String cnpjCpf;
	private String nomeSujeito;	
	
	private String codigoStatusMovimentacaoProcuracao;
	private String descricaoStatusMovimentacaoProcuracao;	

	private Integer idProcuracao;
	private Date dataEmissaoProcuracao;
	private Date dataValidadeProcuracao;		
	private String codigoStatusProcuracao;	
	private String descricaoStatusProcuracao;
	
	private Integer idRenuncia;
	
	private String cpfProcurador;
	private String nomeProcurador;	
	
	private Integer idRevogacao;
	private Integer idSubstabelecimento;
	
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
	public String getCpfProcurador() {
		return cpfProcurador;
	}
	public void setCpfProcurador(String cpfProcurador) {
		this.cpfProcurador = cpfProcurador;
	}
	public String getNomeProcurador() {
		return nomeProcurador;
	}
	public void setNomeProcurador(String nomeProcurador) {
		this.nomeProcurador = nomeProcurador;
	}
	public Integer getIdRevogacao() {
		return idRevogacao;
	}
	public void setIdRevogacao(Integer idRevogacao) {
		this.idRevogacao = idRevogacao;
	}
	public Integer getIdSubstabelecimento() {
		return idSubstabelecimento;
	}
	public void setIdSubstabelecimento(Integer idSubstabelecimento) {
		this.idSubstabelecimento = idSubstabelecimento;
	}

}
