package br.gov.go.sefaz.pat.procuracao.repository.jdbc;

import java.util.List;

import br.gov.go.sefaz.pat.procuracao.model.dto.ProcuracaoDto;

public interface ProcuracaoJdbcRepository {
	
	List<ProcuracaoDto> listarProcuracaoPorProcurador(Integer idPessoa);
	List<ProcuracaoDto> listarProcuracaoPorSujeitoPassivo(Integer idPessoa);
	List<ProcuracaoDto> listarProcuracaoPorSujeitoPassivo(String numrCnpjBase);
	public boolean verificarProcuracao(Integer idSujeitoPassivo, String status);

}
