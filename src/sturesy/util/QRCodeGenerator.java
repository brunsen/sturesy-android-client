/*
 * StuReSy - Student Response System
 * Copyright (C) 2012-2014  StuReSy-Team
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package sturesy.util;

import sturesy.util.web.WebCommands;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.encoder.QRCode;

/**
 * Class for generating QR-Codes
 * 
 * @author w.posdorfer
 */
public class QRCodeGenerator {

	/**
	 * Return a QR-Code-Image from the saved URL adding the lecture-id to the
	 * end<br>
	 * http://someurl.com/index.php?lecture=lectureid
	 */
	public static Bitmap getQRImageForSavedAdress(String lectureid, int size) {
		return getQRImage(
				Settings.getInstance().getString(Settings.CLIENTADDRESS)
						+ "?lecture=" + WebCommands.encode(lectureid), size);
	}

	/**
	 * Generates a QR-Code-Image from given String
	 * 
	 * @param url
	 * @param width
	 * @param height
	 * @throws WriterException
	 */
	public static Bitmap getQRImage(String url, int size) {
		Bitmap mBitmap;
		BitMatrix bitMatrix;
		@SuppressWarnings("unused")
		QRCode qr = new QRCode();
		QRCodeWriter writer = new QRCodeWriter();
		mBitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		try {
			bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, size, size);
			for (int i = 0; i < 350; i++) {
				for (int j = 0; j < 350; j++) {
					mBitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK
							: Color.WHITE);
				}
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return mBitmap;
	}
}
