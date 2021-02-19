package config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import io.fabric8.kubernetes.api.KubernetesHelper;
import io.fabric8.kubernetes.api.PodStatusType;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.assertions.KubernetesAssert;
import io.fabric8.kubernetes.assertions.ServicesAssert;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Condition;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.K8sClient;

public class TestUtils {
  private static final Logger logger = LoggerFactory.getLogger(K8sClient.class);

  private KubernetesAssert k8sAssert;
  private K8sClient client;

  public TestUtils(K8sClient k8sClient) {

    this.client = k8sClient;
    k8sAssert = new KubernetesAssert(client.getRawClient());
  }

  public void allPodsReady(int numberOfPods) {
    k8sAssert.podList().runningStatus().hasSize(numberOfPods);
  }

  public void allAllPodsAreOk() {
    client.getNamespacedPods().forEach(pod -> assertPodIsOk(pod));
  }

  public void allContainersReady() {
    client
        .getNamespacedPods()
        .forEach(
            pod ->
                pod.getStatus()
                    .getContainerStatuses()
                    .forEach(containerStatus -> assertTrue(containerStatus.getReady())));
  }

  /** Asserts that either this service has a valid Endpoint or that a pod is Ready */
  public void assertAllServicesCreated(Set<String> servicesNames) {
    k8sAssert.serviceList().items().isIn(servicesNames);
  }

  public void assertAllServicesReadiness(Set<String> servicesNames) {
    List<Service> services = new ArrayList<>();
    servicesNames.forEach(serviceName -> services.add(client.getService(serviceName)));
    ServicesAssert servicesAssert = new ServicesAssert(client.getRawClient(), services);
    servicesAssert.assertAllServicesHaveEndpointOrReadyPod();
  }

  public void assertServicePorts(
      String serviceName, String portName, Integer portValue, String protocol) {
    Service service = client.getService(serviceName);
    Optional<ServicePort> servicePort =
        service.getSpec().getPorts().stream()
            .filter(port -> port.getName().equals(portName))
            .findFirst();
    assertTrue("Service port wasn't found", servicePort.isPresent());
    Assert.assertEquals(
        "The intended Port is not available for the service",
        portValue,
        servicePort.get().getPort());
    Assert.assertEquals(
        "The intended Port is not available for the service",
        protocol.toLowerCase(),
        servicePort.get().getProtocol().toLowerCase());
  }

  public void isEnvVarExists(String containerName, String envVarName, String secretName) {

    List<EnvVar> envVars = client.getContainerEnvVars(containerName);
    assertFalse(envVars.isEmpty());
    EnvVar envVar =
        envVars.stream().filter(envVar1 -> envVar1.getName().equals(envVarName)).findFirst().get();
    if (secretName != null) {
      Assert.assertEquals(secretName, envVar.getValueFrom().getSecretKeyRef().getName());
    }
  }

  public void assertSecretsAreReady(String secretName, String secretKey) {
    Secret secret = client.getSecret(secretName);
    Assert.assertEquals(secretKey, secret.getData().get("secretKey"));
  }

  private void assertPodIsOk(Pod pod) {
    PodStatusType status = PodStatusType.OK;
    Condition<Pod> expectedCondition =
        new Condition<Pod>() {
          @Override
          public String toString() {
            return "podStatus(" + status + ")";
          }

          @Override
          public boolean matches(Pod pod) {
            return Objects.equals(status, KubernetesHelper.getPodStatus(pod));
          }
        };
    assertTrue(expectedCondition.matches(pod));
  }
}
