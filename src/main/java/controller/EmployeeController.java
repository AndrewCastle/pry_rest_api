package controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.google.gson.Gson;

public class EmployeeController {
    
    private EntityManager em;
    
    // Constructor que recibe el EntityManager para operaciones de base de datos
    public EmployeeController(EntityManager em) {
        this.em = em;
    }
    
    // Método para obtener el perfil completo de un empleado (datos personales, usuario y rol)
    public String perfilPorEmpleado(Long employeeId, String sessionToken) {
        try {
            if (sessionToken == null || sessionToken.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Cookie de sesión requerida\"}";
            }
            
            if (sessionToken.split("\\.").length != 3) {
                return "{\"success\": false, \"message\": \"Formato de token inválido\"}";
            }
            
            if (employeeId == null) {
                return "{\"success\": false, \"message\": \"employeeId es requerido\"}";
            }
            
            Query query = em.createQuery("SELECT p.id, p.name, s.id, s.name, c.id, c.code "
                    + "FROM Project p "
                    + "LEFT JOIN p.statusId s "
                    + "LEFT JOIN p.categoryId c "
                    + "WHERE p.employeeId.id = :employeeId OR p.shared = true");
            query.setParameter("employeeId", employeeId);
            List<Object[]> results = query.getResultList();
            
            if (results.isEmpty()) {
                return "{\"success\": false, \"message\": \"Empleado no encontrado\"}";
            }
            
            Object[] result = results.get(0);
            
            Map<String, Object> employee = new HashMap<>();
            employee.put("id", result[0]);
            employee.put("firstName", result[1]);
            employee.put("lastName", result[2]);
            employee.put("company", result[3]);
            employee.put("phone", result[4]);
            
            Map<String, Object> user = new HashMap<>();
            user.put("id", result[5]);
            user.put("email", result[6]);
            employee.put("user", user);
            
            Map<String, Object> role = new HashMap<>();
            role.put("id", result[7]);
            role.put("name", result[8]);
            employee.put("role", role);
            
            Gson gson = new Gson();
            return "{\"success\": true, \"data\":" + gson.toJson(employee) + "}";
                
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}";
        }
    }
}
