package com.zk.criminalintent.vm

import androidx.lifecycle.ViewModel
import com.zk.criminalintent.CrimeRepository
import com.zk.criminalintent.bean.Crime

class CrimeListViewModel: ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimeListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime){
        crimeRepository.addCrime(crime)
    }
}