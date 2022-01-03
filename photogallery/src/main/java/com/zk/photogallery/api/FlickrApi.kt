package com.zk.photogallery.api

import com.zk.photogallery.bean.FlickrResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * @author ZhuKun
 * @date 2021/12/27
 * @see
 */
interface FlickrApi {

//    @GET("/")
//    fun fetchContents(): Call<String>

    @GET("services/rest/?method=flickr.interestingness.getList" +
            "&api_key=2f848e4f5efdd422002dc60f2a082db9" +
            "&format=json" +
            "&nojsoncallback=1" +
            "&extras=url_s"
    )
    fun fetchPhotos(): Call<FlickrResponse>

    @GET
    fun fetchUrlBytes(@Url url: String): Call<ResponseBody>
}