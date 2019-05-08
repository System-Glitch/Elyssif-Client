package fr.elyssif.client.gui.view;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.image.BufferedImage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import javafx.embed.swing.SwingFXUtils;



public final class QRCode {
	private String data;
	private int size;

	
	public QRCode(){
		this("", 400);
	}
	public QRCode(String data) {
		this.data = data;
		this.size = 400;
	}
	public QRCode(String data, int size){
		this.data = data;
		this.size = size;
	}

	public final String getData() {
		return data;
	}

	public final void setData(String data) {
		this.data = data;
	}

	public final int getSize() {
		return size;
	}

	public final void setSize(int size) {
		this.size = size;
	}

	private BitMatrix generateMatrix(final String data, final int size) {
		BitMatrix bitMatrix;
		try {
			bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, size, size);
			return bitMatrix;
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			Logger.getGlobal().log(Level.SEVERE, "message", e);
		
		}
		return null;

	}

	private BufferedImage toBufferedImage(final BitMatrix bitMatrix) {
		BufferedImage bufImg;
		bufImg = MatrixToImageWriter.toBufferedImage(bitMatrix);
		return bufImg;
	}

	public WritableImage make(){				
		// encode
		BitMatrix bitMatrix = generateMatrix(data,size);
		return SwingFXUtils.toFXImage(toBufferedImage(bitMatrix), null);


	}
	/**
	 * @param args
	 * @return 
	 */

}
