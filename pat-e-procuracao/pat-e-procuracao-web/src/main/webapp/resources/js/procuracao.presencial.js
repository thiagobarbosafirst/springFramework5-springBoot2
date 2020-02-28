var ProcuracaoPresencial = ProcuracaoPresencial || {}

function saveProcuracaoPresencial() {

	if (!validateProcuracaoPresencial())
		return;

	var formData = construirFormDataPresencial();

	$.ajax({
		url : $('#actionUrl').val(),
		method : 'POST',
		processData : false,
		contentType : false,
		data : formData,
		beforeSend : function(xhr) {
			_onBeforeSend(xhr);
		},
		success : _onSucessSalvar.bind(this),
		error : _onErrorSalvarProcuracao.bind(this)
	});
}

function construirFormDataPresencial(movimentacoes, arquivos) {
	// movimentacoes -> somente as movimentacoes selecionadas.
	// sujeitosPassivos -> somente os sujeitos passivos selecionados.
	var listaSelecao = $('#form-procuracao-presencial').serializeArray();

	var formData = new FormData();

	$('#form-procuracao-presencial .procuradorListaPresencial').each(
			function(index, obj) {

				formData.append($(obj).attr('name'), $(obj).attr('value'));

			});

	$('#form-procuracao-presencial .fieldProcuracaoPresencial').each(
			function(index, obj) {

				formData.append($(obj).attr('name'), $(obj).val());

			});

	// arquivos -> arquivos selecionados pelo usuário com seus respectivos
	// modelos.
	var arquivos = $('#upload-file-list-procuracao-presencial').getAllFiles();

	for (var i = 0; i < listaSelecao.length; i++) {
		// Para subestabelecimento presencial
		if (listaSelecao[i].name == "movimentacoes[]") {
			// movimentacoes: IDs das movimentações selecionadas.
			formData.append('listaMovimentacoes',
					parseInt(listaSelecao[i].value));
		}
		// Para procuração presencial
		if (listaSelecao[i].name == "sujeitosPassivos[]") {
			// movimentacoes: IDs das movimentações selecionadas.
			formData.append('listaSujeitoPassivos', listaSelecao[i].value);
		}
	}

	for (var i = 0; i < arquivos.length; i++) {
		// files[]: arquivos cadastrados com seus respectivos modelos de
		// documentos.
		formData.append('files[' + i + '].documento', arquivos[i].file);
		formData.append('files[' + i + '].modeloDocumento',
				arquivos[i].codModelo);
	}
	formData.append("idProcuracao", $("#idProcuracao").val());
	return formData;
}

function validateProcuracaoPresencial() {
	var arquivos = $('#upload-file-list-procuracao-presencial').getAllFiles();

	// if(!itensSelecionadosProcesso()){
	// mensagemErro('Não há itens selecionados para prosseguir o processo.');
	// return false;
	// }

	if (arquivos.length <= 0) {
		mensagemErro('Não há documentos selecionados para prosseguir o processo.');
		return false;
	}

	return true;
}

function itensSelecionadosProcesso() {
	var total = 0;
	$('.table-sujeitos-processo td input[type="checkbox"]').each(
			function(i, e) {
				if ($(e).is(':checked'))
					total++;
			});
	return (total > 0)
}

function _onSucessSalvar(data) {
	$('.salvar-procuracao-presencial').attr('disabled', 'disabled');
	$.LoadingOverlay('hide');

	// Redirect ou apenas recaregue a página.
	if (data != null || data != undefined)
		window.location.replace(data);
	else
		location.reload(true); 
}

function _onErrorSalvarProcuracao(erro) {
	$.LoadingOverlay('hide');
	$('.salvar-procuracao-presencial').removeAttr('disabled');
	// erro.responseJSON: Erro disparado no servidor ou falha na validação.
	mensagem = (erro.responseJSON != undefined) ? erro.responseJSON
			: "O processo não pode ser realizado por um problema na comunicação com a SEFAZ-GO.";
	mensagemErro(mensagem);
}

ProcuracaoPresencial.Report = (function() {

	function Report() {
		this.btnReport = $('.btn-report-js');
		// this.form = $('form:first');
		this.form = $('.formProcuracao');

	}

	Report.prototype.enable = function() {
		this.btnReport.on('click', onReport.bind(this));
	}

	function onReport(event) {

		event.preventDefault();

		var botao = $(event.target);
		var tr = botao.closest('tr');
		// var processo = tr.find('.processo');

		var action = "";

		if (this.form.attr("id") == 'form-procuracao-presencial') {

			if (!validarMinuta(botao)) {
				return;
			}
			action = botao.data('action-url');

		} else if (this.form.attr("id") == 'formSubstabelecimento') {

			if (!validarMinutaSubstabelecimento()) {
				return;
			}
			action = this.form.data('action-pdf');
		} else if (this.form.attr("id") == 'formRevogacao') {

			if (!validarMinutaRevogacao()) {
				return;
			}
			action = this.form.data('action-pdf');

		} else {
			action = this.form.data('action-pdf');
		}

		// $('#processoSelecionado').val(processo.val());

		this.form.attr('target', '_newtab');

		this.form.attr('action', action);

		this.form.submit();

		var actionSave = this.form.data('action-default');

		this.form.attr('action', actionSave);

		this.form.removeAttr('target');

	}

	return Report;

}());

$(function() {

	$('.salvar-procuracao-presencial').click(function() {
		saveProcuracaoPresencial();
	});

	$('#upload-file-list-procuracao-presencial').fileUploadList();

	new ProcuracaoPresencial.Report().enable();

});
