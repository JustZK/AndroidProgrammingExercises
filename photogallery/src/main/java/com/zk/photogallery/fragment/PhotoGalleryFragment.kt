package com.zk.photogallery.fragment

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zk.photogallery.R
import com.zk.photogallery.bean.GalleryItem
import com.zk.photogallery.thread.ThumbnailDownloader
import com.zk.photogallery.vm.PhotoGalleryViewModel


private const val TAG = "PhotoGalleryFragment"
/**
 * @author ZhuKun
 * @date 2021/12/27
 * @see
 */
class PhotoGalleryFragment : Fragment() {
    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var photoRecyclerView: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        photoGalleryViewModel = ViewModelProvider(this).get(PhotoGalleryViewModel::class.java)

        thumbnailDownloader = ThumbnailDownloader()
        lifecycle.addObserver(thumbnailDownloader)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        photoRecyclerView = view.findViewById(R.id.photo_recycler_view)
        photoRecyclerView.layoutManager = GridLayoutManager(context, 3)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(viewLifecycleOwner, Observer { galleryItems ->
            Log.d(TAG, "Have gallery items from ViewModel $galleryItems")
            //最后，更新数据时更新recycler view
            photoRecyclerView.adapter = PhotoAdapter(galleryItems)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader)
    }

    private class PhotoHolder(private val itemImageView: ImageView): RecyclerView.ViewHolder(itemImageView) {
        val bindDrawable: (Drawable) -> Unit = itemImageView::setImageDrawable
    }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>): RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
            val view = layoutInflater.inflate(R.layout.list_item_gallery, parent, false) as ImageView
            return PhotoHolder(view)
        }

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val placeholder: Drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher_background) ?: ColorDrawable()

            holder.bindDrawable(placeholder)
        }

        override fun getItemCount(): Int = galleryItems.size

    }

    companion object {
        fun newInstance() = PhotoGalleryFragment()
    }
}