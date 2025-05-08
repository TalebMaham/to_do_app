# 💼 Application de Gestion de Projets Collaboratif (Angular + Spring Boot)

Cette application permet de créer, gérer et suivre des projets en équipe, avec une interface Angular réactive et un backend robuste en Spring Boot. Elle offre une gestion fine des utilisateurs, des rôles, des tâches et un suivi complet des modifications.

---

## ✨ Fonctionnalités clés

### 🔐 Authentification
- Inscription et connexion via formulaire Angular.
- Backend Spring valide les identifiants et retourne un token simulé.
- Stockage local du token côté client (session/local storage selon config).

### 📁 Projets
- Création de projets avec définition d’un administrateur.
- Récupération des projets par utilisateur.
- Attribution de rôles (ADMIN, MEMBER, OBSERVER).
- Suppression de projets.

### ✅ Tâches
- Création, assignation et modification des tâches dans un projet.
- Priorité, échéance, deadline et statut.
- Historique des changements traçable par champ.

### 📜 Historique
- Suivi des modifications de chaque champ d'une tâche.
- Affichage lisible dans l’interface Angular.

### 📬 Notifications (préparé)
- Service d'envoi d'e-mails côté backend prêt à l'emploi.

---

## 🧱 Architecture technique

```plaintext
Frontend (Angular) <--> Backend (Spring Boot) <--> BDD (JPA / H2 ou MySQL)
```

| Côté             | Stack principale             |
|------------------|------------------------------|
| Frontend         | Angular 16+, TypeScript      |
| Backend          | Java 17, Spring Boot 3+      |
| Base de données  | JPA, H2 (dev) ou MySQL       |
| Communication    | REST API avec JSON           |
| Sécurité (POC)   | Token simulé (JWT à venir)   |

---

## ⚙️ Lancement

### Lancer l'application avec Docker Compose

1. Assurez-vous d’avoir Docker et Docker Compose installés sur votre machine.
2. Placez-vous à la racine du projet, puis exécutez la commande suivante :

```bash
docker-compose up --build
```

👉 Une fois l'application lancée, accédez à l’interface ici : [http://localhost:4200](http://localhost:4200)

La base de données est prête mais vide. Commencez par vous inscrire, ajoutez d'autres utilisateurs, puis utilisez l'application !

---

## 🔁 Exemple de flux

1. L’utilisateur s’inscrit via `/signup`.
2. Il se connecte via `/signin`
3. Il crée un projet.
4. Il ajoute des membres avec rôles.
5. Il crée des tâches et les assigne.
6. Il modifie des tâches → l’historique est mis à jour.

---

## 📑 API REST (exemples)

| Méthode | Endpoint                         | Fonction                            |
|---------|----------------------------------|-------------------------------------|
| POST    | `/api/auth/signup`              | Créer un compte                     |
| POST    | `/api/auth/signin`              | Connexion (retourne un token)       |
| POST    | `/api/projects`                 | Créer un projet                     |
| POST    | `/api/projects/{id}/add-member` | Ajouter un membre                   |
| GET     | `/api/projects/user/{userId}`   | Projets associés à un utilisateur   |
| POST    | `/api/projects/{id}/tasks`      | Créer une tâche                     |
| PUT     | `/api/tasks/{id}/assign`        | Assigner une tâche                  |
| PATCH   | `/api/tasks/{id}`               | Modifier une tâche (partiellement)  |
| GET     | `/api/tasks/{id}/history`       | Historique de la tâche              |

***Documentation de l'api ==> [Documentation de l'api](doc_api.md)

---

## 📈 Diagrammes UML

- ![Diagramme de classes](/uml/class-diagram.png)
- ![Diagramme de séquence](/uml/sequence-diagram.png)

---


## 👨‍💻 Auteur

Projet développé par Sidi Mohamed TALEB MAHAM  
But : Formation, démonstration et expérimentation fullstack Java/Angular.

---

## 📝 Licence

Code open-source à usage personnel ou éducatif. Pas encore prêt pour la production.
