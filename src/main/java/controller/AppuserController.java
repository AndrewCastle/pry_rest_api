package controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import repository.Appuser;
import repository.Recovery;
import services.PasswordUtils;
import services.JWTUtils;
import com.google.gson.Gson;

public class AppuserController {
    
    private EntityManager em;
    
    // Constructor que recibe el EntityManager para operaciones de base de datos
    public AppuserController(EntityManager em) {
        this.em = em;
    }
    
    // Método para autenticar usuario con email y contraseña, devuelve JWT token
    public String login(Map<String, Object> loginData) {
        try {
            String email = (String) loginData.get("email");
            String password = (String) loginData.get("password");
            
            if (email == null || email.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"El email es requerido\"}";
            }
            
            if (password == null || password.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"La contraseña es requerida\"}";
            }
            
            Query query = em.createNamedQuery("Appuser.findByEmail");
            query.setParameter("email", email.trim());
            
            @SuppressWarnings("unchecked")
            List<Appuser> users = query.getResultList();
            
            if (users.isEmpty()) {
                return "{\"success\": false, \"message\": \"Credenciales inválidas\"}";
            }
            
            Appuser user = users.get(0);
            
            if (user.getActive() == null || !user.getActive()) {
                return "{\"success\": false, \"message\": \"Usuario inactivo\"}";
            }
            
            if (!PasswordUtils.verifyPassword(password, user.getPassword())) {
                return "{\"success\": false, \"message\": \"Credenciales inválidas\"}";
            }
            
            String userRole = "USER"; 
            String jwtToken = JWTUtils.generateToken(user.getId(), user.getEmail(), userRole);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login exitoso");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("active", user.getActive());
            response.put("loginTime", new Date());
            response.put("token", jwtToken);
            response.put("expiresIn", 24L * 60L * 60L);
            
            Gson gson = new Gson();
            return gson.toJson(response);
            
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}";
        }
    }
    
    // Método para cerrar sesión del usuario
    public String logout(Map<String, Object> logoutData, String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Token de autorización requerido\"}";
            }

            if (token.split("\\.").length != 3) {
                return "{\"success\": false, \"message\": \"Formato de token inválido\"}";
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logout exitoso - Cookie eliminada");
            response.put("logoutTime", new Date());

            Gson gson = new Gson();
            return gson.toJson(response);

        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}";
        }
    }
    
    // Método para solicitar cambio de contraseña, genera token de recuperación
    public String solicitarCambioContrasena(Map<String, Object> requestData) {
        try {
            String email = (String) requestData.get("email");
            
            if (email == null || email.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"El email es requerido\"}";
            }
            
            Query query = em.createNamedQuery("Appuser.findByEmail");
            query.setParameter("email", email.trim());
            
            @SuppressWarnings("unchecked")
            List<Appuser> users = query.getResultList();
            
            if (users.isEmpty()) {
                return "{\"success\": false, \"message\": \"Usuario no encontrado\"}";
            }
            
            Appuser user = users.get(0);
            
            if (user.getActive() == null || !user.getActive()) {
                return "{\"success\": false, \"message\": \"Usuario inactivo\"}";
            }
            
            String recoveryToken = JWTUtils.generateToken(user.getId(), user.getEmail(), "RECOVERY");
            
            em.getTransaction().begin();
            Recovery recovery = new Recovery();
            recovery.setToken(recoveryToken);
            recovery.setCreatedAt(new Date());
            recovery.setExpirationAt(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000));
            recovery.setUsed(false);
            recovery.setStatus("PENDING");
            recovery.setUserId(user);
            em.persist(recovery);
            em.getTransaction().commit();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token de recuperación generado exitosamente");
            response.put("recoveryToken", recoveryToken);
            response.put("expiresIn", 24L * 60L * 60L);
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            
            Gson gson = new Gson();
            return gson.toJson(response);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}";
        }
    }
    
    // Método para cambiar contraseña usando token de recuperación válido
    public String cambiarContrasena(Map<String, Object> requestData) {
        try {
            String recoveryToken = (String) requestData.get("recoveryToken");
            String newPassword = (String) requestData.get("newPassword");
            
            if (recoveryToken == null || recoveryToken.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"El token de recuperación es requerido\"}";
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"La nueva contraseña es requerida\"}";
            }
            
            if (newPassword.length() < 6) {
                return "{\"success\": false, \"message\": \"La contraseña debe tener al menos 6 caracteres\"}";
            }
            
            Query recoveryQuery = em.createNamedQuery("Recovery.findByToken");
            recoveryQuery.setParameter("token", recoveryToken.trim());
            
            @SuppressWarnings("unchecked")
            List<Recovery> recoveries = recoveryQuery.getResultList();
            
            if (recoveries.isEmpty()) {
                return "{\"success\": false, \"message\": \"Token de recuperación no encontrado\"}";
            }
            
            Recovery recovery = recoveries.get(0);
            
            if (recovery.getUsed() != null && recovery.getUsed()) {
                return "{\"success\": false, \"message\": \"Token de recuperación ya fue utilizado\"}";
            }
            
            if (recovery.getExpirationAt() != null && recovery.getExpirationAt().before(new Date())) {
                return "{\"success\": false, \"message\": \"Token de recuperación ha expirado\"}";
            }
            
            if (!"PENDING".equals(recovery.getStatus())) {
                return "{\"success\": false, \"message\": \"Token de recuperación no está activo\"}";
            }
            
            Appuser user = recovery.getUserId();
            
            em.getTransaction().begin();
            String hashedPassword = PasswordUtils.hashPassword(newPassword);
            user.setPassword(hashedPassword);
            em.merge(user);
            
            recovery.setUsed(true);
            recovery.setStatus("USED");
            em.merge(recovery);
            em.getTransaction().commit();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Contraseña cambiada exitosamente");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("changedAt", new Date());
            
            Gson gson = new Gson();
            return gson.toJson(response);
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}";
        }
    }
}