# GitHub Actions Workflows

SymbolCraft uses modular GitHub Actions workflows for efficient CI/CD.

## 📋 Workflow Structure

### CI Workflows (Build & Test)

| Workflow | Triggers | Purpose |
|----------|----------|---------|
| **plugin-ci.yml** | `symbolcraft-plugin/**` | Build and test plugin module |
| **runtime-ci.yml** | `symbolcraft-runtime/**` | Build and test runtime module (macOS runner for iOS) |
| **material-symbols-ci.yml** | `symbolcraft-material-symbols/**` | Build and test material-symbols module (macOS runner for iOS) |

**Path-based triggering**: Only runs when relevant files change, avoiding unnecessary CI runs.

### Release Workflows

| Workflow | Commit Message | Publishes To |
|----------|---------------|--------------|
| **release-plugin.yml** | `chore(plugin): v1.2.3` | Gradle Plugin Portal + Maven Central |
| **release-runtime.yml** | `chore(runtime): v1.2.3` | Maven Central |
| **release-material-symbols.yml** | `chore(material-symbols): v1.2.3` | Maven Central |
| **release-all.yml** | `chore(release): v1.2.3` or `chore(all): v1.2.3` | All repositories |

---

## 🚀 How to Release

### Release Single Module

#### Plugin
```bash
git commit -m "chore(plugin): v0.4.1

Release plugin with new features"
git push origin main
```

**Triggers**: `release-plugin.yml`  
**Publishes**: Gradle Plugin Portal + Maven Central  
**Tag**: `plugin-v0.4.1`

#### Runtime
```bash
git commit -m "chore(runtime): v0.4.0

Release runtime library"
git push origin main
```

**Triggers**: `release-runtime.yml`  
**Publishes**: Maven Central  
**Tag**: `runtime-v0.4.0`

#### Material Symbols
```bash
git commit -m "chore(material-symbols): v0.4.0

Release Material Symbols library"
git push origin main
```

**Triggers**: `release-material-symbols.yml`  
**Publishes**: Maven Central  
**Tag**: `material-symbols-v0.4.0`

### Release All Modules

```bash
git commit -m "chore(release): v0.4.0

Release all modules:
- Plugin updates
- Runtime improvements
- Material Symbols additions"
git push origin main
```

**Triggers**: `release-all.yml`  
**Publishes**: All repositories (Plugin Portal + Maven Central)  
**Tag**: `v0.4.0`

---

## 📝 Commit Message Format

### Version Format

All version formats are supported:
- `chore(plugin): v1.2.3` (with 'v')
- `chore(plugin): 1.2.3` (without 'v')

### Valid Scopes

- `plugin` - Plugin module only
- `runtime` - Runtime module only
- `material-symbols` - Material Symbols module only
- `release` or `all` - All modules

### Examples

```bash
# Plugin release
chore(plugin): v0.4.1

# Runtime release
chore(runtime): 0.5.0

# Material Symbols release
chore(material-symbols): v1.0.0

# Unified release
chore(release): v0.4.0
chore(all): 0.5.0
```

---

## 🔍 Path-based Triggering

### Plugin CI Triggers

```yaml
paths:
  - 'symbolcraft-plugin/**'
  - 'gradle/libs.versions.toml'
  - 'gradle.properties'
  - 'build.gradle.kts'
  - 'settings.gradle.kts'
```

### Runtime CI Triggers

```yaml
paths:
  - 'symbolcraft-runtime/**'
  - 'gradle/libs.versions.toml'
  - 'gradle.properties'
  - 'build.gradle.kts'
  - 'settings.gradle.kts'
```

### Material Symbols CI Triggers

```yaml
paths:
  - 'symbolcraft-material-symbols/**'
  - 'symbolcraft-runtime/**'  # Depends on runtime
  - 'gradle/libs.versions.toml'
  - 'gradle.properties'
  - 'build.gradle.kts'
  - 'settings.gradle.kts'
```

---

## 🛠️ Workflow Features

### CI Workflows

- ✅ **Smart Path Filtering** - Only runs for relevant file changes
- ✅ **Parallel Execution** - Multiple modules can run CI simultaneously
- ✅ **Artifact Upload** - Build artifacts saved for 7 days
- ✅ **Platform-specific Runners**:
  - Ubuntu for plugin (JVM only)
  - macOS for runtime and material-symbols (iOS builds)

### Release Workflows

- ✅ **Automatic Version Extraction** - From commit message
- ✅ **Version Update** - Automatically updates `build.gradle.kts`
- ✅ **Multi-platform Builds** - Android, JVM, iOS
- ✅ **GitHub Releases** - Automatic release creation with artifacts
- ✅ **Dual Publishing**:
  - Plugin → Gradle Plugin Portal + Maven Central
  - Libraries → Maven Central
- ✅ **Release Notes** - Auto-generated from commits

---

## 🔐 Required Secrets

Configure these in GitHub repository settings:

### Maven Central
- `OSSRH_USERNAME` - Sonatype username
- `OSSRH_PASSWORD` - Sonatype password
- `SIGNING_KEY` - GPG private key (ASCII armored)
- `SIGNING_PASSWORD` - GPG key password

### Gradle Plugin Portal
- `GRADLE_PUBLISH_KEY` - Gradle publish key
- `GRADLE_PUBLISH_SECRET` - Gradle publish secret

---

## 📊 Workflow Status

Check workflow status:
- [Actions Tab](../../actions)
- [Plugin CI](../../actions/workflows/plugin-ci.yml)
- [Runtime CI](../../actions/workflows/runtime-ci.yml)
- [Material Symbols CI](../../actions/workflows/material-symbols-ci.yml)

---

## 🚨 Troubleshooting

### CI not triggered?

Check:
1. File paths match the workflow `paths` filter
2. Branch is correct (`**` for CI, `main` for releases)
3. GitHub Actions are enabled in repository settings

### Release not triggered?

Check:
1. Commit message format is correct
2. Push to `main` branch
3. Commit message contains valid scope and version

### macOS runners timing out?

iOS framework compilation can take 5-10 minutes. This is normal.

---

## 📚 Related Documentation

- [Monorepo Architecture](../../MIGRATION_TO_MONOREPO.md)
- [Version Compatibility](../../VERSION_DOWNGRADE_SUMMARY.md)
- [Development Guide](../../AGENTS.md)
