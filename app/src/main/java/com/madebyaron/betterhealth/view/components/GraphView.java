package com.madebyaron.betterhealth.view.components;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GraphView extends View {

	private float offsetX = 0f;
	private float offsetY = 0f;
	private float zoomX = 1f;
	private float zoomY = 1f;

	private List<Point> points = new ArrayList<>();
	private List<Label> xLabels = new ArrayList<>();
	private List<Label> yLabels = new ArrayList<>();

	private Paint linePaint = new Paint();
	private Paint axisPaint = new Paint();
	private Paint gridPaint = new Paint();
	private Paint textPaint = new Paint();
	private float textPadding = 0;

	public GraphView(Context context) {
		super(context);
	}

	public GraphView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// Offset

	public float getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(float offsetX) {
		this.offsetX = offsetX;
		invalidate();
	}

	public float getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(float offsetY) {
		this.offsetY = offsetY;
		invalidate();
	}

	// Zoom

	public float getZoomX() {
		return zoomX;
	}

	public void setZoomX(float zoomX) {
		this.zoomX = zoomX;
		invalidate();
	}

	public float getZoomY() {
		return zoomY;
	}

	public void setZoomY(float zoomY) {
		this.zoomY = zoomY;
		invalidate();
	}

	// Points and labels

	public void setPoints(List<Point> points) {
		this.points = new ArrayList<>(points);
	}

	public void setXLabels(List<Label> xLabels) {
		this.xLabels = new ArrayList<>(xLabels);
	}

	public void setYLabels(List<Label> yLabels) {
		this.yLabels = new ArrayList<>(yLabels);
	}

	// Line style

	public int getLineColor() {
		return linePaint.getColor();
	}

	public void setLineColor(int color) {
		linePaint.setColor(color);
		invalidate();
	}

	public float getLineWidth() {
		return linePaint.getStrokeWidth();
	}

	public void setLineWidth(float width) {
		linePaint.setStrokeWidth(width);
		invalidate();
	}

	// Axis style

	public int getAxisColor() {
		return axisPaint.getColor();
	}

	public void setAxisColor(int color) {
		axisPaint.setColor(color);
		invalidate();
	}

	public float getAxisWidth() {
		return axisPaint.getStrokeWidth();
	}

	public void setAxisWidth(float width) {
		axisPaint.setStrokeWidth(width);
		invalidate();
	}

	// Grid style

	public int getGridColor() {
		return gridPaint.getColor();
	}

	public void setGridColor(int color) {
		gridPaint.setColor(color);
		invalidate();
	}

	public float getGridWidth() {
		return gridPaint.getStrokeWidth();
	}

	public void setGridWidth(float width) {
		gridPaint.setStrokeWidth(width);
		invalidate();
	}

	// Text style

	public int getTextColor() {
		return textPaint.getColor();
	}

	public void setTextColor(int color) {
		textPaint.setColor(color);
		invalidate();
	}

	public float getTextSize() {
		return textPaint.getTextSize();
	}

	public void setTextSize(float size) {
		textPaint.setTextSize(size);
		invalidate();
	}

	public Typeface getTextFont() {
		return textPaint.getTypeface();
	}

	public void setTextFont(Typeface font) {
		textPaint.setTypeface(font);
		invalidate();
	}

	public float getTextPadding() {
		return textPadding;
	}

	public void setTextPadding(float padding) {
		textPadding = padding;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float maxXLabelHeight = 0;
		for (Label label : xLabels) {
			float pos = (label.pos - offsetX) / zoomX;
			if (0 <= pos && pos <= 1.0) {
				float height = getTextHeight(textPaint, label.text);
				if (height > maxXLabelHeight) maxXLabelHeight = height;
			}
		}

		float maxYLabelWidth = 0;
		for (Label label : yLabels) {
			float pos = (label.pos - offsetY) / zoomY;
			if (0 <= pos && pos <= 1.0) {
				float width = getTextWidth(textPaint, label.text);
				if (width > maxYLabelWidth) maxYLabelWidth = width;
			}
		}

		float graphXOffset = maxYLabelWidth;
		float graphYOffset = maxXLabelHeight;

		float graphWidth = getWidth() -
				getPaddingLeft() -
				getPaddingRight() -
				graphXOffset -
				textPadding;

		float graphHeight = getHeight() -
				getPaddingTop() -
				getPaddingBottom() -
				graphYOffset -
				textPadding;

		for (Label label : xLabels) {
			float pos = (label.pos - offsetX) / zoomX;
			if (0 <= pos && pos <= 1.0) {
				float x = graphWidth * pos + (getPaddingLeft() + graphXOffset + textPadding);
				float y = getPaddingBottom() + graphYOffset;
				drawText(canvas, label.text, 0.5f, 0.0f, x, getHeight() - y, textPaint);
				canvas.drawLine(
						x, getPaddingTop(),
						x, getHeight() - (getPaddingBottom() + graphYOffset + textPadding),
						gridPaint
				);
			}
		}

		for (Label label : yLabels) {
			float pos = (label.pos - offsetY) / zoomY;
			if (0 <= pos && pos <= 1.0) {
				float x = getPaddingLeft() + graphXOffset;
				float y = graphHeight * pos + (getPaddingBottom() + graphYOffset + textPadding);
				drawText(canvas, label.text, 1.0f, 0.5f, x, getHeight() - y, textPaint);
				canvas.drawLine(
						getPaddingLeft() + graphXOffset + textPadding, getHeight() - y,
						getWidth() - getPaddingRight(), getHeight() - y,
						gridPaint
				);
			}
		}

		canvas.save();
		canvas.clipRect(
				getPaddingLeft() + graphXOffset + textPadding,
				getPaddingTop(),
				getWidth() - getPaddingRight(),
				getHeight() - (getPaddingBottom() + graphYOffset + textPadding)
		);

		List<Float> linePoints = new ArrayList<>();
		for (Point point : points) {
			if (linePoints.size() > 2) {
				float prevScreenX = linePoints.get(linePoints.size() - 2);
				float prevScreenY = linePoints.get(linePoints.size() - 1);
				linePoints.add(prevScreenX);
				linePoints.add(prevScreenY);
			}

			float x = (point.x - offsetX) / zoomX;
			float y = (point.y - offsetY) / zoomY;
			linePoints.add(graphWidth * x + getPaddingLeft() + graphXOffset + textPadding);
			linePoints.add(getHeight() - (graphHeight * y + getPaddingBottom() + graphYOffset + textPadding));
		}

		float[] lineArray = new float[linePoints.size()];
		for (int i = 0; i < linePoints.size(); i++) lineArray[i] = linePoints.get(i);
		canvas.drawLines(lineArray, linePaint);
		canvas.restore();

		canvas.drawLine(
				getPaddingLeft() + graphXOffset + textPadding,
				getHeight() - (getPaddingBottom() + graphYOffset + textPadding),
				getWidth() - getPaddingRight(),
				getHeight() - (getPaddingBottom() + graphYOffset + textPadding),
				axisPaint
		);

		canvas.drawLine(
				getPaddingLeft() + graphXOffset + textPadding,
				getHeight() - (getPaddingBottom() + graphYOffset + textPadding),
				getPaddingLeft() + graphXOffset + textPadding,
				getPaddingTop(),
				axisPaint
		);
	}

	public static class Point {

		private final float x;
		private final float y;

		public Point(float x, float y) {
			this.x = x;
			this.y = y;
		}

		public Point() {
			this(0, 0);
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}
	}

	public static class Label {

		private final float pos;
		private final String text;

		public Label(float pos, String text) {
			this.pos = pos;
			this.text = text;
		}

		public Label() {
			this(0, "");
		}

		public float getPos() {
			return pos;
		}

		public String getText() {
			return text;
		}
	}

	// Extensions

	private static Rect bounds = new Rect();

	private static int getTextWidth(Paint paint, String text) {
		paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.width();
	}

	private static int getTextHeight(Paint paint, String text) {
		paint.getTextBounds(text, 0, text.length(), bounds);
		return bounds.height();
	}

	private static void drawText(
			Canvas canvas,
			String text,
			float pivotX, float pivotY,
			float x, float y,
			Paint paint
	) {
		paint.getTextBounds(text, 0, text.length(), bounds);
		float width = bounds.width();
		float height = bounds.height();

		canvas.drawText(text, x - width * pivotX, y + height * (1 - pivotY), paint);
	}
}