package br.gov.go.sefaz.pat.procuracao.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;

@Component
public class DadosUsuarioSupport {
	
	@Autowired
	private PessoaService pessoaService;

	private FormatSupport formatSupport = new FormatSupport();
				
	public String filtroUsuarioLogado(Integer idPessoa){
		String cnpjMF 		  = ", CNPJ/MF sob nº ";
		String cpfMF 		  = ", CPF/MF sob nº ";
		String usuarioLogado  = "";
		Pessoa pessoa = pessoaService.consultar(idPessoa);
		
		if(pessoa.getPessoaFisica() != null){
			usuarioLogado = pessoa.getPessoaFisica().getNome() + cpfMF + formatSupport.formatarCPF(pessoa.getPessoaFisica().getNumeroCpf());				
		}
		else{
			usuarioLogado = pessoa.getPessoaJuridica().getNomeFantasia() + cnpjMF + formatSupport.formatarCNPJ(pessoa.getPessoaJuridica().getNumeroCnpj());				
		}
		
		return usuarioLogado;
	}
}
