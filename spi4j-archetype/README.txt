Ce module est un archetype Maven permettant, après son installation dans le repository Maven local,
de générer la structure et la configuration Maven et Eclipse d'un projet type
et basé sur Spi4J et Pacman.

Pour générer un nouveau projet, il suffit alors d'exécuter la commande Maven suivante, en précisant la version de l'archetype:
mvn archetype:generate -DarchetypeGroupId=fr.spi4j -DarchetypeArtifactId=spi4j-archetype -DarchetypeVersion=0.23
puis de répondre à quelques questions (par exemple : groupId=fr.test, artifactId=test, version=1.0-SNAPSHOT, package=fr.test, spi4jVersion=0.23)

Le but de cet archetype est d'aider à démarrer la structure d'un projet basé sur Spi4J et Pacman,
notamment pour les projets n'ayant pas d'autre exemple que l'application blanche de Spi4J.
La configuration générée pourra ensuite être adaptée selon les besoins à la version de Maven ou d'Eclipse
ou par exemple pour la création d'un autre module pour une application web (jsf, jsp, gwt, client RDA, etc)
comme les exemples dans le répertoire appwhite1.
