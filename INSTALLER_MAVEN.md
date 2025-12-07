# 📥 Guide d'Installation de Maven 3.6+

## 🎯 Objectif
Installer Maven 3.6 ou supérieur pour compiler et gérer les dépendances du projet.

---

## ✅ Méthode 1 : Installation avec Archive ZIP (Recommandé)

### Étape 1 : Télécharger Maven

1. **Ouvrez votre navigateur** et allez sur : **https://maven.apache.org/download.cgi**

2. **Sur la page**, cherchez la section **"Files"**

3. **Téléchargez** :
   - **Fichier** : `apache-maven-3.9.5-bin.zip` (ou version la plus récente)
   - **Section** : "Binary zip archive"
   - **Version** : 3.9.x ou 3.8.x (minimum 3.6.x)

4. **Le fichier se télécharge** (environ 10-15 Mo)

---

### Étape 2 : Extraire Maven

1. **Trouvez le fichier téléchargé** (ex: `apache-maven-3.9.5-bin.zip`)

2. **Faites un clic droit** sur le fichier → **"Extraire tout..."**

3. **Choisissez un emplacement** :
   - **Recommandé** : `C:\Program Files\Apache\maven`
   - Ou : `C:\apache-maven-3.9.5`

4. **Cliquez sur "Extraire"**

5. **Vous devriez avoir** un dossier `apache-maven-3.9.5` (ou similaire)

---

### Étape 3 : Ajouter Maven au PATH

**IMPORTANT** : Maven doit être dans le PATH pour fonctionner.

#### Option A : Via l'interface Windows (Recommandé)

1. **Appuyez sur** `Windows + Pause` (ou clic droit sur "Ce PC" → "Propriétés")

2. **Cliquez sur** "Paramètres système avancés"

3. **Cliquez sur** "Variables d'environnement" (en bas)

4. **Dans "Variables système"**, trouvez **"Path"** et cliquez sur **"Modifier"**

5. **Cliquez sur** "Nouveau"

6. **Ajoutez le chemin** vers le dossier `bin` de Maven :
   ```
   C:\Program Files\Apache\maven\apache-maven-3.9.5\bin
   ```
   (Remplacez par votre chemin exact si différent)

7. **Cliquez sur** "OK" partout

8. **Fermez TOUS les terminaux ouverts**

9. **Ouvrez un NOUVEAU PowerShell**

---

#### Option B : Via PowerShell (Avancé)

Ouvrez PowerShell en **Administrateur** et exécutez :

```powershell
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";C:\Program Files\Apache\maven\apache-maven-3.9.5\bin", [EnvironmentVariableTarget]::Machine)
```

(Remplacez le chemin par votre chemin exact)

---

### Étape 4 : Vérifier l'installation

1. **Fermez TOUS les terminaux**

2. **Ouvrez un NOUVEAU PowerShell**

3. **Tapez** (ou copiez-collez) :
   ```
   mvn -version
   ```

4. **Résultat attendu** :
   ```
   Apache Maven 3.9.5 (ou version similaire)
   Maven home: C:\Program Files\Apache\maven\apache-maven-3.9.5
   Java version: 17.0.x
   Java home: C:\Program Files\Eclipse Adoptium\jdk-17.0.x
   OS name: "windows 10", version: "10.0", arch: "amd64", family: "windows"
   ```

   ✅ **Si vous voyez la version de Maven, c'est bon !**

---

## 🔄 Méthode 2 : Installation avec Chocolatey (Si vous avez Chocolatey)

Si vous avez **Chocolatey** installé :

```powershell
choco install maven
```

---

## 🚨 Problèmes courants

### "mvn n'est pas reconnu" après installation

**Solutions :**

1. **Vérifiez que Maven est extrait** :
   - Allez dans `C:\Program Files\Apache\maven\`
   - Vous devriez voir un dossier `apache-maven-3.9.5` (ou similaire)
   - À l'intérieur, il doit y avoir un dossier `bin` avec `mvn.cmd`

2. **Vérifiez le PATH** :
   - Le chemin doit pointer vers le dossier `bin`
   - Exemple : `C:\Program Files\Apache\maven\apache-maven-3.9.5\bin`
   - **PAS** : `C:\Program Files\Apache\maven\apache-maven-3.9.5` (sans \bin)

3. **Redémarrez le terminal** :
   - Fermez TOUS les terminaux
   - Rouvrez un nouveau terminal
   - Testez avec `mvn -version`

4. **Si ça ne marche toujours pas** :
   - Vérifiez que Java est installé : `java -version`
   - Maven a besoin de Java pour fonctionner

---

### Erreur "JAVA_HOME not set"

**Solution :**

1. **Ouvrez** "Variables d'environnement"

2. **Créez une nouvelle variable système** :
   - Nom : `JAVA_HOME`
   - Valeur : `C:\Program Files\Eclipse Adoptium\jdk-17.0.9+9`
     (Remplacez par votre chemin Java exact)

3. **Cliquez sur** "OK"

4. **Fermez et rouvrez** le terminal

---

### Le téléchargement est lent

**Solution :**
- Utilisez un miroir : https://maven.apache.org/download.cgi
- Cliquez sur un lien "mirror" si disponible
- Ou téléchargez depuis : https://archive.apache.org/dist/maven/maven-3/

---

## ✅ Vérification finale

Après installation, exécutez :

```powershell
mvn -version
java -version
```

**Les deux doivent fonctionner :**
- `mvn -version` → Affiche la version de Maven (3.6+)
- `java -version` → Affiche la version de Java (17+)

---

## 📝 Résumé rapide

1. **Téléchargez** : https://maven.apache.org/download.cgi → Binary zip archive (3.9.x)
2. **Extrayez** : Dans `C:\Program Files\Apache\maven\`
3. **Ajoutez au PATH** : Le chemin vers le dossier `bin` (ex: `C:\Program Files\Apache\maven\apache-maven-3.9.5\bin`)
4. **Fermez** : Tous les terminaux
5. **Rouvrez** : Un nouveau terminal
6. **Vérifiez** : `mvn -version` doit afficher la version

---

## 🎉 C'est prêt !

Une fois Maven installé, vous pouvez compiler le projet avec `mvn clean install` !

---

## 📍 Structure des dossiers (exemple)

```
C:\Program Files\Apache\maven\
└── apache-maven-3.9.5\
    ├── bin\
    │   ├── mvn.cmd          ← C'est ce fichier qui doit être dans le PATH
    │   └── mvn
    ├── boot\
    ├── conf\
    └── lib\
```

Le PATH doit pointer vers : `C:\Program Files\Apache\maven\apache-maven-3.9.5\bin`


