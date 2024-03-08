package com.example.notifydatasetchanged

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyRecyclerViewAdapter(val clickListener: (Menu) -> Unit) :
    RecyclerView.Adapter<MyViewHolder>() {

    var menus = ArrayList<Menu>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val listItem = layoutInflater.inflate(R.layout.list_item, parent, false)
        return MyViewHolder(listItem)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
/*
        // 나쁘지 않지만 객체지향적이지는 않은 코드
        val menu = menus[position]
        holder.textItem.text = "${menu.name}\n${menu.price}₩"
*/
        // 객체지향적인 코드
        holder.bind(menus[position], clickListener)
    }
}

class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val textItem: TextView = view.findViewById(R.id.textItem)

    fun bind(menu: Menu, clickListener: (Menu) -> Unit) {
        textItem.text = "${menu.name}\n${menu.price}₩"

        view.setOnClickListener {
            clickListener(menu)
        }
    }
}