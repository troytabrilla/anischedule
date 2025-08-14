# start minikube
minikube start
minikube addons enable ingress

# build images
docker build -t schedule-api-java:0.0.0 $(pwd)/schedule-api-java
docker build --build-arg VITE_BASE_API_URL=http://localhost -t ui-react:0.0.0 $(pwd)/ui-react

# load images to minikube
minikube image load schedule-api-java:0.0.0
minikube image load ui-react:0.0.0
minikube image ls

# install app locally
helm install local $(pwd)/infra/helm/anischedule --values $(pwd)/infra/helm/anischedule/values.yaml

# wait for ingress to get IP address
kubectl wait --for=jsonpath="{.status.loadBalancer.ingress}" ingress/ingress-local --timeout=120s
kubectl get all -o wide
kubectl get ingress

# expose ingress
minikube tunnel
