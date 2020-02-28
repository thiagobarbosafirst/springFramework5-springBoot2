//Javascript de inicialização cliente

//<pat:wizard id="idSmartWizard" />

//<div th:replace="/layout/fragments/wizard-documento :: wizardDocumento ('idSmartWizard')/>

var Client = Client || {}

Client.Wizard = (function() {

	function Wizard() {
		this.wizard = $('#smartwizard'); // O cliente define a div que registrará o plugin patWizard
		this.btnActionWizard = $('.js-pat-btn-popup'); // Pode ser botão, input hidden, etc;
		this.verificationUri = this.btnActionWizard.data('verification-uri');
		this.downloadUri = this.btnActionWizard.data('download-uri');
		this.uploadUri= this.btnActionWizard.data('upload-uri');

		this.mensagemContainerError;
		this.mensagemErroDetail;
		
//		this.idProcuracao = this.btnActionWizard.data('id-procuracao');
		//this.idProcuracao;
	}

	Wizard.prototype.enable = function(){

		this.btnActionWizard.on('click', _verifyStep.bind(this))

		this.wizard.patWizard({
//			enableEvents : true,
			onInit : _onInit.bind(this),
			onChange: _onChange.bind(this),
			onDownload: _onDownload.bind(this),
			onUpload: _onUpload.bind(this)
		});
		$('.js-pat-wizard-upload-doc').attr('accept', '.p7s');
//		this.wizard.on('pat.wizard.change', _onChange.bind(this))
		
	}


	function _verifyStep(event) {
		
		this.idProcuracao = $(event.currentTarget).data('id-procuracao');
		
		$.ajax({
			url : this.verificationUri + this.idProcuracao,
			method : 'POST',
			contentType : 'application/json',
			beforeSend : _onBeforeSend.bind(this),
			success : _onSucessVerifyStep.bind(this),
			error : _onErrorVerifyStep.bind(this)
		});
		
	}

	function _loadingAnimation(){
		$.LoadingOverlay('show', {
			image       : '<svg id="svg" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="400" height="400" viewBox="0, 0, 400,400"><g id="svgg"><path id="path0" d="M53.031 45.026 C 52.389 45.668,52.021 120.744,52.648 123.086 C 52.868 123.906,53.153 123.979,56.543 124.086 L 60.200 124.200 60.409 127.675 C 60.523 129.587,60.763 131.297,60.942 131.475 C 61.121 131.654,123.487 131.800,199.533 131.800 L 337.800 131.800 338.733 130.867 C 339.483 130.117,339.710 129.359,339.887 127.016 L 340.108 124.098 342.990 123.913 C 345.356 123.761,346.044 123.556,346.836 122.764 L 347.800 121.800 347.800 84.382 L 347.800 46.964 346.618 45.782 L 345.436 44.600 199.549 44.497 C 67.108 44.404,53.604 44.453,53.031 45.026 M66.100 148.690 C 64.945 148.773,64.000 148.993,64.000 149.178 C 64.000 149.362,63.553 149.626,63.006 149.763 C 62.393 149.917,61.917 150.393,61.763 151.006 C 61.626 151.553,61.362 152.000,61.178 152.000 C 60.993 152.000,60.787 152.945,60.721 154.100 L 60.600 156.200 56.929 156.404 C 54.910 156.517,53.110 156.757,52.929 156.938 C 52.748 157.119,52.600 176.205,52.600 199.352 L 52.600 241.436 53.782 242.618 L 54.964 243.800 171.810 243.800 C 281.991 243.800,288.697 243.761,289.348 243.109 C 289.728 242.729,290.311 242.266,290.643 242.080 C 291.020 241.869,291.390 240.704,291.629 238.971 C 292.100 235.569,291.946 157.413,291.467 156.933 C 291.284 156.750,289.460 156.538,287.414 156.463 L 283.693 156.326 283.510 154.126 C 283.410 152.916,283.151 151.808,282.935 151.663 C 282.719 151.518,282.427 151.090,282.288 150.712 C 282.148 150.333,281.532 149.901,280.917 149.750 C 280.303 149.600,279.672 149.280,279.515 149.038 C 279.245 148.624,71.694 148.285,66.100 148.690 M55.064 268.683 C 54.659 268.787,53.939 269.261,53.464 269.736 L 52.600 270.600 52.600 312.200 L 52.600 353.800 53.600 354.800 L 54.600 355.800 197.069 355.902 C 316.723 355.988,339.626 355.917,340.082 355.461 C 340.381 355.162,341.459 354.792,342.478 354.639 C 344.926 354.272,346.275 352.918,346.636 350.465 C 346.787 349.439,347.111 348.472,347.356 348.315 C 348.064 347.860,347.980 276.882,347.271 276.444 C 346.979 276.264,346.569 275.351,346.359 274.416 C 345.868 272.232,344.164 270.531,341.973 270.039 C 341.032 269.827,340.104 269.372,339.911 269.027 C 339.572 268.421,57.401 268.080,55.064 268.683 " stroke="none" fill="#0c4c24" fill-rule="evenodd"></path><path id="path1" d="" stroke="none" fill="#104c24" fill-rule="evenodd"></path><path id="path2" d="" stroke="none" fill="#104c24" fill-rule="evenodd"></path><path id="path3" d="" stroke="none" fill="#104c24" fill-rule="evenodd"></path><path id="path4" d="" stroke="none" fill="#104c24" fill-rule="evenodd"></path></g></svg>',
			imageAnimation : '2000ms fadein'
			});
	}

	function _onInit(){
		
		this.mensagemContainerError = this.wizard.find('.js-sefazgo-messages-error');
		this.mensagemErroDetail = this.wizard.find('.js-sefazgo-messages-error-detail');
		
	/*	$.ajax({
			url : this.verificationUri + this.idProcuracao,
			method : 'POST',
			contentType : 'application/json',
			beforeSend : _onBeforeSend.bind(this),
			success : _onSucessVerifyStep.bind(this),
			error : _onErrorVerifyStep.bind(this)
		});*/
		console.log('_onInit');
	}

	function _onSucessVerifyStep(step) {
		$(".alert-success").addClass("hidden");
		this.wizard.patWizard('next', step);
	}

	function _onErrorVerifyStep() {
//		location.reload();
		console.log(arguments);
	}
	
	function _onChange(event, step){
		console.log('_onChange event', step);
	}
	
	function _onDownload(step){
		_loadingAnimation();
		$.ajax({
			url: this.downloadUri + this.idProcuracao,
			method: 'GET',
			xhrFields: {
				responseType: 'blob'
			},
			success: _onSucessDownload.bind(this, step),
			error: _onErrorDownload.bind(this),
			beforeSend: _onBeforeSend.bind(this)
		});
	}

	function _onSucessDownload(step, data, status, response){
		/*var fileName = response.getResponseHeader('content-disposition').split('filename=')[1];
		var a = document.createElement('a');
		var url = window.URL.createObjectURL(data);
		a.href = url;
		a.download = fileName;
		a.click();
		window.URL.revokeObjectURL(url);
		
		if (step == 0) {
			this.wizard.patWizard('next');
		}*/	
		$.LoadingOverlay('hide');
		var fileName = response.getResponseHeader('content-disposition').split('filename=')[1];	
	    if (navigator.appVersion.toString().indexOf('.NET') > 0){
	    	window.navigator.msSaveBlob(data, fileName);
	    	if (step == 0) {
				this.wizard.patWizard('next');
			}
	    }
		else
		{
			var link = document.getElementById("link");
			var url = window.URL.createObjectURL(data);
			link.href = url;
			link.download =  fileName;
			link.click();
			window.URL.revokeObjectURL(url)			
			if (step == 0) {
				this.wizard.patWizard('next');
			}
		}
	}

	function _onErrorDownload(result, errStatus, errorMessage){
		console.log('_onErrorDownload');
		$.LoadingOverlay('hide');
	}
	
	function _onUpload(formData){
		_loadingAnimation();
		
		var extension = $('.js-pat-wizard-upload-doc').val().replace(/^.*\./, '');
		if(extension === 'p7s'){
			$.ajax({
				type: "POST",
				async: true,
				contentType: false,
				processData: false,
				cache: false,
				data: formData,
				enctype: 'multipart/form-data',
				url: this.uploadUri + this.idProcuracao,
				success : _onSucessUpload.bind(this),
				error : _onErrorUpload.bind(this),
				beforeSend: _onBeforeSend.bind(this)
			});
		}else {
			$.LoadingOverlay('hide');
			/*this.mensagemErroDetail.text(erro.responseJSON[0]);*/
			this.mensagemErroDetail.text("Formato inválido, por favor selecione um arquivo 'P7S'.");
			this.mensagemContainerError.show();
			this.mensagemContainerError.removeClass('hidden');
		}
	}			
		
	function _onSucessUpload(){
		$.LoadingOverlay('hide');
		this.wizard.patWizard('done');
	}

	function _onErrorUpload(erro){
		this.mensagemErroDetail.text(erro.responseJSON[0]);
		this.mensagemContainerError.show();
		this.mensagemContainerError.removeClass('hidden');
		$.LoadingOverlay('hide');
		/*this.btnEnviarDocumento.val(null);*/
	}

	function _onBeforeSend(xhr, settings) {
		
		var token = $('input[name=_csrf]').val();
		var header = $('input[name=_csrf_header]').val();

		xhr.setRequestHeader(header, token);
	}

	return Wizard;

}());

$(function() {

	new Client.Wizard().enable();
});