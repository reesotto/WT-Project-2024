package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.exceptions.NoAccessException;
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

	public void checkDocumentFolderChildren(Document document) throws SQLException, SameFileNameException{
		String query = "SELECT * FROM document WHERE name = ? AND folderLocation = ? AND type = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, document.getName());
			pstatement.setInt(2, document.getFolderLocation());
			pstatement.setString(3, document.getType());
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					throw new SameFileNameException("There is already a document with the same name and type in the folder.");
				}
				return;
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
	
	public void addDocumentsToFolder(Folder folder, int userId)throws SQLException{
		String query = "SELECT * FROM document WHERE folderLocation = ? AND owner = ? ORDER BY name ASC";
		int folderId = folder.getFolderId();
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, folderId);
			pstatement.setInt(2, userId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				while(resultSet.next()) {
					Document document = new Document();
					document.setDocumentId(resultSet.getInt("DocumentId"));
					document.setName(resultSet.getString("Name"));
					document.setDate(resultSet.getDate("Date"));
					document.setType(resultSet.getString("Type"));
					document.setOwner(resultSet.getInt("Owner"));
					document.setSummary(resultSet.getString("Summary"));
					document.setFolderLocation(resultSet.getInt("FolderLocation"));
					
					folder.getDocuments().add(document);
				}
			}
		}
	}
	
	public Document getDocumentByParameters(String name, String type, int userId, int folderLocation) throws SQLException {
		String query = "SELECT * FROM document WHERE name = ? AND type = ? AND owner = ? AND folderLocation = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, name);
			pstatement.setString(2, type);
			pstatement.setInt(3, userId);
			pstatement.setInt(4, folderLocation);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					Document document = new Document();
					document.setDocumentId(resultSet.getInt("DocumentId"));
					document.setOwner(resultSet.getInt("Owner"));
					document.setName(resultSet.getString("Name"));
					document.setDate(resultSet.getDate("Date"));
					document.setType(resultSet.getString("Type"));
					document.setFolderLocation(resultSet.getInt("FolderLocation"));
					document.setSummary(resultSet.getString("Summary"));
					return document;
				}
				else {
					throw new SQLException("No document was found with such parameters");
				}
			}
		}
	}
	
	public void isDocumentAccessible(int userId, int documentId) throws SQLException, NoAccessException{
		String query = "SELECT 1 FROM document WHERE documentId = ? AND owner = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, documentId);
			pstatement.setInt(2, userId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(!resultSet.next()) {
					throw new NoAccessException("You don't have permissions to access this document.");
				}
				return;
			}
		}
	}
	
	public void moveDocument(int documentId, int folderId) throws SQLException, SameFileNameException{
		String query1 = "SELECT * FROM document WHERE documentId = ?";
		String query2 = "SELECT * FROM document WHERE folderLocation = ?";
		Document document = new Document();
		try(PreparedStatement pstatement = connection.prepareStatement(query1)){
			pstatement.setInt(1, documentId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					document.setName(resultSet.getString("Name"));
					document.setType(resultSet.getString("Type"));
				}
			}
		}
		
		try(PreparedStatement pstatement = connection.prepareStatement(query2)){
			pstatement.setInt(1, folderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				while(resultSet.next()) {
					if(document.getName().equals(resultSet.getString("Name")) && 
							document.getType().equals(resultSet.getString("Type"))) {
						throw new SameFileNameException("Folder destination already contains file with same name and format");
					}
				}
			}
		}
		
		String update = "UPDATE document SET folderLocation = ? WHERE documentId = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(update)){
			pstatement.setInt(1, folderId);
			pstatement.setInt(2, documentId);
			
			int affectedRows = pstatement.executeUpdate();
			if(affectedRows == 0) {
				throw new SQLException();
			}
		}
	}
	
	public void deleteDocument(int documentId) throws SQLException {
		String update = "DELETE FROM document WHERE documentId = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(update)){
			pstatement.setInt(1, documentId);
			int affectedRows = pstatement.executeUpdate();
			if(affectedRows == 0) {
				throw new SQLException();
			}
		}	
	}
	
}


