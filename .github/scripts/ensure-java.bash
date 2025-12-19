#!/bin/bash
# Verify variable name online first! (e.g., JAVA_HOME_21_X64)
VAR_NAME="JAVA_HOME_${REQUIRED_VERSION}_X64"
if [ -n "${!VAR_NAME}" ]; then
  echo "Java found at ${!VAR_NAME}. Setting JAVA_HOME."
  echo "JAVA_HOME=${!VAR_NAME}" >> $GITHUB_ENV
  echo "skipped=true" >> $GITHUB_OUTPUT
else
  echo "Java ${REQUIRED_VERSION} not found. Will install."
  echo "skipped=false" >> $GITHUB_OUTPUT
fi
