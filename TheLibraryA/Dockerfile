FROM node:20-alpine AS build
WORKDIR /angular_client
COPY package*.json ./
RUN npm ci
COPY . ./
RUN npm run build --omit-dev

FROM node:20-alpine
WORKDIR /angular_client
COPY --from=build /angular_client/dist/the-library-a ./
CMD node server/server.mjs
EXPOSE 4000
