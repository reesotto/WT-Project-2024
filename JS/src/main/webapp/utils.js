/**
 * AJAX call management
 */

/*method indica "GET" o "POST" */
/* reset: Un parametro opzionale che, se impostato su true, resetta il form una volta che i dati sono stati inviati. 
Il valore predefinito è true.
 */
function makeCall(method, url, formElement, cback, reset = true) {
    var req = new XMLHttpRequest(); // Crea un nuovo oggetto XMLHttpRequest per la richiesta AJAX

	//onreadystatechange è un parametro della request a cui do una funzione da eseguire ogni volta che la request cambia stato.
    req.onreadystatechange = function() {
        cback(req); 
		/*Ogni volta che lo stato della richiesta cambia, viene eseguita la callback fornita (cback), a cui passo
			la request stessa*/		
    };

    req.open(method, url); // Apre la connessione con il server, utilizzando il metodo e l'URL forniti

    if (formElement == null) {
        req.send(); // Se non è stato passato alcun formElement, invia la richiesta senza dati
    } else {
        req.send(new FormData(formElement)); // Altrimenti invia i dati del form specificato (formElement) usando FormData
    }

    if (formElement !== null && reset === true) {
        formElement.reset(); // Se il form esiste e reset è true, resetta il form dopo l'invio della richiesta
    }
}
