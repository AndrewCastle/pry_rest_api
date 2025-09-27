package services;

import java.security.SecureRandom;
import java.util.Base64;


public class PasswordUtils {
    
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    // Método para cifrar contraseña con salt aleatorio y hash SHA-256
    public static String hashPassword(String password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            
            String passwordWithSalt = password + saltBase64;
            String hashedPassword = sha256(passwordWithSalt);
            
            return saltBase64 + ":" + hashedPassword;
            
        } catch (Exception e) {
            System.err.println("Error cifrando contraseña: " + e.getMessage());
            throw new RuntimeException("Error cifrando contraseña", e);
        }
    }
    
    // Método para verificar si una contraseña coincide con su hash almacenado
    public static boolean verifyPassword(String password, String hashedPassword) {
        try {
            if (password == null || hashedPassword == null) {
                return false;
            }
            
            String[] parts = hashedPassword.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            String salt = parts[0];
            String storedHash = parts[1];
            
            String passwordWithSalt = password + salt;
            String hashedInput = sha256(passwordWithSalt);
            
            return storedHash.equals(hashedInput);
            
        } catch (Exception e) {
            System.err.println("Error verificando contraseña: " + e.getMessage());
            return false;
        }
    }
    
    // Método privado para generar hash SHA-256 de una cadena
    private static String sha256(String input) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance(ALGORITHM);
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (Exception e) {
            throw new RuntimeException("Error generando hash SHA-256", e);
        }
    }
    
    // Método para generar contraseña aleatoria con caracteres seguros
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    // Método para validar si una contraseña cumple con criterios de seguridad
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
}
