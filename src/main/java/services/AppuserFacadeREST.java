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

    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.appuser/login

        HEADERS:
        Content-Type: application/json

        BODY (JSON) radioButtoon=raw:
        {
            "email": "admin@pryapi.com",
            "password": "123456"
        }

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "expiresIn": 86400,
            "loginTime": "Sep 27, 2025, 1:19:57 AM",
            "success": true,
            "active": true,
            "message": "Login exitoso",
            "userId": 1,
            "email": "admin@pryapi.com",
            "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVVNFUiIsImV4cCI6MTc1OTA0MDM5NywidXNlcklkIjoxLCJpYXQiOjE3NTg5NTM5OTcsImVt
                      YWlsIjoiYWRtaW5AcHJ5YXBpLmNvbSIsInN1YiI6ImFkbWluQHByeWFwaS5jb20ifQ.kxGNQ6nUzTPtlpGgjNSSyqBL78otyfF-G3cgU_XZbmg"
        }
        NOTA: El token JWT se guarda automáticamente en la cookie 'session_token' para uso en otras APIs que requieren autenticación.
     */
    // Endpoint REST para autenticar usuario y crear cookie de sesión
    @POST
    @Path("login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response login(Map<String, Object> loginData) {
        try {
            AppuserController controller = new AppuserController(em);
            String result = controller.login(loginData);

            // Parsear el JSON para verificar correctamente
            Gson gson = new Gson();
            Map<String, Object> responseMap = gson.fromJson(result, Map.class);
            Boolean success = (Boolean) responseMap.get("success");

            if (success != null && success) {
                // Login exitoso - crear cookie
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
                // Login fallido - devolver error
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
    

    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.appuser/logout

        HEADERS:
        Content-Type: application/json
        Authorization: Bearer {TOKEN_GENERADO}

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
        "logoutTime": "Sep 27, 2025, 1:06:52 AM",
        "success": true,
        "message": "Logout exitoso - Cookie eliminada"
        }
     */
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

    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.appuser/solicitarCambioContrasena

        HEADERS:
        Content-Type: application/json

        BODY (JSON) radioButtoon=raw:
        {
            "email": "admin@pryapi.com"
        }

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "expiresIn": 86400,
            "success": true,
            "recoveryToken": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiUkVDT1ZFUlkiLCJleHAiOjE3NTkwNDE3NjcsInVzZXJJZCI6MSwiaWF0IjoxNzU4OTU1MzY3LCJlbWFpbCI6ImFkbWluQHByeWFwaS5jb20iLCJzdWIiOiJhZG1pbkBwcnlhcGkuY29tIn0.iCDHbdIKOL4LRCdaVkV0R4l9iRQzggmhTAY23Vrmjkw",
            "message": "Token de recuperación generado exitosamente",
            "userId": 1,
            "email": "admin@pryapi.com"
        }
        NOTA: El recoveryToken generado tiene 24 horas de validez y debe ser usado en el endpoint 'cambiarContrasena' para completar el proceso.
     */
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

    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.appuser/cambiarContrasena

        HEADERS:
        Content-Type: application/json

        BODY (JSON) radioButton=raw:
        {
            "recoveryToken": {tokenGenerado},
            "newPassword": "nueva_contraseña_123"
        }

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "success": true,
            "changedAt": "Sep 27, 2025, 1:57:35 AM",
            "message": "Contraseña cambiada exitosamente",
            "userId": 1,
            "email": "admin@pryapi.com"
        }

        NOTA: El recoveryToken debe ser válido, no expirado y no haber sido usado previamente.
        La nueva contraseña debe tener al menos 6 caracteres.
    */
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
