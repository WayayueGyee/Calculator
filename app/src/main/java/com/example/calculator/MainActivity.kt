package com.example.calculator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.parser.calculateResult

class MainActivity : AppCompatActivity() {
    private var numberOfOpenedBrackets = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun digitsOnClick(view: View) {
        val editText: EditText = findViewById(R.id.editText)
        editText.append((view as Button).text)
    }

    fun clearEditTextOnClick(view: View) {
        val editText: EditText = findViewById(R.id.editText)
        editText.setText("")
        numberOfOpenedBrackets = 0
    }

    // *, / and + can be placed only after digit or closing bracket
    fun operatorsOnClick(view: View) {
        val editText: EditText = findViewById(R.id.editText)
        val operatorToAdd: String =
            when ((view as Button).text) {
            "X" -> "*"
            "÷" -> "/"
            else -> view.text.toString()
        }

        if (operatorToAdd == "-" && Regex("^$|[\\d()^]$").containsMatchIn(editText.text)) {
            editText.append(view.text)
        }
        else if (Regex("[\\d)]$").containsMatchIn(editText.text)) {
            editText.append(operatorToAdd)
        }
        else {
            editText.setText(editText.text.replace(Regex("[*/\\-+%]\$"), operatorToAdd))
        }
    }

    /*
    Открывающиеся скобки "(" ставятся:
        1) Если нет других открывающихся скобок
        2) После операторов
        3) После открывающихся скобок
        4) Если у каждой открывающейся скобки есть закрывающаяся
        В иных случаях ставится закрывающаяся скобка ")"
     */
    fun parenthesesOnClick(view: View) {
        val editText: EditText = findViewById(R.id.editText)

        if (Regex("[(*/+-]$").containsMatchIn(editText.text) || numberOfOpenedBrackets == 0) {
            editText.append("(")
            numberOfOpenedBrackets++
        }
        else {
            editText.append(")")
            numberOfOpenedBrackets--
        }
    }

    // TODO: сделать так, чтобы точку нельзя было поставть несколько раз в одно число
    fun dotOnClick(view: View) {
        val editText: EditText = findViewById(R.id.editText)

        if (editText.text.isNotBlank()) {
            if (!Regex("[+\\-*/]\$|\\d+\\.\\d*\\s*\$").containsMatchIn(editText.text)) {
                editText.append(".")
            }
        }
    }

    fun equalsOnClick(view: View) {
        val editText: EditText = findViewById(R.id.editText)
        try {
            editText.setText(calculateResult(editText.text.toString()).toString())
        }
        catch (e: Exception) {
        }
        editText.setSelection(editText.length())
    }
}

class WrongExpression(message: String) : Exception(message)
