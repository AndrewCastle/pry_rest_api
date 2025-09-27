package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repository.Status;
import controller.StatusController;
import com.google.gson.Gson;

@Stateless
@Path("repository.status")
public class StatusFacadeREST extends AbstractFacade<Status> {

    @PersistenceContext(unitName = "com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU");
    private EntityManager em = emf.createEntityManager();

    // Constructor que inicializa el servicio para manejo de estados
    public StatusFacadeREST() {
        super(Status.class);
    }

    // Endpoint REST para listar todos los estados disponibles (catálogo de estados)
    @GET
    @Path("listarSimple")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listarSimple(@CookieParam("session_token") String sessionToken) {
        try {
            StatusController controller = new StatusController(em);
            String result = controller.listarSimple(sessionToken);
            
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
