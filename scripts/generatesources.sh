#!/usr/bin/env bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$(cd -P "$(dirname "$SOURCE")" && pwd)"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
. "$(dirname "$SOURCE")/init.sh"

cd "$basedir" || exit
purpurVer=$(cat current-purpur)

minecraftversion=$(grep <"$basedir"/Purpur/Paper/work/BuildData/info.json minecraftVersion | cut -d '"' -f 4)
decompile="Purpur/Paper/work/Minecraft/$minecraftversion/spigot"

mkdir -p mc-dev/src/net/minecraft/server

cd mc-dev || exit
if [ ! -d ".git" ]; then
  git init
fi

rm src/net/minecraft/server/*.java
cp "$basedir"/"$decompile"/net/minecraft/server/*.java src/net/minecraft/server

base="$basedir/Purpur/Purpur-Server/src/main/java/net/minecraft/server"
cd "$basedir"/mc-dev/src/net/minecraft/server/ || exit
for file in $(/bin/ls "$base"); do
  if [ -f "$file" ]; then
    rm -f "$file"
  fi
done
cd "$basedir"/mc-dev || exit
git add . -A
git commit . -m "mc-dev"
git tag -a "$purpurVer" -m "$purpurVer" 2>/dev/null
