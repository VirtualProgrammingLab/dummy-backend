package de.uni_stuttgart.tik.viplab.dummybackend;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

@ApplicationScoped
public class MessageResponder {

  @Inject
  Logger logger;

  @Inject
  @ConfigProperty(name = "viplab.responses")
  private Optional<String> responseDirectory;

  @Incoming("computations")
  @Outgoing("results")
  public String processCompuation(String message) {

    JsonReader jsonReader = Json.createReader(new StringReader(message));
    JsonObject computation = jsonReader.readObject();
    String computationID = computation.getString("identifier");
    logger.info(computation);
    logger.info(computationID);

    InputStream is = null;
    if (responseDirectory.isEmpty()) {
      is = getClass().getClassLoader()
              .getResourceAsStream("results/example1.json");
    } else {
      //TODO: setup resources
    }
    jsonReader = Json.createReader(is);
    JsonObject result = jsonReader.readObject();
    result = Json.createPatchBuilder()
            .replace("/computation",
                    Json.createValue(computationID))
            .build()
            .apply(result);
    logger.info(result);
    return result.toString();
  }
}
