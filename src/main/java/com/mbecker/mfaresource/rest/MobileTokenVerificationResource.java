package com.mbecker.mfaresource.rest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mbecker.jpa.MobileTokenVerification;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;

public class MobileTokenVerificationResource {

    private static final Logger LOG = Logger.getLogger(MFARestResource.class);

    private final KeycloakSession session;
    private UserModel user;
    private AuthenticationManager.AuthResult auth;

    public MobileTokenVerificationResource(KeycloakSession session, AuthResult auth) {
        this.session = session;
        this.auth = auth;
        if (auth != null) {
            this.user = auth.getUser();
        }
    }

    private EntityManager getEntityManager() {
        return this.session.getProvider(JpaConnectionProvider.class).getEntityManager();
    }

    @GET
    @Path("")
    @NoCache
    @Produces(MediaType.APPLICATION_JSON)
    public Response listMobileTokenVerification() {
        if (this.auth == null) {
            LOG.info("GET /verification user is not authenticated");
            return this.badRequest();
        }
        List<MobileTokenVerification> l = getEntityManager()
                    .createNamedQuery("findByRealm", MobileTokenVerification.class)
                .setParameter("realmId", this.session.getContext().getRealm().getId()).getResultList();
        
        return Response.status(Response.Status.CREATED).entity(l).build();
    }

    @POST
    @Path("")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCompany(MobileTokenVerification rep) {
        if (this.auth == null) {
            LOG.info("POST /verification user is not authenticated");
            return this.badRequest();
        }
        LOG.info("Resource MobileTokenVerification: " + rep);
        return Response.status(Response.Status.CREATED).entity(rep).build();
    }

    // @GET
    // @NoCache
    // @Path("{code}")
    // @Produces(MediaType.APPLICATION_JSON)
    // public Response getCompany(@PathParam("code") final String code) {
    //     if (this.auth == null) {
    //         LOG.info("POST /{code} user is not authenticated");
    //         return this.badRequest();
    //     }
    //     MobileTokenVerificationRepresentation m = session.getProvider(MFAProvider.class)
    //             .findMobileTokenVerification(code, session.getContext().getRealm().getId());
    //     return Response.status(Response.Status.CREATED).entity(m).build();
    // }

    protected Response badRequest() {
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

}
