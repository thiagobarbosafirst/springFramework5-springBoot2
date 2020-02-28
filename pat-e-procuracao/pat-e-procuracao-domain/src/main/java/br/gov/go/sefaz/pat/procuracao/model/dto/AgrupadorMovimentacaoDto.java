package br.gov.go.sefaz.pat.procuracao.model.dto; 

import java.util.Date;
import java.util.List;

import br.gov.go.sefaz.pat.enumerator.EnumIndiPendenteAssinatura;
import br.gov.go.sefaz.pat.procuracao.enumerator.EnumModalidadeEletronicaPresencial;

/** 
 * DTO utilizado na listagem de movimentações. 
*/
public class AgrupadorMovimentacaoDto {
	
	private Integer idProcesso;

	private String numeroPAT;
	private Date dataFormalizacao;
	private String statusProcesso;
	private String ritoProcessual;	
	
	private Integer idRenuncia;
	private Date dataRenuncia;	
	private String renunciaPendenteAssinaturaDigital;
	private String indiRenunciaPresencial;
		
	private Integer idRevogacao;
	private Date dataRevogacao;
	private Character indiPendenteAssinaturaAsChar;
	private Character indiModalidadeAsChar;
	
	private Integer idProcuracao;
	private Date dataEmissaoProcuracao;
	private Date dataValidadeProcuracao;
	private String poderProcuracao;
	private String descricaoPoderProcuracao;
	private String statusProcuracao;
	private String descricaoStatusProcuracao;
	
	private Integer idSubstabelecimento;
	private Date dataSubstabelecimento;
	private String statusSubstabelecimento;
	private String permiteSubstabelecimento;
	private Date dataValidadeSubstabelecimento;
	private Date dataLimiteSubstabelecimento;
	private String descricaoStatusSubstabelecimento;
	
	private Integer idPessoaProcurador;
	
	private List<MovimentacaoDto> movimentacoesDto;
	private boolean substabelecimentoValidado;
	
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

	public List<MovimentacaoDto> getMovimentacoesDto() {
		return movimentacoesDto;
	}

	public void setMovimentacoesDto(List<MovimentacaoDto> movimentacoesDto) {
		this.movimentacoesDto = movimentacoesDto;
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
	
	public String getIndiRenunciaPresencial() {
		return indiRenunciaPresencial;
	}

	public void setIndiRenunciaPresencial(String indiRenunciaPresencial) {
		this.indiRenunciaPresencial = indiRenunciaPresencial;
	}

	public Integer getIdRevogacao() {
		return idRevogacao;
	}
	public void setIdRevogacao(Integer idRevogacao) {
		this.idRevogacao = idRevogacao;
	}
	public Date getDataRevogacao() {
		return dataRevogacao;
	}
	public void setDataRevogacao(Date dataRevogacao) {
		this.dataRevogacao = dataRevogacao;
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

	public Character getIndiModalidadeAsChar() {
		return indiModalidadeAsChar;
	}
	
	public void setIndiModalidadeAsChar(Character indiModalidadeAsChar) {
		this.indiModalidadeAsChar = indiModalidadeAsChar;
	}
	
	public EnumModalidadeEletronicaPresencial getIndiModalidade(){
		return EnumModalidadeEletronicaPresencial.parse(getIndiModalidadeAsChar());
	}
	
	public void setIndiRevogacaoPresencial(EnumModalidadeEletronicaPresencial enumModalidadeEletronicaPresencial) {
		this.indiModalidadeAsChar = (enumModalidadeEletronicaPresencial != null ? enumModalidadeEletronicaPresencial.getCodigo() : null);
	}

	public String getRenunciaPendenteAssinaturaDigital() {
		return renunciaPendenteAssinaturaDigital;
	}

	public void setRenunciaPendenteAssinaturaDigital(String renunciaPendenteAssinaturaDigital) {
		this.renunciaPendenteAssinaturaDigital = renunciaPendenteAssinaturaDigital;
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
	
	public String getPoderProcuracao() {
		return poderProcuracao;
	}

	public void setPoderProcuracao(String poderProcuracao) {
		this.poderProcuracao = poderProcuracao;
	}

	public String getStatusProcuracao() {
		return statusProcuracao;
	}

	public void setStatusProcuracao(String statusProcuracao) {
		this.statusProcuracao = statusProcuracao;
	}

	public String getDescricaoStatusProcuracao() {
		return descricaoStatusProcuracao;
	}

	public void setDescricaoStatusProcuracao(String descricaoStatusProcuracao) {
		this.descricaoStatusProcuracao = descricaoStatusProcuracao;
	}

	public String getDescricaoPoderProcuracao() {
		return descricaoPoderProcuracao;
	}

	public void setDescricaoPoderProcuracao(String descricaoPoderProcuracao) {
		this.descricaoPoderProcuracao = descricaoPoderProcuracao;
	}

	public Integer getIdSubstabelecimento() {
		return idSubstabelecimento;
	}

	public void setIdSubstabelecimento(Integer idSubstabelecimento) {
		this.idSubstabelecimento = idSubstabelecimento;
	}

	public Date getDataSubstabelecimento() {
		return dataSubstabelecimento;
	}

	public void setDataSubstabelecimento(Date dataSubstabelecimento) {
		this.dataSubstabelecimento = dataSubstabelecimento;
	}

	public String getStatusSubstabelecimento() {
		return statusSubstabelecimento;
	}

	public void setStatusSubstabelecimento(String statusSubstabelecimento) {
		this.statusSubstabelecimento = statusSubstabelecimento;
	}

	public String getPermiteSubstabelecimento() {
		return permiteSubstabelecimento;
	}

	public void setPermiteSubstabelecimento(String permiteSubstabelecimento) {
		this.permiteSubstabelecimento = permiteSubstabelecimento;
	}

	public Date getDataValidadeSubstabelecimento() {
		return dataValidadeSubstabelecimento;
	}

	public void setDataValidadeSubstabelecimento(Date dataValidadeSubstabelecimento) {
		this.dataValidadeSubstabelecimento = dataValidadeSubstabelecimento;
	}

	public Date getDataLimiteSubstabelecimento() {
		return dataLimiteSubstabelecimento;
	}

	public void setDataLimiteSubstabelecimento(Date dataLimiteSubstabelecimento) {
		this.dataLimiteSubstabelecimento = dataLimiteSubstabelecimento;
	}

	public String getDescricaoStatusSubstabelecimento() {
		return descricaoStatusSubstabelecimento;
	}

	public void setDescricaoStatusSubstabelecimento(String descricaoStatusSubstabelecimento) {
		this.descricaoStatusSubstabelecimento = descricaoStatusSubstabelecimento;
	}

	public boolean isSubstabelecimentoValidado() {
		return substabelecimentoValidado;
	}

	public void setSubstabelecimentoValidado(boolean substabelecimentoValidado) {
		this.substabelecimentoValidado = substabelecimentoValidado;
	}

	public Integer getIdPessoaProcurador() {
		return idPessoaProcurador;
	}

	public void setIdPessoaProcurador(Integer idPessoaProcurador) {
		this.idPessoaProcurador = idPessoaProcurador;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}


}
