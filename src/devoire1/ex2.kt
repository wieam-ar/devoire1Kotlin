package `devoir 1`

// Exceptions

class VehiculeIndisponibleException(message: String) : Exception(message)
class VehiculeNonTrouveException(message: String) : Exception(message)

// Partie 1 : Classes de base

abstract class Vehicule(
    val immatriculation: String,
    val marque: String,
    val modele: String,
    var kilometrage: Int,
    var disponible: Boolean = true
) {
    open fun afficherDetails() {
        println("Vehicule[$immatriculation] : $marque $modele | km: $kilometrage | disponible: $disponible")
    }

    fun estDisponible(): Boolean = disponible

    fun marquerIndisponible() {
        disponible = false
    }

    fun marquerDisponible() {
        disponible = true
    }

    fun mettreAJourKilometrage(km: Int) {
        if (km >= kilometrage) {
            kilometrage = km
        } else {
            println("⚠️ Le kilométrage fourni ($km) est inférieur au kilométrage actuel ($kilometrage). Ignoré.")
        }
    }
}
/*
Cette classe représente un véhicule général du parc automobile.
Elle contient les propriétés communes comme l’immatriculation, la marque, le modèle, le kilométrage et la disponibilité.
Elle définit aussi des méthodes pour afficher les détails, mettre à jour le kilométrage et changer l’état (disponible ou non).
C’est une classe abstraite servant de base aux autres types de véhicules.
 */

class Voiture(
    immatriculation: String,
    marque: String,
    modele: String,
    kilometrage: Int,
    val nombrePortes: Int,
    val typeCarburant: String,
    disponible: Boolean = true
) : Vehicule(immatriculation, marque, modele, kilometrage, disponible) {
    override fun afficherDetails() {
        println("Voiture[$immatriculation] : $marque $modele | ${nombrePortes} portes | $typeCarburant | km: $kilometrage | dispo: $disponible")
    }
}

/*
La classe Voiture hérite de Vehicule et ajoute des propriétés spécifiques comme le nombre de portes et le type de carburant.
Elle redéfinit la méthode afficherDetails() pour afficher les informations détaillées d’une voiture.
Elle représente un véhicule à quatre roues destiné aux conducteurs classiques.
 */

class Moto(
    immatriculation: String,
    marque: String,
    modele: String,
    kilometrage: Int,
    val cylindree: Int,
    disponible: Boolean = true
) : Vehicule(immatriculation, marque, modele, kilometrage, disponible) {
    override fun afficherDetails() {
        println("Moto[$immatriculation] : $marque $modele | ${cylindree}cc | km: $kilometrage | dispo: $disponible")
    }
}

/*
La classe Moto hérite aussi de Vehicule et ajoute la propriété cylindrée exprimée en cm³.
Elle redéfinit la méthode afficherDetails() pour montrer les caractéristiques d’une moto.
Elle sert à gérer les véhicules à deux roues du parc.
 */

// Partie 2 : Conducteur et Reservation

class Conducteur(val nom: String, val prenom: String, val numeroPermis: String) {
    fun afficherDetails() {
        println("Conducteur : $nom $prenom | Permis : $numeroPermis")
    }
}

class Reservation(
    val vehicule: Vehicule,
    val conducteur: Conducteur,
    val dateDebut: String,
    val dateFin: String,
    val kilometrageDebut: Int,
    var kilometrageFin: Int? = null
) {
    var cloturee: Boolean = false

    fun cloturerReservation(kilometrageRetour: Int) {
        if (cloturee) {
            println("⚠️ Cette réservation est déjà clôturée.")
            return
        }
        kilometrageFin = kilometrageRetour
        vehicule.mettreAJourKilometrage(kilometrageRetour)
        vehicule.marquerDisponible()
        cloturee = true
        println("✅ Réservation clôturée pour ${vehicule.immatriculation} | km retour : $kilometrageRetour")
    }

    fun afficherDetails() {
        println("----- Réservation -----")
        println("Vehicule : ${vehicule.immatriculation} (${vehicule.marque} ${vehicule.modele})")
        println("Conducteur : ${conducteur.nom} ${conducteur.prenom} (Permis: ${conducteur.numeroPermis})")
        println("Période : $dateDebut -> $dateFin")
        println("Kilométrage : début $kilometrageDebut | fin ${kilometrageFin ?: "Non renseigné"}")
        println("Clôturée : $cloturee")
    }
}

/*
La classe Reservation relie un conducteur à un véhicule sur une période donnée.
Elle garde les dates de début et de fin ainsi que le kilométrage avant et après utilisation.
Elle permet de clôturer une réservation en mettant à jour le véhicule et en le rendant disponible.
 */

// Partie 3 : Gestion du parc

class ParcAutomobile {
    val vehicules = mutableListOf<Vehicule>()
    val reservations = mutableListOf<Reservation>()

    fun ajouterVehicule(vehicule: Vehicule) {
        vehicules.add(vehicule)
        println("➕ Véhicule ajouté : ${vehicule.immatriculation}")
    }

    fun supprimerVehicule(immatriculation: String) {
        val found = vehicules.find { it.immatriculation.equals(immatriculation, ignoreCase = true) }
            ?: throw VehiculeNonTrouveException("Le véhicule avec immatriculation $immatriculation n'a pas été trouvé.")
        // Vérifier s'il est réservé actuellement (indisponible et existe une réservation non clôturée)
        val reservationActive = reservations.any { it.vehicule == found && !it.cloturee }
        if (reservationActive) {
            println("⚠️ Suppression impossible : véhicule $immatriculation a une réservation active.")
            return
        }
        vehicules.remove(found)
        println("➖ Véhicule supprimé : $immatriculation")
    }

    fun reserverVehicule(
        immatriculation: String,
        conducteur: Conducteur,
        dateDebut: String,
        dateFin: String
    ): Reservation {
        val vehicule = vehicules.find { it.immatriculation.equals(immatriculation, ignoreCase = true) }
            ?: throw VehiculeNonTrouveException("Le véhicule avec immatriculation $immatriculation n'a pas été trouvé.")

        if (!vehicule.estDisponible()) {
            throw VehiculeIndisponibleException("Le véhicule $immatriculation n'est pas disponible pour réservation.")
        }

        // Créer la réservation
        val reservation = Reservation(
            vehicule = vehicule,
            conducteur = conducteur,
            dateDebut = dateDebut,
            dateFin = dateFin,
            kilometrageDebut = vehicule.kilometrage
        )
        // Marquer véhicule indisponible et stocker la réservation
        vehicule.marquerIndisponible()
        reservations.add(reservation)
        println("📅 Réservation créée : ${immatriculation} pour ${conducteur.nom} du $dateDebut au $dateFin")
        return reservation
    }

    fun afficherVehiculesDisponibles() {
        println("\n--- Véhicules disponibles ---")
        val disponibles = vehicules.filter { it.estDisponible() }
        if (disponibles.isEmpty()) {
            println("Aucun véhicule disponible.")
        } else {
            disponibles.forEach { it.afficherDetails() }
        }
    }

    fun afficherReservations() {
        println("\n--- Liste des réservations ---")
        if (reservations.isEmpty()) {
            println("Aucune réservation.")
            return
        }
        reservations.forEach { it.afficherDetails() }
    }
}
/*
Cette classe gère tout le système du parc : les véhicules et les réservations.
Elle permet d’ajouter ou supprimer un véhicule, de réserver un véhicule, d’afficher les véhicules disponibles et de voir toutes les réservations.
Elle centralise la gestion globale du parc automobile.
 */
// ---------------------------
// Exemple d'utilisation (main)
// ---------------------------
fun main() {
    val parc = ParcAutomobile()

    // 1) Créer plusieurs véhicules (voitures & motos)
    val v1 = Voiture("AA-123-BB", "Renault", "Clio", 45000, nombrePortes = 5, typeCarburant = "essence")
    val v2 = Voiture("CC-456-DD", "Tesla", "Model 3", 12000, nombrePortes = 4, typeCarburant = "électrique")
    val m1 = Moto("EE-789-FF", "Yamaha", "MT-07", 8000, cylindree = 689)
    val m2 = Moto("GG-321-HH", "Honda", "CBR600", 15000, cylindree = 600)

    // Ajouter au parc
    parc.ajouterVehicule(v1)
    parc.ajouterVehicule(v2)
    parc.ajouterVehicule(m1)
    parc.ajouterVehicule(m2)

    // 2) Ajouter conducteurs
    val c1 = Conducteur("Ali", "Ben", "P12345678")
    val c2 = Conducteur("Sara", "Lazaar", "P87654321")
    val c3 = Conducteur("Omar", "Khalid", "P11122233")

    // 3) Faire des réservations et gérer exceptions
    try {
        val r1 = parc.reserverVehicule("AA-123-BB", c1, "05/10/2025", "10/10/2025")
    } catch (e: Exception) {
        println("Erreur réservation : ${e.message}")
    }

    try {
        // Essayer de réserver le même véhicule (devrait lancer VehiculeIndisponibleException)
        val r2 = parc.reserverVehicule("AA-123-BB", c2, "06/10/2025", "07/10/2025")
    } catch (e: VehiculeIndisponibleException) {
        println("Exception: ${e.message}")
    } catch (e: Exception) {
        println("Autre erreur: ${e.message}")
    }

    try {
        // Réserver un véhicule non existant (devrait lancer VehiculeNonTrouveException)
        val r3 = parc.reserverVehicule("ZZ-000-ZZ", c3, "01/11/2025", "05/11/2025")
    } catch (e: VehiculeNonTrouveException) {
        println("Exception: ${e.message}")
    }

    // Afficher véhicules disponibles après réservations
    parc.afficherVehiculesDisponibles()

    // 4) Afficher toutes les réservations
    parc.afficherReservations()

    // 5) Clôturer une réservation (mettre km de retour et rendre le véhicule disponible)
    // Récupérer la première réservation non clôturée
    val premiereRes = parc.reservations.firstOrNull { !it.cloturee }
    premiereRes?.let {
        println("\n-- Clôture de la première réservation --")
        it.cloturerReservation(kilometrageRetour = it.kilometrageDebut + 300)
    }

    // Vérifier affichage après clôture
    parc.afficherVehiculesDisponibles()
    parc.afficherReservations()

    // 6) Tester suppression d'un véhicule
    try {
        parc.supprimerVehicule("CC-456-DD") // devrait supprimer Tesla si pas réservé
    } catch (e: VehiculeNonTrouveException) {
        println("Erreur suppression: ${e.message}")
    }

    // Affichage final
    println("\n--- État final du parc ---")
    parc.vehicules.forEach { it.afficherDetails() }
}

/*
Le programme principal crée plusieurs voitures, motos et conducteurs.
Il ajoute ces éléments au parc, effectue des réservations, gère les exceptions et affiche les états du parc avant et après les opérations.
C’est une démonstration pratique du bon fonctionnement du système orienté objet.
 */