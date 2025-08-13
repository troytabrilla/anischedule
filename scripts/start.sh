# start minikube
minikube start
minikube addons enable ingress

# build images
docker build -t api-java:0.0.0 $(pwd)/api-java
docker build --build-arg VITE_BASE_API_URL=http://localhost -t ui-react:0.0.0 $(pwd)/ui-react

# load images to minikube
minikube image load api-java:0.0.0
minikube image load ui-react:0.0.0
minikube image ls

# install app locally
helm install local $(pwd)/infra/helm/anischedule --values $(pwd)/infra/helm/anischedule/values.yaml
kubectl get all -o wide

# wait for gateway to get IP address
kubectl wait --for=jsonpath="{.status.loadBalancer.ingress}" ingress/gateway-local --timeout=120s
kubectl get ingress

# expose ingress
minikube tunnel
