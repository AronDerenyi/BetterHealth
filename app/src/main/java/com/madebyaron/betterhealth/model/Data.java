package com.madebyaron.betterhealth.model

import java.util.*

data class Data(val id: Int, val type: Int, val date: Date, val amount: Float) {

	companion object {
		const val TYPE_WEIGHT = 0
		const val TYPE_CALORIES = 1
		const val TYPE_DRINK = 2
		const val TYPE_CIGARETTES = 3
	}
}