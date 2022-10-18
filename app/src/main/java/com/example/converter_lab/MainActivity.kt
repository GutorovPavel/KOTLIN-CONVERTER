package com.example.converter_lab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    var baseCurrency = "BYN"
    var convertedToCurrency = "EUR"
    var conversionRate = 0f

    var convertedToDistance = "inch"
    var convertedToWeight = "pounds"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spinnerSetup()
        textChanged()
    }

    private fun textChanged() {
        et_firstConversion.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main", "before text changed")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main", "on text changed")
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    getApiResult()
                    getDistanceResult()
                    getWeightResult()
                }
                catch (e: Exception) {
                    Log.e("Main", "$e")
                }
            }

        })
    }

    private fun getApiResult() {
        if(et_firstConversion != null &&
            et_firstConversion.text.isNotEmpty() &&
            et_firstConversion.text.isNotBlank()) {

            //apikey=FcbkosMOF7884DLJbPjAHmgcuJ8PAMEx&  // API-key
            //val API = "https://api.apilayer.com/fixer/latest?base={$baseCurrency}&symbols={$convertedToCurrency}&apikey=FcbkosMOF7884DLJbPjAHmgcuJ8PAMEx"
            val API = "*"

            if(baseCurrency == convertedToCurrency) {
                Toast.makeText(applicationContext,
                    "Cannot convert the same currency", Toast.LENGTH_SHORT).show()
            }
            else {
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val apiResult = URL(API).readText()
                        val jsonObject = JSONObject(apiResult)

                        conversionRate = jsonObject.getJSONObject("rates").getString(convertedToCurrency).toFloat()

                        withContext(Dispatchers.Main) {
                            val text = (et_firstConversion.text.toString().toFloat() * conversionRate).toString()
                            et_secondConversion?.setText(text)
                        }
                    }
                    catch (e: Exception) {
                        Log.e("Main", "$e")
                    }
                }
            }
        }
    }

    private fun getDistanceResult() {
        if(et2_firstConversion != null &&
            et2_firstConversion.text.isNotEmpty() &&
            et2_firstConversion.text.isNotBlank()) {

            if(convertedToDistance == "inch") {
                val text = (et2_firstConversion.text.toString().toFloat() * 39.37).toString()
                et2_secondConversion?.setText(text)
            }
            if(convertedToDistance == "cm") {
                val text = (et2_firstConversion.text.toString().toFloat() * 100).toString()
                et2_secondConversion?.setText(text)
            }
            if(convertedToDistance == "km") {
                val text = (et2_firstConversion.text.toString().toFloat() * 0.01).toString()
                et2_secondConversion?.setText(text)
            }
        }
    }

    private fun getWeightResult() {
        if(et3_firstConversion != null &&
            et3_firstConversion.text.isNotEmpty() &&
            et3_firstConversion.text.isNotBlank()) {

            if(convertedToDistance == "pounds") {
                val text = (et2_firstConversion.text.toString().toFloat() * 0.061).toString()
                et3_secondConversion?.setText(text)
            }
            if(convertedToDistance == "g") {
                val text = (et2_firstConversion.text.toString().toFloat() * 1000).toString()
                et3_secondConversion?.setText(text)
            }
            if(convertedToDistance == "centner") {
                val text = (et2_firstConversion.text.toString().toFloat() * 0.01).toString()
                et3_secondConversion?.setText(text)
            }
        }
    }

    private fun spinnerSetup() {
        val spinner1: Spinner = findViewById(R.id.spinner_firstConversion)
        val spinner2: Spinner = findViewById(R.id.spinner_secondConversion)
        val spinner3: Spinner = findViewById(R.id.spinner2_secondConversion)
        val spinner4: Spinner = findViewById(R.id.spinner3_secondConversion)

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner1.adapter = adapter
        }

        ArrayAdapter.createFromResource(
            this,
            R.array.currencies2,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner2.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.distance,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner3.adapter = adapter
        }
        ArrayAdapter.createFromResource(
            this,
            R.array.weight,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner4.adapter = adapter
        }

        spinner1.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                baseCurrency = parent?.getItemAtPosition(pos).toString()
                getApiResult()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })
        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                convertedToCurrency = parent?.getItemAtPosition(pos).toString()
                getApiResult()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })
        spinner3.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                convertedToDistance = parent?.getItemAtPosition(pos).toString()
                getDistanceResult()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })
        spinner4.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                convertedToWeight = parent?.getItemAtPosition(pos).toString()
                getWeightResult()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        })
    }
}