package es.uvigo.ei.sing.r;

import org.rosuda.JRI.Rengine;

public class Util {

	
	
	/**
	 * Creates a matrix in R from the specified one. A varible named with <code>varName</code> will be created
	 * @param data
	 * @param varName
	 */
	public static void createMatrix(double[][] data, String varName){
		Rengine engine = RengineFactory.getEngine();	
		
		engine.eval(""+varName+"<- matrix(nrow="+data.length+", ncol="+data[0].length+")");
		for (int i = 0; i< data.length; i++){
			engine.assign("temp", data[i]);
			engine.eval(""+varName+"["+(i+1)+",]<-temp");
		}
		
		engine.eval("gc()");
		
	}

	/**
	 * Creates a matrix in R from the specified one. A varible named with <code>varName</code> will be created
	 * @param data
	 * @param varName
	 */
	public static void createMatrix(double[] col1, double[] col2, String varName){
		Rengine engine = RengineFactory.getEngine();	
		
		engine.eval(""+varName+"<- matrix(nrow="+col1.length+", ncol="+2+")");
		for (int i = 0; i< col1.length; i++){
			double[] row = new double[2];
			row[0] = col1[i];
			row[1] = col2[i];
			engine.assign("temp", row);
			engine.eval(""+varName+"["+(i+1)+",]<-temp");
		}
		
		engine.eval("gc()");
		
	}
	

	/**
	 * Creates a matrix in R from the specified one. A varible named with <code>varName</code> will be created
	 * @param data
	 * @param varName
	 */
	public static void createMatrix(double[] col1, String varName){
		Rengine engine = RengineFactory.getEngine();	
		
		engine.eval(""+varName+"<- matrix(nrow="+col1.length+", ncol="+1+")");
		for (int i = 0; i< col1.length; i++){
			double[] row = new double[1];
			row[0] = col1[i];
			engine.assign("temp", row);
			engine.eval(""+varName+"["+(i+1)+",]<-temp");
		}
		
		engine.eval("gc()");
		
	}
	/**
	 * Creates a matrix in R from the specified one. A varible named with <code>varName</code> will be created
	 * @param data
	 * @param varName
	 */
	public static void createMatrix(double[][] data, String varName, String[] rownames, String[] colnames){
		createMatrix(data, varName);
		
		Rengine engine = RengineFactory.getEngine();
		engine.assign("rnames", rownames);
		engine.assign("cnames", colnames);
		
		
		engine.eval("rownames("+varName+")<-rnames"); 
		engine.eval("colnames("+varName+")<-cnames"); 
	}

}
