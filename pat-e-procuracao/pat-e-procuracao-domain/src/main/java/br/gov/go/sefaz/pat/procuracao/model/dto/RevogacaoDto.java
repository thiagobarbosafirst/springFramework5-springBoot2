package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import br.gov.go.sefaz.pat.enumerator.EnumIndiPendenteAssinatura;

public class RevogacaoDto implements Serializable{
	
	private static final long serialVersionUID = 4402287555872147921L;
	
	private BigDecimal idProcessoAdmEletronico;
	private String numeroPAT;
	private Date dataFormalizacao;
	
	private BigDecimal idRevogacao;
	private Date dataRevogacao;
	private Character indiPendenteAssinaturaAsChar;
	
	private List<RevogacaoDetailsDto> revogacaoDetailsDto;		
	
	public BigDecimal getIdProcessoAdmEletronico() {
		return idProcessoAdmEletronico;
	}

	public void setIdProcessoAdmEletronico(BigDecimal idProcessoAdmEletronico) {
		this.idProcessoAdmEletronico = idProcessoAdmEletronico;
	}

	public String getNumeroPAT() {
		return numeroPAT;
	}

	public void setNumeroPAT(String numeroPAT) {
		this.numeroPAT = numeroPAT;
	}

	public Date getDataFormalizacao() {
		return dataFormalizacao;
	}

	public void setDataFormalizacao(Date dataFormalizacao) {
		this.dataFormalizacao = dataFormalizacao;
	}		

	public BigDecimal getIdRevogacao() {
		return idRevogacao;
	}

	public void setIdRevogacao(BigDecimal idRevogacao) {
		this.idRevogacao = (idRevogacao != null ? idRevogacao : null);
	}

	public Date getDataRevogacao() {
		return dataRevogacao;
	}

	public void setDataRevogacao(Date dataRevogacao) {
		this.dataRevogacao = (dataRevogacao != null ? dataRevogacao : null);
	}

	public Character getIndiPendenteAssinaturaAsChar() {
		return indiPendenteAssinaturaAsChar;
	}

	public void setIndiPendenteAssinaturaAsChar(Character indiPendenteAssinaturaAsChar) {
		this.indiPendenteAssinaturaAsChar = indiPendenteAssinaturaAsChar;
	}
	
	public EnumIndiPendenteAssinatura getIndiPendenteAssinatura(){
		return EnumIndiPendenteAssinatura.parse(getIndiPendenteAssinaturaAsChar());
	}
	
	public void setIndiPendenteAssinatura(EnumIndiPendenteAssinatura enumIndiPendenteAssinatura) {
		this.indiPendenteAssinaturaAsChar = (enumIndiPendenteAssinatura != null ? enumIndiPendenteAssinatura.getValor() : null);
	}

	public List<RevogacaoDetailsDto> getRevogacaoDetailsDto() {
		return revogacaoDetailsDto;
	}

	public void setRevogacaoDetailsDto(List<RevogacaoDetailsDto> revogacaoDetailsDto) {
		this.revogacaoDetailsDto = revogacaoDetailsDto;
	}		
}
