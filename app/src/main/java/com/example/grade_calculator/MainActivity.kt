package com.example.grade_calculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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
    private var homeworkFields = mutableListOf<EditText>()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var attendanceEditText: EditText
    private lateinit var groupPresentationEditText: EditText
    private lateinit var midterm1EditText: EditText
    private lateinit var midterm2EditText: EditText
    private lateinit var finalProjectEditText: EditText
    private lateinit var homeworkFieldEditText: EditText
    private lateinit var homeworkLayoutLinearLayout: LinearLayout

    private var homeworkCount = 1

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        loadSharedPreferences()
        applyFilters()
        setResetButtonListener()
        addHomeworkField()
        setRemoveButton()
        setResetAllGradesListener()
        setCalculateButtonListener()

    }

    private fun applyFilters() {
        val inputFilter = createInputFilter()

        val editTextGP = findViewById<EditText>(R.id.groupPresentation)
        val editTextAT = findViewById<EditText>(R.id.attendance)
        val editTextM1 = findViewById<EditText>(R.id.midterm1)
        val editTextM2 = findViewById<EditText>(R.id.midterm2)
        val editTextFP = findViewById<EditText>(R.id.finalProject)
        val editTextHF = findViewById<EditText>(R.id.homeworkField)

        editTextGP.filters = arrayOf(inputFilter)
        editTextAT.filters = arrayOf(inputFilter)
        editTextM1.filters = arrayOf(inputFilter)
        editTextM2.filters = arrayOf(inputFilter)
        editTextFP.filters = arrayOf(inputFilter)
        editTextHF.filters = arrayOf(inputFilter)
    }

    private fun EditText.getIntValue(): Int {
        return this.text.toString().toIntOrNull() ?: 0
    }

    private fun initializeViews() {

        attendanceEditText = findViewById(R.id.attendance)
        groupPresentationEditText = findViewById(R.id.groupPresentation)
        midterm1EditText = findViewById(R.id.midterm1)
        midterm2EditText = findViewById(R.id.midterm2)
        finalProjectEditText = findViewById(R.id.finalProject)
        homeworkFieldEditText = findViewById(R.id.homeworkField)
        homeworkLayoutLinearLayout = findViewById(R.id.homeworkLayout)

    }

    private fun loadSharedPreferences() {
        sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        attendanceEditText.setText(sharedPreferences.getInt("attendanceGrade", 0).toString())
        groupPresentationEditText.setText(sharedPreferences.getInt("groupPresentationGrade", 0).toString())
        midterm1EditText.setText(sharedPreferences.getInt("midterm1Grade", 0).toString())
        midterm2EditText.setText(sharedPreferences.getInt("midterm2Grade", 0).toString())
        finalProjectEditText.setText(sharedPreferences.getInt("finalProjectGrade", 0).toString())
    }

    private fun createInputFilter(): InputFilter {
        return InputFilter { source, start, end, dest, dstart, dend ->
            val input = (dest.subSequence(0, dstart).toString() + source.subSequence(start, end) +
                    dest.subSequence(dend, dest.length))
            if (input.length > 3 || (input.startsWith("0") && input.length > 1) ||
                input.toDoubleOrNull() == null || input.toDouble() !in 0.0..100.0
            ) {
                ""
            } else {
                null
            }
        }
    }

    private fun setResetButtonListener() {
        val resetButton: Button = findViewById(R.id.resetButton)
        resetButton.setOnClickListener {
            homeworkFields.forEach { editText ->
                editText.text.clear()
            }
            homeworkFieldEditText.text.clear()
        }
    }

    private fun addHomeworkField() {
        val inputFilter = createInputFilter()
        val addButton: Button = findViewById(R.id.addButton)
        addButton.setOnClickListener {
            if (homeworkCount < 5) {
                homeworkCount++
                val newHomeworkLabel = "Homework $homeworkCount"
                val newHomeworkField = EditText(this)
                newHomeworkField.id = View.generateViewId()
                newHomeworkField.layoutParams = homeworkFieldEditText.layoutParams
                newHomeworkField.inputType = InputType.TYPE_CLASS_NUMBER or
                        InputType.TYPE_NUMBER_FLAG_DECIMAL
                newHomeworkField.filters = arrayOf(inputFilter)
                homeworkLayoutLinearLayout.addView(TextView(this).apply {
                    text = newHomeworkLabel
                })
                homeworkLayoutLinearLayout.addView(newHomeworkField)
                homeworkFields.add(newHomeworkField)
            }
            if (homeworkCount == 5) {
                addButton.isEnabled = false
            }
        }
    }

    private fun setRemoveButton() {
        val addButton: Button = findViewById(R.id.addButton)
        val removeButton: Button = findViewById(R.id.removeButton)
        removeButton.setOnClickListener {
            if (homeworkCount > 1) {
                homeworkFields.removeLast().let { removedField ->
                    val removedLabelIndex =
                        homeworkLayoutLinearLayout.indexOfChild(removedField) - 1
                    homeworkLayoutLinearLayout.removeViewAt(removedLabelIndex)
                    homeworkLayoutLinearLayout.removeView(removedField)
                    homeworkCount--
                    addButton.isEnabled = true
                }
            }
        }
    }

    private fun setResetAllGradesListener() {
        val resetAllGrades: Button = findViewById(R.id.resetAllGrades)
        resetAllGrades.setOnClickListener {
            attendanceEditText.text?.clear()
            groupPresentationEditText.text.clear()
            midterm1EditText.text.clear()
            midterm2EditText.text.clear()
            finalProjectEditText.text.clear()
            homeworkFields.forEach { editText ->
                editText.text.clear()
            }
            homeworkFieldEditText.text.clear()
        }
    }

    private fun setCalculateButtonListener() {
        val finalGrade: TextView = findViewById(R.id.finalGrade)
        val calculateButton: Button = findViewById(R.id.calculateButton)
        calculateButton.setOnClickListener {
            val attendanceGrade = attendanceEditText.getIntValue()
            val groupPresentationGrade = groupPresentationEditText.getIntValue()
            val midterm1Grade = midterm1EditText.getIntValue()
            val midterm2Grade = midterm2EditText.getIntValue()
            val finalProjectGrade = finalProjectEditText.getIntValue()
            var total = 0.0
            var average by Delegates.notNull<Double>()

            var count = 1
            for (field in homeworkFields) {
                count += 1
                total += field.text.toString().toDoubleOrNull() ?: 0.0
            }
            val hw1 = homeworkFieldEditText.text.toString().toDoubleOrNull() ?: 0.0
            total += hw1
            average = total / count


            with(sharedPreferences.edit()) {
                putInt("attendanceGrade", attendanceGrade)
                putInt("groupPresentationGrade", groupPresentationGrade)
                putInt("midterm1Grade", midterm1Grade)
                putInt("midterm2Grade", midterm2Grade)
                putInt("finalProjectGrade", finalProjectGrade)
                apply()
            }

            val finalGradeValue = calculateFinalGrade(
                average, attendanceGrade,
                groupPresentationGrade, midterm1Grade, midterm2Grade, finalProjectGrade
            )
            finalGrade.text = finalGradeValue.toString()
        }
    }

    private fun calculateFinalGrade(
        average: Double,
        attendanceGrade: Int,
        groupPresentationGrade: Int,
        midterm1Grade: Int,
        midterm2Grade: Int,
        finalProjectGrade: Int
    ): Double {
        val homeworkWeight = 0.2
        val attendanceWeight = 0.1
        val groupPresentationWeight = 0.1
        val midterm1Weight = 0.1
        val midterm2Weight = 0.2
        val finalProjectWeight = 0.3

        return average * homeworkWeight +
                attendanceGrade * attendanceWeight +
                groupPresentationGrade * groupPresentationWeight +
                midterm1Grade * midterm1Weight +
                midterm2Grade * midterm2Weight +
                finalProjectGrade * finalProjectWeight
    }
}

