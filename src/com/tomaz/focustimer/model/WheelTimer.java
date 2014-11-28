package com.tomaz.focustimer.model;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class WheelTimer extends View {

	
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
    
    //Padding (with defaults)
    private int paddingTop = 5;
    private int paddingBottom = 5;
    private int paddingLeft = 5;
    private int paddingRight = 5;
    

    //Colors (with defaults)
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
    private int spinSpeed = 2; 		// pixel per move
    private int delayMs = 0; 	// milliseconds per move
    
    int progress = 0;
    boolean isSpinning = false;
    
    
	
	
	/**
	 * 
	 * Default constructor
	 * 
	 * @param context
	 * @param attrs
	 */
	public WheelTimer(Context context, AttributeSet attrs) {
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
		int heightWithoutPadding = height - getPaddingBottom() - getPaddingTop();
		

		// use the smallest number
		size = (widthWithoutPadding>heightWithoutPadding)?(heightWithoutPadding):(widthWithoutPadding);
		
		// report measurement by calling setMeasuredDimension()
		setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
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
		// TODO Auto-generated method stub
		
	}

	
	

}
