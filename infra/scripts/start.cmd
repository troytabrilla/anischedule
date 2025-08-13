@rem start minikube
minikube start

@rem build images
docker build -t api-java:0.0.0 %cd%\api-java
docker build -t ui-react:0.0.0 %cd%\ui-react

@rem load images to minikube
minikube image load api-java:0.0.0
minikube image load ui-react:0.0.0
minikube image ls

@rem apply configs
kubectl apply -f %cd%\infra\k8s
kubectl get all -o wide

@rem expose ingress
echo "TODO expose ingress for services"
