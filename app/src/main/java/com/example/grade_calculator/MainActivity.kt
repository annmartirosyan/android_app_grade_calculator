package com.example.grade_calculator

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {

    private val sharedPreferencesKey = "user_input_values"
    private val homeworkFields = mutableListOf<EditText>()

    private lateinit var attendance: EditText
    private lateinit var groupPresentation: EditText
    private lateinit var midterm1: EditText
    private lateinit var midterm2: EditText
    private lateinit var finalProject: EditText
    private lateinit var calculateButton: Button
    private lateinit var finalGrade: TextView
    private lateinit var addButton: Button
    private lateinit var homeworkField: EditText
    private lateinit var homeworkLayout: LinearLayout
    private lateinit var homeworkButtons: LinearLayout
    private lateinit var resetAllGrades: Button
    private lateinit var resetButton: Button

    private var homeworkCount = 1
    private var total = 0.0
    private var avg by Delegates.notNull<Double>()


    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        attendance = findViewById(R.id.attendance)
        groupPresentation = findViewById(R.id.groupPresentation)
        midterm1 = findViewById(R.id.midterm1)
        midterm2 = findViewById(R.id.midterm2)
        finalProject = findViewById(R.id.finalProject)
        calculateButton = findViewById(R.id.calculateButton)
        finalGrade = findViewById(R.id.finalGrade)
        addButton = findViewById(R.id.addButton)
        homeworkField = findViewById(R.id.homeworkField)
        homeworkLayout = findViewById(R.id.homeworkLayout)
        homeworkButtons = findViewById(R.id.homeworkButtons)
        resetAllGrades = findViewById(R.id.resetAllGrades)
        resetButton = findViewById(R.id.resetButton)

        val sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        attendance.setText(sharedPreferences.getInt("attd", 0).toString())
        groupPresentation.setText(sharedPreferences.getInt("grpPrs", 0).toString())
        midterm1.setText(sharedPreferences.getInt("mid1", 0).toString())
        midterm2.setText(sharedPreferences.getInt("mid2", 0).toString())
        finalProject.setText(sharedPreferences.getInt("fp", 0).toString())


        val editTextGP = findViewById<EditText>(R.id.groupPresentation)
        val editTextAT = findViewById<EditText>(R.id.attendance)
        val editTextM1 = findViewById<EditText>(R.id.midterm1)
        val editTextM2 = findViewById<EditText>(R.id.midterm2)
        val editTextFP = findViewById<EditText>(R.id.finalProject)

        val inputFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val input = (dest.subSequence(0, dstart).toString() + source.subSequence(start, end) +
                    dest.subSequence(dend, dest.length)).toString()
            if (input.length > 3 || (input.startsWith("0") && input.length > 1) ||
                input.toDoubleOrNull() == null || input.toDouble() !in 0.0..100.0) {
                ""
            } else {
                null
            }
        }
        editTextGP.filters = arrayOf(inputFilter)
        editTextAT.filters = arrayOf(inputFilter)
        editTextM1.filters = arrayOf(inputFilter)
        editTextM2.filters = arrayOf(inputFilter)
        editTextFP.filters = arrayOf(inputFilter)


        fun getEditTextValue(editText: EditText): Int {
            return editText.text.toString().toIntOrNull() ?: 0
        }

        try {
            resetButton.setOnClickListener {
                homeworkFields.forEach { editText ->
                    editText.text.clear()
                }
                homeworkField.text.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        try {
            addButton.setOnClickListener {
                if (homeworkCount < 5) {
                    homeworkCount++
                    val newHomeworkLabel = "Homework $homeworkCount"
                    val newHomeworkField = EditText(this)
                    newHomeworkField.id = View.generateViewId()
                    newHomeworkField.layoutParams = homeworkField.layoutParams
                    newHomeworkField.inputType = InputType.TYPE_CLASS_NUMBER or
                            InputType.TYPE_NUMBER_FLAG_DECIMAL
                    homeworkLayout.addView(TextView(this).apply {
                        text = newHomeworkLabel
                    })
                    homeworkLayout.addView(newHomeworkField)
                    homeworkFields.add(newHomeworkField)
                }
                if (homeworkCount == 5) {
                    addButton.isEnabled = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        try {
            resetAllGrades.setOnClickListener {
                attendance.text.clear()
                groupPresentation.text.clear()
                midterm1.text.clear()
                midterm2.text.clear()
                finalProject.text.clear()
                homeworkFields.forEach { editText ->
                    editText.text.clear()
                }
                homeworkField.text.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            calculateButton.setOnClickListener {
                val attd = getEditTextValue(attendance)
                val grpPrs = getEditTextValue(groupPresentation)
                val mid1 = getEditTextValue(midterm1)
                val mid2 = getEditTextValue(midterm2)
                val fp = getEditTextValue(finalProject)

                total = 0.0
                var count = 1
                for (field in homeworkFields) {
                    count +=1
                    total += field.text.toString().toDoubleOrNull() ?: 0.0
                }
                val hw1 = homeworkField.text.toString().toDoubleOrNull() ?: 0.0
                total += hw1
                avg= total/ count


                with(sharedPreferences.edit()) {
                    putInt("attd", attd)
                    putInt("grpPrs", grpPrs)
                    putInt("mid1", mid1)
                    putInt("mid2", mid2)
                    putInt("fp", fp)
                    apply()
                }

                val finalGradeValue =
                    calculateFinalGrade(avg, attd, grpPrs, mid1, mid2, fp)
                finalGrade.text = finalGradeValue.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun calculateFinalGrade(
        avg:Double,
        attd: Int,
        grpPrs: Int,
        mid1: Int,
        mid2: Int,
        fp: Int
    ): Double {
        val hwWeight = 0.2
        val attdWeight = 0.1
        val grpPrsWeight = 0.1
        val mid1Weight = 0.1
        val mid2Weight = 0.2
        val fpWeight = 0.3

        return avg * hwWeight +
                attd * attdWeight +
                grpPrs * grpPrsWeight +
                mid1 * mid1Weight +
                mid2 * mid2Weight +
                fp * fpWeight
    }
}

