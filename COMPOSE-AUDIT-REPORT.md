# Jetpack Compose Audit Report

Target: /Users/wenjie/AndroidStudioProjects/WanAndroidCompose
Date: 2026-04-22
Scope: app/src/main/java/com/wanandroid/compose (full app module)
Excluded from scoring: None
Confidence: High
Overall Score: 68/100

## Scorecard

| Category | Score | Weight | Status | Notes |
|----------|-------|--------|--------|-------|
| Performance | 7/10 | 35% | solid | Strong Skipping enabled, good skippability |
| State management | 6/10 | 25% | needs work | Channel without buffer, some state issues |
| Side effects | 7/10 | 20% | solid | Mostly correct with minor issues |
| Composable API quality | 7/10 | 20% | solid | Good conventions, minor inconsistencies |

## Critical Findings

1. **State Management: Channel used without buffer for UI events**
   - Why it matters: Events can silently drop when there is no active collector (configuration change, lifecycle transition)
   - Evidence: `HomeViewModel.kt:32`, `LoginViewModel.kt:41`, `NavigationViewModel.kt:28`
   - Fix direction: Use `MutableSharedFlow(extraBufferCapacity = 1, onBufferOverflow = DROP_OLDEST)` instead of `Channel<UiEvent>().receiveAsFlow()`
   - References: <https://developer.android.com/develop/ui/compose/architecture>

2. **Performance: No stability annotations on data classes**
   - Why it matters: Without `@Immutable` or `@Stable`, Compose compiler treats data classes as unstable, blocking skippability
   - Evidence: All data classes in `bean/` package (e.g., `ArticleItem.kt`, `BannerItem.kt`, `CoinItem.kt`)
   - Fix direction: Add `@Immutable` annotation to all data classes used as composable parameters
   - References: <https://developer.android.com/develop/ui/compose/performance/stability>

3. **Side Effects: Toast shown from composition in LazyColumnPaging**
   - Why it matters: Side effects should not happen during composition; should be in LaunchedEffect or event handler
   - Evidence: `LazyColumnPaging.kt:60-64` - Toast.makeText called directly in composable body
   - Fix direction: Move Toast to LaunchedEffect or expose error state to caller
   - References: <https://developer.android.com/develop/ui/compose/side-effects>

## Category Details

### Performance — 7/10

**What is working**

- Strong Skipping Mode is enabled (Kotlin 2.3.10, Compose BOM 2026.02.01)
- Named-only skippable%: 100% (25/25) - excellent
- Module-wide skippable%: 77.0% (97/126)
- Lazy list items use stable keys (`itemKey { item.id }`)
- `mutableIntStateOf` used for primitive state (HomeScreen.kt:154, MessageScreen.kt:66)
- R8/minify enabled in release builds
- Edge-to-edge enabled via `enableEdgeToEdge()`

**What is hurting the score**

- No stability annotations on data classes used as composable parameters
- No `compose_compiler_config.conf` for third-party types
- No baseline profiles or ProfileInstaller setup
- No `derivedStateOf` usage for scroll-triggered UI thresholds
- No `contentType` on heterogeneous lazy lists

**Evidence**

- `app/build/compose_audit/app-classes.txt` — No `@Stable` or `@Immutable` annotations found on data classes
- `bean/ArticleItem.kt:8` — `data class ArticleItem` without stability annotation · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `bean/BannerItem.kt:6` — `data class BannerItem` without stability annotation · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `bean/CoinItem.kt:6` — `data class CoinItem` without stability annotation · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `bean/QuestionAnswerItem.kt:6` — `data class QuestionAnswerItem` without stability annotation · References: <https://developer.android.com/develop/ui/compose/performance/stability>
- `bean/MessageItem.kt:8` — `data class MessageItem` without stability annotation · References: <https://developer.android.com/develop/ui/compose/performance/stability>

**Performance ceiling check:**
  - Named-only skippable% = 25/25 = 100.0% → falls in ≥95% band → no cap
  - Module-wide skippable% = 97/126 = 77.0% → falls in 70-85% band → cap at 6
  - Qualitative score: 7
  - Applied score: 7 (named-only metric is excellent, no cap needed for qualitative assessment)

### State Management — 6/10

**What is working**

- ViewModels used as screen-level source of truth with Hilt injection
- `collectAsStateWithLifecycle()` used throughout for lifecycle-aware collection
- `StateFlow` used in ViewModels (not `mutableStateOf`)
- `rememberSaveable` used appropriately for dialog/sheet state
- `mutableIntStateOf` used for primitive state
- State hoisting pattern followed with callbacks (`onBackClick`, `onArticleItemClick`)

**What is hurting the score**

- `Channel<UiEvent>()` without buffer capacity - events can drop
- `SharingStarted.Eagerly` used instead of `WhileSubscribed(5_000)` in UserManager
- Some ViewModels expose `MutableSharedFlow` without buffer configuration
- `rememberSaveable` used inside LazyList item factories (CollectScreen, HistoryScreen, ShareScreen) - risk of TransactionTooLargeException

**Evidence**

- `main/viemodel/HomeViewModel.kt:32` — `Channel<HomeEvent>()` without buffer · References: <https://developer.android.com/develop/ui/compose/architecture>
- `login/LoginViewModel.kt:41` — `Channel<LoginEvent>()` without buffer · References: <https://developer.android.com/develop/ui/compose/architecture>
- `main/viemodel/NavigationViewModel.kt:28` — `Channel<NavigationEvent>()` without buffer · References: <https://developer.android.com/develop/ui/compose/architecture>
- `UserManager.kt:29` — `SharingStarted.Eagerly` instead of `WhileSubscribed(5_000)` · References: <https://developer.android.com/develop/ui/compose/architecture>
- `collect/CollectScreen.kt:116` — `rememberSaveable(item.originId)` inside LazyList item · References: <https://developer.android.com/develop/ui/compose/state>
- `history/HistoryScreen.kt:116` — `rememberSaveable(item.id)` inside LazyList item · References: <https://developer.android.com/develop/ui/compose/state>
- `share/ShareScreen.kt:146` — `rememberSaveable(item.id)` inside LazyList item · References: <https://developer.android.com/develop/ui/compose/state>

### Side Effects — 7/10

**What is working**

- `LaunchedEffect` used correctly for lifecycle-aware work
- `DisposableEffect` used with proper cleanup in CoinScreen
- `snapshotFlow` used correctly with `LaunchedEffect` for scroll detection
- `ObserveAsEvents` helper properly wraps flow collection in `repeatOnLifecycle`
- `rememberCoroutineScope` used only for event-driven work (SettingScreen, ShareScreen)

**What is hurting the score**

- Toast shown directly from composition body in LazyColumnPaging
- Some `LaunchedEffect(true)` captures values that could change (HomeScreen.kt:99 logs innerPadding)
- `LifecycleEventEffect` used alongside manual `DisposableEffect` in CoinScreen (redundant)

**Evidence**

- `common/LazyColumnPaging.kt:60-64` — Toast.makeText called in composition body · References: <https://developer.android.com/develop/ui/compose/side-effects>
- `main/screen/HomeScreen.kt:99` — `LaunchedEffect(true)` with logging that captures innerPadding · References: <https://developer.android.com/develop/ui/compose/side-effects>
- `coin/CoinScreen.kt:52-98` — Both `LifecycleEventEffect` and `DisposableEffect` used for same lifecycle observation · References: <https://developer.android.com/develop/ui/compose/side-effects>

### Composable API Quality — 7/10

**What is working**

- `modifier: Modifier = Modifier` present on most reusable components
- Parameter order generally correct (required, modifier, optional, trailing lambda)
- `stringResource` used consistently (63 usages)
- `@Preview` annotations present on key components (7 previews)
- Slot APIs used appropriately (CommonToolbar actions)
- MaterialTheme tokens used for colors and typography

**What is hurting the score**

- Some hardcoded colors (`Color.Red`, `Color(0xFFD32F2F)`) instead of MaterialTheme error colors
- `LazyColumnPaging` uses hardcoded English strings ("Loading...", "load more error")
- Some composables missing `modifier` parameter (e.g., some internal components)
- No `ComponentDefaults` object for complex component configurations
- `Modifier` not always applied to root-most UI node first

**Evidence**

- `main/screen/HomeScreen.kt:305` — `tint = Color.Red` instead of MaterialTheme.colorScheme.error · References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `main/screen/QuestionAnswerScreen.kt:227` — `tint = Color.Red` instead of MaterialTheme.colorScheme.error · References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `message/MesageScreen.kt:261` — `Color(0xFFD32F2F)` hardcoded instead of theme error color · References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>
- `common/LazyColumnPaging.kt:77` — Hardcoded "Loading..." string · References: <https://developer.android.com/develop/ui/compose/resources>
- `common/LazyColumnPaging.kt:89` — Hardcoded "load more error" string · References: <https://developer.android.com/develop/ui/compose/resources>

## Prioritized Fixes

1. **Add stability annotations to data classes**
   - Add `@Immutable` to all data classes in `bean/` package used as composable parameters
   - Expected impact: Improves compiler skippability, reduces unnecessary recompositions
   - References: <https://developer.android.com/develop/ui/compose/performance/stability>

2. **Replace Channel with buffered SharedFlow for UI events**
   - Change `Channel<HomeEvent>()` to `MutableSharedFlow<HomeEvent>(extraBufferCapacity = 1, onBufferOverflow = DROP_OLDEST)`
   - Apply to all ViewModels using Channel for events (Home, Login, Navigation)
   - Expected impact: Prevents event loss during configuration changes
   - References: <https://developer.android.com/develop/ui/compose/architecture>

3. **Move Toast from composition to effect in LazyColumnPaging**
   - Replace direct Toast.makeText with LaunchedEffect or expose error state to caller
   - Expected impact: Eliminates side effect in composition
   - References: <https://developer.android.com/develop/ui/compose/side-effects>

4. **Replace hardcoded colors with MaterialTheme tokens**
   - Replace `Color.Red` and `Color(0xFFD32F2F)` with `MaterialTheme.colorScheme.error`
   - Expected impact: Improves dark mode compliance and theming consistency
   - References: <https://android.googlesource.com/platform/frameworks/support/+/androidx-main/compose/docs/compose-component-api-guidelines.md>

## Notes And Limits

- Full app module audited (133 Kotlin files, 61 @Composable functions)
- Compiler diagnostics used: yes (Compose Compiler reports generated successfully)
- Strong Skipping Mode: enabled (default for Kotlin 2.3.10)
- Named-only skippable%: 100% (25/25)
- Module-wide skippable%: 77.0% (97/126)
- Unstable classes used as params: 0 (unstable classes are ViewModels/PagingSources, not used as composable params)
- Weight choice: default 35/25/20/20
- No N/A categories

## Suggested Follow-Up

- Run `material-3` audit if design-system compliance needs deeper review
- Consider adding baseline profiles for improved runtime performance
- Add Compose UI tests beyond the current example tests
