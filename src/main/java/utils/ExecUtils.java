package utils;

import client.K8sClientConfig;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecUtils {

  private static final CountDownLatch execLatch = new CountDownLatch(1);
  private static final Logger logger = LoggerFactory.getLogger(K8sClient.class);

  private K8sClientConfig client;

  public ExecUtils(K8sClientConfig client) {
    this.client = client;
  }

  public void execPod(String namespace, String podName, String... command) {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream error = new ByteArrayOutputStream();
    try {
      logger.debug("Started Exec into {} pod", podName);
      ExecWatch execWatch =
          client
              .getClient()
              .pods()
              .inNamespace(namespace)
              .withName(podName)
              .writingOutput(out)
              .writingError(error)
              .usingListener(new PodExecListener())
              .exec(command);

      boolean latchTerminationStatus = execLatch.await(5, TimeUnit.SECONDS);
      if (!latchTerminationStatus) {
        logger.debug("Latch could not terminate within specified time");
      }
      logger.debug("Exec Output:  " + out);
      execWatch.close();
    } catch (InterruptedException ie) {
      Thread.currentThread().interrupt();
      ie.printStackTrace();
    }
  }
}
