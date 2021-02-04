# K8s Automation test Suite 

This repository used fabric8 [Kubernetes Client](https://github.com/fabric8io/kubernetes-client) and [fabric8 kubernetes-assertions](https://github.com/fabric8io/kubernetes-assertions) 
to run smoke test on any Kubernetes Cluster or Spring Boot Containers. 

  
## Running Tests  
Fabric8 Kubernetes clients will look for kubctl config file in the home path and it will inherent it's configurations.
The Repository uses gradle to manage dependencies and running the tests, you can run the test on any kubernetes cluster;
by running the following command: 

```bash

./gradlew clean test --info
```
### Environment Variables

The test suite will need the following Environment variables to be sat before any execution:

#### MacOs/Linux machines
```bash
export namespace=TARGET_NAMESPACE
```
#### Windows machines
```shell script
setx namespace 'TARGET_NAMESPACE'
```

## Linting and Styling

We are using `Spotless` for our linting and lint check, note that code build is depending on lint check to pass.
to run the lint check you execute the following command:
```bash
./gradlew sA
```