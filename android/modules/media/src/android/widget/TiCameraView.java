package android.widget;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class TiCameraView extends SurfaceView
{
   Camera camera;
   SurfaceHolder previewHolder;
   public TiCameraView(Context context)
   {
	   super(context);
	   initCameraView();
   }
   
   private void initCameraView()
   {
	   previewHolder = this.getHolder();
	   previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	   previewHolder.addCallback(surfaceHolderListener);
	}
   
   SurfaceHolder.Callback surfaceHolderListener = new SurfaceHolder.Callback() {
	   public void surfaceCreated(SurfaceHolder holder) {
  	            camera=Camera.open();
  	            try {
  	                    camera.setPreviewDisplay(previewHolder);
  	            }
  	            catch (Throwable t){ }
  	           }
  	   public void surfaceChanged(SurfaceHolder holder, int format, int w,int h)
  	   {
  	      Parameters params = camera.getParameters();
  	      params.setPreviewSize(w, h);
  	      params.setPictureFormat(PixelFormat.JPEG);
  	              camera.setParameters(params);
  	              camera.startPreview();
  	   }

  	   public void surfaceDestroyed(SurfaceHolder arg0)
  	   {
  	      camera.stopPreview();
  	      camera.release();   
  	   }
   };
}