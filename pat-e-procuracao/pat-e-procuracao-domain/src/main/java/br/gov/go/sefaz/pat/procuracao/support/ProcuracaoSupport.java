package br.gov.go.sefaz.pat.procuracao.support;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import br.gov.go.sefaz.javaee.commons.exception.SefazException;
import br.gov.go.sefaz.javaee.corporativo.model.Pessoa;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaJuridica;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaFisicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaJuridicaService;
import br.gov.go.sefaz.javaee.corporativo.service.PessoaService;
import br.gov.go.sefaz.pat.filter.ProcessSearchFilter;
import br.gov.go.sefaz.pat.procuracao.model.dto.AgrupadorMovimentacaoPresencialDto;
import br.gov.go.sefaz.pat.procuracao.service.RevogacaoService;
import br.gov.go.sefaz.pat.support.formatting.FormatSupport;

@Component
public class ProcuracaoSupport {
	
	private static Logger logger = LogManager.getLogger(RevogacaoService.class);
	
	@Autowired
	private PessoaService pessoaService;

	@Autowired
	private PessoaJuridicaService pessoaJuridicaService;
	
	@Autowired
	private PessoaFisicaService pessoaFisicaService;
	
	private FormatSupport formatSupport = new FormatSupport();
				
	public Character consultarTipoPessoa(Integer idPessoa) {		
		Pessoa pessoa = pessoaService.consultar(idPessoa);	
		return pessoa.getTipoPessoa();
	}
	
	public PessoaFisica consultarPessoFisica(Integer idPessoa){
			PessoaFisica pessoaFisica = pessoaFisicaService.consultarPorId(idPessoa);			
		return pessoaFisica;
	}
	
	public PessoaJuridica consultarPessoaJuridica(Integer idPessoa){
		PessoaJuridica pessoaJuridica = pessoaJuridicaService.consultarPorId(idPessoa); 
		return pessoaJuridica;
	}
	
	public Integer consultarPessoFisicaCpf(String cpf){
		PessoaFisica pessoaFisica = pessoaFisicaService.consultarPorCpf(cpf);			
	return pessoaFisica.getIdPessoa();
	}

	public Integer consultarPessoaJuridicaCnpj(String cnpj){
		PessoaJuridica pessoaJuridica = pessoaJuridicaService.consultarPorCnpj(cnpj); 
		return pessoaJuridica.getIdPessoa();
	}	
	
	public Integer consultaIdPessoaSujeitoPassivo(ProcessSearchFilter pesquisa) throws SefazException {
		Integer idPessoa = null;
		try {
			if(!pesquisa.getTexto().isEmpty() && pesquisa.getTexto().length() == 14) {
				idPessoa = pessoaJuridicaService.consultarPorCnpj(pesquisa.getTexto()).getIdPessoa();													
			}
			else {
				idPessoa = pessoaFisicaService.consultarPorCpf(pesquisa.getTexto()).getIdPessoa();
			}
		} catch (Exception e) {
			logger.error("Não encontramos registros no nosso banco de dados com esse CPF/CNPJ." , e);
			return idPessoa;
		}
		return idPessoa;
	}
	
	public String filtroRedirectPessoaFisicaJuridica(AgrupadorMovimentacaoPresencialDto agrupadorMovimentacaoPresencialDto, String redirect) throws SefazException {		
		try {
			Pessoa pessoa = pessoaService.consultar(agrupadorMovimentacaoPresencialDto.getIdSujeitoPassivo());
			
			if(pessoa.getTipoPessoa() == 'J'){
				PessoaJuridica pessoaJuridica = pessoaJuridicaService.consultarPorId(pessoa.getIdPessoa());
				redirect = redirect + "?opcao=cnpj&texto=" + pessoaJuridica.getNumeroCnpj();
			}else {
				PessoaFisica  pessoaFisica = pessoaFisicaService.consultarPorId(pessoa.getIdPessoa());
				redirect = redirect + "?opcao=cpf&texto=" + pessoaFisica.getNumeroCpf();
			}
			return redirect;			
		} catch (Exception e) {
			logger.error(e);
			throw new SefazException("Ocorreu algum problema ao fazer o redirect. " + e.getMessage(), e);
		}
	}
	
	public String filtroUsuarioLogado(Integer idPessoa){
		String cnpjMF 		  = ", CNPJ/MF sob nº ";
		String cpfMF 		  = ", CPF/MF sob nº ";
		String usuarioLogado  = "";
		Pessoa pessoa = pessoaService.consultar(idPessoa);
		
		if(pessoa.getTipoPessoa() == 'F'){
			PessoaFisica pessoaFisica = pessoaFisicaService.consultarPorId(idPessoa); 
			usuarioLogado = pessoaFisica.getNome() + cpfMF + formatSupport.formatarCPF(pessoaFisica.getNumeroCpf());				
		}
		else{
			PessoaJuridica pessoaJuridica = pessoaJuridicaService.consultarPorId(idPessoa);
			usuarioLogado = pessoaJuridica.getNomeFantasia() + cnpjMF + formatSupport.formatarCNPJ(pessoaJuridica.getNumeroCnpj());				
		}
		
		return usuarioLogado;
	}
}
