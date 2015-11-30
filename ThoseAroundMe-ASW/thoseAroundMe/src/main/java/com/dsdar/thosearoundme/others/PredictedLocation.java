package com.dsdar.thosearoundme.others;

public class PredictedLocation {
	String friendname = null;
	String friendId = null;
	String friendPredictedLocation = null;

	public PredictedLocation(String friendname, String friendId,
			String friendPredictedLocation) {
		super();
		this.friendname = friendname;
		this.friendId = friendId;
		this.friendPredictedLocation = friendPredictedLocation;
	}

	public String getFriendname() {
		return friendname;
	}

	public void setFriendname(String friendname) {
		this.friendname = friendname;
	}

	public String getFriendId() {
		return friendId;
	}

	public void setFriendId(String friendId) {
		this.friendId = friendId;
	}

	public String getFriendPredictedLocation() {
		return friendPredictedLocation;
	}

	public void setFriendPredictedLocation(String friendPredictedLocation) {
		this.friendPredictedLocation = friendPredictedLocation;
	}

}
