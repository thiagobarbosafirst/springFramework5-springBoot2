package br.gov.go.sefaz.pat.procuracao.enumerator;

public enum EnumStatusProcuracao {
	
	Ativa("1", "Ativa"),
	Revogada("2", "Revogada"),
	Vencida("3", "Vencida"),
	Encerrada("4", "Encerrada"),
	RenunciaRepresentantes("5", "Inativa - Por renúncia de todos representantes"),
	PendenteAssinatura("6", "Pendente de Assinatura Digital");
	
	private final String codigo;
	private final String descricao;	
	
	private EnumStatusProcuracao(String codigo, String descricao) {
		this.codigo = codigo; 
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescricao() {
		return descricao;
	}
	
	
} 