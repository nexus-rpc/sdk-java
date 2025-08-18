package io.nexusrpc.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.nexusrpc.Link;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class OperationContextTest {
  @Test
  void linkTest() throws URISyntaxException {
    OperationContext octx =
        OperationContext.newBuilder().setService("service").setOperation("operation").build();
    URI url = new URI("http://somepath?k=v");
    octx.addLinks(Link.newBuilder().setUri(url).setType("com.example.MyResource").build());
    assertEquals(
        octx.getLinks(),
        Arrays.asList(Link.newBuilder().setUri(url).setType("com.example.MyResource").build()));
    octx.setLinks();
    assertEquals(octx.getLinks(), Arrays.asList());
  }

  @Test
  void deadlineTest() {
    Instant deadline = Instant.now().plusMillis(1000);
    OperationContext octx =
        OperationContext.newBuilder()
            .setService("service")
            .setOperation("operation")
            .setDeadline(deadline)
            .build();
    assertEquals(deadline, octx.getDeadline());
  }
}
