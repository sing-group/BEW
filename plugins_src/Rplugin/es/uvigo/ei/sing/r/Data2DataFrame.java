package es.uvigo.ei.sing.r;

import java.util.ArrayList;
import java.util.List;

import org.rosuda.JRI.Rengine;

import es.uvigo.ei.aibench.core.operation.annotation.Direction;
import es.uvigo.ei.aibench.core.operation.annotation.Operation;
import es.uvigo.ei.aibench.core.operation.annotation.Port;
import es.uvigo.ei.sing.datatypes.data.Data;
import es.uvigo.ei.sing.datatypes.data.Sample;
import es.uvigo.ei.sing.datatypes.data.Variable;

@Operation(description="creates a R data frame variable from a given SING Data")
public class Data2DataFrame {

	
	
	private Data data;
	private String varName;
	
	@Port(direction=Direction.INPUT, order=1, name="SING Data")
	public void setData(Data data) {
		this.data = data;
	}

	@Port(direction=Direction.INPUT, order=2, name="R variable name")
	public void setVarName(String varName) {
		this.varName = varName;
		createDataFrameFromData(this.data, this.varName);
	}	
	
	
	public void createDataFrameFromData(Data data, String varName){		
		Rengine engine = RengineFactory.getEngine();
		
		
		String[] rowNames = new String[data.getSampleCount()];
		for (int i = 0; i<data.getSampleCount(); i++){
			rowNames[i]=data.getSampleAt(i).getSampleIdentifier().toString();
		}
		
		engine.assign("temp", rowNames);
		engine.eval(""+varName+"<-data.frame(row.names=temp)");
		
		// fast version, create a double matrix
		
		List<Variable> floatVariables = new ArrayList<Variable>(data.getVariableCount());
		List<Variable> otherVariables = new ArrayList<Variable>(data.getVariableCount());
		for (int i = 0; i<data.getVariableCount(); i++){
			if (data.getVariableAt(i).getType() == Variable.TYPE_FLOAT){
				floatVariables.add(data.getVariableAt(i));
			}else{
				otherVariables.add(data.getVariableAt(i));
			}
		}
		double[][] floatVals = new double[data.getSampleCount()][floatVariables.size()];
		String[] varNames = new String[floatVariables.size()];
		String[] sampleNames = new String[data.getSampleCount()];
		boolean firstRow = true;
		for (int v=0; v<floatVariables.size(); v++){
			
			varNames[v] = floatVariables.get(v).getName();
			for (int s=0; s<data.getSampleCount(); s++){
				if (firstRow){
					sampleNames[s] = data.getSampleAt(s).getSampleIdentifier().toString();
				}
				floatVals[s][v] = ((Float)data.getSampleAt(s).getVariableValue(floatVariables.get(v))).doubleValue();
			}
			firstRow = false;
		}
		
		String name = "x_temp";
		Util.createMatrix(floatVals, name, sampleNames, varNames);
		System.err.println("eval: "+engine.eval(""+name+"[1,2]"));
		
		engine.eval(""+varName+"<- data.frame("+name+")");
		
		for (int i = 0; i<otherVariables.size(); i++){
			Variable var = otherVariables.get(i);		
			String[] values = new String[data.getSampleCount()];
			for (int j = 0; j<data.getSampleCount(); j++){
				Sample sample = data.getSampleAt(j);
				values[j] = sample.getVariableValue(var).toString();
			}
			engine.assign("temp", values);
			engine.eval(""+varName+"[\""+var.getName()+"\"]<-temp");
		
		}
		
		/*
		for (int i = 0; i<data.getVariableCount(); i++){
			Variable var = data.getVariableAt(i);
			if (var.getType()!= Variable.TYPE_FLOAT) continue;
		}
		
		for (int i = 0; i<data.getVariableCount(); i++){
			Variable var = data.getVariableAt(i);
			if (var.getType()==Variable.TYPE_FLOAT){
				double[] values = new double[data.getSampleCount()];
				for (int j = 0; j<data.getSampleCount(); j++){
					Sample sample = data.getSampleAt(j);
					values[j] = ((Float)sample.getVariableValue(var)).doubleValue();
				}
				engine.assign("temp", values);
				engine.eval(""+varName+"[\""+var.getName()+"\"]<-temp");
				
			}else{
				String[] values = new String[data.getSampleCount()];
				for (int j = 0; j<data.getSampleCount(); j++){
					Sample sample = data.getSampleAt(j);
					values[j] = sample.getVariableValue(var).toString();
				}
				engine.assign("temp", values);
				engine.eval(""+varName+"[\""+var.getName()+"\"]<-temp");
			}
		}
	*/
		
	}



	public static void main(String[] args){
		double[][] data = new double[][]{{11,12,13},{21,22,23},{31,32,33}};
		Util.createMatrix(data, "hola", new String[]{"fila1", "fila2", "fila3"}, new String[]{"col1", "col2", "col3"});
		System.err.println(data[1][0]+""+RengineFactory.getEngine().eval("hola[3,2]"));
		System.exit(0);
	}





}
