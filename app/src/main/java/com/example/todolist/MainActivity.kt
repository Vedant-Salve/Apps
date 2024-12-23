package com.example.todolist

import android.icu.text.Transliterator.Position
import androidx.activity.enableEdgeToEdge
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*


import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
//    Made by Vedant Salve

    private lateinit var editTextTask: EditText
    private lateinit var buttonAddTask: Button
    private lateinit var listViewTasks: ListView
    private lateinit var textViewGreeting: TextView

    private val tasklist=ArrayList<String>()
    private lateinit var taskAdapter: TaskAdapter

    private val sharedPreferences: SharedPreferences by lazy {
        getSharedPreferences("com.example.todolist.PREFS", Context.MODE_PRIVATE)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        editTextTask=findViewById(R.id.EditText)
        buttonAddTask=findViewById(R.id.add_btn)
        listViewTasks=findViewById(R.id.tasklistview)
        textViewGreeting=findViewById(R.id.title)

        loadTask()
        setGreetingText()

        taskAdapter=TaskAdapter(this,tasklist){position ->
            tasklist.removeAt(position)
            taskAdapter.notifyDataSetChanged()
            saveTask()
        }
        listViewTasks.adapter=taskAdapter

        buttonAddTask.setOnClickListener{
            val task=editTextTask.text.toString()
            if(task.isNotEmpty()){
                tasklist.add(task)
                taskAdapter.notifyDataSetChanged()
                editTextTask.text.clear()
                saveTask()
            }
        }
    }
    private fun saveTask(){
        val editor=sharedPreferences.edit()
        val gson=Gson()
        val json=gson.toJson(tasklist)
        editor.putString("tasklist",json)
        editor.apply()
    }
    private fun loadTask() {
        val gson = Gson()
        val json = sharedPreferences.getString("tasklist", null)
        val type = object : TypeToken<ArrayList<String>>() {}.type
        if (json != null) {
            val savedTaskList: ArrayList<String> = gson.fromJson(json, type)
            tasklist.addAll(savedTaskList)
        }
    }
    private fun setGreetingText() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)

        val greeting = when (hourOfDay) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            else -> "Good Evening"
        }
        textViewGreeting.text = greeting
    }

}

class TaskAdapter(context: Context,private val taskList: ArrayList<String>,private val onDeleteClick: (Int) -> Unit) :
    ArrayAdapter<String>(context, 0, taskList){

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
            val view=convertView ?: LayoutInflater.from(context).inflate(R.layout.task_list_dsgn,parent,false)
            val textViewTask=view.findViewById<TextView>(R.id.tasktextview)
            val buttonDelete=view.findViewById<Button>(R.id.Deletbtn)

            textViewTask.text= getItem(position)
            buttonDelete.setOnClickListener{
                onDeleteClick(position)
            }
            return view
        }
    }