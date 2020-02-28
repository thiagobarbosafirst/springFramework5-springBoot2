package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;


public class RevogacaoDetailsDto implements Serializable{

	private static final long serialVersionUID = -5335973442378180740L;
	
	private BigDecimal idMovmtProcuracao;
	private BigDecimal idProcessoAdmEletronico;
	private BigDecimal idRevogacao;
	private String nomeSujeitoPassivo;
	private String cnpjCpf;
	private String procurador;

	public BigDecimal getIdMovmtProcuracao() {
		return idMovmtProcuracao;
	}

	public void setIdMovmtProcuracao(BigDecimal idMovmtProcuracao) {
		this.idMovmtProcuracao =  (idMovmtProcuracao != null ? idMovmtProcuracao : null);
	}

	public BigDecimal getIdProcessoAdmEletronico() {
		return idProcessoAdmEletronico;
	}

	public void setIdProcessoAdmEletronico(BigDecimal idProcessoAdmEletronico) {
		this.idProcessoAdmEletronico = idProcessoAdmEletronico;
	}		
	
	public BigDecimal getIdRevogacao() {
		return idRevogacao;
	}

	public void setIdRevogacao(BigDecimal idRevogacao) {
		this.idRevogacao = idRevogacao;
	}

	public String getNomeSujeitoPassivo() {
		return nomeSujeitoPassivo;
	}

	public void setNomeSujeitoPassivo(String nomeSujeitoPassivo) {
		this.nomeSujeitoPassivo = nomeSujeitoPassivo;
	}

	public String getCnpjCpf() {
		return cnpjCpf;
	}

	public void setCnpjCpf(String cnpjCpf) {
		this.cnpjCpf = cnpjCpf;
	}

	public String getProcurador() {
		return procurador;
	}

	public void setProcurador(String procurador) {
		this.procurador = procurador;
	}

}
