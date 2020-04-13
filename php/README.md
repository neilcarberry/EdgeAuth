# Phenix EdgeAuth Digest Tokens for PHP

Easily generate secure digest tokens to use with the Phenix platform without requiring any networking activity.

## Installation

To install Phenix Edge Authorization Digest Token with composer:

```shell script
$ composer install
```

## Run the Tests

To run the unit tests with composer:

```shell script
$ composer run unit
```

## PHP Example

```PHP
<?php

namespace EdgeAuthExample;

use EdgeAuth\TokenBuilder;

class Example
{
    function __construct()
    {
        $theToken = (new TokenBuilder())
            ->withApplicationId('test')
            ->withSecret('abc')
            ->expiresInSeconds(300);
        $result = $theToken->build();
    }
}
```

## Command Line Examples

Display the help information:
```shell script
./bin/eddgeauth --help
```

Create a token for channel access:
```shell script
./bin/edgeauth --applicationId "my-application-id" --secret "my-secret" --expiresInSeconds 3600 --channel "us-northeast#my-application-id#my-channel.1345"
```

## To include Phenix EdgeAuth in your project

Sample composer.json:

```json
{
    "name": "you/edgeauthexample",
    "type": "project",
    "authors": [
        {
            "name": "PHP Developer",
            "email": "php.developer@somemail.com"
        }
    ],
    "require": {
        "phenixrts/edgeauth": "@1.0.*"
    },
    "autoload": {
        "psr-4": {"EdgeAuthExample\\": "src/"}
    },
    "repositories": [
        {
            "type": "package",
            "package": {
                "name": "phenixrts/edgeauth",
                "version": "1.0.0",
                "dist": {
                    "url": "https://github.com/PhenixRTS/EdgeAuth/releases/download/php%401.0.0/php@1.0.0.zip",
                    "type": "zip"
                },
                "autoload": {
                    "psr-4": ["src/"]
                }
            }
        }
    ]
}
```