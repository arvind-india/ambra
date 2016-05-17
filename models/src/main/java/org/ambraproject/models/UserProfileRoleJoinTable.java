package org.ambraproject.models;

import java.io.Serializable;

public class UserProfileRoleJoinTable implements Serializable {

  private Long userRoleID;
  private Long userProfileID;

  //private UserRole userRole;

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

//  public UserRole getUserRole() {
//    return userRole;
//  }
//
//  public void setUserRole(UserRole userRole) {
//    this.userRole = userRole;
//  }

  @Override
  public String toString() {
    return "UserProfileRoleJoinTable{" +
        "userRoleID='" + userRoleID + '\'' +
        ", userProfileID=" + userProfileID +
        '}';
  }

}
