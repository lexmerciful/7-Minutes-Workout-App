package com.lex.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import com.lex.a7minutesworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    private var binding: ActivityBmiBinding? = null

    private var currentMetricTab = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBmiBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        //Setting Toolbar and it button Navigation
        setSupportActionBar(binding?.toolbarBMI)
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }

        binding?.toolbarBMI?.setNavigationOnClickListener {
            onBackPressed()
        }

        binding?.btnCalculate?.setOnClickListener {
            calculateUnits()
        }

        binding?.btnClear?.setOnClickListener {
            binding?.etMetricWeight?.setText("")
            binding?.etMetricHeight?.setText("")
            binding?.etMetricFeet?.text?.clear()
            binding?.etMetricInch?.text?.clear()
            binding?.clDisplayBMIResult?.visibility = View.INVISIBLE
            binding?.etMetricWeight?.requestFocus()
        }

        makeMetricUnitVisible()

        binding?.rgUnits?.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbMetricUnit){
                makeMetricUnitVisible()
            }else{
                makeUSUnitVisible()
            }
        }
    }

    private fun makeMetricUnitVisible(){
        currentMetricTab = 0
        binding?.llUSMetricView?.visibility = View.GONE
        binding?.tilMetricHeight?.visibility = View.VISIBLE
        binding?.tilMetricWeight?.hint = "Weight (in kg)"

        binding?.etMetricWeight?.text?.clear()
        binding?.etMetricHeight?.text?.clear()
        binding?.etMetricFeet?.text?.clear()
        binding?.etMetricInch?.text?.clear()
    }

    private fun makeUSUnitVisible(){
        currentMetricTab = 1
        binding?.llUSMetricView?.visibility = View.VISIBLE
        binding?.tilMetricHeight?.visibility = View.INVISIBLE
        binding?.tilMetricWeight?.hint = "Weight (in lb)"

        binding?.etMetricWeight?.text?.clear()
        binding?.etMetricHeight?.text?.clear()
        binding?.etMetricFeet?.text?.clear()
        binding?.etMetricInch?.text?.clear()
    }

    private fun calculateUnits(){
        if (currentMetricTab == 0){
            if (validateMetricUnits()){
                val heightvalue: Float = binding?.etMetricHeight?.text.toString().toFloat() / 100
                val weightvalue: Float = binding?.etMetricWeight?.text.toString().toFloat()
                val bmi: Float = weightvalue / (heightvalue * heightvalue)

                displayBMIResult(bmi)

                val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE)!! as InputMethodManager
                imm.hideSoftInputFromWindow(binding?.etMetricHeight?.windowToken,0)

            }else{
                Toast.makeText(this,"Please enter a valid value", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            if (validateMetricUnits()){
                val weightValue: Float = binding?.etMetricWeight?.text.toString().toFloat()
                val feetValue: Float = binding?.etMetricFeet?.text.toString().toFloat()
                val inchValue: Float = binding?.etMetricInch?.text.toString().toFloat()

                val totalHeightValue: Float = inchValue + feetValue * 12
                val bmi: Float = 703 * (weightValue / (totalHeightValue * totalHeightValue))

                displayBMIResult(bmi)

                val imm : InputMethodManager = getSystemService(INPUT_METHOD_SERVICE)!! as InputMethodManager
                imm.hideSoftInputFromWindow(binding?.etMetricHeight?.windowToken,0)
            }else{
                Toast.makeText(this,"Please enter a valid value", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayBMIResult(bmi: Float){
        val bmiType: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0){
            bmiType = "Very severely underweight"
            bmiDescription = "Oops! You need to take better care of yourself and eat more!"
        }
        else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0){
            bmiType = "Severely underweight"
            bmiDescription = "Oops! You need to take better care of yourself and eat more!"
        }
        else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0){
            bmiType = "Underweight"
            bmiDescription = "Oops! You need to take better care of yourself and eat more!"
        }
        else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0){
            bmiType = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        }
        else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0){
            bmiType = "Overweight"
            bmiDescription = "Oops! You need to take better care of yourself! Maybe workout more!"
        }
        else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0){
            bmiType = "Obese Class I (Moderately obese)"
            bmiDescription = "Oops! You need to take better care of yourself! Maybe workout more!"
        }
        else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0){
            bmiType = "Obese Class II (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }
        else {
            bmiType = "Obese Class III (Very severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.clDisplayBMIResult?.visibility = View.VISIBLE
        binding?.btnClear?.visibility = View.VISIBLE
        binding?.tvBMIValue?.text = bmiValue
        binding?.tvBMIType?.text = bmiType
        binding?.tvBMINote?.text = bmiDescription

    }

    private fun validateMetricUnits(): Boolean{
        var isValid = true

        if (currentMetricTab == 0){
            if (binding?.etMetricWeight?.text?.toString()?.isEmpty() == true || binding?.etMetricHeight?.text?.toString()?.isEmpty() == true){
                isValid = false
            }
        }else{
            if (binding?.etMetricWeight?.text?.toString()?.isEmpty() == true
                || binding?.etMetricFeet?.text?.toString()?.isEmpty() == true || binding?.etMetricInch?.text?.toString()?.isEmpty() == true){
                isValid = false
            }
        }

        return isValid
    }
}