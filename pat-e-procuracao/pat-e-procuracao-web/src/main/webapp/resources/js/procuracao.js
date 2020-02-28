var Procuracao = Procuracao || {}

/**
 * DATE RANGE PIKER
 */
Procuracao.DateRangePicker = (function() {

	function DateRangePicker() {
		this.pattern = 'DD/MM/YYYY';
		this.rangepiker = $('.js-daterangepiker');
		this.inputFrom = $(".js-daterangepiker-input-from");
		this.inputTo = $(".js-daterangepiker-input-to");
	}

	DateRangePicker.prototype.enable = function() {

		if (typeof ($.fn.daterangepicker) === 'undefined') {
			console.log('Daterangepicker dependency not loaded');
			return;
		}

		this.rangepiker.bind("change", onChange.bind(this));
		
		moment.locale('pt-br');
		
		this.rangepiker.daterangepicker({
			autoUpdateInput : false,
			autoApply : true,
			showDropdowns : true,
			locale : {
				format : this.pattern
			}
		}, onApply.bind(this));
	}

	function onApply(start, end, label) {

		this.inputFrom.prop("value", start.format(this.pattern));
		this.inputTo.prop("value", end.format(this.pattern));

		this.rangepiker.val(start.format(this.pattern) + ' - '
				+ end.format(this.pattern));
	}

	function onChange() {

		if (this.rangepiker.val() == '') {
			this.inputFrom.prop('value', '');
			this.inputTo.prop('value', '');
		}
	}

	return DateRangePicker;

}());

/**
 * DATE PIKER
 */
Procuracao.DatePicker = (function() {

	function DatePicker() {
		this.pattern = 'DD/MM/YYYY';
		this.datepickers = $('.js-datepicker');
	}

	DatePicker.prototype.enable = function() {

		for (var i = 0; i < this.datepickers.length; i++) {
			
			var datepicker = $(this.datepickers[i]);
			
			if (typeof ($.fn.daterangepicker) === 'undefined') {
				console.log('Daterangepicker dependency not loaded');
				return;
			}

			var initializer = datepicker.data('initialize-date');

			if (initializer == undefined) {
				initializer = false;
			}

			datepicker.daterangepicker({
				singleDatePicker : true,
				showDropdowns : true,
				autoApply : true,
				autoUpdateInput : initializer,
				locale : {
					format : this.pattern
				}
			});

			//datepicker.on('hide.daterangepicker', onHide.bind(this));
			datepicker.on('apply.daterangepicker', onApply.bind(this));
		}
	}

	function onHide(event, picker) {
		var input = $(event.currentTarget);
		input.val(picker.startDate.format(picker.locale.format));
	}
	
	function onApply(event, picker) {
		var input = $(event.currentTarget);		
		input.val(picker.startDate.format(picker.locale.format));
	}

	return DatePicker;

}());

/**
 * RANGE SLIDER
 */
Procuracao.RangeSlider = (function() {

	function RangeSlider() {

		this.slider = $(".js-rangeslider");
		this.inputFrom = $(".js-rangeslider-input-from"),
				this.inputTo = $(".js-rangeslider-input-to")

		this.from = this.inputFrom.val();
		this.to = this.inputTo.val();
	}

	RangeSlider.prototype.enable = function() {

		if (typeof ($.fn.ionRangeSlider) === 'undefined') {
			console.log('IonRangeSlider dependency not loaded');
			return;
		}

		this.slider.ionRangeSlider({
			type : "double",
			grid : true,
			min : 0,
			max : 1,
			from : isNumber(this.from) ? this.from : 0,
			to : isNumber(this.to) ? this.to : 1,
			postfix : '%',
			step : 0.1,
			keyboard : true,
			keyboard_step : 0.1,
			decorate_both : true,
			values_separator : ' to ',
			onChange : updateInputs.bind(this),
			onFinish : updateInputs.bind(this)
		});

	}

	function updateInputs(data) {
		this.inputFrom.prop("value", data.from);
		this.inputTo.prop("value", data.to);
	}

	function isNumber(n) {
		return !isNaN(parseFloat(n)) && isFinite(n);
	}

	return RangeSlider;

}());


Procuracao.Report = (function() {
	
	function Report(){
		this.btnReport = $('.btn-report-js');
		//this.form = $('form:first');
		this.form = $('.formProcuracao'); 
		
	}
	
	Report.prototype.enable = function(){
		this.btnReport.on('click', onReport.bind(this));
	}
	
	function onReport(event) {		
		
		event.preventDefault();
		
		var botao = $(event.target);
	    var tr = botao.closest('tr');
	   // var processo = tr.find('.processo');
	    
	    var action = "";
	    
	   if(this.form.attr("id") == 'formProcuracao') {
		   
		   if(!validarMinuta(botao)) {
			   return;
		   } 
		   
			action = botao.data('action-url');
		}
	   
	   else if(this.form.attr("id") == 'formSubstabelecimento') {
		   
		   if(!validarMinutaSubstabelecimento()) {
			   return;
		   } 
		   action = this.form.data('action-pdf');  
		   
	   }
	   else if(this.form.attr("id") == 'formRevogacao') {
		   
		   if(!validarMinutaRevogacao()) {
			   return;
		   } 
		   action = this.form.data('action-pdf');  
		   
	   }
	   else {
		   action = this.form.data('action-pdf');  
	   }	    
	   
		
//		$('#processoSelecionado').val(processo.val());
		
		this.form.attr('target', '_newtab');	
		
		this.form.attr('action', action);
				
		this.form.submit();	
		
		var actionSave = this.form.data('action-default');
		
		this.form.attr('action', actionSave);
		
		this.form.removeAttr('target'); 
		
	}
	
	return Report;
	
}());

/**
 * MODAL DELETE CONFIRMATION
 */
Procuracao.ModalDelete = (function() {
	
	function ModalDelete() {
		this.modalDelete = $('#modalDeleteConfirmation');
	}
	
	ModalDelete.prototype.enable = function(){
		
		this.modalDelete.on('show.bs.modal', function(event) {
			
			var button = $(event.relatedTarget);
			var actionUrl = button.data('action-url');
			var recordId = button.data('record-id');
			var recordName = button.data('record-name');
			var modal = $(this);
			modal.find('.btn-modal-js').attr("onclick", "location.href='" + actionUrl + "/" + recordId + "'");
			modal.find('.modal-body').text("Tem certeza de excluir '" + recordName + "'?")
		});
	}
	
	return ModalDelete;
	
}());

function selecionarSujeitos(item){
	var processo = $(item).data('id-processo');	
	var element = null;
		
	$('.table-sujeitos-processo td input[type="checkbox"]').each(function(i,e){
		if($(e).data('id-processo') == processo){
			if($(item).is(':checked')) {
				$(e).iCheck('check');	
				
			}
			else {
				$(e).iCheck('uncheck');
			}
		}			
	});
}

/**
 * Recuperado do histórico 
 */  
function validarMinuta(botao) {
	
	if(!validarProcurador()) {
		return false;
	}
	
	var processo = $(botao).data('id-processo');
    
	var element = null;
	
	var selecionouSujeitoPassivoProcesso = false;
		
	$('.table-sujeitos-processo td input[type="checkbox"]').each(function(i,e){
		if($(e).data('id-processo') == processo){
			if($(e).is(':checked')){
				selecionouSujeitoPassivoProcesso = true;
			}
		}			
	}); 
	
	if(!selecionouSujeitoPassivoProcesso) {		
		mensagemErro('Selecione o processo para visualizar a minuta.');
		return false;
	}
	
	
	return true;
}

function validarProcurador() {
	var totalProcuradores = 0;
	$('.procuradores').each(function(index){
		totalProcuradores++;
	});	
	if(totalProcuradores == 0) {
		$('#secondTab').removeClass('active');
		$('#second-tab').removeClass('active');
		$('#firstTab').addClass('active')
		$('#first-tab').addClass('active')
		mensagemErro("Informe os procuradores");			
		return false;
	}
	return true;
}


function validarMinutaSubstabelecimento() {
	if(!validarProcurador()) {
		return false;
	}
	
	var selecionouSujeitoPassivoProcesso = false;
		
	$('.table-sujeitos-processo td input[type="checkbox"]').each(function(i,e){		
		if($(e).is(':checked')){
			selecionouSujeitoPassivoProcesso = true;
		}
	}); 
	
	if(!selecionouSujeitoPassivoProcesso) {		
		mensagemErro('Selecione o processo para visualizar a minuta.');
		return false;
	}	
	
	return true;
}

function validarMinutaRevogacao() {
	
	var selecionouSujeitoPassivoProcesso = false;
		
	$('.table-sujeitos-processo td input[type="checkbox"]').each(function(i,e){		
		if($(e).is(':checked')){
			selecionouSujeitoPassivoProcesso = true;
		}
	}); 
	
	if(!selecionouSujeitoPassivoProcesso) {		
		mensagemErro('Selecione o processo para visualizar a minuta.');
		return false;
	}	
	
	return true;
}

function mensagemErro(mensagem){	
	$('.js-sefazgo-messages-error').show();
	$('.js-sefazgo-messages-error-detail').html(mensagem);
}


function itensSelecionadosRenuncia(){
	var total = 0;	
	$('.table-sujeitos-processo td input[type="checkbox"]').each(function(i,e){
		if($(e).is(':checked'))
		  total++;		
	});
	return (total > 0)
}

function _onErrorSalvar(erro){
	$.LoadingOverlay('hide');
	$('.salvar-movimentacao-presencial').removeAttr('disabled');
	//erro.responseJSON: Erro disparado no servidor ou falha na validação.
	mensagem = (erro.responseJSON != undefined) ? erro.responseJSON : "O processo não pode ser realizado por um problema na comunicação com a SEFAZ-GO."; 
	showErrorMessage(mensagem);				
}

function showInfoMessage(mensagem){
	$('#mensagem-renuncia').hide();
	$('#mensagem-load').show();
	$('#mensagem-load').find('.mensagem-icone-texto').html(mensagem);
}

function hideInfoMessage(){
	$('#mensagem-load').hide();
}	

function showErrorMessage(mensagem){
	$('#mensagem-load').hide();
	
	$('#mensagem-renuncia').show()
		.addClass('alert-danger')
		.removeClass('alert-success')
		.find('.mensagem-texto')
		.html(mensagem);
}

function showSuccessMessage(mensagem){
	$('#mensagem-load').hide();
	
	$('#mensagem-renuncia').show()
		.addClass('alert-success')
		.removeClass('alert-danger')
		.find('.mensagem-texto')
		.html(mensagem);
}

function _onBeforeSend(xhr) {
	Pat.loadingAnimation();		
	var token = $('input[name=_csrf]').val();
	var header = $('input[name=_csrf_header]').val();
	xhr.setRequestHeader(header, token);	
	$('.salvar-movimentacao-presencial').attr('disabled','disabled');
}

function _onSucessSalvar(data){
	$('.salvar-movimentacao-presencial').attr('disabled','disabled');
	$.LoadingOverlay('hide');
	
	if(data != null || data != undefined)
		window.location.replace(data);
	else			
		location.reload(true);		
}

function saveMovimentacaoPresencial() {
	
	if(!validateMovimentacaoPresencial())
		return;
	
	$.ajax({
		url : $('#actionUrl').val(),			
		method : 'POST',
		processData: false,
        contentType: false,            
		data: construirFormData(),
		beforeSend : function(xhr){
			_onBeforeSend(xhr);
		},			
		success : _onSucessSalvar.bind(this),
		error : _onErrorSalvar.bind(this)
	});	
}

function construirFormData(movimentacoes,arquivos){
	//movimentacoes -> somente as movimentacoes selecionadas.
	var movimentacoes = $('#form-movimentacao-presencial').serializeArray();
	
	//arquivos -> arquivos selecionados pelo usuário com seus respectivos modelos.
	var arquivos = $('#upload-file-list-renuncia').getAllFiles();
	
	var formData = new FormData();
	
	//procurador: ID do usuário vinculado ao procurador.
	formData.append('procurador', $('#id-usuario-procurador').val());
	formData.append("idSujeitoPassivo", $("#idSujeitoPassivo").val());
	
	for (var i = 0; i < movimentacoes.length; i++) {
		if(movimentacoes[i].name == "detalhe[]"){
			//movimentacoes: IDs das movimentações selecionadas.
			formData.append('movimentacoes', parseInt(movimentacoes[i].value));
		}				
	}
	
	for (var i = 0; i < arquivos.length; i++) {	
		//files[]: arquivos cadastrados com seus respectivos modelos de documentos.
		formData.append('files[' + i + '].documento', arquivos[i].file);
		formData.append('files[' + i + '].modeloDocumento',  arquivos[i].codModelo);
	}
	return formData;	
}

function validateMovimentacaoPresencial(){
	var arquivos = $('#upload-file-list-renuncia').getAllFiles();
	
	if(!itensSelecionadosRenuncia()){
		showErrorMessage('Não há itens selecionados para prosseguir o processo de renúncia.');
		return false;
	} 
	
	if(arquivos.length <= 0){
		showErrorMessage('Não há documentos selecionados para prosseguir o processo de renúncia.');
		return false;
	}
	
	return true;
}

Procuracao.SelecionarTipoProcurador = (function() {

	function SelecionarTipoProcurador() {
		this.toggle = $('#tipoProcuradorToggle');
		if(this.toggle != undefined && this.toggle.length > 0){
			this.formularioProcurador = document.getElementById("formulario-procurador");
			this.escritorioAdvogado = document.getElementById("escritorioAdvogado");
			this.ufAdvogado = document.getElementById("divUfAdvogado");
			this.numeroOAB = document.getElementById("divNumeroOAB");
			
			if(!this.toggle.prop('checked')){
				this.escritorioAdvogado.style.display = 'none';
				this.ufAdvogado.style.display = 'none';	
				this.numeroOAB.style.display = 'none';		
			}
		}
	}

	SelecionarTipoProcurador.prototype.enable = function() {
		if(this.toggle != undefined && this.toggle.length > 0)
			this.toggle.bind("change", onSelectedValue.bind(this));
	}

	function onSelectedValue(event){
		this.advogado = this.toggle.prop('checked');
		if(this.advogado){
			this.escritorioAdvogado.style.display = 'block';
			this.numeroOAB.style.display = 'block';
			this.ufAdvogado.style.display = 'block';
		}
		if(!this.advogado){
			this.escritorioAdvogado.style.display = 'none';
			this.numeroOAB.style.display = 'none';		
			this.ufAdvogado.style.display = 'none';		
		}
		this.formularioProcurador.style.display = 'block';
	}
	
	return SelecionarTipoProcurador;

}());


$(function() {
	if($('#permiteEstabelecimento').length > 0) {		
			var toggle = $('#permiteEstabelecimento').sefazgoToggle({
				onChange: function(event, status) {
					var resevaPoderes = $('#divReservaPoderes');
					status ? resevaPoderes.show() : resevaPoderes.hide();
					var divDataLimite = $('#divDataLimite');
					status ? divDataLimite.show() : divDataLimite.hide();
				}
			});				
	}

	if (document.getElementById("tpProcuracao") != null){
		document.getElementById("tpProcuracao").value = 1;
	}
	
	if($('#tipoProcuracao').length > 0) {
		$('#tipoProcuracao').sefazgoToggle({
			onChange: function(event) {
				if(event.context.checked == true){
					document.getElementById("tpProcuracao").value = 2;
				}
				if(event.context.checked == false){
					document.getElementById("tpProcuracao").value = 1;
				}
			}
		});		
	}
	
	$('.collapse-toggle').on('click', function() {
		$(this).next('.collapse').collapse('toggle');
		$(this).find('i:first').toggleClass('fa-angle-double-up fa-angle-double-down');
	})

	$(".clickable").click(function() {
	    $(this).next().toggle();	    
	});	
	
	$('.table-procuracoes-processo > tbody > tr > td .row-processo-renuncia').on('ifChecked',function(event){
		selecionarSujeitos(this);		
	});
	
	$('.table-procuracoes-processo > tbody > tr > td .row-processo-renuncia').on('ifUnchecked',function(event){
		selecionarSujeitos(this);		
	});	
		
	$('.salvar-movimentacao-presencial').click(function(){
		saveMovimentacaoPresencial();
	});	

	$('#formRenuncia').on('submit',function(){
		
		if(!itensSelecionadosRenuncia()){
			$('.mensagem-erro').show();
			$('.mensagem-erro .mensagem-texto').html('Não há itens selecionados para prosseguir o processo de renúncia.');			
			return false;
		}
		else{
			$('.mensagem-erro').hide();
		}
	});
	
	$(document).ready(function(){
		//Quando a pesquisa for por todos.
		if($('#lastQuery').val() == ''){
			$('#process-search-component #textoPesquisa').val('');
		}
	});
	
	$('#upload-file-list-renuncia').fileUploadList();
	
	new Procuracao.ModalDelete().enable();	
	new Procuracao.Report().enable(); 
	
	new Sefazgo.Mask().enable();
	new Sefazgo.InputClear().enable();
	
	new Procuracao.DateRangePicker().enable();
	new Procuracao.RangeSlider().enable();
	new Procuracao.DatePicker().enable();
	new Procuracao.SelecionarTipoProcurador().enable();
	
	new Pat.ModalDownload().enable(); 
		
	
});

