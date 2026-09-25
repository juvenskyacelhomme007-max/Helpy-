package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

data class UserProfile(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String = "",
    val phoneNumber: String = "",
    val country: String = "Mondial",
    val currency: String = "USD",
    val role: String = "Utilisateur",
    val verified: Boolean = false,
    val createdAt: Timestamp? = null
)

data class Product(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val category: String = "Électronique",
    val condition: String = "Neuf", // "Neuf", "Très bon état", "Occasion"
    val isNegotiable: Boolean = true,
    val hasDelivery: Boolean = true,
    val imageUrl: String = "",
    val country: String = "Mondial",
    val city: String = "",
    val sellerId: String = "",
    val sellerName: String = "",
    val sellerPhone: String = "",
    val sellerWhatsApp: String = "",
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)

data class ServiceItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val rate: Double = 0.0,
    val currency: String = "USD",
    val pricingType: String = "par heure", // "par heure", "forfait fixe", "sur devis"
    val category: String = "Réparation",
    val experienceYears: Int = 3,
    val imageUrl: String = "",
    val providerId: String = "",
    val providerName: String = "",
    val providerPhone: String = "",
    val providerWhatsApp: String = "",
    val country: String = "Mondial",
    val city: String = "",
    val rating: Double = 4.9,
    val reviewCount: Int = 18,
    val createdAt: Timestamp? = null
)

data class Business(
    val id: String = "",
    val name: String = "",
    val category: String = "Boutiques & Commerces",
    val description: String = "",
    val address: String = "",
    val city: String = "",
    val country: String = "Mondial",
    val imageUrl: String = "",
    val phone: String = "",
    val whatsApp: String = "",
    val email: String = "",
    val website: String = "",
    val openingHours: String = "Lun - Sam : 08h00 - 18h00",
    val ownerId: String = "",
    val rating: Double = 4.8,
    val verified: Boolean = true,
    val createdAt: Timestamp? = null
)

@Entity(tableName = "favorites")
data class FavoriteItem(
    @PrimaryKey val id: String,
    val title: String,
    val type: String, // "product", "service", "business"
    val category: String,
    val priceOrRate: String,
    val location: String,
    val imageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

object AppConstants {
    val SUPPORTED_CURRENCIES = listOf("USD", "HTG", "EUR", "CAD", "DOP")

    val SUPPORTED_COUNTRIES = listOf(
        "Toutes les zones",
        "Haïti",
        "États-Unis",
        "Canada",
        "France",
        "République Dominicaine",
        "Chili & Brésil",
        "Autres pays"
    )

    val PRODUCT_CATEGORIES = listOf(
        "Tous",
        "Téléphones & Tablettes",
        "Ordinateurs & Pièces",
        "Mode & Vêtements",
        "Maison, Meubles & Électroménager",
        "Véhicules & Motos",
        "Alimentation & Épicerie",
        "Beauté & Soins",
        "Outils & Matériaux"
    )

    val SERVICE_CATEGORIES = listOf(
        "Tous",
        "Plomberie & Tuyauterie",
        "Électricité & Énergie Solaire",
        "Technologies, Sites Web & Graphisme",
        "Mécanique & Réparation Auto",
        "Transport, Livraison & Fret",
        "Menuiserie & Construction",
        "Couture & Coiffure",
        "Traduction & Documents légaux"
    )

    val BUSINESS_CATEGORIES = listOf(
        "Tous",
        "Restaurants & Gastronomie",
        "Magasins & Supermarchés",
        "Agences de Transfert & Finance",
        "Cliniques & Pharmacies",
        "Hébergement & Hôtels",
        "Garages & Ateliers",
        "Écoles & Centres de formation"
    )
}
