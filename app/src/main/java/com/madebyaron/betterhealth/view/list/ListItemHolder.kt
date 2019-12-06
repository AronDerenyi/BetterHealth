package com.madebyaron.betterhealth.view.list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.model.Data
import com.madebyaron.betterhealth.viewmodel.ListModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class ListItemHolder(private val activity: FragmentActivity, view: View) : RecyclerView.ViewHolder(view) {

	private val model = ViewModelProviders.of(activity).get(ListModel::class.java)

	private val iconWeight: ImageView = view.findViewById(R.id.list_item_icon_weight)
	private val iconCalories: ImageView = view.findViewById(R.id.list_item_icon_calories)
	private val iconDrink: ImageView = view.findViewById(R.id.list_item_icon_drink)
	private val iconCigarettes: ImageView = view.findViewById(R.id.list_item_icon_cigarettes)

	private val amount: TextView = view.findViewById(R.id.list_item_amount)
	private val unit: TextView = view.findViewById(R.id.list_item_unit)
	private val date: TextView = view.findViewById(R.id.list_item_date)

	private lateinit var data: Data

	fun bind(data: Data) {
		this.data = data

		iconWeight.visibility = if (data.type == Data.TYPE_WEIGHT) View.VISIBLE else View.GONE
		iconCalories.visibility = if (data.type == Data.TYPE_CALORIES) View.VISIBLE else View.GONE
		iconDrink.visibility = if (data.type == Data.TYPE_DRINK) View.VISIBLE else View.GONE
		iconCigarettes.visibility = if (data.type == Data.TYPE_CIGARETTES) View.VISIBLE else View.GONE

		amount.text = DecimalFormat("0.##").format(data.amount)

		unit.text = when (data.type) {
			Data.TYPE_WEIGHT -> activity.getString(R.string.unit_kilograms)
			Data.TYPE_CALORIES -> activity.getString(R.string.unit_calories)
			Data.TYPE_DRINK -> activity.getString(R.string.unit_units)
			Data.TYPE_CIGARETTES -> activity.getString(R.string.unit_cigarettes)
			else -> "Something :P"
		}

		date.text = SimpleDateFormat(
				"EEE, MMM dd, yyyy   hh:mm a",
				Locale.getDefault()
		).format(data.date)
	}

	fun remove() {
		model.deleteData(data)
	}
}