var ProcuracaoCadastro = ProcuracaoCadastro || {}

ProcuracaoCadastro.ResetarCadastro = (function() {

	function ResetarCadastro() {
		initVariables.call(this);
	}
	
	ResetarCadastro.prototype.enable = function() {
		registerEvents.call(this);
	}
	
	function initVariables() {
		this.btnLimpar = $('#limparFormulario');
		
		this.idPessoa = $('#idPessoa');
		this.idProcurador = $('#idProcurador');
		this.numeroCpf = $('#numeroCpf');
		this.cnpj = $('#numeroCnpj');
		
		this.limparFormulario = function() {
			limpar();		
		};
	}

	function registerEvents() {
		this.btnLimpar.on('click', limpar.bind());		
		$('button.close').click(function(){$(this).parents('.alert-dismissible').hide();});
	}
	
	function limpar(event) {
				
		var elements = $('.formProcuracao #container-dados-procurador :input')
			.not(':button, :submit, :reset, :hidden')
			.removeAttr('checked')
			.removeAttr('selected')
			.not(':checkbox, :radio').val('');
		
		//Clear hidden fields.
		$('#idPessoa,#idProcurador,#cpfConsultado').val('');
		$('#idEscritorio,#cnpjConsultado,#idEnderecoEscritorio').val('');
		$('#container-endereco-procurador :input[type="hidden"]').val('');
		
		$('#btn-endereco-escritorio').attr('disabled','disabled');
		
		//Retirar Alertas.
		$('#container-mensagens .alert').each(function(i,e){$(e).hide()});
		
		$('#formProcuracao #container-dados-procurador .form-group').each(function(i,e){$(e).removeClass('has-error')});
		$('#formProcuracao #container-dados-procurador .input-group').each(function(i,e){$(e).removeClass('has-error')});
	}

	return ResetarCadastro;

}());
/**
 * FIM DO BLOCO REFERENTE A PERSONALIZAÇÃO DO FRAGMENTO DE CONSULTA DE PESSOA
 */

$(function() {
	new ProcuracaoCadastro.ResetarCadastro().enable();
});