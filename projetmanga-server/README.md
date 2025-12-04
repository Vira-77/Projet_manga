# Projet_manga

pour lancer le projet la premiere fois, il faut d'abord cloner le dépôt git :

```bash
git clone https://github.com/Vira-77/Projet_manga
cd Projet_manga
```

ensuite pour faciliter l'utilisation de l'IA on a besoin d'un container docker, pour ceci neccesite de telecharger la version desktop de docker : https://www.docker.com/products/docker-desktop/
</br>
pour lancer la creation du container docker, il faut executer la commande suivante dans le terminal a la racine du projet :
```bash
docker-compose up --build
```

cela va creer un container docker avec tout les packages necessaire pour faire fonctionner l'IA.
ensuite pour les prochain lancements du container il suffira d'executer la commande suivante dans le terminal a la racine du projet :

```bash
docker-compose up
```