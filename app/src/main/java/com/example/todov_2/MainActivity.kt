package com.example.todov_2

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todov_2.databinding.ActivityMainBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

// BY Marks Grišajevs 191RDB191 2.grupa
val database = Firebase.database("https://todov2grisajevs-default-rtdb.europe-west1.firebasedatabase.app/")
class MainActivity : AppCompatActivity() {
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        todoAdapter = TodoAdapter(mutableListOf())
        recyclerView.adapter = todoAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        val mdatabase = Firebase.database("https://todov2grisajevs-default-rtdb.europe-west1.firebasedatabase.app/").getReference("List")
        mdatabase.keepSynced(true)


        /*fun addDataToList(dataSnapshot: DataSnapshot) {
            val todo_item= dataSnapshot.getValue(ToDoItem::class.java)
            println(todo_item?.done)
            println(todo_item?.itemText)

            //Check if current database contains any collection

            //alert adapter that has changed
            todoAdapter.notifyDataSetChanged()
        }*/
        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for (childSnapshot:DataSnapshot in dataSnapshot.children){
                    val map = childSnapshot.value as HashMap<String,Any>
                    var todoItem=ToDoItem()
                    todoItem.done = map["done"] as Boolean
                    todoItem.itemText = map["itemText"] as String
                    todoAdapter.addTodo(todoItem)
                    todoAdapter.notifyDataSetChanged()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message
                Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
            }
        }

        mdatabase.orderByKey().addListenerForSingleValueEvent(itemListener)



        fun addNewItemDialog() {
            val alert = AlertDialog.Builder(this)
            val itemEditText = EditText(this)
            alert.setMessage("Add New Item")
            alert.setTitle("Enter To Do Item Text")
            alert.setView(itemEditText)
            alert.setPositiveButton("Submit") { _, _ ->
                val toDo = ToDoItem(false,itemEditText.text.toString())
                todoAdapter.addTodo(toDo)
                //We first make a push so that a new item is made with a unique ID
               mdatabase.push().setValue(toDo)
                //then, we used the reference to set the value on that ID

            }
            alert.show()
        }


        val deleteButton = findViewById<Button>(R.id.button2)
        deleteButton.setOnClickListener{
            todoAdapter.deleteDoneTodos()
            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()
        }

        val saveButton= findViewById<Button>(R.id.button)
        saveButton.setOnClickListener{
            todoAdapter.saveTodo()
            Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show()

        }


        binding.fab.setOnClickListener {
            addNewItemDialog()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}