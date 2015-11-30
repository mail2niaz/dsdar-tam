package com.dsdar.thosearoundme.dto;

import android.database.Cursor;
import android.net.Uri;
import android.widget.QuickContactBadge;

public class ContactDto {
	private String name;
	private String phone;
	private String type;
	private boolean value;
	private QuickContactBadge qcb;
	private Uri contactUri;
	private Cursor cursor;
	private String photo;

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public Cursor getCursor() {
		return cursor;
	}

	public void setCursor(Cursor cursor) {
		this.cursor = cursor;
	}

	public Uri getContactUri() {
		return contactUri;
	}

	public void setContactUri(Uri contactUri) {
		this.contactUri = contactUri;
	}

	public QuickContactBadge getQcb() {
		return qcb;
	}

	public void setQcb(QuickContactBadge qcb) {
		this.qcb = qcb;
	}

	public boolean isValue() {
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}