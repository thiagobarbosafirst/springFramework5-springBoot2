var PatUpload = PatUpload || {}

PatUpload.ModalUpload = (function() {
	
	function ModalUpload() {
		this.modalUpload = $('#modalUploadConfirmation');
	}
	
	ModalUpload.prototype.enable = function(){		
		this.modalUpload.on('hidden.bs.modal', onModalClose.bind(this));		
	}
	
	function onModalClose(){
		//document.location.href='/pat/procuracao/procuracao/list';
		document.location.reload(true);
	}

	return ModalUpload;
	
}());

$(function() {
	new PatUpload.ModalUpload().enable();
});