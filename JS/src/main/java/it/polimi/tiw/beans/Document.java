package it.polimi.tiw.beans;

import java.sql.Date;

public class Document {
	private int documentId;
	private int owner;
	private String name;
	private Date date;
	private String type;
	private int folderLocation;
	private String summary;
	
	public Document() {
		
	}
	
	public int getDocumentId() {
		return this.documentId;
	}
	
	public int getOwner() {
		return this.owner;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Date getDate() {
		return this.date;
	}
	
	public String getType() {
		return this.type;
	}
	
	public int getFolderLocation() {
		return this.folderLocation;
	}
	
	public String getSummary() {
		return this.summary;
	}
	
	public void setDocumentId(int documentId) {
		this.documentId = documentId;
	}
	
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setFolderLocation(int folderLocation) {
		this.folderLocation = folderLocation;
	}
	
	public void setSummary(String summary) {
		this.summary = summary;
	}
}
