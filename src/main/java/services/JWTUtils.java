package services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

public class JWTUtils {
    
    // Clave secreta para firmar los tokens (en producción debería estar en variables de entorno)
    private static final String SECRET_KEY = "mi_clave_secreta_muy_larga_y_segura_para_jwt_2024";
    
    // Tiempo de expiración del token (24 horas)
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 horas en milisegundos
    
    /**
     * Genera un JWT para un usuario
     * @param userId ID del usuario
     * @param email Email del usuario
     * @param userRole Rol del usuario (opcional)
     * @return Token JWT como String
     */
    public static String generateToken(Long userId, String email, String userRole) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);
        
        // Crear claims (datos del token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", userRole != null ? userRole : "USER");
        claims.put("iat", now.getTime());
        claims.put("exp", expiryDate.getTime());
        
        // Generar el token
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Valida un token JWT
     * @param token Token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public static boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            System.err.println("Error validando token: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Extrae el email del usuario desde el token
     * @param token Token JWT
     * @return Email del usuario o null si el token es inválido
     */
    public static String getEmailFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            System.err.println("Error extrayendo email del token: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Extrae el ID del usuario desde el token
     * @param token Token JWT
     * @return ID del usuario o null si el token es inválido
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            System.err.println("Error extrayendo userId del token: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Verifica si un token ha expirado
     * @param token Token JWT
     * @return true si el token ha expirado, false en caso contrario
     */
    public static boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true; // Si hay error, consideramos el token como expirado
        }
    }
    
    /**
     * Obtiene la clave de firma
     * @return SecretKey para firmar los tokens
     */
    private static SecretKey getSigningKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Extrae el token del header Authorization
     * @param authHeader Header Authorization (formato: "Bearer <token>")
     * @return Token sin el prefijo "Bearer" o null si no es válido
     */
    public static String extractTokenFromHeader(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
