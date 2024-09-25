Aristotle Acceptance Tests
==========================

![Cucumber Badge][Cucumber Badge]

Aristotle acceptance test framework is a slightly modified BDD, which
[eliminates the QA](https://spectrum.ieee.org/yahoos-engineers-move-to-coding-without-a-net)

## Instantiating the Aristotle Template
Please visit [Aristotle Github](https://github.com/paion-data/aristotle).
1. **clone the repo with `git clone https://github.com/paion-data/aristotle.git`**

## Running

Run the aristotle template with:

```console
mvn clean package
docker compose up --build --force-recreate
```

Then navigate to the project root and run all acceptance tests with

```console
mvn clean verify
```

[Cucumber Badge]: https://img.shields.io/badge/Cucumber-23D96C?style=for-the-badge&logo=cucumber&logoColor=white
