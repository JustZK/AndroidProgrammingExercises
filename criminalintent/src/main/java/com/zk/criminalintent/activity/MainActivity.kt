package com.zk.criminalintent.activity

import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.zk.criminalintent.R
import com.zk.criminalintent.base.SingleFragmentActivity
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.fragment.CrimeFragment
import com.zk.criminalintent.fragment.CrimeListFragment
import java.util.*

class MainActivity : SingleFragmentActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    override fun createFragment(): Fragment {
        return CrimeListFragment.newInstance()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }

    override fun onCrimeSelected(crimeId: UUID) {
        if (findViewById<FrameLayout>(R.id.detail_fragment_container) == null){
            //小屏幕，替换显示

            val newDetail = CrimeFragment.newInstance(crimeId)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, newDetail)
                .addToBackStack(null)
                .commit()

        } else {
            //大屏幕，直接在右边显示

            val newDetail = CrimeFragment.newInstance(crimeId)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit()
        }
    }

    override fun onCrimeUpdated(crime: Crime) {
        Log.d("MainActivity", "------ onCrimeUpdated")
        val listFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as CrimeListFragment
        listFragment.updateUI()
    }
}