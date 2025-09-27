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

    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.project/listarPorEmpleado?employeeId=1

        QUERY PARAMETERS:
        employeeId: 1 (ID del empleado a consultar)
    
        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "success": true,
            "data": [
                {
                    "name": "Proyecto Alpha",
                    "id": 3,
                    "category": {
                        "code": "WEB",
                        "id": 1
                    },
                    "status": {
                        "name": "Completado",
                        "id": 2
                    }
                },
                {
                    "name": "Mi Nuevo Proyecto",
                    "id": 5,
                    "category": {
                        "code": "WEB",
                        "id": 1
                    },
                    "status": {
                        "name": "Activo",
                        "id": 1
                    }
                },
                .
                .
                .
            ]
        }

        NOTA: Este endpoint requiere autenticación mediante cookie de sesión.
        Incluye proyectos del empleado específico Y proyectos compartidos (shared = true).
        El employeeId es obligatorio y debe existir en la base de datos.
    */
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

    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.project/crear

        HEADERS:
        Content-Type: application/json
    
        BODY (JSON) radioButton=raw:
        {
            "name": "Mi Nuevo Proyecto",
            "description": "Descripción detallada del proyecto",
            "startDate": "2024-01-15",
            "endDate": "2024-12-31",
            "shared": false,
            "icon": "project-icon.png",
            "categoryId": {
                "id": 1
            },
            "employeeId": {
                "id": 1
            },
            "statusId": {
                "id": 1
            }
        }

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "success": true,
            "message": "Proyecto creado exitosamente",
            "id": 8
        }

        NOTA: Este endpoint requiere autenticación mediante cookie de sesión.
        Los campos categoryId, employeeId y statusId deben existir en la base de datos.
        Las fechas deben estar en formato YYYY-MM-DD.
    */
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
    
    
    /*
        <------------------- COMPROBAR API CON POSTMAN ------------------->

        URL: http://localhost:8080/Api_rest_pry/webresources/repository.project/busquedaAvanzada?employeeId=1&nombreProyecto=Proyecto%Alpha&nombreCategoria=Desarrollo%Web&codigoCategoria=WEB&nombreEstado=Completado&fechaInicio=2025-01-01&fechaFinalizacion=2025-06-30

        QUERY PARAMETERS (todos opcionales excepto employeeId):
        employeeId: 1 (OBLIGATORIO - ID del empleado)
        nombreProyecto: web (opcional - búsqueda por nombre de proyecto)
        nombreCategoria: desarrollo (opcional - búsqueda por nombre de categoría)
        codigoCategoria: WEB (opcional - búsqueda por código de categoría)
        nombreEstado: progreso (opcional - búsqueda por nombre de estado)
        fechaInicio: 2024-01-01 (opcional - proyectos desde esta fecha)
        fechaFinalizacion: 2024-12-31 (opcional - proyectos hasta esta fecha)

        -------------------------------------------------------------------
        RESPUESTA ESPERADA:
        {
            "success": true,
            "data": [
                {
                    "name": "Proyecto Alpha",
                    "id": 3,
                    "category": {
                        "code": "WEB",
                        "id": 1
                    },
                    "status": {
                        "name": "Completado",
                        "id": 2
                    }
                }
            ]
        }

        NOTA: Este endpoint requiere autenticación mediante cookie de sesión.
        Solo employeeId es obligatorio, todos los demás filtros son opcionales.
        Las fechas deben estar en formato YYYY-MM-DD.
        Incluye proyectos del empleado específico Y proyectos compartidos (shared = true).
    */
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
