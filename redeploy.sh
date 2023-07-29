cd ./L2jMobuisServer
git pull
ant

cd ../


if [ ! -f ./build/L2J_Mobius_Classic_Interlude.zip ]; then
	echo "File not found"
	exit 1
fi

if [ ! -d ./runner ]; then
	mkdir runner
else
#	rm -rf runner
	mkdir runner
fi

mv ./build/L2J_Mobius_Classic_Interlude.zip ./runner
rm -rf build
cd ./runner
unzip L2J_Mobius_Classic_Interlude.zip
rm L2J_Mobius_Classic_Interlude.zip


chmod +x *.sh
cd ./login
chmod +x *.sh
cd ../game
chmod +x *.sh
cd ../


./stop.sh &&
./start.sh