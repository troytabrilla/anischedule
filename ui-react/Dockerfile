FROM node:23-alpine AS build

WORKDIR /build

ARG VITE_BASE_API_URL=http://localhost:8080
ENV VITE_BASE_API_URL=$VITE_BASE_API_URL

ARG VITE_SCHEDULE_API_PATH=/schedule/api/v1
ENV VITE_SCHEDULE_API_PATH=$VITE_SCHEDULE_API_PATH

COPY package*.json .
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine AS final

COPY --from=build build/dist /usr/share/nginx/html
COPY --from=build build/config/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 3000

CMD ["nginx", "-g", "daemon off;"]
