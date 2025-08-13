@rem delete configs
kubectl delete -f %cd%\infra\k8s

@rem delete minikube
minikube stop
minikube delete
