package com.example.miniproject.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.example.miniproject.R


class RequestFragment : Fragment() {

    private lateinit var spinnerType: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_request, container, false)

        // Initialize views
        initViews(view)

        setupItemSelectedListener()
        return view
    }

    private fun initViews(view: View){
        spinnerType = view.findViewById(R.id.spinner_request_type)
    }

    private fun setupItemSelectedListener(){
        //get type from string.xml
        val items = resources.getStringArray(R.array.requestType)

        setupArrayAdapterFromRes(items)

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val typeSelected = items[position]

                val fragment = when (typeSelected) {
                    "Request check-in" -> RequestCheckInFragment()
                    "Request check-out" ->  RequestCheckOutFragment()
                    "Request OT" -> RequestOTFragment()
                    "Request leave" -> RequestLeaveFragment()
                    else -> {
                        null
                    }
                }

                if (fragment != null) {
                    childFragmentManager.beginTransaction()
                        .replace(R.id.frame_sub, fragment)
                        .commit()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {}

        }
    }

    private fun setupArrayAdapterFromRes(items: Array<String>) {
        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.requestType,
            android.R.layout.simple_spinner_item
        )

        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerType.adapter = arrayAdapter
    }

}


