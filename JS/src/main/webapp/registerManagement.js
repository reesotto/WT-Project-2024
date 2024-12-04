(function() {
	// Ascolta l'evento 'click' sul bottone di login
	document.getElementById("registerButton").addEventListener('click', (e) => {

		// Trova il form più vicino al bottone che ha generato l'evento
		let form = e.target.closest("form");

		// Controlla se il form è valido (rispetta i requisiti dei campi HTML5)
		if (form.checkValidity()) {
			
			let username = form.querySelector("input[name='username']").value;
			let password = form.querySelector("input[name='password']").value;
			let passwordconfirm = form.querySelector("input[name='passwordconfirm']").value;
			let email = form.querySelector("input[name='email']").value;
			
			if(!username || !password || !passwordconfirm || !email){
				let message = "Form fields are empty!"
				document.getElementById("registerMessage").textContent = message;
				return;
			}
			
			if(password !== passwordconfirm){
				let message = "Password and password confirmation aren't equal!"
				document.getElementById("registerMessage").textContent = message;
				return;
			}

			// Chiama la funzione makeCall per fare la richiesta AJAX
			makeCall("POST", 'CheckRegister', e.target.closest("form"), function(x) {
				// La callback gestisce la risposta del server
				if (x.readyState == XMLHttpRequest.DONE) {
					// Ottiene il testo della risposta, x è la variabile per la request, vedi makeCall
					let message = x.responseText;
					document.getElementById("registerMessage").textContent = message;
				}
			});
		} else {
			// Se il form non è valido, mostra un messaggio di errore nei campi non validi
			form.reportValidity();
		}
	});
})();
