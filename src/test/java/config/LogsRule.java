package config;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.K8sClient;

public class LogsRule extends TestWatcher {

  private static final Logger logger = LoggerFactory.getLogger(LogsRule.class);

  private K8sClient client;

  public LogsRule(K8sClient client) {
    this.client = client;
  }
  /**
   * Invoked when a test fails gathers Logs from all Containers within all the Pods
   *
   * @param e : Failure exception
   * @param description : Test Case description
   */
  @Override
  protected void failed(Throwable e, Description description) {
    logger.debug("Test Failed " + description.getMethodName() + " " + e.getMessage());
    File dir = new File("logs");
    dir.mkdirs();

    Map<String, ArrayList<String>> podsNames =
        client.getNamespacedPods().stream()
            .collect(
                Collectors.toMap(
                    pod -> pod.getMetadata().getName(),
                    pod ->
                        pod.getSpec().getContainers().stream()
                            .map(container -> container.getName())
                            .collect(Collectors.toCollection(ArrayList::new))));

    podsNames.forEach(
        (name, containers) ->
            containers.forEach(
                c -> {
                  File file = new File(dir, c + ".log");
                  String logs = client.getContainersLog(name, c);
                  try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    byte[] strToBytes = logs.getBytes();
                    outputStream.write(strToBytes);
                  } catch (Exception ex) {
                    logger.trace(ex.getMessage());
                    ex.printStackTrace();
                  }
                }));
  }
}
