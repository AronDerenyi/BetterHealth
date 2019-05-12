package com.madebyaron.betterhealth.view.components

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class GraphView : View {

	var offsetX = 0f
		set(value) {
			field = value
			invalidate()
		}

	var offsetY = 0f
		set(value) {
			field = value
			invalidate()
		}

	var zoomX = 1f
		set(value) {
			field = value
			invalidate()
		}

	var zoomY = 1f
		set(value) {
			field = value
			invalidate()
		}

	private var points: List<Point> = listOf()

	fun setPoints(points: List<Point>) {
		this.points = ArrayList(points)
	}

	private var xLabels: List<Label> = listOf()

	fun setXLabels(xLabels: List<Label>) {
		this.xLabels = ArrayList(xLabels)
	}

	private var yLabels: List<Label> = listOf()

	fun setYLabels(yLabels: List<Label>) {
		this.yLabels = ArrayList(yLabels)
	}

	private val linePaint = Paint()

	var lineColor: Int
		get() {
			return linePaint.color
		}
		set(value) {
			linePaint.color = value
			invalidate()
		}

	var lineWidth: Float
		get() {
			return linePaint.strokeWidth
		}
		set(value) {
			linePaint.strokeWidth = value
			invalidate()
		}

	private val axisPaint = Paint()

	var axisColor: Int
		get() {
			return axisPaint.color
		}
		set(value) {
			axisPaint.color = value
			invalidate()
		}

	var axisWidth: Float
		get() {
			return axisPaint.strokeWidth
		}
		set(value) {
			axisPaint.strokeWidth = value
			invalidate()
		}

	private val gridPaint = Paint()

	var gridColor: Int
		get() {
			return gridPaint.color
		}
		set(value) {
			gridPaint.color = value
			invalidate()
		}

	var gridWidth: Float
		get() {
			return gridPaint.strokeWidth
		}
		set(value) {
			gridPaint.strokeWidth = value
			invalidate()
		}

	private val textPaint = Paint()

	var textColor: Int
		get() {
			return textPaint.color
		}
		set(value) {
			textPaint.color = value
			invalidate()
		}

	var textSize: Float
		get() {
			return textPaint.textSize
		}
		set(value) {
			textPaint.textSize = value
			invalidate()
		}

	var textFont: Typeface
		get() {
			return textPaint.typeface
		}
		set(value) {
			textPaint.typeface = value
			invalidate()
		}

	var textPadding: Float = 0f
		set(value) {
			field = value
			invalidate()
		}

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	override fun onDraw(canvas: Canvas) {
		var maxXLabelHeight = 0
		for (label in xLabels) {
			val pos = (label.pos - offsetX) / zoomX
			if (pos in 0.0..1.0) {
				val height = textPaint.getTextHeight(label.text)
				if (height > maxXLabelHeight) maxXLabelHeight = height
			}
		}

		var maxYLabelWidth = 0
		for (label in yLabels) {
			val pos = (label.pos - offsetY) / zoomY
			if (pos in 0.0..1.0) {
				val width = textPaint.getTextWidth(label.text)
				if (width > maxYLabelWidth) maxYLabelWidth = width
			}
		}

		val graphXOffset = maxYLabelWidth
		val graphYOffset = maxXLabelHeight
		val graphWidth = width - paddingLeft - paddingRight - graphXOffset - textPadding
		val graphHeight = height - paddingTop - paddingBottom - graphYOffset - textPadding

		for (label in xLabels) {
			val pos = (label.pos - offsetX) / zoomX
			if (pos in 0.0..1.0) {
				val x = graphWidth * pos + (paddingLeft + graphXOffset + textPadding)
				val y = (paddingBottom + graphYOffset).toFloat()
				canvas.drawText(label.text, 0.5f, 0.0f, x, height - y, textPaint)
				canvas.drawLine(
						x, paddingTop.toFloat(),
						x, height - (paddingBottom + graphYOffset + textPadding),
						gridPaint
				)
			}
		}

		for (label in yLabels) {
			val pos = (label.pos - offsetY) / zoomY
			if (pos in 0.0..1.0) {
				val x = (paddingLeft + graphXOffset).toFloat()
				val y = graphHeight * pos + (paddingBottom + graphYOffset + textPadding)
				canvas.drawText(label.text, 1.0f, 0.5f, x, height - y, textPaint)
				canvas.drawLine(
						paddingLeft + graphXOffset + textPadding, height - y,
						width - paddingRight.toFloat(), height - y,
						gridPaint
				)
			}
		}

		canvas.save()
		canvas.clipRect(
				(paddingLeft + graphXOffset + textPadding).toInt(),
				paddingTop,
				width - paddingRight,
				(height - (paddingBottom + graphYOffset + textPadding)).toInt()
		)

		val linePoints = mutableListOf<Float>()
		for (point in points) {
			if (linePoints.size > 2) {
				val prevScreenX = linePoints[linePoints.size - 2]
				val prevScreenY = linePoints[linePoints.size - 1]
				linePoints.add(prevScreenX)
				linePoints.add(prevScreenY)
			}

			val x = (point.x - offsetX) / zoomX
			val y = (point.y - offsetY) / zoomY
			linePoints.add(graphWidth * x + paddingLeft + graphXOffset + textPadding)
			linePoints.add(height - (graphHeight * y + paddingBottom + graphYOffset + textPadding))
		}

		canvas.drawLines(linePoints.toFloatArray(), linePaint)
		canvas.restore()

		canvas.drawLine(
				paddingLeft + graphXOffset + textPadding,
				height - (paddingBottom + graphYOffset + textPadding),
				width - paddingRight.toFloat(),
				height - (paddingBottom + graphYOffset + textPadding),
				axisPaint
		)

		canvas.drawLine(
				paddingLeft + graphXOffset + textPadding,
				height - (paddingBottom + graphYOffset + textPadding),
				paddingLeft + graphXOffset + textPadding,
				paddingTop.toFloat(),
				axisPaint
		)
	}

	data class Point(val x: Float = 0f, val y: Float = 0f)
	data class Label(val pos: Float = 0f, val text: String = "")

	// Extensions

	private val bounds = Rect()

	private fun Paint.getTextWidth(text: String): Int {
		this.getTextBounds(text, 0, text.length, bounds)
		return bounds.width()
	}

	private fun Paint.getTextHeight(text: String): Int {
		this.getTextBounds(text, 0, text.length, bounds)
		return bounds.height()
	}

	private fun Canvas.drawText(text: String, pivotX: Float, pivotY: Float, x: Float, y: Float, paint: Paint) {
		paint.getTextBounds(text, 0, text.length, bounds)
		val width = bounds.width()
		val height = bounds.height()

		this.drawText(text, x - width * pivotX, y + height * (1 - pivotY), paint)
	}
}