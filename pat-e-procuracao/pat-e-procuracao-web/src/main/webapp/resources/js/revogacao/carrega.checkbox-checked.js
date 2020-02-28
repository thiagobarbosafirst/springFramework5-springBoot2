/**
 * Seta os checkbox armazenados ao recarregar o DOM
 */
var ExecutaCheckboxArmazenado = ExecutaCheckboxArmazenado || {}

ExecutaCheckboxArmazenado.ObtemCheckboxArmazenado = (function() {
	
	function ObtemCheckboxArmazenado(){
		this.documento = $(document);		
	}
	
	ObtemCheckboxArmazenado.prototype.iniciar = function() {
		this.documento.ready(onDocumento.bind(this));
	}
	
	function onDocumento (event){
		if(localStorage.length != 0){
			localStorage.getItem("keepCheckBoxes").split(",").forEach(function(o){
				$('input[value="'+o+'"]').iCheck('check');
			});			
		}
	} 
	
	return ObtemCheckboxArmazenado;
}());

$(function() {
	new ExecutaCheckboxArmazenado.ObtemCheckboxArmazenado().iniciar();
});

