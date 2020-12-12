#!/usr/bin/env bash
# get base dir regardless of execution location
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$(cd -P "$(dirname "$SOURCE")" && pwd)"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
. "$(dirname "$SOURCE")/init.sh"

git submodule update --init --progress
if [[ "$1" == up* ]]; then
  (
    echo "Updating submodules"
    (
    cd "$basedir/Tuinity/" || exit
    git checkout ver/1.16.4 && git pull && git reset --hard && git submodule update --init --recursive -f
    cd ../
    git add Tuinity
    )

    (
    cd "$basedir/fabric-loader/" || exit
    git checkout master && git pull && git reset --hard && git submodule update --init --recursive -f
    cd ../
    git add fabric-loader
    )
  )
fi

tuinityVer=$(gethead Tuinity)
cd "$basedir/Tuinity/" || exit

./tuinity up
./tuinity patch
git reset HEAD current-paper
git checkout -- current-paper

cd "Tuinity-Server" || exit
mcVer=$(mvn -o org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate -Dexpression=minecraft_version | sed -n -e '/^\[.*\]/ !{ /^[0-9]/ { p; q } }')

basedir
. "$basedir"/scripts/importmcdev.sh

minecraftversion=$(grep <"$basedir"/Tuinity/Paper/work/BuildData/info.json minecraftVersion | cut -d '"' -f 4)
version=$(echo -e "Tuinity: $tuinityVer\nmc-dev:$importedmcdev")
tag="${minecraftversion}-${mcVer}-$(echo -e "$version" | shasum | awk '{print $1}')"
echo "$tag" >"$basedir"/current-tuinity

"$basedir"/scripts/generatesources.sh

cd Tuinity/ || exit

function tag() {
  (
    cd "$1" || exit
    if [ "$2" == "1" ]; then
      git tag -d "$tag" 2>/dev/null
    fi
    echo -e "$(date)\n\n$version" | git tag -a "$tag" -F - 2>/dev/null
  )
}
echo "Tagging as $tag"
echo -e "$version"

forcetag=0
if [ "$(cat "$basedir"/current-tuinity)" != "$tag" ]; then
  forcetag=1
fi

tag Tuinity-API $forcetag
tag Tuinity-Server $forcetag
