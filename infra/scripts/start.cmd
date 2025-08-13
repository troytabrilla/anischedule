@SET PWD = (gl).Path

@rem start minikube
minikube start
minikube addons enable registry

@rem build images
docker build -t api-java:0.0.0 %PWD%\api-java
docker build -t ui-react:0.0.0 %PWD%\ui-react

@rem load images to minikube
minikube image load api-java:0.0.0
minikube image load ui-react:0.0.0
minikube image ls

@rem apply configs
kubectl apply -f %PWD%\infra\k8s
kubectl get all -o wide

@rem expose ingress
echo "TODO expose ingress for services"
