#!/bin/sh
# Verifies that a packaged macOS app bundle only contains binaries for the given architecture,
# i.e. that it runs natively (without Rosetta 2) on the target Mac.
#
# Usage: verify-mac-arch.sh <ipscan-*.zip> <app name> <arch>
# Example: verify-mac-arch.sh build/libs/ipscan-*-macArm64.zip "Angry IP Scanner" arm64

set -e
[ $# -ge 3 ] || { echo "Usage: $0 <zip> <app name> <arch>" >&2; exit 2; }

zip="$1"
app="$2"
arch="$3"
tmp=$(mktemp -d)
trap 'rm -rf "$tmp"' EXIT

unzip -q "$zip" -d "$tmp"
macos="$tmp/$app.app/Contents/MacOS"

check() {
    actual=$(file -b "$1")
    echo "$actual" | grep -qw "$arch" || {
        echo "ERROR: $1 has wrong architecture (expected $arch): $actual" >&2
        exit 1
    }
}

check "$macos/jre/bin/java" "$arch"
check "$macos/jre/lib/server/libjvm.dylib" "$arch"

unzip -q -o "$macos"/*.jar '*.jnilib' -d "$tmp/swt"
for lib in "$tmp"/swt/*.jnilib; do
    check "$lib" "$arch"
done

echo "OK: $app binaries are $arch"
