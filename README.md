# JavaChat

Реализация клиент-серверной игры “Танки” на двух игроков. Поддержка сохранения статистики боев в БД.

# Сборка

cd TanksServer-folder/Server && mvn clean install && cd ../../TanksClient-folder/Client && mvn clean install && cd ../..

# Запуск

Нужно 3 окна: \
2 окна - cd TanksClient-folder/Client && java -jar target/Client-1.0-SNAPSHOT.jar \
1 окно - cd TanksServer-folder/Server && java -jar target/Server-1.0-SNAPSHOT.jar