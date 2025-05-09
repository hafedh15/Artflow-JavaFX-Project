package tn.artflow.utils;

import tn.artflow.entities.User;

public class UserSession {
    private static UserSession instance;
    private User user;

    private UserSession(User user) {
        this.user = user;
    }

    // Static method to get the instance
    public static UserSession getInstance(User user) {
        if (instance == null) {
            instance = new UserSession(user);
        }
        return instance;
    }

    // Overloaded to get the current session without passing a user
    public static UserSession getInstance() {
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // To clear session if user logs out
    public void cleanUserSession() {
        user = null;
        instance = null;
    }

}
