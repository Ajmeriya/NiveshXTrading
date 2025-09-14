package com.nivesh.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


/**This class handles:

 ✅ Creating JWT tokens

 ✅ Extracting email from token

 ✅ Converting authorities (roles) to string

 */
public class JwtProvider {

    ///🔐 Generates a SecretKey from your SECRET_KEY constant.
    private static final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRETE_KEY.getBytes());


    ///📦 Inside generateToken(...), you likely:
    //👉Extract the email
    //👉Extract roles/authorities
    //👉Add issued time and expiry
    //👉Sign with secret key

    ///👉This method creates a JWT token using the user's authentication details
    public static String generateToken(Authentication auth) {

        ///		👉Gets the roles/authorities from the logged-in user and converts them to a comma-separated string.
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populatesAuthorities(authorities);

        // ✅ build token (you can adjust expiry etc.)
        ///	✅ This builds the JWT like this:
        ///		Header: { "alg": "HS256", "typ": "JWT" }
        ///		Payload: contains:
        ///		email: user email
        ///		authorities: e.g., ROLE_ADMIN,ROLE_USER
        ///		iat & exp: issued at, expires at
        ///		Signature: hashed using your secret key
        String jwt= Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 86400000))
                .claim("email",auth.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();

        return jwt;
    }

    /// 👉 Extract the email from the token
    public static String getEmailFromToken(String token) {

        token=token.substring(7);
        Claims claims = Jwts.parser()          // ✅ not parserBuilder()
                .verifyWith(key)               // provide signing key
                .build()
                .parseSignedClaims(token)        // parse signed claims (for JWS)
                .getPayload();                 // get the Claims


        // If you want parseClaimsJws, you can:
        // Claims claims = Jwts.parserBuilder()
        //         .setSigningKey(key)
        //         .build()
        //         .parseClaimsJws(jwt)
        //         .getBody();

        //👉👉👉👉👉In JJWT (the library you’re using), Claims is basically a map of key-value pairs that live inside the JWT payload.


        String email = String.valueOf(claims.get("email"));
        return email;// get the subject (email/username)
    }


    ///     ✅ Converts Spring Security authorities like:
// 			[ROLE_USER , ROLE_ADMIN]

    /// 	To a comma-separated string:
//			"ROLE_USER,ROLE_ADMIN"
    private static String populatesAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auth = new HashSet<>();

        for (GrantedAuthority grantedAuthority : authorities) {
            auth.add(grantedAuthority.getAuthority());
        }

        return String.join(",", auth);
    }
}
