# with-var-sets
A convenience Babashka script designed to streamline the process of setting multiple environment variables before executing a command.
This could be particularly useful for configuring environment variables in a local development setup.

## Usage
Create a configuration file `~/.var-sets.edn`
```edn
{"~/workspace/my-app/"
 {"local-dev"
  {:FOO_API_KEY "vNmhYZt3fAP2swEJtQJGo_opGpBidulZsvc"
   :BAR_NAME "baz"
   :BAZ_HOST "http://127.0.0.1"
   :QUX_PORT "8080"}}}
```
Run the command
```
./with-var-set local-dev [command to run your app]
```
Alternatively you can create alias in you shell or symlink it to dir included in `PATH` variable
```
with-var-set local-dev [command to run your app]
```

This is equivalent to:
```bash
FOO_API_KEY=vNmhYZt3fAP2swEJtQJGo_opGpBidulZsvc \
BAR_NAME=baz \
BAZ_HOST=http://127.0.0.1 \
QUX_PORT=8080 \
[command to run your app]
```

