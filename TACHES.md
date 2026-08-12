# FitZone — Répartition des tâches (TCH057)

Règle : la **Partie A** est faite en entier d'abord, puis la **Partie B** démarre sur cette base.
Aucune tâche de B ne peut commencer avant que A soit poussé sur `main` et fonctionnel.

---

## Partie A — Tyler (fondations + entrée dans l'app) — ✅ TERMINÉE

Code livré dans `code/`. Voir « Contrat A → B » en bas du fichier avant de commencer la partie B.


### A0. Maquettes Figma des écrans de A
Connexion, Inscription, Accueil/Tableau de bord, Liste des programmes.
Export PDF. Critères ergonomiques (Bastien & Scapin ou Nielsen) respectés.

### A1. Création du projet Android Studio
- Projet Java (pas Kotlin), `minSdk` et `targetSdk` notés pour le rapport.
- Package de base `com.nom1_nom2.fitzone`.
- Sous-packages : `activites`, `modeles`, `adaptateurs`, `dao`, `reseau`, `utils`.
- Permission `INTERNET` + `usesCleartextTraffic` pour le serveur local.

### A2. Serveur JSON opérationnel
- `npx json-server --watch fitzone.json --port 3000 --host 0.0.0.0`.
- Vérifier les endpoints : `/users`, `/programs`, `/quizzes`, `/seances`.
- Documenter l'URL de base : `10.0.2.2:3000` (émulateur) ou l'IP locale (téléphone réel).

### A3. Classes modèles (`modeles`)
`User`, `Program`, `Quiz`, `Question`, `QuizResult`, `Seance`, `Aliment` (créer la classe même si l'écran nutrition est en B).
Getters/setters + parsing depuis JSON.

### A4. Couche réseau (`reseau`) — **base commune**
- Client HTTP unique (GET / POST / PATCH) en tâche d'arrière-plan.
- Méthodes génériques réutilisables par tous les écrans de B.
- ⚠️ Choix de la techno (HttpURLConnection natif vs Retrofit/Volley/Gson) à valider avec le prof/l'équipe avant de coder.

### A5. Base SQLite (`dao`) — **base commune**
- `SQLiteOpenHelper` + création des tables : utilisateur connecté (cache), résultats de quiz, état local des séances, historique de consultation.
- DAO avec les méthodes CRUD que B utilisera (quiz + séances).

### A6. Écran Connexion
- Saisie courriel + mot de passe, validation contre `/users` du serveur.
- Message explicite en cas d'échec (« Courriel ou mot de passe incorrect »).
- Sauvegarde de la session (SharedPreferences ou SQLite) → **B en dépend pour connaître l'utilisateur courant**.
- Redirection vers l'Accueil.

### A7. Écran Inscription
- Prénom, nom, courriel, mot de passe, téléphone, photo de profil (URL).
- Validation des champs + POST vers `/users` (avec `enrolledProgramIds`, `quizResults`, `completedSeanceIds` vides).

### A8. Écran Accueil / Tableau de bord
- Programmes inscrits, séances à venir, quiz disponibles, annonces récentes.
- Résumé du statut (complétées / en retard / validées).
- **Navigation globale** (BottomNavigation ou menu) avec les entrées vides/placeholder vers les écrans de B → B n'a qu'à brancher ses activités.

### A9. Écran Liste des programmes
- Adaptateur RecyclerView : code, nom, coach, session, image.
- Recherche par nom/code.
- Filtrage (tous / actifs / terminés).
- Clic → ouvre l'écran Détails (Intent avec l'`id` du programme) — **le contrat d'Intent est défini ici, B l'implémente**.

**Fin de A : commit + push d'une app qui se lance, se connecte, affiche le tableau de bord et la liste des programmes.**

---

## Partie B — Collègue (écrans de contenu + livrables)

### B0. Maquettes Figma des écrans de B
Détails programme, Séances (liste + détail), Quiz, Conseils nutritionnels, Profil. Export PDF, puis fusion avec le PDF de A.

### B1. Écran Détails d'un programme
- Reçoit l'`id` via l'Intent défini en A9.
- Sections : description, coach, annonces, séances, quiz, ressources/notes.
- Navigation claire entre les sections (onglets, ancres ou cartes dépliables).

### B2. Écran Séances (liste)
- Liste des séances du programme : titre, date limite, statut, note si disponible.
- Statuts : À faire / Soumise / En retard / Validée (le « En retard » se calcule à partir de `dueDate`).
- Adaptateur + code couleur des statuts.

### B3. Détail d'une séance + soumission simulée
- Description, consignes, date limite, statut, note et commentaire du coach.
- Soumission : lien URL ou texte de compte-rendu + bouton « Marquer comme soumise ».
- Passage du statut à « Soumise », enregistrement de la date/heure, persistance via le DAO de A5 (et/ou PATCH serveur).

### B4. Écran Quiz
- Liste des quiz du programme avec statut (non commencé / terminé) et nombre de questions.
- Ouverture d'un quiz, affichage des questions, validation des réponses.
- Calcul du score + affichage du résultat, enregistrement dans SQLite (DAO de A5).

### B5. Conseils nutritionnels
- Ajouter une collection `nutrition` dans `fitzone.json` (les 10 aliments de l'énoncé : gruau, banane, amandes, œufs, poulet grillé, yogourt grec, pomme, salade de quinoa, saumon, avocat) avec image, nom, description, calories, moment recommandé.
- Écran liste avec image + nom + description courte + calories + moment de consommation.

### B6. Écran Profil utilisateur
- Affichage des infos de l'utilisateur connecté (session de A6).
- Modification : prénom, nom, téléphone, photo (URL), mot de passe ; courriel en lecture seule.
- PATCH vers `/users/{id}` + mise à jour du cache local.

### B7. Rapport PDF (~5 pages)
Description de l'application, choix d'architecture/navigation/organisation des classes, état du projet (fonctionnel / partiel + problèmes rencontrés), versions SDK Android et Gradle.

### B8. Déclaration d'utilisation de l'IA (PDF séparé)
Modèle fourni par le prof — **son absence = pénalité automatique de -50 %**.

### B9. Archive finale
`.zip` contenant : code source complet, maquettes Figma en PDF, rapport PDF, déclaration IA PDF. Dépôt dans la section Projet sur ENA.

---

## Équilibre

| | Partie A | Partie B |
|---|---|---|
| Écrans | 4 | 6 |
| Infrastructure | projet, réseau, SQLite, navigation | — |
| Livrables docs | maquettes A | maquettes B, rapport, déclaration IA, archive |

A porte la mise en place technique (plus lente, moins visible), B porte le volume d'écrans et la documentation finale.

---

## Contrat A → B

Tout ce qui suit existe déjà et **doit être réutilisé tel quel**. Ne pas créer de deuxième mécanisme à côté.

### Lancer le serveur
Depuis la racine du repo :
```
npx json-server fitzone.json --port 3000 --host 0.0.0.0
```
L'app y accède via `http://10.0.2.2:3000` (émulateur). **Le rechargement à chaud ne fonctionne pas** : après une modification manuelle de `fitzone.json`, redémarrer le serveur.

### Source de vérité des données

| Donnée | Où |
|---|---|
| `programs`, `quizzes`, `seances` | Serveur JSON, lecture seule |
| `users` | Serveur JSON (POST à l'inscription, PUT au profil) |
| Résultats de quiz | SQLite (`ResultatQuizDao`) |
| État local des séances | SQLite (`EtatSeanceDao`) |
| Id de l'utilisateur connecté | SharedPreferences (`SessionManager`) |

### `reseau.ApiClient` (statique)
```java
ApiClient.get(String path, ApiCallback callback);
ApiClient.post(String path, JSONObject body, ApiCallback callback);
ApiClient.put(String path, JSONObject body, ApiCallback callback);
```
`ApiCallback` : `onSuccess(String body)` / `onError(String message)`, tous deux appelés sur le thread principal.

- **Pas de `PATCH`** : `HttpURLConnection` ne le supporte pas sur Android. Pour modifier le profil (B6), envoyer `put("/users/" + id, user.toJson(), …)` avec l'objet **complet** (json-server remplace l'item).
- `onError` = serveur injoignable ou code HTTP ≥ 400. Une requête qui ne trouve rien renvoie HTTP 200 avec `[]` → ce n'est **pas** une erreur, c'est un cas métier à traiter dans `onSuccess`.

### `dao`
```java
new UtilisateurDao(context).sauvegarder(User user);
new UtilisateurDao(context).obtenir(String userId);      // null si absent
new UtilisateurDao(context).supprimer(String userId);

new ResultatQuizDao(context).enregistrer(String userId, QuizResult resultat);
new ResultatQuizDao(context).obtenir(String userId, String quizId);   // null si non fait
new ResultatQuizDao(context).obtenirTous(String userId);

new EtatSeanceDao(context).enregistrer(String userId, String seanceId, String statut, String dateSoumission, String contenu);
new EtatSeanceDao(context).obtenirStatut(String userId, String seanceId);
new EtatSeanceDao(context).obtenirDateSoumission(String userId, String seanceId);
new EtatSeanceDao(context).obtenirStatuts(String userId);   // Map<seanceId, statut>
```
Base `fitzone.db` version 1 (`BaseSQLite`). Ajouter une colonne = incrémenter `VERSION`.

### `utils`
```java
new SessionManager(context).obtenirUserId();   // null si déconnecté
new SessionManager(context).estConnecte();
new SessionManager(context).ouvrirSession(String userId);
new SessionManager(context).fermerSession();

StatutSeance.calculer(Seance seance, String statutLocal);   // renvoie une des 4 constantes
StatutSeance.A_FAIRE / SOUMISE / EN_RETARD / VALIDEE
StatutSeance.aujourdhui();   // "yyyy-MM-dd"
```
Règle de priorité déjà implémentée : note du coach → soumission locale → échéance dépassée → à faire. **B2 et B3 doivent appeler `calculer`, pas réimplémenter la règle.**

### Navigation (contrat d'Intent)
```java
Intent intent = new Intent(this, DetailsProgrammeActivity.class);
intent.putExtra(DetailsProgrammeActivity.EXTRA_PROGRAMME_ID, programme.getId());
```
`DetailsProgrammeActivity` existe en **stub** (affiche l'id reçu) : B1 remplit son contenu, garde la constante `EXTRA_PROGRAMME_ID` et le nom de classe. Toute nouvelle activité doit être déclarée dans `AndroidManifest.xml`.

### Adaptateurs réutilisables
- `ProgrammeAdapter(List<Program>, SurClicProgramme)` — item : code, titre, coach, session.
- `SeanceApercuAdapter(List<Seance>, Map<Seance, String> statuts)` — aperçu du tableau de bord.

### Modifications faites à `fitzone.json`
- Ajout d'un champ `statut` (`"actif"` / `"termine"`) sur chaque programme : sans ça le filtre exigé pour l'écran Liste des programmes n'a aucune source. Lu par `Program.getStatut()`.
- Aucune collection `nutrition` : c'est B5 qui la crée et qui écrit la classe `Aliment` correspondante.

### Non fait volontairement (à la charge de B ou à trancher)
- **Images des programmes** : les `imageUrl` de `fitzone.json` pointent vers `example.com` (inexistant), et charger une image distante sans bibliothèque externe demande du code manuel. L'énoncé marque l'image comme optionnelle sur cet écran.
- **Reconnexion automatique** : la session est conservée, mais l'écran de connexion ne redirige pas tout seul vers l'accueil.
- **Contrôle des courriels en double à l'inscription** : deux comptes avec le même courriel sont acceptés par le serveur, la connexion prendrait le premier.
