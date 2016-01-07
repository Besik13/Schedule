package com.schedule.risebes.schedule.server;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class ServerCommand {

    public static boolean isAvailable() {
        boolean res = false;
        try {
            res = (boolean) new AsyncTask() {
                public boolean connect;

                @Override
                protected Boolean doInBackground(Object[] params) {
                    try {
                        Socket s = new Socket(ServerSettings.SERVER_ADDRESS, ServerSettings.TCP_SERVER_PORT);
                        if (s.isConnected()) {
                            return true;
                        } else {
                            return false;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                }
            }.execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return res;
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
