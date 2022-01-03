package com.zk.photogallery.bean

import com.google.gson.annotations.SerializedName

/**
 * @author ZhuKun
 * @date 2021/12/28
 * @see
 */
class PhotoResponse {
    @SerializedName("photo")
    lateinit var galleryItems: List<GalleryItem>
}