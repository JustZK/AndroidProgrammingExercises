package com.zk.photogallery.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.zk.photogallery.api.FlickrFetchr
import com.zk.photogallery.bean.GalleryItem

/**
 * @author ZhuKun
 * @date 2021/12/28
 * @see
 */
class PhotoGalleryViewModel: ViewModel() {
    private val flickrFetchr: FlickrFetchr
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        flickrFetchr = FlickrFetchr()
        galleryItemLiveData = flickrFetchr.fetchPhotos()
    }

    override fun onCleared() {
        super.onCleared()
        flickrFetchr.cancelRequestInFlight()
    }
}