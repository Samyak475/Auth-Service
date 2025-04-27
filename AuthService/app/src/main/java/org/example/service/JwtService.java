package org.example.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtService {
    private static final String SECRET ="e9efba493a6a8feedd20101cd4aa0559734c1ba59c28e8817a3eab7aebbd59ef09e5e53a53f06786d2fd407125d13875b515c9766b91543f0ebeec9f72e6bad7d400ae042cd51b98d39f72dc0ccb6f8685159163c94e5c4f9ee598593c93b092c7109eb73fd2b5725fa0cbdfa1980e546dc035b39962e48859caf4362dd89dca8bd8b3bdd3b12276668e6f031c95bee3c999386b8c8b2bc2a9f36df28bc5f5e61965a7c0bc5c3f775814651c386faf050f74f3f81c90c9965c1efac633f6c30ba1cc8d3ab60dced4687743bfce1a01a66fe19d009dd280115e320326f33ac978be1445f3005164d7874e357a171fd9bce2c9547f12587caa7bca7332e2a2fb00";


    private <T> T extractClaims(String token , Function<Claims, T>claimsResolver){
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(){
        byte[] keys = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keys);
    }

    public String extractuserName(String token){
        return extractClaims(token , Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaims(token , Claims::getExpiration);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails){
        String userName = extractuserName(token);
        return (userName.equalsIgnoreCase(userDetails.getUsername()) && !isTokenExpired(token));
    }
    public  String generateToken(String username){
        Map<String , Object> claims = new HashMap<>();
        return createToken(claims,username);
    }
    public String createToken(Map<String , Object>claims ,String username ){
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+10*60*1))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
    }

}

