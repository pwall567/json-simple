# json-simple

[![Build Status](https://github.com/pwall567/json-simple/actions/workflows/build.yml/badge.svg)](https://github.com/pwall567/json-simple/actions/workflows/build.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/io.jstuff/json-simple?label=Maven%20Central)](https://central.sonatype.com/artifact/io.jstuff/json-simple)

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

The latest version of the library is 2.1, and it may be obtained from the Maven Central repository.

### Maven
```xml
    <dependency>
      <groupId>io.jstuff</groupId>
      <artifactId>json-simple</artifactId>
      <version>2.1</version>
    </dependency>
```
### Gradle
```groovy
    implementation 'io.jstuff:json-simple:2.1'
```
### Gradle (kts)
```kotlin
    implementation("io.jstuff:json-simple:2.1")
```

Peter Wall

2025-05-28
