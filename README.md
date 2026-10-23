<div align="center">

# Forge ton histoire

**Outil d'aide à l'écriture pour structurer un univers romanesque : personnages, lieux, événements et chronologie**

![Status](https://img.shields.io/badge/status-en%20cours-orange)
![Dernière mise à jour](https://img.shields.io/github/last-commit/farahbouh/Forgetonhistoire?label=derni%C3%A8re%20mise%20%C3%A0%20jour)
![CI](https://img.shields.io/github/actions/workflow/status/farahbouh/Forgetonhistoire/ci.yml?label=CI)

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![API REST](https://img.shields.io/badge/API-REST-0D7377)
![Angular](https://img.shields.io/badge/Angular-interface%20en%20cours-DD0031?logo=angular&logoColor=white)
![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow?logo=conventionalcommits)

[Installation](#mise-en-place) · [API](#api-rest) · [Signaler un bug](../../issues)

</div>

---

## Pourquoi ce projet ?

Application Java pour structurer un univers romanesque, avec gestion des personnages, lieux, événements et chronologie.

Le projet a été conçu pour centraliser les éléments narratifs dans un modèle cohérent et exploitable à travers une API REST et une interface console.

> **Avancement :** le backend en Java est fonctionnel. L'interface Angular qui s'y connecte est en cours de développement.

---

## Stack technique

| | |
|---|---|
| **Backend** | Java 21, sans framework |
| **API REST** | Serveur HTTP intégré au JDK (`com.sun.net.httpserver`), JSON avec Gson |
| **Données** | Fichier `bibliotheque.json`, créé et mis à jour automatiquement par le serveur |
| **Console** | Menu en ligne de commande (`AppConsole`), sans navigateur |
| **Tests** | Programme de tests dans `Main.java` (27 vérifications) |
| **CI** | GitHub Actions : compilation et tests à chaque push |
| **Frontend** | Angular (en cours) |

__*Pourquoi cette stack ?*__

La logique métier a été implémentée en Java pur afin de maîtriser la conception orientée objet sans dépendre d'un framework. Le serveur HTTP intégré au JDK, associé à un fichier JSON, permet d'obtenir une architecture légère, compréhensible et facile à faire évoluer.

Cette approche favorise un contrôle clair sur la logique applicative, la validation des données et la persistance des informations au fil du développement.

---

## Fonctionnalités

- **Organisation** : une bibliothèque contient des livres, chaque livre des univers, chaque univers des personnages, des lieux et des événements
- **Personnages** : dates de naissance et de décès, et vérification de leur cohérence (le décès ne peut pas précéder la naissance)
- **Lieux dans le temps** : un lieu peut changer de nom au fil des époques (par exemple Lugdunum, puis Lyon). Les périodes sont ordonnées et celles qui se chevauchent ou sont inversées sont refusées
- **Liens** : relier personnages, lieux et événements entre eux, sans doublons
- **Calendrier** : une grille de dates triée, bornée, qui refuse les doublons et les dates hors limites
- **Filtrage** : retrouver les livres d'un type donné (Fantastique, Science-Fiction…)
- **Sauvegarde** : toute modification est enregistrée dans un fichier JSON, partagé par l'API et la version console
- **Version console** : utiliser l'application sans navigateur, depuis un menu dans le terminal

---

## API REST

Le serveur écoute sur `http://localhost:8080` et autorise les appels depuis une interface web (CORS).

| Route | Méthodes | Rôle |
|---|---|---|
| `/test` | GET | Vérifier que le serveur répond |
| `/api/livres` | GET, POST | Lister ou créer des livres |
| `/api/univers?livreId=` | GET, POST | Lister ou créer les univers d'un livre |
| `/api/personnages?universId=` | GET, POST, PUT | Lister, créer ou modifier les personnages d'un univers |

Exemple :

```bash
curl -X POST "http://localhost:8080/api/personnages?universId=1" \
  -H "Content-Type: application/json" \
  -d '{"nom":"Baggins","prenom":"Bilbon","description":"Hobbit"}'
# {"statut":"ok","id":7}
```

Au premier lancement, trois livres de démonstration (Le Seigneur des Anneaux, Star Wars, Chroniques parallèles) sont créés.

---

## Mise en place

Il vous faut Java 21 ou plus récent. La bibliothèque Gson est fournie dans le dépôt (`gson.jar`).

**Windows (PowerShell) :**

```powershell
git clone https://github.com/farahbouh/Forgetonhistoire.git
cd Forgetonhistoire
javac -encoding UTF-8 -cp gson.jar -d out Elements/*.java Temporalite/*.java VueGlobale/*.java Main.java ServeurApi.java AppConsole.java
java -cp "out;gson.jar" ServeurApi
```

**Linux / macOS :**

```bash
git clone https://github.com/farahbouh/Forgetonhistoire.git
cd Forgetonhistoire
javac -encoding UTF-8 -cp gson.jar -d out Elements/*.java Temporalite/*.java VueGlobale/*.java Main.java ServeurApi.java AppConsole.java
java -cp "out:gson.jar" ServeurApi
```

Le serveur démarre sur http://localhost:8080 ; testez-le avec `http://localhost:8080/test`.

**Utiliser la version console (sans navigateur) :**

```bash
java -cp "out:gson.jar" AppConsole        # Windows : "out;gson.jar"
```

Un menu permet de lister et créer des livres, des univers et des personnages, et de savoir si un personnage est vivant à une année donnée. Les données sont enregistrées dans le même fichier JSON que l’API.

**Lancer les tests :**

```bash
java -cp out Main
# ... 27 test(s) OK, 0 test(s) en echec.
```

<details>
<summary>Structure du projet</summary>

```bash
.
├── Elements
│   ├── Evenement.java
│   ├── Lien.java
│   ├── Lieux.java
│   └── Personnage.java
├── Temporalite
│   ├── Calendrier.java
│   ├── Compteur.java
│   └── Date.java
├── Verification
│   └── comparateur.java
├── VueGlobale
│   ├── Bibliotheque.java
│   ├── Livre.java
│   └── Univers.java
├── out
│   ├── Elements
│   ├── Temporalite
│   ├── VueGlobale
│   └── production
├── AppConsole.java
├── Main.java
├── ServeurApi.java
├── bibliotheque.json
└── gson.jar
```

</details>

<details>
<summary>Un problème au lancement ?</summary>

- **Port 8080 déjà utilisé** : fermez le programme qui l'occupe, puis relancez le serveur.
- **`package com.google.gson does not exist`** : ajoutez `-cp gson.jar` à la compilation.
- **Caractères accentués illisibles** : ajoutez `-encoding UTF-8` à `javac`.
- **Repartir de zéro** : supprimez `bibliotheque.json` ; les données de démonstration seront recréées.

</details>

---

## Prochaines étapes

- Terminer l'interface Angular et la relier à l'API
- Ajouter la suppression des personnages, ainsi que les routes pour les lieux et les événements

---

## Ce que ce projet m'a appris

- Modéliser des données interconnectées en programmation orientée objet (liens entre personnages, lieux et événements)
- Gérer le temps dans un modèle : périodes, chevauchements, cohérence des dates
- Exposer un modèle Java via une API REST, avec les codes d'erreur HTTP, le CORS et la sérialisation JSON
- Écrire des tests et les faire tourner automatiquement avec une intégration continue

---

<div align="center">

Réalisé par [Farah](https://github.com/farahbouh)

Contact : [LinkedIn](https://www.linkedin.com/in/farahbouh) · [Email](mailto:farah.bouhnaida@gmail.com)

</div>
