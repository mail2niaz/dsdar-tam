/**
 * 
 */
package com.dsdar.thosearoundme.others;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.dsdar.thosearoundme.R;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

/**
 * Utility class is used to provide utility functions for decoding or compress
 * the image file.
 * 
 */
public class ImageUtil {
	public static final int CAPTURE = 0;
	public static final int BROWSE_LIBRARY = 1;

	/**
	 * @param bitmap
	 * @return converting bitmap and return a string
	 */
	public static String BitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		String temp = Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}

	/**
	 * @param encodedString
	 * @return bitmap (from given string)
	 */
	public static Bitmap StringToBitMap(String encodedString) {
		Bitmap bitmap = null;
		try {
			byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
			bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
		} catch (Exception e) {
			e.getMessage();
		}
		return bitmap;
	}

	/**
	 * @param encodedString
	 * @return bitmap (from given byte[])
	 */
	public static Bitmap ByteArrayToBitMap(byte[] encodeByte) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
					encodeByte.length);
		} catch (Exception e) {
			e.getMessage();
		}
		return bitmap;
	}

	/**
	 * Method is used to get the compressed bitmap from the given image file
	 * path
	 * 
	 * @param theFilePath
	 * @return
	 */
	public static Bitmap getCompressedBitmapFromImagePath(
			Uri aSelectedImageUri, Activity theActivity) {
		Bitmap aBitmap = null;
		try {
			String[] aProjection = { MediaStore.Images.Media.DATA };
			@SuppressWarnings("deprecation")
			Cursor aCursor = theActivity.managedQuery(aSelectedImageUri,
					aProjection, null, null, null);
			int aColumnIndex = aCursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			aCursor.moveToFirst();
			String aPath = aCursor.getString(aColumnIndex);

			FileInputStream aInputStream = new FileInputStream(aPath);
			BufferedInputStream aBufferedstream = new BufferedInputStream(
					aInputStream);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 8;
			aBitmap = BitmapFactory
					.decodeStream(aBufferedstream, null, options);

			if (aInputStream != null) {
				aInputStream.close();
			}
			if (aBufferedstream != null) {
				aBufferedstream.close();
			}
		} catch (FileNotFoundException e) {
			Log.e("ParkCheckIn",
					"Exception occurred while creating bitmap from the given file path",
					e);
		} catch (IOException e) {
			Log.e("ParkCheckIn",
					"Exception occurred while creating bitmap from the given file path",
					e);
		} catch (Exception e) {
			Log.e("ParkCheckIn",
					"Exception occurred while creating bitmap from the given file path",
					e);
		}

		return aBitmap;
	}

	public static BitmapDescriptor createMemberMarker(Context theContext, int id) {
//		Bitmap aMemberBitMap = BitmapFactory.decodeResource(
//				theContext.getResources(), R.drawable.member_marker);
		Bitmap aMemberBitMap = BitmapFactory.decodeResource(
				theContext.getResources(),id);
		Bitmap aUpdatedBitMap = Bitmap.createScaledBitmap(aMemberBitMap,
				50, 50,
				true);
		BitmapDescriptor aBitmapDescriptor = BitmapDescriptorFactory
				.fromBitmap(aUpdatedBitMap);
		return aBitmapDescriptor;
	}
	
	public static BitmapDescriptor createMyMarker(Context theContext) {
		Bitmap aMemberBitMap = BitmapFactory.decodeResource(
				theContext.getResources(), R.drawable.man_100);
		Bitmap aUpdatedBitMap = Bitmap.createScaledBitmap(aMemberBitMap,
				75, 75,
				true);
		BitmapDescriptor aBitmapDescriptor = BitmapDescriptorFactory
				.fromBitmap(aUpdatedBitMap);
		return aBitmapDescriptor;
	}	
}