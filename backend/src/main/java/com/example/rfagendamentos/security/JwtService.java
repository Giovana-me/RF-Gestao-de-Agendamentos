package com.example.rfagendamentos.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String gerarToken(String email, Long versaoToken) {
        return Jwts.builder()
                .subject(email)
                .claim("versaoToken", versaoToken)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey())
                .compact();
    }

    public Long extrairVersaoToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("versaoToken", Long.class);
    }

    public String extrairEmail(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean tokenValido(String token, String email, Long versaoTokenAtual) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String emailToken = claims.getSubject();
        Long versaoToken = claims.get("versaoToken", Long.class);
        Date expiracao = claims.getExpiration();

        return emailToken != null
                && emailToken.equals(email)
                && versaoToken != null
                && versaoToken.equals(versaoTokenAtual)
                && expiracao != null
                && expiracao.after(new Date());
    }
}
