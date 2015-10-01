package es.uvigo.ei.sing.r;

import java.io.File;

import org.platonos.pluginengine.PluginLifecycle;

/**
 * @author Daniel Gonzalez Peña
 *
 */
public class Lifecycle extends PluginLifecycle {

	@Override
	public void start(){	
		File f = new File("rimage.png");
		if (f.exists()){
			f.delete();
		}
		RengineFactory.getEngine().eval("png(file=\"rimage.png\", width=1024, height=768)");
//		new Thread(){
//			
//			private long lastUpdate = System.currentTimeMillis();
//			private boolean shouldUpdate(){
//				File f = new File("rimage.png");
//				if (f.exists()){
//					long now = System.currentTimeMillis();
//					if (f.lastModified()!=lastUpdate){
//						
//						return true;
//					}else{
//						return false;
//					}
//				}
//				return false;
//			}
//			@Override
//			public void run() {
//				
//				while (true){
//					try {
//						Thread.sleep(2000);
//						
//						if (shouldUpdate()){
//							RengineFactory.getEngine().eval("dev.off(dev.cur())");
//							File f = new File("rimage.png"); 
//							
//							byte[] read = new byte[(int)f.length()];							
//							FileInputStream st = new FileInputStream(f);
//							st.read(read);
//							st.close();
//							RengineFactory.getEngine().eval("png(file=\"rimage.png\", width=1024, height=768)");
//							
//							lastUpdate = new File("rimage.png").lastModified();
//							ArrayList params = new ArrayList(1);
//							params.add(new String(Base64Coder.encode(read))); 
//							Core.getInstance().executeOperation("funster.createimage", new ProgressHandler(){
//
//								@Override
//								public void operationError(Throwable arg0) {
//									// TODO Auto-generated method stub
//									
//								}
//
//								@Override
//								public void operationFinished(List<Object> arg0, List<ClipboardItem> arg1) {
//									// TODO Auto-generated method stub
//									
//								}
//
//								@Override
//								public void operationStart(Object arg0, Object arg1) {
//									// TODO Auto-generated method stub
//									
//								}
//
//								@Override
//								public void validationError(Throwable arg0) {
//									// TODO Auto-generated method stub
//									
//								}}, params);
//						}
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (FileNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		}.start();
	}
}
