package com.zk.criminalintent.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zk.criminalintent.R
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.vm.CrimeListViewModel
import java.util.*



class CrimeListFragment : Fragment() {
    private lateinit var crimeRecyclerView: RecyclerView
    private var adapter = CrimeAdapter(emptyList())
    private var subtitleVisible = false
    private var callbacks: Callbacks? = null

    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProvider(this).get(CrimeListViewModel::class.java)
    }

    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "------ onAttach")
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "------ onCreate")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    companion object {
        private const val TAG = "CrimeListFragment"

        private const val REQUEST_CRIME = 0x01

        private const val SAVE_SUBTITLE_VISIBLE = "subtitle"

        fun newInstance(): CrimeListFragment {
            return CrimeListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "------ onCreateView")
        val view = inflater.inflate(R.layout.fragment_crime_list, container, false)
        crimeRecyclerView = view.findViewById(R.id.crime_recycler_view)
        crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        crimeRecyclerView.adapter = adapter

        if (savedInstanceState != null) subtitleVisible =
            savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBLE)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "------ onViewCreated")
        super.onViewCreated(view, savedInstanceState)
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, "------ onCreateOptionsMenu")
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.frament_crime_list, menu)

        val subtitleItem = menu.findItem(R.id.show_subtitle)
        if (subtitleVisible) subtitleItem.setTitle(R.string.hide_subtitle)
        else subtitleItem.setTitle(R.string.show_subtitle)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_crime -> {
                val crime = Crime()
                crimeListViewModel.addCrime(crime)
                updateSubtitle(adapter.itemCount + 1)
                callbacks?.onCrimeSelected(crime.id)
                true
            }
            R.id.show_subtitle -> {
                subtitleVisible = !subtitleVisible
                activity?.invalidateOptionsMenu()
                updateSubtitle(adapter.itemCount)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun updateSubtitle(crimeCount: Int) {
        var subtitle: String? = getString(R.string.subtitle_format, crimeCount)

        if (!subtitleVisible) subtitle = null
        (activity as AppCompatActivity).supportActionBar?.subtitle = subtitle
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.d(TAG, "------ onSaveInstanceState")
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVE_SUBTITLE_VISIBLE, subtitleVisible)
    }

    fun updateUI() {
        Log.d(TAG, "------ updateUI")
        crimeListViewModel.crimeListLiveData.observe(
            viewLifecycleOwner,
            { crimes ->
                crimes?.let {
                    Log.d(TAG, "Got crimes ${crimes.size}")
                    adapter.crimes = crimes
                    adapter.notifyDataSetChanged()
                    updateSubtitle(crimes.size)
                }
            }
        )
    }

    private inner class CrimeHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

        init {
            itemView.setOnClickListener(this)
        }

        private val titleTextView = itemView.findViewById<TextView>(R.id.crime_title)
        private val dateTextView = itemView.findViewById<TextView>(R.id.crime_date)
        private val solvedImageView = itemView.findViewById<ImageView>(R.id.crime_solved)

        private lateinit var crime: Crime

        fun bind(crime: Crime) {
            this.crime = crime
            titleTextView.text = crime.title
            dateTextView.text = crime.date.toLocaleString()
            solvedImageView.visibility = if (crime.isSolved) View.VISIBLE else View.GONE
        }

        override fun onClick(v: View?) {
            callbacks?.onCrimeSelected(crime.id)
        }
    }

    private inner class CrimeAdapter(var crimes: List<Crime>) :
        RecyclerView.Adapter<CrimeHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime, parent, false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int {
            return crimes.size
        }

    }

    override fun onDetach() {
        Log.d(TAG, "------ onDetach")
        super.onDetach()
        callbacks = null
    }
}