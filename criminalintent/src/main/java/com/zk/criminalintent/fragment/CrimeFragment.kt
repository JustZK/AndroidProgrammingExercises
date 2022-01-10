package com.zk.criminalintent.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.zk.criminalintent.R
import com.zk.criminalintent.bean.Crime
import com.zk.criminalintent.dialog.DatePickerFragment
import com.zk.criminalintent.tools.PictureUtils
import com.zk.criminalintent.vm.CrimeDetailViewModel
import java.io.File
import java.util.*

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    private lateinit var crime: Crime
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var suspectButton: Button
    private lateinit var reportButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView
    private var callbacks: Callbacks? = null

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    public interface Callbacks {
        fun onCrimeUpdated(crime: Crime)
    }

    override fun onAttach(context: Context) {
        Log.d(TAG, "------ onAttach")
        super.onAttach(context)
        callbacks = context as Callbacks
    }

    companion object {
        private const val TAG = "CrimeFragment"

        private const val ARG_CRIME_ID = "crime_id"
        private const val DIALOG_DATE = "DialogDate"

        private const val REQUEST_DATE = 0x01
        private const val REQUEST_CONTACT = 0x02
        private const val REQUEST_PHOTO = 0x03

        fun newInstance(crimeId: UUID) = CrimeFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "------ onCreate")
        super.onCreate(savedInstanceState)
        val uuid = arguments?.getSerializable(ARG_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(uuid)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "------ onCreateView")
        val v = inflater.inflate(R.layout.fragment_crime, container, false)

        titleField = v.findViewById(R.id.crime_title)
        dateButton = v.findViewById(R.id.crime_date)
        solvedCheckBox = v.findViewById(R.id.crime_solved)
        reportButton = v.findViewById(R.id.crime_report)
        suspectButton = v.findViewById(R.id.crime_suspect)
        photoButton = v.findViewById(R.id.crime_camera)
        photoView = v.findViewById(R.id.crime_photo)
//        updatePhotoView()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "------ onViewCreated")
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner, {
                it?.let {
                    this.crime = it
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.zk.criminalintent.fileprovider",
                        photoFile
                    )
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        Log.d(TAG, "------ onStart")
        super.onStart()
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                crime.title = p0.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }
        titleField.addTextChangedListener(textWatcher)

        solvedCheckBox.apply {

            setOnCheckedChangeListener { _, p1 ->
                crime.isSolved = p1
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
            }.also {
                val chooserIntent = Intent.createChooser(it, getString(R.string.send_report))
                startActivity(chooserIntent)
            }

        }

        suspectButton.apply {
            val pickContact = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            setOnClickListener {
                startActivityForResult(pickContact, REQUEST_CONTACT)
            }

            //判读是否有通讯录的软件
            val packageManager = requireActivity().packageManager
            if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
                isEnabled = false
            }
        }

        photoButton.apply {
            val packageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) isEnabled = false

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

                val cameraActivities = packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

                for (ac in cameraActivities) {
                    requireActivity().grantUriPermission(
                        ac.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )

                }

                startActivityForResult(captureImage, REQUEST_PHOTO)

            }
        }

    }

    override fun onStop() {
        Log.d(TAG, "------ onStop")
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    private fun updateUI() {
        Log.d(TAG, "------ updateUI")
        titleField.setText(crime.title)
        dateButton.text = crime.date.toLocaleString()
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotBlank()) {
            suspectButton.text = crime.suspect
        }

        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (!photoFile.exists()) {
            photoView.setImageDrawable(null)
            photoView.contentDescription = getString(R.string.crime_photo_no_image_description)
        } else {
            val bitmap = PictureUtils.getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
            photoView.contentDescription = getString(R.string.crime_photo_image_description)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "------ onActivityResult")
        when {
            resultCode != Activity.RESULT_OK -> return
            requestCode == REQUEST_CONTACT && data != null -> {
                val contactUri = data.data!!
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val c =
                    requireActivity().contentResolver.query(
                        contactUri,
                        queryFields,
                        null,
                        null,
                        null
                    )
                c?.use { it ->
                    if (it.count == 0) return
                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                    suspectButton.text = suspect
                }
            }
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                updatePhotoView()
            }
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dataFormat = "EEE, MMM dd"
        val dateString = DateFormat.format(dataFormat, crime.date).toString()

        var suspect: String? = crime.suspect
        suspect = if (suspect.isNullOrBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    override fun onDetach() {
        Log.d(TAG, "------ onDetach")
        super.onDetach()
        callbacks = null
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    override fun onDateSelected(date: Date) {
        crime.date = date
        updateUI()
    }
}