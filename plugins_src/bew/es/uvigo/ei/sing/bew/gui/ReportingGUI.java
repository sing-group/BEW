package es.uvigo.ei.sing.bew.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.uvigo.ei.aibench.core.ParamSpec;
import es.uvigo.ei.aibench.core.operation.OperationDefinition;
import es.uvigo.ei.aibench.workbench.InputGUI;
import es.uvigo.ei.aibench.workbench.ParamsReceiver;
import es.uvigo.ei.sing.bew.model.IExperiment;
import es.uvigo.ei.sing.bew.model.Method;
import es.uvigo.ei.sing.bew.model.Plot;
import es.uvigo.ei.sing.bew.model.Statistic;
import es.uvigo.ei.sing.bew.view.dialogs.ReportingDialog;

/**
 * This class allows data collection to be sent to an AIBench operation.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class ReportingGUI implements InputGUI {

	@Override
	public void init(ParamsReceiver arg0, OperationDefinition<?> arg1) {

		ReportingDialog repd = new ReportingDialog();
		repd.setVisible(true);

		if (repd.isExit()) {
			// List with AIBench params
			List<Object> params = new LinkedList<Object>();

			// Variables for AIBench
			String path;
			// This is an Experiment.class
			IExperiment selExp;
			Boolean[] optionsList;
			Map<Method, List<Plot>> mapMethodPlots = new HashMap<Method, List<Plot>>();
			Map<Method, List<Statistic>> mapMethodStat = new HashMap<Method, List<Statistic>>();

			path = repd.getPath();
			selExp = repd.getSelectedExperiment();
			optionsList = repd.getExperimentOptions();
			mapMethodPlots = repd.getMapMethodPlots();
			mapMethodStat = repd.getMapMethodStatistics();
			
			params.add(path);
			params.add(selExp);
			params.add(optionsList);
			params.add(mapMethodPlots);
			params.add(mapMethodStat);

			ParamSpec[] aibenchParams = es.uvigo.ei.aibench.core.CoreUtils
					.createParams(params);

			System.err.println(aibenchParams);
			arg0.paramsIntroduced(aibenchParams);
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onValidationError(Throwable arg0) {
		// TODO Auto-generated method stub

	}
}
