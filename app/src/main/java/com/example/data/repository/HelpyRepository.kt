package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.local.HelpyDao
import com.example.data.local.HelpyDatabase
import com.example.data.model.Business
import com.example.data.model.FavoriteItem
import com.example.data.model.Product
import com.example.data.model.ServiceItem
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class HelpyRepository(private val context: Context) {
    private val databaseId: String = try {
        context.getString(R.string.firestore_database_id)
    } catch (e: Exception) {
        ""
    }

    private val firestore: FirebaseFirestore = if (databaseId.isNotEmpty()) {
        FirebaseFirestore.getInstance(databaseId)
    } else {
        @Suppress("DEPRECATION")
        FirebaseFirestore.getInstance()
    }

    private val auth = Firebase.auth
    private val helpyDao: HelpyDao = HelpyDatabase.getDatabase(context).helpyDao()

    fun observeProducts(category: String? = null, country: String? = null): Flow<List<Product>> {
        var query: Query = firestore.collection("products")
        if (category != null && category != "Tous" && category != "Tout" && category != "Toutes") {
            query = query.whereEqualTo("category", category)
        }
        if (country != null && country != "Toutes les zones" && country != "Tout Zòn yo" && country != "Mondial" && country != "Port-au-Prince") {
            query = query.whereEqualTo("country", country)
        }

        return query.snapshots().map { snapshot ->
            val list = snapshot.toObjects(Product::class.java)
            if (list.isEmpty()) getSampleProducts() else list
        }.catch { e ->
            Log.w("HelpyRepository", "Firestore lecture produits: ${e.message}")
            emit(getSampleProducts())
        }
    }

    fun observeServices(category: String? = null, country: String? = null): Flow<List<ServiceItem>> {
        var query: Query = firestore.collection("services")
        if (category != null && category != "Tous" && category != "Tout" && category != "Toutes") {
            query = query.whereEqualTo("category", category)
        }

        return query.snapshots().map { snapshot ->
            val list = snapshot.toObjects(ServiceItem::class.java)
            if (list.isEmpty()) getSampleServices() else list
        }.catch { e ->
            Log.w("HelpyRepository", "Firestore lecture services: ${e.message}")
            emit(getSampleServices())
        }
    }

    fun observeBusinesses(category: String? = null, country: String? = null): Flow<List<Business>> {
        var query: Query = firestore.collection("businesses")
        if (category != null && category != "Tous" && category != "Tout" && category != "Toutes") {
            query = query.whereEqualTo("category", category)
        }

        return query.snapshots().map { snapshot ->
            val list = snapshot.toObjects(Business::class.java)
            if (list.isEmpty()) getSampleBusinesses() else list
        }.catch { e ->
            Log.w("HelpyRepository", "Firestore lecture commerces: ${e.message}")
            emit(getSampleBusinesses())
        }
    }

    suspend fun addProduct(product: Product): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Vous devez être connecté pour publier une annonce"))
        return try {
            val docRef = firestore.collection("products").document()
            val payload = product.copy(
                id = docRef.id,
                sellerId = uid,
                sellerName = auth.currentUser?.displayName ?: "Vendeur Helpy",
                createdAt = Timestamp.now()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addService(service: ServiceItem): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Vous devez être connecté pour proposer un service"))
        return try {
            val docRef = firestore.collection("services").document()
            val payload = service.copy(
                id = docRef.id,
                providerId = uid,
                providerName = auth.currentUser?.displayName ?: "Professionnel Helpy",
                createdAt = Timestamp.now()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addBusiness(business: Business): Result<Unit> {
        val uid = auth.currentUser?.uid ?: return Result.failure(IllegalStateException("Vous devez être connecté pour enregistrer une entreprise"))
        return try {
            val docRef = firestore.collection("businesses").document()
            val payload = business.copy(
                id = docRef.id,
                ownerId = uid,
                createdAt = Timestamp.now()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getFavorites(): Flow<List<FavoriteItem>> = helpyDao.getAllFavorites()
    fun isFavorite(id: String): Flow<Boolean> = helpyDao.isFavorite(id)
    suspend fun toggleFavorite(item: FavoriteItem, isFav: Boolean) {
        if (isFav) {
            helpyDao.deleteFavoriteById(item.id)
        } else {
            helpyDao.insertFavorite(item)
        }
    }

    // Realistic community sample listings matching the UI mockup
    private fun getSampleProducts(): List<Product> = listOf(
        Product(
            id = "p1",
            title = "iPhone 15 Pro Max",
            description = "iPhone 15 Pro Max 256GB, état neuf, encore sous garantie. Tout fonctionne parfaitement. Téléphone original Apple dans sa boîte.",
            price = 75000.0,
            currency = "G",
            category = "Électronique",
            condition = "Neuf",
            isNegotiable = true,
            hasDelivery = true,
            imageUrl = "android.resource://${context.packageName}/drawable/img_product_phone",
            country = "Haïti",
            city = "Delmas, Port-au-Prince",
            sellerName = "Marie Style",
            sellerPhone = "+509 3700 1122",
            sellerWhatsApp = "+509 3700 1122"
        ),
        Product(
            id = "p2",
            title = "iPhone 13",
            description = "iPhone 13 128GB Bleu, batterie 88%, débloqué mondial, en très bon état sans aucune fissure.",
            price = 55000.0,
            currency = "G",
            category = "Électronique",
            condition = "Occasion",
            isNegotiable = true,
            hasDelivery = true,
            imageUrl = "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?auto=format&fit=crop&w=800&q=80",
            country = "Haïti",
            city = "Pétion-Ville",
            sellerName = "Alex Tech",
            sellerPhone = "+509 3611 2233",
            sellerWhatsApp = "+509 3611 2233"
        ),
        Product(
            id = "p3",
            title = "iPhone 11",
            description = "iPhone 11 64GB Rouge, coque et chargeur inclus, fonctionne parfaitement avec toutes les puces.",
            price = 45000.0,
            currency = "G",
            category = "Électronique",
            condition = "Occasion",
            isNegotiable = false,
            hasDelivery = false,
            imageUrl = "https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?auto=format&fit=crop&w=800&q=80",
            country = "Haïti",
            city = "Tabarre",
            sellerName = "Jean Digital",
            sellerPhone = "+509 3899 4400",
            sellerWhatsApp = "+509 3899 4400"
        ),
        Product(
            id = "p4",
            title = "iPhone XR",
            description = "iPhone XR 128GB en bon état cosmétique, Face ID fonctionnel, batterie neuve à 100%.",
            price = 35000.0,
            currency = "G",
            category = "Électronique",
            condition = "Occasion",
            isNegotiable = true,
            hasDelivery = true,
            imageUrl = "https://images.unsplash.com/photo-1556656793-08538906a9f8?auto=format&fit=crop&w=800&q=80",
            country = "Haïti",
            city = "Carrefour",
            sellerName = "Mobilis Lakay",
            sellerPhone = "+509 3422 1100",
            sellerWhatsApp = "+509 3422 1100"
        ),
        Product(
            id = "p5",
            title = "Moto 150cc",
            description = "Moto Haojin 150cc neuve, 0 km, moteur 4 temps très résistant et économique, papiers en règle.",
            price = 85000.0,
            currency = "G",
            category = "Véhicules",
            condition = "Neuf",
            isNegotiable = true,
            hasDelivery = false,
            imageUrl = "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?auto=format&fit=crop&w=800&q=80",
            country = "Haïti",
            city = "Pétion-Ville",
            sellerName = "Moto Star",
            sellerPhone = "+509 3100 9988",
            sellerWhatsApp = "+509 3100 9988"
        ),
        Product(
            id = "p6",
            title = "Appartement 3 Chambres",
            description = "Bel appartement moderne avec cour clôturée, eau courante, système électrique et sécurité 24h.",
            price = 35000.0,
            currency = "G",
            category = "Immobilier",
            condition = "Location",
            isNegotiable = false,
            hasDelivery = false,
            imageUrl = "https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80",
            country = "Haïti",
            city = "Tabarre",
            sellerName = "ImmoCaraïbes",
            sellerPhone = "+509 3788 4455",
            sellerWhatsApp = "+509 3788 4455"
        )
    )

    private fun getSampleServices(): List<ServiceItem> = listOf(
        ServiceItem(
            id = "s1",
            title = "Installation Système Solaire & Batteries Lithium",
            description = "Technicien certifié avec 8 ans d'expérience. Dimensionnement de charge, installation de panneaux, onduleurs et sécurisation.",
            rate = 5000.0,
            currency = "G",
            pricingType = "forfait",
            category = "Services & Dépannage",
            experienceYears = 8,
            imageUrl = "android.resource://${context.packageName}/drawable/img_service_solar",
            providerName = "Jean Robert Joseph",
            providerPhone = "+509 3788 1900",
            providerWhatsApp = "+509 3788 1900",
            country = "Haïti",
            city = "Port-au-Prince & Pétion-Ville",
            rating = 4.9,
            reviewCount = 34
        ),
        ServiceItem(
            id = "s2",
            title = "Transport de Colis & Fret Express Haïti - USA",
            description = "Envoi rapide de colis, cartons, pièces détachées et documents sécurisés entre la Floride et Haïti. Livraison à domicile.",
            rate = 4500.0,
            currency = "G",
            pricingType = "sur devis",
            category = "Transport & Livraison",
            experienceYears = 6,
            imageUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=800&q=80",
            providerName = "Kargo Express Lakay",
            providerPhone = "+1 954 620 4410",
            providerWhatsApp = "+1 954 620 4410",
            country = "États-Unis",
            city = "Fort Lauderdale, FL",
            rating = 5.0,
            reviewCount = 52
        )
    )

    private fun getSampleBusinesses(): List<Business> = listOf(
        Business(
            id = "b1",
            name = "Restaurant Le Coin Créole",
            category = "Gastronomie & Restos",
            description = "Saveurs caribéennes authentiques : griot croustillant, riz djon-djon, poisson gros sel, lalo et soupe joumou tous les dimanches.",
            address = "1250 NE 163rd St",
            city = "North Miami Beach, FL",
            country = "États-Unis",
            imageUrl = "android.resource://${context.packageName}/drawable/img_biz_resto",
            phone = "+1 305 940 3320",
            whatsApp = "+1 305 940 3320",
            openingHours = "Tous les jours : 09h00 - 22h00",
            rating = 4.9,
            verified = true
        ),
        Business(
            id = "b2",
            name = "Quincaillerie & Matériaux du Nord",
            category = "Magasins & Commerces",
            description = "Fournisseur direct de ciment, fer, peintures, toitures et outillage professionnel de construction pour tout le grand Nord.",
            address = "Boulevard du Cap, Rilwa",
            city = "Cap-Haïtien",
            country = "Haïti",
            imageUrl = "https://images.unsplash.com/photo-1581783342308-f792dbdd27c5?auto=format&fit=crop&w=800&q=80",
            phone = "+509 2262 1080",
            whatsApp = "+509 3701 4422",
            openingHours = "Lun - Sam : 07h30 - 17h00",
            rating = 4.7,
            verified = true
        )
    )
}
