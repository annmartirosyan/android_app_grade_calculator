The script is a grade calculator application for an Android device. It allows users to input their grades for different coursework components, and calculates their final grade based on a weighting system. The script is written in Kotlin programming language, which is used for developing Android applications.

The script has a class named "MainActivity" which extends "AppCompatActivity". It also contains several variables, functions and a lifecycle method.

Variables
sharedPreferencesKey is a string constant that is used to store user input values in shared preferences.
homeworkFields is a mutable list of EditText objects, used to store dynamically generated homework fields.
sharedPreferences is a SharedPreferences object used to store user input values in the application.
attendanceEditText, groupPresentationEditText, midterm1EditText, midterm2EditText, finalProjectEditText, homeworkFieldEditText are EditText objects used to represent the different coursework components in the application.
homeworkLayoutLinearLayout is a LinearLayout object that stores the dynamically generated homework fields.
homeworkCount is an integer variable that tracks the number of dynamically generated homework fields.


Functions
applyFilters() function applies input filters to the different EditText objects in the application. It also sets default grades for the coursework components, and retrieves any stored values from shared preferences if they exist.
createInputFilter() function creates an input filter that restricts user input to numbers between 0 and 100.
initializeViews() function initializes the different EditText and LinearLayout objects in the application.
setResetButtonListener() function sets a listener for the reset button. When clicked, it clears all homework fields and the homework field input box.
addHomeworkField() function adds a new homework field to the application when the add button is clicked. It restricts the number of homework fields to a maximum of 5.
setRemoveButton() function sets a listener for the remove button. When clicked, it removes the last homework field that was added.
setResetAllGradesListener() function sets a listener for the reset all grades button. When clicked, it clears all the coursework component fields and the homework fields.
setCalculateButtonListener() function sets a listener for the calculate button. When clicked, it calculates the final grade for the user based on the weighted average of the different coursework components.


Lifecycle method
onCreate() is a lifecycle method that is called when the activity is created. It sets the content view to the main activity layout, initializes the views in the application, applies input filters to the EditText objects, sets listeners for the different buttons, and adds a default homework field.
Overall, the script allows users to input their grades and calculate their final grade based on a weighting system. The script is also flexible, allowing users to add or remove homework fields as needed.