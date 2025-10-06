// Classe de base Personne

open class Personne(var nom: String, var prenom: String, var email: String) {
    open fun afficherInfos() {
        println("👤 Nom : $nom $prenom | Email : $email")
    }
}

/*
Cette classe représente une personne dans le système.
Elle contient les propriétés nom, prenom et email, et une méthode afficherInfos() qui montre les informations de la personne à l’écran.
C’est la classe de base dont d’autres classes vont hériter.
*/


// Classe Livre

class Livre(
    var titre: String,
    var auteur: String,
    var isbn: String,
    var nombreExemplaires: Int
) {
    fun afficherDetails() {
        println("📚 Livre : $titre | Auteur : $auteur | ISBN : $isbn | Stock : $nombreExemplaires")
    }

    fun disponiblePourEmprunt(): Boolean {
        return nombreExemplaires > 0
    }

    fun mettreAJourStock(nouveauStock: Int) {
        nombreExemplaires = nouveauStock
    }
}

/*
Cette classe représente un livre avec son titre, auteur, isbn, et nombre d’exemplaires disponibles.
Elle contient des méthodes pour afficher les détails, vérifier la disponibilité et mettre à jour le stock lors des emprunts ou des retours.

*/

// Classe Emprunt

class Emprunt(
    val utilisateur: Utilisateur,
    val livre: Livre,
    val dateEmprunt: String,
    var dateRetour: String? = null
) {
    fun afficherDetails() {
        println("📖 Emprunt - Utilisateur : ${utilisateur.nom} ${utilisateur.prenom}, " +
                "Livre : ${livre.titre}, " +
                "Date d'emprunt : $dateEmprunt, " +
                "Date de retour : ${dateRetour ?: "Non encore retourné"}")
    }

    fun retournerLivre(dateRetour: String) {
        this.dateRetour = dateRetour
        livre.mettreAJourStock(livre.nombreExemplaires + 1)
        println("✅ Livre '${livre.titre}' retourné le $dateRetour par ${utilisateur.nom}.")
    }
}

/*
Cette classe relie un utilisateur à un livre et contient les dates d’emprunt et de retour.
Elle possède la méthode afficherDetails() pour montrer les informations sur l’emprunt, et retournerLivre() pour enregistrer le retour du livre et remettre à jour le stock.

 */

// Classe Utilisateur (hérite de Personne)

class Utilisateur(
    var idUtilisateur: Int,
    nom: String,
    prenom: String,
    email: String
) : Personne(nom, prenom, email) {

    val emprunts = mutableListOf<Emprunt>()

    fun emprunterLivre(livre: Livre, dateEmprunt: String) {
        if (livre.disponiblePourEmprunt()) {
            livre.mettreAJourStock(livre.nombreExemplaires - 1)
            val emprunt = Emprunt(this, livre, dateEmprunt)
            emprunts.add(emprunt)
            println("📦 ${nom} a emprunté le livre '${livre.titre}' le $dateEmprunt.")
        } else {
            println("❌ Le livre '${livre.titre}' n'est pas disponible pour l'emprunt.")
        }
    }

    fun afficherEmprunts() {
        println("🧾 Emprunts de $nom $prenom :")
        if (emprunts.isEmpty()) {
            println("Aucun emprunt trouvé.")
        } else {
            emprunts.forEach { it.afficherDetails() }
        }
    }
}

/*
L’utilisateur hérite de Personne et ajoute deux nouvelles propriétés :
idUtilisateur (identifiant unique) et emprunts (liste des livres empruntés).
Il possède la méthode emprunterLivre() pour emprunter un livre, et afficherEmprunts() pour afficher tous les livres qu’il a empruntés.
*/

// Classe abstraite GestionBibliotheque

abstract class GestionBibliotheque {
    val utilisateurs = mutableListOf<Utilisateur>()
    val livres = mutableListOf<Livre>()

    abstract fun ajouterUtilisateur(utilisateur: Utilisateur)
    abstract fun ajouterLivre(livre: Livre)
    abstract fun afficherTousLesLivres()
}

/*
C’est une classe de base pour gérer les livres et les utilisateurs de la bibliothèque.
Elle contient les listes utilisateurs et livres, et déclare les méthodes abstraites ajouterUtilisateur(), ajouterLivre(), et afficherTousLesLivres().
Elle ne peut pas être instanciée directement.*/



// Classe Bibliotheque qui hérite de GestionBibliotheque

class Bibliotheque : GestionBibliotheque() {

    override fun ajouterUtilisateur(utilisateur: Utilisateur) {
        utilisateurs.add(utilisateur)
        println("👥 Utilisateur ajouté : ${utilisateur.nom} ${utilisateur.prenom}")
    }

    override fun ajouterLivre(livre: Livre) {
        livres.add(livre)
        println("📘 Livre ajouté : ${livre.titre}")
    }

    override fun afficherTousLesLivres() {
        println("\n📚 Liste des livres dans la bibliothèque :")
        if (livres.isEmpty()) {
            println("Aucun livre disponible.")
        } else {
            livres.forEach { it.afficherDetails() }
        }
    }

    fun rechercherLivreParTitre(titre: String): Livre? {
        return livres.find { it.titre.equals(titre, ignoreCase = true) }
    }
}

/*
Cette classe hérite de GestionBibliotheque et implémente toutes ses méthodes.
Elle ajoute une fonction rechercherLivreParTitre() pour retrouver un livre à partir de son titre.
C’est la classe principale qui gère toute la bibliothèque.
*/

// ---------------------------
// PROGRAMME PRINCIPAL
// ---------------------------


fun main() {
    val bibliotheque = Bibliotheque()

    // Création des livres
    val livre1 = Livre("Kotlin pour Débutants", "Jean Dupont", "ISBN123", 3)
    val livre2 = Livre("Programmation Android", "Marie Curie", "ISBN456", 2)
    val livre3 = Livre("Intelligence Artificielle", "Albert Turing", "ISBN789", 1)

    //Ajout des livres à la bibliothèque
    bibliotheque.ajouterLivre(livre1)
    bibliotheque.ajouterLivre(livre2)
    bibliotheque.ajouterLivre(livre3)

    // Création des utilisateurs
    val user1 = Utilisateur(1, "Wieam", "Aarika", "wieam@example.com")
    val user2 = Utilisateur(2, "Youssef", "Benali", "youssef@example.com")

    // Ajout des utilisateurs à la bibliothèque
    bibliotheque.ajouterUtilisateur(user1)
    bibliotheque.ajouterUtilisateur(user2)

    // Emprunt de livres
    user1.emprunterLivre(livre1, "04/10/2025")
    user2.emprunterLivre(livre2, "04/10/2025")
    user1.emprunterLivre(livre3, "04/10/2025")

    // Affichage
    println("\n----- INFOS UTILISATEURS -----")
    user1.afficherInfos()
    user2.afficherInfos()

    println("\n----- LISTE DES LIVRES -----")
    bibliotheque.afficherTousLesLivres()

    println("\n----- EMPRUNTS -----")
    user1.afficherEmprunts()
    user2.afficherEmprunts()

    // 7️⃣ Retour d'un livre
    println("\n----- RETOUR DE LIVRE -----")
    val premierEmprunt = user1.emprunts.first()
    premierEmprunt.retournerLivre("10/10/2025")

    // Vérification du stock après retour
    println("\n----- LIVRES APRÈS RETOUR -----")
    bibliotheque.afficherTousLesLivres()
}
/*
Le programme crée plusieurs livres et utilisateurs, les ajoute à la bibliothèque, puis simule des emprunts et retours de livres.
Enfin, il affiche les informations de tous les objets (livres, utilisateurs, emprunts) pour vérifier le bon fonctionnement du système.
 */