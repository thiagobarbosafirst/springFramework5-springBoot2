package br.gov.go.sefaz.pat.procuracao.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.gov.go.sefaz.javaee.corporativo.model.DocumentoPessoaId;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.TipoDocumento;
import br.gov.go.sefaz.javaee.repository.history.jpa.HistoryJpa;
import br.gov.go.sefaz.pat.model.DocumentoAdvogado;
import br.gov.go.sefaz.pat.procuracao.repository.jpa.DocumentoJpaRepository;

@Service
public class DocumentoService {
	
	@Autowired
	private DocumentoJpaRepository documentoJpaRepository;
	
	@HistoryJpa
	@Transactional
	public void save(String matriculaUsuarioHistorico, DocumentoAdvogado documentoPessoa) {
		documentoJpaRepository.save(documentoPessoa);
	}
	
	public DocumentoAdvogado consultar(DocumentoPessoaId documentoPessoaId){
		return this.documentoJpaRepository.findOne(documentoPessoaId);
	}	
	
	/**
	 * Buscar o documento do advogado (Carteira OAB). 
	 * Procura somente documentos do tipo 11 - Carteira Funcional. 
	 @param idPessoaAdvogado ID da pessoa relacionada ao advogado.
	*/
	public DocumentoAdvogado getDocumentoAdvogado(Integer idPessoaAdvogado) {
		DocumentoPessoaId documentoPessoaId = new DocumentoPessoaId();
		
		Pessoa pessoaDocumento = new Pessoa();
		pessoaDocumento.setIdPessoa(idPessoaAdvogado);
				
		TipoDocumento tipoDocumento = new TipoDocumento();
		tipoDocumento.setIdTipoDocumento(11);
		
		documentoPessoaId.setPessoa(pessoaDocumento);
		documentoPessoaId.setTipoDocumento(tipoDocumento);
		
		DocumentoAdvogado documentoAdvogado = this.consultar(documentoPessoaId);
		return documentoAdvogado;
	}
	
}
