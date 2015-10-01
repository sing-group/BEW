package es.uvigo.ei.sing.r;


import org.apache.log4j.Logger;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RMainLoopCallbacks;
import org.rosuda.JRI.Rengine;

public class RengineFactory {
	static Logger logger = Logger.getLogger(RengineFactory.class.getName());
	private static Rengine engine;
	
	
	public static Rengine getEngine(){
		if (engine == null){
			
			engine = new Rengine(new String[]{"--no-save"}, false,null){

				
				@Override
				public REXP eval(String s, boolean convert){
					
					return super.eval(s, convert);
					
				}
			};
			
			engine.addMainLoopCallbacks(new RMainLoopCallbacks(){

				@Override
				public void rBusy(Rengine arg0, int arg1) {
					// TODO Auto-generated method stub
					
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
					// TODO Auto-generated method stub
					
				}

				@Override
				public String rReadConsole(Rengine arg0, String arg1, int arg2) {
					logger.info("read console "+arg1+" arg2 "+arg2);
					return null;
				}

				@Override
				public void rSaveHistory(Rengine arg0, String arg1) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void rShowMessage(Rengine arg0, String arg1) {
					//System.out.println("[Rengine message]: "+arg1);
					
				}

				@Override
				public void rWriteConsole(Rengine arg0, String arg1, int arg2) {
					logger.info("write console 1");
				//	if (arg2==0){
					//	System.out.println("[Rengine write type "+arg2+"]: "+arg1);
					//}
					
				}
				
			});
			
			
			engine.waitForR();
		}
		return engine;
	}
}
