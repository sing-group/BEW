package es.uvigo.ei.sing.r.plot;

import javax.swing.ImageIcon;

import es.uvigo.ei.aibench.core.Base64Coder;

/**
 * 
 * A base64 encoded image
 * @author lipido
 *
 */
public class RImage {

	byte[] bytes;
	public RImage(String b64image){
		bytes = Base64Coder.decode(b64image.toCharArray());
	}
	
	public ImageIcon getImageIcon(){
		return new ImageIcon(this.bytes);
	}
}
