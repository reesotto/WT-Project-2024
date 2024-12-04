package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import it.polimi.tiw.beans.Folder;
import it.polimi.tiw.exceptions.NoAccessException;
import it.polimi.tiw.exceptions.NoPathException;
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
	
	//Checks if the path given does exist
	public void checkFolderPath(Folder folder, String[] folderPath) throws SQLException, SameFileNameException, NoPathException{
		//If the folder has to be created in the root, check if at least it can be done inside root
		if(folderPath == null) {
			throw new NoPathException("Folder can be created only inside root folder.");
		}
		//If the folder has to be created under another folder, check if the path exists first
		else {
			String query1 = "SELECT 1 FROM folder WHERE owner = ? AND name = ? AND parentFolder IS NULL";
			String query2 = "SELECT folderid FROM folder WHERE owner = ? AND name = ? AND parentFolder IS NULL";
			String query3 = "SELECT folderid FROM folder WHERE owner = ? AND name = ? AND parentFolder = ?";
			String query4 = "SELECT 1 FROM folder WHERE owner = ? AND name = ? AND parentFolder = ?";

			//Folder root check
			try(PreparedStatement pstatement = connection.prepareStatement(query1)){
				pstatement.setInt(1, folder.getOwner());
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
						pstatement.setInt(1, folder.getOwner());
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
						pstatement.setInt(1, folder.getOwner());
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
			
			//Check there are no other folders with the same name under the same parentFolder
			try(PreparedStatement pstatement = connection.prepareStatement(query4)){
				pstatement.setInt(1, folder.getOwner());
				pstatement.setString(2, folder.getName());
				pstatement.setInt(3, parentFolder);
				try(ResultSet resultSet = pstatement.executeQuery()){
					if(resultSet.next()) {
						throw new SameFileNameException("There is already a folder with the same name in the parent folder.");
					}
					folder.setParentFolder(parentFolder);
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
	
	public Folder getFolderById(int userId, int folderId) throws SQLException, NoAccessException{
		//This query is to get the immediate folder
		String query = "SELECT folderId, date, name, owner, parentFolder FROM folder WHERE owner = ? AND folderId = ?";
		//This query is to get the immediate subfolders of the folder
		String query1 = "SELECT * FROM folder WHERE owner = ? AND parentFolder = ?";
		try(PreparedStatement pstatement = connection.prepareStatement(query)){
			pstatement.setInt(1, userId);
			pstatement.setInt(2, folderId);
			try(ResultSet resultSet = pstatement.executeQuery()){
				if(resultSet.next()) {
					Folder folder = new Folder();
					folder.setFolderId(resultSet.getInt("FolderId"));
					folder.setDate(resultSet.getDate("Date"));
					folder.setName(resultSet.getString("Name"));
					folder.setOwner(resultSet.getInt("Owner"));
					int parentFolder = resultSet.getInt("ParentFolder");
					if(resultSet.wasNull()) {
						folder.setParentFolder(null);
					}
					else {
						folder.setParentFolder(parentFolder);
					}
					try(PreparedStatement pstatement1 = connection.prepareStatement(query1)){
						pstatement1.setInt(1, userId);
						pstatement1.setInt(2, folderId);
						try(ResultSet resultSet1 = pstatement1.executeQuery()){
							while(resultSet1.next()) {
								Folder folder1 = new Folder();
								folder1.setFolderId(resultSet1.getInt("FolderId"));
								folder1.setDate(resultSet1.getDate("Date"));
								folder1.setName(resultSet1.getString("Name"));
								folder1.setOwner(resultSet1.getInt("Owner"));
								folder1.setParentFolder(resultSet1.getInt("ParentFolder"));
								folder1.setSubFolders(null);
								folder.getSubFolders().add(folder1);
							}
						}
					}
					return folder;
				}
				else {
					throw new NoAccessException("You don't have permissions to access this folder.");
				}
			}
		}
	}
}

