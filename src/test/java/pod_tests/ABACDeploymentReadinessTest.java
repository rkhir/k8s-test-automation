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

public class ABACDeploymentReadinessTest {
  private static final String NAMESPACE = System.getenv().getOrDefault("data-lake", "default");
  private K8sClient k8sClient = new K8sClient(NAMESPACE);
  private TestUtils testUtils;
  private Set<String> containerNames = new HashSet<>();

  @Rule public LogsRule logsRule = new LogsRule(k8sClient);

  @Before
  public void setup() {
    this.testUtils = new TestUtils(k8sClient);
    Collections.addAll(
        containerNames,
        "administration-point",
        "enforcement-point",
        "pap-db-server",
        "redis-pubsub");
  }

  @Test
  public void administrationPointEnvVariablesTest() {

    testUtils.isEnvVarExists("administration-point", "DB_URL", "pap-database-secrets");
    testUtils.assertSecretsAreReady("pap-database-secrets", "/cert/pap/DB_URL");
  }
}
