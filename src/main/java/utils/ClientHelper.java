package utils;

import client.K8sClient;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.Deployment;

import java.util.List;


public class ClientHelper {

    private K8sClient client;
    public ClientHelper(K8sClient client){
        this.client = client;

    }

   public List<Service> getNamespacedServices(String namespace){

       return client.getClient().services().inNamespace(namespace).list().getItems();
   }

    public List<Deployment> getNamespacedDeployment(String namespace) {

        return client.getClient().apps().deployments().inNamespace(namespace).list().getItems();
    }

    public List<Namespace> getNameSpaces(){

        return client.getClient().namespaces().list().getItems();
    }
    public List<Pod> getNamespacedPods(String namespace){

        return client.getClient().pods().inNamespace(namespace).list().getItems();
    }

    public ObjectMeta getServiceMetadata(Service service ){

       return service.getMetadata();
    }

    public <T extends HasMetadata> ObjectMeta getPodMetadata(T obj ){

        return obj.getMetadata();
    }
}
