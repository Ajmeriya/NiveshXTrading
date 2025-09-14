package com.nivesh.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;


/**This will check token every time when it requestes
 * 1. we extract the header
 * 2. we check is valid or not
 * 3. we extract the TOKEN
 * 4. generate the secret key
 * 5. using Cliams we authenticate the token
 * 6. convert authorities into SimpleGrantedAuthority
 * 7. create the authentication object of user
 * 8. set the authentication to the Spring Securiety
 * 9. after this allow the request to pass if token is correct */

public class JwtTokenValidator extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = request.getHeader(JwtConstant.JWT_HEADER);

        //'Bearer Token'
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);

            try {
                SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRETE_KEY.getBytes());

                Claims claims = Jwts.parser()          // ✅ not parserBuilder()
                        .verifyWith(key)               // provide signing key
                        .build()
                        .parseSignedClaims(jwt)        // parse signed claims (for JWS)
                        .getPayload();                 // get the Claims


                // If you want parseClaimsJws, you can:
                // Claims claims = Jwts.parserBuilder()
                //         .setSigningKey(key)
                //         .build()
                //         .parseClaimsJws(jwt)
                //         .getBody();

                String email = String.valueOf(claims.get("email"));
                String authorities = String.valueOf(claims.get("authorities"));

                List<GrantedAuthority> authoritiesList =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                Authentication auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authoritiesList
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                throw new RuntimeException("Invalid Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}
