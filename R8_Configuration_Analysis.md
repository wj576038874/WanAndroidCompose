# R8 Configuration Analysis

## Configuration

### Build configuration check

- `app/build.gradle.kts`:
  - `release` 已开启 `isMinifyEnabled = true`
  - `release` 已开启 `isShrinkResources = true`
  - 使用 `getDefaultProguardFile("proguard-android-optimize.txt")`
  - 同时引入 `proguard-rules.pro`
- 结论：发布构建的压缩与优化配置正确。

### Gradle properties check

- `gradle.properties` 未出现 `android.enableR8.fullMode=false`
- 结论：未禁用 full mode。

### AGP check

- `gradle/libs.versions.toml` 中 `agp = "9.2.0"`
- 结论：已满足 AGP 9+，无需升级建议。

## Keep rules analysis

- `app/proguard-rules.pro` 当前没有启用任何 keep/dontwarn/dontnote 规则（仅注释内容）。
- 全项目 `.pro/.txt` 检索也未发现生效中的 keep 规则。
- 结论：当前不存在需要移除或收窄的 keep 规则。

## Actions to take

1. 保持当前 `release` 构建的 R8 配置不变。
2. 可选优化：`debug` 构建中存在 `proguardFiles(...)`，但 `isMinifyEnabled = false`；可移除该段以减少误解（不影响产物行为）。
3. 后续若新增 keep 规则，优先使用最小范围规则：仅保留反射实际访问的类/成员，避免包级通配符。
4. 变更 keep 规则后，使用 UI Automator 回归以下路径：
   - 导航与页面跳转
   - 网络请求与 JSON 反序列化链路
   - Hilt 注入创建页面/ViewModel
   - CameraX 与 WebView（若相关页面存在）

## Suggested `proguard-rules.pro` content

```proguard
# Keep this file minimal by default.
# Current project scan did not find mandatory reflection/JNI/WebView keep scenarios.

# Intentionally no custom keep rules.
```

## When to add rules later

仅在出现以下真实问题时再添加定向规则：

1. 运行期反射找不到类/方法（例如 `Class.forName` 或 `getDeclaredMethod` 报错）。
2. JNI 回调方法被混淆导致 `UnsatisfiedLinkError`。
3. WebView JS 接口被压缩后无法调用。
4. 某个序列化链路在 release 失效并能定位到具体类/字段。

新增规则时只保留最小目标（具体类/成员），不要使用包级通配符规则。
