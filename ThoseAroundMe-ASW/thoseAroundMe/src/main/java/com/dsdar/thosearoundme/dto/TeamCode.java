package com.dsdar.thosearoundme.dto;

import java.io.Serializable;

public class TeamCode implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long teamId;
	private long code;
	private String description;

	public long getTeamId() {
		return teamId;
	}

	public void setTeamId(long teamId) {
		this.teamId = teamId;
	}

	public long getCode() {
		return code;
	}

	public void setCode(long code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
