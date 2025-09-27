package services;

import java.util.Set;
import javax.ws.rs.core.Application;

@javax.ws.rs.ApplicationPath("webresources")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(services.AppuserFacadeREST.class);
        resources.add(services.CategoryFacadeREST.class);
        resources.add(services.EmployeeFacadeREST.class);
        resources.add(services.ProjectFacadeREST.class);
        resources.add(services.StatusFacadeREST.class);
    }
    
}
