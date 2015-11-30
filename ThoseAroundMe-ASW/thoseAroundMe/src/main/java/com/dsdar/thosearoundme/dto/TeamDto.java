package com.dsdar.thosearoundme.dto;

import java.util.List;

public class TeamDto {

	private boolean value;
	private String teamName;
	private String name;
	private String memberId;
	private String teamId;
	private String isSticky;
	private String isOwner;
	private String isStickyOwner;
	private String ownerName;
	private List<TeamCode> teamCodes;

	public List<TeamCode> getTeamCodes() {
		return teamCodes;
	}

	public void setTeamCodes(List<TeamCode> teamCodes) {
		this.teamCodes = teamCodes;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
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

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getIsSticky() {
		return isSticky;
	}

	public void setIsSticky(String isSticky) {
		this.isSticky = isSticky;
	}

	public String getIsOwner() {
		return isOwner;
	}

	public void setIsOwner(String isOwner) {
		this.isOwner = isOwner;
	}

	public String getIsStickyOwner() {
		return isStickyOwner;
	}

	public void setIsStickyOwner(String isStickyOwner) {
		this.isStickyOwner = isStickyOwner;
	}

}