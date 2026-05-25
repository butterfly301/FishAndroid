# AGENT Rules

## Build Execution Policy

- Do not run packaging/build commands by default.
- Only run packaging/build when the user explicitly requests it (e.g., “打包”, “构建”, “assemble”, “build”).
- For normal code changes, perform only necessary local/static checks unless explicitly asked to build.
