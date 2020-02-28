package br.gov.go.sefaz.pat.procuracao.model.dto;

import br.gov.go.sefaz.javaee.corporativo.model.Endereco;
import br.gov.go.sefaz.javaee.corporativo.model.PessoaFisica;

public class ProcuradorDto {
	
	private Integer idProcurador;
	
	private String nome;
	private String cpf;
	private Integer idPessoa;
	
	private Integer tipoEmail;	
	private String emailPessoa;
	private Long idEmail;	
	
	private String endereco;
	private Integer idEndereco;
	private String enderecoFormatado;
	private Integer codgLogradouro;
	private String numero;
	private String quadra;
	private String lote;
	private String complemento;
	private String cep;
	
	private String tipoLogradouro;
	private String nomeLogradouro;
	private String bairroLogradouro;
	private String cidadeLogradouro;
	private String ufLogradouro;
	
	private Integer idEscritorio;
	private Integer idEnderecoEscritorio;
	private String numeroCnpjEscritorio;
	private String nomeFantasiaEscritorio;
	private String nomeEmpresarialEscritorio;
	private String regimeFiscal; 
	
	private PessoaFisica pessoaFisica;
	
	private String telefone;
	private Long idTelefone;
	private Integer tipoTelefone;
	
	private String ufAdvogado;
	private String numeroOAB;
	private Integer tipoDocumento;
	private Integer idDocumento;
	
	private Endereco enderecoEscritorio;
	
	private String enderecoEscritorioFormatado;
	private Integer codgLogradouroEscritorio;
	private String numeroEscritorio;
	private String quadraEscritorio;
	private String loteEscritorio;
	private String complementoEscritorio;
	private String cepEscritorio;
	
	private String tipoLogradouroEscritorio;
	private String nomeLogradouroEscritorio;
	private String bairroLogradouroEscritorio;
	private String cidadeLogradouroEscritorio;
	private String ufLogradouroEscritorio;
	private Character tipoMovimentacaoRepresentacao;
	
	public Character getTipoMovimentacaoRepresentacao() {
		return tipoMovimentacaoRepresentacao;
	}

	public void setTipoMovimentacaoRepresentacao(Character tipoMovimentacaoRepresentacao) {
		this.tipoMovimentacaoRepresentacao = tipoMovimentacaoRepresentacao;
	}

	private boolean cadastrarCpf;
	private boolean cadastrarCnpj;
		
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getCpf() {
		return cpf;
	}
	
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	
	public Endereco getEnderecoEscritorio() {
		return enderecoEscritorio;
	}
	
	public void setEnderecoEscritorio(Endereco enderecoEscritorio) {
		this.enderecoEscritorio = enderecoEscritorio;
	}	
	
	public PessoaFisica getPessoaFisica() {
		return pessoaFisica;
	}
	
	public void setPessoaFisica(PessoaFisica pessoaFisica) {
		this.pessoaFisica = pessoaFisica;
	}
	
	public String getEndereco() {
		return endereco;
	}
	
	public String getCep() {
		return cep;
	}

	public String getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(String tipoLogradouro) {
		this.tipoLogradouro = tipoLogradouro;
	}

	public String getNomeLogradouro() {
		return nomeLogradouro;
	}

	public void setNomeLogradouro(String nomeLogradouro) {
		this.nomeLogradouro = nomeLogradouro;
	}

	public String getCidadeLogradouro() {
		return cidadeLogradouro;
	}

	public void setCidadeLogradouro(String cidadeLogradouro) {
		this.cidadeLogradouro = cidadeLogradouro;
	}

	public String getUfLogradouro() {
		return ufLogradouro;
	}

	public void setUfLogradouro(String ufLogradouro) {
		this.ufLogradouro = ufLogradouro;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}	
	
	public String getEnderecoEscritorioFormatado() {
		return enderecoEscritorioFormatado;
	}
	
	public void setEnderecoEscritorioFormatado(String enderecoEscritorioFormatado) {
		this.enderecoEscritorioFormatado = enderecoEscritorioFormatado;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getUfAdvogado() {
		return ufAdvogado;
	}
	
	public void setUfAdvogado(String ufAdvogado) {
		this.ufAdvogado = ufAdvogado;
	}
	
	public String getNumeroOAB() {
		return numeroOAB;
	}
	
	public void setNumeroOAB(String numeroOAB) {
		this.numeroOAB = numeroOAB;
	}

	public void setEmailPessoa(String emailPessoa) {
		this.emailPessoa = emailPessoa;
	}

	public String getEmailPessoa() {
		return emailPessoa;
	}

	public Integer getIdEndereco() {
		return idEndereco;
	}

	public void setIdEndereco(Integer idEndereco) {
		this.idEndereco = idEndereco;
	}

	public Integer getIdPessoa() {
		return idPessoa;
	}

	public void setIdPessoa(Integer idPessoa) {
		this.idPessoa = idPessoa;
	}

	public Integer getTipoEmail() {
		return tipoEmail;
	}

	public void setTipoEmail(Integer tipoEmail) {
		this.tipoEmail = tipoEmail;
	}

	public Integer getIdEscritorio() {
		return idEscritorio;
	}

	public void setIdEscritorio(Integer idEscritorio) {
		this.idEscritorio = idEscritorio;
	}

	public Integer getIdEnderecoEscritorio() {
		return idEnderecoEscritorio;
	}

	public void setIdEnderecoEscritorio(Integer idEnderecoEscritorio) {
		this.idEnderecoEscritorio = idEnderecoEscritorio;
	}

	public Integer getIdProcurador() {
		return idProcurador;
	}

	public void setIdProcurador(Integer idProcurador) {
		this.idProcurador = idProcurador;
	}

	public Long getIdEmail() {
		return idEmail;
	}

	public void setIdEmail(Long idEmail) {
		this.idEmail = idEmail;
	}

	public Long getIdTelefone() {
		return idTelefone;
	}

	public void setIdTelefone(Long idTelefone) {
		this.idTelefone = idTelefone;
	}

	public Integer getTipoTelefone() {
		return tipoTelefone;
	}

	public void setTipoTelefone(Integer tipoTelefone) {
		this.tipoTelefone = tipoTelefone;
	}

	public Integer getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(Integer tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Integer getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(Integer idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getEnderecoFormatado() {
		return enderecoFormatado;
	}

	public void setEnderecoFormatado(String enderecoFormatado) {
		this.enderecoFormatado = enderecoFormatado;
	}

	public Integer getCodgLogradouro() {
		return codgLogradouro;
	}

	public void setCodgLogradouro(Integer codgLogradouro) {
		this.codgLogradouro = codgLogradouro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getQuadra() {
		return quadra;
	}

	public void setQuadra(String quadra) {
		this.quadra = quadra;
	}

	public String getLote() {
		return lote;
	}

	public void setLote(String lote) {
		this.lote = lote;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public Integer getCodgLogradouroEscritorio() {
		return codgLogradouroEscritorio;
	}

	public void setCodgLogradouroEscritorio(Integer codgLogradouroEscritorio) {
		this.codgLogradouroEscritorio = codgLogradouroEscritorio;
	}

	public String getNumeroEscritorio() {
		return numeroEscritorio;
	}

	public void setNumeroEscritorio(String numeroEscritorio) {
		this.numeroEscritorio = numeroEscritorio;
	}

	public String getQuadraEscritorio() {
		return quadraEscritorio;
	}

	public void setQuadraEscritorio(String quadraEscritorio) {
		this.quadraEscritorio = quadraEscritorio;
	}

	public String getLoteEscritorio() {
		return loteEscritorio;
	}

	public void setLoteEscritorio(String loteEscritorio) {
		this.loteEscritorio = loteEscritorio;
	}

	public String getComplementoEscritorio() {
		return complementoEscritorio;
	}

	public void setComplementoEscritorio(String complementoEscritorio) {
		this.complementoEscritorio = complementoEscritorio;
	}

	public String getCepEscritorio() {
		return cepEscritorio;
	}

	public void setCepEscritorio(String cepEscritorio) {
		this.cepEscritorio = cepEscritorio;
	}

	public String getBairroLogradouro() {
		return bairroLogradouro;
	}

	public String getTipoLogradouroEscritorio() {
		return tipoLogradouroEscritorio;
	}

	public void setTipoLogradouroEscritorio(String tipoLogradouroEscritorio) {
		this.tipoLogradouroEscritorio = tipoLogradouroEscritorio;
	}

	public String getNomeLogradouroEscritorio() {
		return nomeLogradouroEscritorio;
	}

	public void setNomeLogradouroEscritorio(String nomeLogradouroEscritorio) {
		this.nomeLogradouroEscritorio = nomeLogradouroEscritorio;
	}

	public String getBairroLogradouroEscritorio() {
		return bairroLogradouroEscritorio;
	}

	public void setBairroLogradouroEscritorio(String bairroLogradouroEscritorio) {
		this.bairroLogradouroEscritorio = bairroLogradouroEscritorio;
	}

	public String getCidadeLogradouroEscritorio() {
		return cidadeLogradouroEscritorio;
	}

	public void setCidadeLogradouroEscritorio(String cidadeLogradouroEscritorio) {
		this.cidadeLogradouroEscritorio = cidadeLogradouroEscritorio;
	}

	public String getUfLogradouroEscritorio() {
		return ufLogradouroEscritorio;
	}

	public void setUfLogradouroEscritorio(String ufLogradouroEscritorio) {
		this.ufLogradouroEscritorio = ufLogradouroEscritorio;
	}

	public void setBairroLogradouro(String bairroLogradouro) {
		this.bairroLogradouro = bairroLogradouro;
	}

	public String getNumeroCnpjEscritorio() {
		return numeroCnpjEscritorio;
	}

	public void setNumeroCnpjEscritorio(String numeroCnpjEscritorio) {
		this.numeroCnpjEscritorio = numeroCnpjEscritorio;
	}

	public String getNomeFantasiaEscritorio() {
		return nomeFantasiaEscritorio;
	}

	public void setNomeFantasiaEscritorio(String nomeFantasiaEscritorio) {
		this.nomeFantasiaEscritorio = nomeFantasiaEscritorio;
	}

	public String getNomeEmpresarialEscritorio() {
		return nomeEmpresarialEscritorio;
	}

	public void setNomeEmpresarialEscritorio(String nomeEmpresarialEscritorio) {
		this.nomeEmpresarialEscritorio = nomeEmpresarialEscritorio;
	}

	public String getRegimeFiscal() {
		return regimeFiscal;
	}

	public void setRegimeFiscal(String regimeFiscal) {
		this.regimeFiscal = regimeFiscal;
	}

	public boolean isCadastrarCpf() {
		return cadastrarCpf;
	}

	public void setCadastrarCpf(boolean cadastrarCpf) {
		this.cadastrarCpf = cadastrarCpf;
	}

	public boolean isCadastrarCnpj() {
		return cadastrarCnpj;
	}

	public void setCadastrarCnpj(boolean cadastrarCnpj) {
		this.cadastrarCnpj = cadastrarCnpj;
	}
	
}
