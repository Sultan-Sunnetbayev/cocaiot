package tm.salam.cocaiot.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.lifetime}")
    private Duration jwtLifetime;

    private final UserDetailsService userDetailsService;

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(final UUID uuid, final String email, final String role){

        Map<String, Object>claims=new HashMap<>();
        final Date issuedDate=new Date();
        final Date expiredDate=new Date(issuedDate.getTime() + jwtLifetime.toMillis());

        claims.put("uuid", uuid);
        claims.put("email", email);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(issuedDate)
                .setExpiration(expiredDate)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
    public String getUserEmail(final String token) throws JwtException{

        return getAllClaimsFromToken(token).getSubject();
    }

    public String getUserRoleName(final String token){

        return getAllClaimsFromToken(token).get("role", String.class);
    }

    private Claims getAllClaimsFromToken(final String token) throws JwtException {

        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getToken(HttpServletRequest request){

        String bearerToken=request.getHeader("Authorization");

        if(bearerToken!=null && bearerToken.startsWith("Bearer_")){

            return bearerToken.substring(7);
        }

        return null;
    }

    public boolean validateToken(final String token){

        try {

            return !getAllClaimsFromToken(token).getExpiration().before(new Date());
        }catch (ExpiredJwtException expiredJwtException){
            expiredJwtException.printStackTrace();

            return false;
        }
    }

    public Authentication getAuthentication(final String token){

        UserDetails userDetails=userDetailsService.loadUserByUsername(getUserEmail(token));

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());

    }

}
