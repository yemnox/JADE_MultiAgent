# Rapport d'Installation de JADE (Java Agent DEvelopment Framework)

## 1. Contexte

**Environnement :**
- Système d'exploitation : Ubuntu 24.04
- Environnement graphique : Wayland avec Xwayland
- Java Runtime : OpenJDK 17.0.17
- Java Compiler : javac 17.0.17
- Version JADE : 4.6.0 (révision 6869)

## 2. Étapes d'Installation

### 2.1 Téléchargement et Extraction
```bash
# Télécharger JADE depuis https://jade.tilab.com/download/jade/
mkdir -p ~/jade
cd ~/jade
# Extraire l'archive téléchargée
unzip jade-bin-*.zip  # ou tar -xzf jade-bin-*.tar.gz
```

### 2.2 Vérification de l'Installation
```bash
# Vérifier la présence du fichier JAR principal
ls ~/jade/jade/lib/jade.jar
```

### 2.3 Compilation des Exemples
**Problème rencontré :** Erreurs de compilation dues à des dépendances manquantes (bibliothèque JESS).

**Solution :** Exclure les exemples nécessitant JESS lors de la compilation.

```bash
cd ~/jade/jade/src/examples

# Créer une liste de fichiers Java en excluant les dépendances JESS
find . -name "*.java" \
  ! -path "./JadeJessProtege/*" \
  ! -path "./jess/*" \
  > sources.txt

# Compiler les exemples compatibles
javac -cp ~/jade/jade/lib/jade.jar:. @sources.txt
```

## 3. Problèmes Rencontrés et Solutions

### 3.1 Incompatibilité des Versions Java
**Problème :** Désynchronisation entre javac (version 17) et java (version 21).

**Solution :**
```bash
# Aligner java sur la version 17
sudo update-alternatives --config java
# Sélectionner : /usr/lib/jvm/java-17-openjdk-amd64/bin/java

# Vérification
java --version    # Doit afficher 17.0.17
javac --version   # Doit afficher 17.0.17
```

### 3.2 Erreur HeadlessException (Interface Graphique)
**Problème :** `java.awt.HeadlessException: No X11 DISPLAY variable was set`

**Cause :** Environnement Wayland nécessitant une configuration spécifique pour X11.

**Solution :**
```bash
# Configuration des variables d'environnement
export DISPLAY=:0
export GDK_BACKEND=x11
```

### 3.3 Bibliothèques Graphiques Manquantes
**Problème :** `Can't load library: libawt_xawt.so`

**Solution :**
```bash
# Installation des dépendances graphiques
sudo apt update
sudo apt install openjdk-17-jdk libxrender1 libxtst6 libxi6
```

## 4. Lancement de JADE

### 4.1 Lancement avec Interface Graphique (GUI)
```bash
# Configuration de l'environnement
export DISPLAY=:0
export GDK_BACKEND=x11

# Lancement de JADE avec GUI
cd ~/jade/jade/src/examples
java -cp ~/jade/jade/lib/jade.jar:. jade.Boot -gui
```

### 4.2 Lancement avec un Agent
```bash
# Lancement de JADE avec un agent spécifique (exemple : ThanksAgent)
java -cp ~/jade/jade/lib/jade.jar:. jade.Boot -gui \
  -agents "thanks:examples.thanksAgent.ThanksAgent"
```

### 4.3 Lancement sans Interface Graphique
```bash
# Mode console uniquement
java -cp ~/jade/jade/lib/jade.jar:. jade.Boot \
  -agents "thanks:examples.thanksAgent.ThanksAgent"
```

## 5. Vérification du Fonctionnement

**Logs attendus lors du démarrage :**
```
This is JADE 4.6.0 - revision 6869 of 30-11-2022 14:47:03
downloaded in Open Source, under LGPL restrictions,
at http://jade.tilab.com/
----------------------------------------
Listening for intra-platform commands on address:
- jicp://172.17.0.1:1099

Service jade.core.management.AgentManagement initialized
Service jade.core.messaging.Messaging initialized
Service jade.core.resource.ResourceManagement initialized
Service jade.core.mobility.AgentMobility initialized
Service jade.core.event.Notification initialized

MTP addresses:
http://LOQ:7778/acc

Agent container Main-Container@172.17.0.1 is ready.
```

**Interface graphique :** La fenêtre principale de JADE s'affiche avec la liste des agents actifs.

## 6. Structure des Commandes JADE

### Syntaxe Générale
```bash
java -cp <classpath> jade.Boot [options] [-agents <agent-list>]
```

### Options Principales
- `-gui` : Lance l'interface graphique RMA (Remote Monitoring Agent)
- `-agents "name:class"` : Crée un agent avec un nom et une classe spécifiques
- `-host <hostname>` : Spécifie l'hôte du conteneur principal
- `-port <port>` : Spécifie le port de communication

### Exemples de Commandes
```bash
# GUI seule
java -cp ~/jade/jade/lib/jade.jar jade.Boot -gui

# Plusieurs agents
java -cp ~/jade/jade/lib/jade.jar:. jade.Boot -gui \
  -agents "agent1:package.Class1;agent2:package.Class2"

# Sans GUI
java -cp ~/jade/jade/lib/jade.jar:. jade.Boot \
  -agents "myagent:package.MyAgent"
```

## 7. Configuration Permanente (Optionnel)

Pour éviter de réexporter les variables à chaque session :

```bash
# Ajouter au fichier ~/.bashrc
echo 'export GDK_BACKEND=x11' >> ~/.bashrc
echo 'export DISPLAY=:0' >> ~/.bashrc

# Créer un alias pour JADE
echo 'alias jade-gui="cd ~/jade/jade/src/examples && java -cp ~/jade/jade/lib/jade.jar:. jade.Boot -gui"' >> ~/.bashrc

# Recharger la configuration
source ~/.bashrc
```

## 8. Conclusion

L'installation de JADE sur Ubuntu avec environnement Wayland nécessite :
1. L'alignement des versions de Java (javac et java)
2. La configuration des variables d'environnement pour X11
3. L'installation des bibliothèques graphiques nécessaires
4. L'exclusion des exemples avec dépendances externes lors de la compilation

Une fois ces étapes complétées, JADE fonctionne correctement avec son interface graphique et permet le développement et l'exécution d'agents JADE.

## 9. Références

- Site officiel JADE : https://jade.tilab.com/
- Documentation : https://jade.tilab.com/documentation/
- Téléchargements : https://jade.tilab.com/download/jade/
- Version utilisée : JADE 4.6.0 (révision 6869 du 30-11-2022)