package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repository.Employee;
import controller.EmployeeController;

@Stateless
@Path("repository.employee")
public class EmployeeFacadeREST extends AbstractFacade<Employee> {

    private EntityManagerFactory emf;
    private EntityManager em;

    // Constructor que inicializa el servicio para manejo de empleados
    public EmployeeFacadeREST() {
        super(Employee.class);
        initializeEntityManager();
    }
    
    // Método privado para inicializar y configurar el EntityManager
    private void initializeEntityManager() {
        try {
            if (emf == null) {
                emf = Persistence.createEntityManagerFactory("com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU");
            }
            if (em == null || !em.isOpen()) {
                em = emf.createEntityManager();
            }
        } catch (Exception e) {
            System.err.println("Error inicializando EntityManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Endpoint REST para obtener el perfil completo de un empleado específico
    @GET
    @Path("perfilPorEmpleado")
    @Produces({MediaType.APPLICATION_JSON})
    public Response perfilPorEmpleado(@QueryParam("employeeId") Long employeeId, 
                                     @CookieParam("session_token") String sessionToken) {
        try {
            EmployeeController controller = new EmployeeController(em);
            String result = controller.perfilPorEmpleado(employeeId, sessionToken);
            
            return Response.ok()
                .entity(result)
                .build();
                
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage() != null ? e.getMessage() : "Error desconocido") + "\"}")
                .build();
        }
    }

    // Método que retorna el EntityManager, lo inicializa si es necesario
    @Override
    protected EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            initializeEntityManager();
        }
        return em;
    }
    
}
