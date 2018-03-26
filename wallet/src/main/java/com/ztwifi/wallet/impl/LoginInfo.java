package com.ztwifi.wallet.impl;

public class LoginInfo {
	
	String uid;
	int isLogin;
	String token;
	String expireAt;
	String nick;
	String iconurl;
	int datelevel;
	
	
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public int getIsLogin() {
		return isLogin;
	}
	public void setIsLogin(int isLogin) {
		this.isLogin = isLogin;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getExpireAt() {
		return expireAt;
	}
	public void setExpireAt(String expireAt) {
		this.expireAt = expireAt;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getIconurl() {
		return iconurl;
	}
	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}
	public int getDatelevel() {
		return datelevel;
	}
	public void setDatelevel(int datelevel) {
		this.datelevel = datelevel;
	}
}
