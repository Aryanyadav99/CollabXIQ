package com.collabskill.collabxskill.security;

import com.collabskill.collabxskill.extra.Constants;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthCookieUtil {

    @Value("${security.cookie.access-max-age}")
    private int accessCookieMaxAge;

    @Value("${security.cookie.refresh-max-age}")
    private int refreshCookieMaxAge;

    String cookieSameSiteAttribute = Constants.SAME_SITE_ATTRIBUTE;
    public void setAccessTokenCookie(HttpServletResponse response, String accessToken){
        Cookie cookie =new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(accessCookieMaxAge);
        cookie.setAttribute("SameSite", cookieSameSiteAttribute);
        response.addCookie(cookie);
    }

    public void setRefreshCookie(HttpServletResponse response,String refreshToken){
        Cookie cookie=new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/users");
        cookie.setMaxAge(refreshCookieMaxAge);

        cookie.setAttribute("SameSite", cookieSameSiteAttribute);
        response.addCookie(cookie);
    }

    public void clearAuthCookies(HttpServletResponse response) {
       Cookie access=new Cookie("accessToken", null);
        access.setMaxAge(0);
        access.setHttpOnly(true);
        access.setSecure(true);
        access.setPath("/");
        access.setAttribute("SameSite", cookieSameSiteAttribute);

        Cookie refresh = new Cookie("refreshToken", null);
        refresh.setMaxAge(0);
        refresh.setHttpOnly(true);
        refresh.setSecure(true);
        refresh.setPath("/api/users");
        refresh.setAttribute("SameSite", cookieSameSiteAttribute);

        response.addCookie(access);
        response.addCookie(refresh);
    }
}
