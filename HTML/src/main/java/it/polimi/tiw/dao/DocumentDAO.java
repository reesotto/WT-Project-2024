package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.NoPathException;
import it.polimi.tiw.exceptions.SameFileNameException;

public class DocumentDAO {
	private Connection connection;

	public DocumentDAO(Connection connection) {
		this.connection = connection;
	}

	public void createDocument(Document document) throws SQLException {
		String query = "INSERT INTO document (owner, name, date, type, folderlocation, summary) VALUES (?, ?, ?, ?, ?, ?)";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, document.getOwner());
			pstatement.setString(2, document.getName());
			pstatement.setDate(3, document.getDate());
			pstatement.setString(4, document.getType());
			pstatement.setInt(5, document.getFolderLocation());
			pstatement.setString(6, document.getSummary());
			pstatement.executeUpdate();
		}
	}

	public void checkFolderPath(Document document, String folderPath[]) throws SQLException, SameFileNameException, NoPathException{

		//Folder creation for the document
		String query1 = "SELECT 1 FROM folder WHERE owner = ? AND name = ? AND parentFolder IS NULL";
		String query2 = "SELECT folderid FROM folder WHERE owner = ? AND name = ? AND parentFolder IS NULL";
		String query3 = "SELECT folderid FROM folder WHERE owner = ? AND name = ? AND parentFolder = ?";
		String query4 = "SELECT 1 FROM document WHERE owner = ? AND name = ? AND folderLocation = ? AND type = ?";

		//Folder root check
		try(PreparedStatement pstatement = connection.prepareStatement(query1)){
			pstatement.setInt(1, document.getOwner());
			pstatement.setString(2, folderPath[0]);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(!resultSet.next()) {
					throw new NoPathException("Input path doesn't exist.");
				}
			}
		}

		//Folder path check
		Integer parentFolder = null;

		for(String currFolder : folderPath) {
			if(parentFolder == null) {
				try(PreparedStatement pstatement = connection.prepareStatement(query2)){
					pstatement.setInt(1, document.getOwner());
					pstatement.setString(2, currFolder);

					try(ResultSet resultSet = pstatement.executeQuery()){
						if(!resultSet.next()) {
							throw new NoPathException("Input path doesn't exist.");
						}
						parentFolder = resultSet.getInt("folderid");
					}
				}
			}
			else {
				try(PreparedStatement pstatement = connection.prepareStatement(query3)){
					pstatement.setInt(1, document.getOwner());
					pstatement.setString(2, currFolder);
					pstatement.setInt(3, parentFolder);

					try(ResultSet resultSet = pstatement.executeQuery()){
						if(!resultSet.next()) {
							throw new NoPathException("Input path doesn't exist.");
						}
						parentFolder = resultSet.getInt("folderId");
					}
				}
			}
		}

		//Check there are no other documents with the same name and same type in the folder
		try(PreparedStatement pstatement = connection.prepareStatement(query4)){
			pstatement.setInt(1, document.getOwner());
			pstatement.setString(2, document.getName());
			pstatement.setInt(3, parentFolder);
			pstatement.setString(4, document.getType());
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					throw new SameFileNameException("There is already a document with the same name and type in the folder.");
				}
				document.setFolderLocation(parentFolder);
			}
		}
	}
	
	public List<Document> getDocumentsByFolderId(int userId, int folderId) throws SQLException{
		List<Document> documents = new ArrayList<>();

		String query = "SELECT * FROM document WHERE owner = ? AND folderLocation = ? ORDER BY name ASC";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			pstatement.setInt(2, folderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				while(resultSet.next()) {
					Document document = new Document();
					document.setDocumentId(resultSet.getInt("DocumentId"));
					document.setOwner(resultSet.getInt("Owner"));
					document.setName(resultSet.getString("Name"));
					document.setDate(resultSet.getDate("Date"));
					document.setType(resultSet.getString("Type"));
					document.setFolderLocation(resultSet.getInt("FolderLocation"));
					documents.add(document);
				}
			}
		}
		return documents;
	}
	
	public Document getDocumentById(int userId, int documentId) throws SQLException, NoAccessException{
		String query = "SELECT * FROM document WHERE owner = ? AND documentId = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			pstatement.setInt(2, documentId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					Document document = new Document();
					document.setDocumentId(documentId);
					document.setName(resultSet.getString("Name"));
					document.setDate(resultSet.getDate("Date"));
					document.setType(resultSet.getString("Type"));
					document.setOwner(resultSet.getInt("Owner"));
					document.setSummary(resultSet.getString("Summary"));
					document.setFolderLocation(resultSet.getInt("FolderLocation"));
					return document;
				}
				else {
					throw new NoAccessException("You don't have permissions to access this document.");
				}
			}
		}
	}
	
	public void moveDocument(Document document, int folderId) throws SQLException, SameFileNameException{
		//Check if there's another document with same name and type in the location first
		String check = "SELECT 1 FROM document WHERE folderLocation = ? AND name = ? AND type = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(check)){
			pstatement.setInt(1, folderId);
			pstatement.setString(2, document.getName());
			pstatement.setString(3, document.getType());
			try (ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					throw new SameFileNameException("There is already a document with the same name and type in the folder destination.");
				}
			}
		}
		
		String query = "UPDATE document SET folderLocation = ? WHERE documentId = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, folderId);
			pstatement.setInt(2, document.getDocumentId());
			int rowsUpdated = pstatement.executeUpdate();
			
			if(rowsUpdated > 0) {
				return;
			}
			else {
				throw new SQLException();
			}
		}
	}
}


