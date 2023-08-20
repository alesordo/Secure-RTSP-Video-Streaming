# Secure simplified video streaming system

Java implementation of an encrypted real-time media streaming system.

## Project overview

The system is composed by a Streaming Server, a ProxyBox and an MPEG Player tool.

- **Streaming server**: a component that distributes movies encoded in sequences of secured FFMPEG media frames and delivered to distant proxies;
- **ProxyBox**: : a component to receive the encrypted streams. The media frames must first be processed at the proxy level in order to be decrypted and to maintain the necessary integrity, after which the streams are transferred (decrypted or in clear-format) to the media player tool;
- **MPEG Player tool**: a common media-player software, such as [VLC](https://www.videolan.org/index.pt.html).

The **security protocol** used to encrypt the video stream is a *simplified* [**SRTSP - Secure Real Time Streaming Protocol**](https://en.wikipedia.org/wiki/Secure_Real-time_Transport_Protocol). SRTSP is used to secure the media streams sent by the Streaming Server and received by the ProxyBox. To deliver the received MPEG frame segments encapsulated in the SRTSP payload in clear format (plainframes), the ProxyBox must be able to decrypt and process the protected frames.

It's simplified because the cryptography between Streaming Server and ProxyBox is **symmetric**. A better implementation will use separate keys.

[UDP](https://en.wikipedia.org/wiki/User_Datagram_Protocol) is the **transport protocol**. Data frames are encapsulated in SRTSP format and all SRTSP payload is, in turn, encapsulated in a UDP datagram, as you can see below:

![data-format](https://github.com/alesordo/Secure-RTSP-Video-Streaming/assets/85616887/4fa37a8c-f389-49e8-8c96-99ffba19366e)

The **SRTSP Header** is composed by:
* 4 bits: Simplified SRTSP protocol version ID (0001);
* 4 bits: message type. The default 0000 value denotes endpoint configuration by hand;
* 16 bits: integer which contains the size in bytes of the encrypted frame + MAC integrity check.

**RTSP protected FFMPEG Frame**. Frames in the payload are encrypted using the security configuration put up in the Streaming Server and ProxyBox endpoints that implement the SRSTP protocol. The cryptographic parameterizations, such as the symmetric cryptographic algorithm employed, the cryptographic mode, and the padding, determine the variable size.

**MAC Integrity Check**. Integrity check is supported by the HMAC and it can have variable sizes.

The complete structure of the project is the following:

![project-structure](https://github.com/alesordo/Secure-RTSP-Video-Streaming/assets/85616887/2d37aa22-1d1d-4030-aa50-b6fafb2c0d22)


## Project directory structure

```bash
.gitignore
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
    │   configSecurity.properties
    │   MySRTSPDatagramSocket.java
    │
    └───encryption
            EncryptPayload.java
            KeyManager.java
LICENSE
```

## Usage

### Configuration

