#!/bin/bash
set -e

VERSION=$1
TOKEN=$2

# 1. Download directly using the output of the Python script
echo "Searching for Ghidra version: '$VERSION'"
DOWNLOAD_URL=$(python3 "./.github/scripts/python/get_ghidra_artifact_url.py"\
    --version "$VERSION"\
    --token "$TOKEN")

if [ -z "$DOWNLOAD_URL" ]; then
    echo "Error: Failed to identify Ghidra artifact URL for version '$VERSION'"
    exit 1
fi

echo "Downloading from: '$DOWNLOAD_URL'"
curl -L -o ghidra.zip "$DOWNLOAD_URL"

# 2. Extract the archive
echo "Unzipping Ghidra..."
unzip -q ghidra.zip  # -q for quiet mode to keep logs clean

# 3. Identify folder and rename to .ghidra
# We use a wildcard to capture the folder even with the date suffix
EXTRACTED_FOLDER=$(ls -d ghidra_${VERSION}_PUBLIC* | head -n 1)

if [ -z "$EXTRACTED_FOLDER" ]; then
    echo "Error: Could not find extracted folder for version '$VERSION'"
    exit 1
fi

echo "Renaming $EXTRACTED_FOLDER to '.ghidra'"
mv "$EXTRACTED_FOLDER" .ghidra

# 4. Cleanup
rm ghidra.zip
echo "Ghidra installation complete"

# Install tree if it's missing (takes ~2 seconds)
if ! command -v tree &> /dev/null; then
    sudo apt-get update -qq && sudo apt-get install -y tree > /dev/null
fi

# Log the tree
# -a: Include hidden files (like .ghidra)
# -L 3: Show depth up to 3 levels
# -I: Ignore the large .git folder to keep the log clean
tree -LaI 3 -F .ghidra