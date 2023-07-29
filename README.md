# pinot-client

Vert.x Pinot client exposes a convenient API for Eclipse Vert.x applications to query Apache Pinot servers.

The client is built atop the official [pinot-java-client](https://docs.pinot.apache.org/users/clients/java).

## Install

Using maven:
```
<dependency>
    <groupId>io.reactiverse</groupId>
    <artifactId>pinot-client</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

Using Gradle:
```
implementation("io.reactiverse:pinot-client:1.0-SNAPSHOT")
```

## Sample usage

Initialize the transport and the client:

```java
String brokerUrl = "localhost:8000";
VertxPinotClientTransport transport = new VertxPinotClientTransport(vertx);
VertxConnection connection = VertxConnectionFactory.fromHostList(vertx, List.of(brokerUrl), transport);
```

Here is a sample usage with Java API where we ask to retrieve a list of top 10 players with most home runs:

```java
String query = "select playerName, sum(homeRuns) AS totalHomeRuns from baseballStats where homeRuns > 0 group by playerID, playerName ORDER BY totalHomeRuns DESC limit 10";
connection
    .execute(query)
    .onSuccess(resultSetGroup -> {
        ResultSet results = resultSetGroup.getResultSet(0);
        System.out.println("Player Name\tTotal Home Runs");
        for (int i = 0; i < results.getRowCount(); i++) {
            System.out.println(results.getString(i, 0) + "\t" + results.getString(i, 1));
        }
    })
    .onFailure(Throwable::printStackTrace);
```

And here is the RxJava 2 API equivalent:

```java
String query = "select playerName, sum(homeRuns) AS totalHomeRuns from baseballStats where homeRuns > 0 group by playerID, playerName ORDER BY totalHomeRuns DESC limit 10";
connection
    .rxExecute(query)
    .subscribe(resultSetGroup -> {
        ResultSet results = resultSetGroup.getResultSet(0);
        System.out.println("Player Name\tTotal Home Runs");
        for (int i = 0; i < results.getRowCount(); i++) {
            System.out.println(results.getString(i, 0) + "\t" + results.getString(i, 1));
        }
    }, Throwable::printStackTrace);
```

You can configure the underlying webclient options while creating the Vert.x transport. For instance, here is how an
example of configuring timeouts on the web client transport:
```java
WebClientOptions options = new WebClientOptions()
    .setConnectTimeout(15000)
    .setIdleTimeout(15000)
    .setKeepAliveTimeout(15000);
VertxPinotClientTransport transport = new VertxPinotClientTransport(vertx, Map.of(), "http", null, options);
```
