package com.mbecker.mfaresource.rest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mbecker.jpa.MobileTokenVerification;

import org.jboss.logging.Logger;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.keycloak.connections.jpa.JpaConnectionProvider;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.AuthenticationManager;
import org.keycloak.services.managers.AuthenticationManager.AuthResult;

public class MobileTokenVerificationResource {

    private static final Logger LOG = Logger.getLogger(MFARestResource.class);

    private final KeycloakSession session;
    private AuthenticationManager.AuthResult auth;

    public MobileTokenVerificationResource(KeycloakSession session, AuthResult auth) {
        this.session = session;
        this.auth = auth;
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
            return this.notAuthenticated();
        }
        
        

        List<UserModel> result = session.userStorageManager().searchForUserByUserAttribute("mobile-x-code", "1234", this.auth.getSession().getRealm());
        // List<MobileTokenVerification> l = getEntityManager()
        //             .createNamedQuery("findByRealm", MobileTokenVerification.class)
        //         .setParameter("realmId", this.session.getContext().getRealm().getId()).getResultList();
        
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @POST
    @Path("")
    @NoCache
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createCompany(MobileTokenVerification rep) {
        if (this.auth == null) {
            LOG.info("POST /verification user is not authenticated");
            return this.notAuthenticated();
        }
        LOG.info("Resource MobileTokenVerification: " + rep);
        return Response.status(Response.Status.CREATED).entity(rep).build();
    }

    @GET
    @NoCache
    @Path("/{realm}/{code}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserByCode(@PathParam("realm") final String realm, @PathParam("code") final String code) {
        
        if(realm == null || code == null) {
            ErrorMessage em = new ErrorMessage("No realm or code", Response.Status.BAD_REQUEST);
            return em.response();
        }

        RealmModel realmModel = this.session.realms().getRealm(realm);
        if(realmModel == null) {
            ErrorMessage em = new ErrorMessage(String.format("No realm found for: %s", realm), Response.Status.BAD_REQUEST);
            return em.response();
        }

        List<UserModel> result = session.userStorageManager().searchForUserByUserAttribute("mobile-x-code", code, realmModel);
        
        
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    protected Response notAuthenticated() {
        ErrorMessage em = new ErrorMessage("Not authenticated", Response.Status.UNAUTHORIZED);
        return em.response();
    }

}
