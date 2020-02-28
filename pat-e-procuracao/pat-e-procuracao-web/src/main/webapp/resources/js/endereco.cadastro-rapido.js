var Endereco = Endereco || {};

Endereco.PesquisaRapidaEndereco = (function(){
	
	function PesquisaRapidaEndereco(){
		
		this.pesquisaRapidaEnderecosModal = $('#cadastroRapidoEnderecos');
		this.cepInput = $('#cepModal');
		this.pesquisaRapidaBtn = $('.btn-consultar-cep-js');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaEnderecos');
		this.okBtn = $('.js-cadastro-rapido-enderecos-btn');
		this.actionUrl = this.okBtn.data('action-url');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-endereco').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemContainer = this.pesquisaRapidaEnderecosModal.find('.js-sefazgo-messages-error');
		this.mensagemErroDetail= this.pesquisaRapidaEnderecosModal.find('.js-sefazgo-messages-error-detail');
		this.mensagemCarregar = $('#mensagem-carregamento-cep');
		this.formEnderecoCadastro = $('#form-endereco-cadastro');
		this.inputRetorno = null;
		this.retornoEnderecoEscritorio = "#enderecoEscritorioFormatado";
		this.retornoEnderecoPessoa = "#enderecoFormatado";	
		this.cepPessoaGravado = "#cepProcurador";
		this.cepEscritorioGravado = "#cepEscritorio";
	}
	
	PesquisaRapidaEndereco.prototype.iniciar = function(){
		this.pesquisaRapidaBtn.on('click', consultarEnderecoPorCep.bind(this));
		this.okBtn.on('click', retornaEndereco.bind(this));
		this.pesquisaRapidaEnderecosModal.on('shown.bs.modal', onModalShow.bind(this));
		this.pesquisaRapidaEnderecosModal.on('show.bs.modal', onModalStart.bind(this));
		this.pesquisaRapidaEnderecosModal.on('hidden.bs.modal', onModalClose.bind(this));
	}
	
	function onModalClose(){
		this.containerTabelaPesquisa.html('');
		this.mensagemContainer.addClass('hidden');
		this.formEnderecoCadastro.trigger('reset');
		$('#contentInputsModal').addClass('hidden');
	}
	
	function onModalShow(event){
		var id = $(event.relatedTarget).attr('id');
		var enderecoEscritorio = (id == "btn-endereco-escritorio");
		
		this.inputRetorno = enderecoEscritorio ? this.retornoEnderecoEscritorio : this.retornoEnderecoPessoa;		
		var valueCep = enderecoEscritorio ? $(this.cepEscritorioGravado).val(): $(this.cepPessoaGravado).val();
		
		var tituloModal = enderecoEscritorio ? 'Consultar Endereço - Escritório do Advogado' : 'Consultar Endereço - Procurador'; 		
		$('#modal-title').html(tituloModal);		
		
		if(valueCep.trim().length > 0){			
			this.cepInput.val(valueCep);
			$('#contentInputsModal').removeClass('hidden');
			
			if(!enderecoEscritorio)				
				loadEnderecoPessoa();			   
			else				
				loadEnderecoEscritorio();							
		}
				
		this.cepInput.focus();
	}
	
	function onModalStart(event){
		//Ocultar as mensagens de alertas.
		$('#cadastroRapidoEnderecos .alert').hide();
	}
	
	function loadEnderecoPessoa(){
		$('#tipoLogradouroModal').val($('#tipoLogradouroProcurador').val());
		$('#logradouroModal').val($('#logradouroProcurador').val());
		$('#bairroLogradouroModal').val($('#bairroLogradouroProcurador').val());
		$('#municipioModal').val($('#cidadeLogradouroProcurador').val());
		$('#ufModal').val($('#ufLogradouroProcurador').val());
		
		$('#numeroModal').val($('#numero').val());
		$('#quadraModal').val($('#quadra').val());
		$('#loteModal').val($('#lote').val());
		$('#complementoModal').val($('#complemento').val());
	}
	
	function loadEnderecoEscritorio(){
		$('#tipoLogradouroModal').val($('#tipoLogradouroEscritorio').val());
		$('#logradouroModal').val($('#logradouroEscritorio').val());
		$('#bairroLogradouroModal').val($('#bairroLogradouroEscritorio').val());
		$('#municipioModal').val($('#cidadeLogradouroEscritorio').val());
		$('#ufModal').val($('#ufLogradouroEscritorio').val());
		
		$('#numeroModal').val($('#numeroEscritorio').val());
		$('#quadraModal').val($('#quadraEscritorio').val());
		$('#loteModal').val($('#loteEscritorio').val());
		$('#complementoModal').val($('#complementoEscritorio').val());
	}
	
	function consultarEnderecoPorCep(event){
		event.preventDefault();
		var cep = cepSemFormatacao(this.cepInput);		
		$('#cepConsultado').val(cep);
		if(cep != ""){
			$.ajax({
				url : this.actionUrl,
				method : 'GET',
				contentType : 'application/json',
				data: {
					cep: cep
				},
				success : onSucess.bind(this),
				error : onError.bind(this),
				beforeSend: onProgress.bind(this)
			});
		}
	}
	
	function cepSemFormatacao(cepInput)
	{
		return cepInput.val().replace(/[^0-9]/g, '');
	}
	
	function retornaEndereco(event){
		event.preventDefault();
		var cep = $('#cepConsultado').val().trim() == "" ? cepSemFormatacao(this.cepInput) : $('#cepConsultado').val();
		
		if(this.inputRetorno == this.retornoEnderecoPessoa){						
			$('#codgLogradouro').val($('#codgLogradouroModal').val());
			$('#cepProcurador').val(cep);
			
			$('#tipoLogradouroProcurador').val($('#tipoLogradouroModal').val());
			$('#logradouroProcurador').val($('#logradouroModal').val());
			$('#bairroLogradouroProcurador').val($('#bairroLogradouroModal').val());
			$('#cidadeLogradouroProcurador').val($('#municipioModal').val());
			$('#ufLogradouroProcurador').val($('#ufModal').val());
			
			$('#numero').val($('#numeroModal').val());
			$('#quadra').val($('#quadraModal').val());
			$('#lote').val($('#loteModal').val());
			$('#complemento').val($('#complementoModal').val());
		}
		else{
			$('#codgLogradouroEscritorio').val($('#codgLogradouroModal').val());
			$('#cepEscritorio').val(cep);
			
			$('#tipoLogradouroEscritorio').val($('#tipoLogradouroModal').val());
			$('#logradouroEscritorio').val($('#logradouroModal').val());
			$('#bairroLogradouroEscritorio').val($('#bairroLogradouroModal').val());
			$('#cidadeLogradouroEscritorio').val($('#municipioModal').val());
			$('#ufLogradouroEscritorio').val($('#ufModal').val());
			
			$('#numeroEscritorio').val($('#numeroModal').val());
			$('#quadraEscritorio').val($('#quadraModal').val());
			$('#loteEscritorio').val($('#loteModal').val());
			$('#complementoEscritorio').val($('#complementoModal').val());
		}
		
		$(this.inputRetorno).parents('.input-group').first().removeClass('has-error');

		//Retirar mensagens de alerta.
		$('#container-mensagens .alert').each(function(i,e){$(e).hide()});
		
		var tipoLogradouro = $('#tipoLogradouroModal').val();
		var logradouro = $('#logradouroModal').val();
		var numero = $('#numeroModal').val();
		var quadra = $('#quadraModal').val();
		var lote = $('#loteModal').val();
		var complemento = $('#complementoModal').val();
		var bairro = $('#bairroLogradouroModal').val();
		var cidade = $('#municipioModal').val();
		var uf = $('#ufModal').val();
		
		var enderecoFormatado = formatarEndereco(tipoLogradouro,logradouro,numero,quadra,lote,complemento,bairro,cidade,uf);
		
		$(this.inputRetorno).val(enderecoFormatado);
		
		this.pesquisaRapidaEnderecosModal.modal('hide');
	}
	
	function formatarEndereco(tipoLogradouro,logradouro,numero,quadra,lote,complemento,bairro,cidade,uf){
		var endereco = "";
		
		endereco += tipoLogradouro.length > 0 ? tipoLogradouro : "";
		endereco += logradouro.length > 0 ? ' '  + logradouro : "";
		endereco += numero.length > 0 ? ', No. ' + numero : "";
		endereco += quadra.length > 0 ? ', QD. ' + quadra : "";
		endereco += lote.length > 0 ? ', LT. ' + lote : "";
		endereco += complemento.length > 0 ? ', ' + complemento : "";
		endereco += bairro.length > 0 ? ', ' + bairro : "";
		endereco += cidade.length > 0 ? ', ' + cidade : "";
		endereco += uf.length > 0 ? ' - ' + uf : "";
		
		return endereco; 
	}
	
	function onSucess(enderecos){
		this.mensagemContainer.addClass('hidden');
		var html = this.template(enderecos);
		this.containerTabelaPesquisa.html(html);
		new Endereco.TabelaEnderecoPesquisaRapida(this.pesquisaRapidaEnderecosModal).iniciar();
		this.mensagemCarregar.hide();
	}
	
	function onError(err){
		this.mensagemErroDetail.text(err.responseText)
		this.mensagemContainer.removeClass('hidden');
		this.containerTabelaPesquisa.html('');
		this.mensagemCarregar.hide();
	}
	
	function onProgress(){
		this.mensagemCarregar.show();
	}
	
	return PesquisaRapidaEndereco;
}());

Endereco.TabelaEnderecoPesquisaRapida = (function(){
	
	function TabelaEnderecoPesquisaRapida(modal){
		this.modalEndereco = modal;
		this.endereco = $('.js-endereco-pesquisa-rapida');
	}
	
	TabelaEnderecoPesquisaRapida.prototype.iniciar = function(){
		this.endereco.on('click', onEnderecoSelecionado.bind(this));
	}
	
	function onEnderecoSelecionado(evento){
		var enderecoSelecionado = $(evento.currentTarget);
		
		$('#contentInputsModal').removeClass('hidden');
		
		// inputModal.val(data-... do hbs)
		//$('#idEnderecoModal').val(enderecoSelecionado.data('idendereco'));
		$('#codgLogradouroModal').val(enderecoSelecionado.data('codglogradouro'));
		$('#tipoLogradouroModal').val(enderecoSelecionado.data('tipologradouro').toString().toUpperCase());
		$('#logradouroModal').val(enderecoSelecionado.data('nomelogradouro').toString().toUpperCase());
		$('#ufModal').val(enderecoSelecionado.data('uf').toUpperCase().toString().toUpperCase());
		$('#municipioModal').val(enderecoSelecionado.data('municipio').toString().toUpperCase());
		$('#bairroLogradouroModal').val(enderecoSelecionado.data('bairro').toString().toUpperCase());
		$('#numeroModal').focus();
	}
	
	return TabelaEnderecoPesquisaRapida;
	
}());

$(function(){
	new Endereco.PesquisaRapidaEndereco().iniciar();
});