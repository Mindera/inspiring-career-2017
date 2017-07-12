# Mindera Inspiring Career 07/2017 Backend

## Configuration

Run the following commands:

```
$ mvn clean package
```

You can now boot your application in two ways:

### Java

```
$ java -jar bootstrap/target/inspiring-career-fat.jar -conf bootstrap/src/main/conf/config.json
```

### Docker

```
$ docker-compose up -d
### Remember: if there are any changes in the code you need to rebuild Docker images with:
### docker-compose build --no-cache
```
