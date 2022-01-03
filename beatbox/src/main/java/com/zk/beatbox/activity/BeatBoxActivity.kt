package com.zk.beatbox.activity

import androidx.fragment.app.Fragment
import com.zk.beatbox.fragment.BeatBoxFragment
import com.zk.beatbox.base.SingleFragmentActivity

class BeatBoxActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment {
        return BeatBoxFragment.newInstance()
    }

}