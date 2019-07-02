package com.thuanduong.education.network.Model;

/** Model để hiển thị các Request kết bạn của người dùng
 *
 */
public class FriendRequest {
    String fullname, profileimage;

    public FriendRequest(){

    }

    public FriendRequest(String fullname, String profileimage) {
        this.fullname = fullname;
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
