@rem uninstall local app
helm uninstall local

@rem delete minikube
minikube stop
minikube delete
