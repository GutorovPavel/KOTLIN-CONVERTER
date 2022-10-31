package com.example.converter_lab

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.converter_lab.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var inputReady: String = ""

    private var convertFrom: String = ""
    private var convertTo: String = ""

    private lateinit var clipboard: ClipboardManager

    private val convertibleValues: Map<String, Double> = mapOf(
        "BYN" to 1.0,  "EUR" to 2.45996, "USD" to 2.53473,
        "m" to 1.0, "dm" to 0.1, "cm" to 0.01,
        "kg" to 1.0, "g" to 0.001, "t" to 1000.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(Currency())
        spinnerSetup(R.array.currencies)
        cTitle.text = "CURRENCY CONVERTER"
        clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        binding.bottomAppBar.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.currency_fr -> {
                    replaceFragment(Currency.newInstance())
                    spinnerSetup(R.array.currencies)
                    cTitle.text = "CURRENCY CONVERTER"
                }
                R.id.distance_fr -> {
                    replaceFragment(Distance.newInstance())
                    spinnerSetup(R.array.distance)
                    cTitle.text = "DISTANCE CONVERTER"
                }
                R.id.weight_fr -> {
                    replaceFragment(Weight.newInstance())
                    spinnerSetup(R.array.weight)
                    cTitle.text = "WEIGHT CONVERTER"
                }

                else -> {}
            }
            true
        }

        forbidActions(inputArea)
        textChange()
    }

    private fun forbidActions(input: TextView){
        input.customSelectionActionModeCallback = object : ActionMode.Callback{
            override fun onCreateActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(p0: ActionMode?) {}
        }
    }

    private fun reload() {
        inputArea.setText("")
        outputArea.text = ""
    }

    private fun replaceFragment(fragment: Fragment) {
        //inputArea.showSoftInputOnFocus = false
        inputArea.inputType = InputType.TYPE_NULL
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.place_holder, fragment).commit()
        reload()
    }

    /////////////////////// BIND BUTTONS ////////////////////////

    private fun updateText(str: String) {
        inputArea.showSoftInputOnFocus = false
        inputArea.inputType = InputType.TYPE_CLASS_TEXT

        var max = 16
        val oldStr: String = inputArea.text.toString()
        var cursorPos = inputArea.selectionStart
        var leftStr: String = oldStr.substring(0, cursorPos)
        val rightStr: String = oldStr.substring(cursorPos)

        if (oldStr.endsWith(".")) max += 1

        if (inputArea.text.length < max) {
            if (oldStr == "0" && str != ".") {
                leftStr = ""
                inputArea.setText(String.format("%s%s%s", leftStr, str, rightStr))
                cursorPos = 0
            }
            if (("." in oldStr || oldStr == "") && str == ".") {

            } else {
                inputArea.setText(String.format("%s%s%s", leftStr, str, rightStr))
                inputArea.setSelection(cursorPos + 1)
            }
        }
    }

    fun clearBtn(view: View) {
        inputArea.setText("")
    }

    fun delBtn(view: View) {
        val cursorPos = inputArea.selectionStart
        val textLen = inputArea.text.length

        if (cursorPos != 0 && textLen != 0) {
            val selection: SpannableStringBuilder = (SpannableStringBuilder(inputArea.text))
            selection.replace(cursorPos - 1, cursorPos, "")
            inputArea.text = selection
            inputArea.setSelection(cursorPos - 1)
        }
    }

    fun numberBtn(view: View) {
        if (view is androidx.appcompat.widget.AppCompatButton) {
            updateText(view.text.toString())
        }
    }

    fun swapBtn(view: View) {
        val temp = spinnerFrom.selectedItemId.toInt()
        spinnerFrom.setSelection(spinnerTo.selectedItemId.toInt())
        spinnerTo.setSelection(temp)
        conversion()
    }

    fun copyBtn(view: View) {
        val clip = ClipData.newPlainText("TextView", outputArea.text.toString())
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied!", Toast.LENGTH_SHORT).show()
    }

    fun pasteBtn(view: View) {
        val copied = clipboard.primaryClip?.getItemAt(0)
        updateText(copied?.text.toString())
        Toast.makeText(this, "Pasted!", Toast.LENGTH_SHORT).show()
    }

    ////////////////////////////////////////////////////////////

    private fun conversion() {
        if (inputArea != null &&
            inputArea.text.isNotEmpty() &&
            inputArea.text.isNotBlank())
        {
            Formatter.BigDecimalLayoutForm.SCIENTIFIC
            val res = (inputArea.text.toString().toBigDecimal()) *
                    (convertibleValues[convertFrom]!! / convertibleValues[convertTo]!!)
                        .toBigDecimal()

            inputReady = res.toPlainString()
            if (inputReady.length > 16) {
                var dotIndex = inputReady.indexOf(".")

                if (dotIndex == 15) {
                    dotIndex += 2
                    inputReady = inputReady.substring(0, dotIndex)
                }
                else {
                    inputReady = inputReady.substring(0, 16)
                }
            }
            outputArea.text = inputReady
        }
        else {
            outputArea.text = ""
        }
    }

    private fun textChange() {
        inputArea.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main", "before text changed")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                Log.d("Main", "on text changed")
            }

            override fun afterTextChanged(p0: Editable?) {
                conversion()
            }
        })
    }

    private fun spinnerSetup(arr: Int) {
        val arrayAdapter =
            ArrayAdapter.createFromResource(this, arr, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val spinner1: Spinner = findViewById(R.id.spinnerFrom)
        val spinner2: Spinner = findViewById(R.id.spinnerTo)
        spinner1.adapter = arrayAdapter
        spinner2.adapter = arrayAdapter


        spinner1.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                convertFrom = spinner1.selectedItem.toString()
                conversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                convertFrom = spinner2.selectedItem.toString()
            }
        })
        spinner2.onItemSelectedListener = (object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                convertTo = spinner2.selectedItem.toString()
                conversion()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                convertTo = spinner2.selectedItem.toString()
            }
        })
    }

    /////////////////////////////// LIFECYCLE ////////////////////////////

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putString("input", inputArea.text.toString())

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputArea.setText(savedInstanceState.getString("input"))
    }

    override fun onResume() {
        super.onResume()
        inputArea.inputType = InputType.TYPE_NULL
    }

    override fun onStop() {
        super.onStop()
        inputArea.inputType = InputType.TYPE_CLASS_TEXT
    }
}