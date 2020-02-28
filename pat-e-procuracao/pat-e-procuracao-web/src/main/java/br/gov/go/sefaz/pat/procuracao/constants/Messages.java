package br.gov.go.sefaz.pat.procuracao.constants;

public class Messages {
	
	//Errors
		public static final String LOGIN_ERROR_UNHANDLED = "Desculpe, ocorreu um erro não tratado durante sua autenticação.";
		public static final String ACCESS_DENIED = "Desculpe, parece que não tem permissão para acessar esta página: %s";
		public static final String PAGE_NOT_FOUND = "Desculpe, parece que você está tentando acessar uma página não encontrada: %s";
		
		//Save Operation (Entity 'ID - Name')
		public static final String SUCCESSFULLY_SAVED = "%s '%s - %s' salvo com sucesso!";
		public static final String SUCCESSFULLY_SAVED_PADRAO = "Operação realizada com sucesso!";
		
		//Delete Operation (Entity 'ID')
		public static final String SUCCESSFULLY_DELETED = "%s '%s' deletado com sucesso!";
		public static final String SUCCESSFULLY_DELETED_PADRAO = "Registro deletado com sucesso!";
	
}
