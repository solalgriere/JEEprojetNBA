# 📥 Guide d'Installation de Java 17

## 🎯 Objectif
Installer Java 17 (ou supérieur) pour faire fonctionner le projet NBA Actor Framework.

---

## ✅ Méthode 1 : Installation avec Adoptium (Recommandé - Gratuit)

### Étape 1 : Télécharger Java 17

1. **Ouvrez votre navigateur** et allez sur : **https://adoptium.net/**

2. **Sur la page d'accueil**, vous verrez :
   - **Version** : Choisissez **17 LTS** ou **21 LTS** (recommandé : 17 LTS)
   - **Operating System** : **Windows**
   - **Architecture** : **x64** (pour la plupart des PC)
   - **Package Type** : **JDK** (Java Development Kit)

3. **Cliquez sur "Latest release"** ou le bouton de téléchargement

4. **Le fichier se télécharge** (ex: `OpenJDK17U-jdk_x64_windows_hotspot_17.0.9_9.msi`)

---

### Étape 2 : Installer Java

1. **Double-cliquez sur le fichier téléchargé** (.msi)

2. **Suivez l'installation** :
   - Cliquez sur "Next" / "Suivant"
   - Acceptez les termes (si demandé)
   - **IMPORTANT** : Cochez **"Add to PATH"** ou **"Set JAVA_HOME variable"** si proposé
   - Choisissez le dossier d'installation (par défaut : `C:\Program Files\Eclipse Adoptium\`)
   - Cliquez sur "Install" / "Installer"

3. **Attendez la fin de l'installation** (1-2 minutes)

4. **Cliquez sur "Finish" / "Terminer"**

---

### Étape 3 : Vérifier l'installation

1. **Fermez TOUS les terminaux ouverts** (important !)

2. **Ouvrez un NOUVEAU PowerShell** ou Invite de commandes

3. **Tapez** (ou copiez-collez) :
   ```
   java -version
   ```

4. **Résultat attendu** :
   ```
   openjdk version "17.0.9" 2023-10-17
   OpenJDK Runtime Environment Temurin-17.0.9+9 (build 17.0.9+9)
   OpenJDK 64-Bit Server VM Temurin-17.0.9+9 (build 17.0.9+9, mixed mode, sharing)
   ```

   ✅ **Si vous voyez "17" ou "21" dans la version, c'est bon !**

---

## 🔄 Méthode 2 : Si vous avez déjà une autre version de Java

### Option A : Désinstaller l'ancienne version (recommandé)

1. **Ouvrez "Paramètres"** → **"Applications"** → **"Applications et fonctionnalités"**

2. **Recherchez "Java"** dans la liste

3. **Désinstallez les anciennes versions** :
   - Java 8, Java 11, etc.
   - Oracle Java (si présent)

4. **Installez Java 17** avec la Méthode 1 ci-dessus

---

### Option B : Garder plusieurs versions (avancé)

1. **Installez Java 17** avec la Méthode 1

2. **Configurez JAVA_HOME** :
   - Ouvrez "Variables d'environnement"
   - Créez/modifiez `JAVA_HOME` → `C:\Program Files\Eclipse Adoptium\jdk-17.0.9+9`
   - Modifiez `Path` → Ajoutez `%JAVA_HOME%\bin` en premier

3. **Redémarrez le terminal**

---

## 🚨 Problèmes courants

### "java n'est pas reconnu" après installation

**Solution :**
1. Vérifiez que Java est installé :
   - Allez dans `C:\Program Files\Eclipse Adoptium\`
   - Vous devriez voir un dossier `jdk-17.x.x`

2. **Ajoutez Java au PATH manuellement** :
   - Appuyez sur **Windows + Pause** (ou clic droit sur "Ce PC" → "Propriétés")
   - Cliquez sur **"Paramètres système avancés"**
   - Cliquez sur **"Variables d'environnement"**
   - Dans "Variables système", trouvez **"Path"** et cliquez sur **"Modifier"**
   - Cliquez sur **"Nouveau"**
   - Ajoutez : `C:\Program Files\Eclipse Adoptium\jdk-17.0.9+9\bin` (remplacez par votre version)
   - Cliquez sur **"OK"** partout
   - **Fermez et rouvrez votre terminal**

---

### Plusieurs versions de Java installées

**Solution :**
1. Vérifiez quelle version est utilisée :
   ```
   java -version
   ```

2. Si ce n'est pas Java 17, modifiez le PATH (voir ci-dessus) pour mettre Java 17 en premier

---

### L'installation ne démarre pas

**Solution :**
1. **Téléchargez la version .zip** au lieu de .msi :
   - Sur Adoptium, choisissez **"Package Type: JDK"** et **"Archive"**
   - Extrayez dans `C:\Program Files\Java\`
   - Ajoutez manuellement au PATH (voir ci-dessus)

---

## ✅ Vérification finale

Après installation, exécutez :

```powershell
java -version
javac -version
```

**Les deux doivent afficher la version 17 ou supérieure.**

---

## 📝 Résumé rapide

1. **Téléchargez** : https://adoptium.net/ → Java 17 LTS
2. **Installez** : Double-cliquez sur le .msi
3. **Cochez** : "Add to PATH" si proposé
4. **Fermez** : Tous les terminaux
5. **Rouvrez** : Un nouveau terminal
6. **Vérifiez** : `java -version` doit afficher "17" ou "21"

---

## 🎉 C'est prêt !

Une fois Java 17 installé, vous pouvez lancer le projet avec `DEMARRAGE_RAPIDE.bat` !


