package com.zk.photogallery.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.zk.photogallery.R
import com.zk.photogallery.fragment.PhotoGalleryFragment

class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty){
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}