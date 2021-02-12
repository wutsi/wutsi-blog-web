WebApp for the wutsi blog platform


![](https://github.com/wutsi/wutsi-blog-web/workflows/master/badge.svg)
![](https://github.com/wutsi/wutsi-blog-web/workflows/pull_request/badge.svg)
![](https://img.shields.io/badge/jdk-1.8-brightgreen.svg)
![](https://img.shields.io/badge/language-kotlin-blue.svg)


# Getting Started
## Pre-requisites
- JDK 1.8
- MySQL 5.6+
- Maven 3.6+
- Google Chrome
- Setup Maven
   - Setup a token to get access to Github packages
      - Goto [https://github.com/settings/tokens](https://github.com/settings/tokens)
      - Click on `Generate New Token`
      - Give a value to your token
      - Select the permissions `read:packages`
   - Register the repositories in `~/.m2/settings.xml`
```xml
    <settings>
        ...
        <servers>
            ...
            <!-- Configure connectivity to the Github repositories -->
            <server>
                <id>github-wutsi-blog-client</id>
                <username>YOUR_GITHIB_USERNAME</username>
                <password>YOUR_GITHIB_TOKEN</password>
            </server>
            <server>
                <id>github-wutsi-core</id>
                <username>YOUR_GITHIB_USERNAME</username>
                <password>YOUR_GITHIB_TOKEN</password>
            </server>
            <server>
                <id>github-wutsi-core-aws</id>
                <username>YOUR_GITHIB_USERNAME</username>
                <password>YOUR_GITHIB_TOKEN</password>
            </server>
        </servers>
    </settings>
```

## Installation
- Download the code and build
```
$ git clone git@github.com:wutsi/wutsi-blog-web.git
$ cd wutsi-blog-web
$ mvn clean install
```
- Install the Blog API [wutsi-blog-service](https://github.com/wutsi/wutsi-blog-service#installation)
- Install the Tracking API [wutsi-track-service](https://github.com/wutsi/wutsi-track-service#installation)

## Run test
- Download your chromedriver version [there](https://chromedriver.chromium.org/downloads)
- Move this chromedriver in application root
- Run your tests
```
mvn clean install -Dheadless=true
```


### Launch the application
- Launch [wutsi-blog-service](https://github.com/WutsiTeam/wutsi-blog-service#usage)
- Launch [wutsi-track-service](https://github.com/wutsi/wutsi-track-service#usage)
- Launch the webapp on port `8081` with the command:
```
$ java -jar target/wutsi-blog-web.jar
```
- Navigate to `http://localhost:8081`

# How to
#### How to format code
```
mvn antrun:run@ktlint
```

#### How to check code formatting error
```
mvn antrun:run@ktlint-format
```
#### How to generate coverage report
```
mvn jacoco:report
```

