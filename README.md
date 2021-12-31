# 趣味で作るAndroidネイティブのCOCOA

[![CI](https://github.com/keiji/cocoa-android/actions/workflows/CI.yml/badge.svg?branch=main)](https://github.com/keiji/cocoa-android/actions/workflows/CI.yml)


## How to build

```
cp settings-sample.properties settings.properties 
./gradlew assembleDebug
```

### ProductFlavors and BuildTypes

(*) Best choice for contributors.

#### ProductFlavors

##### enApiMode - ExposureNotification API Mode
|  enMock(*)  |  enProd  |
| ---- | ---- |
|  Mocked, for develop  |  Product, need certification  |

##### enApiVersion - ExposureNotification API Version
|  legacyV1  |  exposureWindow(*)  |
| ---- | ---- |
|  Use Legacy-V1(ENv1) API  |  Use ExposureWindow(ENv2) API  |

#### BuildTypes

|  Feature  |  debug(*)  |  staging  | release  |
| ---- | ---- | ---- | ---- |
|  AttestationAPI  |  disable  |  enable  |  enable  |


## License

TBD
