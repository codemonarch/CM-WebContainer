# CM-WebContainer

一个神奇的 Web 容器

- - -

CMW 提供两种接入方式，分别是直接接入 WebContainer 和接入相应的 Activity/UIViewController。

接入 WebContainer 方式如下:

Android

```kotlin
val webContainer = WebContainer(context)
webContainer.delegate = this
```

iOS

```swift
let webContainer = WebContainer(frame: CGRect(...))
webContainer.delegate = self
```

接入 Activity 的方式如下:

```kotlin
正在准备中 ...
```

接入 UIViewController 的方式如下:

```swift
正在准备中 ...
```

- - -

**API**

API 是两大平台通用的，它们的描述也完全一致。

| | | |
| :-- | :-- | :-- |
| Property | acceptCookies | 是否接受 Cookie |
| Property | delegate | 代理类，其中包含 ```onStartLoad```，```onEndLoad```，```onMeta``` 三个方法，分别对应页面开始载加，页面完成加载，获得 Meta 三个时机的回调 |
| Method | loadLocalResource | 加载本地的资源，这个加载将允许 CMW 优先使用本地资源，开发者可以将全部或部分资源放在本地 |
| Method | getCookie | 获取当前的 Cookie |
| Method | setCookie | 设置 Cookie，必须在请求前进行设置 |
| Method | load | 加载一个 url 对应的页面 |
| Method | loadLocal | 加载一个本地文件 |
| Method | callJs | 调用页面内的 js 路由 |
| Method | runJs | 直接执行一段 js 代码 |
| JsRouting | registerRouting | 注册一个 js 路由 |
| JsRouting | removeRouting | 移除一个 js 路由 |

