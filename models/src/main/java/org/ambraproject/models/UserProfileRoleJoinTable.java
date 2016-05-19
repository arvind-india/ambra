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

  @Override
  public String toString() {
    return "UserProfileRoleJoinTable{" +
        "userRoleID='" + this.getUserRoleID() + '\'' +
        ", userProfileID=" + this.getUserProfileID() +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UserProfileRoleJoinTable that = (UserProfileRoleJoinTable) o;

    if (getUserRoleID() != null ? !getUserRoleID().equals(that.getUserRoleID()) : that.getUserRoleID() != null)
      return false;
    return !(getUserProfileID() != null ? !getUserProfileID().equals(that.getUserProfileID()) : that.getUserProfileID() != null);

  }

  @Override
  public int hashCode() {
    int result = getUserRoleID() != null ? getUserRoleID().hashCode() : 0;
    result = 31 * result + (getUserProfileID() != null ? getUserProfileID().hashCode() : 0);
    return result;
  }
}
