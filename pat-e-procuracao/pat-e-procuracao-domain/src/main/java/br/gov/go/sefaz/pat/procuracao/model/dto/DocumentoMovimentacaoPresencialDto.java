package br.gov.go.sefaz.pat.procuracao.model.dto;

import org.springframework.web.multipart.MultipartFile;

/** 
 * DTO utilizado na listagem dos processos com seus respectivos sujeitos passivos outorgantes. 
 @author Marcos Jr Lopez
*/
public class DocumentoMovimentacaoPresencialDto {
	
	private MultipartFile documento;
	private Integer modeloDocumento;
	
	public Integer getModeloDocumento() {
		return modeloDocumento;
	}

	public void setModeloDocumento(Integer modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}
	
	public MultipartFile getDocumento() {
		return documento;
	}
	public void setDocumento(MultipartFile documento) {
		this.documento = documento;
	}

}
