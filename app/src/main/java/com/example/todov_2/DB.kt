package com.example.todov_2


import android.graphics.Paint.STRIKE_THRU_TEXT_FLAG

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.row_items.view.*




data class ToDoItem( var done: Boolean = false, var itemText: String="")


class TodoAdapter(private val todos: MutableList<ToDoItem>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_items,
                parent,
                false
            )
        )
    }

    fun saveTodo(){
        val mdatabase = Firebase.database("https://todov2grisajevs-default-rtdb.europe-west1.firebasedatabase.app/").getReference("List")
        val newMap = mutableMapOf<String,ToDoItem>()
        todos.associateByTo(newMap) {it.itemText}

        mdatabase.removeValue()
        mdatabase.updateChildren(newMap as Map<String, Any>)



    }

    fun addTodo(todo: ToDoItem) {
        todos.add(todo)
        notifyItemInserted(todos.size - 1)
    }



    fun deleteDoneTodos() {
        val mdatabase = Firebase.database("https://todov2grisajevs-default-rtdb.europe-west1.firebasedatabase.app/").getReference("List")


        todos.removeAll { todo ->
            todo.done
        }

        val newMap = mutableMapOf<String,ToDoItem>()
        todos.associateByTo(newMap) {it.itemText}

        mdatabase.removeValue()
        mdatabase.updateChildren(newMap as Map<String, Any>)

        notifyDataSetChanged()
    }

    private fun toggleStrikeThrough(tv_item_text: TextView, done: Boolean) {
        if (done) {
            tv_item_text.paintFlags = tv_item_text.paintFlags or STRIKE_THRU_TEXT_FLAG
        } else {
            tv_item_text.paintFlags = tv_item_text.paintFlags and STRIKE_THRU_TEXT_FLAG.inv()
        }
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        var curTodo = todos[position]
        holder.itemView.apply {
            tv_item_text.text = curTodo.itemText
            cb_item_is_done.isChecked = curTodo.done
            toggleStrikeThrough(tv_item_text, curTodo.done)
            cb_item_is_done.setOnCheckedChangeListener { _, isChecked ->
                toggleStrikeThrough(tv_item_text, isChecked)
                curTodo.done = !curTodo.done
                }
            }
    }

    override fun getItemCount(): Int {
        return todos.size
    }
}
