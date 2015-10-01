# What is BEW?
Biofilms Experiment Workbench (BEW) is a novel software workbench for the operation and analysis of Biofilms experimental data. BEW enables **full management and analysis of Experiments and Inter-Lab Experiments**.

Users can perfom routine data management as well as data analysis namely:

* **Experiment and analytical methods management**. These contain the information data.
* **Open and save Experiments** in xml and **import and export** them in xls.
* **Run statistical tests** using R engine on these Experiments.
* **Plot generation** about the methods data.
* **Comparing two or more Intra Experiments** in order to create an Inter laboratory Experiment.
* Generating **HTML reports** of the information and data of the Experiments.

# How it works?
The application was developed with [AIBench, an open-source Java desktop application framework](http://www.aibench.org/ "AIBENCH's Homepage") for scientific software development in the domain of translational biomedicine.

As illustrated in the image below, BEW also incorporates external libraries and plugins:

* A plug-in for the R statistical computing tool [(R webpage)](http://www.r-project.org/ "R's Homepage").
* JFreeChart library for data plotting [(JFreeChart webpage)](http://www.jfree.org/jfreechart/ "JFreeChart's Homepage").
* JXL library to read and write Excel sheets [(JXL webpage)](http://jexcelapi.sourceforge.net/ "JExcel's Homepage").
* JSoup library for working with HTML documents [(JSoup webpage)](http://jsoup.org/ "JSoup's Homepage").

These third-parties support main data processing and data analysis operations whilst enable future adaptation or extension.

BEW can also communicate [BiofOmics](http://biofomics.org/ "BiofOmics's Homepage") through webservices to perform some operations like: data submission, download public Experiments, update metadata, etc.

# How to run it?

Please, refer to the official tutorial in the following webpage: [BEW installation](http://sing.ei.uvigo.es/bew/tutorial.html#installation "BEW's tutorial webpage"), if you want to run the software in Windows, Linux or Mac.
