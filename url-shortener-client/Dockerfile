# Stage 1: Build Angular app
FROM node:18-alpine as build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# Stage 2: Serve with NGINX
FROM nginx:stable-alpine

# Remove the default nginx static assets
RUN rm -rf /usr/share/nginx/html/*

# Copy the Angular build output to NGINX's public folder
COPY --from=build /app/dist/url-shortener-client/browser /usr/share/nginx/html

# Optional: custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
