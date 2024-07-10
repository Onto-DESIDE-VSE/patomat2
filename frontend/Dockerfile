# BASE STAGE
# Prepare node, copy package.json
FROM node:lts-alpine AS base
WORKDIR /usr/src/app
COPY package.json package-lock.json ./

# DEPENDENCIES STAGE
# Install production and dev dependencies
FROM base AS dependencies
# install node packages
RUN npm install

# BUILD STAGE
# run NPM build
FROM dependencies AS build

COPY . .
RUN set -ex; \
  npm run build

# RELEASE STAGE
# Only include the static files in the final image
FROM nginx:1.26-alpine
COPY --from=build /usr/src/app/dist /usr/share/nginx/html
COPY ./nginx/nginx.conf /etc/nginx/conf.d/default.conf
# Make env var substitution happen on *.template files in the html dir
ENV NGINX_ENVSUBST_TEMPLATE_DIR=/usr/share/nginx/html
ENV NGINX_ENVSUBST_OUTPUT_DIR=/usr/share/nginx/html
RUN chmod a+r -R /usr/share/nginx/html
