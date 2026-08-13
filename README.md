# FitZone — projet de session TCH057

Application Android native en Java permettant de consulter des programmes
d'entraînement, leurs séances et leurs quiz, des conseils nutritionnels ainsi
que le profil de l'utilisateur.

## Démarrage

Prérequis : Android Studio avec un SDK Android compatible avec l'API 36, un
JDK 11 ou plus récent, et Node.js.

1. À la racine du dépôt, lancer le serveur REST :

   ```powershell
   npx json-server fitzone.json --port 3000
   ```

2. Ouvrir le dossier `code` dans Android Studio.
3. Démarrer l'application sur un émulateur Android. L'adresse du serveur est
   déjà configurée à `http://10.0.2.2:3000` pour l'émulateur standard.

Compte de démonstration :

- courriel : `karim@etsmtl.ca`
- mot de passe : `111`

## Vérification en ligne de commande

Depuis le dossier `code` :

```powershell
.\gradlew :app:assembleDebug :app:testDebugUnitTest
.\gradlew :app:lintDebug
```

## Organisation

- `code/app/src/main/java/.../activites` : écrans et interactions Android;
- `adaptateurs` : listes RecyclerView;
- `modeles` : modèles construits depuis les réponses JSON;
- `reseau` : client HTTP partagé;
- `dao` : persistance SQLite;
- `utils` et `vues` : session, statuts, navigation et images;
- `fitzone.json` : données du serveur REST local;
- `output` : rapport de projet aux formats DOCX et PDF.

## Éléments à compléter avant la remise finale

- exporter et joindre les maquettes produites dans Figma;
- remplir le formulaire officiel de déclaration d'utilisation de l'IA fourni
  par le professeur;
- remplacer le package `com.example.fitzone` par le package unique exigé dans
  l'énoncé, après confirmation des noms des membres de l'équipe;
- refaire une recette interactive complète sur un émulateur ou un téléphone.
