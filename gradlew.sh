#!/usr/bin/env bash
# Bash shim for the Gradle wrapper. Delegates to the POSIX launcher in this
# directory so the Gradle wrapper can be run from any working directory.
SCRIPT_DIR="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
exec "$SCRIPT_DIR/gradlew" "$@"
