
// TODO: CHECK ON THE CLIENT SIDE!!!! 
(function() {
	let folders = [];
	let alertMessage = document.getElementById("alertMessage");
	let foldersTreeContainer = document.getElementById("foldersTreeContainer");
	let documentDetails = document.getElementById("documentDetails");
	let createSubfolderDetails = document.getElementById("createSubfolderDetails");
	let createDocumentDetails = document.getElementById("createDocumentDetails");
	//let cancelDragAndDrop = document.getElementById("cancelDragAndDrop");
	let trashbin = document.getElementById("trashbin");
	let deleteAlert = document.getElementById("deleteAlert");

	//Page components
	pageOrchestrator = new pageOrchestrator();

	window.addEventListener("load", () => {
		if (sessionStorage.getItem("username") == null) {
			window.location.href = "index.html";
		}
		else {
			//initialize
			pageOrchestrator.start();
			//display initial content
		}
	}, false);
	//false, no bubbling, is catching

	function PersonalMessage() {
		this.show = function() {
			let username = sessionStorage.getItem("username");
			document.getElementById("personalMessage").textContent = "Welcome back " + username;
		}
	}

	function logout() {
		makeCall("GET", 'Logout', null, function(x) {
			if (x.readyState == XMLHttpRequest.DONE) {
				sessionStorage.clear();
				window.location.href = "index.html";
			}
		});
	}

	function FoldersTree() {
		//First parentElement id the foldersTreeContainer
		function buildFoldersTree(folders, parentElement, pageOrchestrator) {
			console.log("Building the tree with pageOrchestrator " + pageOrchestrator);
			foldersTreeContainer.classList.add("nodisplay");

			//Creating an unordered list and appends it to the parent node, so that it can be built recorsively
			let ulElement = document.createElement("ul");
			parentElement.appendChild(ulElement);

			for (let currFolder of folders) {
				//console.log(currFolder);
				let liElement = document.createElement("li");
				let divElement = document.createElement("div");

				//Outerdiv has the 2 children, one which is the folder name and the two buttons,
				//another one for the list of possible documents in such folder
				let outerDiv = document.createElement("div");

				let paragraph1 = document.createElement("p");
				let paragraph2 = document.createElement("p");
				let paragraph3 = document.createElement("p");
				//Inner div which has the folder's name, the buttons for new folder and new document
				divElement.id = currFolder.folderId;
				divElement.classList.add("folderDiv");

				//List of documents in the folder
				let ulDocumentList = document.createElement("ul");
				ulDocumentList.classList.add("nodisplay");

				//Drag start for folders
				divElement.setAttribute('draggable', true);
				divElement.addEventListener('dragstart', (event) => {
					event.dataTransfer.setData('type', 'folder'); //Letting the drag target know what type of file I'm dragging over
					event.dataTransfer.setData('folderId', divElement.id); // Passed the folder ID on drag start
					event.dropEffect = 'move'; // Move effect
					documentDetails.classList.add("nodisplay");
					createSubfolderDetails.classList.add("nodisplay");
					createDocumentDetails.classList.add("nodisplay");
					deleteAlert.classList.add("nodisplay");
					//cancelDragAndDrop.classList.remove("nodisplay");
				});

				// Make the folder div a drop target
				divElement.addEventListener('dragover', (event) => {
					event.preventDefault();
					event.dataTransfer.dropEffect = 'move'; // Imposta l'effetto di drop a 'move'
				});
				//Add event Listener for dropping a document on the folder
				divElement.addEventListener('drop', (event) => {
					event.preventDefault(); // Evita che la pagina venga ricaricata al drop
					//cancelDragAndDrop.classList.add("nodisplay");

					let dropType = event.dataTransfer.getData('type'); // Getting the type of the document dragged over
					let targetFolderId = currFolder.folderId; // L'ID della cartella su cui è stato fatto il drop

					if (dropType === 'document') {
						// Chiamata al server per spostare il documento
						let documentId = event.dataTransfer.getData('documentId');
						requestMoveDocument(documentId, targetFolderId, pageOrchestrator);
					}
					else if (dropType === 'folder') {
						console.log("mhhh ok");
						//DO NOTHING
					}
				});


				//iterate through the documents in the folder
				for (let documentInFolder of currFolder.documents) {
					let liDocument = document.createElement("li");
					liDocument.textContent = documentInFolder.name + "." + documentInFolder.type;
					//Identification for the documents is through ID and class type
					liDocument.id = documentInFolder.documentId;
					liDocument.classList.add("documentLi");

					liDocument.addEventListener("click", () => {
						showDocumentDetails(documentInFolder);
					});

					//Drag start for documents
					liDocument.setAttribute('draggable', true);
					liDocument.addEventListener('dragstart', (event) => {
						event.dataTransfer.setData('type', 'document'); //Letting the drag target know what type of file I'm dragging over
						event.dataTransfer.setData('documentId', liDocument.id); // Passed the document ID on drag start
						event.dropEffect = 'move'; // Move effect
						documentDetails.classList.add("nodisplay");
						createSubfolderDetails.classList.add("nodisplay");
						createDocumentDetails.classList.add("nodisplay");
						deleteAlert.classList.add("nodisplay");
						//cancelDragAndDrop.classList.remove("nodisplay");
					});
					ulDocumentList.appendChild(liDocument);
				}

				//To show the current folder's name
				paragraph1.textContent = currFolder.name;
				//if required for showing only the folders with documents
				if (currFolder.documents.length > 0) {
					paragraph1.classList.add("fakeLink")
				}
				//if user clicks on the folder name, shows the ul of documents in such folder
				paragraph1.addEventListener("click", function() {
					if (ulDocumentList.classList.contains("nodisplay")) {
						ulDocumentList.classList.remove("nodisplay");  // Shows the list
					} else {
						ulDocumentList.classList.add("nodisplay");     // Hides the list
					}
				});

				paragraph2.textContent = "New Folder";
				paragraph2.classList.add("creationButton");
				//if user clicks on the new folder button, it shows the wizard for creating a subfolder in such location,
				//which hides the precedent wizard
				paragraph2.addEventListener("click", () => {
					//Showing where the subfolder is going to be created
					document.getElementById("parentfoldername").textContent = currFolder.name;
					subfolderWizard(currFolder.folderId, pageOrchestrator);
				});

				paragraph3.textContent = "New Document";
				paragraph3.classList.add("creationButton");
				//if user clicks on the new document button, it shows the wizard for creating a document in such location,
				//which hides the precedent wizard
				paragraph3.addEventListener("click", () => {
					//Showing where the document is going to be created
					document.getElementById("documentfoldername").textContent = currFolder.name;
					documentWizard(currFolder.folderId, pageOrchestrator);
				});

				//Inner div which has the folder's name, the buttons for new folder and new document
				divElement.appendChild(paragraph1);
				divElement.appendChild(paragraph2);
				divElement.appendChild(paragraph3);

				//Outer div which contains the previous div and the list of documents inside the folder
				outerDiv.appendChild(divElement);
				outerDiv.appendChild(ulDocumentList);

				liElement.appendChild(outerDiv);
				ulElement.appendChild(liElement);

				//Going down to the subfolders;
				if (currFolder.subFolders && currFolder.subFolders.length > 0) {
					buildFoldersTree(currFolder.subFolders, liElement, pageOrchestrator);
				}
			}
		};

		this.get = function(pageOrchestrator) {
			makeCall("GET", 'GetFolders', null, function(x) {
				if (x.readyState == XMLHttpRequest.DONE) {
					if (x.status == 200) {
						folders = [];
						foldersTreeContainer.innerHTML = "";

						// Parse the JSON response
						let rootFolder = JSON.parse(x.responseText);
						folders.push(rootFolder);
						//After knowing I have the rootFolder, I pass it to the folders array. 
						//Server-side java already populated the tree with folders and documents.
						buildFoldersTree(folders, foldersTreeContainer, pageOrchestrator);
						foldersTreeContainer.classList.remove("nodisplay");

						return;
					}
					if (x.status == 401) {
						logout();
						return;
					}
					document.getElementById("alertMessage").textContent = x.responseText;
				}
			})
		};

		this.refresh = function(pageOrchestrator) {
			foldersTreeContainer.innerHTML = "";
			buildFoldersTree(folders, foldersTreeContainer, pageOrchestrator);
			foldersTreeContainer.classList.remove("nodisplay");
		}

	}

	function showDocumentDetails(documentInFolder) {
		if (documentDetails.classList.contains("nodisplay")) {
			documentDetails.classList.remove("nodisplay");
		}
		else {
			documentDetails.classList.add("nodisplay");
		}
		createSubfolderDetails.classList.add("nodisplay");
		createDocumentDetails.classList.add("nodisplay");
		//cancelDragAndDrop.classList.add("nodisplay");
		deleteAlert.classList.add("nodisplay");


		documentDetails.innerHTML = "";
		let title = document.createElement("h2");
		title.textContent = documentInFolder.name + "." + documentInFolder.type;

		let name = document.createElement("p");
		name.textContent = "Document name: " + documentInFolder.name;

		let type = document.createElement("p");
		type.textContent = "Document type: " + documentInFolder.type;

		let summary = document.createElement("p");
		summary.textContent = "Document summary: " + documentInFolder.summary;
		summary.style.overflowWrap = "break-word"

		let date = document.createElement("p");
		date.textContent = "Creation date: " + new Date(documentInFolder.date).toLocaleDateString();

		let folderLocation = document.createElement("p");
		folderLocation.textContent = "Folder location: " +
			document.getElementById(documentInFolder.folderLocation).querySelector("p").textContent;

		documentDetails.appendChild(title);
		documentDetails.appendChild(name);
		documentDetails.appendChild(type);
		documentDetails.appendChild(summary);
		documentDetails.appendChild(date);
		documentDetails.appendChild(folderLocation);
	}

	function subfolderWizard(parentFolderId, pageOrchestrator) {
		createSubfolderDetails.classList.remove("nodisplay");
		createDocumentDetails.classList.add("nodisplay");
		documentDetails.classList.add("nodisplay");
		//cancelDragAndDrop.classList.add("nodisplay");
		deleteAlert.classList.add("nodisplay");

		let createSubfolderButton = document.getElementById("createSubfolderButton");

		let oldButton = createSubfolderButton.cloneNode(true);  // clone the button to remove the event listeners
		createSubfolderButton.parentNode.replaceChild(oldButton, createSubfolderButton);  // replace old one with new button
		createSubfolderButton = oldButton;

		createSubfolderButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");

			if (form.checkValidity()) {
				let folderName = form.querySelector("input[name='foldername']").value;
				if (!folderName) {
					let message = "Folder name can't be left empty!"
					alertMessage.textContent = message;
					return;
				}
				if (!parentFolderId) {
					let message = "Parent folder can't be empty!"
					alertMessage.textContent = message;
					return;
				}
				form.querySelector("input[name='parentfolder']").value = parentFolderId;

				makeCall("POST", 'CreateFolder', form, function(x) {
					// La callback gestisce la risposta del server
					if (x.readyState == XMLHttpRequest.DONE) {
						// Ottiene il testo della risposta, x è la variabile per la request, vedi makeCall
						if (x.status == 200) {
							//TODO when the folder is created correctly, update the elements of the website accordingly
							let newFolder = JSON.parse(x.responseText);
							addSubFolder(newFolder, folders);
							pageOrchestrator.refresh("Success! New folder has been created.");
							return;
						}
						if (x.status == 401) {
							logout();
							return;
						}
						pageOrchestrator.refresh(x.responseText);
					}
				});
			}
			else {
				form.reportValidity();
			}

		});
	}

	//Function to add folder to the folders array on client-side
	function addSubFolder(folder, foldersArray) {
		for (let parentFolder of foldersArray) {
			if (parentFolder.folderId === folder.parentFolder) {
				parentFolder.subFolders.push(folder);
				return;
			}
			else {
				if (parentFolder.subFolders && parentFolder.subFolders.length > 0) {
					addSubFolder(folder, parentFolder.subFolders);
				}
			}
		}
	}


	function documentWizard(documentFolderId, pageOrchestrator) {
		createDocumentDetails.classList.remove("nodisplay");
		createSubfolderDetails.classList.add("nodisplay");
		documentDetails.classList.add("nodisplay");
		//cancelDragAndDrop.classList.add("nodisplay");
		deleteAlert.classList.add("nodisplay");

		let createDocumentButton = document.getElementById("createDocumentButton");

		let oldButton = createDocumentButton.cloneNode(true);  // clone the button to remove the event listeners
		createDocumentButton.parentNode.replaceChild(oldButton, createDocumentButton);  // replace old one with new button
		createDocumentButton = oldButton;

		createDocumentButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");

			if (form.checkValidity()) {
				let documentName = form.querySelector("input[name='documentname']").value;
				let summary = form.querySelector("textarea[name='summary']").value;
				if (!documentName) {
					let message = "Name can't be left empty!"
					alertMessage.textContent = message;
					return;
				}
				if (!summary) {
					let message = "Summary can't be left empty!"
					alertMessage.textContent = message;
					return;
				}
				if (!documentFolderId) {
					let message = "Folder location can't be left empty!"
					alertMessage.textContent = message;
					return;
				}

				form.querySelector("input[name='documentfolder']").value = documentFolderId;

				makeCall("POST", 'CreateDocument', form, function(x) {
					// La callback gestisce la risposta del server
					if (x.readyState == XMLHttpRequest.DONE) {
						if (x.status == 200) {
							//TODO when the folder is created correctly, update the elements of the website accordingly
							let newDocument = JSON.parse(x.responseText);
							addDocument(newDocument, folders);
							pageOrchestrator.refresh("Success! New document has been created.");
							return;
						}
						if (x.status == 401) {
							logout();
							return;
						}
						pageOrchestrator.refresh(x.responseText);
					}
				});
			}
			else {
				form.reportValidity();
			}
		});
	}

	function deleteFolderAlert(folderId, pageOrchestrator) {
		let deleteFileButton = document.getElementById("deleteFileButton");
		
		let oldButton = deleteFileButton.cloneNode(true);  // clone the button to remove the event listeners
		deleteFileButton.parentNode.replaceChild(oldButton, deleteFileButton);  // replace old one with new button
		deleteFileButton = oldButton;
		
		deleteFileButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");

			if (form.checkValidity()) {
				form.querySelector("input[name='fileId']").value = folderId;

				console.log("File to delete is " + folderId);
				makeCall("POST", 'DeleteFolder', form, function(x) {
					// La callback gestisce la risposta del server
					if (x.readyState == XMLHttpRequest.DONE) {
						if (x.status == 200) {
							removeFolder(x.responseText, folders);
							pageOrchestrator.refresh("Success! Folder has been deleted.");
							return;
						}
						if (x.status == 401) {
							logout();
							return;
						}
						pageOrchestrator.refresh(x.responseText);
					}
				});
			}
			else {
				form.reportValidity();
			}
		});
	}

	//Creates a window that upon user clicking on YES deletes the document and refreshes the folders tree
	function deleteDocumentAlert(documentId, pageOrchestrator) {
		let deleteFileButton = document.getElementById("deleteFileButton");

		let oldButton = deleteFileButton.cloneNode(true);  // clone the button to remove the event listeners
		deleteFileButton.parentNode.replaceChild(oldButton, deleteFileButton);  // replace old one with new button
		deleteFileButton = oldButton;

		deleteFileButton.addEventListener('click', function(e) {
			let form = e.target.closest("form");

			if (form.checkValidity()) {
				form.querySelector("input[name='fileId']").value = documentId;
				
				console.log("File to delete is " + documentId);
				
				makeCall("POST", 'DeleteDocument', form, function(x) {
					// La callback gestisce la risposta del server
					if (x.readyState == XMLHttpRequest.DONE) {
						if (x.status == 200) {
							removeDocument(x.responseText, folders);							
							pageOrchestrator.refresh("Success! Document has been deleted.");
							
							return;
						}
						if (x.status == 401) {
							logout();
							return;
						}
						pageOrchestrator.refresh(x.responseText);
					}
				});
				
			}
			else {
				form.reportValidity();
			}
		});
	}
	

	//Function to add document to the folders array on client-side
	function addDocument(document, foldersArray) {
		for (let folder of foldersArray) {
			if (folder.folderId === document.folderLocation) {
				folder.documents.push(document);
				return;
			}
			else {
				if (folder.subFolders && folder.subFolders.length > 0) {
					addDocument(document, folder.subFolders);
				}
			}
		}
	}

	//Function called once the user drops a document on another folder
	function requestMoveDocument(documentId, targetFolderId, pageOrchestrator) {
		let form = document.createElement('form');
		form.setAttribute('action', '#');

		let fieldSet = document.createElement('fieldset');

		let documentField = document.createElement('input');
		documentField.setAttribute('type', 'text');
		documentField.setAttribute('name', 'documentId');

		let targetField = document.createElement('input');
		targetField.setAttribute('type', 'text');
		targetField.setAttribute('name', 'targetFolderId');

		documentField.value = documentId;
		targetField.value = targetFolderId;

		form.appendChild(fieldSet);
		fieldSet.appendChild(documentField);
		fieldSet.appendChild(targetField);

		makeCall("POST", 'MoveDocument', form, function(x) {
			// La callback gestisce la risposta del server
			if (x.readyState == XMLHttpRequest.DONE) {
				if (x.status == 200) {
					let document = JSON.parse(x.responseText);
					removeDocument(document.documentId, folders);
					addDocument(document, folders);
					pageOrchestrator.refresh("Success! Document has been moved.");
					return;
				}
				if (x.status == 401) {
					logout();
					return;
				}
				pageOrchestrator.refresh(x.responseText);
			}
		});
	}

	//Function to find the document in folders array and remove it
	function removeDocument(documentId, foldersArray) {
		for (let folder of foldersArray) {
			const documentIndex = folder.documents.findIndex(document => document.documentId == documentId);

			if (documentIndex != -1) {
				// Rimuove il documento dalla cartella
				folder.documents.splice(documentIndex, 1);
				return;
			}

			if (folder.subFolders && folder.subFolders.length > 0) {
				const foundDocument = removeDocument(documentId, folder.subFolders);
				if (foundDocument) {
					return;
				}
			}
		}
	}


	function removeFolder(folderId, folders) {
		for (let folder of folders) {
			const folderIndex = folder.subFolders.findIndex(folder => folder.folderId == folderId);

			if (folderIndex != -1) {
				// Rimuove la cartella dalle cartelle
				folder.subFolders.splice(folderIndex, 1);
				return;
			}
			if (folder.subFolders && folder.subFolders.length > 0) {
				const foundFolder = removeFolder(folderId, folder.subFolders);
				if (foundFolder) {
					return;
				}
			}
		}
	}


	function pageOrchestrator() {
		let foldersTree = new FoldersTree();

		this.start = function() {
			//For printing the personal message
			let personalMessage = new PersonalMessage();
			personalMessage.show();

			//For adding a click listener on the logout button
			document.getElementById("logoutButton").addEventListener('click', (e) => logout());

			//For adding a listener to stop drag and drop
			//cancelDragAndDrop.addEventListener('dragover', (e) => { e.preventDefault() });
			/*cancelDragAndDrop.addEventListener('drop', (e) => {
				e.preventDefault();
				alertMessage.textContent = "Drag and drop cancelled";
			});
			*/

			//Adding listener to trash bin
			trashbin.addEventListener('dragover', (e) => {
				e.preventDefault();
				e.dataTransfer.dropEffect = 'move'; // Imposta l'effetto di drop a 'move'
			});
			trashbin.addEventListener('drop', (e) => {
				e.preventDefault();
				let dropType = e.dataTransfer.getData('type'); // Getting the type of the document dragged over
				//cancelDragAndDrop.classList.add("nodisplay");
				deleteAlert.classList.remove("nodisplay");
				if (dropType === 'document') {
					// Chiamata al server per eliminare il documento
					let documentId = e.dataTransfer.getData('documentId');
					deleteDocumentAlert(documentId, this);
				}
				else if (dropType === 'folder') {
					//Chiamata al server eliminare la folder e i documenti e subfolders
					let folderId = e.dataTransfer.getData('folderId');
					deleteFolderAlert(folderId, this)
				}
			})


			const closeButtons = document.querySelectorAll('.closebutton');
			// Itera su ciascun elemento e aggiungi un listener di chiusura del wizard
			closeButtons.forEach(button => {
				button.addEventListener('click', function() {
					documentDetails.classList.add('nodisplay');
					createSubfolderDetails.classList.add('nodisplay');
					createDocumentDetails.classList.add('nodisplay');
					deleteAlert.classList.add('nodisplay');
					alertMessage.textContent = "";
				});
			});


			//For getting the FoldersTree of the user
			foldersTree.get(this);

		};

		this.refresh = function(message) {
			deleteAlert.classList.add("nodisplay");
			alertMessage.textContent = message;
			foldersTree.refresh(this);
		};
	}
})();
