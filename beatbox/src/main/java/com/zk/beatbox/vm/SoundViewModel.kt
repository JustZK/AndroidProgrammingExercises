package com.zk.beatbox.vm

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.lifecycle.MutableLiveData
import com.zk.beatbox.bean.BeatBox
import com.zk.beatbox.model.Sound
import java.util.*

/**
 * @author ZhuKun
 * @date 2021/12/21
 * @apiNote
 */
class SoundViewModel(private val beatBox: BeatBox): BaseObservable() {
    fun onButtonClicked() {
        sound?.let {
            beatBox.play(it)
        }
    }

    var sound: Sound? = null
        set(sound) {
            field = sound
            notifyChange()
        }

    @get:Bindable
    val title: String?
        get() = sound?.name
}