var PessoaFisica = PessoaFisica || {}

PessoaFisica.PesquisaPessoa = (function() {

	function PesquisaPessoaFisica() {
		initVariables.call(this);
	}

	PesquisaPessoaFisica.prototype.enable = function() {
		registerEvents.call(this);
	}

	function initVariables() {
		this.cpf = $('.numeroCpf');
		this.ultimoCPF = null;
		this.btnLimparFormularioPessoaFisica = $('[id^=consulta-pessoa-fisica]');
		this.actionUrl = this.btnLimparFormularioPessoaFisica.data('action-url');		
	}

	function registerEvents() {
		this.cpf.on('blur', consultarPessoaFisica.bind(this));
	}
	
	function mensagemErro(mensagem){		
		$('.js-sefazgo-messages-error').show();
		$('.js-sefazgo-messages-error-detail').html(mensagem);
	}

	function consultarPessoaFisica(event) {
		this.ultimoCPF = this.cpf.val();
		
		if (this.cpf.val().replace(/[^0-9]/g, '').length == 11) {
			
			event.preventDefault();
			
			$.ajax({
				url : this.actionUrl + "/" + this.cpf.val().replace(/[^0-9]/g, ''),
				method : 'POST', 
				beforeSend : onBeforeSend.bind(this),
				contentType : 'application/json',
				success : onPesquisaConcluida.bind(this),
				error : onErroPesquisa.bind(this)
			});
		}
	}

	function onBeforeSend(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();

		xhr.setRequestHeader(header, token);
		
		$('#mensagem-consulta-erro').hide();
		$('#mensagem-consulta-alerta').hide();
		
		ocultarAlertas();
		
		$('#mensagem-carregamento').show();
		$('#mensagem-carregamento-texto').html('Consultando CPF, por favor aguarde...');		
	}

	function onPesquisaConcluida(htmlResponse) {
		
		var container = $('.nomePessoaContainer');
		container.html(htmlResponse);
		// Para o caso de procuração presencial.
		new Procuracao.SelecionarTipoProcurador().enable();
		
		new Inputmask().mask(container.find("input"));

		initVariables.call(this);
		registerEvents.call(this);
		
		new PessoaJuridica.PesquisaPessoa().enable();
		new Procurador.ListaProcurador().enable();
		
		setTimeout(function(){
			$('#mensagem-carregamento').hide();
			
			if($('#idPessoa').val().trim() == ''){
		/**		$('#mensagem-consulta-alerta').show();
				$('#mensagem-consulta-alerta-texto').html('Pessoa não encontrada em nossos cadastros. Por favor, corrija e tente novamente.');
				$('.numeroCpf').parents('.form-group').first().addClass('has-error');
				**/
			}
		}, 1000);
		
		if($('#idPessoa').val().trim() == '')
			$(this.cpf).val($('#cpfConsultado').val());

		new ProcuracaoCadastro.ResetarCadastro().enable();  		

	}
	
	function ocultarAlertas(){
		$('.js-sefazgo-messages-error').hide(); 
	}
		
	function limparFormulario() {
		var elements = $('.formProcuracao #container-dados-procurador :input')
		.not(':button, :submit, :reset, :hidden')
		.removeAttr('checked')
		.removeAttr('selected')
		.not(':checkbox, :radio').val('');
	
	//Clear hidden fields.
	$('#idPessoa,#idProcurador').val('');
	$('#idEscritorio,#cnpjConsultado,#idEnderecoEscritorio').val('');
	$('#container-endereco-procurador :input[type="hidden"]').val('');
	
	$('#btn-endereco-escritorio').attr('disabled','disabled');
	
	//Retirar Alertas.
	$('#container-mensagens .alert').each(function(i,e){$(e).hide()});
	
	$('#formProcuracao #container-dados-procurador .form-group').each(function(i,e){$(e).removeClass('has-error')});  
	$('#formProcuracao #container-dados-procurador .input-group').each(function(i,e){$(e).removeClass('has-error')});
		
	}
	
	function onErroPesquisa(erro) {		
		$('#mensagem-carregamento').hide();	
		limparFormulario();
		mensagem = (erro.responseJSON != undefined) ? erro.responseJSON : "Erro ao validar o cpf."; 
		mensagemErro(mensagem);
	}

	return PesquisaPessoaFisica;

}());
/**
 * FIM DO BLOCO REFERENTE A PERSONALIZAÇÃO DO FRAGMENTO DE CONSULTA DE PESSOA
 */

$(function() {
	new PessoaFisica.PesquisaPessoa().enable();
});