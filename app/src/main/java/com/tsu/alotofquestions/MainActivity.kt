package com.tsu.alotofquestions

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import com.tsu.alotofquestions.data.TaskHelper
import com.tsu.alotofquestions.data.network.APIFactory
import com.tsu.alotofquestions.databinding.ActivityMainBinding
import com.tsu.alotofquestions.ui.EnergyFragment
import com.tsu.alotofquestions.ui.NoteFragment
import com.tsu.alotofquestions.ui.TaskFragment
import com.tsu.alotofquestions.ui.dashboard.DashboardFragment
import com.tsu.alotofquestions.ui.home.HomeFragment
import com.tsu.alotofquestions.ui.notifications.ScannerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nl.joery.animatedbottombar.AnimatedBottomBar


class MainActivity : AppCompatActivity() {

    private var mPermissionGranted = false
    private val RC_PERMISSION = 10
    val TASK_ONE_ID = 1
    val TASK_TWO_ID = 2
    val TASK_THREE_ID = 3
    val TASK_FOUR_ID = 4
    val TASK_FIVE_ID = 5
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private  lateinit var locationManager: LocationManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
      //  val navView: BottomNavigationView = findViewById(R.id.nav_view)

       // val navController = findNavController(R.id.NavHostFragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
      /*  val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.taskFragment, R.id.navigation_photo, R.id.noteFragment
            )
        )*/

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION),
                RC_PERMISSION
            )
        return
        }
        requestLocation()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = false
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    RC_PERMISSION
                )
            } else {
                mPermissionGranted = true
            }
        } else {
            mPermissionGranted = true
        }

        checkCurrentTask()

        binding.bottomBar.setOnTabSelectListener(object: AnimatedBottomBar.OnTabInterceptListener,
            AnimatedBottomBar.OnTabSelectListener {

            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                var manager = supportFragmentManager
                val fragmentTransaction: FragmentTransaction = manager
                    .beginTransaction()
                when (newIndex) {
                    0 -> {

                        fragmentTransaction.replace(
                            R.id.fragmentContainer, TaskFragment(),
                            "task"
                        )

                    }
                    1 -> {
                        when (TaskHelper.currentTask?.id) {
                            TASK_ONE_ID -> {
                                fragmentTransaction.replace(
                                    R.id.fragmentContainer, DashboardFragment(),
                                    "instrument"
                                )
                            }
                            TASK_TWO_ID -> {
                                fragmentTransaction.replace(
                                    R.id.fragmentContainer, HomeFragment(),
                                    "instrument"
                                )
                            }
                            TASK_THREE_ID -> {
                                fragmentTransaction.replace(
                                    R.id.fragmentContainer, EnergyFragment(),
                                    "instrument"
                                )
                            }
                            TASK_FOUR_ID -> {
                                fragmentTransaction.replace(
                                    R.id.fragmentContainer, ScannerFragment(),
                                    "instrument"
                                )
                            }
                        }

                    }
                    2 -> {
                        fragmentTransaction.replace(
                            R.id.fragmentContainer, NoteFragment(),
                            "notes"
                        )
                    }

                }
                fragmentTransaction.commit()
            }

            override fun onTabIntercepted(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ): Boolean {
                return false
            }


        })

        //   setupActionBarWithNavController(navController, appBarConfiguration)
      //  navView.setupWithNavController(navController)
    }

    fun checkCurrentTask() {
        //teams/location
        binding.loading.visibility = View.VISIBLE
        GlobalScope.launch {
            var result =  APIFactory.APIService.getTaskAsync(APIFactory.token).await()

            withContext(Dispatchers.Main) {
                if (result.isSuccessful) {
                    TaskHelper.currentTask = result.body()
                    setupScreen()
                } else {
                    //TODO: show error
                }
            }
        }
    }

    fun requestLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30*1000,
            50.0f
        ) { p0 ->
            //TODO: send location
            TaskHelper.currentLocation = p0
        }
    }

    fun setupScreen() {
     //   val navController = findNavController(R.id.NavHostFragment)


        binding.bottomBar.selectTabAt(0, false)

        binding.fragmentContainer.visibility = View.VISIBLE
        binding.loading.visibility = View.GONE
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_PERMISSION) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionGranted = true

            } else {
                mPermissionGranted = false
            }
        }
        requestLocation()
        checkCurrentTask()
    }
}

