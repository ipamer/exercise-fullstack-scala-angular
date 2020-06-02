#docker-compose exec kafka bash -c 'kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic meetups --from-beginning'
docker-compose exec kafka bash -c 'kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic meetups'
