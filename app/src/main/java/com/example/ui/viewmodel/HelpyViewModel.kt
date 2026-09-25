package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Business
import com.example.data.model.FavoriteItem
import com.example.data.model.Product
import com.example.data.model.ServiceItem
import com.example.data.model.UserProfile
import com.example.data.repository.AuthRepository
import com.example.data.repository.HelpyRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HelpyViewModel(application: Application) : AndroidViewModel(application) {
    val authRepository = AuthRepository(application)
    val helpyRepository = HelpyRepository(application)

    val currentUser: StateFlow<FirebaseUser?> = authRepository.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.Eagerly, authRepository.currentUser)

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile.asStateFlow()

    val selectedCountry = MutableStateFlow("Toutes les zones")
    val selectedCategory = MutableStateFlow("Tous")

    val products: StateFlow<List<Product>> = combine(
        selectedCategory,
        selectedCountry
    ) { category, country ->
        category to country
    }.flatMapLatest { (category, country) ->
        helpyRepository.observeProducts(category, country)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val services: StateFlow<List<ServiceItem>> = combine(
        selectedCategory,
        selectedCountry
    ) { category, country ->
        category to country
    }.flatMapLatest { (category, country) ->
        helpyRepository.observeServices(category, country)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val businesses: StateFlow<List<Business>> = combine(
        selectedCategory,
        selectedCountry
    ) { category, country ->
        category to country
    }.flatMapLatest { (category, country) ->
        helpyRepository.observeBusinesses(category, country)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favorites: StateFlow<List<FavoriteItem>> = helpyRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            authRepository.attemptAutoSignIn()
        }
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    _userProfile.value = authRepository.getUserProfile(user.uid)
                } else {
                    _userProfile.value = null
                }
            }
        }
    }

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val result = authRepository.signInWithGoogle(activity)
            result.onSuccess {
                _isAuthLoading.value = false
                _userProfile.value = authRepository.getUserProfile(it.uid)
            }.onFailure { error ->
                _isAuthLoading.value = false
                _authError.value = error.localizedMessage ?: "Échec de la connexion"
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _userProfile.value = null
        }
    }

    fun toggleFavorite(item: FavoriteItem, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            helpyRepository.toggleFavorite(item, isCurrentlyFavorite)
        }
    }

    fun publishProduct(product: Product, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = helpyRepository.addProduct(product)
            result.onSuccess { onSuccess() }
                .onFailure { onError(it.localizedMessage ?: "Erreur lors de la publication") }
        }
    }

    fun publishService(service: ServiceItem, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = helpyRepository.addService(service)
            result.onSuccess { onSuccess() }
                .onFailure { onError(it.localizedMessage ?: "Erreur lors de la publication") }
        }
    }

    fun publishBusiness(business: Business, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = helpyRepository.addBusiness(business)
            result.onSuccess { onSuccess() }
                .onFailure { onError(it.localizedMessage ?: "Erreur lors de la publication") }
        }
    }
}
