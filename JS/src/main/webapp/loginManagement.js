(function() {
    // Ascolta l'evento 'click' sul bottone di login
    document.getElementById("loginButton").addEventListener('click', (e) => {

        // Trova il form più vicino al bottone che ha generato l'evento
        let form = e.target.closest("form");

        // Controlla se il form è valido (rispetta i requisiti dei campi HTML5)
        if (form.checkValidity()) {
			
            // Chiama la funzione makeCall per fare la richiesta AJAX
            makeCall("POST", 'CheckLogin', e.target.closest("form"), function(x) {
                // La callback gestisce la risposta del server
                if (x.readyState == XMLHttpRequest.DONE) {
                    // Ottiene il testo della risposta, x è la variabile per la request, vedi makeCall
                    let message = x.responseText;

                    // Controlla lo stato della risposta HTTP
                    switch (x.status) {
                        case 200:
                            // Se la risposta è OK (200), salva il nome utente in sessionStorage
                            sessionStorage.setItem('username', message);
                            // Reindirizza l'utente a "HomeCS.html"
                            window.location.href = "Home.html";
                            break;

                        case 400: // Richiesta malformata
                        case 401: // Non autorizzato
                        case 500: // Errore del server
                            // Mostra il messaggio di errore nella pagina
                            document.getElementById("loginMessage").textContent = message;
                            break;
                    }
                }
            });
        } else {
            // Se il form non è valido, mostra un messaggio di errore nei campi non validi
            form.reportValidity();
        }
    });
})();
