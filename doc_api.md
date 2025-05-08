## 🔐 Inscription d'un utilisateur

**Endpoint** :  
`POST /api/auth/signup`

**Description** :  
Permet de créer un nouveau compte utilisateur dans le système.

---

### ✅ Requête

**Headers** :
```http
Content-Type: application/json
```

**Body (JSON)** :
```json
{
  "email": "hassan@outlook.fr",
  "password": "hassanpass",
  "username": "Hassan"
}
```

---

### 📤 Réponse

**Code HTTP :** `200 OK`

**Body (JSON)** :
```json
{
  "message": "Utilisateur inscrit avec succès"
}
```

---

### ❌ Réponse – Conflit (utilisateur déjà existant)

**Code HTTP :** `409 Conflict`  
**Body (JSON)** :
```json
{
  "error": "Le nom d'utilisateur est déjà pris."
}
```


---

### 🔒 Contraintes de validation

- L’`email` doit être unique.
- Le `username` doit être unique.
- Le `password` est requis (validation côté serveur recommandée).

---

## 🔑 Connexion d'un utilisateur

**Endpoint** :  
`POST /api/auth/signin`

**Description** :  
Permet à un utilisateur existant de se connecter avec son nom d’utilisateur et mot de passe.

---

### ✅ Requête

**Headers** :
```http
Content-Type: application/json
```

**Body (JSON)** :
```json
{
  "username": "Sidi",
  "password": "sidipassword"
}
```

---

### 📤 Réponse – Succès

**Code HTTP :** `200 OK`  
**Body (JSON)** :
```json
{
  "user_name": "Sidi",
  "token": "49fa2767-5e58-428f-8a37-567903dfd99a",
  "user_id": "1",
  "message": "Connexion réussie"
}
```

---

### ❌ Réponse – Échec (mauvais identifiants)

**Code HTTP :** `401 Unauthorized`  
**Body (JSON)** :
```json
{
  "error": "Nom d'utilisateur ou mot de passe incorrect"
}
```

---

### 🛡️ À propos du token

- Le `token` retourné est utilisé pour l’authentification des requêtes futures.
- Il peut être stocké dans :
  - `localStorage`
  - `sessionStorage`
  - ou en `cookie` sécurisé

---

### 🔁 Exemple Postman

- Méthode : `POST`
- URL : `http://localhost:8080/api/auth/signin`
- Corps : JSON brut
- Content-Type : `application/json`

---

## 📂 Obtenir les projets d’un utilisateur

**Endpoint :**  
`GET /api/projects/user/{userId}`

**Description :**  
Retourne tous les projets auxquels un utilisateur est lié, en tant qu’administrateur, membre ou observateur. Inclut également les tâches assignées dans chaque projet.

---

### 🔍 Exemple de requête

```http
GET http://localhost:8080/api/projects/user/1
```

---

### ✅ Exemple de réponse (200 OK)

```json
[
  {
    "id": 101,
    "name": "Application de gestion d'événements",
    "description": "Développement d'une plateforme web pour organiser et suivre des événements professionnels.",
    "startDate": "2025-05-01",
    "admin": {
      "id": 1,
      "username": "Nadia",
      "email": "nadia@eventpro.com"
    },
    "projectMembers": [
      {
        "user": {
          "id": 2,
          "username": "Rayan",
          "email": "rayan@eventpro.com"
        },
        "role": "MEMBER"
      },
      {
        "user": {
          "id": 3,
          "username": "Leila",
          "email": "leila@eventpro.com"
        },
        "role": "OBSERVER"
      }
    ],
    "tasks": [
      {
        "id": 201,
        "name": "Créer la base de données",
        "description": "Modéliser et implémenter le schéma de base de données avec PostgreSQL.",
        "dueDate": "2025-05-10",
        "deadline": null,
        "priority": "HIGH",
        "status": "IN_PROGRESS",
        "assignee": {
          "id": 2,
          "username": "Rayan",
          "email": "rayan@eventpro.com"
        }
      },
      {
        "id": 202,
        "name": "Maquette de l’interface utilisateur",
        "description": "Concevoir les écrans de connexion, tableau de bord et gestion des événements.",
        "dueDate": "2025-05-15",
        "deadline": null,
        "priority": "MEDIUM",
        "status": "TODO",
        "assignee": {
          "id": 3,
          "username": "Leila",
          "email": "leila@eventpro.com"
        }
      }
    ]
  }
]
```

### POST /api/projects

Crée un nouveau projet.

#### Requête JSON
```json
{
  "name": "Application Mobile Santé",
  "description": "Suivi des patients à distance",
  "startDate": "2025-06-01",
  "adminId": 1
}
```

#### Réponse JSON (200 OK)
```json
{
  "id": 4,
  "name": "Application Mobile Santé",
  "description": "Suivi des patients à distance",
  "startDate": "2025-06-01",
  "admin": {
    "id": 1,
    "username": "Sidi",
    "email": "sidi@outlook.fr"
  },
  "projectMembers": [],
  "tasks": []
}
```

### POST /api/projects/{projectId}/add-member

Permet d’ajouter un utilisateur existant à un projet avec un rôle spécifique.

#### Paramètres de requête
- `email` : adresse e-mail de l'utilisateur à ajouter  
- `role` : rôle attribué dans le projet (`MEMBER`, `ADMIN`, `OBSERVER`)

#### Exemple de requête
```
POST /api/projects/3/add-member?email=lea.dupont@startup.fr&role=MEMBER
```

#### Réponse (200 OK)
```json
{
  "id": 3,
  "name": "Lancement Application Mobile",
  "description": "Développement d’une application mobile de gestion des finances personnelles.",
  "startDate": "2025-05-15",
  "admin": {
    "id": 1,
    "username": "Julien",
    "email": "julien.martin@startup.fr"
  },
  "projectMembers": [
    {
      "user": {
        "id": 4,
        "username": "Léa",
        "email": "lea.dupont@startup.fr"
      },
      "role": "MEMBER"
    }
  ],
  "tasks": []
}
```
### POST /api/projects/{projectId}/tasks

Crée une nouvelle tâche dans un projet donné.

#### Corps de la requête (JSON)
```json
{
  "name": "Définir les objectifs de l’application",
  "description": "Rédiger un document avec les fonctionnalités principales de l'app mobile",
  "dueDate": "2025-05-20",
  "priority": "MEDIUM"
}
```

#### Réponse (200 OK)
```json
{
  "id": 6,
  "name": "Définir les objectifs de l’application",
  "description": "Rédiger un document avec les fonctionnalités principales de l'app mobile",
  "dueDate": "2025-05-20",
  "deadline": null,
  "priority": "MEDIUM",
  "assignee": null,
  "status": "TODO"
}
```

> ⚠️ Seuls les membres ou administrateurs du projet peuvent créer une tâche Depuis le front mais :
![Remarque ](/uml/nb.png)
>



### 🔄 Modifier une tâche

**PATCH** `/api/projects/3/tasks/6?userId=1`

#### Body JSON

```json
{
  "name": "Préparer les visuels",
  "description": "Créer les supports visuels",
  "dueDate": "2025-05-17",
  "priority": "LOW",
  "status": "IN_PROGRESS"
}
```

#### Réponse

```json
{
  "id": 6,
  "name": "Préparer les visuels",
  "description": "Créer les supports visuels",
  "dueDate": "2025-05-17",
  "deadline": null,
  "priority": "LOW",
  "assignee": {
    "id": 1,
    "username": "Sidi",
    "email": "sidi@outlook.fr"
  },
  "status": "IN_PROGRESS"
}
```

> ⚠️ Seuls les membres ou administrateurs du projet peuvent modifier  une tâche Depuis le front mais :
![Remarque ](/uml/nb.png)
>



### 📜 Consulter l’historique d’une tâche

**GET** `/api/projects/3/tasks/6/history`

#### Réponse

```json
[
  {
    "id": 10,
    "task": {
      "id": 6,
      "name": "Une tache creer par Sidi ",
      "description": "Une tache crer par Sidi",
      "dueDate": "2025-05-17",
      "deadline": null,
      "priority": "LOW",
      "assignee": null,
      "status": "IN_PROGRESS"
    },
    "fieldChanged": "status",
    "oldValue": "TODO",
    "newValue": "IN_PROGRESS",
    "modifiedAt": "2025-05-08T17:33:31.189363",
    "modifiedBy": {
      "id": 1,
      "username": "Sidi",
      "email": "sidi@outlook.fr"
    }
  }
]
```
