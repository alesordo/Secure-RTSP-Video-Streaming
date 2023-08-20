# Secure simplified video streaming system

Java implementation of a encrypted real-time media streaming system.

## Project overview

The system is composed by a Streaming Server, a ProxyBox and an MPEG Player tool.

- **Streaming server**: a component that may distribute movies that are encoded in sequences of secured FFMPEG media frames and delivered to distant proxies;
- **ProxyBox**: : a component to receive the encrypted streams. The media frames must first be processed at the proxy level in order to be decrypted and to maintain the necessary integrity, after which the streams are transferred (decrypted or in clear-format) to the media player tool;
- **MPEG Player tool**: a common media-player software, such as [VLC](https://www.videolan.org/index.pt.html).

The **security protocol** used to encrypt the video stream is a "simplified" [**SRTSP - Secure Real Time Streaming Protocol**](https://en.wikipedia.org/wiki/Secure_Real-time_Transport_Protocol). SRTSP is used to secure the media streams sent by the Streaming Server and received by the ProxyBox. To deliver the received MPEG frame segments encapsulated in the SRTSP payload in clear format (plainframes), the ProxyBox must be able to decrypt and process the protected frames.

SRTSP encapsulation uses [UDP](https://en.wikipedia.org/wiki/User_Datagram_Protocol) as transport protocol. This way, the data frames are encapsulated in SRTSP format and this one is, in turn, encapsulated in UDP format.

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
