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

    public void setRefreshTokenCookie(HttpServletResponse response,String refreshToken){
        Cookie cookie=new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(refreshCookieMaxAge);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }

    String cookieSameSiteAttribute = Constants.SAME_SITE_ATTRIBUTE;
    public void setAccessTokenCookie(HttpServletResponse response, String accessToken){
        Cookie cookie =new Cookie("accessToken", accessToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(accessCookieMaxAge);
        cookie.setAttribute("SameSite", "None");
        response.addCookie(cookie);
    }



    public void clearAuthCookies(HttpServletResponse response) {
       Cookie access=new Cookie("accessToken", null);
        access.setMaxAge(0);
        access.setHttpOnly(true);
        access.setSecure(true);
        access.setPath("/");
        access.setAttribute("SameSite", "None");

        Cookie refresh = new Cookie("refreshToken", null);
        refresh.setMaxAge(0);
        refresh.setHttpOnly(true);
        refresh.setSecure(true);
        refresh.setPath("/");
        refresh.setAttribute("SameSite", "None");

        response.addCookie(access);
        response.addCookie(refresh);
    }
}
