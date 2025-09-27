package services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence; 
import javax.persistence.Query;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import repository.Appuser;
import repository.Recovery;
import controller.AppuserController;
import com.google.gson.Gson;

@Stateless
@Path("repository.appuser")
public class AppuserFacadeREST extends AbstractFacade<Appuser> {

    private EntityManagerFactory emf;
    private EntityManager em;

    // Constructor que inicializa el EntityManager para operaciones de base de datos
    public AppuserFacadeREST() {
        super(Appuser.class);
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
    

    // Método que retorna el EntityManager, lo inicializa si es necesario
    @Override
    protected EntityManager getEntityManager() {
        if (em == null || !em.isOpen()) {
            initializeEntityManager();
        }
        return em;
    }
    
    // Endpoint REST para autenticar usuario y crear cookie de sesión
    @POST
    @Path("login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(Map<String, Object> loginData) {
        try {
            AppuserController controller = new AppuserController(em);
            String result = controller.login(loginData);
            
            // Si el login es exitoso, crear cookie
            if (result.contains("\"success\": true")) {
                // Extraer token del resultado
                Gson gson = new Gson();
                Map<String, Object> responseMap = gson.fromJson(result, Map.class);
                String jwtToken = (String) responseMap.get("token");
                
                NewCookie sessionCookie = new NewCookie(
                    "session_token",           
                    jwtToken,             
                    "/",                     
                    null,                    
                    "Session Token",   
                    24 * 60 * 60,            
                    false                  
                );
                
                return Response.ok()
                    .entity(result)
                    .cookie(sessionCookie)
                    .build();
            } else {
                // Si hay error, devolver respuesta de error
                return Response.status(Response.Status.BAD_REQUEST)
                    .entity(result)
                    .build();
            }
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}")
                .build();
        }
    }
    
    // Endpoint REST para cerrar sesión e invalidar cookie
    @POST
    @Path("logout")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response logout(Map<String, Object> logoutData,
            @javax.ws.rs.HeaderParam("Authorization") String authHeader) {
        try {
            String token = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
            }
            
            AppuserController controller = new AppuserController(em);
            String result = controller.logout(logoutData, token);
            
            // Crear cookie de invalidación
            NewCookie invalidateCookie = new NewCookie(
                "session_token",         
                "",                     
                "/",                      
                null,                     
                "Session Token",          
                0,                         
                false                      
            );

            return Response.ok()
                .entity(result)
                .cookie(invalidateCookie)
                .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}")
                .build();
        }
    }
    
    // Endpoint REST para solicitar cambio de contraseña (genera token de recuperación)
    @POST
    @Path("solicitarCambioContrasena")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response solicitarCambioContrasena(Map<String, Object> requestData) {
        try {
            AppuserController controller = new AppuserController(em);
            String result = controller.solicitarCambioContrasena(requestData);
            
            return Response.ok()
                .entity(result)
                .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}")
                .build();
        }
    }
    
    // Endpoint REST para cambiar contraseña usando token de recuperación
    @POST
    @Path("cambiarContrasena")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response cambiarContrasena(Map<String, Object> requestData) {
        try {
            AppuserController controller = new AppuserController(em);
            String result = controller.cambiarContrasena(requestData);
            
            return Response.ok()
                .entity(result)
                .build();
            
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity("{\"success\": false, \"message\": \"Error interno del servidor: " + (e.getMessage()) + "\"}")
                .build();
        }
    }
    
}
