# 📋 Prérequis pour faire fonctionner le projet

## ✅ Obligatoires

### 1. Java 17 ou supérieur

**Pourquoi ?** Le projet utilise Java 17 (requis par Spring Boot 3.2.0)

**Comment vérifier ?**
```powershell
java -version
```

**Résultat attendu :**
```
openjdk version "17.0.x" ou "21.0.x" ou supérieur
```

**Si pas installé :**
- Téléchargez depuis : https://adoptium.net/ (recommandé) ou https://www.oracle.com/java/technologies/downloads/
- Choisissez **Java 17 LTS** ou **Java 21 LTS**
- Installez et redémarrez votre terminal
- Vérifiez avec `java -version`

**Alternative :** Si vous avez déjà Java 8 ou 11, vous devrez mettre à jour vers Java 17+

---

### 2. Maven 3.6 ou supérieur

**Pourquoi ?** Maven est utilisé pour compiler et gérer les dépendances du projet

**Comment vérifier ?**
```powershell
mvn -version
```

**Résultat attendu :**
```
Apache Maven 3.6.x ou 3.9.x
Maven home: ...
Java version: 17.x
```

**Si pas installé :**
- Téléchargez depuis : https://maven.apache.org/download.cgi
- Choisissez **Binary zip archive** (apache-maven-3.9.x-bin.zip)
- Extrayez dans un dossier (ex: `C:\Program Files\Apache\maven`)
- Ajoutez `C:\Program Files\Apache\maven\bin` à votre **PATH** :
  1. Ouvrez "Variables d'environnement" dans Windows
  2. Modifiez la variable "Path"
  3. Ajoutez le chemin vers le dossier `bin` de Maven
- Redémarrez votre terminal
- Vérifiez avec `mvn -version`

**Alternative rapide :** Si vous avez IntelliJ IDEA ou Eclipse, Maven est souvent inclus

---

## 🔧 Optionnels (mais recommandés)

### 3. Python 3.x (pour le serveur web de l'interface)

**Pourquoi ?** Pour servir l'interface web et éviter les problèmes CORS

**Comment vérifier ?**
```powershell
python --version
```
ou
```powershell
python3 --version
```

**Résultat attendu :**
```
Python 3.8.x ou supérieur
```

**Si pas installé :**
- Téléchargez depuis : https://www.python.org/downloads/
- **Important :** Cochez "Add Python to PATH" lors de l'installation
- Redémarrez votre terminal

**Alternative :** Vous pouvez ouvrir directement `index.html` dans le navigateur (mais peut avoir des problèmes CORS)

---

### 4. Un IDE (optionnel mais très utile)

**Recommandations :**
- **IntelliJ IDEA Community** (gratuit) : https://www.jetbrains.com/idea/download/
- **Eclipse** (gratuit) : https://www.eclipse.org/downloads/
- **VS Code** (gratuit) : https://code.visualstudio.com/ (avec extensions Java)

**Avantages :**
- Compilation automatique
- Gestion Maven intégrée
- Débogage facilité
- Coloration syntaxique

---

## 🧪 Vérification complète

Ouvrez PowerShell et exécutez ces commandes :

```powershell
# Vérifier Java
java -version

# Vérifier Maven
mvn -version

# Vérifier Python (optionnel)
python --version
```

**Résultat idéal :**
```
✅ java version "17.0.x" ou supérieur
✅ Apache Maven 3.6.x ou supérieur
✅ Python 3.8.x ou supérieur (optionnel)
```

---

## 📦 Résumé des versions requises

| Outil | Version minimale | Version recommandée | Où télécharger |
|-------|-----------------|---------------------|----------------|
| **Java** | 17 | 17 LTS ou 21 LTS | https://adoptium.net/ |
| **Maven** | 3.6 | 3.9.x | https://maven.apache.org/download.cgi |
| **Python** | 3.8 (optionnel) | 3.11+ | https://www.python.org/downloads/ |

---

## 🚀 Après installation

Une fois tout installé :

1. **Redémarrez votre terminal** (important !)
2. Vérifiez avec les commandes ci-dessus
3. Lancez le projet avec `DEMARRAGE_RAPIDE.bat`

---

## ❓ Problèmes courants

### "java n'est pas reconnu"
- Java n'est pas dans le PATH
- Réinstallez Java en cochant "Add to PATH"
- Ou ajoutez manuellement le chemin dans les variables d'environnement

### "mvn n'est pas reconnu"
- Maven n'est pas dans le PATH
- Ajoutez le dossier `bin` de Maven dans les variables d'environnement
- Redémarrez le terminal

### "python n'est pas reconnu"
- Python n'est pas dans le PATH
- Réinstallez Python en cochant "Add Python to PATH"
- Ou utilisez l'option d'ouvrir directement `index.html`

### Version Java incorrecte
- Vérifiez avec `java -version`
- Si vous avez plusieurs versions, configurez `JAVA_HOME` vers Java 17
- Dans PowerShell : `$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"`

---

## ✅ Checklist avant de démarrer

- [ ] Java 17+ installé et vérifié (`java -version`)
- [ ] Maven 3.6+ installé et vérifié (`mvn -version`)
- [ ] Python 3.8+ installé (optionnel, `python --version`)
- [ ] Terminal redémarré après installation
- [ ] Tous les outils fonctionnent correctement

**Une fois tout coché, vous êtes prêt ! 🎉**


