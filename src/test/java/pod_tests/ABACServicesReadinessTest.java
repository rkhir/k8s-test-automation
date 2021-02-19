package pod_tests;

import config.LogsRule;
import config.TestUtils;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import utils.K8sClient;

public class ABACServicesReadinessTest {
  private static final String NAMESPACE = System.getenv().getOrDefault("data-lake", "default");
  private K8sClient k8sClient = new K8sClient(NAMESPACE);
  private TestUtils testUtils;
  private Set<String> servicesNames = new HashSet<>();

  @Rule public LogsRule logsRule = new LogsRule(k8sClient);

  @Before
  public void setup() {
    this.testUtils = new TestUtils(k8sClient);
    Collections.addAll(
        servicesNames,
        "administration-point",
        "enforcement-point",
        "pap-db-server",
        "redis-pubsub");
  }

  @Test
  public void assertAllServicesReadiness() {

    testUtils.assertAllServicesCreated(servicesNames);
    testUtils.assertAllServicesReadiness(servicesNames);
    testUtils.assertServicePorts("administration-point", "http", 10988, "tcp");
    testUtils.assertServicePorts("enforcement-point", "http", 10987, "tcp");
    testUtils.assertServicePorts("pap-db-server", "5432", 5432, "tcp");
    testUtils.assertServicePorts("redis-pubsub", "redis", 6379, "tcp");
  }
}
