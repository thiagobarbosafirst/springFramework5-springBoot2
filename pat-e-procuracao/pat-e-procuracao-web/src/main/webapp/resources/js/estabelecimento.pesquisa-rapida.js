var recordId;
var idOutorgante;
function setval(varval){	
	idOutorgante = varval;
		event.preventDefault();
			
}

var Estabelecimento = Estabelecimento || {};

Estabelecimento.PesquisaRapidaEstabelecimento = (function(){
	
	function PesquisaRapidaEstabelecimento(){
		
		this.pesquisaRapidaEstabelecimentosModal = $('#pesquisaRapidaEstabelecimentos');
		this.nomeInput = $('#nomeEstabelecimentoModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-estabelecimentos-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaEstabelecimentos');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-estabelecimento').html();		
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemContainer = this.pesquisaRapidaEstabelecimentosModal.find('.js-sefazgo-messages-error');
		this.mensagemErroDetail= this.pesquisaRapidaEstabelecimentosModal.find('.js-sefazgo-messages-error-detail');
		
	}
	
	PesquisaRapidaEstabelecimento.prototype.iniciar = function(){
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaEstabelecimentosModal.on('shown.bs.modal', onModalShow.bind(this));
		this.pesquisaRapidaEstabelecimentosModal.on('hidden.bs.modal', onModalClose.bind(this));
	}
	
	function onModalClose(){
		this.containerTabelaPesquisa.html('');
		this.mensagemContainer.addClass('hidden');
		$('#nomeEstabelecimentoModal').val('');
	}
	
	function onModalShow(event){
		var button = $(event.relatedTarget);
		var actionUrl = button.data('action-url');
		recordId = button.data('record-id');
		this.nomeInput.focus();
		
		//Ocultar as mensagens de alertas.
		$('#pesquisaRapidaEstabelecimentos .alert').hide();
	}
	
//	function onPesquisaRapidaClicado(event){
//		event.preventDefault();
//		var jsonArr = [];
//
//		    jsonArr.push({
//		    	nome: "nome",
//		    	cnpj: "cnpj"
//		    });
//		
//		
//		// + "/" + recordId  
////		alert(JSON.stringify(jsonArr));		
//		
//		$.ajax({
//			url: this.pesquisaRapidaEstabelecimentosModal.find('form').attr('action'), 
//			method: 'GET',
//			contentType: 'application/json',
//			data: JSON.stringify(jsonArr),			
//			success: onPesquisaConcluida.bind(this),
//			error: onErroPesquisa.bind(this)
//		});
//	}  
	
	function onPesquisaRapidaClicado(event){
		event.preventDefault();		
	    $.ajax({
	        type: "GET",
	        contentType : 'application/json;',
	        dataType : 'json',
	        url: this.pesquisaRapidaEstabelecimentosModal.find('form').attr('action'), 
	        data: JSON.stringify(usuario), 
	        success :function(result) {
	         // ..
	       }
	    });
	}  
	
	function onPesquisaConcluida(estabelecimentos){	
		
		this.mensagemContainer.addClass('hidden');
		
		var html = this.template(estabelecimentos);
		this.containerTabelaPesquisa.html(html);
		
		new Estabelecimento.TabelaEstabelecimentoPesquisaRapida(this.pesquisaRapidaEstabelecimentosModal).iniciar();
	}
	
	function onErroPesquisa(erro){
		
		this.mensagemErroDetail.text(erro.responseText)
		this.mensagemContainer.removeClass('hidden');
		this.containerTabelaPesquisa.html('');
	}
	
	return PesquisaRapidaEstabelecimento;
}());

Estabelecimento.TabelaEstabelecimentoPesquisaRapida = (function(){
	
	function TabelaEstabelecimentoPesquisaRapida(modal){
		this.modalEstabelecimento = modal;
		this.estabelecimento = $('.js-estabelecimento-pesquisa-rapida');
	}
	
	TabelaEstabelecimentoPesquisaRapida.prototype.iniciar = function(){
		this.estabelecimento.on('click', onEstabelecimentoSelecionado.bind(this));
	}
	
	function onEstabelecimentoSelecionado(evento){  
		this.modalEstabelecimento.modal('hide');		
		
		var estabelecimentoSelecionado = $(evento.currentTarget);
		
		$('#idOutorgante').val(estabelecimentoSelecionado.data('idpessoa')); // O retorno do json tem que ser minúsculo ex: nomefantasia
		$('#nomeOutorgante').val(estabelecimentoSelecionado.data('nomefantasia')); 
	} 
	
	return TabelaEstabelecimentoPesquisaRapida;  
}());

$(function(){
	new Estabelecimento.PesquisaRapidaEstabelecimento().iniciar();
});