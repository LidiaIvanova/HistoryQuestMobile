package com.tsu.alotofquestions.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.filter.Filters
import com.otaliastudios.cameraview.filter.MultiFilter
import com.otaliastudios.cameraview.filters.DuotoneFilter
import com.tsu.alotofquestions.MainActivity
import com.tsu.alotofquestions.R
import com.tsu.alotofquestions.data.TaskHelper
import com.tsu.alotofquestions.data.network.APIFactory
import com.tsu.alotofquestions.databinding.FragmentDashboardBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class DashboardFragment : Fragment() {

    //private lateinit var dashboardViewModel: DashboardViewModel
    private val binding by lazy { FragmentDashboardBinding.inflate(layoutInflater) }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cameraView.setLifecycleOwner(this)

        val filter: DuotoneFilter = Filters.DUOTONE.newInstance() as DuotoneFilter
        filter.firstColor = requireContext().getColor(R.color.accent)
        filter.secondColor = Color.BLACK
        binding.cameraView.filter = MultiFilter(Filters.DOCUMENTARY.newInstance(), Filters.GRAIN.newInstance(), filter)
        binding.cameraView.mode = Mode.PICTURE
        binding.cameraView.addCameraListener(object: CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)

                result.toBitmap { bitmap ->
                    if (bitmap != null)
                        MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            bitmap, Date().toString(), "GhostBusters"
                        );
                }

                if (TaskHelper.compareLocationWithTask()) {
                    GlobalScope.launch {
                        var result =  APIFactory.APIService.sendAnswerAsync(APIFactory.token, TaskHelper.FIRST_TASK_ANSWER).await()

                        withContext(Dispatchers.Main) {
                            if (result.isSuccessful) {
                                if (result.body()?.isSuccess!!)
                                    (context as MainActivity).checkCurrentTask()
                            } else {
                                Toast.makeText(requireContext(), "Is it correct place to make a photo?", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
                else {
                    Toast.makeText(requireContext(), "Is it correct place to make a photo?", Toast.LENGTH_LONG).show()
                }
            }
        })


        binding.makePhotoButton.setOnClickListener{
            binding.cameraView.takePicture()
        }

    }
}