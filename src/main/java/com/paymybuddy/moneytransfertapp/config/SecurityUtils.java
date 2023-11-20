package com.paymybuddy.moneytransfertapp.config;

import jakarta.servlet.http.HttpServletRequest;

public class SecurityUtils {

    public static boolean isUserLoggedIn(HttpServletRequest request) {
        return request.getSession().getAttribute("userEmail") != null;
    }

    public static void loginUser(HttpServletRequest request, String userEmail) {
        request.getSession().setAttribute("userEmail", userEmail);
    }

    public static void logoutUser(HttpServletRequest request) {
        request.getSession().invalidate();
    }

    public static String getLoggedInUserEmail(HttpServletRequest request) {
        return (String) request.getSession().getAttribute("userEmail");
    }
}
