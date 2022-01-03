package com.zk.photogallery.bean

import com.google.gson.annotations.SerializedName

/**
 * @author ZhuKun
 * @date 2021/12/28
 * @see
 */
data class GalleryItem(
    var title: String = "",
    var id: String = "",
    @SerializedName("url_s") var url: String = ""
)
