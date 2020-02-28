var PessoaJuridica = PessoaJuridica || {}

PessoaJuridica.PesquisaPessoa = (function() {

	function PesquisaPessoaJuridica() {
		initVariables.call(this);
	}

	PesquisaPessoaJuridica.prototype.enable = function() {
		registerEvents.call(this);
	}

	function initVariables() {
		this.cnpj = $('.numeroCnpj');
		this.btnLimparFormulario = $('[id^=consulta-pessoa-juridica]');
		this.actionUrl = this.btnLimparFormulario.data('action-url');
	}

	function registerEvents() {
		this.cnpj.on('blur', consultarPessoaJuridica.bind(this));
	}

	function consultarPessoaJuridica(event) {
		if (this.cnpj.val().replace(/[^0-9]/g, '').length == 14) {
			
			event.preventDefault();	

			$.ajax({
				url : this.actionUrl + "/" + this.cnpj.val().replace(/[^0-9]/g, ''),				
				method : 'POST', 
				beforeSend : onBeforeSend.bind(this),
				contentType : 'application/json',
				success : onPesquisaConcluida.bind(this),
				error : onErroPesquisa.bind(this)
			});
		}
		else{
			$('#idEscritorio').val('');
			$('#cnpjConsultado').val('');
			$('#nomeFantasiaEscritorio,#nomeEmpresarialEscritorio,#enderecoEscritorio,#idEnderecoEscritorio').val('');
		}
	}

	function onBeforeSend(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();

		xhr.setRequestHeader(header, token);
		
		ocultarAlertas();
		
		$('#mensagem-consulta-erro').hide();
		$('#mensagem-consulta-alerta').hide();
		
		$('#mensagem-carregamento').show();
		$('#mensagem-carregamento-texto').html('Consultando CNPJ, por favor aguarde...');
	}

	function onPesquisaConcluida(htmlResponse) {
				
		var container = $('.escritorioContainer');
		container.html(htmlResponse);

		new Inputmask().mask(container.find("input"));

		initVariables.call(this);
		registerEvents.call(this);
	//	new Procurador.ListaProcurador().enable();
		
		setTimeout(function(){
			$('#mensagem-carregamento').hide();		
			/**		if($('#idEscritorio').val().trim() == ''){
				$('#mensagem-consulta-alerta').show();
				$('#mensagem-consulta-alerta-texto').html('Escritório não encontrada em nossos cadastros. Por favor, corrija e tente novamente.');				 
			}**/
		}, 1000);
		
		if($('#idEscritorio').val().trim() == '')
			$(this.cnpj).val($('#cnpjConsultado').val());
		else
			$('#btn-endereco-escritorio').removeAttr('disabled');
	}
	
	function ocultarAlertas(){
		$('.js-sefazgo-messages-error').hide(); 
	}

	function limparFormulario() {

	}

	function onErroPesquisa(erro) {
		$('#mensagem-carregamento').hide();
		mensagem = (erro.responseJSON != undefined) ? erro.responseJSON : "Erro ao validar o cnpj."; 
		mensagemErro(mensagem);
		
	}

	function mensagemErro(mensagem){		
		$('#mensagem-consulta-erro').show();
		$('#mensagem-consulta-erro-texto').html(mensagem);
	}
	
	return PesquisaPessoaJuridica;

}());
/**
 * FIM DO BLOCO REFERENTE A PERSONALIZAÇÃO DO FRAGMENTO DE CONSULTA DE PESSOA
 */

$(function() {
	new PessoaJuridica.PesquisaPessoa().enable();
});