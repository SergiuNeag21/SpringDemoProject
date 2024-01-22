package socialnetwork.config;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


public class JwtService {
    // The secret key used for signing and verifying JWTs.
    private static final String SECRET_KEY = "IElf6/ReCiW/tbNS34QBV1llO3s4C1AKatv0w40er6POnI8SJg1EijmRMPicU/Fb\n";

    // Extracts the username (subject) from the JWT token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);  // claim is a piece of information
    }

    // A generic method to extract a specific claim from the JWT token using a claimsResolver function.
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // Generates a JWT token with additional claims using the provided UserDetails.
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a JWT token with additional claims using the provided UserDetails.
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder().setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 100 * 60 * 24 * 1000))
            .signWith(getSignInKey(),SignatureAlgorithm.HS256)
            .compact();
        
    };

    // Validates whether a given token is valid.
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);

    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token){ 
        return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    // Retrieves the signing key used for JWT verification. It decodes the base64-encoded secret key.
    private Key getSignInKey(){
        //takes a Base64-encoded string and returns the decoded byte array.  converting it back to its original binary representation
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return (Key) Keys.hmacShaKeyFor(keyBytes);

    }


}
