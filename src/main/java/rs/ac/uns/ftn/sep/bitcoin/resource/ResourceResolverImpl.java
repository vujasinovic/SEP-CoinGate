package rs.ac.uns.ftn.sep.bitcoin.resource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public class ResourceResolverImpl implements ResourceResolver {
    private static final String FRONTEND_DIR = "/static/";
    private static final Resource index = new ClassPathResource(FRONTEND_DIR + "index.html");

    @Override
    public Resource resolveResource(HttpServletRequest request,
                                    String requestPath,
                                    List<? extends Resource> locations,
                                    ResourceResolverChain chain) {

        return resolve(requestPath, locations);
    }

    @Override
    public String resolveUrlPath(String resourcePath, List<? extends Resource> locations, ResourceResolverChain chain) {

        Resource resolvedResource = resolve(resourcePath, locations);
        if (resolvedResource == null) {
            return null;
        }
        try {
            return resolvedResource.getURL().toString();
        } catch (IOException e) {
            return resolvedResource.getFilename();
        }
    }

    private Resource resolve(String requestPath, List<? extends Resource> locations) {
        if (requestPath == null) return null;

        ClassPathResource classPathResource = new ClassPathResource(FRONTEND_DIR + requestPath);
        Resource response = index;
        if (classPathResource.exists())
            response = classPathResource;

        return response;
    }
}
