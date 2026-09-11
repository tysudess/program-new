#!/usr/bin/env sh
set -eu
if command -v gradle >/dev/null 2>&1; then exec gradle "$@"; fi
VER=8.10.2
BASE="${GRADLE_USER_HOME:-$HOME/.gradle}/bootstrap/gradle-$VER"
BIN="$BASE/gradle-$VER/bin/gradle"
if [ ! -x "$BIN" ]; then
  mkdir -p "$BASE"
  ZIP="$BASE/gradle.zip"
  URL="https://services.gradle.org/distributions/gradle-$VER-bin.zip"
  if command -v curl >/dev/null 2>&1; then curl -L --fail "$URL" -o "$ZIP"; elif command -v wget >/dev/null 2>&1; then wget "$URL" -O "$ZIP"; else echo "Instale Gradle $VER ou curl/wget." >&2; exit 1; fi
  unzip -q -o "$ZIP" -d "$BASE"
fi
exec "$BIN" "$@"
