# Étape 1 : Build de l'application Angular avec Node.js
FROM node:18 AS build-stage
WORKDIR /app

# Désactiver les analytics Angular
ENV NG_CLI_ANALYTICS="false"

# Copier uniquement les fichiers nécessaires pour installer les dépendances
COPY package.json package-lock.json ./
RUN npm install

# Copier le reste du projet et construire l’application Angular
COPY . .
RUN npm run build --prod

# Étape 2 : Servir l’application avec Nginx
FROM nginx:alpine AS production-stage

# Copier la configuration personnalisée de Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers de build Angular vers Nginx
COPY --from=build-stage /app/dist/front/browser/ /usr/share/nginx/html/


# Exposer le port 4200
EXPOSE 4200

# Lancer Nginx
CMD ["nginx", "-g", "daemon off;"]
