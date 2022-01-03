package com.zk.photogallery.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zk.photogallery.bean.FlickrResponse
import com.zk.photogallery.bean.GalleryItem
import com.zk.photogallery.bean.PhotoResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


private const val TAG = "FlickrFetchr"

/**
 * @author ZhuKun
 * @date 2021/12/27
 * @see
 */
class FlickrFetchr {
    private val flickrApi: FlickrApi
    private lateinit var flickrHomePageRequest: Call<FlickrResponse>

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.flickr.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        flickrApi = retrofit.create(FlickrApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        flickrHomePageRequest = flickrApi.fetchPhotos()

        flickrHomePageRequest.enqueue(object : Callback<FlickrResponse> {
            override fun onResponse(
                call: Call<FlickrResponse>,
                response: Response<FlickrResponse>
            ) {
                Log.e(TAG, "Response received")
                val flickrResponse: FlickrResponse? = response.body()
                val photoResponse: PhotoResponse? = flickrResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems ?: mutableListOf()
                galleryItems = galleryItems.filterNot { it.url.isBlank() }
                responseLiveData.value = galleryItems
            }

            override fun onFailure(call: Call<FlickrResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

        })

        return responseLiveData
    }

    fun cancelRequestInFlight() {
        if (::flickrHomePageRequest.isInitialized) {
            flickrHomePageRequest.cancel()
        }
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = flickrApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use (BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap = $bitmap from Response = $response")
        return bitmap
    }
}