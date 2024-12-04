package it.polimi.tiw.beans;

public class User {
	private int userId;
	private String username;
	private String password;
	private String email;
	
	public User() {
	}
	
	public int getUserId(){
		return this.userId;
	}
	
	public String getUsername(){
		return this.username;
	}
	
	public String getPassword() {
		return this.password;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setUserId(int userId){
		this.userId = userId;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setEmail(String email) {
		this.email= email;
	}
}
