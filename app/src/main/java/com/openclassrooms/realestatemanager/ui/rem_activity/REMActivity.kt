package com.openclassrooms.realestatemanager.ui.rem_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.openclassrooms.realestatemanager.ui.list_property_fragment.ListPropertyFragment
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.utilities.ViewModelFactory


class REMActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_r_e_m)

        supportFragmentManager.beginTransaction().replace(R.id.container1,
            ListPropertyFragment.newInstance()).commit()

        val viewModel = ViewModelProvider(this, ViewModelFactory).get(REMActivityViewModel::class.java)

    }


}
