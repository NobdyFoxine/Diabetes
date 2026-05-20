package com.antigravity.diabetes.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "Antigravity2026!@#";
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24 hours

    public static String generateToken(Long userId, String username, Integer roleType) {
        Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET);
        return JWT.create()
                .withClaim("userId", userId)
                .withClaim("username", username)
                .withClaim("roleType", roleType)
                .withExpiresAt(date)
                .sign(algorithm);
    }

    public static DecodedJWT verifyToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(SECRET);
            return JWT.require(algorithm).build().verify(token);
        } catch (Exception e) {
            return null;
        }
    }
}
