#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out src/ExpressionEvaluator.java src/Theme.java src/UnitConverterPanel.java src/ScientificCalculator.java
java -cp out ScientificCalculator

