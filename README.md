WebApp for the wutsi blog platform


![](https://github.com/wutsi/wutsi-blog-web/workflows/build/badge.svg)
[![](https://img.shields.io/codecov/c/github/wutsi/wutsi-blog-web/master.svg)](https://codecov.io/gh/wutsi/wutsi-blog-web)
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
            <server>
                <id>github-wutsi-editorjs</id>
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
- Install the blog REST API [wutsi-blog-service](https://github.com/wutsi/wutsi-blog-service#installation)


### Launch the application
- Make sure that `wutsi-blog-service` is not running!
- Launch the webapp on port `8081` with the command:
```
$ java -jar target/wutsi-blog-web.jar
```
- Launch the blog REST server [wutsi-blog-service](https://github.com/wutsi/wutsi-blog-service#launch-the-service)
- Navigate to `http://localhost:8081`
