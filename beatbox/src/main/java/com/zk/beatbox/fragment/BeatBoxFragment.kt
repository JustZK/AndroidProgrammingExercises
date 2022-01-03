package com.zk.beatbox.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zk.beatbox.R
import com.zk.beatbox.bean.BeatBox
import com.zk.beatbox.model.Sound
import com.zk.beatbox.databinding.FragmentBeatBoxBinding
import com.zk.beatbox.databinding.ListItemSoundBinding
import com.zk.beatbox.vm.SoundViewModel

/**
 * @author ZhuKun
 * @date 2021/12/20
 * @apiNote
 */
class BeatBoxFragment : Fragment() {
    private lateinit var beatBox: BeatBox

    companion object {
        fun newInstance(): BeatBoxFragment {
            return BeatBoxFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        beatBox = BeatBox(resources.assets)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentBeatBoxBinding>(
            inflater,
            R.layout.fragment_beat_box,
            container,
            false
        )

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(activity, 3)
            adapter = SoundAdapter(beatBox.sounds)
        }

        return binding.root
    }

    private inner class SoundHolder(private val binding: ListItemSoundBinding): RecyclerView.ViewHolder(binding.root) {
        init {
            binding.viewModel = SoundViewModel(beatBox)
        }
        fun bind(sound: Sound){
            binding.apply {
                viewModel?.sound = sound
                executePendingBindings()
            }
        }
    }

    private inner class SoundAdapter(private val sounds: List<Sound>): RecyclerView.Adapter<SoundHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
            val binding = DataBindingUtil.inflate<ListItemSoundBinding>(
                layoutInflater,
                R.layout.list_item_sound,
                parent,
                false
            )

            return SoundHolder(binding)
        }

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {
            val sound = sounds[position]
            holder.bind(sound)
        }

        override fun getItemCount(): Int {
            return sounds.size
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        beatBox.release()
    }
}