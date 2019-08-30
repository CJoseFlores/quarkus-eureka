package io.quarkus.eureka;

import io.quarkus.eureka.client.EurekaClient;
import io.quarkus.eureka.client.InstanceInfo;
import io.quarkus.eureka.config.ServiceLocationConfig;
import io.quarkus.eureka.operation.OperationFactory;
import io.quarkus.eureka.operation.remove.RemoveInstanceOperation;
import io.quarkus.runtime.ShutdownEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import java.util.logging.Logger;

@ApplicationScoped
public class EurekaProducer {

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    private OperationFactory operationFactory;

    private InstanceInfo instanceInfo;

    private ServiceLocationConfig serviceLocationConfig;

    @Produces
    EurekaClient eurekaClient() {
        return new EurekaClient(serviceLocationConfig, operationFactory);
    }

    void onApplicationStop(@Observes ShutdownEvent shutdownEvent) {
        logger.info("application finished... now we have to deregister from Eureka...");
        String appId = instanceInfo.getApp();
        serviceLocationConfig.getLocations()
                .forEach(location -> operationFactory.get(RemoveInstanceOperation.class).remove(location, appId));
    }

    void setInstanceInfo(final InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    void setServiceLocationConfig(final ServiceLocationConfig serviceLocationConfig) {
        this.serviceLocationConfig = serviceLocationConfig;
    }

    void setOperationFactory(final OperationFactory operationFactory) {
        this.operationFactory = operationFactory;
    }
}
