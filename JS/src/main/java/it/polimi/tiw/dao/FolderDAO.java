package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.SameFileNameException;

//TODO change the logic in a way that all users have a root folder
public class FolderDAO {
	private Connection connection;

	public FolderDAO(Connection connection) {
		this.connection = connection;
	}
	
	public Folder getRootFolder(int userId) throws SQLException{
		String query = "SELECT * FROM folder WHERE owner = ? AND parentFolder IS NULL";
		Folder folder = new Folder();
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				while(resultSet.next()) {
					folder.setFolderId(resultSet.getInt("folderId"));
					folder.setOwner(resultSet.getInt("owner"));
					folder.setName(resultSet.getString("name"));
					folder.setDate(resultSet.getDate("date"));
					folder.setParentFolder(null);
				}
			}
		}
		return folder;
	}
	
	public void setSubFolders(int userId, List<Folder> parentFolders) throws SQLException{
		String query = "SELECT * FROM folder WHERE owner = ? AND parentFolder = ? ORDER BY name ASC";
		
		for(Folder parentFolder : parentFolders) {
			//Adding documents to folder logic, folder bean has the documents property, it can hold documents!
			DocumentDAO documentDao = new DocumentDAO(connection);
			documentDao.addDocumentsToFolder(parentFolder, userId);
			
			//Adding sub folders to folder logic
			try(PreparedStatement pstatement = connection.prepareStatement(query)){
				pstatement.setInt(1, userId);
				pstatement.setInt(2, parentFolder.getFolderId());
				try(ResultSet resultSet = pstatement.executeQuery()){
					while(resultSet.next()) {
						Folder currFolder = new Folder();
						currFolder.setFolderId(resultSet.getInt("folderId"));
						currFolder.setOwner(resultSet.getInt("owner"));
						currFolder.setName(resultSet.getString("name"));
						currFolder.setDate(resultSet.getDate("date"));
						currFolder.setParentFolder(resultSet.getInt("parentFolder"));
						parentFolder.getSubFolders().add(currFolder);
					}
				}
			}
			if(!parentFolder.getSubFolders().isEmpty()) {
				setSubFolders(userId, parentFolder.getSubFolders());
			}
		}
	}
	
	//Creates subfolder in the SQL database
	public void createFolder(Folder folder) throws SQLException{
		String query = "INSERT INTO folder (owner, name, date, parentfolder) VALUES (?, ?, ?, ?)";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, folder.getOwner());
			pstatement.setString(2, folder.getName());
			pstatement.setDate(3, folder.getDate());
			if(folder.getParentFolder() == null) {
				pstatement.setNull(4, java.sql.Types.INTEGER);
			}
			else {
				pstatement.setInt(4, folder.getParentFolder());
			}
			pstatement.executeUpdate();
		}
	}
	
	//Checks if the parent folder has a child with the same folder name
	public void checkParentFolderChildren(Folder folder) throws SQLException, SameFileNameException{
		String query = "SELECT * FROM folder WHERE parentFolder = ?";
		int parentFolderId = folder.getParentFolder();
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, parentFolderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				while(resultSet.next()) {
					if(resultSet.getString("name").equals(folder.getName())) {
						throw new SameFileNameException("There is already a folder with the same name in the parent folder.");
					}
				}
			}
		}	
	}
	
	public void isFolderAccessible(int userId, int folderId) throws SQLException, NoAccessException {
		String query = "SELECT 1 FROM folder WHERE owner = ? AND folderId = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			pstatement.setInt(2, folderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					return;
				}
				else {
					throw new NoAccessException("You don't have permissions to access this folder.");
				}
			}
		}
	}
	
	public Folder getFolderByParameters(String name, int userId, int parentFolder) throws SQLException {
		String query = "SELECT * FROM folder WHERE name = ? AND owner = ? AND parentFolder = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setString(1, name);
			pstatement.setInt(2, userId);
			pstatement.setInt(3, parentFolder);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					Folder folder = new Folder();
					folder.setFolderId(resultSet.getInt("FolderId"));
					folder.setDate(resultSet.getDate("Date"));
					folder.setName(resultSet.getString("Name"));
					folder.setOwner(resultSet.getInt("Owner"));
					folder.setParentFolder(resultSet.getInt("ParentFolder"));
					return folder;
				}
				else {
					throw new SQLException("No folder was found with such parameters");
				}
			}
		}
	}
	
	
	public Folder getFolderById(int userId, int folderId) throws SQLException, NoAccessException{
		String query = "SELECT * FROM folder WHERE owner = ? AND folderId = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			pstatement.setInt(2, folderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					Folder folder = new Folder();
					folder.setFolderId(folderId);
					folder.setDate(resultSet.getDate("Date"));
					folder.setParentFolder(resultSet.getInt("ParentFolder"));
					folder.setName(resultSet.getString("Name"));
					folder.setOwner(resultSet.getInt("Owner"));
					return folder;
				}
				else {
					throw new NoAccessException("You don't have permissions to access this folder.");
				}
			}
		}
	}
	
	public boolean isFolderRoot(int userId, int folderId) throws SQLException {
		String query = "SELECT 1 FROM folder WHERE owner = ? AND folderId = ? AND parentFolder IS NULL";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			pstatement.setInt(2, folderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					return true;
				}
				else {
					return false;
				}
			}
		}
	}
	
	public void deleteFolder(int folderId) throws SQLException {
		String update = "DELETE FROM folder WHERE folderId = ?";
        try (PreparedStatement pstatement = connection.prepareStatement(update)) {
            pstatement.setInt(1, folderId);
            int affectedRows = pstatement.executeUpdate();
			if(affectedRows == 0) {
				throw new SQLException();
			}
        }
	}
}

