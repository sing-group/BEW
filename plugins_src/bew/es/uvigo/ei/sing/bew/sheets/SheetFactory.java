package es.uvigo.ei.sing.bew.sheets;

/**
 * This class lets the user creates new Sheets.
 * 
 * @author Gael Pérez Rodríguez.
 * 
 */
public class SheetFactory {
	/**
	 * Creates a new DataSheetConfigurator.
	 * 
	 * @return
	 */
	public CreateMethodSheetConfigurator newDataSheetConf() {
		return new CreateMethodSheetConfigurator();
	}

	/**
	 * Creates a new ConditionSheetConfigurator.
	 * 
	 * @return
	 */
	public SetupSheetConfig newCondSheetConf() {
		return new SetupSheetConfig();
	}
}
