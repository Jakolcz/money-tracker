package dev.kolacek.moneytracker.resource;

import dev.kolacek.moneytracker.domain.user.User;
import dev.kolacek.moneytracker.service.UserService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;

import java.net.URI;
import java.util.Map;
import java.util.Set;

@Path("/auth")
public class AuthResource {

    @Inject
    UserService userService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance login();

        public static native TemplateInstance register();

        public static native TemplateInstance profile(User user);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Uni<Response> register(@FormParam("email") String email,
                                  @FormParam("password") String password,
                                  @FormParam("roles") String roles) {
        return userService.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return Uni.createFrom().item(
                                Response.status(Response.Status.BAD_REQUEST)
                                        .entity(Map.of("error", "Username already taken"))
                                        .build());
                    }

                    User user = new User(null, email, password, Set.of(roles.split(",")));
                    return userService.createUser(user)
                            .map(registeredUser -> Response
                                    .created(URI.create("/auth/login"))
                                    .entity(Map.of("message", "Registration successful. Please log in."))
                                    .build());
                });
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance loginPage() {
        return Templates.login();
    }

    @GET
    @Path("/register")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance registerPage() {
        return Templates.register();
    }

    @GET
    @Path("/profile")
    @Produces(MediaType.TEXT_HTML)
    @Authenticated
    public Uni<TemplateInstance> profilePage(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        return userService.findByEmail(username)
                .map(Templates::profile);
    }
}
