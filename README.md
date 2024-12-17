# WT-Project-2024
Milan Technical University's Web Technologies project.

This project involves the development of a web application using HTML, Java, and JavaScript, with Apache Tomcat as the web server. The application serves as a simple File Manager, allowing users to log in, create folders, and create documents, all of which are stored in an SQL database.
The application is divided into two main versions, each representing a different implementation approach for the required features:
- **Pure HTML Version**: Uses HTML and Thymeleaf for server-side rendered static web pages.
- **JavaScript Version**: Leverages JavaScript for interactive and dynamic client-side features with asynchronous server requests.

## General Features
The application implements a document and folder management system, accessible after authentication. Below are the main features:
1. **Registration and Login**  
   - Validates registration data.
   - Checks username uniqueness.
   - Logout available from any page.
2. **Folder and Document Management**  
   - Creation of folders and documents.
   - Viewing folder and document lists.
   - Moving documents between folders.
   - Accessing document details.
   - Deleting folders and documents via a trash folder (only in the JS version).
3. **Differences Between Versions**  
   - **Pure HTML**: Each interaction triggers a full page reload.
   - **JavaScript**: Interactions are managed via asynchronous requests (AJAX) without reloading the entire page.
---

## Details of the Pure HTML Version

The HTML version uses Thymeleaf and not JavaScript.  
Key characteristics:
- Navigation between pages via links and buttons.
- Feedback messages displayed through page reloads.
- Operations for moving and managing content performed using static forms and buttons.
---

## Details of the JavaScript Version

The JavaScript version adopts a single-page application approach with dynamic updates.  
Key characteristics:
- Asynchronous client-side operations managed via AJAX.
- Drag and drop for moving documents.
- Dynamic input forms for creating folders and documents.
- Server error handling through alert messages.
---

## System Requirements

- Apache Tomcat.
- Any modern browser (Chrome, Firefox, Edge, etc.).
- JRE (Java Runtime Environment) or JDK (Java Development Kit).
- MySql Server.
- **Read installation PDF for further info.**
---


