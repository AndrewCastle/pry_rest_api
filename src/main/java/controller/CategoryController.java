package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import com.google.gson.Gson;

public class CategoryController {
    
    private EntityManager em;
    
    // Constructor que recibe el EntityManager para operaciones de base de datos
    public CategoryController(EntityManager em) {
        this.em = em;
    }

    // Método para listar todas las categorías disponibles (id, código y nombre)
    public String listar(String sessionToken) {
        try {
            if (sessionToken == null || sessionToken.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Cookie de sesión requerida\"}";
            }
            
            if (sessionToken.split("\\.").length != 3) {
                return "{\"success\": false, \"message\": \"Formato de token inválido\"}";
            }
            
            Query query = em.createQuery("SELECT c.id, c.code, c.name FROM Category c");
            List<Object[]> results = query.getResultList();
            List<Object> categoryList = new ArrayList<>();
            
            for (Object[] result : results) {
                Long id = (Long) result[0];
                String code = (String) result[1];
                String name = (String) result[2];
                Map<String, Object> category = new HashMap<>();
                category.put("id", id);
                category.put("code", code);
                category.put("name", name);
                categoryList.add(category);
            }
            
            Gson gson = new Gson();
            return "{\"success\": true, \"data\":" + gson.toJson(categoryList) + "}";
            
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}";
        }
    }
}
