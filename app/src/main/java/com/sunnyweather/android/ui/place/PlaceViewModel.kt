package com.sunnyweather.android.ui.place

import androidx.lifecycle.*
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse


class PlaceViewModel: ViewModel(){

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavePlace() = Repository.getSavePlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()


    private val searchLiveData = MutableLiveData<String>()

    val placeList = ArrayList<Place>()

    val placeLiveData = Transformations.switchMap(searchLiveData) { query ->
        Repository.searchPlaces(query)
    }
    fun searchPlaces(query: String){
        searchLiveData.value = query
    }
}