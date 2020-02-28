package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.Date;
import java.util.List;

/** 
 * DTO utilizado na listagem dos processos com seus respectivos sujeitos passivos outorgantes. 
 @author Marcos Jr Lopez
*/
public class AgrupadorRenunciaDTO {
	
	private Integer idProcesso;

	private String numeroPAT;
	private Date dataFormalizacao;
	private String statusProcesso;
	private String ritoProcessual;	
	
	private Integer idRenuncia;
	private Date dataRenuncia;	
	private String renunciaPendenteAssinaturaDigital;
	private String indiRenunciaPresencial;
		
	private List<RenunciaSujeitoDTO> sujeitosPassivos;

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

	public Date getDataFormalizacao() {
		return dataFormalizacao;
	}

	public void setDataFormalizacao(Date dataFormalizacao) {
		this.dataFormalizacao = dataFormalizacao;
	}

	public String getStatusProcesso() {
		return statusProcesso;
	}

	public void setStatusProcesso(String statusProcesso) {
		this.statusProcesso = statusProcesso;
	}

	public String getRitoProcessual() {
		return ritoProcessual;
	}

	public void setRitoProcessual(String ritoProcessual) {
		this.ritoProcessual = ritoProcessual;
	}

	public List<RenunciaSujeitoDTO> getSujeitosPassivos() {
		return sujeitosPassivos;
	}

	public void setSujeitosPassivos(List<RenunciaSujeitoDTO> sujeitosPassivos) {
		this.sujeitosPassivos = sujeitosPassivos;
	}

	public Integer getIdRenuncia() {
		return idRenuncia;
	}

	public void setIdRenuncia(Integer idRenuncia) {
		this.idRenuncia = idRenuncia;
	}

	public Date getDataRenuncia() {
		return dataRenuncia;
	}

	public void setDataRenuncia(Date dataRenuncia) {
		this.dataRenuncia = dataRenuncia;
	}

	public String getRenunciaPendenteAssinaturaDigital() {
		return renunciaPendenteAssinaturaDigital;
	}

	public void setRenunciaPendenteAssinaturaDigital(String renunciaPendenteAssinaturaDigital) {
		this.renunciaPendenteAssinaturaDigital = renunciaPendenteAssinaturaDigital;
	}

	public String getIndiRenunciaPresencial() {
		return indiRenunciaPresencial;
	}

	public void setIndiRenunciaPresencial(String indiRenunciaPresencial) {
		this.indiRenunciaPresencial = indiRenunciaPresencial;
	}
		
}
