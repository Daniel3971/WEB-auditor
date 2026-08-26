# Production deployment checklist

## Required architecture

- Serve the Angular application and `/api` through one HTTPS domain where possible.
- Route `/api` to Spring Boot and all other paths to the Angular static files.
- Redirect HTTP to HTTPS at the reverse proxy or hosting platform.
- Run Spring Boot with `SPRING_PROFILES_ACTIVE=prod`.
- Use a persistent, private directory or object-storage-backed volume for CV files. Do not use an ephemeral application filesystem.

## Required secrets and settings

Set all values shown in `.env.example` through the hosting provider's secret manager. Do not upload a populated `.env` file. Use a dedicated MySQL user with access only to the application database.

`FRONTEND_ORIGIN` must be the exact HTTPS origin, for example `https://www.company.com`, without a path or trailing slash. `CV_UPLOAD_DIR` must point to the mounted persistent CV directory.

The production profile enables secure cookies, trusted forwarded-header processing, hides error details, and disables JPA open-in-view.

## Backups

The database and CV directory must be backed up together so database records and files remain consistent. `ops/backup-mysql.ps1` creates a timestamped MySQL dump, copies the CV directory, and creates a SHA-256 checksum for the dump.

Run backups from a protected operations machine or scheduled job. Store encrypted copies outside the application server. Define retention only after the company chooses its legal/business policy; a reasonable starting discussion is daily backups for 30 days plus monthly backups for one year.

Test restoration regularly on a separate database and separate CV directory. A backup that has never been restored is not considered verified.

## Applicant retention

Before automatic deletion is implemented, the company must decide:

- how long rejected and unsuccessful applications are retained;
- whether accepted applications follow a different employee-record policy;
- whether audit logs must outlive applicant records;
- who is authorized to approve deletion;
- whether applicants must be notified of the retention period.

Until that decision is documented, do not add an automatic deletion job.

## Release verification

1. Confirm the browser shows a valid HTTPS certificate and HTTP redirects to HTTPS.
2. Confirm cookies are `Secure`, `HttpOnly` for `JSESSIONID`, and `SameSite=Lax`.
3. Test login, logout, session expiry, and the one-session limit.
4. Test create, edit, open, close, archive, restore, and permanent deletion rules.
5. Submit a test application and verify the CV survives an application restart/redeployment.
6. Confirm applicant and CV endpoints return 401/403 when signed out.
7. Confirm the frontend origin is accepted and an unrelated origin is rejected.
8. Trigger five failed logins and verify the temporary lockout.
9. Confirm audit rows are created for admin changes.
10. Create a database/CV backup and restore it in an isolated environment.
