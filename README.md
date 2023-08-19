# Secure Simplified RTSP video protocol for streaming

Java implementation of a Client/Server protocol to transmit videos safely through UDP

## Project overview



## Project structure

```bash
.gitignore
LICENSE
src
├───hjStreamServer
│   │   hjStreamServer.java
│   │
│   └───movies
│           ._cars.dat
│           cars.dat
│           monsters.dat
│
├───hjUDPproxy
│       config.properties
│       hjUDPproxy.java
│
└───security
    │   MySRTSPDatagramSocket.java
    │
    └───encryption
            EncryptPayload.java
            KeyManager.java
```
