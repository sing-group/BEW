package es.uvigo.ei.sing.r;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class RConsole extends JPanel implements RMainLoopCallbacks{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Rengine engine;
	
	private JTextPane messagePane;
	private static Font WARNFont = new Font("monospaced", Font.BOLD, 12);
    private static Color WARNColor = new Color(255, 153, 51);
    private static Font ERRORFont = new Font("monospaced", Font.BOLD, 12);
    private static Color ERRORColor = Color.RED;
    private static Font FATALFont = new Font("monospaced", Font.BOLD, 12);
    private static Color FATALColor = Color.RED;
    private static Font defaultFont = new Font("monospaced", Font.BOLD, 12);
    private static Color defaultColor = Color.BLACK;
    private static Color backgroundEnabled = new Color(200,255,200);
    private static Color backgroundDisabled = Color.GRAY;
    private JTextField inputField;
    private Object fieldKey = new Object();
    
    
    private LinkedList<String> history = new LinkedList<String>();
    private int historyPos = -1;

	public RConsole(){
		this.engine = RengineFactory.getEngine();
		engine.addMainLoopCallbacks(this);
		this.setLayout(new BorderLayout());
		this.add(new JScrollPane(getMessagePane()));
		this.add(new JScrollPane(getInputField()), BorderLayout.SOUTH);
		engine.startMainLoop();
	}
	
	
	
	
	private JTextPane getMessagePane(){
		if (this.messagePane==null){
			this.messagePane = new JTextPane();
			this.messagePane.setEditable(false);
		}
		return this.messagePane;
	}
	
	
	private JTextField getInputField(){
		if (this.inputField == null){
			this.inputField = new JTextField();
			this.inputField.addKeyListener(new KeyAdapter(){
				@Override
				public void keyPressed(KeyEvent event){
					if (event.getKeyCode() == KeyEvent.VK_ENTER){
						synchronized(fieldKey){
							history.add(0, inputField.getText());
							historyPos = -1;
							fieldKey.notify();
							
						}
						
					}else if (event.getKeyCode() == KeyEvent.VK_UP){
						// history back
						if ((historyPos+1) < history.size()){
							inputField.setText(history.get(historyPos+1));
							historyPos++;
						}
					}else if (event.getKeyCode() == KeyEvent.VK_DOWN){
						if (historyPos > 0){
							inputField.setText(history.get(historyPos-1));
							historyPos--;
						}
						else if (historyPos == 0){
							inputField.setText("");
							historyPos--;
						}
					}
				}
			});
			this.inputField.setBackground(backgroundEnabled);
		}
		return this.inputField;
	}
	
	//////// RMainLoopCallbacks
	@Override
	public void rBusy(Rengine arg0, int arg1) {
	
		this.inputField.setEnabled(false);
		this.inputField.setBackground(backgroundDisabled);
		
	}

	@Override
	public String rChooseFile(Rengine arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rFlushConsole(Rengine arg0) {
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public void rLoadHistory(Rengine arg0, String arg1) {
		System.err.println("rLoadHistory "+arg1);
		
	}

	@Override
	public String rReadConsole(Rengine arg0, String arg1, int arg2) {
		
		
		synchronized(fieldKey){				
			try {
				inputField.setBackground(backgroundEnabled);
				
				inputField.setEnabled(true);
				inputField.requestFocus();
				fieldKey.wait();				
				if (this.getInputField().getText().length()>0){
					String text = inputField.getText();
					
					inputField.setText("");
					
					
						text+="\n";
						return text;
					
					
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	

	@Override
	public void rSaveHistory(Rengine arg0, String arg1) {
	//	System.err.println("rSaveHistory "+arg0+" "+arg1);
		
	}

	@Override
	public void rShowMessage(Rengine arg0, String arg1) {
//		System.err.println("rShowMessage: "+arg1);
		 Font f = defaultFont;
	     Color c = defaultColor;
         this.appendText(arg1, f,c);
		
	}

	@Override
	public void rWriteConsole(Rengine arg0, String arg1, int arg2) {
		//System.err.println("write console: "+arg1);
		 Font f = defaultFont;
	     Color c = defaultColor;
         if (arg2==1) {
             f = ERRORFont;
             c = ERRORColor;
         } 
         this.appendText(arg1, f, c);

		
	}
	private int MAXSIZE=10000;
	private void appendText(String text, Font font, Color c) {
        JTextPane pane = this.messagePane;
        Document d = pane.getDocument();
        int oldPosition = d.getEndPosition().getOffset();

        try {
            d.insertString(oldPosition, text, null);

            MutableAttributeSet attrs = pane.getInputAttributes();
            StyleConstants.setFontFamily(attrs, font.getFamily());
            StyleConstants.setFontSize(attrs, font.getSize());
            StyleConstants.setItalic(attrs, (font.getStyle() & Font.ITALIC) != 0);
            StyleConstants.setBold(attrs, (font.getStyle() & Font.BOLD) != 0);
            StyleConstants.setForeground(attrs, c);

            StyledDocument doc = pane.getStyledDocument();
            doc.setCharacterAttributes(oldPosition, doc.getLength() + 1, attrs,
                false);
            pane.setCaretPosition(pane.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        if (d.getLength()>MAXSIZE){
        	try {
				d.remove(0, d.getLength()-MAXSIZE);
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
    }
	private void appendText(String text) {
		appendText(text, defaultFont, defaultColor);
    }

}
