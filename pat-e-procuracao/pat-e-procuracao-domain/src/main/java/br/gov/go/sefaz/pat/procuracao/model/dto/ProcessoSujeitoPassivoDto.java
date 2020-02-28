package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.Date;
import java.util.List;

public class ProcessoSujeitoPassivoDto {
	
	private Integer id;
	
	private Integer idSujeitoPassivo;

	private String descCapitulacao;

	private Byte descFatoSujeicaoPassiva;

	private String descEnvioCapitulacao;

	private String tipo;

	private Integer idProcessoAdmEletronico;

	private String indicadorCreditoTributarioIntegral;
	
	private String cpfCnpj;
	
	private String nomeSujeitoPassivo;

	private String numeroProcesso;
	
	private boolean selecaoProcesso;
	
	private List<SujeitoPassivoDto> listaSujeitoPassivo;
	
	private int qteSujeitoPassivo;
	
	private String estiloLinha;
	
	private Date dataFormalizacao;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id; 
	}

	public String getDescCapitulacao() {
		return descCapitulacao;
	}

	public void setDescCapitulacao(String descCapitulacao) {
		this.descCapitulacao = descCapitulacao;
	}

	public Byte getDescFatoSujeicaoPassiva() {
		return descFatoSujeicaoPassiva;
	}

	public void setDescFatoSujeicaoPassiva(Byte descFatoSujeicaoPassiva) {
		this.descFatoSujeicaoPassiva = descFatoSujeicaoPassiva;
	}

	public String getDescEnvioCapitulacao() {
		return descEnvioCapitulacao;
	}

	public void setDescEnvioCapitulacao(String descEnvioCapitulacao) {
		this.descEnvioCapitulacao = descEnvioCapitulacao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getIdProcessoAdmEletronico() {
		return idProcessoAdmEletronico;
	}

	public void setIdProcessoAdmEletronico(Integer idProcessoAdmEletronico) {
		this.idProcessoAdmEletronico = idProcessoAdmEletronico;
	}

	public String getIndicadorCreditoTributarioIntegral() {
		return indicadorCreditoTributarioIntegral;
	}

	public void setIndicadorCreditoTributarioIntegral(String indicadorCreditoTributarioIntegral) {
		this.indicadorCreditoTributarioIntegral = indicadorCreditoTributarioIntegral;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getNomeSujeitoPassivo() {
		return nomeSujeitoPassivo;
	}

	public void setNomeSujeitoPassivo(String nomeSujeitoPassivo) {
		this.nomeSujeitoPassivo = nomeSujeitoPassivo;
	}

	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}

	public boolean isSelecaoProcesso() {
		return selecaoProcesso;
	}

	public void setSelecaoProcesso(boolean selecaoProcesso) {
		this.selecaoProcesso = selecaoProcesso;
	}

	public List<SujeitoPassivoDto> getListaSujeitoPassivo() {
		return listaSujeitoPassivo;
	}

	public void setListaSujeitoPassivo(List<SujeitoPassivoDto> listaSujeitoPassivo) {
		this.listaSujeitoPassivo = listaSujeitoPassivo;
	}

	public int getQteSujeitoPassivo() {
		return qteSujeitoPassivo;
	}

	public void setQteSujeitoPassivo(int qteSujeitoPassivo) {
		this.qteSujeitoPassivo = qteSujeitoPassivo;
	}

	public String getEstiloLinha() {
		return estiloLinha;
	}

	public void setEstiloLinha(String estiloLinha) {
		this.estiloLinha = estiloLinha;
	}

	public Integer getIdSujeitoPassivo() {
		return idSujeitoPassivo;
	}

	public void setIdSujeitoPassivo(Integer idSujeitoPassivo) {
		this.idSujeitoPassivo = idSujeitoPassivo;
	}

	public Date getDataFormalizacao() {
		return dataFormalizacao;
	}

	public void setDataFormalizacao(Date dataFormalizacao) {
		this.dataFormalizacao = dataFormalizacao;
	}
}
