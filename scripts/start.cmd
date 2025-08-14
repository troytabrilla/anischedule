@REM start minikube
minikube start
minikube addons enable ingress

@REM build images
docker build -t schedule-api-java:0.0.0 %cd%\schedule-api-java
docker build --build-arg VITE_BASE_API_URL=http://localhost -t ui-react:0.0.0 %cd%\ui-react

@REM load images to minikube
minikube image load schedule-api-java:0.0.0
minikube image load ui-react:0.0.0
minikube image ls

@REM install app locally
helm install local %cd%\infra\helm\anischedule --values %cd%\infra\helm\anischedule\values.yaml

@REM wait for ingress to get IP address
kubectl wait --for=jsonpath="{.status.loadBalancer.ingress}" ingress/ingress-local --timeout=120s
kubectl get all -o wide
kubectl get ingress

@REM expose ingress
minikube tunnel
