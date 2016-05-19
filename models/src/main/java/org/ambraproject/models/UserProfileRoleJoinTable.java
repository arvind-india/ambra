package org.ambraproject.models;

import java.io.Serializable;

public class UserProfileRoleJoinTable implements Serializable {

  private Long userRoleID;
  private Long userProfileID;

  public Long getUserRoleID() {
    return userRoleID;
  }

  public void setUserRoleID(Long userRoleID) {
    this.userRoleID = userRoleID;
  }
  public Long getUserProfileID() {
    return userProfileID;
  }

  public void setUserProfileID(Long userProfileID) {
    this.userProfileID = userProfileID;
  }

  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (!this.getClass().equals(obj.getClass())) return false;

    if (this.userRoleID.equals(((UserProfileRoleJoinTable) obj).getUserRoleID()) &&
        this.userProfileID == ((UserProfileRoleJoinTable) obj).getUserProfileID()) {
      return true;
    }
    return false;
  }

  public int hashCode() {
    int tmp = 0;
    tmp = (userRoleID.hashCode() + userProfileID.hashCode());
    return tmp;
  }

  @Override
  public String toString() {
    return "UserProfileRoleJoinTable{" +
        "userRoleID='" + this.getUserRoleID() + '\'' +
        ", userProfileID=" + this.getUserProfileID() +
        '}';
  }

}
