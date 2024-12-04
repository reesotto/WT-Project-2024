package it.polimi.tiw.beans;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class Folder {
	private int folderId;
	private int owner;
	private Date date;
	private Integer parentFolder;
	private String name;
	private List<Folder> subFolders;
	private List<Document> documents;
	
	public Folder() {
		this.subFolders = new ArrayList<>();
		this.documents = new ArrayList<>();
	}
	
	public int getFolderId() {
		return this.folderId;
	}
	
	//Owner is identified by its ID
	public int getOwner() {
		return this.owner;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	//Changed from int to Integer for the null property
	public Integer getParentFolder() {
		return this.parentFolder;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<Folder> getSubFolders(){
		return this.subFolders;
	}
	
	public List<Document> getDocuments(){
		return this.documents;
	}
	
	public void setFolderId(int folderId) {
		this.folderId = folderId;
	}
	
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setParentFolder(Integer parentFolder) {
		this.parentFolder = parentFolder;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setSubFolders(List<Folder> subFolders) {
		this.subFolders = subFolders;
	}
	
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}
	
}
