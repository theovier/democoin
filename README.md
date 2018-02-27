# Democoin

This is a small example cryptocurrency which is based on _Bitcoin_.
It was written for my bachelor thesis _Implementierung einer eigenen Kryptowährung in Java nach dem Vorbild Bitcoin_.

## Getting Started

###Docker

**Democoin** has an own docker image.

```
docker run -d \
--publish=7777:7777 \
-v /Users/theo/desktop/democoin/data:/data \
-v /Users/theo/desktop/democoin/logs:/logs \
theovier/democoin \
-m "my Message" \
-a "1AQPpmmMrUBCCdEJBt7usSrXG7ZTuBs9tu"
```