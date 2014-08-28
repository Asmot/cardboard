package com.example.cardboarddemo;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.EyeTransform;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;

public class TestAcitiity extends CardboardActivity implements CardboardView.StereoRenderer{
	
    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;

    private static final float YAW_LIMIT = 0.12f;
    private static final float PITCH_LIMIT = 0.12f;
	
	private final static String TAG="TestAcitiity";
	
	
	private Vibrator  mVibrator;
	
	 private Square   mSquare;
	 
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    
    private final float[] mRotationMatrix=new float[16];
    private int mAngle;
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		setContentView(R.layout.common_ui);
	    CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
	    // Associate a CardboardView.StereoRenderer with cardboardView.
	    cardboardView.setRenderer(this);
	    
	    // Associate the cardboardView with this activity. 
	    setCardboardView(cardboardView);

	    mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        
       
	}

	
	
	@Override
	public void onDrawEye(EyeTransform transform) {
		// TODO Auto-generated method stub

      Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
//
//      mSquare.draw(mMVPMatrix);
      
    // Create a rotation for the triangle  
    // long time = SystemClock.uptimeMillis() % 4000L;  
    // float angle = 0.090f * ((int) time);  
    Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);  
  
    // 合并旋转矩阵到投影和相机视口矩阵  
    Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);  
  
    // 画一个角度  
    mSquare.draw(mMVPMatrix);

	}

	@Override
	public void onFinishFrame(Viewport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		// TODO Auto-generated method stub
		 // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 1, 0,-3, CAMERA_Z, 0f, 0f, 0f, 1.0f, 0.0f);

////        // Calculate the projection and view transformation
//        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
////
////        
////        // Draw square
//        mSquare.draw(mMVPMatrix);
        
//       // Create a rotation for the triangle  
//        // long time = SystemClock.uptimeMillis() % 4000L;  
//        // float angle = 0.090f * ((int) time);  
//        Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);  
//      
//        // 合并旋转矩阵到投影和相机视口矩阵  
//        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);  
//      
//        // 画一个角度  
//        mSquare.draw(mMVPMatrix);
	}

	@Override
	public void onRendererShutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		// TODO Auto-generated method stub
		// Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}
	
	

	@Override
	public void onSurfaceCreated(EGLConfig gl) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "onSurfaceCreated");
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        mSquare   = new Square();
	}
	
	
    @Override
    public void onCardboardTrigger() {
        Log.i(TAG, "onCardboardTrigger");
    }
    
    
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:

                float dx = x - mPreviousX;
                float dy = y - mPreviousY;
                
                // reverse direction of rotation above the mid-line  
                if (y > 1000 / 2) {  
                  dx = dx * -1 ;  
                }  
      
                // reverse direction of rotation to left of the mid-line  
                if (x < 2000 / 2) {  
                  dy = dy * -1 ;  
                }  
      
                mAngle += (dx + dy) * (180.0f / 320);  // = 180.0f / 320  
                
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
}

