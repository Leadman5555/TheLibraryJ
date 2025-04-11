FROM node:18 AS build
WORKDIR /angular_client
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build --omit-dev

FROM nginx:alpine
COPY --from=build /angular_client/dist/the-library-a /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
