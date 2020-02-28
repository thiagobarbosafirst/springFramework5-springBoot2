package br.gov.go.sefaz.pat.procuracao.repository.jdbc;

import java.util.List;

import br.gov.go.sefaz.pat.model.PecaRevogacaoProcuracao;

public interface PecaRevogacaoJdbcRepository {
	
	List<PecaRevogacaoProcuracao> findByRevogacaoProcuracaoId(Integer id);
}
