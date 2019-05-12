package com.madebyaron.betterhealth.view.list

import android.content.ClipData
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.madebyaron.betterhealth.R
import com.madebyaron.betterhealth.data.Data

class ListAdapter(private val activity: FragmentActivity) : RecyclerView.Adapter<ListItemHolder>() {

	var dataList = listOf<Data>()
		set (value) {
			field = value
			notifyDataSetChanged()
		}

	private val touchHelper = ItemTouchHelper(
			object : ItemTouchHelper.SimpleCallback(0,
					ItemTouchHelper.START or ItemTouchHelper.END) {

				override fun onMove(
						recyclerView: RecyclerView,
						viewHolder: RecyclerView.ViewHolder,
						target: RecyclerView.ViewHolder) = false

				override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
					if (viewHolder is ListItemHolder) viewHolder.remove()
				}
			}
	)

	fun attachToRecyclerView(recyclerView: RecyclerView) {
		recyclerView.adapter = this
		touchHelper.attachToRecyclerView(recyclerView)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemHolder {
		val inflater = LayoutInflater.from(parent.context)
		val view = inflater.inflate(R.layout.item_list, parent, false)
		return ListItemHolder(activity, view)
	}

	override fun getItemCount(): Int {
		return dataList.size
	}

	override fun onBindViewHolder(holder: ListItemHolder, position: Int) {
		holder.bind(dataList[position])
	}
}