# Nexus Java SDK

[![Java Docs](https://img.shields.io/badge/Java_Docs-red)](https://javadoc.io/doc/io.nexusrpc/nexus-sdk/latest/index.html)
[![Continuous Integration](https://github.com/nexus-rpc/sdk-java/actions/workflows/ci.yml/badge.svg)](https://github.com/nexus-rpc/sdk-java/actions/workflows/ci.yml)

Package for working with the Nexus [HTTP API](https://github.com/nexus-rpc/api).

## What is Nexus?

Nexus is a synchronous RPC protocol. Arbitrary length operations are modelled on top of a set of pre-defined synchronous RPCs.

A Nexus caller calls a handler. The handler may respond inline or return a reference for a future, asynchronous
operation. The caller can cancel an asynchronous operation, check for its outcome, or fetch its current state. The
caller can also specify a callback URL, which the handler uses to asynchronously deliver the result of an operation when it is ready.

## Supported Java runtimes
* Java 1.8+

## Build configuration

[Find the latest release](https://search.maven.org/artifact/io.temporal/temporal-sdk) of the Temporal Java SDK at maven central.

Add *temporal-sdk* as a dependency to your *pom.xml*:

    <dependency>
      <groupId>io.nexusrpc</groupId>
      <artifactId>nexus-sdk</artifactId>
      <version>N.N.N</version>
    </dependency>

or to *build.gradle*:

    compile group: 'io.nexusrpc', name: 'nexus-sdk', version: 'N.N.N'

