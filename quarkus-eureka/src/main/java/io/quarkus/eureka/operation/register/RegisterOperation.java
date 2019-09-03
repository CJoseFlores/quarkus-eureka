package io.quarkus.eureka.operation.register;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.operation.Operation;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

import static io.quarkus.eureka.client.Status.UP;
import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static javax.ws.rs.core.Response.Status.Family.CLIENT_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SERVER_ERROR;
import static javax.ws.rs.core.Response.Status.Family.SUCCESSFUL;

public class RegisterOperation implements Operation {

    private Logger logger = Logger.getLogger(this.getClass());

    public void register(final String location, final InstanceInfo instanceInfo) {
        try {
            String registrationUrl = String.join("/", location, "apps", instanceInfo.getApp());
            Map<String, InstanceInfo> instance = singletonMap("instance", instanceInfo.withStatus(UP));
            Client client = ResteasyClientBuilder.newClient();
            Response response = client
                    .target(registrationUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(objectToJson(instance), MediaType.APPLICATION_JSON_TYPE));

            if (response.getStatusInfo().getFamily().equals(SUCCESSFUL)) {
                logger.info(format("Service has been registered in %s", location));
            } else if (response.getStatusInfo().getFamily().equals(CLIENT_ERROR)) {
                logger.info(format("Service has problems to register in %s", location));
            } else if (response.getStatusInfo().getFamily().equals(SERVER_ERROR)) {
                logger.info(format("%s returns error message %s", location, response.readEntity(String.class)));
            }
            response.close();
            client.close();
        } catch (ProcessingException ex) {
            logger.info("eureka service is down and no status can be register");
        }
    }

    private String objectToJson(Map<String, InstanceInfo> instance) {
        try {
            return new ObjectMapper().writeValueAsString(instance);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}