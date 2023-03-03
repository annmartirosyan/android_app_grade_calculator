package com.example.grade_calculator

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.grade_calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val sharedPreferencesKey = "user_input_values"
    private var homeworkFields = mutableListOf<EditText>()
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivityMainBinding

    private lateinit var attendanceEditText: EditText
    private lateinit var groupPresentationEditText: EditText
    private lateinit var midterm1EditText: EditText
    private lateinit var midterm2EditText: EditText
    private lateinit var finalProjectEditText: EditText
    private lateinit var homeworkFieldEditText: EditText
    private lateinit var homeworkLayoutLinearLayout: LinearLayout

    private var homeworkCount = 1
    private var isResetAllGradesPressed = false

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeViews()
        applyFilters()
        setResetButtonListener()
        addHomeworkField()
        setRemoveButton()
        setResetAllGradesListener()
        setCalculateButtonListener()

    }

    private fun applyFilters() {
        val inputFilter = createInputFilter()
        val editTextMap = mapOf(
            R.id.groupPresentation to "groupPresentationGrade",
            R.id.attendance to "attendanceGrade",
            R.id.midterm1 to "midterm1Grade",
            R.id.midterm2 to "midterm2Grade",
            R.id.finalProject to "finalProjectGrade",
            R.id.homeworkField to "homeworkFieldGrade"
        )
        val defaultGrade = 100

        editTextMap.forEach { (viewId, key) ->
            val editText = findViewById<EditText>(viewId)
            editText.setText(defaultGrade.toString())
            editText.filters = arrayOf(inputFilter)

            sharedPreferences = getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
            val storedGrade = sharedPreferences.getInt(key, 0)
            if (storedGrade != 0) {
                editText.setText(storedGrade.toString())
            }
        }
    }

    private fun initializeViews() {

        attendanceEditText = binding.attendance
        groupPresentationEditText = binding.groupPresentation
        midterm1EditText = binding.midterm1
        midterm2EditText = binding.midterm2
        finalProjectEditText = binding.finalProject
        homeworkFieldEditText = binding.homeworkField
        homeworkLayoutLinearLayout = binding.homeworkLayout

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
        val resetButton: Button = binding.resetButton
        resetButton.setOnClickListener {
            homeworkFields.forEach { editText ->
                editText.text.clear()
            }
            homeworkFieldEditText.text.clear()
        }
    }

    private fun addHomeworkField() {
        val inputFilter = createInputFilter()
        val addButton: Button = binding.addButton
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
        val addButton: Button = binding.addButton
        val removeButton: Button = binding.removeButton
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
        val resetAllGrades: Button = binding.resetAllGrades
        resetAllGrades.setOnClickListener {
            isResetAllGradesPressed = true
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

    private fun EditText.parseNullableInt(): Int? {
        return text?.toString()?.toIntOrNull()
    }

    private fun EditText.parseNullableDouble(): Double? {
        return text?.toString()?.toDoubleOrNull()
    }

    private fun setCalculateButtonListener() {
        val finalGrade: TextView = binding.finalGrade
        val calculateButton: Button = binding.calculateButton

        calculateButton.setOnClickListener {
            if (isResetAllGradesPressed) {
                Toast.makeText(this, "Please enter grades before calculating", Toast.LENGTH_SHORT)
                    .show()
                isResetAllGradesPressed = false
                return@setOnClickListener
            }

            val attendanceGrade = attendanceEditText.parseNullableInt() ?: 0
            val groupPresentationGrade = groupPresentationEditText.parseNullableInt() ?: 0
            val midterm1Grade = midterm1EditText.parseNullableInt() ?: 0
            val midterm2Grade = midterm2EditText.parseNullableInt() ?: 0
            val finalProjectGrade = finalProjectEditText.parseNullableInt() ?: 0
            val homeworkGrades = homeworkFields.map { it.parseNullableDouble() ?: 0.0 }
            val total = (homeworkFieldEditText.parseNullableDouble() ?: 0.0) + homeworkGrades.sum()

            val average = calculateAverage(total, homeworkGrades.size + 1)

            sharedPreferences.edit()
                ?.putInt("attendanceGrade", attendanceGrade)
                ?.putInt("groupPresentationGrade", groupPresentationGrade)
                ?.putInt("midterm1Grade", midterm1Grade)
                ?.putInt("midterm2Grade", midterm2Grade)
                ?.putInt("finalProjectGrade", finalProjectGrade)
                ?.commit()

            finalGrade.text = calculateFinalGrade(
                average,
                attendanceGrade,
                groupPresentationGrade,
                midterm1Grade,
                midterm2Grade,
                finalProjectGrade
            ).toString()
        }
    }


    private fun calculateAverage(total: Double, count: Int): Double {
        return total / count
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

