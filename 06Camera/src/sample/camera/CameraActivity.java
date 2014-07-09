package sample.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnTouchListener {

	private static final int GALLERY_ACTION=0;
	private static final int CAMERA_ACTION=1;
	private Intent mIntent;
	private CanvasView mCanvasView;
	private Uri mUri;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		//キャンバスを取得して、背景画像を設定する。
		mCanvasView = (CanvasView)findViewById(R.id.canvas);
		mCanvasView.setBackgroundDrawable(new BitmapDrawable(BitmapFactory.decodeResource(getResources(), R.drawable.frame)));
		
		ImageView imageViewredbtn = (ImageView) findViewById(R.id.redbtn);
		imageViewredbtn.setOnTouchListener(this);
		ImageView imageViewbluebtn = (ImageView) findViewById(R.id.bluebtn);
		imageViewbluebtn.setOnTouchListener(this);
		ImageView imageViewgreenbtn = (ImageView) findViewById(R.id.greenbtn);
		imageViewgreenbtn.setOnTouchListener(this);
		ImageView imageViewblackbtn = (ImageView) findViewById(R.id.blackbtn);
		imageViewblackbtn.setOnTouchListener(this);
		ImageView imageViewimageViewblackbtn = (ImageView) findViewById(R.id.whitebtn);
		imageViewimageViewblackbtn.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			switch (v.getId()) {
			case R.id.redbtn:
				mCanvasView.setColor(Color.RED);
				return true;
			case R.id.bluebtn:
				mCanvasView.setColor(Color.BLUE);
				return true;
			case R.id.greenbtn:
				mCanvasView.setColor(Color.GREEN);
				return true;
			case R.id.blackbtn:
				mCanvasView.setColor(Color.BLACK);
				return true;
			case R.id.whitebtn:
				mCanvasView.setColor(Color.WHITE);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gallery:
			actionGallery();
			return true;
		case R.id.camera:
			actionCamera();
			return true;
		case R.id.export:
			actionExport();
			return true;
		case R.id.clear:
			mCanvasView.clear();
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK == resultCode) {
				switch(requestCode){
				case GALLERY_ACTION:
					mUri=data.getData();
					Toast.makeText(this, mUri.toString(), Toast.LENGTH_LONG).show();
					break;
				case CAMERA_ACTION:
					break;
				default:
					return;
			}
			try{
				InputStream is = getContentResolver().openInputStream(mUri);
				Bitmap bitmap = resize(BitmapFactory.decodeStream(is),mCanvasView.getWidth(),mCanvasView.getHeight());
				BitmapDrawable bd = new BitmapDrawable(bitmap);
				bd.setGravity(Gravity.LEFT|Gravity.TOP);
				mCanvasView.setBackgroundDrawable(bd);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}

	private void actionGallery() {
		mIntent = new Intent(Intent.ACTION_PICK);
		mIntent.setType("image/");
		startActivityForResult(mIntent,GALLERY_ACTION);
		
		
	}

	private void actionCamera() {
		ContentValues values = new ContentValues();
		String filename = System.currentTimeMillis() + "jpg";
		values.put(MediaStore.Images.Media.TITLE, filename);
		values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
		mUri= getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
		mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		mIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
		startActivityForResult(mIntent,CAMERA_ACTION);
	}

	private void actionExport() {
		File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddmmssSSS'.png'");
		File file = new File(filepath,sdf.format(new Date(System.currentTimeMillis())));
		try{
			Bitmap outputBitmap = mCanvasView.getBitmap();
			FileOutputStream fos = new FileOutputStream(file);
			outputBitmap.compress(Bitmap.CompressFormat.PNG, 100,fos);
			fos.close();
			MediaScannerConnection.scanFile(this,new String[]{file.getPath()},new String[]{"image/png"},null);
		}catch(IOException e){
			e.printStackTrace();
		}
		Toast.makeText(this, file.getPath() + "に保存しました。", Toast.LENGTH_LONG).show();
	}

	private Bitmap resize(Bitmap bitmap,int maxWidth,int maxHeight){
		if(bitmap==null){
			return bitmap;
		}
		int width=0;
		int height=0;
		if(bitmap.getWidth()>bitmap.getHeight()){
			width = maxWidth;
			height = width * bitmap.getHeight()/bitmap.getWidth();
		}else{
			height=maxHeight;
			width=height * bitmap.getWidth()/bitmap.getHeight();
		}
		Bitmap ret = Bitmap.createScaledBitmap(bitmap, width, height, true);
		return ret;
	}
	
}