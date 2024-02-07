package com.example.argumenttoadapter

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    val burgers = listOf<Menu>(
        Menu("Classic Cheeseburger", 6800),
        Menu("Bacon Deluxe Burger", 7800),
        Menu("BBQ Ranch Burger", 5800),
        Menu("Mushroom Swiss Burger", 6700),
        Menu("Double Stack Burger", 9800),
        Menu("Spicy Chicken Burger", 7900),
        Menu("Crispy Chicken Burger", 6800),
        Menu("Grilled Chicken Club", 9700),
        Menu("Veggie Burger Deluxe", 6800),
        Menu("Hawaiian Teriyaki Burger", 8900),
        Menu("Philly Cheese Steak Burger", 6200),
        Menu("Western BBQ Burger", 7300),
        Menu("Jalapeno Pepper Jack Burger", 7900),
        Menu("Turkey Avocado Burger", 6700),
        Menu("Black Bean Burger", 7600),
        Menu("Bacon Jalapeno Burger", 6700),
        Menu("Ultimate BBQ Bacon Burger", 6700),
        Menu("Chicken Bacon Ranch Burger", 6700),
        Menu("Chipotle Guacamole Burger", 6700),
        Menu("Breakfast Burger", 4700),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyRecyclerViewAdapter(burgers) { menu: Menu -> itemClicked(menu) }
    }

    private fun itemClicked(menu: Menu) {
        Toast.makeText(
            this, "Selected Menu is ${menu.name}", Toast.LENGTH_LONG
        ).show()
    }
}