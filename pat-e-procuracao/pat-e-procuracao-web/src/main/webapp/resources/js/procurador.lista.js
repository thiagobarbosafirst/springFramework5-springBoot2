var Procurador = Procurador || {}

Procurador.ListaProcurador = (function() {

	function PesquisaProcurador() {
		initVariables.call(this);
	}

	PesquisaProcurador.prototype.enable = function() {
		registerEvents.call(this);
	}

	function initVariables() {
		this.cpf = $('.numeroCpf');
		this.nomeProcurador = $('#nomeProcurador');
		this.idPessoa = $('#idPessoa');
		
		this.enderecoFormatado = $('#enderecoFormatado');
		this.codgLogradouro = $('#codgLogradouro');
		this.numero = $('#numero');
		this.quadra = $('#quadra');
		this.lote = $('#lote');
		this.complemento = $('#complemento');
		
		
		this.idEmail = $('#idEmail');
		this.emailPessoa = $('#emailPessoa');
		this.tipoEmail = $('#tipoEmail');
		
		this.idTelefone = $('#idTelefone');
		this.telefonePessoa = $('#telefone');
		this.tipoTelefone = $('#tipoTelefone');
		
		this.listaProcuradores = $('#procuradores'); 
		this.idEscritorio = $('#idEscritorio');
		this.numeroCnpjEscritorio = $('#numeroCnpjEscritorio');
		this.nomeFantasiaEscritorio = $('#nomeFantasiaEscritorio');
		this.nomeEmpresarialEscritorio = $('#nomeEmpresarialEscritorio'); 
		this.regimeFiscal = $('#regimeFiscal');
		
		this.idEnderecoEscritorio = $('#idEnderecoEscritorio');
		
		this.numeroOAB = $('#numeroOAB');
		this.ufAdvogado = $('#ufAdvogado');
		this.idDocumento = $('#idDocumento');
		this.tipoDocumento = $('#tipoDocumento');
		
		this.idProcurador = $('#idProcurador');
		
		
		this.btnAddProcurador = $('[id^=btn-incluir-procurador]');
		this.btnRemoveProcurador = $('[id^=btn-remover-procurador]');
		this.actionUrl = this.btnAddProcurador.data('action-url');
	}

	function registerEvents() {
		this.btnAddProcurador.on('click', addProcurador.bind(this));
		this.btnRemoveProcurador.on('click', removeProcurador.bind(this));
		this.ufAdvogado.on('change', removerIndicadorErro.bind(this));
		this.numeroOAB.on('change', removerIndicadorErro.bind(this));
		controlarListaProcuradorSubstabelecimento(); 
	}
	
	function removerIndicadorErro(event){
		var elemento = $(event.currentTarget);
		
		if(elemento.val() != undefined && elemento.val().trim() != ''){
			elemento.parents('.form-group').first().removeClass('has-error');
			elemento.parents('.input-group').first().removeClass('has-error');
			ocultarAlertas();
		}	
	}
	
	function controlarListaProcuradorSubstabelecimento() {
		
		if($('.procuradores').size() == 0) {
			$('#divListaProcuradorContainer').hide();
		//	$('#formSubstabelecimento').hide();
		} else {
			$('#divListaProcuradorContainer').show();
		//	$('#formSubstabelecimento').show();			
		}
		
	}
	
	function closest(el, cssClass) {
	    while (el = el.parentNode){
	        if (el.classList.contains(cssClass)) return el;
	    }
	    return false;
	}
	
	function deleteRowOfProductTable(productID){
	    $('#YourTableId tr').each(function (i, row) {
	        var $row = $(row);
	        var productLabelId =  $row.find('label[name*="Product_' + productID + '"]');

	       var $productLabelIdValue = $productLabelId.text();
	       if (parseInt(productID) == parseInt($productLabelIdValue))
	       {
	           $row.remove(); 
	       }
	    });
	 }
	
	function removeProcurador(event) {

		 var procuradores = closest(event.target, 'procuradores');
		 procuradores.parentNode.removeChild(procuradores);	
		 controlarListaProcuradorSubstabelecimento();
	}
	
	function validarProcurador(context){		
		
		if(context.cpf.val().trim() == ''){
			context.cpf.parents('.form-group').first().addClass('has-error');
			context.cpf.focus();
			mensagemErro('O CPF está inválido. Ele precisa ser informado para cadastrar o procurador.');
			return false;
		}
		
//		if(context.idPessoa.val().trim() == ''){
//			context.cpf.parents('.form-group').first().addClass('has-error');
//			context.cpf.focus();
//			mensagemErro('O CPF não foi encontrado. Ele precisa ser informado para cadastrar o procurador.');
//			return false;
//		}
		
		if(context.codgLogradouro.val().trim() == ''){
			context.enderecoFormatado.parents('.input-group').first().addClass('has-error');			
			mensagemErro('Deve-se cadastrar o Endereço do procurador.');
			return false;
		}
		
		if(context.ufAdvogado.val().trim() == '' && $('#tipoProcuradorToggle').prop('checked')){
			context.ufAdvogado.parents('.form-group').first().addClass('has-error');
			context.ufAdvogado.focus();
			mensagemErro('Deve-se cadastrar a Seccional da Carteira Funcional.');
			return false;
		}
		
		if( context.numeroOAB.val().trim() == '' && $('#tipoProcuradorToggle').prop('checked')){
			context.numeroOAB.parents('.form-group').first().addClass('has-error');	
			context.numeroOAB.focus();
			mensagemErro('Deve-se cadastrar o Número da Carteira Funcional.');
			return false;
		}		
		
		if(duplicidadeCpf(context.cpf.val().trim())){
			context.cpf.parents('.form-group').first().addClass('has-error');			
			mensagemErro('O CPF informado já existe nesta procuração. Confira a lista de procuradores adicionados.');
			return false;
		}
		return true;
	}
	
	function duplicidadeCpf(numeroCpf){
		var duplicidade = false;
		
		$('.procuradores .cpfProcuradorLista').each(function(index,elemento){
			if($(elemento).val().trim() == numeroCpf)
				duplicidade = true;
		});
		
		return duplicidade;
	}
	
	function mensagemErro(mensagem){		
//		$('#mensagem-consulta-erro').show();
//		$('#mensagem-consulta-erro-texto').html(mensagem);	
		$('.js-sefazgo-messages-error').show();
		$('.js-sefazgo-messages-error-detail').html(mensagem);
	}
	
	function ocultarAlertas(){
		$('#errorMessage').hide();
		$('.js-sefazgo-messages-error').hide(); 
	}
	
	function addProcurador(event) {
		
			event.preventDefault();
			
			if(!validarProcurador(this))
				return;
			
			var procuradores = new Array();			
						
			$('.procuradores').each(function(index){
				
				var procurador = {
						
						cpf : $( this ).find('.cpfProcuradorLista').val(),
						nome : $( this ).find('.nomeProcuradorLista').val(),
						idProcurador : $( this ).find('.idProcuradorProcuradorLista').val(),
						idPessoa : $( this ).find('.idPessoaProcuradorLista').val(),
						
						enderecoFormatado : $( this ).find('.enderecoFormatadoLista').val(),
						codgLogradouro : $( this ).find('.codgLogradouroLista').val(),
						numero : $( this ).find('.numeroLista').val(),
						quadra : $( this ).find('.quadraLista').val(),
						lote : $( this ).find('.loteLista').val(),
						complemento : $( this ).find('.complementoLista').val(),
						
						idEscritorio : $( this ).find('.idEscritorioProcuradorLista').val(),
						numeroCnpjEscritorio : $( this ).find('.numeroCnpjEscritorioLista').val(),
						nomeFantasiaEscritorio : $( this ).find('.nomeFantasiaEscritorioLista').val(),	
						nomeEmpresarialEscritorio : $( this ).find('.nomeEmpresarialEscritorioLista').val(),
						regimeFiscal : $( this ).find('.regimeFiscalLista').val(),
						
						numeroOAB : $( this ).find('.numeroOABProcuradorLista').val(),
						ufAdvogado : $( this ).find('.ufAdvogadoProcuradorLista').val(),
						idDocumento : $( this ).find('.idDocumentoProcuradorLista').val(),
						tipoDocumento : $( this ).find('.tipoDocumentoProcuradorLista').val(),
						
						idEmail : $( this ).find('.idEmailProcuradorLista').val(),
						emailPessoa : $( this ).find('.emailPessoaProcuradorLista').val(),
						tipoEmail : $( this ).find('.tipoEmailProcuradorLista').val(),
						
						idTelefone : $( this ).find('.idTelefoneProcuradorLista').val(),
						telefone : $( this ).find('.telefoneProcuradorLista').val(),
						tipoTelefone : $( this ).find('.tipoTelefoneProcuradorLista').val()						
				}

				procuradores.push(procurador);

			});	 
			
			var procuracaoDTO = {
				    "procurador": {				    
				}
			}
			
			procuracaoDTO.procurador.idPessoa = this.idPessoa.val();
			procuracaoDTO.procurador.nome = this.nomeProcurador.val();
			procuracaoDTO.procurador.cpf = this.cpf.val();
			
			procuracaoDTO.procurador.idEmail = this.idEmail.val();
			procuracaoDTO.procurador.emailPessoa = this.emailPessoa.val();
			procuracaoDTO.procurador.tipoEmail = this.tipoEmail.val();
			
			procuracaoDTO.procurador.enderecoFormatado = this.enderecoFormatado.val();
			procuracaoDTO.procurador.codgLogradouro = this.codgLogradouro.val();
			procuracaoDTO.procurador.numero = this.numero.val();
			procuracaoDTO.procurador.quadra = this.quadra.val(); 
			procuracaoDTO.procurador.lote = this.lote.val();
			procuracaoDTO.procurador.complemento = this.complemento.val();	
			
			procuracaoDTO.procurador.idEscritorio = $('#idEscritorio').val();
			procuracaoDTO.procurador.numeroCnpjEscritorio = $('#numeroCnpjEscritorio').val();
			procuracaoDTO.procurador.nomeFantasiaEscritorio = $('#nomeFantasiaEscritorio').val();
			procuracaoDTO.procurador.nomeEmpresarialEscritorio = $('#nomeEmpresarialEscritorio').val();
			procuracaoDTO.procurador.regimeFiscal = $('#regimeFiscal').val();
			
			procuracaoDTO.procurador.numeroOAB = this.numeroOAB.val();
			procuracaoDTO.procurador.ufAdvogado = this.ufAdvogado.val();
			procuracaoDTO.procurador.idDocumento = this.idDocumento.val();
			procuracaoDTO.procurador.tipoDocumento = this.tipoDocumento.val();
			
			procuracaoDTO.procurador.telefone = this.telefonePessoa.val();
			procuracaoDTO.procurador.idTelefone = this.idTelefone.val();
			procuracaoDTO.procurador.tipoTelefone = this.tipoTelefone.val();
			
			procuracaoDTO.procurador.idProcurador = this.idProcurador.val();			
			procuracaoDTO.procuradores = procuradores;  
									
			$.ajax({
				url : this.actionUrl, 
				method : 'POST', 
				beforeSend : onBeforeSend.bind(this),
				data: JSON.stringify(procuracaoDTO),
				contentType : 'application/json',
				success : onPesquisaConcluida.bind(this),
				error : onErroPesquisa.bind(this)
			});
	}

	function onBeforeSend(xhr) {
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();
		
		xhr.setRequestHeader(header, token);
		ocultarAlertas();
		
		mostrarMensagemCarregamento('Adicionando o procurador, por favor aguarde...');
	}

	function onPesquisaConcluida(htmlResponse) {
		
		var container = $('.listaProcuradorContainer');
		container.html(htmlResponse);

		new Inputmask().mask(container.find("input"));
		
		controlarListaProcuradorSubstabelecimento();
		new Procurador.ListaProcurador().enable();
		
		new ProcuracaoCadastro.ResetarCadastro().limparFormulario();
		this.cpf.focus();
		
		$('#mensagem-carregamento').hide();
		
	}
	
	function mostrarMensagemCarregamento(mensagem){
		$('#mensagem-consulta-erro').hide();
		//$('#mensagem-consulta-alerta').hide();
		
		$('#mensagem-carregamento').show();
		$('#mensagem-carregamento-texto').html(mensagem);
	}
	
	function onErroPesquisa(error) {		
		$('#mensagem-carregamento').hide();
		mensagemErro(error.responseText);
	}

	return PesquisaProcurador;

}());
/**
 * FIM DO BLOCO REFERENTE A PERSONALIZAÇÃO DO FRAGMENTO DE CONSULTA DE PESSOA
 */

$(function() {
	new Procurador.ListaProcurador().enable();
	new ProcuracaoCadastro.ResetarCadastro().enable();
});