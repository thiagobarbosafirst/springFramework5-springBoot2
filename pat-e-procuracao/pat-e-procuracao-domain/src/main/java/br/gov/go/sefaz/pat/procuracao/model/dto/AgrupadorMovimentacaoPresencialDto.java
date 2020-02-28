package br.gov.go.sefaz.pat.procuracao.model.dto;

import java.util.List;

/** 
 @author Marcos Jr Lopez
*/
public class AgrupadorMovimentacaoPresencialDto {
	
	private Integer procurador;
	private Integer idSujeitoPassivo;
	private List<Integer> movimentacoes;
	private List<DocumentoMovimentacaoPresencialDto> files;
	
	public List<DocumentoMovimentacaoPresencialDto> getFiles() {
		return files;
	}
	public void setFiles(List<DocumentoMovimentacaoPresencialDto> files) {
		this.files = files;
	}
	
	public List<Integer> getMovimentacoes() {
		return movimentacoes;
	}
	public void setMovimentacoes(List<Integer> movimentacoes) {
		this.movimentacoes = movimentacoes;
	}
	public Integer getProcurador() {
		return procurador;
	}
	public void setProcurador(Integer procurador) {
		this.procurador = procurador;
	}
	public Integer getIdSujeitoPassivo() {
		return idSujeitoPassivo;
	}
	public void setIdSujeitoPassivo(Integer idSujeitoPassivo) {
		this.idSujeitoPassivo = idSujeitoPassivo;
	}
	
		
}
