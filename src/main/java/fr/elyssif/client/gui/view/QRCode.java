/*
 * Elyssif-Client
 * Copyright (C) 2019 Jérémy LAMBERT (System-Glitch)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 package fr.elyssif.client.gui.view;
import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

/**
 * QRCode builder.
 * 
 * @author Rémi SKONIECZNY
 *
 */
public final class QRCode {

	private String data;
	private int size;

	/**
	 * Create a new instance of QRCode builder with
	 * an empty content and a size of 400x400.
	 */
	public QRCode() {
		this("");
	}

	/**
	 * Create a new instance of QRCode builder with
	 * the given content and a size of 400x400. 
	 * @param data the QRCode content
	 */
	public QRCode(String data) {
		this(data, 400);
	}

	/**
	 * Create a new instance of QRCode builder.
	 * @param data the QRCode content
	 * @param size the size of the output image.
	 */
	public QRCode(String data, int size) {
		this.data = data;
		this.size = size;
	}

	/**
	 * Get the content of the QRCode.
	 * @return data
	 */
	public final String getData() {
		return data;
	}

	/**
	 * Set the content of the QRCode.
	 * @param data
	 */
	public final void setData(String data) {
		this.data = data;
	}

	/**
	 * Get the size (in pixels) of the output image.
	 * @return size
	 */
	public final int getSize() {
		return size;
	}

	/**
	 * Set the size of the output image.
	 * @param size in pixels
	 */
	public final void setSize(int size) {
		this.size = size;
	}

	private BitMatrix generateMatrix(final String data, final int size) {
		try {
			var hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hints.put(EncodeHintType.MARGIN, 0);
			return new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, size, size, hints);
		} catch (WriterException e) {
			Logger.getGlobal().log(Level.SEVERE, "message", e);
		}

		return null;
	}

	private BufferedImage toBufferedImage(final BitMatrix bitMatrix) {
		BufferedImage bufImg;
		bufImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
		return bufImg;
	}

	/**
	 * Create a QRCode image.
	 * @return image or null if an error occurred
	 */
	public WritableImage make() {				
		var bitMatrix = generateMatrix(data,size);
		return bitMatrix != null ? SwingFXUtils.toFXImage(toBufferedImage(bitMatrix), null) : null;
	}

}
