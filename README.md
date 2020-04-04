WebApp for the wutsi blog platform


![](https://github.com/wutsi/wutsi-blog-web/workflows/build/badge.svg)
[![](https://img.shields.io/codecov/c/github/wutsi/wutsi-blog-web/master.svg)](https://codecov.io/gh/wutsi/wutsi-blog-web)
![](https://img.shields.io/badge/jdk-1.8-brightgreen.svg)
![](https://img.shields.io/badge/language-kotlin-blue.svg)


## Installation
- Download the code and build
```
$ git clone git@github.com:wutsi/wutsi-blog-web.git
$ cd wutsi-blog-web
$ mvn clean install
```
- Install the blog REST API [wutsi-blog-service](https://github.com/wutsi/wutsi-blog-service#installation)


### Launch the application
- Launch the webapp on port `8081` with the command:
```
$ java -jar target/wutsi-blog-web.jar
```
- Launch the blog REST server [wutsi-blog-service](https://github.com/wutsi/wutsi-blog-service#launch-the-service)
- Navigate to `http://localhost:8081`
