package com.dsdar.thosearoundme.others;

public class Friend {

	String id = null;
	String name = null;
	String url = null;
	String avatar;
	String location;
	boolean selected = false;

	public Friend(String id, String name, String url, boolean selected) {
		super();
		this.id = id;
		this.name = name;
		this.url = url;
		this.selected = selected;
	}

	public Friend(String id, String name, String avatar, String location) {
		super();
		this.id = id;
		this.name = name;
		this.avatar = avatar;
		this.location = location;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url
	 *            the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected
	 *            the selected to set
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
}
