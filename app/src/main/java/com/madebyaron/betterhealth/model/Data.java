package com.madebyaron.betterhealth.model;

import java.util.*;

public class Data {

	public static final int TYPE_WEIGHT = 0;
	public static final int TYPE_CALORIES = 1;
	public static final int TYPE_DRINK = 2;
	public static final int TYPE_CIGARETTES = 3;

	private final int id;
	private final int type;
	private final Date date;
	private final float amount;

	public Data(int id, int type, Date date, float amount) {
		this.id = id;
		this.type = type;
		this.date = date;
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	public int getType() {
		return type;
	}

	public Date getDate() {
		return date;
	}

	public float getAmount() {
		return amount;
	}
}