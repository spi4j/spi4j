* Spi4J-Doc-Report *

Ce projet Spi4J-Doc-Report permet de générer en Java des éditions, sous forme de documents au format ODT ou DOCX (ou éventuellement PDF ou HTML),
à partir de modèles ODT ou DOCX.

C'est une alternative plus simple et plus légère au projet Spi4J-Report (et à BIRT ou JasperReport).
Spi4J-Doc-Report se base sur la librairie XDocReport (licence open-source MIT).
La librairie XDocReport est hébergée et son utilisation est documentée en anglais à cette adresse :
	http://code.google.com/p/xdocreport/


Spi4J-Doc-Report est bien adapté pour le besoin de générer dynamiquement des documents pour des courriers
(moins pour générer des documents PDF de tableaux sur de nombreuses pages avec des graphiques dynamiques).

Les modèles des documents sont créés et maintenus avec LibreOffice, OpenOffice ou MS Word (formats ODT ou DOCX), qui sont connus par "tout le monde".
Ainsi, toutes les personnes à orientation fonctionnelle peuvent facilement participer à la création et à la mise en page de ces modèles.
(Contrairement à BIRT qui nécessite l'IDE Eclipse et qui n'est maîtrisable que par des développeurs.)

Il peut être remarqué que Spi4J-Doc-Report (génération des documents de même que conversion de documents en PDF ou HTML) utilise des librairies en pur Java,
et ne nécessite donc pas l'installation d'un logiciel particulier sur le poste utilisé lors de l'exécution
(aucune dépendance à OpenOffice/LibreOffice ou à MS Word lors de l'exécution de Spi4J-Doc-Report)
