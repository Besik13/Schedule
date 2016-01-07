package com.schedule.risebes.schedule.rest;

import java.io.Serializable;

/**
 * Created by besik on 06.01.16.
 */
public class NewUser{
    private String email;
    private String password;
    private String name;
    private String surename;
    private int group;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getSurename() {
        return surename;
    }

    public int getGroup() {
        return group;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurename(String surename) {
        this.surename = surename;
    }

    public void setGroup(int group) {
        this.group = group;
    }
}
