# LAB 14 - Sauvegarde des données : SharedPreferences et fichiers

## Description

Ce projet est une application Android développée en Java dans le cadre du cours **Programmation Mobile : Android avec Java**.

Le laboratoire porte sur la sauvegarde locale des données sous Android à travers plusieurs mécanismes de persistance :

- `SharedPreferences`
- `EncryptedSharedPreferences`
- Fichiers internes
- Cache applicatif
- Stockage externe app-specific

L’objectif principal est de comprendre comment sauvegarder, lire, exporter et supprimer des données localement tout en respectant des bonnes pratiques de sécurité.

L’application fonctionne hors Internet et applique une logique de protection des données sensibles, notamment en évitant le stockage de secrets en clair et en empêchant l’affichage du token dans Logcat.

## Objectifs du laboratoire

Ce lab permet de mettre en pratique les notions suivantes :

- Écrire et lire des préférences avec `SharedPreferences`
- Comprendre la différence entre `apply()` et `commit()`
- Stocker un token de manière sécurisée avec `EncryptedSharedPreferences`
- Utiliser `MasterKey` avec AndroidX Security Crypto
- Écrire et lire des fichiers internes en UTF-8
- Sauvegarder et charger une liste d’objets en JSON
- Utiliser `cacheDir` pour stocker des données temporaires
- Exporter un fichier vers le stockage externe app-specific
- Nettoyer toutes les données stockées
- Appliquer des bonnes pratiques de sécurité mobile

## Fonctionnalités réalisées

L’application permet de :

- Saisir un nom utilisateur
- Sélectionner une langue
- Choisir un thème
- Saisir un token secret
- Sauvegarder les préférences non sensibles
- Sauvegarder le token de manière chiffrée
- Charger les préférences sauvegardées
- Créer un fichier interne `note.txt`
- Créer un fichier JSON `students.json`
- Lire les fichiers internes
- Écrire un fichier temporaire dans le cache
- Lire le fichier cache
- Exporter un fichier `export.txt` vers le stockage externe app-specific
- Lire le fichier exporté
- Supprimer toutes les données stockées

## Technologies utilisées

- Java
- Android Studio
- XML
- Android SDK
- AppCompat
- Material Components
- SharedPreferences
- EncryptedSharedPreferences
- AndroidX Security Crypto
- MasterKey
- Internal Storage
- Cache Directory
- App-specific External Storage
- JSON avec `org.json`

## Structure du projet

```text
SecureStorageLabJava/
│
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/
│           │       └── example/
│           │           └── securestoragelabjava/
│           │               │
│           │               ├── ui/
│           │               │   └── MainActivity.java
│           │               │
│           │               ├── prefs/
│           │               │   ├── AppPrefs.java
│           │               │   └── SecurePrefs.java
│           │               │
│           │               ├── files/
│           │               │   ├── InternalTextStore.java
│           │               │   └── StudentsJsonStore.java
│           │               │
│           │               ├── cache/
│           │               │   └── CacheStore.java
│           │               │
│           │               ├── external/
│           │               │   └── ExternalAppFilesStore.java
│           │               │
│           │               └── model/
│           │                   └── Student.java
│           │
│           ├── res/
│           │   ├── layout/
│           │   │   └── activity_main.xml
│           │   │
│           │   └── values/
│           │       └── themes.xml
│           │
│           └── AndroidManifest.xml
│
├── README.md
└── .gitignore

## Description des fichiers principaux

### MainActivity.java

`MainActivity.java` est l’activité principale de l’application.

Elle gère l’interface utilisateur et appelle les différentes classes responsables du stockage local.

Elle permet de :

* Sauvegarder les préférences classiques
* Charger les préférences sauvegardées
* Sauvegarder un token chiffré
* Créer des fichiers internes
* Lire les fichiers internes
* Écrire et lire un fichier cache
* Exporter un fichier dans le stockage externe app-specific
* Supprimer toutes les données

Le token n’est jamais affiché en clair.
L’application affiche uniquement sa longueur afin de vérifier qu’il est bien stocké sans exposer sa valeur.

Exemple correct :

```text
tokenLength=16
```

Exemple à éviter :

```text
token=mySecretToken123
```

### AppPrefs.java

`AppPrefs.java` gère les préférences non sensibles avec `SharedPreferences`.

Les données stockées sont :

* Nom utilisateur
* Langue
* Thème

Le stockage utilise :

```java
Context.MODE_PRIVATE
```

La méthode `save()` permet de choisir entre :

```java
apply()
```

et :

```java
commit()
```

`apply()` est asynchrone et ne retourne pas de résultat.
Il est recommandé pour des préférences simples liées à l’interface.

`commit()` est synchrone et retourne `true` ou `false`.
Il peut être utilisé lorsqu’une confirmation immédiate de l’écriture est nécessaire.

### SecurePrefs.java

`SecurePrefs.java` gère le stockage sécurisé du token.

Cette classe utilise :

* `MasterKey`
* `EncryptedSharedPreferences`
* Chiffrement des clés
* Chiffrement des valeurs

Le token est stocké dans des préférences chiffrées et n’est jamais écrit en clair dans Logcat.

Le stockage sécurisé repose sur AndroidX Security Crypto.

### Student.java

`Student.java` est une classe modèle simple représentant un étudiant.

Elle contient :

* `id`
* `name`
* `age`

Elle est utilisée pour créer une liste d’étudiants sauvegardée dans un fichier JSON.

### InternalTextStore.java

`InternalTextStore.java` gère les fichiers texte internes.

Il permet de :

* Écrire un fichier texte en UTF-8
* Lire un fichier texte en UTF-8
* Supprimer un fichier interne

Les fichiers internes sont privés à l’application et ne nécessitent aucune permission.

### StudentsJsonStore.java

`StudentsJsonStore.java` permet de sauvegarder et charger une liste d’étudiants au format JSON.

Le fichier créé est :

```text
students.json
```

Si le fichier est absent ou corrompu, l’application retourne une liste vide au lieu de planter.

Cette approche permet d’éviter un crash de l’application en cas de fichier supprimé ou invalide.

### CacheStore.java

`CacheStore.java` gère le cache applicatif.

Il permet de :

* Écrire un fichier temporaire
* Lire un fichier temporaire
* Purger le cache

Le cache est réservé aux données temporaires et régénérables.
Il ne doit pas contenir de secrets ou de données sensibles importantes.

### ExternalAppFilesStore.java

`ExternalAppFilesStore.java` gère le stockage externe app-specific.

Il permet de créer et lire un fichier :

```text
export.txt
```

Ce fichier est stocké dans un dossier externe propre à l’application.

Aucune permission de stockage n’est nécessaire pour ce type de stockage, car il reste lié à l’application.

## Fichiers créés par l’application

| Fichier            | Emplacement                   | Rôle                                   |
| ------------------ | ----------------------------- | -------------------------------------- |
| `app_prefs.xml`    | SharedPreferences internes    | Stockage des préférences non sensibles |
| `secure_prefs.xml` | SharedPreferences chiffrées   | Stockage sécurisé du token             |
| `note.txt`         | Stockage interne              | Fichier texte UTF-8                    |
| `students.json`    | Stockage interne              | Liste d’étudiants en JSON              |
| `last_ui.txt`      | Cache                         | Donnée temporaire                      |
| `export.txt`       | Stockage externe app-specific | Export contrôlé                        |

## Emplacements de stockage

### Fichiers internes

```text
/data/data/com.example.securestoragelabjava/files/
```

Exemples :

```text
/data/data/com.example.securestoragelabjava/files/note.txt
/data/data/com.example.securestoragelabjava/files/students.json
```

### SharedPreferences

```text
/data/data/com.example.securestoragelabjava/shared_prefs/
```

Exemples :

```text
/data/data/com.example.securestoragelabjava/shared_prefs/app_prefs.xml
/data/data/com.example.securestoragelabjava/shared_prefs/secure_prefs.xml
```

### Cache

```text
/data/data/com.example.securestoragelabjava/cache/
```

Exemple :

```text
/data/data/com.example.securestoragelabjava/cache/last_ui.txt
```

### Stockage externe app-specific

```text
/storage/emulated/0/Android/data/com.example.securestoragelabjava/files/
```

Exemple :

```text
/storage/emulated/0/Android/data/com.example.securestoragelabjava/files/export.txt
```

## Fonctionnement de l’application

Au lancement, l’application affiche une interface contenant :

* Un champ pour le nom
* Une liste de langues
* Un switch de thème
* Un champ de token masqué
* Plusieurs boutons de test
* Une zone de résultat

L’utilisateur peut saisir ses informations, sauvegarder les données, fermer l’application, puis relancer et vérifier que les préférences sont restaurées.

Le token est sauvegardé de manière sécurisée avec `EncryptedSharedPreferences`.

Le texte et le JSON sont sauvegardés dans le stockage interne.

Le cache est utilisé pour stocker un fichier temporaire.

Le stockage externe app-specific est utilisé pour exporter un fichier sans permission de stockage.

## Sécurité appliquée

Les règles de sécurité appliquées dans ce projet sont :

* Ne pas stocker de token dans des `SharedPreferences` classiques
* Utiliser `EncryptedSharedPreferences` pour les secrets
* Utiliser `MasterKey` avec AndroidX Security Crypto
* Ne jamais afficher le token en clair
* Ne jamais logger le token dans Logcat
* Afficher uniquement la longueur du token
* Utiliser `MODE_PRIVATE` pour les préférences et fichiers internes
* Utiliser le cache uniquement pour des données temporaires
* Utiliser le stockage externe app-specific et non un stockage public
* Prévoir un bouton de nettoyage complet
* Supprimer les fichiers de test à la fin si nécessaire

## Exemple de log sécurisé

Exemple correct :

```text
tokenLength=16
```

Exemple incorrect :

```text
token=mySecretToken123
```

## Dépendances principales

Dans le fichier `build.gradle.kts` du module `app`, les dépendances importantes sont :

```kotlin
implementation("androidx.appcompat:appcompat:1.7.0")
implementation("com.google.android.material:material:1.12.0")
implementation("androidx.constraintlayout:constraintlayout:2.2.0")
implementation("androidx.security:security-crypto:1.1.0-alpha06")
```

## Prérequis d’exécution

Pour exécuter le projet, il faut :

* Android Studio installé
* JDK configuré
* Gradle synchronisé
* Un émulateur Android ou un appareil Android
* Min SDK 24 ou supérieur
* AndroidX Security Crypto disponible

## Exécution du projet

1. Cloner le dépôt :

```bash
git clone https://github.com/wiiam8/SecureStorageLabJava.git
```

2. Ouvrir le projet avec Android Studio.

3. Lancer la synchronisation Gradle.

4. Exécuter l’application sur un émulateur ou un appareil Android.

5. Tester les actions suivantes :

```text
1. Saisir un nom utilisateur
2. Sélectionner une langue
3. Choisir le thème
4. Saisir un token
5. Sauvegarder les préférences et le token chiffré
6. Charger les préférences
7. Sauvegarder les fichiers internes JSON et note
8. Charger les fichiers internes
9. Écrire dans le cache
10. Lire le cache
11. Exporter vers le stockage externe app-specific
12. Lire le fichier externe
13. Effacer toutes les données
```

## Résultat attendu

Après exécution, l’application doit permettre de :

* Sauvegarder les préférences non sensibles
* Restaurer le nom, la langue et le thème
* Stocker un token chiffré
* Afficher seulement la longueur du token
* Créer `note.txt`
* Créer `students.json`
* Charger la liste des étudiants
* Créer un fichier temporaire dans le cache
* Lire le fichier cache
* Exporter `export.txt`
* Relire `export.txt`
* Supprimer toutes les données

## Vérification avec Device File Explorer

Dans Android Studio, ouvrir :

```text
View > Tool Windows > Device Explorer
```

Vérifier les chemins suivants :

```text
/data/data/com.example.securestoragelabjava/files/note.txt
/data/data/com.example.securestoragelabjava/files/students.json
/data/data/com.example.securestoragelabjava/shared_prefs/app_prefs.xml
/data/data/com.example.securestoragelabjava/shared_prefs/secure_prefs.xml
/data/data/com.example.securestoragelabjava/cache/
```

Pour le stockage externe app-specific :

```text
/storage/emulated/0/Android/data/com.example.securestoragelabjava/files/export.txt
```

## Captures recommandées

Pour documenter le lab, il est recommandé d’ajouter des captures montrant :

* L’interface principale de l’application
* La sauvegarde des préférences
* Le chargement des préférences
* La sauvegarde du fichier JSON
* La lecture des fichiers internes
* L’écriture dans le cache
* La lecture du cache
* L’export externe app-specific
* Device File Explorer avec `note.txt` et `students.json`
* Logcat montrant `tokenLength` sans afficher le token



## Bilan pédagogique

Ce laboratoire permet de comprendre les mécanismes de stockage local sous Android.

Il montre la différence entre un stockage simple et un stockage sécurisé. Les préférences non sensibles peuvent être stockées avec `SharedPreferences`, tandis que les secrets doivent être protégés avec `EncryptedSharedPreferences`.

Le lab montre aussi comment manipuler des fichiers internes, sauvegarder des données en JSON, utiliser un cache temporaire et exporter un fichier dans un stockage externe propre à l’application.

Les notions principales abordées sont :

* `SharedPreferences`
* `apply()`
* `commit()`
* `EncryptedSharedPreferences`
* `MasterKey`
* Stockage interne
* Fichiers UTF-8
* JSON local
* `cacheDir`
* Stockage externe app-specific
* Nettoyage des données
* Bonnes pratiques de sécurité mobile



## Lab

LAB 14 : Sauvegarde des données – SharedPreferences et fichiers avec bonnes pratiques de sécurité
