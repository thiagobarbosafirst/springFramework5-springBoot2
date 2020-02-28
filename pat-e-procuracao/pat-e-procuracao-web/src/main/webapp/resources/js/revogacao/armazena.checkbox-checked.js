/**
 * Armazena estado do checkbox selecionando no DOM
 */
var CheckedCheckbox = CheckedCheckbox || {}

CheckedCheckbox.ArmazenaEstado = (function() {

	function ArmazenaEstado(){
		this.checkbox = $('input[type=checkbox]');		
	}
		
	ArmazenaEstado.prototype.iniciar = function() {
		this.checkbox.on('ifChanged', onLocalStorage.bind(this));		
	}
		
	function onLocalStorage (event){
		
		this.checked = $("input[type='checkbox']:checked");
		let keepCheckBoxes = [];
		
		this.checked.each((i,o)=>{
			keepCheckBoxes.push($(o).val());
		});
		localStorage.setItem("keepCheckBoxes", keepCheckBoxes);	
	}
			
	return ArmazenaEstado;
}());

$(function() {
	new CheckedCheckbox.ArmazenaEstado().iniciar();
});
