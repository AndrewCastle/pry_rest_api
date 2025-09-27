package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import repository.Project;
import com.google.gson.Gson;

public class ProjectController {
    
    private EntityManager em;
    
    // Constructor que recibe el EntityManager para operaciones de base de datos
    public ProjectController(EntityManager em) {
        this.em = em;
    }
    
    // Método para listar todos los proyectos de un empleado específico
    public String listarPorEmpleado(Long employeeId, String sessionToken) {
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

            Query query = em.createQuery("SELECT p.id, p.name, s.id, s.name, c.id, c.code " +
                                        "FROM Project p " +
                                        "LEFT JOIN p.statusId s " +
                                        "LEFT JOIN p.categoryId c " +
                                        "WHERE p.employeeId.id = :employeeId");
            query.setParameter("employeeId", employeeId);
            List<Object[]> results = query.getResultList();
            
            List<Object> projects = new ArrayList<>();
            for (Object[] result : results) {
                Map<String, Object> project = new HashMap<>();
                project.put("id", result[0]);
                project.put("name", result[1]);
                
                Map<String, Object> status = new HashMap<>();
                status.put("id", result[2]);
                status.put("name", result[3]);
                project.put("status", status);
                
                Map<String, Object> category = new HashMap<>();
                category.put("id", result[4]);
                category.put("code", result[5]);
                project.put("category", category);
                
                projects.add(project);
            }
            
            Gson gson = new Gson();
            return "{\"success\": true, \"data\":" + gson.toJson(projects) + "}";
            
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}";
        }
    }
    
    // Método para crear un nuevo proyecto en la base de datos
    public String crear(Project entity, String sessionToken) {
        try {
            if (sessionToken == null || sessionToken.trim().isEmpty()) {
                return "{\"success\": false, \"message\": \"Cookie de sesión requerida\"}";
            }
            
            if (sessionToken.split("\\.").length != 3) {
                return "{\"success\": false, \"message\": \"Formato de token inválido\"}";
            }
            
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return "{\"success\": true, \"message\": \"Proyecto creado exitosamente\", \"id\": " + entity.getId() + "}";
            
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return "{\"success\": false, \"message\": \"Error al crear proyecto: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}";
        }
    }
    
    // Método para realizar búsqueda avanzada de proyectos con múltiples filtros opcionales
    public String busquedaAvanzada(Long employeeId, String nombreProyecto, String nombreCategoria, 
                                  String codigoCategoria, String nombreEstado, String fechaInicio, 
                                  String fechaFinalizacion, String sessionToken) {
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
            
            // Construir consulta JPQL dinámica
            StringBuilder jpql = new StringBuilder();
            jpql.append("SELECT p.id, p.name, s.id, s.name, c.id, c.code ");
            jpql.append("FROM Project p ");
            jpql.append("LEFT JOIN p.statusId s ");
            jpql.append("LEFT JOIN p.categoryId c ");
            jpql.append("WHERE (p.employeeId.id = :employeeId OR p.shared = true) ");
            
            // Agregar condiciones dinámicas
            if (nombreProyecto != null && !nombreProyecto.trim().isEmpty()) {
                jpql.append("AND LOWER(p.name) LIKE LOWER(:nombreProyecto) ");
            }
            if (nombreCategoria != null && !nombreCategoria.trim().isEmpty()) {
                jpql.append("AND LOWER(c.name) LIKE LOWER(:nombreCategoria) ");
            }
            if (codigoCategoria != null && !codigoCategoria.trim().isEmpty()) {
                jpql.append("AND LOWER(c.code) LIKE LOWER(:codigoCategoria) ");
            }
            if (nombreEstado != null && !nombreEstado.trim().isEmpty()) {
                jpql.append("AND LOWER(s.name) LIKE LOWER(:nombreEstado) ");
            }
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                jpql.append("AND p.startDate >= :fechaInicio ");
            }
            if (fechaFinalizacion != null && !fechaFinalizacion.trim().isEmpty()) {
                jpql.append("AND p.endDate <= :fechaFinalizacion ");
            }
            
            Query query = em.createQuery(jpql.toString());
            
            // Establecer parámetro obligatorio
            query.setParameter("employeeId", employeeId);

            // Establecer parámetros opcionales
            if (nombreProyecto != null && !nombreProyecto.trim().isEmpty()) {
                query.setParameter("nombreProyecto", "%" + nombreProyecto + "%");
            }
            if (nombreCategoria != null && !nombreCategoria.trim().isEmpty()) {
                query.setParameter("nombreCategoria", "%" + nombreCategoria + "%");
            }
            if (codigoCategoria != null && !codigoCategoria.trim().isEmpty()) {
                query.setParameter("codigoCategoria", "%" + codigoCategoria + "%");
            }
            if (nombreEstado != null && !nombreEstado.trim().isEmpty()) {
                query.setParameter("nombreEstado", "%" + nombreEstado + "%");
            }
            if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
                query.setParameter("fechaInicio", java.sql.Date.valueOf(fechaInicio));
            }
            if (fechaFinalizacion != null && !fechaFinalizacion.trim().isEmpty()) {
                query.setParameter("fechaFinalizacion", java.sql.Date.valueOf(fechaFinalizacion));
            }
            
            List<Object[]> results = query.getResultList();
            List<Object> projects = new ArrayList<>();

            for (Object[] result : results) {
                Map<String, Object> project = new HashMap<>();
                project.put("id", result[0]);
                project.put("name", result[1]);

                Map<String, Object> status = new HashMap<>();
                status.put("id", result[2]);
                status.put("name", result[3]);
                project.put("status", status);

                Map<String, Object> category = new HashMap<>();
                category.put("id", result[4]);
                category.put("code", result[5]);
                project.put("category", category);

                projects.add(project);
            }

            Gson gson = new Gson();
            return "{\"success\": true, \"data\":" + gson.toJson(projects) + "}";
            
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}";
        }
    }
}
