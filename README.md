# json-simple

[![Build Status](https://travis-ci.com/pwall567/json-simple.svg?branch=main)](https://app.travis-ci.com/github/pwall567/json-simple)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/net.pwall.json/json-simple?label=Maven%20Central)](https://search.maven.org/search?q=g:%22net.pwall.json%22%20AND%20a:%22json-simple%22)

Simple JSON Parser and Formatter

## Background

This library is a set of simple JSON parsing and formatting functions, for use in those cases where the use of a more
heavyweight library can not be justified.

## Quick Start

To parse a string of JSON to a simple structure consisting solely of standard Java classes and interfaces:
```java
        Object structure = JSONSimple.parse(json);
```
The structure will consist of:
- a `Map` (for JSON objects)
- a `List` (for JSON arrays)
- a `String` (for JSON strings)
- an `Integer` (for JSON integers up to 32 bits)
- a `Long` (for JSON integers 32-64 bits)
- a `BigDecimal` (for other JSON numbers, including floating point)
- a `Boolean` (for JSON booleans)
- `null` (for the JSON null values using the `null` keyword)

To format an object like the one returned by a `parse()` operation:
```java
        String formatted = JSONSimple.format(structure);
```

See the JavaDoc for more information, including changing the indentation size and line terminator.

## Dependency Specification

The latest version of the library is 1.5.5, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>net.pwall.json</groupId>
      <artifactId>json-simple</artifactId>
      <version>1.5.5</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'net.pwall.json:json-simple:1.5.5'
```
### Gradle (kts)
```kotlin
    implementation("net.pwall.json:json-simple:1.5.5")
```

Peter Wall

2022-06-01
