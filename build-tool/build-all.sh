cd aibg-2018-topic-game/logika
mvn package
cd ../../
cp aibg-2018-topic-game/logika/target/logika-0.0.1-SNAPSHOT.jar aibg-2018-topic-server/Server/lib/game.jar
cd aibg-2018-topic-server/Server/src/main/resources/static
npm run build
cd ..
cp static/dist/index.html templates/home.html
cd ../../../
mvn package
cd ../../
cp aibg-2018-topic-server/Server/target/Server-0.0.1-SNAPSHOT.jar build/build.jar
cp aibg-2018-topic-server/Server/src/main/resources/maps build/ -R
