package com.zk.criminalintentold.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.zk.criminalintentold.R
import com.zk.criminalintentold.base.SingleFragmentActivity
import com.zk.criminalintentold.bean.Crime
import com.zk.criminalintentold.bean.CrimeLab
import com.zk.criminalintentold.fragment.CrimeFragment
import com.zk.criminalintentold.fragment.CrimeListFragment

class CrimeListActivity : SingleFragmentActivity(), CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CrimeLab.instance.init(this)
    }

    override fun createFragment(): Fragment {
        return CrimeListFragment()
    }

    override fun getLayoutResId(): Int {
        return R.layout.activity_masterdetail
    }

    override fun onCrimeSelected(crime: Crime) {
        if (findViewById<FrameLayout>(R.id.detail_fragment_container) == null){
            val intent = CrimePagerActivity.newInstance(this, crime.id)
            startActivity(intent)
        } else {
            val newDetail = CrimeFragment.newInstance(crime.id)

            supportFragmentManager.beginTransaction()
                .replace(R.id.detail_fragment_container, newDetail)
                .commit()
        }
    }

    override fun onCrimeUpdated(crime: Crime) {
        val listFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as CrimeListFragment
        listFragment.updateUI()
    }
}