package it.keyorchestra.registrowebapp.scuola.util;

import android.util.Base64;

public class EncoderBase64 {
	/**
	 * Java program to encode and decode String in Java using Base64 encoding
	 * algorithm
	 * 
	 * @author http://javarevisited.blogspot.com
	 */
	public static String codificaBase64(String daCodificare) {
		// encoding byte array into base 64
		byte[] encoded = android.util.Base64.encode(daCodificare.getBytes(),
				Base64.DEFAULT);
		// encoded = Base64.encodeBase64(encoded);

		return new String(encoded);
	}

	public static String decodificaBase64(String codificata) {
		// decoding byte array into base64 byte[] decoded =
		byte[] decoded = android.util.Base64.decode(codificata.getBytes(),
				Base64.DEFAULT);
		// decoded = Base64.decodeBase64(decoded);

		return new String(decoded);
	}
}
