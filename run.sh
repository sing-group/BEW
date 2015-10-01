#!/bin/bash
# Set R environment variables.
echo "Configuring R stuff ..."

# cd to this file
cd "$(dirname "$0")"

# Set R environment variables.
export R_HOME=$(dirname "$0")/R-3.0.0

if [ ! -x $R_HOME/bin/R ]; then
	chmod +x $R_HOME/bin/R
fi

export R_SHARE_DIR=${R_HOME}/share 
export R_INCLUDE_DIR=${R_HOME}/include
export R_DOC_DIR=${R_HOME}/doc
export R_LIBS_USER="$($R_HOME/bin/R --slave -e 'cat(.libPaths()[1])')"
# Include R shared libraries in LD_LIBRARY_PATH.
export LD_LIBRARY_PATH=${R_HOME}/lib:${R_HOME}/bin

# The directory holding the JRI shared library (libjri.so).
# JRI_LIB_PATH=${HOME}/R/i686-pc-linux-gnu-library/3.0/rJava/jri
JRI_LIB_PATH=${R_HOME}/library/rJava/jri

# Loadgin AIBench
echo "Loading BEW ..."

export JAVA_HOME=$(dirname "$0")/jre7
if [ ! -x $JAVA_HOME/bin/java ]; then
	chmod +x $JAVA_HOME/bin/java
fi

# Start with installed Java version
$JAVA_HOME/bin/java -Djava.library.path="${JRI_LIB_PATH}" -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel -Duser.language=en -jar ./lib/aibench.jar ./plugins_bin
