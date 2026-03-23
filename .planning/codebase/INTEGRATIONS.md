# External Integrations

**Analysis Date:** 2025-02-14

## APIs & External Services

**Networking:**
- None detected. The app currently does not require internet permissions or use networking libraries. `app/src/main/AndroidManifest.xml`

## Data Storage

**Databases:**
- SQLite (via Room persistence library)
  - Connection: Managed by `hr.fipu.organizationtool.data.AppDatabase`
  - Client: Room (androidx.room)
  - Location: `app/src/main/java/hr/fipu/organizationtool/data/AppDatabase.kt`

**File Storage:**
- Local filesystem only.

**Caching:**
- Local caching via Room database.

## Authentication & Identity

**Auth Provider:**
- Custom (None). The app does not have user authentication.

## Monitoring & Observability

**Error Tracking:**
- None.

**Logs:**
- Standard Android `Log` class.

## CI/CD & Deployment

**Hosting:**
- Android Play Store (Target platform).

**CI Pipeline:**
- None.

## Environment Configuration

**Required env vars:**
- `sdk.dir` in `local.properties` (for local development).

**Secrets location:**
- Not applicable (local-only app).

## Webhooks & Callbacks

**Incoming:**
- App Widget Updates: `hr.fipu.organizationtool.widget.ZenStackWidgetReceiver` handles `android.appwidget.action.APPWIDGET_UPDATE`. `app/src/main/AndroidManifest.xml`

**Outgoing:**
- None.

---

*Integration audit: 2025-02-14*
