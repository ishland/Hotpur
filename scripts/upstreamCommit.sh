#!/usr/bin/env bash
(
  set -e
  PS1="$"

  function changelog() {
    base=$(git ls-tree HEAD "$1" | cut -d' ' -f3 | cut -f1)
    cd "$1" && git log --oneline "${base}"...HEAD
  }
  tuinity=$(changelog Tuinity)
  fabric_loader=$(changelog fabric-loader)

  updated=""
  logsuffix=""
  updatedMultiple=""
  if [ -n "$tuinity" ]; then
    logsuffix="$logsuffix\n\Tuinity Changes:\n$tuinity"
    if [ -z "$updated" ]; then updated="Tuinity"; else updated="$updated, Tuinity"; updatedMultiple="s"; fi
  fi
  if [ -n "$fabric_loader" ]; then
    logsuffix="$logsuffix\n\nfabric-loader Changes:\n$fabric_loader"
    if [ -z "$updated" ]; then updated="fabric-loader"; else updated="$updated, fabric-loader"; updatedMultiple="s"; fi
  fi
  disclaimer="Upstream has released updates that appears to apply and compile correctly\nThis update has NOT been tested by ishlandbukkit and as with ANY update, please do your own testing."

  if [ -n "$1" ]; then
    disclaimer=("$@")
  fi

  log="${UP_LOG_PREFIX}Updated Upstream${updatedMultiple} ($updated)\n\n${disclaimer[*]}${logsuffix}"

  echo -e "$log" | git commit -F -

) || exit 1
