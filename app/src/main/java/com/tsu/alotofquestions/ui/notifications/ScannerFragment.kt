package com.tsu.alotofquestions.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import com.tsu.alotofquestions.databinding.FragmentNotificationsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ScannerFragment : Fragment() {

    private val binding by lazy { FragmentNotificationsBinding.inflate(layoutInflater) }
    private lateinit var codeScanner: CodeScanner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        codeScanner = CodeScanner(requireContext(), binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = Collections.unmodifiableList(
            listOf(
               BarcodeFormat.PDF_417
            )
        ) // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.CONTINUOUS // or CONTINUOUS
        codeScanner.scanMode = ScanMode.CONTINUOUS // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                    //TODO: send results
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context, "Camera initialization error: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }


      //  codeScanner.startPreview()

    }

    override fun onResume() {
        super.onResume()


            codeScanner.startPreview()

    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}