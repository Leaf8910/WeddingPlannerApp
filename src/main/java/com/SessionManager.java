package com;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

/**
 * A comprehensive singleton class to manage the user session across the application.
 * This allows different controllers to access the currently logged-in user and session data.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private LocalDateTime loginTime;
    private LocalDateTime lastActivityTime;
    private Map<String, Object> sessionAttributes;
    private Preferences userPreferences;
    private boolean rememberMe;
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    // Private constructor for singleton pattern
    private SessionManager() {
        sessionAttributes = new HashMap<>();
        userPreferences = Preferences.userNodeForPackage(SessionManager.class);
    }

    /**
     * Get the singleton instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Set the current logged-in user
     * @param user The user to set as the current user
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        this.lastActivityTime = LocalDateTime.now();

        if (user != null && rememberMe) {
            // Save the username for "remember me" functionality
            userPreferences.put("rememberedUsername", user.getUsername());
        }
    }

    /**
     * Get the current logged-in user
     * @return The current user or null if no user is logged in
     */
    public User getCurrentUser() {
        updateLastActivityTime();
        return currentUser;
    }

    /**
     * Check if a user is logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        if (currentUser == null) {
            return false;
        }

        // Check if session has timed out
        if (isSessionTimedOut()) {
            logout();
            return false;
        }

        updateLastActivityTime();
        return true;
    }

    /**
     * Get the duration of the current session in minutes
     * @return The number of minutes since login, or -1 if not logged in
     */
    public long getSessionDurationMinutes() {
        if (currentUser == null || loginTime == null) {
            return -1;
        }

        updateLastActivityTime();
        return java.time.Duration.between(loginTime, LocalDateTime.now()).toMinutes();
    }

    /**
     * Log out the current user
     */
    public void logout() {
        if (!rememberMe) {
            userPreferences.remove("rememberedUsername");
        }

        currentUser = null;
        loginTime = null;
        lastActivityTime = null;
        sessionAttributes.clear();
    }

    /**
     * Set a session attribute
     * @param key The key for the attribute
     * @param value The value to store
     */
    public void setAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
        updateLastActivityTime();
    }

    /**
     * Get a session attribute
     * @param key The key for the attribute
     * @return The stored value, or null if not found
     */
    public Object getAttribute(String key) {
        updateLastActivityTime();
        return sessionAttributes.get(key);
    }

    /**
     * Remove a session attribute
     * @param key The key for the attribute to remove
     */
    public void removeAttribute(String key) {
        sessionAttributes.remove(key);
        updateLastActivityTime();
    }

    /**
     * Enable or disable "remember me" functionality
     * @param remember true to remember the current user, false otherwise
     */
    public void setRememberMe(boolean remember) {
        this.rememberMe = remember;

        if (!remember) {
            userPreferences.remove("rememberedUsername");
        } else if (currentUser != null) {
            userPreferences.put("rememberedUsername", currentUser.getUsername());
        }
    }

    /**
     * Get the remembered username (if any)
     * @return The remembered username, or null if none exists
     */
    public String getRememberedUsername() {
        return userPreferences.get("rememberedUsername", null);
    }

    /**
     * Clear all remembered user data
     */
    public void clearRememberedUser() {
        userPreferences.remove("rememberedUsername");
        rememberMe = false;
    }

    /**
     * Get the time of the last user activity
     * @return The LocalDateTime of the last activity
     */
    public LocalDateTime getLastActivityTime() {
        return lastActivityTime;
    }

    /**
     * Update the last activity time to now
     */
    private void updateLastActivityTime() {
        lastActivityTime = LocalDateTime.now();
    }

    /**
     * Check if the current session has timed out
     * @return true if the session has timed out, false otherwise
     */
    private boolean isSessionTimedOut() {
        if (lastActivityTime == null) {
            return false;
        }

        long inactiveMinutes = java.time.Duration.between(lastActivityTime, LocalDateTime.now()).toMinutes();
        return inactiveMinutes >= SESSION_TIMEOUT_MINUTES;
    }

    /**
     * Get minutes until session timeout
     * @return Minutes remaining until timeout, or -1 if no active session
     */
    public long getMinutesUntilTimeout() {
        if (lastActivityTime == null) {
            return -1;
        }

        long inactiveMinutes = java.time.Duration.between(lastActivityTime, LocalDateTime.now()).toMinutes();
        return Math.max(0, SESSION_TIMEOUT_MINUTES - inactiveMinutes);
    }

    /**
     * Reset the session timeout by updating the last activity time
     */
    public void resetSessionTimeout() {
        updateLastActivityTime();
    }
}