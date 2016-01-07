package com.schedule.risebes.schedule.server;

public class ServerCommand {

    public static boolean isAvailable() {
        //TODO: Create connectify check
        return true;
    }

    public final static String getLoginString(String email, String password) {
        return ServerSettings.SERVER_URL + "/login?email=" + email + "&" + "password=" + password;
    }

    public final static String getGroupsString() {
        return ServerSettings.SERVER_URL + "/groups";
    }

    public final static String getAddUserString() {
        return ServerSettings.SERVER_URL + "/add_user";
    }


}
