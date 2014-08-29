package com.example.cardboarddemo;

import javax.microedition.khronos.egl.EGLConfig;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;

import com.example.cardboarddemo.model.Sprite;
import com.example.cardboarddemo.model.Square;
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
	
    // We keep the light always position just above the user.
    private final float[] mLightPosInWorldSpace = new float[] {0.0f, 2.0f, 0.0f, 1.0f};
    private final float[] mLightPosInEyeSpace = new float[4];
    
	private final static String TAG="TestAcitiity";
	
	
	private Vibrator  mVibrator;
	
	private Square   mSquare;
	private Sprite sprite;
	 
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mCamera = new float[16];
    
    private float[] mView= new float[16];
    
    
    private int mLightPosParam;
    private int mGlProgram;
    
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

	public static float test=0;
	
	@Override
	public void onDrawEye(EyeTransform transform) {
		// TODO Auto-generated method stub
		
		
//		Matrix.setLookAtM(mCamera, 0, test, 0,-3, CAMERA_Z, 0f, 0f, 0f, 1.0f, 0.0f);
		
		// Apply the eye transformation to the camera.
        Matrix.multiplyMM(mView, 0, transform.getEyeView(), 0, mCamera, 0);


        // Set the position of the light
//        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mView, 0, mLightPosInWorldSpace, 0);
//        GLES20.glUniform3f(mLightPosParam, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1],
//                mLightPosInEyeSpace[2]);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        Matrix.multiplyMM(mMVPMatrix, 0, mView, 0, mCamera, 0);
//
//      mSquare.draw(mMVPMatrix);
      
//	    // Create a rotation for the triangle  
//	    // long time = SystemClock.uptimeMillis() % 4000L;  
//	    // float angle = 0.090f * ((int) time);  
//	    Matrix.setRotateM(mRotationMatrix, 0, mAngle, 0, 0, -1.0f);  
//	  
//	    // 合并旋转矩阵到投影和相机视口矩阵  
//	    Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);  
	  
	    // 画一个角度  
	    mSquare.draw(mMVPMatrix);
//	    sprite.Draw(mMVPMatrix);

	}

	@Override
	public void onFinishFrame(Viewport arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewFrame(HeadTransform headTransform) {
		// TODO Auto-generated method stub
		
//		mLightPosParam = GLES20.glGetUniformLocation(mGlProgram, "u_LightPos");
		
		 // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mCamera, 0, 0, 0,0, CAMERA_Z, 0f, 0f, 0f, 1.0f, 0.0f);

        
        
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
        
//        mGlProgram = GLES20.glCreateProgram();
	}
	
	

	@Override
	public void onSurfaceCreated(EGLConfig gl) {
		// TODO Auto-generated method stub
		 Log.i(TAG, "onSurfaceCreated");
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        mSquare   = new Square();
        sprite=new Sprite(this);
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
                
                if(dx>0)
                {
                	test+=0.1;
                }
                else
                {
                	test-=0.1;
                }
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }
    
}

