# Todo App 

 L'application Todo App est une solution simple et efficace pour organiser vos tâches quotidiennes.

## Fonctionnalités principales :

- Liste déroulante de tâches : L'application affiche une liste de tâches (via une RecyclerView) récupérées depuis une API distante.
- Interaction avec une API distante : Nous avons utilisé Retrofit pour consommer l'API et kotlinx.coroutines pour effectuer des appels réseau de manière asynchrone.
- Navigation entre plusieurs écrans : L'application permet de naviguer entre différents écrans via des Intent pour afficher et gérer les tâches (comme l'édition ou la suppression d'une tâche).
- Échange d'informations entre écrans : L'application utilise Activity Result API pour récupérer les résultats des activités d'édition ou d'ajout de tâches.
- Architecture propre : L'application suit un modèle d'architecture de type MVVM (Model-View-ViewModel) avec l'utilisation de ViewModel et Repository pour gérer les données.
- Permissions : L'application demande des permissions nécessaires pour accéder à certaines fonctionnalités (par exemple, l'accès à l'internet pour récupérer les tâches depuis l'API).
