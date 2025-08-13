@SET PWD = (gl).Path

@rem delete configs
kubectl delete -f %PWD%\infra\k8s

@rem delete minikube
minikube stop
minikube delete
