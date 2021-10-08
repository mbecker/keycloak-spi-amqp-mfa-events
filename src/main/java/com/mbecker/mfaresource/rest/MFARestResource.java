package com.mbecker.mfaresource.rest;

import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;



public class MFARestResource {

    private static final Logger LOG = Logger.getLogger(MFARestResource.class);

    private KeycloakSession session;
    private AuthenticationManager.AuthResult auth;

    public MFARestResource(KeycloakSession session) {
        this.session = session;
        
        HttpHeaders headers = session.getContext().getRequestHeaders();
        String tokenString = AppAuthManager.extractAuthorizationHeaderToken(headers);
        LOG.infof("Access Token: %s", tokenString);
        this.auth = new AppAuthManager.BearerTokenAuthenticator(session).authenticate();
    }

    @Path("verification")
    public MobileTokenVerificationResource getCompanyResource() {
        LOG.info("GET /verification");
        return new MobileTokenVerificationResource(session, this.auth);
    }
        
    @GET
    @Path("/attributes")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public Response downloadCurrentUserAvatarImage() {

        LOG.info("GET /attributes");

        if (this.auth == null) {
            LOG.info("GET /attributes user is not authenticated");
            return this.badRequest();
        }

        UserModel user = this.auth.getUser();
        Map<String, List<String>>  attributes = user.getAttributes();;
        
        // String realmName = auth.getSession().getRealm().getName();

        return Response.ok(attributes).build();
    }

    // @POST
    // @Consumes({MediaType.APPLICATION_JSON})
    // @Produces({MediaType.TEXT_PLAIN})
    // @Path("/post")
    // public String postMessage(Message msg) throws Exception{
        
    //     System.out.println("First Name = "+msg.getFirstName());
    //     System.out.println("Last Name  = "+msg.getLastName());
        
    //     return "ok";
    // }

    protected Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
