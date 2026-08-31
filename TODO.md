# Security & Bug Audit Findings

## CRITICAL

### 2. HostnameFetcher Resource Leak — UDP Sockets on Exception
**File:** `src/net/azib/ipscan/fetchers/HostnameFetcher.java:70-72, 86-88`

`MDNSResolver` and `NetBIOSResolver` are created, `resolve()` is called, but if it throws, `close()` is never reached. Leaks UDP sockets on every failed resolution during scans.

```java
var resolver = new MDNSResolver(subject.getAdaptedPortTimeout());
var name = resolver.resolve(subject.getAddress());  // if this throws...
resolver.close();  // ...this is skipped
```

**Fix proposal:** Convert to try-with-resources:
```java
try (var resolver = new MDNSResolver(subject.getAdaptedPortTimeout())) {
    return resolver.resolve(subject.getAddress());
}
```

---

### 3. PingFetcher.init()/cleanup() Race Condition
**File:** `src/net/azib/ipscan/fetchers/PingFetcher.java:90-110`

`pinger` is `volatile` and `pingerUsers` is `AtomicInteger`, but the check-then-act in `init()` is not atomic. Two threads calling `init()` can both see `pinger == null` and create two pingers (one leaked). `cleanup()` has a similar race between decrement and null assignment.

**Fix proposal:** Use `synchronized` on a shared lock object for the init/cleanup block, or use `AtomicReference<Pinger>` with `compareAndSet`.

---

### 4. ScanningSubject.isLocalHost() NPE
**File:** `src/net/azib/ipscan/core/ScanningSubject.java:80-82`

`ifAddr` can be null when no matching address family exists on the network interface. `address.equals(ifAddr.getAddress())` throws NPE. Called by `UnixMACFetcher.getLocalMAC()`.

**Fix proposal:** Add null check: `return ifAddr != null && address.equals(ifAddr.getAddress());`

---

## HIGH

### 5. mDNS Response ID Validation — `&&` Instead of `||`
**File:** `src/net/azib/ipscan/util/MDNSResolver.java:71`

```java
if (response[0] != request[0] && response[1] != request[1]) return null;
```

Should be `||`. With `&&`, a forged response matching either byte of the 16-bit transaction ID is accepted (1-in-256 chance). Enables hostname spoofing on the LAN.

**Fix proposal:** Change `&&` to `||`.

---

### 6. mDNS decodeName Out-of-Bounds Read
**File:** `src/net/azib/ipscan/util/MDNSResolver.java:35-44`

`len` is a signed byte from network data. Values 0x80-0xFF become negative, causing `StringIndexOutOfBoundsException`. Even positive values are never checked against remaining buffer length.

**Fix proposal:** Mask with `& 0xFF`, validate `len` against remaining bytes before constructing the string.

---

### 7. mDNS Response Offset from Untrusted numQueries
**File:** `src/net/azib/ipscan/util/MDNSResolver.java:72-74`

`numQueries` is read directly from the attacker-controlled response. If the response is shorter than the computed offset, `respPacket.getLength() - offset` goes negative.

**Fix proposal:** Validate that `offset < respPacket.getLength()` before proceeding.

---

### 8. SQL Injection in SQLExporter — results[0] Unescaped
**File:** `src/net/azib/ipscan/exporters/SQLExporter.java:51`

`results[0]` (IP address) is concatenated with zero escaping. Subsequent values at least get `'`→`''`.

**Fix proposal:** Apply the same `.replace("'", "''")` to `results[0]`, and also handle null values (see #12).

---

### 9. SQLExporter — Backslash Not Escaped (MySQL Attack Vector)
**File:** `src/net/azib/ipscan/exporters/SQLExporter.java:56`

Only `'`→`''` escaping. MySQL's default mode treats `\` as escape char. A value like `DOMAIN\user` or `\'; DROP TABLE scan;--` corrupts the SQL.

**Fix proposal:** Also escape backslashes: `.replace("\\", "\\\\").replace("'", "''")`.

---

### 10. ScanningResultList.update() — NPE from Map.get() Auto-Unboxing
**File:** `src/net/azib/ipscan/core/ScanningResultList.java:134-140`

`resultIndexes.get(result.getAddress())` can return `null`, which auto-unboxes to `int` and throws NPE.

**Fix proposal:** Use `resultIndexes.getOrDefault(result.getAddress(), -1)` and handle the -1 case.

---

### 11. ScanningResultList.iterator() — Unsynchronized Iterator
**File:** `src/net/azib/ipscan/core/ScanningResultList.java:175-179`

Documented as "not synchronized". Iterator is obtained under lock but used without synchronization. `ConcurrentModificationException` during concurrent scanning and export.

**Fix proposal:** Return a copy of the list's iterator, or use `CopyOnWriteArrayList`.

---

### 12. SQLExporter — NPE on Null Result Values
**File:** `src/net/azib/ipscan/exporters/SQLExporter.java:56`

`result.toString()` on null throws NPE. The `Exporter` interface explicitly allows null values.

**Fix proposal:** `output.print("'" + (result == null ? "NULL" : result.toString().replace("'", "''")) + "'");`

---

### 13. NamedListConfig.remove() — NPE on Missing Key
**File:** `src/net/azib/ipscan/config/NamedListConfig.java:88-90`

`namedList.remove(key)` returns null if key not found, then `.toString()` throws NPE.

**Fix proposal:** Null-check the return value before calling `.toString()`.

---

### 14. FavoritesConfig.getFeederId()/getSerializedParts() — NPE on Missing Key
**File:** `src/net/azib/ipscan/config/FavoritesConfig.java:32-41`

`get(key)` returns null if not found, then `.indexOf()` throws NPE.

**Fix proposal:** Null-check `value` before calling `.indexOf()`.

---

### 15. MACVendorFetcher.findMACVendor() — NPE / StringIndexOutOfBoundsException
**File:** `src/net/azib/ipscan/fetchers/MACVendorFetcher.java:49-51`

`mac.replace(":", "").substring(0, 6)` crashes if mac is null or shorter than 6 chars after removing colons.

**Fix proposal:** Add length check before substring.

---

### 16. RangeFeeder — Wrong Unsigned IP Conversion
**File:** `src/net/azib/ipscan/feeders/RangeFeeder.java:67-74`

```java
rawEndIP = rawEndIP >= 0 ? rawEndIP : rawEndIP + Integer.MAX_VALUE;
```

Adding `Integer.MAX_VALUE` to a negative int does not produce the correct unsigned long. Progress bar is wrong for IPs in 128.0.0.0–255.255.255.255.

**Fix proposal:** `rawEndIP = rawEndIP & 0xFFFFFFFFL;`

---

### 17. UnixMACFetcher — Process Streams Leaked
**File:** `src/net/azib/ipscan/fetchers/UnixMACFetcher.java:26-40`

`Runtime.exec()` returns a Process whose stderr is never consumed and the Process is never destroyed. Can cause file descriptor exhaustion during large scans.

**Fix proposal:** Use try-with-resources on the Process, consume stderr, and call `process.destroy()` in finally.

---

### 18. ScannerDispatcherThread.stop() — Race with User "Kill"
**File:** `src/net/azib/ipscan/core/ScannerDispatcherThread.java:120-135`

Race between feeder loop shutdown and user clicking "Kill". `complete()` may see unexpected state.

**Fix proposal:** Add state check before calling `complete()`, or synchronize the shutdown sequence.

---

### 19. SWTAwareStateMachine — Async Notification Loses Context
**File:** `src/net/azib/ipscan/gui/SWTAwareStateMachine.java:28-34`

`display.asyncExec()` dispatches notification to SWT thread. By the time it runs, state may have changed again. UI can show incorrect state.

**Fix proposal:** Capture the state at dispatch time and verify it hasn't changed before notifying listeners.

---

## MEDIUM

### 20. XML Injection in XMLExporter
**File:** `src/net/azib/ipscan/exporters/XMLExporter.java:59, 81, 85`

XML attribute values (feeder name, host address, fetcher names) written without escaping `"`, `<`, `&`.

**Fix proposal:** Add an `escapeXmlAttr()` helper and apply to all attribute values.

---

### 21. Preferences Tampering → Code Execution
**Files:** `src/net/azib/ipscan/config/OpenersConfig.java:61-72`, `Config.java:29`

Opener exec strings loaded directly from `java.util.prefs.Preferences` (world-readable `~/.java/.userPrefs/ipscan/prefs.xml` on Linux). Any same-user process can inject malicious commands.

**Fix proposal:** Validate opener strings against a whitelist of allowed characters, or sign the preferences data.

---

### 22. FileFeeder — No Path Validation
**File:** `src/net/azib/ipscan/feeders/FileFeeder.java:60-63`

`fileName` passed directly to `FileReader` without path canonicalization.

**Fix proposal:** Canonicalize the path and optionally restrict to user-selected files only.

---

### 23. ExportProcessor — Writes to User-Controlled Paths
**File:** `src/net/azib/ipscan/exporters/ExportProcessor.java:25-28, 43`

CLI `-o` flag accepts arbitrary output filename with no validation. Combined with `-a` (append), can append scan data to sensitive files.

**Fix proposal:** Validate output path, reject paths containing `..`, or restrict to a designated output directory.

---

### 24. PluginLoader — JarFile Leak on Null Manifest
**File:** `src/net/azib/ipscan/core/PluginLoader.java:78-81`

```java
var jarFile = new JarFile(jar);
var manifest = jarFile.getManifest();
if (manifest == null) continue;  // jarFile never closed!
```

**Fix proposal:** Use try-with-resources for JarFile.

---

### 25. ExportProcessor — PrintWriter Not Closed
**File:** `src/net/azib/ipscan/exporters/ExportProcessor.java:37-78`

The `finally` block closes `outputStream` but not the exporter's `PrintWriter`. Data loss on export failure.

**Fix proposal:** Close the exporter's writer in the finally block, or have the exporter implement `Closeable`.

---

### 26. StateMachine.transitionTo() — Check-Then-Act Race
**File:** `src/net/azib/ipscan/core/state/StateMachine.java:74-79`

`state` is volatile but the check-then-set is not atomic.

**Fix proposal:** Synchronize the transition method or use `AtomicReference` with CAS.

---

### 27. ScanningResultComparator — Shared Mutable State
**File:** `src/net/azib/ipscan/core/ScanningResultComparator.java:15-16`

`index` and `ascending` are mutated before each sort. Fragile if sort is ever called concurrently.

**Fix proposal:** Make comparator stateless — pass sort criteria via constructor or method parameters.

---

### 28. Scanner.activeFetchers — Stale Entries on Exception
**File:** `src/net/azib/ipscan/core/Scanner.java:46, 64`

If an exception occurs between `put()` and `remove()`, the entry is never cleaned up.

**Fix proposal:** Move `remove()` into a finally block.

---

### 29. ScanningResultList.findText() — Unsynchronized Size Check
**File:** `src/net/azib/ipscan/core/ScanningResultList.java:228-241`

`resultList.size()` (unsynchronized) used as loop bound, but `getResult(i)` (synchronized) can throw AIOOBE if list shrinks between calls.

**Fix proposal:** Synchronize the entire `findText()` method, or snapshot the size under lock.

---

### 30. Scanner.scan() Catches Throwable Including OOM
**File:** `src/net/azib/ipscan/core/Scanner.java:56-58`

Catches `Throwable` (including `OutOfMemoryError`, `StackOverflowError`) and continues scanning.

**Fix proposal:** Re-throw `Error` subclasses: `if (e instanceof Error) throw (Error) e;`

---

### 31. CSVExporter — Commas Replaced with Periods (Data Corruption)
**File:** `src/net/azib/ipscan/exporters/CSVExporter.java:14-15`

```java
static final char DELIMETER_ESCAPED = '.';
```

Commas in values are replaced with periods, destroying data. Standard CSV wraps fields in double quotes.

**Fix proposal:** Use proper CSV quoting (wrap fields containing commas/quotes/newlines in `"`, escape `"` as `""`).

---

### 32. No Response Size Limit in PortTextFetcher (DoS)
**File:** `src/net/azib/ipscan/fetchers/PortTextFetcher.java:66-76`

No limit on lines read from socket. A malicious server can send unlimited data, exhausting memory/CPU.

**Fix proposal:** Add a line count limit (e.g., 100 lines) or total byte limit.

---

### 33. PortIterator — No Start Port Validation
**File:** `src/net/azib/ipscan/core/PortIterator.java:44-49`

Only `endPort` is bounds-checked. `startPort` can be negative or >65535. No `start <= end` check.

**Fix proposal:** Validate both start and end ports, enforce `start <= end`.

---

### 34. LinuxMACFetcher Constructor — NoSuchElementException
**File:** `src/net/azib/ipscan/fetchers/LinuxMACFetcher.java:17-21`

`.findFirst().get()` on empty Optional crashes if `/proc/net/arp` doesn't exist (containers).

**Fix proposal:** Use `.findFirst().orElse(null)` and handle the null case.

---

### 35. ResultTable.getSelectedResult() — IndexOutOfBoundsException
**File:** `src/net/azib/ipscan/gui/ResultTable.java:173-176`

`getSelectionIndex()` returns -1 if nothing selected, passed directly to `getResult()`.

**Fix proposal:** Check for -1 before calling `getResult()`.

---

### 36. ScanningResult.toString() — NPE When resultList Is Null
**File:** `src/net/azib/ipscan/core/ScanningResult.java:118-133`

`resultList` is only set in `registerAtIndex()`. Calling `toString()` before registration NPEs.

**Fix proposal:** Null-check `resultList` before accessing.

---

### 37. DNS Rebinding Risk in HostnameFetcher
**File:** `src/net/azib/ipscan/fetchers/HostnameFetcher.java:64`

`getCanonicalHostName()` performs reverse DNS. Attacker-controlled PTR → internal IP could be used by openers to access internal services.

**Fix proposal:** Verify that the forward lookup of the returned hostname resolves back to the original IP.

---

### 38. ScanningResultList Grows Unboundedly (OOM on Large Scans)
**File:** `src/net/azib/ipscan/core/ScanningResultList.java:34-35`

For a /8 scan (16M IPs), millions of `ScanningResult` objects are allocated with no upper bound.

**Fix proposal:** Add a configurable max results limit, or use a streaming/paged approach.

---

## LOW

### 40. NetBIOSResolver.nameFlag() — Operator Precedence Bug
**File:** `src/net/azib/ipscan/util/NetBIOSResolver.java:87-88`

`& 0xFF + (...)` is parsed as `& (0xFF + (...))` due to `+` having higher precedence than `&`. Also second byte should multiply by 0x100, not 0xFF.

**Fix proposal:** `(response[...] & 0xFF) + (response[...] & 0xFF) * 0x100`

---

### 41. MDNSResolver Request ID — Signed Byte Arithmetic
**File:** `src/net/azib/ipscan/util/MDNSResolver.java:64`

`addr[2] * 0xFF` produces wrong results for negative byte values. Should use `(addr[2] & 0xFF)`.

**Fix proposal:** `(addr[2] & 0xFF) * 0xFF + (addr[3] & 0xFF)`

---

### 42. GoogleAnalytics — Incomplete JSON Escaping
**File:** `src/net/azib/ipscan/util/GoogleAnalytics.java:36-59`

Only `content` has quotes escaped. Other values (client_id, locale, OS properties) are inserted without JSON escaping.

**Fix proposal:** Use a JSON library or add proper escaping for all interpolated values.

---

### 45. HostnameFetcher.resolveWithRegularDNS() — Empty Catch Block
**File:** `src/net/azib/ipscan/fetchers/HostnameFetcher.java:61`

`catch (Exception ignored) {}` — completely swallows errors, making debugging difficult.

**Fix proposal:** Log at FINE level.

---

### 46. java.util.Random Instead of SecureRandom for GA Client ID
**File:** `src/net/azib/ipscan/config/Config.java:37`

**Fix proposal:** Use `new SecureRandom()`.

---

### 47. SequenceIterator — AIOOBE After Exhaustion
**File:** `src/net/azib/ipscan/util/SequenceIterator.java:33-38`

`next()` after all iterators exhausted throws ArrayIndexOutOfBoundsException.

**Fix proposal:** Check `hasNext()` or bounds-check `currentIndex`.

---

## Positive Findings (No Issues)

- **No Java deserialization** — no `ObjectInputStream`/`readObject` usage
- **No XML parsing** — no XXE attack surface (XMLExporter only writes)
- **JNA native calls are safe** — all use binary structs, no string parameters
- **No SQLExporter unit test** — consider adding one to cover escaping and null handling


# Old TODO

* SWT Error: no more handles (empty modal window appears)
* Retrieve value for any fetcher from context menu
* Monitoring mode (which IPs appear/disappear)
* UX: button for netmask application?
* Graalvm native-image to build binary

* Use ipify for /iplocate
* Add URLFetcher with configurable URL and JSON/XPath expression
* WHOIS fetcher

* Windows: net stop SharedAccess
* gtk sort direction arrows
* advanced exporting options dialog (with append checkbox)
* Enable/Disable ports (without resetting)
* Opener Launchers to the details window
* multiple port support web-detect, opening in browser selects scanned ports if available
* add new fetchers by configuration of PortTextFetcher
* public XSL for XMLExporter
* Easier adding/removing of columns to the result table (without resetting the results)
* command-line: support favorites
* command-line: add netmask support to the range feeder
* find not-null for column
* export/import of settings (profiles or tie with Favorites?)
* display friendly names of ports
* preferences profiles (tied to favorites?)
* free text (advanced) feeder
* saving and restoring of results together with all options
* advanced find (firefox-like) with options Find Next, Find Previoius, Select all matches
* count occurencies of search (either separate or included)
* diff with saved
* show distinct values for a column
* SWT bug: deleting of many IPs at once is very slow (freezes the ipscan) due to the sorting of provided indices

* use jpcap for raw packet injection and ARP scanning
* startup as root option
