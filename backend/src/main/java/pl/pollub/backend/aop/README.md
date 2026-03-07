# AOP Setup

This package contains three Spring AOP aspects used by the backend:

- `ControllerTracingAspect` - traces calls to `*Controller` public methods.
- `ServiceTimingAspect` - records execution time for `*ServiceImpl` public methods.
- `MutationAuditAspect` - audits mutating operations in `*ServiceImpl` (`create*`, `update*`, `delete*`, etc.).

## Dependency

AOP is enabled by adding `spring-boot-starter-aop` to `pom.xml`.

## Quick test

Run the focused smoke test:

```powershell
cd F:\PROJEKTY\personal-finance-manager\backend
.\mvnw.cmd -Dtest=pl.pollub.backend.aop.AspectWiringSmokeTest test
```

