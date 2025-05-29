#!/bin/bash

echo "From: $1"
echo "  To: $2"

mkdir -p $2
cp -r $1/data $2/data
cp -r $1/graphics $2/graphics
cp -r $1/jars $2/jars
