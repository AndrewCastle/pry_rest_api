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
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import repository.Category;
import controller.CategoryController;
import com.google.gson.Gson;

@Stateless
@Path("repository.category")
public class CategoryFacadeREST extends AbstractFacade<Category> {

    @PersistenceContext(unitName = "com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU")
        private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Api_rest_pry_war_1.0-SNAPSHOTPU");
    private EntityManager em = emf.createEntityManager();

    // Constructor que inicializa el servicio para manejo de categorías
    public CategoryFacadeREST() {
        super(Category.class);
    }
    
    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.category/listar

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "success": true,
            "data": [
                {
                    "code": "WEB",
                    "name": "Desarrollo Web",
                    "id": 1
                },
                {
                    "code": "MOBILE",
                    "name": "Desarrollo Móvil",
                    "id": 2
                }
            ]
        }
        NOTA: Este endpoint requiere autenticación mediante cookie de sesión.
        La cookie se establece automáticamente después del login exitoso.
    */
    // Endpoint REST para listar todas las categorías disponibles
    @GET
    @Path("listar")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listar(@CookieParam("session_token") String sessionToken) {
        try {
            CategoryController controller = new CategoryController(em);
            String result = controller.listar(sessionToken);
            
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
