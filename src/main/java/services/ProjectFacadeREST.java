package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repository.Project;
import controller.ProjectController;

@Stateless
@Path("repository.project")
public class ProjectFacadeREST extends AbstractFacade<Project> {

    @PersistenceContext(unitName = "com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU");
    private EntityManager em = emf.createEntityManager();

    // Constructor que inicializa el servicio para manejo de proyectos
    public ProjectFacadeREST() {
        super(Project.class);
    }

    // Endpoint REST para listar proyectos de un empleado específico
    @GET
    @Path("listarPorEmpleado")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listarPorEmpleado(@QueryParam("employeeId") Long employeeId, 
                                   @CookieParam("session_token") String sessionToken) {
        try {
            ProjectController controller = new ProjectController(em);
            String result = controller.listarPorEmpleado(employeeId, sessionToken);
            
            return Response.ok()
                .entity(result)
                .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}")
                .build();
        }
    }

    // Endpoint REST para crear un nuevo proyecto en la base de datos
    @POST
    @Path("crear")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response crear(Project entity, @CookieParam("session_token") String sessionToken) {
        try {
            ProjectController controller = new ProjectController(em);
            String result = controller.crear(entity, sessionToken);
            
            return Response.ok()
                .entity(result)
                .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error al crear proyecto: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}")
                .build();
        }
    }
    
    // Endpoint REST para realizar búsqueda avanzada de proyectos con múltiples filtros
    @GET
    @Path("busquedaAvanzada")
    @Produces({MediaType.APPLICATION_JSON})
    public Response busquedaAvanzada(@QueryParam("employeeId") Long employeeId,
            @QueryParam("nombreProyecto") String nombreProyecto,
            @QueryParam("nombreCategoria") String nombreCategoria,
            @QueryParam("codigoCategoria") String codigoCategoria,
            @QueryParam("nombreEstado") String nombreEstado,
            @QueryParam("fechaInicio") String fechaInicio,
            @QueryParam("fechaFinalizacion") String fechaFinalizacion,
            @CookieParam("session_token") String sessionToken) {
        try {
            ProjectController controller = new ProjectController(em);
            String result = controller.busquedaAvanzada(employeeId, nombreProyecto, nombreCategoria, 
                                                      codigoCategoria, nombreEstado, fechaInicio, 
                                                      fechaFinalizacion, sessionToken);
            
            return Response.ok()
                .entity(result)
                .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}")
                .build();
        }
    }

    // Método que retorna el EntityManager para operaciones de base de datos
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
