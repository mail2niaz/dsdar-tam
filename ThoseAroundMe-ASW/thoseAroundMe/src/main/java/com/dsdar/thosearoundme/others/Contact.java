package com.dsdar.thosearoundme.others;

public class Contact {
	private String name = "";
	private String email = "";
	private boolean isChecked = false;
	private int postiton;
	private String phoneNumber="";
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public int getPostiton() {
		return postiton;
	}
	public void setPostiton(int postiton) {
		this.postiton = postiton;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	

}
