package com.tomaz.focustimer.components.progressbar;

import com.tomaz.focustimer.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class ProgressWheel extends View {

	// Size
	private int layout_height = 0;
	private int layout_width = 0;
	private int fullRadius = 100;
	private int circleRadius = 80;
	private int barLength = 60;
	private int barWidth = 20;
	private int rimWidth = 20;
	private int textSize = 20;
	private float contourSize = 0;

	// Padding (with defaults)
	private int paddingTop = 5;
	private int paddingBottom = 5;
	private int paddingLeft = 5;
	private int paddingRight = 5;

	// Colors (with defaults)
	private int barColor = 0xAA000000;
	private int contourColor = 0xAA000000;
	private int circleColor = 0x00000000;
	private int rimColor = 0xAADDDDDD;
	private int textColor = 0xFF000000;

	// Paints
	private Paint barPaint = new Paint();
	private Paint circlePaint = new Paint();
	private Paint rimPaint = new Paint();
	private Paint textPaint = new Paint();
	private Paint contourPaint = new Paint();

	// Rectangles
	private RectF rectBounds = new RectF();
	private RectF circleBounds = new RectF();
	private RectF circleOuterContour = new RectF();
	private RectF circleInnerContour = new RectF();

	// Movement
	private int spinSpeed = 2; // pixel per move
	private int delayMs = 0; // milliseconds per move

	int progress = 0;
	boolean isSpinning = false;

	// Other
	private String text = "";
	private String[] splitText = {};

	/**
	 * 
	 * Default constructor
	 * 
	 * @param context
	 * @param attrs
	 */
	public ProgressWheel(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int size = 0;
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();

		int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
		int heightWithoutPadding = height - getPaddingBottom()
				- getPaddingTop();

		// use the smallest number
		size = (widthWithoutPadding > heightWithoutPadding) ? (heightWithoutPadding)
				: (widthWithoutPadding);

		// report measurement by calling setMeasuredDimension()
		setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size
				+ getPaddingTop() + getPaddingBottom());
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		layout_width = w;
		layout_height = h;

		setupBounds();
		setupPaints();
		invalidate();

	}

	/**
	 * setup Paints that paints the wheel
	 */
	private void setupPaints() {
		barPaint.setColor(barColor);
		barPaint.setAntiAlias(true);
		barPaint.setStyle(Style.STROKE);
		barPaint.setStrokeWidth(barWidth);

		rimPaint.setColor(rimColor);
		rimPaint.setAntiAlias(true);
		rimPaint.setStyle(Style.STROKE);
		rimPaint.setStrokeWidth(rimWidth);

		circlePaint.setColor(circleColor);
		circlePaint.setAntiAlias(true);
		circlePaint.setStyle(Style.FILL);

		textPaint.setColor(textColor);
		textPaint.setStyle(Style.FILL);
		textPaint.setAntiAlias(true);
		textPaint.setTextSize(textSize);

		contourPaint.setColor(contourColor);
		contourPaint.setAntiAlias(true);
		contourPaint.setStyle(Style.STROKE);
		contourPaint.setStrokeWidth(contourSize);
	}

	/**
	 * 
	 */
	private void setupBounds() {
		// Width should equal to Height, find the min value to steup the circle
		int minValue = Math.min(layout_width, layout_height);

		// Calc the Offset if needed
		int xOffset = layout_width - minValue;
		int yOffset = layout_height - minValue;

		// Add the offset
		paddingTop = this.getPaddingTop() + (yOffset / 2);
		paddingBottom = this.getPaddingBottom() + (yOffset / 2);
		paddingLeft = this.getPaddingLeft() + (xOffset / 2);
		paddingRight = this.getPaddingRight() + (xOffset / 2);

		int width = getWidth(); // this.getLayoutParams().width;
		int height = getHeight(); // this.getLayoutParams().height;

		rectBounds = new RectF(paddingLeft, paddingTop, width - paddingRight,
				height - paddingBottom);

		circleBounds = new RectF(paddingLeft + barWidth, paddingTop + barWidth,
				width - paddingRight - barWidth, height - paddingBottom
						- barWidth);
		circleInnerContour = new RectF(circleBounds.left + (rimWidth / 2.0f)
				+ (contourSize / 2.0f), circleBounds.top + (rimWidth / 2.0f)
				+ (contourSize / 2.0f), circleBounds.right - (rimWidth / 2.0f)
				- (contourSize / 2.0f), circleBounds.bottom - (rimWidth / 2.0f)
				- (contourSize / 2.0f));
		circleOuterContour = new RectF(circleBounds.left - (rimWidth / 2.0f)
				- (contourSize / 2.0f), circleBounds.top - (rimWidth / 2.0f)
				- (contourSize / 2.0f), circleBounds.right + (rimWidth / 2.0f)
				+ (contourSize / 2.0f), circleBounds.bottom + (rimWidth / 2.0f)
				+ (contourSize / 2.0f));

		fullRadius = (width - paddingRight - barWidth) / 2;
		circleRadius = (fullRadius - barWidth) + 1;
	}

	/**
	 * Parse the attributes passed to the view from the XML
	 * 
	 * @param a
	 *            the attributes to parse
	 */
	private void parseAttributes(TypedArray a) {
		barWidth = (int) a.getDimension(R.styleable.ProgressWheel_barWidth,
				barWidth);

		rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_rimWidth,
				rimWidth);

		spinSpeed = (int) a.getDimension(R.styleable.ProgressWheel_spinSpeed,
				spinSpeed);

		delayMs = a.getInteger(R.styleable.ProgressWheel_delayMillis, delayMs);
		if (delayMs < 0) {
			delayMs = 0;
		}

		barColor = a.getColor(R.styleable.ProgressWheel_barColor, barColor);

		barLength = (int) a.getDimension(R.styleable.ProgressWheel_barLength,
				barLength);

		textSize = (int) a.getDimension(R.styleable.ProgressWheel_textSize,
				textSize);

		textColor = (int) a.getColor(R.styleable.ProgressWheel_textColor,
				textColor);

		// if the text is empty , so ignore it
		if (a.hasValue(R.styleable.ProgressWheel_text)) {
			setText(a.getString(R.styleable.ProgressWheel_text));
		}

		rimColor = (int) a.getColor(R.styleable.ProgressWheel_rimColor,
				rimColor);

		circleColor = (int) a.getColor(R.styleable.ProgressWheel_circleColor,
				circleColor);

		contourColor = a.getColor(R.styleable.ProgressWheel_contourColor,
				contourColor);
		contourSize = a.getDimension(R.styleable.ProgressWheel_contourSize,
				contourSize);

		// Recycle
		a.recycle();
	}

	// ----------------------------------
	// Animation stuff
	// ----------------------------------

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw the inner circle
		canvas.drawArc(circleBounds, 360, 360, false, circlePaint);
		// Draw the rim
		canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
		canvas.drawArc(circleOuterContour, 360, 360, false, contourPaint);
		canvas.drawArc(circleInnerContour, 360, 360, false, contourPaint);
		// Draw the bar
		if (isSpinning) {
			canvas.drawArc(circleBounds, progress - 90, barLength, false,
					barPaint);
		} else {
			canvas.drawArc(circleBounds, -90, progress, false, barPaint);
		}
		// Draw the text (attempts to center it horizontally and vertically)
		float textHeight = textPaint.descent() - textPaint.ascent();
		float verticalTextOffset = (textHeight / 2) - textPaint.descent();

		for (String s : splitText) {
			float horizontalTextOffset = textPaint.measureText(s) / 2;
			canvas.drawText(s, this.getWidth() / 2 - horizontalTextOffset,
					this.getHeight() / 2 + verticalTextOffset, textPaint);
		}
		if (isSpinning) {
			scheduleRedraw();
		}
	}

	private void scheduleRedraw() {
		progress += spinSpeed;
		if (progress > 360) {
			progress = 0;
		}
		postInvalidateDelayed(delayMs);
	}

	/**
	 * Check if the wheel is currently spinning
	 */

	public boolean isSpinning() {
		if (isSpinning) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reset the count (in increment mode)
	 */
	public void resetCount() {
		progress = 0;
		setText("0%");
		invalidate();
	}

	/**
	 * Turn off spin mode
	 */
	public void stopSpinning() {
		isSpinning = false;
		progress = 0;
		postInvalidate();
	}

	/**
	 * Pause spin
	 */

	public void pauseSpinning() {
		isSpinning = false;
		postInvalidate();
	}

	/**
	 * Puts the view on spin mode
	 */
	public void spin() {
		isSpinning = true;
		postInvalidate();
	}

	/**
	 * Increment the progress by 1 (of 360)
	 */
	public void incrementProgress() {
		isSpinning = false;
		progress++;
		if (progress > 360)
			progress = 0;
		// setText(Math.round(((float) progress / 360) * 100) + "%");
		postInvalidate();
	}

	/**
	 * Increment the progress by n (of 360)
	 */
	public void incrementProgress(int n) {
		isSpinning = false;
		progress += n;
		if (progress > 360)
			progress = 0;
		// setText(Math.round(((float) progress / 360) * 100) + "%");
		postInvalidate();
	}

	/**
	 * Set the progress to a specific value
	 */
	public void setProgress(int i) {
		isSpinning = false;
		progress = i;
		postInvalidate();
	}

	// ----------------------------------
	// Getters + setters
	// ----------------------------------

	/**
	 * Set the text in the progress bar Doesn't invalidate the view
	 * 
	 * @param text
	 *            the text to show ('\n' constitutes a new line)
	 */
	public void setText(String text) {
		this.text = text;
		splitText = this.text.split("\n");
	}

	public int getCircleRadius() {
		return circleRadius;
	}

	public void setCircleRadius(int circleRadius) {
		this.circleRadius = circleRadius;
	}

	public int getBarLength() {
		return barLength;
	}

	public void setBarLength(int barLength) {
		this.barLength = barLength;
	}

	public int getBarWidth() {
		return barWidth;
	}

	public void setBarWidth(int barWidth) {
		this.barWidth = barWidth;

		if (this.barPaint != null) {
			this.barPaint.setStrokeWidth(this.barWidth);
		}
	}

	public int getTextSize() {
		return textSize;
	}

	public void setTextSize(int textSize) {
		this.textSize = textSize;

		if (this.textPaint != null) {
			this.textPaint.setTextSize(this.textSize);
		}
	}

	public int getPaddingTop() {
		return paddingTop;
	}

	public void setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
	}

	public int getPaddingBottom() {
		return paddingBottom;
	}

	public void setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
	}

	public int getPaddingLeft() {
		return paddingLeft;
	}

	public void setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
	}

	public int getPaddingRight() {
		return paddingRight;
	}

	public void setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
	}

	public int getBarColor() {
		return barColor;
	}

	public void setBarColor(int barColor) {
		this.barColor = barColor;

		if (this.barPaint != null) {
			this.barPaint.setColor(this.barColor);
		}
	}

	public int getCircleColor() {
		return circleColor;
	}

	public void setCircleColor(int circleColor) {
		this.circleColor = circleColor;

		if (this.circlePaint != null) {
			this.circlePaint.setColor(this.circleColor);
		}
	}

	public int getRimColor() {
		return rimColor;
	}

	public void setRimColor(int rimColor) {
		this.rimColor = rimColor;

		if (this.rimPaint != null) {
			this.rimPaint.setColor(this.rimColor);
		}
	}

	public Shader getRimShader() {
		return rimPaint.getShader();
	}

	public void setRimShader(Shader shader) {
		this.rimPaint.setShader(shader);
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;

		if (this.textPaint != null) {
			this.textPaint.setColor(this.textColor);
		}
	}

	public int getSpinSpeed() {
		return spinSpeed;
	}

	public void setSpinSpeed(int spinSpeed) {
		this.spinSpeed = spinSpeed;
	}

	public int getRimWidth() {
		return rimWidth;
	}

	public void setRimWidth(int rimWidth) {
		this.rimWidth = rimWidth;

		if (this.rimPaint != null) {
			this.rimPaint.setStrokeWidth(this.rimWidth);
		}
	}

	public int getDelayMs() {
		return delayMs;
	}

	public void setDelayMs(int delayMs) {
		this.delayMs = delayMs;
	}

	public int getContourColor() {
		return contourColor;
	}

	public void setContourColor(int contourColor) {
		this.contourColor = contourColor;

		if (contourPaint != null) {
			this.contourPaint.setColor(this.contourColor);
		}
	}

	public float getContourSize() {
		return this.contourSize;
	}

	public void setContourSize(float contourSize) {
		this.contourSize = contourSize;

		if (contourPaint != null) {
			this.contourPaint.setStrokeWidth(this.contourSize);
		}
	}
}