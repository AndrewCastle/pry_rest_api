package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.google.gson.Gson;

public class StatusController {
    
    private EntityManager em;
    
    // Constructor que recibe el EntityManager para operaciones de base de datos
    public StatusController(EntityManager em) {
        this.em = em;
    }
    
    // Método para listar todos los estados disponibles (catálogo de estados)
    public String listarSimple(String sessionToken) {
        try {
            if (sessionToken == null || sessionToken.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Cookie de sesión requerida\"}";
            }
            
            if (sessionToken.split("\\.").length != 3) {
                return "{\"success\": false, \"message\": \"Formato de token inválido\"}";
            }
            
            Query query = em.createNamedQuery("Status.findIdName");
            List<Object[]> results = query.getResultList();
            List<Object> statusList = new ArrayList<>();
            for (Object[] result : results) {
                Long id = (Long) result[0];
                String name = (String) result[1];
                Map<String, Object> status = new HashMap<>();
                status.put("id", id);
                status.put("name", name);
                statusList.add(status);
            }
            Gson gson = new Gson();
            return "{\"success\": true, \"data\":" + gson.toJson(statusList) + "}";
            
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}";
        }
    }
}
