# raven-clj

Fork of A Clojure interface to Sentry.

## Usage

```clojure
[org.clojars.hcarvalhoalves/raven-clj "1.5.0"]
```

### `capture`

The `capture` function is a general use function that could be placed throughout your Clojure code to log information to your Sentry server.

```clojure
(def dsn "https://b70a31b3510c4cf793964a185cfe1fd0:b7d80b520139450f903720eb7991bf3d@example.com/1")

(capture dsn {:message "Test Exception Message"
             :tags {:version "1.0"}
             :logger "main-logger"
             :extra {:my-key 1
                     :some-other-value "foo bar"}})

;; Associate stacktrace information to event, optionally passing your app's namespaces as the final arg to stacktrace.
(capture dsn
        (-> {:message "Test Stacktrace Exception"}
            (interfaces/stacktrace (Exception.) ["myapp.ns"])))
```

Please refer to [Building the JSON Packet](http://sentry.readthedocs.org/en/latest/developer/client/index.html#building-the-json-packet) for more information on what
attributes are allowed within the packet sent to Sentry.

## License

Copyright © 2013-2015 Seth Buntin
Copyright © 2016 Henrique Carvalho Alves

Distributed under the Eclipse Public License, the same as Clojure.
