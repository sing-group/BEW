package es.uvigo.ei.sing.r.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ScrollPaneConstants;

import es.uvigo.ei.sing.bew.view.components.CustomFileChooser;

public class RImageViewer extends JPanel{
	
	public int zoom = 100;
	private JComboBox zoomBox = new JComboBox();
	private Image image;
	private JScrollPane scroll = new JScrollPane();
	private JPanel imagePanel;
	public RImageViewer(final RImage rimage) {		
		
		this.setLayout(new BorderLayout());
		
		
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		
		this.image = rimage.getImageIcon().getImage();
		
		this.imagePanel = new JPanel(){
			@Override
			public void paint(Graphics g) {
				System.out.println("painting at zoom "+zoom);
				
				AffineTransform t = ((Graphics2D)g).getTransform();
				t.setToScale((float)zoom/(float)100, (float)zoom/(float)100);
				
				((Graphics2D)g).setBackground(Color.black);
				((Graphics2D)g).fillRect(0, 0, this.getWidth(), this.getHeight());
				((Graphics2D)g).drawImage(image, t, this);
				
			}

		};
		this.imagePanel.setSize(new Dimension((int)(image.getWidth(null)*(float)this.zoom/100f),(int)(image.getHeight(null)*(float)this.zoom/100f)));
		this.imagePanel.setPreferredSize(new Dimension((int)(image.getWidth(null)*(float)this.zoom/100f),(int)(image.getHeight(null)*(float)this.zoom/100f)));
		this.imagePanel.setOpaque(true);
		this.imagePanel.setBackground(Color.BLACK);
		this.imagePanel.setForeground(Color.BLACK);
		
		
		scroll.setViewportView(imagePanel);
		
		this.add(scroll, BorderLayout.CENTER);
		
		final JToolBar toolbar = new JToolBar();
		
		toolbar.add(new JButton("Save to PNG"){{
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					CustomFileChooser dialog = new CustomFileChooser();
					if (dialog.showSaveDialog(toolbar)==JFileChooser.APPROVE_OPTION){
						BufferedImage renderedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
						renderedImage.getGraphics().drawImage(image, 0, 0, null);						
						try {
							ImageIO.write(renderedImage, "png", dialog.getSelectedFile());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				
					
					
				}
				
			});
		}});
		
		zoomBox.setEditable(true);
		zoomBox.addItem("25");
		zoomBox.addItem("50");
		zoomBox.addItem("100");
		zoomBox.addItem("125");
		zoomBox.addItem("200");
		zoomBox.setSelectedItem("100");	
		
		zoomBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				updateZoom();			
			}		
			
		});
		
		
		
		toolbar.add(zoomBox);
		
		this.add(toolbar, BorderLayout.NORTH);
		
		updateZoom();
	}
	private void updateZoom() {
		if(zoomBox.getSelectedItem()!=null){
			try{
				this.zoom = Integer.parseInt(zoomBox.getSelectedItem().toString());
			}catch(NumberFormatException ex){
				this.zoom = 100;
				zoomBox.setSelectedItem("100");
			}
			
		}else{
			this.zoom = 100;
			zoomBox.setSelectedItem("100");
		}
		//imageLabel.invalidate();
		imagePanel.setSize(new Dimension((int)(image.getWidth(null)*(float)this.zoom/100f),(int)(image.getHeight(null)*(float)this.zoom/100f)));
		imagePanel.setPreferredSize(new Dimension((int)(image.getWidth(null)*(float)this.zoom/100f),(int)(image.getHeight(null)*(float)this.zoom/100f)));
		imagePanel.revalidate();
		System.out.println("image label size: "+imagePanel.getSize());
		
		//scroll.setViewportView(imageLabel);
		
		this.repaint();
		
	}

}
